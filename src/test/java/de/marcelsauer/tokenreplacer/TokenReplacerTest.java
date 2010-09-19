package de.marcelsauer.tokenreplacer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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
		Token token = new Token("random").replacedBy(new NumberGenerator());
		replacer.register(token);

		mockRandom(token);

		assertEquals("1234", replacer.substitute("${random}"));
		assertEquals("12341234", replacer.substitute("${random}${random}"));
		assertEquals("... 1234 1234    ", replacer.substitute("... ${random} ${random}    "));
	}

	private void mockRandom(Token token) {
		Set<Token> tokens = new HashSet<Token>();
		tokens.add(token);
		Mockito.when(extractor.extract(Mockito.anyString())).thenReturn(tokens);
	}

	@Test
	public void thatForcedPatternWorks() {
		Token token = new Token("random").replacedBy(new NumberGenerator()).withTokenStart("[").withTokenEnd("]");
		replacer.register(token);
		mockRandom(token);
		assertEquals("1234", replacer.substitute("[random]"));
	}

	@Test
	public void thatStaticStringsWork() {
		replacer.register("random", "666");
		mockRandom(new Token("random").replacedBy("666"));
		assertEquals("666", replacer.substitute("${random}"));
	}

	@Test
	public void throwsProperExceptionForInvalidSetup() {
		try {
			replacer.substitute("${random}");
			fail("expected IllegalStateException to be thrown");
		} catch (IllegalStateException expected) {
		}

		try {
			replacer.register(new Token("random"));
			fail("expected IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException expected) {
		}
	}

	private class NumberGenerator implements Generator {
		@Override
		public String generate() {
			return "1234";
		}

		@Override
		public void inject(String[] args) {
		}
	}
}
