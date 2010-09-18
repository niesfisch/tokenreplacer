package de.marcelsauer.tokenreplacer;

import static org.junit.Assert.assertEquals;

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
public class TokenReplacerIntegrationTest {

	@Test
	public void thatDefaultPatternWorks() {
		TokenReplacer replacer = new TokenReplacer().register(new NumberGenerator()).register(new SomeCharGenerator());

		assertEquals("98765", replacer.substitute("${random}"));
		assertEquals("abc 98765", replacer.substitute("${somechar} ${random}"));
		assertEquals("9876598765", replacer.substitute("${random}${random}"));
		assertEquals("... 98765 98765    ", replacer.substitute("... ${random} ${random}    "));
		assertEquals("9876598765", replacer.substitute("${random[2]}"));
		assertEquals("98765.9876598765.987659876598765", replacer.substitute("${random[1]}.${random[2]}.${random[3]}"));
	}

	@Test
	public void thatForcedPatternWorks() {
		TokenReplacer replacer = new TokenReplacer().register(new NumberGenerator()).withTokenStart("[").withTokenEnd(
				"]").withAmountStart("{").withAmountEnd("}");

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

		@Override
		public String forToken() {
			return "random";
		}
	}

	private class SomeCharGenerator implements Generator {
		@Override
		public String generate() {
			return "abc";
		}

		@Override
		public String forToken() {
			return "somechar";
		}
	}
}
