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
 */
public class TokyTest {

	private final TokenReplacer toky = new Toky();

	@Test
	public void thatNothingToReplaceWorks () {
		String bounceItBack = "   abc   def   ";
		assertEquals(bounceItBack, this.toky.substitute(bounceItBack));
	}

	@Test
	public void thatALongStringWorks () {
		String aLongString = "asldfjhas fasjdhfa sjdhf asjf askjhf aksh\n\n\n\nasjhf sjahf lsjdhfalsjdhfjh j jsdhfa ksjhf akjshf";
		assertEquals(aLongString, this.toky.substitute(aLongString));
	}

	@Test
	public void thatStaticValuesWork () {
		this.toky.register("rand", "1234");
		assertEquals("abc 1234 def", this.toky.substitute("abc {rand} def"));

		this.toky.register(new Token("random").replacedBy("1234"));
		assertEquals("abc 1234 def", this.toky.substitute("abc {random} def"));
	}

	@Test
	public void thatGeneratedValuesWork () {
		this.toky.register("random", new NumberGenerator());
		this.toky.register(new Token("random").replacedBy(new NumberGenerator()));
		this.toky.register(new Token("somechar").replacedBy(new SomeCharGenerator()));

		assertEquals("98765", this.toky.substitute("{random}"));
		assertEquals("abc 98765", this.toky.substitute("{somechar} {random}"));
		assertEquals("9876598765", this.toky.substitute("{random}{random}"));
		assertEquals("... 98765 98765    ", this.toky.substitute("... {random} {random}    "));
	}

	@Test
	public void thatReplacementArraysWork () {
		this.toky.register(new String[] {"one"}).ignoreMissingValues();
		assertEquals("abc one {1} {2} def", this.toky.substitute("abc {0} {1} {2} def"));

		this.toky.register(new String[] {"", " "});
		assertEquals("abc    {2} def", this.toky.substitute("abc {0} {1} {2} def"));

		this.toky.register(new String[] {});
		assertEquals("xxx", this.toky.substitute("xxx"));

		this.toky.register(new String[] {"one"});
		assertEquals("oneoneone", this.toky.substitute("{0}{0}{0}"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void thatNullReplacementArraysThrowException () {
		this.toky.register((String[])null);
	}

	@Test
	public void thatOverwrittenTokenDelimitersWork () {
		this.toky.register(new Token("random").replacedBy(new NumberGenerator())).withTokenStart("[").withTokenEnd("]");
		this.toky.register(new Token("somechar").replacedBy(new SomeCharGenerator()));

		assertEquals("98765 abc", this.toky.substitute("[random] [somechar]"));

		this.toky.register(new Token("random").replacedBy(new NumberGenerator())).withTokenStart("[").withTokenEnd("]");

		assertEquals("98765", this.toky.substitute("[random]"));
		assertEquals("9876598765", this.toky.substitute("[random][random]"));
		assertEquals("... 98765 98765    ", this.toky.substitute("... [random] [random]    "));
	}

	@Test
	public void thatGeneratorWithArgumentsWork () {
		this.toky.register(new Token("dynamicValue").replacedBy(new DynamicGenerator()));
		this.toky.register("static", "static value");
		assertEquals("123", this.toky.substitute("{dynamicValue(1,2,3)}"));

		this.toky.register(new Token("dynamicValue").replacedBy(new DynamicGenerator()));
		assertEquals("1 2 3", this.toky.substitute("{dynamicValue(1)} {dynamicValue(2)} {dynamicValue(3)}"));
	}

	@Test
	public void thatEmptyArgumentsWork () {
		this.toky.register(new Token("value").replacedBy(new DynamicGenerator()));
		assertEquals("abc", this.toky.substitute("{value()}"));
	}

	@Test
	public void thatGeneratorCachingWorks () {
		this.toky.register("nonCachingDefault", new Generator() {

			Random rand = new Random();

			@Override
			public void inject (String[] args) {
			}

			@Override
			public String generate () {
				return String.valueOf(this.rand.nextInt());
			}
		});
		// caching is disabled by default
		String replacedFirst = this.toky.substitute("{nonCachingDefault}");
		String replacedSecond = this.toky.substitute("{nonCachingDefault}");
		assertTrue(replacedFirst != null && !"".equals(replacedFirst));
		assertFalse(replacedFirst.equals(replacedSecond));

		// enable
		this.toky.enableGeneratorCaching();
		replacedFirst = this.toky.substitute("{nonCachingDefault}");
		replacedSecond = this.toky.substitute("{nonCachingDefault}");
		assertTrue(replacedFirst.equals(replacedSecond));

		// disable
		this.toky.disableGeneratorCaching();
		replacedFirst = this.toky.substitute("{nonCachingDefault}");
		replacedSecond = this.toky.substitute("{nonCachingDefault}");
		assertTrue(replacedFirst != null && !"".equals(replacedFirst));
		assertFalse(replacedFirst.equals(replacedSecond));

	}

	@Test
	public void thatMoreThanOneCharArgumentsWork () {
		this.toky.register(new Token("dynamicValue").replacedBy(new DynamicGenerator()));
		assertEquals("110", this.toky.substitute("{dynamicValue(1,10)}"));
		assertEquals("101", this.toky.substitute("{dynamicValue(10,1)}"));
		assertEquals("aaabbbccc", this.toky.substitute("{dynamicValue(aaa,bbb,ccc)}"));
	}

	@Test
	public void thatAllFeaturesInOneWork () {
		this.toky.register(new Token("dynamicValue").replacedBy(new DynamicGenerator()));
		this.toky.register("static", "static value");
		this.toky.withTokenStart("*");
		this.toky.withTokenEnd("#");
		this.toky.withArgumentDelimiter(";");
		this.toky.withArgumentStart("[");
		this.toky.withArgumentEnd("]");

		this.toky.register("theToken", "abc");
		assertEquals("a b c 1x2y3z static value +++", this.toky.substitute("a b c *dynamicValue[1x;2y;3z]# *static# +++"));
	}

	@Test
	public void thatInvalidEnclosingCharsResultInException () {
		this.toky.doNotIgnoreMissingValues();
		this.toky.register("token", new NumberGenerator());
		assertParseException("abc {token(1,2)}} end");

		assertParseException("value}");
		assertParseException("{value");
		assertParseException("{value{");
		assertParseException("}value}");
		assertParseException("}value{");
		assertParseException("value()");
		assertParseException("value(.....)");
		assertParseException("value)(");
		assertParseException("(");
		assertParseException(")");
		assertParseException("((");
		assertParseException("))");
		assertParseException("{}");
		assertParseException("}{");
		assertParseException("}(){");
		assertParseException("abc {} def");
		assertParseException("abc {()} def");
		assertParseException("abc {{token}} end");
		assertParseException("abc {token(1,2} end");

	}

	@Test(expected = NoValueOrGeneratorFoundException.class)
	public void thatNoGeneratorOrValueResultsInException () {
		this.toky.doNotIgnoreMissingValues();
		this.toky.substitute("{value}");
	}

	@Test
	public void thatEmptyStringsToSubstituteWork () {
		assertNull(this.toky.substitute(null));
		assertEquals("", this.toky.substitute(""));
	}

	@Test
	public void thatIgnoresMissingValuesWorksEvenIfValuesAreMissing () {
		this.toky.ignoreMissingValues();
		assertEquals("{willNotBeReplacedBecauseNotDefined}", this.toky.substitute("{willNotBeReplacedBecauseNotDefined}"));
		assertEquals("{willNotBeReplacedBecauseNotDefined(1,2)}", this.toky.substitute("{willNotBeReplacedBecauseNotDefined(1,2)}"));
	}

	@Test
	public void thatInvalidArgumentsResultsInException () {
		this.toky.register(new Token("value").replacedBy(new DynamicGenerator()));
		assertParseException("{value)}");
		assertParseException("{value(}");
		assertParseException("{value(1,)}");
		assertParseException("{value(1,2,)}");
		assertParseException("{value(,)}");
		assertParseException("{value(,,)}");
	}

	private void assertParseException (String toSubstitute) {
		try {
			this.toky.substitute(toSubstitute);
			fail(String.format("expected IllegalStateException to be thrown for token '%s'", toSubstitute));
		} catch (ParseException expected) {
			// expected
		}
	}

	private class DynamicGenerator implements Generator {

		private String[] args;

		@Override
		public String generate () {
			if (this.args.length == 0) {
				return "abc";
			}
			String result = "";
			for (String arg : this.args) {
				result += arg;
			}
			return result;
		}

		@Override
		public void inject (String[] args) {
			this.args = args;
		}
	}

	private class NumberGenerator implements Generator {

		@Override
		public String generate () {
			return "98765";
		}

		@Override
		public void inject (String[] args) {
		}
	}

	private class SomeCharGenerator implements Generator {

		@Override
		public String generate () {
			return "abc";
		}

		@Override
		public void inject (String[] args) {
		}
	}
}
