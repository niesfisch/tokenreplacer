/**
 * Token Replacer
 * Copyright (C) 2010 Marcel Sauer <marcel DOT sauer AT gmx DOT de>
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

package de.marcelsauer.tokenreplacer;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * parser implementation based on {@link CharacterIterator} and
 * {@link StringCharacterIterator}
 * 
 * NOT PART OF THE PUBLIC API!
 * 
 * @author msauer
 */
public class CharSequenceTokenReplacer implements TokenReplacer {

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
	protected boolean isTokenStart;
	protected StringBuilder token = new StringBuilder();

	/**
	 * argument state
	 */
	protected boolean isArgsStarted;
	protected StringBuilder args = new StringBuilder();

	/**
	 * the result that will be returned
	 */
	protected StringBuilder result = new StringBuilder();

	protected Map<String, String> generatorCache = new HashMap<String, String>();

	protected boolean ignoreMissingValues = false;
	protected boolean generatorCachingEnabled = false;

	protected final Map<String, Token> tokens = new HashMap<String, Token>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#substitute(java.lang.String)
	 */
	@Override
	public String substitute(final String toSubstitute) {
		if (toSubstitute == null) {
			return null;
		}

		// reset stuff in case we hold state and the instance is reused
		reset(true);

		final CharacterIterator it = new StringCharacterIterator(toSubstitute);

		for (char character = it.first(); character != CharacterIterator.DONE; character = it.next()) {
			if (isEndOfToken(character)) {
				checkEndOfTokenState(toSubstitute);
				isTokenStart = false;
				result.append(evalToken());
				reset(false);
			} else if (isEndOfArguments(character)) {
				checkEndOfArgumentsState(toSubstitute);
				isArgsStarted = false;
			} else if (isStartOfArguments(character)) {
				isArgsStarted = true;
			} else if (appendToToken()) {
				token.append(character);
			} else if (isStartOfToken(character)) {
				isTokenStart = true;
			} else if (appendToResult()) {
				result.append(character);
			} else if (appendToArgs()) {
				args.append(character);
			}
		}
		checkState(toSubstitute);
		return result.toString();
	}

	protected void checkEndOfArgumentsState(final String toSubstitute) {
		if (!isArgsStarted) {
			throw new IllegalStateException(String.format("missing start '%s' for argument in string '%s'!",
					this.argsStart, toSubstitute));
		}

	}

	protected void checkState(final String toSubstitute) {
		if (isTokenStart) {
			throw new IllegalStateException(String.format("missing  end '%s' for token in string '%s'!", this.tokenEnd,
					toSubstitute));
		}
	}

	protected void checkEndOfTokenState(final String toSubstitute) {
		if (!isTokenStart) {
			throw new IllegalStateException(String.format("missing start '%s' for token in string '%s'!",
					this.tokenStart, toSubstitute));
		}
	}

	protected boolean isStartOfToken(final char character) {
		return tokenStart == character;
	}

	protected boolean isStartOfArguments(final char character) {
		return argsStart == character;
	}

	protected boolean isEndOfArguments(final char character) {
		return argsEnd == character;
	}

	protected boolean isEndOfToken(final char character) {
		return tokenEnd == character;
	}

	protected String[] extractArgs(final String tokenName) {
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
	protected void checkArgumentsAreValid(final String tokenName) {
		if (this.args.length() > 0 && this.args.length() % 2 == 0) {
			throw new IllegalStateException(String.format(
					"the given arguments '%s' for token '%s' seem to be incorrect!", this.args.toString(), tokenName));
		}
	}

	protected void checkArgumentLength(final String tokenName) {
		if (this.isArgsStarted && this.args.length() == 0) {
			throw new IllegalStateException(String.format("the given arguments for token '%s' were empty!", tokenName));
		}
	}

	protected boolean appendToArgs() {
		return isArgsStarted;
	}

	protected void reset(final boolean resetResult) {
		this.isTokenStart = false;
		this.isArgsStarted = false;
		this.token = new StringBuilder();
		this.args = new StringBuilder();
		if (resetResult) {
			this.result = new StringBuilder();
		}
	}

	protected String evalToken() {
		final String tokenName = this.token.toString();
		final String[] args = extractArgs(tokenName);
		if (!tokens.containsKey(tokenName)) {
			if (ignoreMissingValues) {
				return tokenWithPossibleArguments();
			} else {
				throw new IllegalStateException(String.format("no value or generator for token '%s' found!", tokenName));
			}
		}
		return getGeneratorValue(tokenName, args);
	}

	private String getGeneratorValue(final String tokenName, final String[] args) {
		String value = null;
		if (this.generatorCachingEnabled && this.generatorCache.containsKey(tokenName)) {
			return this.generatorCache.get(tokenName);
		}
		final Generator generator = tokens.get(tokenName).getGenerator();
		generator.inject(args);
		value = generator.generate();
		this.generatorCache.put(tokenName, value);
		return value;
	}

	private String tokenWithPossibleArguments() {
		if (this.args.length() > 0) {
			return this.tokenStart + this.token.toString() + this.argsStart + this.args + this.argsEnd + this.tokenEnd;
		} else {
			return this.tokenStart + this.token.toString() + this.tokenEnd;
		}
	}

	protected boolean appendToToken() {
		return isTokenStart && !isArgsStarted;
	}

	protected boolean appendToResult() {
		return !isTokenStart;
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

}
