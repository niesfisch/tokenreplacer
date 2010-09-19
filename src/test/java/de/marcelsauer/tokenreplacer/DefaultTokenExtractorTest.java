package de.marcelsauer.tokenreplacer;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

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
public class DefaultTokenExtractorTest {

	@Test
	public void thatNoArgsWork() {
		TokenExtractor extractor = new DefaultTokenExtractor();
		extractor.register(new Token("number"));

		Set<Token> matches = extractor.extract("${number}");
		assertEquals(1, matches.size());
		Token match = matches.iterator().next();
		assertEquals("number", match.getToken());
		assertEquals(0, match.getArgs().length);
	}

	@Test
	public void thatArgsWork() {
		TokenExtractor extractor = new DefaultTokenExtractor();
		extractor.register(new Token("number"));

		Set<Token> matches = extractor.extract("${number(a)}");
		assertEquals(1, matches.size());
		Token token = matches.iterator().next();
		assertEquals("number", token.getToken());
		assertEquals("${number(a)}", token.getFullToken());
		assertEquals(1, token.getArgs().length);
		assertEquals("a", token.getArgs()[0]);

		matches = extractor.extract("${number(1,2,3,4)}");
		assertEquals(1, matches.size());
		token = matches.iterator().next();
		assertEquals("number", token.getToken());
		assertEquals("${number(1,2,3,4)}", token.getFullToken());
		assertEquals(4, token.getArgs().length);
		assertEquals("1", token.getArgs()[0]);
		assertEquals("2", token.getArgs()[1]);
		assertEquals("3", token.getArgs()[2]);
		assertEquals("4", token.getArgs()[3]);
	}

	// @Test
	// public void throwsIllegalArgumentExceptionForEmptyEnclosingChars() {
	// expectIllegalArgumentExceptionForEmptyEnclosingChars("${ }");
	// expectIllegalArgumentExceptionForEmptyEnclosingChars("${        }");
	// }
	//
	// @Test
	// public void throwsIllegalStateExceptionForEmptyTokens() {
	// expectIllegalArgumentExceptionForEmptyKeys("(", null);
	// expectIllegalArgumentExceptionForEmptyKeys(null, ")");
	// expectIllegalArgumentExceptionForEmptyKeys(null, null);
	// expectIllegalArgumentExceptionForEmptyKeys(null, ")");
	// expectIllegalArgumentExceptionForEmptyKeys("(", null);
	// }
	//
	// private void expectIllegalArgumentExceptionForEmptyKeys(String start,
	// String end) {
	// try {
	// new DefaultTokenExtractor().register(null);
	// fail("expected IllegalArgumentException to be thrown");
	// } catch (IllegalArgumentException expected) {
	// }
	//
	// }
	//
	// private void expectIllegalArgumentExceptionForEmptyEnclosingChars(String
	// toReplace) {
	// try {
	// TokenExtractor extractor = new DefaultTokenExtractor();
	// extractor.extract(toReplace);
	// fail("expected IllegalArgumentException to be thrown");
	// } catch (IllegalArgumentException expected) {
	// }
	// }
}
