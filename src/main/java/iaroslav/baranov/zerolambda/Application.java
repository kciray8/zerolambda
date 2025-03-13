package iaroslav.baranov.zerolambda;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class Application implements Term {

    public Term getLeft() {
        return left;
    }

    public Term getRight() {
        return right;
    }

    private final Term left;
    private final Term right;

    public Application(Term left, Term right) {
        this.left = left;
        this.right = right;
    }

    public Application(Term left, int rightVariableIndex) {
        this.left = left;
        this.right = new Variable(rightVariableIndex);
    }

    public Application(int leftVariableIndex, Term right) {
        this.left = new Variable(leftVariableIndex);
        this.right = right;
    }

    public Application(int leftVariableIndex, int rightVariableIndex) {
        this.left = new Variable(leftVariableIndex);
        this.right = new Variable(rightVariableIndex);
    }

    @Override
    public TermType type() {
        return TermType.APPLICATION;
    }

    @Override
    public Term replace(int index, Term term) {
        return new Application(
                left.replace(index, term),
                right.replace(index, term)
        );
    }

    @Override
    public List<Reduction> allPossibleOneStepReductions(UnaryOperator<Term> insertIntoParent) {
        List<Reduction> result = new ArrayList<>();

        for (Reduction reduction : left.allPossibleOneStepReductions(term -> new Application(term, right))) {
            result.add(new Reduction(
                            reduction.getRedex(),
                            reduction.getContractum(),
                            insertIntoParent.apply(reduction.getResult())
                    ));
        }

        for (Reduction reduction : right.allPossibleOneStepReductions(term -> new Application(left, term))) {
            result.add(new Reduction(
                    reduction.getRedex(),
                    reduction.getContractum(),
                    insertIntoParent.apply(reduction.getResult())
            ));
        }

        if (left instanceof Abstraction abstraction) {
            //(λM)N →β M[1 := N]
            Term M_marked = abstraction.getBody().markVariablesToReplace(1);
            Term M = M_marked.decreaseUnboundIndexes(1);
            Term N = right;
            Term substitutedM = M.replaceMarkedVariables(N);

            result.add(
                    new Reduction(this, substitutedM, insertIntoParent.apply(substitutedM))
            );
        }

        return result;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + ")(" + right.toString() + ")";
    }

    @Override
    public String toStringReadable(Map<Integer, String> indexes) {
        return "(" + left.toStringReadable(indexes) + ")(" + right.toStringReadable(indexes) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Application that)) return false;
        return Objects.equals(left, that.left) && Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public Term increaseUnboundIndexes(int level) {
        return new Application(left.increaseUnboundIndexes(level), right.increaseUnboundIndexes(level));
    }

    @Override
    public Term decreaseUnboundIndexes(int level) {
        return new Application(left.decreaseUnboundIndexes(level), right.decreaseUnboundIndexes(level));
    }

    @Override
    public Term markVariablesToReplace(int level){
        return new Application(left.markVariablesToReplace(level), right.markVariablesToReplace(level));
    }

    @Override
    public Term replaceMarkedVariables(Term term) {
        return new Application(left.replaceMarkedVariables(term), right.replaceMarkedVariables(term));
    }

    @Override
    public Term reduceOnceUsingNormalOrder() {
        //outermost is preferred
        if (left instanceof Abstraction abstraction) {
            //(λM)N →β M[1 := N]
            Term M_marked = abstraction.getBody().markVariablesToReplace(1);
            Term M = M_marked.decreaseUnboundIndexes(1);
            Term N = right;
            Term substitutedM = M.replaceMarkedVariables(N);

            return substitutedM;
        }

        //leftmost is preferred
        if (left.canBeReduced()) {
            return new Application(left.reduceOnceUsingNormalOrder(), right);
        }

        if (right.canBeReduced()) {
            return new Application(left, right.reduceOnceUsingNormalOrder());
        }

        return this;
    }

    @Override
    public boolean canBeReduced() {
        if (left instanceof Abstraction) {
            return true;
        }

        return left.canBeReduced() || right.canBeReduced();
    }
}
