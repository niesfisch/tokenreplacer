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

import java.util.HashMap;
import java.util.Map;

/**
 * parser implementation based on Finite State machine design <br/>
 * <br/>
 * NOT PART OF THE PUBLIC API! STILL HERE AND PUBLIC IN CASE YOU NEED TO
 * 'EMERGENCY' SUBCLASS.
 * 
 * @author msauer
 */
public class FiniteStateMachineTokenReplacer implements TokenReplacer {

	/**
	 * TODO handle end of string properly
	 */
	private static final char END_OF_STRING = '\0';

	protected char tokenStart = Constants.DEFAULT_TOKEN_START;
	protected char tokenEnd = Constants.DEFAULT_TOKEN_END;

	protected char argsStart = Constants.DEFAULT_ARGS_START;
	protected char argsEnd = Constants.DEFAULT_ARGS_END;
	protected char argsSep = Constants.DEFAULT_ARGS_SEPARATOR;

	protected boolean ignoreMissingValues = false;
	protected boolean generatorCachingEnabled = false;

	protected final Map<String, Token> tokens = new HashMap<String, Token>();

	protected enum State {
		READING_INPUT, TOKEN_STARTED, READING_TOKEN, TOKEN_ARGS_STARTED, READING_TOKEN_ARGS, TOKEN_ARGS_END, ERROR
	}

	@Override
	public String substitute(String toSubstitute) {

		StringBuilder tokenBuffer = new StringBuilder();
		StringBuilder argsBuffer = new StringBuilder();
		final StringBuilder resultBuffer = new StringBuilder();
		final Map<String, String> generatorCache = new HashMap<String, String>();

		if (toSubstitute == null) {
			return null;
		}

		State state = State.READING_INPUT;
		toSubstitute = toSubstitute + END_OF_STRING;

		for (int i = 0; i < toSubstitute.length(); ++i) {
			char c = toSubstitute.charAt(i);
			switch (state) {
			case READING_INPUT:
				if (isStdInput(c)) {
					state = State.READING_INPUT;
					resultBuffer.append(c);
				} else if (isEndOfString(c)) {
					state = State.READING_INPUT;
				} else if (isTokenStart(c)) {
					state = State.TOKEN_STARTED;
					tokenBuffer = new StringBuilder();
				} else {
					resultBuffer.append(c);
				}
				break;
			case TOKEN_STARTED:
				if (isStdInput(c)) {
					state = State.READING_TOKEN;
					tokenBuffer.append(c);
				} else {
					state = State.ERROR;
				}
				break;
			case READING_TOKEN:
				if (isStdInput(c)) {
					state = State.READING_TOKEN;
					tokenBuffer.append(c);
				} else if (isArgStart(c)) {
					state = State.TOKEN_ARGS_STARTED;
					argsBuffer = new StringBuilder();
				} else if (isTokenEnd(c)) {
					state = State.READING_INPUT;
					resultBuffer.append(evalToken(tokenBuffer, argsBuffer, generatorCache));
				} else {
					state = State.ERROR;
				}
				break;
			case TOKEN_ARGS_STARTED:
				if (isArgEnd(c)) {
					state = State.TOKEN_ARGS_END;
				} else if (isStdInput(c)) {
					state = State.READING_TOKEN_ARGS;
					argsBuffer.append(c);
				} else {
					state = State.ERROR;
				}
				break;
			case READING_TOKEN_ARGS:
				if (isArgEnd(c)) {
					state = State.TOKEN_ARGS_END;
				} else if (isStdInput(c)) {
					state = State.READING_TOKEN_ARGS;
					argsBuffer.append(c);
				} else {
					state = State.ERROR;
				}
				break;
			case TOKEN_ARGS_END:
				if (isArgEnd(c)) {
					state = State.TOKEN_ARGS_END;
				} else if (isTokenEnd(c)) {
					state = State.READING_INPUT;
					resultBuffer.append(evalToken(tokenBuffer, argsBuffer, generatorCache));
				} else {
					state = State.ERROR;
				}
				break;

			case ERROR:
				error();
			default:
				error();
			}
		}

		if (!isFinalStateReached(state)) {
			error();
		}

		return resultBuffer.toString();
	}

	private void error() {
		throw new ParseException(
				"Invalid input. The given String could not be parsed. Please check if all tokens, brackets etc. are correct.");
	}

	private boolean isFinalStateReached(State state) {
		return state == State.READING_INPUT;
	}

	private boolean isEndOfString(char c) {
		return c == END_OF_STRING;
	}

	private boolean isArgStart(char c) {
		return this.argsStart == c;
	}

	private boolean isArgEnd(char c) {
		return this.argsEnd == c;
	}

	private boolean isTokenEnd(char c) {
		return this.tokenEnd == c;
	}

	private boolean isTokenStart(char c) {
		return this.tokenStart == c;
	}

	private boolean isStdInput(char c) {
		boolean isIdentifier = isTokenStart(c) || isTokenEnd(c) || isArgStart(c) || isArgEnd(c) || isEndOfString(c);
		return (!isIdentifier);
	}

	protected boolean isStartOfToken(final char character) {
		return this.tokenStart == character;
	}

	protected boolean isStartOfArguments(final char character) {
		return this.argsStart == character;
	}

	protected boolean isEndOfArguments(final char character) {
		return this.argsEnd == character;
	}

	protected boolean isEndOfToken(final char character) {
		return this.tokenEnd == character;
	}

	protected String[] extractArgs(final String tokenName, final StringBuilder args) {
		if (args.length() == 0) {
			return new String[] {};
		}
		checkArgumentsAreValid(tokenName, args);
		String[] argsResult = args.toString().split(String.valueOf(this.argsSep));
		return argsResult;
	}

	/**
	 * stuff like {dynamic(1,)} or {dynamic(1,2,)} seems to be invalid TODO do
	 * proper parsing instead of regex
	 */
	protected void checkArgumentsAreValid(final String tokenName, final StringBuilder args) {
		if (args.length() > 0 && (args.toString().matches("^,.*") || args.toString().matches(".*,$"))) {
			throw new ParseException(String.format("the given arguments '%s' for token '%s' seem to be incorrect!",
					args.toString(), tokenName));
		}
	}

	protected String evalToken(final StringBuilder token, final StringBuilder args,
			final Map<String, String> generatorCache) {
		final String tokenName = token.toString();
		final String[] argsResult = extractArgs(tokenName, args);
		if (!this.tokens.containsKey(tokenName)) {
			if (this.ignoreMissingValues) {
				return tokenWithPossibleArguments(token, args);
			} else {
				throw new NoValueOrGeneratorFoundException(String.format("no value or generator for token '%s' found!",
						tokenName));
			}
		}
		return getGeneratorValue(tokenName, argsResult, generatorCache);
	}

	private String getGeneratorValue(final String tokenName, final String[] args,
			final Map<String, String> generatorCache) {
		String value = null;
		if (this.generatorCachingEnabled && generatorCache.containsKey(tokenName)) {
			return generatorCache.get(tokenName);
		}
		final Generator generator = this.tokens.get(tokenName).getGenerator();
		generator.inject(args);
		value = generator.generate();
		if (this.generatorCachingEnabled) {
			generatorCache.put(tokenName, value);
		}
		return value;
	}

	private String tokenWithPossibleArguments(final StringBuilder token, final StringBuilder args) {
		if (args.length() > 0) {
			return this.tokenStart + token.toString() + this.argsStart + args + this.argsEnd + this.tokenEnd;
		} else {
			return this.tokenStart + token.toString() + this.tokenEnd;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#register(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public TokenReplacer register(final String token, final String value) {
		Validate.notEmpty(token);
		Validate.notNull(value);
		this.register(new Token(token).replacedBy(value));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.marcelsauer.tokenreplacer.TokenReplacer#register(de.marcelsauer.
	 * tokenreplacer.Token)
	 */
	@Override
	public TokenReplacer register(final Token token) {
		Validate.notNull(token);
		Validate.notNull(token.getGenerator(), "please specifiy a value or a generator for the token!");
		this.tokens.put(token.getToken(), token);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#register(java.lang.String,
	 * de.marcelsauer.tokenreplacer.Generator)
	 */
	@Override
	public TokenReplacer register(String token, Generator generator) {
		Validate.notEmpty(token);
		Validate.notNull(generator);
		return this.register(new Token(token).replacedBy(generator));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#withTokenStart(java.lang.String
	 * )
	 */
	@Override
	public TokenReplacer withTokenStart(String tokenStart) {
		ensureOneChar(tokenStart);
		this.tokenStart = tokenStart.charAt(0);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#withTokenEnd(java.lang.String)
	 */
	@Override
	public TokenReplacer withTokenEnd(String tokenEnd) {
		ensureOneChar(tokenEnd);
		this.tokenEnd = tokenEnd.charAt(0);
		return this;
	}

	protected void ensureOneChar(String character) {
		if (character.length() != 1) {
			throw new IllegalArgumentException(String.format("the given string '%s' must be exactly of size 1",
					character));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#withArgumentDelimiter(java
	 * .lang.String)
	 */
	@Override
	public TokenReplacer withArgumentDelimiter(String argsSep) {
		ensureOneChar(argsSep);
		this.argsSep = argsSep.charAt(0);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#withArgumentStart(java.lang
	 * .String)
	 */
	@Override
	public TokenReplacer withArgumentStart(String argsStart) {
		ensureOneChar(argsStart);
		this.argsStart = argsStart.charAt(0);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#withArgumentEnd(java.lang.
	 * String)
	 */
	@Override
	public TokenReplacer withArgumentEnd(String argsEnd) {
		ensureOneChar(argsEnd);
		this.argsEnd = argsEnd.charAt(0);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#doNotIgnoreMissingValues()
	 */
	@Override
	public TokenReplacer doNotIgnoreMissingValues() {
		this.ignoreMissingValues = false;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#ignoreMissingValues()
	 */
	@Override
	public TokenReplacer ignoreMissingValues() {
		this.ignoreMissingValues = true;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#enableGeneratorCaching()
	 */
	@Override
	public TokenReplacer enableGeneratorCaching() {
		this.generatorCachingEnabled = true;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#disableGeneratorCaching()
	 */
	@Override
	public TokenReplacer disableGeneratorCaching() {
		this.generatorCachingEnabled = false;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#register(java.lang.String[])
	 */
	@Override
	public TokenReplacer register(String[] replacements) {
		Validate.notNull(replacements);
		int i = 0;
		for (String replacement : replacements) {
			this.register(new Token(String.valueOf(i)).replacedBy(replacement));
			i++;
		}
		return this;
	}

}
