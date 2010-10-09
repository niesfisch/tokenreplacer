/**
 * Copyright (C) 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.marcelsauer.tokenreplacer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;

/**
 * @author msauer
 * 
 */
public class TokyTest {

	private TokenReplacer toky = new Toky();

	public void setUp() {
		this.toky = new Toky();
	}

	@Test
	public void thatStaticValuesWork() {
		toky.register("rand", "1234");
		assertEquals("abc 1234 def", toky.substitute("abc {rand} def"));

		toky.register(new Token("random").replacedBy("1234"));
		assertEquals("abc 1234 def", toky.substitute("abc {random} def"));
	}

	@Test
	public void thatGeneratedValuesWork() {
		toky.register("random", new NumberGenerator());
		toky.register(new Token("random").replacedBy(new NumberGenerator()));
		toky.register(new Token("somechar").replacedBy(new SomeCharGenerator()));

		assertEquals("98765", toky.substitute("{random}"));
		assertEquals("abc 98765", toky.substitute("{somechar} {random}"));
		assertEquals("9876598765", toky.substitute("{random}{random}"));
		assertEquals("... 98765 98765    ", toky.substitute("... {random} {random}    "));
	}

	@Test
	public void thatReplacementArraysWork() {
		toky.register(new String[] { "one" }).ignoreMissingValues();
		assertEquals("abc one {1} {2} def", toky.substitute("abc {0} {1} {2} def"));
		
		toky.register(new String[] { "", " " });
		assertEquals("abc    {2} def", toky.substitute("abc {0} {1} {2} def"));
		
		toky.register(new String[] {});
		assertEquals("xxx", toky.substitute("xxx"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void thatNullReplacementArraysThrowException() {
		toky.register((String[]) null);
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
	public void thatGeneratorCachingWorks() {
		toky.register("nonCachingDefault", new Generator() {
			Random rand = new Random();

			@Override
			public void inject(String[] args) {
			}

			@Override
			public String generate() {
				return String.valueOf(rand.nextInt());
			}
		});
		// caching is disabled by default
		String replacedFirst = toky.substitute("{nonCachingDefault}");
		String replacedSecond = toky.substitute("{nonCachingDefault}");
		assertTrue(replacedFirst != null && !"".equals(replacedFirst));
		assertFalse(replacedFirst.equals(replacedSecond));

		// enable
		toky.enableGeneratorCaching();
		replacedFirst = toky.substitute("{nonCachingDefault}");
		replacedSecond = toky.substitute("{nonCachingDefault}");
		assertTrue(replacedFirst.equals(replacedSecond));

		// disable
		toky.disableGeneratorCaching();
		replacedFirst = toky.substitute("{nonCachingDefault}");
		replacedSecond = toky.substitute("{nonCachingDefault}");
		assertTrue(replacedFirst != null && !"".equals(replacedFirst));
		assertFalse(replacedFirst.equals(replacedSecond));

	}

	@Test
	public void thatMoreThanOneCharArgumentsWork() {
		toky.register(new Token("dynamicValue").replacedBy(new DynamicGenerator()));
		assertEquals("110", toky.substitute("{dynamicValue(1,10)}"));
		assertEquals("101", toky.substitute("{dynamicValue(10,1)}"));
		assertEquals("aaabbbccc", toky.substitute("{dynamicValue(aaa,bbb,ccc)}"));
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
		assertEquals("a b c 1x2y3z static value +++", toky.substitute("a b c *dynamicValue[1x;2y;3z]# *static# +++"));
	}

	@Test
	public void thatInvalidEnclosingCharsResultInException() {
		toky.doNotIgnoreMissingValues();
		assertExceptedException("value}");
		assertExceptedException("{value");
		assertExceptedException("{value{");
		assertExceptedException("}value}");
		assertExceptedException("}value{");
	}

	@Test
	public void thatNoGeneratorOrValueResultsInException() {
		toky.doNotIgnoreMissingValues();
		assertExceptedException("{value}");
	}

	@Test
	public void thatEmptyStringsToSubstituteWork() {
		assertNull(toky.substitute(null));
		assertEquals("", toky.substitute(""));
	}

	@Test
	public void thatIgnoresMissingValuesWorksEvenIfValuesAreMissing() {
		toky.ignoreMissingValues();
		assertEquals("{willNotBeReplacedBecauseNotDefined}", toky.substitute("{willNotBeReplacedBecauseNotDefined}"));
		assertEquals("{willNotBeReplacedBecauseNotDefined(1,2)}", toky
				.substitute("{willNotBeReplacedBecauseNotDefined(1,2)}"));
	}

	@Test
	public void thatInvalidArgumentsResultsInException() {
		toky.register(new Token("value").replacedBy(new DynamicGenerator()));
		assertExceptedException("{value)}");
		assertExceptedException("{value(}");
		assertExceptedException("{value(1,)}");
		assertExceptedException("{value(1,2,)}");
		assertExceptedException("{value(,)}");
		assertExceptedException("{value(,,)}");
	}

	private void assertExceptedException(String toSubstitute) {
		try {
			toky.substitute(toSubstitute);
			fail(String.format("expected IllegalStateException to be thrown for token '%s'", toSubstitute));
		} catch (IllegalStateException expected) {
			// System.out.println("expected exception is: " + expected);
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
