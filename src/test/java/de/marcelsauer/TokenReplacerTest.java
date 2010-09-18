package de.marcelsauer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.marcelsauer.TokenExtractor.Match;

/**
 * @author msauer
 * 
 */
public class TokenReplacerTest {

    private TokenReplacer replacer;
    private TokenExtractor extractor;

    @Before
    public void setUp() {
        extractor = Mockito.mock(TokenExtractor.class);
        replacer = new TokenReplacer(extractor);
    }

    @Test
    public void thatDefaultPatternWorks() {
        replacer.register(new NumberGenerator()).forToken("random");

        Map<String, Match> matches = new HashMap<String, Match>();
        matches.put("random", new Match("random", "random"));

        Mockito.when(extractor.extract("${random}")).thenReturn(matches);
        assertEquals("1234", replacer.substitute("${random}"));

        Mockito.when(extractor.extract("${random}${random}")).thenReturn(matches);
        assertEquals("12341234", replacer.substitute("${random}${random}"));

        Mockito.when(extractor.extract("... ${random} ${random}    ")).thenReturn(matches);
        assertEquals("... 1234 1234    ", replacer.substitute("... ${random} ${random}    "));
    }

    @Test
    public void thatForcedPatternWorks() {
        replacer.register(new NumberGenerator()).forToken("random");
        replacer.withTokenStart("[");
        replacer.withTokenEnd("]");

        Map<String, Match> matches = new HashMap<String, Match>();
        matches.put("random", new Match("random", "random"));

        Mockito.when(extractor.extract("[random]")).thenReturn(matches);
        assertEquals("1234", replacer.substitute("[random]"));
    }

    @Test
    public void throwsIllegalStateExceptionIfNoGeneratorIsFound() {
        replacer.register(null).forToken("random");
        Map<String, Match> matches = new HashMap<String, Match>();
        matches.put("random", new Match("random", "random"));
        Mockito.when(extractor.extract("${random}")).thenReturn(matches);
        try {
            replacer.substitute("${random}");
            fail("expected IllegalStateException to be thrown");
        } catch (IllegalStateException expected) {

        }
    }

    private class NumberGenerator implements Generator {
        @Override
        public String generate() {
            return "1234";
        }
    }
}
