package de.marcelsauer.tokenreplacer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.marcelsauer.tokenreplacer.TokenExtractor.Match;

/**
 * Token Replacer Copyright (C) 2010 Marcel Sauer <marcel DOT sauer AT gmx DOT
 * de>
 * 
 * This file is part of Token Replacer.
 * 
 * Token Replacer is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * Token Replacer is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Token Replacer. If not, see <http://www.gnu.org/licenses/>.
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
		replacer.register(new NumberGenerator());

		Map<String, Match> matches = newMatch("random");

		Mockito.when(extractor.extract("${random}")).thenReturn(matches);
		assertEquals("1234", replacer.substitute("${random}"));

		Mockito.when(extractor.extract("${random}${random}")).thenReturn(matches);
		assertEquals("12341234", replacer.substitute("${random}${random}"));

		Mockito.when(extractor.extract("... ${random} ${random}    ")).thenReturn(matches);
		assertEquals("... 1234 1234    ", replacer.substitute("... ${random} ${random}    "));
	}

	@Test
	public void thatForcedPatternWorks() {
		replacer.register(new NumberGenerator());
		replacer.withTokenStart("[");
		replacer.withTokenEnd("]");

		Map<String, Match> matches = newMatch("random");

		Mockito.when(extractor.extract("[random]")).thenReturn(matches);
		assertEquals("1234", replacer.substitute("[random]"));
	}

	@Test
	public void thatStaticStringsWork() {
		replacer.register("aStaticString", "the content");

		Map<String, Match> matches = newMatch("aStaticString");

		Mockito.when(extractor.extract("${aStaticString}")).thenReturn(matches);

		assertEquals("the content", replacer.substitute("${aStaticString}"));
	}

	private Map<String, Match> newMatch(String tokenWithAmount) {
		Map<String, Match> matches = new HashMap<String, Match>();
		matches.put(tokenWithAmount, new Match(tokenWithAmount, tokenWithAmount));
		return matches;
	}

	@Test
	public void throwsIllegalStateExceptionIfNoGeneratorIsFound() {
		Map<String, Match> matches = newMatch("random");

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

		@Override
		public String forToken() {
			return "random";
		}
	}
}
