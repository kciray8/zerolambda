package iaroslav.baranov.zerolambda;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class Variable implements Term{
    public int getIndex() {
        return index;
    }

    private final int index;

    private boolean marked = false;

    public Variable(int index){
        this.index = index;
    }

    public Variable(int index, boolean marked){
        this.index = index;
        this.marked = marked;
    }

    @Override
    public TermType type() {
        return TermType.VARIABLE;
    }

    @Override
    public Term replace(int index, Term term) {
        if(index == this.index) {
            return term;
        } else {
            return this;
        }
    }

    @Override
    public List<Reduction> allPossibleOneStepReductions(UnaryOperator<Term> insertIntoParent) {
        return List.of();
    }

    @Override
    public String toString() {
        if(marked) {
            return "□";
        } else {
            return Integer.toString(index);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Variable variable)) return false;
        return index == variable.index;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public Term increaseUnboundIndexes(int level) {
        int newIndex = index;
        if (index >= level) {
            newIndex++;
        }

        return new Variable(newIndex);
    }

    @Override
    public Term decreaseUnboundIndexes(int level) {
        int newIndex = index;
        if (index >= level) {
            newIndex--;
        }

        return new Variable(newIndex, marked);
    }

    @Override
    public Term markVariablesToReplace(int level){
        boolean marked = false;
        if (index == level) {
            marked = true;
        }

        return new Variable(index, marked);
    }

    @Override
    public Term replaceMarkedVariables(Term term) {
        if (marked) {
            return term;
        } else {
            return this;
        }
    }

    @Override
    public Term reduceOnceUsingNormalOrder() {
        return this;
    }

    @Override
    public boolean canBeReduced() {
        return false;
    }

    @Override
    public String toStringReadable(Map<Integer, String> indexes) {
        return indexes.get(index);
    }
}
