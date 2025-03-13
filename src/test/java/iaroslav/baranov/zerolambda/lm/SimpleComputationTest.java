package iaroslav.baranov.zerolambda.lm;

import iaroslav.baranov.zerolambda.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static iaroslav.baranov.zerolambda.Terms.*;
import static org.junit.jupiter.api.Assertions.*;

class SimpleComputationTest {
    TermService termService = new TermService();

    @Test
    void indexesDecreasingMustWork() {
        String original = "λ(λ(((2)(2))(2))(1))(λ(1)(2))";

        Abstraction inner1 = new Abstraction(termService.application(
                new Variable(2),new Variable(2),new Variable(2),new Variable(1)));
        Abstraction inner2 = new Abstraction(termService.application(
                new Variable(1),new Variable(2)));
        Abstraction outer = new Abstraction(new Application(inner1, inner2));
        Term nf = outer.nf();
        assertEquals("λ(((1)(1))(1))(λ(1)(2))", nf.toString());
        System.out.println();
    }

    @Test
    void superCheck(){
        String original = "(λλ1)λλλ1";
        Term term = new Application(new Abstraction(new Abstraction(new Variable(1))),
                new Abstraction(new Abstraction(new Abstraction(new Variable(1)))));
        Term nf = term.nf();
        assertEquals("λ1", nf.toString());
        System.out.println();
    }

    @Test
    void mustEatArgument() {
        String original = "[λλ2] 2";
        Term term = new Application(new Abstraction(new Abstraction(new Variable(2))), 2);

        Term nf = term.nf();
        assertEquals("λ3", nf.toString());
        System.out.println();
    }

    @Test
    void incrementShouldWork() {
        //zero := λfx. x,
        Variable x = new Variable(1);
        Abstraction xAbstr = new Abstraction(x);
        Term zero = new Abstraction(xAbstr);

        // suc := λmfx. f(mfx).
        Term suc = new Abstraction(new Abstraction(new Abstraction(
                new Application(new Variable(2),
                        new Application(new Application(3, 2), new Variable(1))
                )
        )));

        Term iterator = zero;
        for (int i = 1; i < 6; i++) {
            iterator = new Application(suc, iterator);
            System.out.println("before" + iterator);

            List<Reduction> reductions = iterator.allPossibleOneStepReductions();
            while (!reductions.isEmpty()) {
                iterator = reductions.get(0).getResult();
                System.out.println("reduction " + iterator);
                reductions = iterator.allPossibleOneStepReductions();
            }
            Term numberTerm = iterator;
            int number = termService.termToNumber(numberTerm);
            System.out.println("Number: " + number);
            assertEquals(i, number);
        }
    }

    @Test
    void multiplicationShouldWork() {
        Term a = termService.numberToTerm(4);
        Term b = termService.numberToTerm(3);

        System.out.println("a: " + a);
        System.out.println("b: " + b);

        //mult := λmnfx . (m(nf))x.
        Term mult = Terms.MULTIPLICATION;

        System.out.println("Mult: " + mult);

        Term res = termService.application(mult, a, b);
        System.out.println("Res                    : " + res);
        Term nf = termService.getNormalFormUsingNormalOrder(res);
        System.out.println("Res after normalization: " + nf);
        int number = termService.termToNumber(nf);
        assertEquals(12, number);
    }


    @Test
    void isZeroShouldWork() {
        Term isZero = termService.application(Terms.IS_ZERO, ZERO);
        Term isZeroNF = termService.getNormalFormUsingNormalOrder(isZero);
        assertTrue(isZeroNF.equals(TRUE));

        Term isZero2 = termService.application(Terms.IS_ZERO, ONE);
        Term isZero2NF = termService.getNormalFormUsingNormalOrder(isZero2);
        assertTrue(isZero2NF.equals(FALSE));
    }

    @Test
    void ifShouldWork() {
        Term check1 = termService.application(IF, TRUE, TWO, THREE).nf();
        Term check2 = termService.application(IF, FALSE, TWO, THREE).nf();
        assertTrue(check1.equals(TWO));
        assertTrue(check2.equals(THREE));
    }

    @Test
    void predShouldWork() {
        assertEquals(termService.application(PRED, THREE).nf(), TWO);
        assertEquals(termService.application(PRED, TWO).nf(), ONE);
        assertEquals(termService.application(PRED, ONE).nf(), ZERO);
    }

    @Test
    void factorialShouldWork() {
        int fact0 = calcFactorial(0);

        int fact1 = calcFactorial(1);

        int fact2 = calcFactorial(2);

        int fact3 = calcFactorial(3);

        int fact4 = calcFactorial(4);

        assertEquals(1, fact0);
        assertEquals(1, fact1);
        assertEquals(2, fact2);
        assertEquals(6, fact3);
        assertEquals(24, fact4);
    }

    private int calcFactorial(int number) {
        // λg.λn.IF(iszero n)(1)(MULT n(g(PRED n)))
        Term FACT = new Application(Y_COMBINATOR, new Abstraction(
                new Abstraction(
                        termService.application(IF, new Application(IS_ZERO, 1),
                                ONE,
                                termService.application(MULTIPLICATION, new Variable(1),
                                        new Application(2, new Application(PRED, 1)))
                        )
                ))
        );

        return termService.termToNumber(termService.getNormalFormUsingNormalOrder(
                new Application(FACT, termService.numberToTerm(number))
        ));
    }
}