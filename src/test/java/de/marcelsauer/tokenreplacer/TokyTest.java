package de.marcelsauer.tokenreplacer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
public class TokyTest {

	private TokenReplacer toky = new Toky();

	public void setUp() {
		this.toky = new Toky();
	}

	@Test
	public void thatStaticValuesWork() {
		toky.register(new Token("random").replacedBy("1234"));
		assertEquals("abc 1234 def", toky.substitute("abc {random} def"));
	}

	@Test
	public void thatGeneratedValuesWork() {
		toky.register(new Token("random").replacedBy(new NumberGenerator()));
		toky.register(new Token("somechar").replacedBy(new SomeCharGenerator()));

		assertEquals("98765", toky.substitute("{random}"));
		assertEquals("abc 98765", toky.substitute("{somechar} {random}"));
		assertEquals("9876598765", toky.substitute("{random}{random}"));
		assertEquals("... 98765 98765    ", toky.substitute("... {random} {random}    "));
	}

	@Test
	public void thatOverwrittenTokenDelimitersWork() {
		toky.register(new Token("random").replacedBy(new NumberGenerator())).withTokenStart("[").withTokenEnd("]");
		toky.register(new Token("somechar").replacedBy(new SomeCharGenerator()));

		assertEquals("98765 abc", toky.substitute("[random] [somechar]"));

		toky.register(new Token("random").replacedBy(new NumberGenerator())).withTokenStart("[").withTokenEnd("]");

		assertEquals("98765", toky.substitute("[random]"));
		assertEquals("9876598765", toky.substitute("[random][random]"));
		assertEquals("... 98765 98765    ", toky.substitute("... [random] [random]    "));
	}

	@Test
	public void thatGeneratorWithArgumentsWork() {
		toky.register(new Token("dynamicValue").replacedBy(new DynamicGenerator()));
		toky.register("static", "static value");
		assertEquals("123", toky.substitute("{dynamicValue(1,2,3)}"));

		toky.register(new Token("dynamicValue").replacedBy(new DynamicGenerator()));
		assertEquals("1 2 3", toky.substitute("{dynamicValue(1)} {dynamicValue(2)} {dynamicValue(3)}"));
	}

	@Test
	public void thatEmptyArgumentsWork() {
		toky.register(new Token("value").replacedBy(new DynamicGenerator()));
		assertEquals("abc", toky.substitute("{value()}"));
	}

	@Test
	public void thatAllFeaturesInOneWork() {
		toky.register(new Token("dynamicValue").replacedBy(new DynamicGenerator()));
		toky.register("static", "static value");
		toky.withTokenStart("*");
		toky.withTokenEnd("#");
		toky.withArgumentDelimiter(";");
		toky.withArgumentStart("[");
		toky.withArgumentEnd("]");

		toky.register("theToken", "abc");
		assertEquals("a b c 123 static value +++", toky.substitute("a b c *dynamicValue[1;2;3]# *static# +++"));
	}

	@Test
	public void thatInvalidEnclosingCharsResultInException() {
		assertExceptedException("value}");
		assertExceptedException("{value");
		assertExceptedException("{value{");
		assertExceptedException("}value}");
		assertExceptedException("}value{");
	}

	@Test
	public void thatNoGeneratorOrValueResultsInException() {
		assertExceptedException("{value}");
	}

	@Test
	public void thatInvalidArgumentsResultsInException() {
		toky.register(new Token("value").replacedBy(new DynamicGenerator()));
		assertExceptedException("{value)}");
		assertExceptedException("{value(}");
		assertExceptedException("{value(1,)}");
		assertExceptedException("{value(1,2,)}");
	}

	private void assertExceptedException(String toSubstitute) {
		try {
			toky.substitute(toSubstitute);
			fail(String.format("expected IllegalStateException to be thrown for token '%s'", toSubstitute));
		} catch (IllegalStateException expected) {
			System.out.println("expected exception is: " + expected);
		}
	}

	private class DynamicGenerator implements Generator {
		private String[] args;

		@Override
		public String generate() {
			if (args.length == 0) {
				return "abc";
			}
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
