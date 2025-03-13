package iaroslav.baranov.zerolambda.parser;

import com.google.common.collect.ImmutableMap;
import iaroslav.baranov.zerolambda.*;

import java.util.*;

import static iaroslav.baranov.zerolambda.parser.TokenType.*;

public class Parser {
    Tokenizer tokenizer = new Tokenizer();

    public Term parse(String string, ParsingEnv env) {
        List<Token> tokens = tokenizer.tokenize(string);
        return parse(tokens, ImmutableMap.of(), env);
    }

    public Term parse(String string) {
        return parse(string, new ParsingEnv());
    }

    private Term parse(
            List<Token> tokens,
            ImmutableMap<String, Integer> indexes,
            ParsingEnv env
    ) {
        Token firstToken = tokens.getFirst();
        TokenType firstTokenType = firstToken.getType();

        if (firstTokenType.equals(LAMBDA)) {
            Token ident = tokens.get(1);
            Token dot = tokens.get(2);
            if (!dot.getType().equals(TokenType.DOT)) {
                throw new ParsingException("There should be a dot after λident, but got: " + dot);
            }

            Map<String, Integer> indexesIncremented = new HashMap<>();
            indexes.forEach((k, v) ->
                    indexesIncremented.put(k, v + 1)
            );

            ImmutableMap<String, Integer> indexesModified = ImmutableMap.<String, Integer>builder()
                    .putAll(indexesIncremented)
                    .put(ident.getText(), 1)
                    .build();

            List<Token> lambdaBody = tokens.subList(3, tokens.size());
            return new Abstraction(parse(lambdaBody, indexesModified, env));
        } else if (firstTokenType.equals(IDENT)) {
            String ident = firstToken.getText();
            Term term;
            if (indexes.containsKey(ident)) {
                int index = indexes.get(firstToken.getText());
                term = new Variable(index);
            } else if(env.hasDefinition(ident)){
                term = env.getDefinition(ident);
            } else {
                throw new ParsingException(
                        "Unknown identifier, not found neither in bound variables nor in definitions: " + ident
                );
            }

            if (tokens.size() > 1) {
                return divideIntoApplicationGroupsAndParse(tokens, indexes, env);
            } else { // =1
                return term;
            }
        } else if (firstTokenType.equals(NUMBER)) {
            Term numberTerm = TermService.instance.numberToTerm(Integer.parseInt(firstToken.getText()));
            if (tokens.size() > 1) {
                return divideIntoApplicationGroupsAndParse(tokens, indexes, env);
            } else { // =1
                return numberTerm;
            }
        } else if (firstTokenType.equals(LPAREN)) {
            return divideIntoApplicationGroupsAndParse(tokens, indexes, env);
        }
        throw new ParsingException("Unexpected state, tokens: " + tokens);
    }

    private Term divideIntoApplicationGroupsAndParse(List<Token> tokens, ImmutableMap<String, Integer> indexes, ParsingEnv env) {
        List<List<Token>> applicationGroups = divideIntoApplicationGroups(tokens);
        List<Term> parsedApplicationGroups = new ArrayList<>();
        for (List<Token> applicationGroup : applicationGroups) {
            parsedApplicationGroups.add(parse(applicationGroup, indexes, env));
        }
        Term currentTerm = parsedApplicationGroups.getFirst();
        for (int i = 1; i < parsedApplicationGroups.size(); i++) {
            currentTerm = new Application(currentTerm, parsedApplicationGroups.get(i));
        }
        return currentTerm;
    }

    private List<List<Token>> divideIntoApplicationGroups(List<Token> tokens){
        List<List<Token>> groups = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            TokenType type = token.getType();
            if(type.equals(LAMBDA)) {
                List<Token> lambdaGroup = tokens.subList(i, tokens.size());
                groups.add(lambdaGroup);
                return groups;
            } else if(type.equals(LPAREN)) {
                int from = i + 1;
                int pCounter = 1;
                while (pCounter > 0) {
                    i++;
                    Token nextToken = tokens.get(i);
                    if(nextToken.getType().equals(LPAREN)) {
                        pCounter++;
                    }
                    if(nextToken.getType().equals(RPAREN)) {
                        pCounter--;
                    }
                    if(pCounter == 0) {
                        break;
                    }
                }
                int to = i;
                List<Token> lambdaGroup = tokens.subList(from, to);
                groups.add(lambdaGroup);
            } else {
                List<Token> lambdaGroup = Collections.singletonList(token);
                groups.add(lambdaGroup);
            }
        }

        return groups;
    }
}
