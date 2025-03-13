package iaroslav.baranov.zerolambda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public interface Term {
    TermType type();

    //P[index := N] - Substitution
    Term replace(int index, Term term);

    Term increaseUnboundIndexes(int level);

    Term decreaseUnboundIndexes(int level);

    Term markVariablesToReplace(int level);

    Term replaceMarkedVariables(Term term);

    // Normal-order (leftmost-outermost) evaluation: Always reduce the leftmost, outermost redex first
    // This guarantees that if your expression has a normal form, you will eventually reach it.
    Term reduceOnceUsingNormalOrder();

    boolean canBeReduced();

    List<Reduction> allPossibleOneStepReductions(
            UnaryOperator<Term> insertIntoParent
    );

    default List<Reduction> allPossibleOneStepReductions(){
        return allPossibleOneStepReductions(it -> it);
    }

    default Term nf(){
        return TermService.instance.getNormalFormUsingNormalOrder(this);
    }

    String toStringReadable(Map<Integer, String> indexes);

    default String toStringReadable() {
        return toStringReadable(new HashMap<>());
    }
}
