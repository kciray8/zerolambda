package iaroslav.baranov.zerolambda.parser;

import iaroslav.baranov.zerolambda.Term;
import iaroslav.baranov.zerolambda.TermService;
import iaroslav.baranov.zerolambda.Terms;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    Parser parser = new Parser();
    TermService termService = new TermService();

    @Test
    public void simpleTest() {
        String str = "λn.λf.λx.n(λgk.λh.h(gk f))(λu.x)(λu.u)";
        Term term = parser.parse(str);

        assertEquals(Terms.PRED.toString(), term.toString());
    }

    @Test
    public void yCombinatorTest() {
        String yCombinatorUnparsed = "λy . (λx . y(x x))(λx . y(x x))";
        Term yCombinator = parser.parse(yCombinatorUnparsed);
        Term reference = Terms.Y_COMBINATOR;
        assertEquals(reference.toString(), yCombinator.toString());
    }

    @Test
    public void zCombinatorTest() {
        String zCombinatorUnparsed = "λy . (λx.y(λv.x x v))(λx.y(λv.x x v))\n";
        Term zCombinator = parser.parse(zCombinatorUnparsed);
        Term reference = Terms.Z_COMBINATOR;
        assertEquals(reference.toString(), zCombinator.toString());
    }

    // The right associativity bug
    @Test
    public void multiplicationShouldBeParsedCorrectly() {
        String str = "λm.λn.λf.λx.m (n f) x";
        Term term = parser.parse(str);
        Term ref = Terms.MULTIPLICATION;

        assertEquals(ref.toString(), term.toString());
    }
    @Test
    public void zeroShouldBeParsedCorrectly() {
        String str = "λf.λx.x";
        Term term = parser.parse(str);
        Term ref = Terms.ZERO;

        assertEquals(ref.toString(), term.toString());
    }
}