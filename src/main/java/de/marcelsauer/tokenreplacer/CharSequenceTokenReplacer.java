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

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * parser implementation based on {@link CharacterIterator} and {@link StringCharacterIterator} NOT PART OF THE PUBLIC API! STILL HERE AND
 * PUBLIC IN CASE YOU NEED TO 'EMERGENCY' SUBCLASS.
 * 
 * @author msauer
 */
public class CharSequenceTokenReplacer implements TokenReplacer {

	private static final char END_OF_STRING = '$';
	/**
	 * token stuff
	 */
	protected char tokenStart = Constants.DEFAULT_TOKEN_START;
	protected char tokenEnd = Constants.DEFAULT_TOKEN_END;

	/**
	 * argument stuff
	 */
	protected char argsStart = Constants.DEFAULT_ARGS_START;
	protected char argsEnd = Constants.DEFAULT_ARGS_END;
	protected char argsSep = Constants.DEFAULT_ARGS_SEPARATOR;

	/**
	 * token state
	 */
	protected StringBuilder token = new StringBuilder();

	/**
	 * argument state
	 */
	protected StringBuilder args = new StringBuilder();

	/**
	 * the result that will be returned
	 */
	protected StringBuilder result = new StringBuilder();

	protected Map<String, String> generatorCache = new HashMap<String, String>();

	protected boolean ignoreMissingValues = false;
	protected boolean generatorCachingEnabled = false;

	protected final Map<String, Token> tokens = new HashMap<String, Token>();

	@Override
	public String substitute (String toSubstitute) {

		if (toSubstitute == null) {
			return null;
		}

		reset(true);

		int state = 1;
		toSubstitute = toSubstitute + END_OF_STRING;

		for (int i = 0; i < toSubstitute.length(); ++i) {
			char c = toSubstitute.charAt(i);
			switch (state) {
				case 1:
					if (isStdInput(c)) {
						state = 1;
						this.result.append(c);
					} else if (isEndOfString(c)) {
						state = 1;
					} else if (isTokenStart(c)) {
						state = 2;
						this.token = new StringBuilder();
					} else {
						state = -1;
					}
					break;
				case 2:
					if (isStdInput(c)) {
						state = 3;
						this.token.append(c);
					} else if (isTokenStart(c)) {
						state = 5;
					} else if (isTokenEnd(c)) {
						state = 5;
					} else {
						state = -1;
					}
					break;
				case 3:
					if (isStdInput(c)) {
						state = 3;
						this.token.append(c);
					} else if (isArgStart(c)) {
						state = 6;
						this.args = new StringBuilder();
					} else if (isTokenEnd(c)) {
						state = 1;
						this.result.append(evalToken());
					} else {
						state = -1;
					}
					break;
				case 6:
					if (isArgStart(c)) {
						state = 7;
					} else if (isArgEnd(c)) {
						state = 9;
					} else if (isTokenEnd(c)) {
						state = 11;
					} else if (isStdInput(c)) {
						state = 8;
						this.args.append(c);
					} else {
						state = -1;
					}
					break;
				case 8:
					if (isArgEnd(c)) {
						state = 9;
					} else if (isStdInput(c)) {
						state = 8;
						this.args.append(c);
					} else {
						state = -1;
					}
					break;
				case 9:
					if (isArgEnd(c)) {
						state = 9;
					} else if (isStdInput(c)) {
						state = 10;
					} else if (isTokenEnd(c)) {
						state = 1;
						this.result.append(evalToken());
					} else {
						state = -1;
					}
					break;
				case 11:
					throw new IllegalStateException("(}");
				case 10:
					throw new IllegalStateException("()xxx");
				case 7:
					throw new IllegalStateException("Args Start can not follow a args start ((");
				case 4:
					throw new IllegalStateException("Token End can not immeditalty follow a token start {}");
				case 5:
					throw new IllegalStateException("Token Start can not follow a token start {{");
				case -1:
					throw new IllegalStateException("Invalid input.");
				default:
					throw new IllegalStateException();
			}
		}

		if (!isFinalStateReached(state)) {
			throw new IllegalStateException("the given string could not be parsed.");
		}

		return this.result.toString();
	}

	private boolean isFinalStateReached (int state) {
		return state == 1;
	}

	private boolean isEndOfString (char c) {
		return c == END_OF_STRING;
	}

	private boolean isArgStart (char c) {
		return this.argsStart == c;
	}

	private boolean isArgEnd (char c) {
		return this.argsEnd == c;
	}

	private boolean isTokenEnd (char c) {
		return this.tokenEnd == c;
	}

	private boolean isTokenStart (char c) {
		return this.tokenStart == c;
	}

	private boolean isStdInput (char c) {
		boolean isIdentifier = isTokenStart(c) || isTokenEnd(c) || isArgStart(c) || isArgEnd(c) || isEndOfString(c);
		return (!isIdentifier);
	}

	protected boolean isStartOfToken (final char character) {
		return this.tokenStart == character;
	}

	protected boolean isStartOfArguments (final char character) {
		return this.argsStart == character;
	}

	protected boolean isEndOfArguments (final char character) {
		return this.argsEnd == character;
	}

	protected boolean isEndOfToken (final char character) {
		return this.tokenEnd == character;
	}

	protected String[] extractArgs (final String tokenName) {
		final List<String> result = new ArrayList<String>();
		checkArgumentLength(tokenName);
		checkArgumentsAreValid(tokenName);
		final StringTokenizer en = new StringTokenizer(this.args.toString(), String.valueOf(this.argsSep));
		while (en.hasMoreTokens()) {
			result.add(en.nextToken());
		}
		return result.toArray(new String[] {});
	}

	/**
	 * stuff like {dynamic(1,)} or {dynamic(1,2,)} seems to be invalid
	 */
	protected void checkArgumentsAreValid (final String tokenName) {
		if (this.args.length() > 0 && (this.args.toString().matches("^,.*") || this.args.toString().matches(".*,$"))) {
			throw new IllegalStateException(String.format("the given arguments '%s' for token '%s' seem to be incorrect!", this.args.toString(),
					tokenName));
		}
	}

	protected void checkArgumentLength (final String tokenName) {
		// if (this.state this.args.length() == 0) {
		// throw new IllegalStateException(String.format("the given arguments for token '%s' were empty!", tokenName));
		// }
	}

	protected void reset (final boolean resetResult) {
		this.token = new StringBuilder();
		this.args = new StringBuilder();
		if (resetResult) {
			this.result = new StringBuilder();
		}
	}

	protected String evalToken () {
		final String tokenName = this.token.toString();
		final String[] args = extractArgs(tokenName);
		if (!this.tokens.containsKey(tokenName)) {
			if (this.ignoreMissingValues) {
				return tokenWithPossibleArguments();
			} else {
				throw new IllegalStateException(String.format("no value or generator for token '%s' found!", tokenName));
			}
		}
		return getGeneratorValue(tokenName, args);
	}

	private String getGeneratorValue (final String tokenName, final String[] args) {
		String value = null;
		if (this.generatorCachingEnabled && this.generatorCache.containsKey(tokenName)) {
			return this.generatorCache.get(tokenName);
		}
		final Generator generator = this.tokens.get(tokenName).getGenerator();
		generator.inject(args);
		value = generator.generate();
		this.generatorCache.put(tokenName, value);
		return value;
	}

	private String tokenWithPossibleArguments () {
		if (this.args.length() > 0) {
			return this.tokenStart + this.token.toString() + this.argsStart + this.args + this.argsEnd + this.tokenEnd;
		} else {
			return this.tokenStart + this.token.toString() + this.tokenEnd;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#register(java.lang.String, java.lang.String)
	 */
	@Override
	public TokenReplacer register (final String token, final String value) {
		Validate.notEmpty(token);
		Validate.notNull(value);
		this.register(new Token(token).replacedBy(value));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @seede.marcelsauer.tokenreplacer.TokenReplacer#register(de.marcelsauer. tokenreplacer.Token)
	 */
	@Override
	public TokenReplacer register (final Token token) {
		Validate.notNull(token);
		Validate.notNull(token.getGenerator(), "please specifiy a value or a generator for the token!");
		this.tokens.put(token.getToken(), token);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#register(java.lang.String, de.marcelsauer.tokenreplacer.Generator)
	 */
	@Override
	public TokenReplacer register (String token, Generator generator) {
		Validate.notEmpty(token);
		Validate.notNull(generator);
		return this.register(new Token(token).replacedBy(generator));
	}

	/*
	 * (non-Javadoc)
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#withTokenStart(java.lang.String )
	 */
	@Override
	public TokenReplacer withTokenStart (String tokenStart) {
		ensureOneChar(tokenStart);
		this.tokenStart = tokenStart.charAt(0);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#withTokenEnd(java.lang.String)
	 */
	@Override
	public TokenReplacer withTokenEnd (String tokenEnd) {
		ensureOneChar(tokenEnd);
		this.tokenEnd = tokenEnd.charAt(0);
		return this;
	}

	protected void ensureOneChar (String character) {
		if (character.length() != 1) {
			throw new IllegalArgumentException(String.format("the given string '%s' must be exactly of size 1", character));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#withArgumentDelimiter(java .lang.String)
	 */
	@Override
	public TokenReplacer withArgumentDelimiter (String argsSep) {
		ensureOneChar(argsSep);
		this.argsSep = argsSep.charAt(0);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#withArgumentStart(java.lang .String)
	 */
	@Override
	public TokenReplacer withArgumentStart (String argsStart) {
		ensureOneChar(argsStart);
		this.argsStart = argsStart.charAt(0);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#withArgumentEnd(java.lang. String)
	 */
	@Override
	public TokenReplacer withArgumentEnd (String argsEnd) {
		ensureOneChar(argsEnd);
		this.argsEnd = argsEnd.charAt(0);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#doNotIgnoreMissingValues()
	 */
	@Override
	public TokenReplacer doNotIgnoreMissingValues () {
		this.ignoreMissingValues = false;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#ignoreMissingValues()
	 */
	@Override
	public TokenReplacer ignoreMissingValues () {
		this.ignoreMissingValues = true;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#enableGeneratorCaching()
	 */
	@Override
	public TokenReplacer enableGeneratorCaching () {
		this.generatorCachingEnabled = true;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#disableGeneratorCaching()
	 */
	@Override
	public TokenReplacer disableGeneratorCaching () {
		this.generatorCachingEnabled = false;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#register(java.lang.String[])
	 */
	@Override
	public TokenReplacer register (String[] replacements) {
		Validate.notNull(replacements);
		int i = 0;
		for (String replacement : replacements) {
			this.register(new Token(String.valueOf(i)).replacedBy(replacement));
			i++;
		}
		return this;
	}

}
