package iaroslav.baranov.zerolambda.app;

import iaroslav.baranov.zerolambda.Term;
import iaroslav.baranov.zerolambda.TermService;
import iaroslav.baranov.zerolambda.Terms;

import java.util.Scanner;

public class MultiplicationApp {
    static Scanner sc = new Scanner(System.in);
    static TermService termService = new TermService();

    public static void main(String[] args) {
        while (true) {
            System.out.print("Input two numbers: ");
            int a = sc.nextInt();
            int b = sc.nextInt();
            sc.nextLine();

            Term aTerm = termService.numberToTerm(a);
            Term bTerm = termService.numberToTerm(b);

            Term res = termService.application(Terms.MULTIPLICATION, aTerm, bTerm);
            Term nf = termService.getNormalFormUsingNormalOrder(res);
            int number = termService.termToNumber(nf);

            System.out.println(a + " " + "* " + b + " = " + number);
        }
    }
}
