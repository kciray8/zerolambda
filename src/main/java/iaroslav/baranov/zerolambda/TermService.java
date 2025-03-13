package iaroslav.baranov.zerolambda;

import iaroslav.baranov.zerolambda.parser.Parser;
import iaroslav.baranov.zerolambda.parser.ParsingEnv;

public class TermService {
    public static TermService instance = new TermService();
    Parser parser = new Parser();

    public int termToNumber(Term term) {
        int result = 0;
        Abstraction outerAbstr = (Abstraction) term;
        Abstraction innerAbstr = (Abstraction) outerAbstr.getBody();
        Term f = innerAbstr.getBody();
        while (f instanceof Application termApp) {
            Term left = termApp.getLeft();
            Term right = termApp.getRight();
            if (!(left instanceof Variable)) {
                throw new IncorrectConversion("Left is expected to be a variable in " + termApp);
            }
            f = right;
            result++;
        }
        if (!(f instanceof Variable)) {
            throw new IncorrectConversion("The final nested element is expected to be a variable" + term);
        }

        return result;
    }

    public boolean isBoolean(Term term) {
        return term.equals(Terms.TRUE) || term.equals(Terms.FALSE);
    }

    public boolean isTag1(Term term) {
        return isTag(term, 1);
    }

    public boolean isTag2(Term term) {
        return isTag(term, 2);
    }

    public boolean isTag(Term term, int expectedIndex) {
        if (!(term instanceof Abstraction outerAbstraction)) {
            return false;
        }
        if (!(outerAbstraction.getBody() instanceof Abstraction innerAbstraction)) {
            return false;
        }
        if (!(innerAbstraction.getBody() instanceof Application application)) {
            return false;
        }
        if (!(application.getLeft() instanceof Variable left)) {
            return false;
        }
        if (left.getIndex() != expectedIndex) {
            return false;
        }
        if ((application.getRight() instanceof Variable right) && right.getIndex() == 1) {
            return false;
        }
        if ((application.getRight() instanceof Variable right) && right.getIndex() == 2) {
            return false;
        }

        return true;
    }

    public Term getTagContent(Term term) {
        Abstraction outerAbstraction = (Abstraction) term;
        Abstraction innerAbstraction = (Abstraction) outerAbstraction.getBody();
        Application application = (Application) innerAbstraction.getBody();
        return application.getRight();
    }

    //cons five (cons seven nil)
    //λ((1)(λλ(2)((2)((2)((2)((2)(1)))))))(λ((1)(λλ(2)((2)((2)((2)((2)((2)((2)(1)))))))))(λλ1))
    public boolean isList(Term term) {
        if (isNil(term)) {
            return true;
        } else {
            if (isCons(term)) {
                Term tail = getConsTail(term);
                return isList(tail);
            } else {
                return false;
            }
        }
    }

    public boolean isPair(Term term) {
        return isCons(term);
    }

    public boolean isTaggedList(Term term) {
        if(!isTag2(term)) {
            return false;
        }
        Term tagContent = getTagContent(term);

        if (isNil(tagContent)) {
            return true;
        } else {
            if (isCons(tagContent)) {
                Term tail = getConsTail(tagContent);
                return isTaggedList(tail);
            } else {
                return false;
            }
        }
    }

    public boolean isTaggedElement(Term term) {
        return isTag1(term);
    }

    public boolean isCons(Term term) {
        if (!(term instanceof Abstraction outerAbstraction)) {
            return false;
        }
        if (!(outerAbstraction.getBody() instanceof Application termApp)) {
            return false;
        }
        //(1 left) right
        Term leftOuter = termApp.getLeft();
        if (!(leftOuter instanceof Application leftOuterApplication)) {
            return false;
        }
        Term num = leftOuterApplication.getLeft();
        Term right = termApp.getRight();

        if (!(num instanceof Variable leftVar)) {
            return false;
        }
        if (leftVar.getIndex() != 1) {
            return false;
        }
        return true;
    }

    private static boolean isNil(Term term) {
        return term.equals(Terms.FALSE);
    }

    public boolean isPrintable(Term term) {
        if (isList(term)) {
            if (isNil(term)) {
                return true;
            }
            Term head = getConsHead(term);
            Term tail = getConsTail(term);
            return isPrintable(head) && isPrintable(tail);
        } else  if (isTaggedList(term)) {
            Term list = getTagContent(term);
            if (isNil(list)) {
                return true;
            }
            Term taggedHead = getConsHead(list);
            Term tail = getConsTail(list);
            return isPrintable(taggedHead) && isPrintable(tail);
        } else if (isPair(term)) {
            Term head = getConsHead(term);
            Term tail = getConsTail(term);
            return isPrintable(head) && isPrintable(tail);
        } else  if (isTaggedElement(term)) {
            return isPrintable(getTagContent(term));
        } else if (isNumber(term)) {
            return true;
        } else if (isBoolean(term)) {
            return true;
        } else {
            return false;
        }
    }

    public String toPrintableMain(Term term) {
        if (isList(term)) {
            return "[" + toPrintable(term);
        } else if (isTaggedList(term)) {
            return "[" + toPrintable(term);
        } else {
            return toPrintable(term);
        }
    }

    private String toPrintable(Term term) {
        if (isList(term)) {
            if (isNil(term)) {
                return "]";
            }
            Term head = getConsHead(term);
            Term tail = getConsTail(term);

            String printableHead = toPrintable(head);
            if (isList(head)){
                printableHead = "[" + printableHead;
            }

            if (isNil(tail)) {
                return printableHead + toPrintable(tail);
            } else{
                return printableHead + ", " + toPrintable(tail);
            }
        } else  if (isTaggedList(term)) {
            Term list = getTagContent(term);
            if (isNil(list)) {
                return "]";
            }
            Term taggedHead = getConsHead(list);
            Term head = getTagContent(taggedHead);
            Term taggedTail = getConsTail(list);
            Term tail = getTagContent(taggedTail);

            String printableHead = toPrintable(taggedHead);
            if (isTaggedList(taggedHead)){
                printableHead = "[" + printableHead;
            }

            if (isNil(tail)) {
                return printableHead + toPrintable(taggedTail);
            } else {
                return printableHead + ", " + toPrintable(taggedTail);
            }
        } else if (isPair(term)) {
            Term head = getConsHead(term);
            Term tail = getConsTail(term);
            return "(" + toPrintable(head) + ", " + toPrintable(tail) + ")";
        } else if (isTaggedElement(term)) {
            return toPrintable(getTagContent(term));
        } else if (isNumber(term)) {
            return Integer.toString(termToNumber(term));
        } else if (isBoolean(term)) {
            return Boolean.toString(termToBoolean(term));
        } else {
            throw new RuntimeException("The term is not printable! Term: " + term.toString());
        }
    }

    public Term getConsHead(Term term) {
        Abstraction outerAbstraction = (Abstraction) term;
        Application termApp = (Application) outerAbstraction.getBody();
        //(1 left) right
        Term leftOuter = termApp.getLeft();
        Application leftOuterApplication = (Application) leftOuter;
        Term left = leftOuterApplication.getRight();
        return left;
    }

    public Term getConsTail(Term term) {
        Abstraction outerAbstraction = (Abstraction) term;
        Application termApp = (Application) outerAbstraction.getBody();
        //(1 left) right
        Term leftOuter = termApp.getLeft();
        Term right = termApp.getRight();
        return right;
    }

    public boolean isNumber(Term term) {
        if (!(term instanceof Abstraction)) {
            return false;
        }
        Abstraction outerAbstr = (Abstraction) term;

        if (!(outerAbstr.getBody() instanceof Abstraction)) {
            return false;
        }
        Abstraction innerAbstr = (Abstraction) outerAbstr.getBody();
        Term f = innerAbstr.getBody();
        while (f instanceof Application termApp) {
            Term left = termApp.getLeft();
            Term right = termApp.getRight();
            if (!(left instanceof Variable)) {
                return false;
            }
            Variable variable = (Variable) left;
            if (variable.getIndex() != 2) {
                return false;
            }
            f = right;
        }
        if (!(f instanceof Variable)) {
            return false;
        }
        Variable variable = (Variable) f;
        if (variable.getIndex() != 1) {
            return false;
        }

        return true;
    }

    public Term numberToTerm(int num) {
        Term term = new Variable(1);
        for (int i = 0; i < num; i++) {
            term = new Application(new Variable(2), term);
        }

        Term innterAbstr = new Abstraction(term);
        Term outerAbstr = new Abstraction(innterAbstr);
        return outerAbstr;
    }

    public Term getNormalFormUsingNormalOrder(Term term) {
        int counter = 0;
        while (true) {
            Term next = term.reduceOnceUsingNormalOrder();
            if (!next.equals(term)) {
                term = next;
            } else {
                System.out.println("counter = " + counter);
                return term;
            }
            counter++;
        }
    }

    public boolean termToBoolean(Term term) {
        Abstraction outerAbstr = (Abstraction) term;
        Abstraction innerAbstr = (Abstraction) outerAbstr.getBody();
        Variable variable = (Variable) innerAbstr.getBody();
        if (variable.getIndex() == 2) {
            return true;
        }
        if (variable.getIndex() == 1) {
            return false;
        }

        throw new IncorrectConversion("Term is not a boolean: " + term);
    }

    public Term application(Term... terms) {
        Term term = terms[0];
        for (int i = 1; i < terms.length; i++) {
            term = new Application(term, terms[i]);
        }

        return term;
    }

    public Term parse(String input) {
        return parser.parse(input, new ParsingEnv());
    }
}
