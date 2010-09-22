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


public class CharSequenceTokenReplacer implements TokenReplacer {

    /**
     * token stuff
     */
    private char tokenStart = Constants.DEFAULT_TOKEN_START;
    private char tokenEnd = Constants.DEFAULT_TOKEN_END;

    /**
     * argument stuff
     */
    private char argsStart = Constants.DEFAULT_ARGS_START;
    private char argsEnd = Constants.DEFAULT_ARGS_END;
    private char argsSep = Constants.DEFAULT_ARGS_SEPARATOR;

    /**
     * token state
     */
    private boolean isTokenStart;
    private StringBuffer token = new StringBuffer();

    /**
     * argument state
     */
    private boolean isArgsStarted;
    private StringBuffer args = new StringBuffer();

    /**
     * the result that will be returned
     */
    private StringBuffer result = new StringBuffer();

    protected final Map<String, Token> tokens = new HashMap<String, Token>();

    @Override
    public String substitute(final String toSubstitute) {

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

    private void checkEndOfArgumentsState(final String toSubstitute) {
        if (!isArgsStarted) {
            throw new IllegalStateException(String.format("missing start '%s' for argument in string '%s'!",
                    this.argsStart, toSubstitute));
        }

    }

    private void checkState(final String toSubstitute) {
        if (isTokenStart) {
            throw new IllegalStateException(String.format("missing  end '%s' for token in string '%s'!", this.tokenEnd,
                    toSubstitute));
        }
    }

    private void checkEndOfTokenState(final String toSubstitute) {
        if (!isTokenStart) {
            throw new IllegalStateException(String.format("missing start '%s' for token in string '%s'!",
                    this.tokenStart, toSubstitute));
        }
    }

    private boolean isStartOfToken(final char character) {
        return tokenStart == character;
    }

    private boolean isStartOfArguments(final char character) {
        return argsStart == character;
    }

    private boolean isEndOfArguments(final char character) {
        return argsEnd == character;
    }

    private boolean isEndOfToken(final char character) {
        return tokenEnd == character;
    }

    private String[] extractArgs(final String tokenName) {
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
    private void checkArgumentsAreValid(final String tokenName) {
        if (this.args.length() > 0 && this.args.length() % 2 == 0) {
            throw new IllegalStateException(String.format(
                    "the given arguments '%s' for token '%s' seem to be incorrect!", this.args.toString(), tokenName));
        }
    }

    private void checkArgumentLength(final String tokenName) {
        if (this.isArgsStarted && this.args.length() == 0) {
            throw new IllegalStateException(String.format("the given arguments for token '%s' were empty!", tokenName));
        }
    }

    private boolean appendToArgs() {
        return isArgsStarted;
    }

    private void reset(final boolean resetResult) {
        this.isTokenStart = false;
        this.isArgsStarted = false;
        this.token = new StringBuffer();
        this.args = new StringBuffer();
        if (resetResult) {
            this.result = new StringBuffer();
        }
    }

    private String evalToken() {
        String value = null;
        final String tokenName = token.toString();
        final String[] args = extractArgs(tokenName);
        if (!tokens.containsKey(tokenName)) {
            throw new IllegalStateException(String.format("no value or generator for token '%s' found!", tokenName));
        }
        final Generator generator = tokens.get(tokenName).getGenerator();
        generator.inject(args);
        value = generator.generate();
        return value;
    }

    private boolean appendToToken() {
        return isTokenStart && !isArgsStarted;
    }

    private boolean appendToResult() {
        return !isTokenStart;
    }

    @Override
    public TokenReplacer register(final String token, final String value) {
        Validate.notEmpty(token);
        Validate.notNull(value);
        this.register(new Token(token).replacedBy(value));
        return this;
    }

    @Override
    public TokenReplacer register(final Token token) {
        Validate.notNull(token);
        Validate.notNull(token.getGenerator(), "please specifiy a value or a generator for the token!");
        this.tokens.put(token.getToken(), token);
        return this;
    }

    @Override
    public TokenReplacer withTokenStart(String tokenStart) {
        ensureOneChar(tokenStart);
        this.tokenStart = tokenStart.charAt(0);
        return this;
    }

    @Override
    public TokenReplacer withTokenEnd(String tokenEnd) {
        ensureOneChar(tokenEnd);
        this.tokenEnd = tokenEnd.charAt(0);
        return this;
    }

    private void ensureOneChar(String character) {
        if (character.length() != 1) {
            throw new IllegalArgumentException(String.format("the given string '%s' must be exactly of size 1", character));
        }
    }

    @Override
    public TokenReplacer withArgumentDelimiter(String argsSep) {
        ensureOneChar(argsSep);
        this.argsSep = argsSep.charAt(0);
        return this;
    }

    @Override
    public TokenReplacer withArgumentStart(String argsStart) {
        ensureOneChar(argsStart);
        this.argsStart = argsStart.charAt(0);
        return this;
    }

    @Override
    public TokenReplacer withArgumentEnd(String argsEnd) {
        ensureOneChar(argsEnd);
        this.argsEnd = argsEnd.charAt(0);
        return this;
    }

}
