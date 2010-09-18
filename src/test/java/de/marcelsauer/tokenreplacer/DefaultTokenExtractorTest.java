package de.marcelsauer.tokenreplacer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

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
		expectIllegalArgumentExceptionForEmptyEnclosingChars("${ }");
		expectIllegalArgumentExceptionForEmptyEnclosingChars("${        }");
	}

	@Test
	public void throwsIllegalStateExceptionForEmptyTokens() {
		expectIllegalArgumentExceptionForEmptyKeys("(", null);
		expectIllegalArgumentExceptionForEmptyKeys(null, ")");
		expectIllegalArgumentExceptionForEmptyKeys(null, null);
		expectIllegalArgumentExceptionForEmptyKeys(null, ")");
		expectIllegalArgumentExceptionForEmptyKeys("(", null);
	}

	private void expectIllegalArgumentExceptionForEmptyKeys(String start, String end) {
		try {
			new DefaultTokenExtractor().withTokenStart(start).withTokenEnd(end);
			fail("expected IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException expected) {
		}

	}

	private void expectIllegalArgumentExceptionForEmptyEnclosingChars(String toReplace) {
		try {
			TokenExtractor extractor = new DefaultTokenExtractor();
			extractor.extract(toReplace);
			fail("expected IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException expected) {
		}
	}
}
