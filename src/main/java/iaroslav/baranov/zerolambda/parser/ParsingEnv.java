package iaroslav.baranov.zerolambda.parser;

import iaroslav.baranov.zerolambda.Term;

import java.util.HashMap;
import java.util.Map;

public class ParsingEnv {
    private Map<String, Term> definitions = new HashMap<>();
    public void addDefinition(String name, Term term) {
        definitions.put(name, term);
    }

    public ParsingEnv(Map<String, Term> definitions) {
        this.definitions = definitions;
    }

    public ParsingEnv() {
    }

    public Term getDefinition(String name) {
        return definitions.get(name);
    }

    public boolean hasDefinition(String name) {
        return definitions.containsKey(name);
    }
}
