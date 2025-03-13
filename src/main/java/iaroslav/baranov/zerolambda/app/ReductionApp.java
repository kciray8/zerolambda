package iaroslav.baranov.zerolambda.app;

import iaroslav.baranov.zerolambda.*;

import java.util.List;
import java.util.Scanner;

import static iaroslav.baranov.zerolambda.Terms.*;

public class ReductionApp {
    public static void main(String[] args) {
        new ReductionApp();
    }

    static Scanner sc = new Scanner(System.in);
    static TermService termService = new TermService();

    public ReductionApp() {
        Term factorialBody = new Application(Y_COMBINATOR, new Abstraction(
                new Abstraction(
                        termService.application(IF, new Application(IS_ZERO, 1),
                                ONE,
                                termService.application(MULTIPLICATION, new Variable(1),
                                        new Application(2, new Application(PRED, 1)))
                        )
                ))
        );
        Term factorial = new Application(factorialBody, ZERO);

        Term current = factorial;
        while (true) {
            System.out.println("Term: " + withDefinitions(current));
            System.out.println("Reductions: ");
            List<Reduction> reductions = current.allPossibleOneStepReductions();
            for (int i = 0; i < current.allPossibleOneStepReductions().size(); i++) {
                Reduction reduction = reductions.get(i);
                System.out.println(i + " - " + withDefinitions(reduction.getResult()));
            }
            int choice = sc.nextInt();
            current = reductions.get(choice).getResult();
        }
    }

    String withDefinitions(Term term) {
        String result = term.toString();
        result = result.replace(Y_COMBINATOR.toString(), "Y_COMBINATOR");
        result = result.replace(IS_ZERO.toString(), "IS_ZERO");
        result = result.replace(ZERO.toString(), "ZERO");
        result = result.replace(ONE.toString(), "ONE");
        result = result.replace(IF.toString(), "IF");
        result = result.replace(PRED.toString(), "PRED");
        result = result.replace(MULTIPLICATION.toString(), "MUL");

        return result;
    }
}
