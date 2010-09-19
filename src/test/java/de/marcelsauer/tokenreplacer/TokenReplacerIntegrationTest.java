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
	public void thatItWorks() {
		TokenReplacer replacer = null;

		replacer = new TokenReplacer().register(new Token("random").replacedBy("1234"));
		assertEquals("1234", replacer.substitute("${random}"));

		replacer = new TokenReplacer();
		replacer.register(new Token("random").replacedBy(new NumberGenerator()));
		replacer.register(new Token("somechar").replacedBy(new SomeCharGenerator()));

		assertEquals("98765", replacer.substitute("${random}"));
		assertEquals("abc 98765", replacer.substitute("${somechar} ${random}"));
		assertEquals("9876598765", replacer.substitute("${random}${random}"));
		assertEquals("... 98765 98765    ", replacer.substitute("... ${random} ${random}    "));

		replacer = new TokenReplacer();
		replacer.register(new Token("random").replacedBy(new NumberGenerator()).withTokenStart("[").withTokenEnd("]"));
		replacer.register(new Token("somechar").replacedBy(new SomeCharGenerator()).withTokenStart("#").withTokenEnd(
				"*"));

		assertEquals("98765 abc", replacer.substitute("[random] #somechar*"));

		replacer = new TokenReplacer();
		replacer.register(new Token("random").replacedBy(new NumberGenerator()).withTokenStart("[").withTokenEnd("]"));

		assertEquals("98765", replacer.substitute("[random]"));
		assertEquals("9876598765", replacer.substitute("[random][random]"));
		assertEquals("... 98765 98765    ", replacer.substitute("... [random] [random]    "));

		replacer = new TokenReplacer();
		replacer.register(new Token("dynamicValue").replacedBy(new DynamicGenerator()));
		replacer.register("static", "static value");
		assertEquals("123", replacer.substitute("${dynamicValue(1,2,3)}"));
		
		// TODO
//		replacer = new TokenReplacer();
//		replacer.register(new Token("dynamicValue").replacedBy(new DynamicGenerator()));
//		assertEquals("123", replacer.substitute("${dynamicValue(1)} ${dynamicValue(2) ${dynamicValue(3)}}"));

	}

	private class DynamicGenerator implements Generator {
		private String[] args;

		@Override
		public String generate() {
			String result = "";
			for (String arg : args) {
				result += arg;
			}
			return result;
		}

		@Override
		public void inject(String[] args) {
			this.args = args;
		}
	}

	private class NumberGenerator implements Generator {
		@Override
		public String generate() {
			return "98765";
		}

		@Override
		public void inject(String[] args) {
		}
	}

	private class SomeCharGenerator implements Generator {
		@Override
		public String generate() {
			return "abc";
		}

		@Override
		public void inject(String[] args) {
		}
	}
}
