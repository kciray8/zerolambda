package iaroslav.baranov.zerolambda.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Tokenizer {
    //"λn.λf.λx.n(λgk.λh.h(gk f))(λu.x)(λu.u)"
    public List<Token> tokenize(String input) {
        List<Token> result = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '(':
                    result.add(new Token(TokenType.LPAREN));
                    break;
                case ')':
                    result.add(new Token(TokenType.RPAREN));
                    break;
                case '.':
                    result.add(new Token(TokenType.DOT));
                    break;
                case 'λ':
                    result.add(new Token(TokenType.LAMBDA));
                    break;
            }

            i = tryToParseContinuous(i, result, input, this::isLetter, TokenType.IDENT);
            i = tryToParseContinuous(i, result, input, this::isDigit, TokenType.NUMBER);
        }

        return result;
    }

    private int tryToParseContinuous(
            int i,
            List<Token> result,
            String input,
            Predicate<Character> check,
            TokenType targetTokenType
    ) {
        char c = input.charAt(i);
        if (check.test(c)) {
            StringBuilder ident = new StringBuilder();
            while (true) {
                ident.append(c);
                int nextCharIndex = i + 1;
                if (nextCharIndex >= input.length()) {
                    break;
                }
                c = input.charAt(nextCharIndex);
                if (!check.test(c)) {
                    break;
                }
                i = nextCharIndex;
            }
            result.add(new Token(targetTokenType, ident.toString()));
        }

        return i;
    }

    private boolean isLetter(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
    }

    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }
}
