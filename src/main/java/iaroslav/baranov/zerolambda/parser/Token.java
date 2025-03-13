package iaroslav.baranov.zerolambda.parser;

public class Token {
    public TokenType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    private final TokenType type;
    private final String text;

    public Token(TokenType type, String text) {
        this.type = type;
        this.text = text;
    }

    public Token(TokenType type) {
        this.type = type;
        this.text = "";
    }

    @Override
    public String toString() {
        return type + (text.isEmpty() ? "" : "(" + text + ")");
    }
}