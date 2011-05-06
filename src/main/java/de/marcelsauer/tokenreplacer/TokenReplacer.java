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


/**
 * @author msauer
 * @see Toky
 */
public interface TokenReplacer {

	/**
	 * replaces all {@link Token} with one of the following:
	 * 
	 * <ul>
	 * <li>the provided static values set via {@link #register(String, String)}
	 * <li>the token registered via {@link #register(Token)}
	 * <li>the generator registered via {@link #register(String, Generator)}
	 * <ul>
	 * 
	 * @param toSubstitute
	 *            the string that contains the tokens, will be returned as-is in
	 *            case of null or empty string
	 * @return the result after replacing all tokens with the proper values
	 * @throws ParseException
	 *             when the internal state is incorrect and error reporting was
	 *             turned on via {@link #doNotIgnoreMissingValues()}
	 * @throws NoValueOrGeneratorFoundException
	 *             when no explicit value or {@link Generator} was found for the token and
	 *             we don't ignore errors via {@link #doNotIgnoreMissingValues()}
	 */
	String substitute(final String toSubstitute);

	/**
	 * registers a static value for a given token. if you need dynamic behaviour
	 * then use {@link #register(Generator)}. same as registering a token via
	 * {@link #register(Token)} and supplying a replacement value via
	 * {@link Token#replacedBy(String)}.
	 * 
	 * @param token
	 *            the name of the token to be replaced e.g. for ${date} ->
	 *            "date" would be the token, must not be null or empty
	 * @param value
	 *            the static value that will be used when replacing the token,
	 *            must not be null or empty
	 * @return the {@link TokenReplacer} to allow method chaining
	 */
	TokenReplacer register(String token, String value);

	/**
	 * registers a {@link Token} that needs to be replaced.
	 * 
	 * @param token
	 *            the {@link Token}, must not be null, the token must have a
	 *            valid value or generator associated with it which was set via
	 *            {@link Token#replacedBy(String)} or
	 *            {@link Token#replacedBy(String)}
	 * @return the {@link #TokenReplacer} to allow method chaining
	 */
	TokenReplacer register(Token token);

	/**
	 * registers a {@link Token} that will be replaced by the given
	 * {@link Generator}. same as registering a token via
	 * {@link #register(Token)} and supplying a generator via
	 * {@link Token#replacedBy(Generator)}
	 * 
	 * @param token
	 *            the name of the token to be replaced e.g. for ${date} ->
	 *            "date" would be the token, must not be null or empty
	 * @param generator
	 *            the {@link #Generator} to use when replacing the value, must
	 *            not be null or empty
	 * @return the {@link #TokenReplacer} to allow method chaining
	 */
	TokenReplacer register(String token, Generator Generator);

	/**
	 * registers an array of replacements for a string based in indexed tokens.
	 * the tokens will be replaced in the order they were added to the array.
	 * 
	 * e.g.
	 * 
	 * <pre>
	 * toky.register(new String[] { "one", "two", "three" });
	 * toky.substitute("{0} {1} {2}")); // will result in "one two three"
	 * </pre>
	 * 
	 * @param replacements
	 *            the array of replacements that will be used when replacing an
	 *            indexed strings, must not be null but can be empty
	 * @return the {@link #TokenReplacer} to allow method chaining
	 */
	TokenReplacer register(String[] replacements);

	/**
	 * @param tokenStart
	 *            sets the token start identifier to the given value e.g.
	 *            [dynamic] -> '[' would be the start identifier, e.g. '[', must
	 *            not be null or empty
	 * @return the {@link #TokenReplacer} to allow method chaining
	 */
	TokenReplacer withTokenStart(String tokenStart);

	/**
	 * @param tokenEnd
	 *            sets the token end identifier to the given value e.g.
	 *            [dynamic] -> ']' would be the end identifier, e.g. '[', must
	 *            not be null or empty
	 * @return the {@link #TokenReplacer} to allow method chaining
	 */
	TokenReplacer withTokenEnd(String tokenEnd);

	/**
	 * @param argsSep
	 *            changes the delimiter of the arguments to the given value e.g.
	 *            {dynamic(1;2;3)} -> ';' would be the delimiter, must not be
	 *            null or empty
	 * @return the {@link #TokenReplacer} to allow method chaining
	 */
	TokenReplacer withArgumentDelimiter(String argsSep);

	/**
	 * @param argsStart
	 *            sets the argument start identifier to the given value e.g.
	 *            {dynamic[1;2;3]} -> '[' would be the delimiter e.g. '[', must
	 *            not be null or empty
	 * @return the {@link #TokenReplacer} to allow method chaining
	 */
	TokenReplacer withArgumentStart(String argsStart);

	/**
	 * @param argsEnd
	 *            sets the argument end identifier to the given value e.g.
	 *            {dynamic[1;2;3]} -> ']' would be the delimiter e.g. ']', must
	 *            not be null or empty
	 * @return the {@link #TokenReplacer} to allow method chaining
	 */
	TokenReplacer withArgumentEnd(String argsEnd);

	/**
	 * tells the {@link TokenReplacer} to report any tokens that can not be
	 * replaced. if turned on an {@link IllegalStateException} will be thrown
	 * during token replacement. reporting errors is turned ON by DEFAULT.
	 * 
	 * @return the {@link #TokenReplacer} to allow method chaining
	 */
	TokenReplacer doNotIgnoreMissingValues();

	/**
	 * tells the {@link TokenReplacer} to IGNORE any tokens that can not be
	 * replaced. if turned OFF no Exceptions will be thrown during token
	 * replacement. reporting errors is turned ON by DEFAULT.
	 * 
	 * @return the {@link #TokenReplacer} to allow method chaining
	 */
	TokenReplacer ignoreMissingValues();

	/**
	 * turns generator caching ON. once a value is determined through a
	 * {@link Generator} all remaining values with the same token name will be
	 * replaced by the cached version. use {@link #disableGeneratorCaching()} to
	 * turn caching off.
	 * 
	 * @return the {@link #TokenReplacer} to allow method chaining
	 */
	TokenReplacer enableGeneratorCaching();

	/**
	 * turns generator caching OFF. use {@link #enableGeneratorCaching()} to
	 * turn caching on.
	 * 
	 * @return the {@link #TokenReplacer} to allow method chaining
	 */
	TokenReplacer disableGeneratorCaching();

}