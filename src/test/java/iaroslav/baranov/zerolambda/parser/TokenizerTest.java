package iaroslav.baranov.zerolambda.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    @Test
    public void testTokenizer() {
        String str = "λn.λf.λx.n(λgk.λh.h(gk f))(λu.x)(λu.u)";
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(str);
        assertEquals(35, tokens.size());
    }
}