package iaroslav.baranov.zerolambda.eval;

import iaroslav.baranov.zerolambda.Term;

import java.util.HashMap;
import java.util.Map;

public class EvaluationContext {
    private Map<String, Term> definitions = new HashMap<>();

    public Term getLastComputation() {
        return lastComputation;
    }

    private Term lastComputation;

    public void addDefinition(String name, Term term) {
        definitions.put(name, term);
    }

    public Map<String, Term> getDefinitions() {
        return definitions;
    }

    public void setLastComputation(Term lastComputation) {
        this.lastComputation = lastComputation;
    }
}
