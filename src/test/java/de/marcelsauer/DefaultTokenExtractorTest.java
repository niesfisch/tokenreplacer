package de.marcelsauer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import de.marcelsauer.TokenExtractor.Match;

/**
 * @author msauer
 * 
 */
public class DefaultTokenExtractorTest {

    @Test
    public void thatDefaultEnclosingCharWorks() {
        TokenExtractor extractor = new DefaultTokenExtractor();
        Map<String, Match> matches = extractor.extract("${number}");
        assertEquals(1, matches.size());
        assertEquals(1, matches.get("number").amount);
        assertEquals("number", matches.get("number").tokenWithoutAmount);

        matches = extractor.extract("${number}${number}${number}");
        assertEquals(1, matches.size());
        assertEquals(1, matches.get("number").amount);
        assertEquals("number", matches.get("number").tokenWithoutAmount);

        matches = extractor.extract(".... ${number} .... ${char} ..... ${number} ....");
        assertEquals(2, matches.size());
        assertEquals(1, matches.get("number").amount);
        assertEquals("number", matches.get("number").tokenWithoutAmount);
        
        assertEquals(1, matches.get("char").amount);
        assertEquals("char", matches.get("char").tokenWithoutAmount);
        
    }

    @Test
    public void thatAmountWithDefaultEnclosingCharWorks() {
        TokenExtractor extractor = new DefaultTokenExtractor();
        Map<String, Match> matches = extractor.extract("${number[2]}");
        assertEquals(1, matches.size());
        assertEquals(2, matches.get("number[2]").amount);
        assertEquals("number", matches.get("number[2]").tokenWithoutAmount);
    }

    @Test
    public void thatAmountWithForcedEnclosingCharWorks() {
        TokenExtractor extractor = new DefaultTokenExtractor().withAmountStart("(").withAmountEnd(")");
        Map<String, Match> matches = extractor.extract("${number(10)}");
        assertEquals(1, matches.size());
        assertEquals(10, matches.get("number(10)").amount);
    }

    @Test
    public void thatForcedEnclosingCharWorks() {
        TokenExtractor extractor = new DefaultTokenExtractor().withTokenStart("[").withTokenEnd("]");
        Map<String, Match> matches = extractor.extract("[number]");
        assertEquals(1, matches.size());
        assertEquals(1, matches.get("number").amount);
    }

    @Test
    public void throwsIllegalArgumentExceptionForEmptyEnclosingChars() {
        try {
            TokenExtractor extractor = new DefaultTokenExtractor();
            extractor.extract("${ }");
            fail("expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException expected) {
        }

        try {
            TokenExtractor extractor = new DefaultTokenExtractor();
            extractor.extract("${        }");
            fail("expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException expected) {
        }

    }

    @Test
    public void throwsIllegalStateExceptionForEmptyTokens() {
        TokenExtractor extractor = new DefaultTokenExtractor().withTokenStart("(").withTokenEnd(null);
        expectIllegalStateExceptionForEmptyKeys(extractor);
        
        extractor = new DefaultTokenExtractor().withTokenStart(null).withTokenEnd(")");
        expectIllegalStateExceptionForEmptyKeys(extractor);
        
        extractor = new DefaultTokenExtractor().withTokenStart(null).withTokenEnd(null);
        expectIllegalStateExceptionForEmptyKeys(extractor);
        
        extractor = new DefaultTokenExtractor().withAmountStart(null).withAmountEnd(")");
        expectIllegalStateExceptionForEmptyKeys(extractor);
        
        extractor = new DefaultTokenExtractor().withAmountStart("(").withAmountEnd(null);
        expectIllegalStateExceptionForEmptyKeys(extractor);
    }
    
    private void expectIllegalStateExceptionForEmptyKeys(TokenExtractor extractor){
        try {
            extractor.extract("${}");
            fail("expected IllegalStateException to be thrown");
        } catch (IllegalStateException expected) {
        }
        
    }
}
