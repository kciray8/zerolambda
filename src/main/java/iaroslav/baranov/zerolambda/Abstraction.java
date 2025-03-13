package iaroslav.baranov.zerolambda;

import java.util.*;
import java.util.function.UnaryOperator;

public class Abstraction implements Term {
    public Term getBody() {
        return body;
    }

    private final Term body;

    public Abstraction(Term body) {
        this.body = body;
    }

    @Override
    public TermType type() {
        return TermType.ABSTRACTION;
    }

    @Override
    public Term replace(int index, Term term) {
        int newIndex = index + 1;
        Term newTerm = term;
        Term newBody = body.replace(newIndex, newTerm);
        return new Abstraction(newBody);
    }

    @Override
    public List<Reduction> allPossibleOneStepReductions(UnaryOperator<Term> insertIntoParent) {
        List<Reduction> result = new ArrayList<>();

        for (Reduction reduction : body.allPossibleOneStepReductions(Abstraction::new)) {
            result.add(
                    new Reduction(
                            reduction.getRedex(),
                            reduction.getContractum(),
                            insertIntoParent.apply(reduction.getResult())));
        }

        return result;
    }

    @Override
    public String toString() {
        return "λ" + body.toString();
    }

    @Override
    public String toStringReadable(Map<Integer, String> indexes) {
        Set<String> presentLetters = new HashSet<>(indexes.values());
        String nextLetter = chooseNextUniqueLetter(presentLetters);
        Map<Integer, String> indexesIncreased = new HashMap<>();
        for (Map.Entry<Integer, String> entry : indexes.entrySet()) {
            indexesIncreased.put(entry.getKey() + 1, entry.getValue());
        }
        indexesIncreased.put(1, nextLetter);

        return "λ" + nextLetter + "." + body.toStringReadable(indexesIncreased);
    }

    private String chooseNextUniqueLetter(Set<String> presentLetters) {
        for (char ch = 'a'; ch <= 'z'; ch++) {
            if (!presentLetters.contains(Character.toString(ch))) {
                return Character.toString(ch);
            }
        }
        throw new RuntimeException("RUN OUT OF LETTERS LOL");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Abstraction that)) return false;
        return getBody().equals(that.getBody());
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public Term increaseUnboundIndexes(int level) {
        return new Abstraction(body.increaseUnboundIndexes(level + 1));
    }

    @Override
    public Term decreaseUnboundIndexes(int level) {
        return new Abstraction(body.decreaseUnboundIndexes(level + 1));
    }

    @Override
    public Term markVariablesToReplace(int level){
        return new Abstraction(body.markVariablesToReplace(level + 1));
    }

    @Override
    public Term replaceMarkedVariables(Term term) {
        Term termIncreased = term.increaseUnboundIndexes(1);
        Term newBody = body.replaceMarkedVariables(termIncreased);
        return new Abstraction(newBody);
    }

    @Override
    public Term reduceOnceUsingNormalOrder() {
        if (body.canBeReduced()) {
            return new Abstraction(body.reduceOnceUsingNormalOrder());
        } else {
            return this;
        }
    }

    @Override
    public boolean canBeReduced() {
        return body.canBeReduced();
    }
}
