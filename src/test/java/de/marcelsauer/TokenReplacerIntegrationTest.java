package de.marcelsauer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author msauer
 * 
 */
public class TokenReplacerIntegrationTest {

    @Test
    public void thatDefaultPatternWorks() {
        TokenReplacer replacer = new TokenReplacer().register(new NumberGenerator()).forToken("random").register(
                new SomeCharGenerator()).forToken("somechar");

        assertEquals("98765", replacer.substitute("${random}"));
        assertEquals("abc 98765", replacer.substitute("${somechar} ${random}"));
        assertEquals("9876598765", replacer.substitute("${random}${random}"));
        assertEquals("... 98765 98765    ", replacer.substitute("... ${random} ${random}    "));
        assertEquals("9876598765", replacer.substitute("${random[2]}"));
        assertEquals("98765.9876598765.987659876598765", replacer
                .substitute("${random[1]}.${random[2]}.${random[3]}"));
    }

    @Test
    public void thatForcedPatternWorks() {
        TokenReplacer replacer = new TokenReplacer().register(new NumberGenerator()).forToken("random").withTokenStart(
                "[").withTokenEnd("]").withAmountStart("{").withAmountEnd("}");

        assertEquals("98765", replacer.substitute("[random]"));
        assertEquals("9876598765", replacer.substitute("[random][random]"));
        assertEquals("... 98765 98765    ", replacer.substitute("... [random] [random]    "));
        assertEquals("9876598765", replacer.substitute("[random{2}]"));
        assertEquals("98765.9876598765.987659876598765", replacer.substitute("[random{1}].[random{2}].[random{3}]"));
    }

    private class NumberGenerator implements Generator {
        @Override
        public String generate() {
            return "98765";
        }
    }
    
    private class SomeCharGenerator implements Generator {
        @Override
        public String generate() {
            return "abc";
        }
    }
}
