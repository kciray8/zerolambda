package iaroslav.baranov.zerolambda;

public class Reduction{
    private Term redex;
    private Term contractum;
    private Term result;

    public Reduction(Term redex, Term contractum, Term result) {
        this.redex = redex;
        this.contractum = contractum;
        this.result = result;
    }

    public Term getRedex() {
        return redex;
    }

    public Term getContractum() {
        return contractum;
    }

    public Term getResult() {
        return result;
    }
}
