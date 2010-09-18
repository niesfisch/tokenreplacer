package de.marcelsauer.tokenreplacer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;

import de.marcelsauer.tokenreplacer.TokenExtractor.Match;

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
public class TokenReplacer {

	private Map<String, Generator> generators = new HashMap<String, Generator>();
	private TokenExtractor extractor;
	private String tokenStart = Constants.DEFAULT_TOKEN_START;
	private String tokenEnd = Constants.DEFAULT_TOKEN_END;

	/**
	 * use this constructor if you want to overwrite the extraction logic with
	 * your own implementation. normally not necessary. use
	 * {@link #TokenReplacer()} instead
	 * 
	 * @param extractor
	 */
	public TokenReplacer(TokenExtractor extractor) {
		Validate.notNull(extractor);
		this.extractor = extractor;
	}

	/**
	 * constructs a new {@link #TokenReplacer()}
	 */
	public TokenReplacer() {
		this.extractor = new DefaultTokenExtractor();
	}

	/**
	 * @param toSubstitute
	 *            the string containing the tokens that need to be replaced <br/>
	 *            e.g. lorem ipsum ${date[2]} dadidada ${name}
	 * @return the string in which all tokens were replaced <br/>
	 *         e.g. lorem ipsum 2010-12-12 dadidada marcel sauer
	 */
	public String substitute(String toSubstitute) {
		Validate.notEmpty(toSubstitute);
		final Map<String, Match> parts = extractor.extract(toSubstitute);

		for (String token : parts.keySet()) {
			final Match match = parts.get(token);
			final Generator generator = generators.get(match.tokenWithoutAmount);
			check(generator, token);
			final String replacement = getReplacement(match, generator);
			final String quotedPattern = Pattern.quote(tokenStart + token + tokenEnd);
			toSubstitute = toSubstitute.replaceAll(quotedPattern, replacement);
		}
		return toSubstitute;
	}

	private String getReplacement(Match match, Generator generator) {
		final String value = generator.generate();

		final StringBuffer replacement = new StringBuffer("");
		if (match.amount > 1) {
			for (int i = 0; i < match.amount; i++) {
				replacement.append(value);
			}
		} else {
			replacement.append(value);
		}
		return replacement.toString();
	}

	private void check(Generator generator, String token) {
		if (generator == null) {
			throw new IllegalStateException(String.format("no generator for key '%s' found", token));
		}
	}

	/**
	 * @param generator
	 *            to use for a specific token
	 * @return
	 */
	public TokenReplacer register(Generator generator) {
		Validate.notNull(generator);
		this.generators.put(generator.forToken(), generator);
		return this;
	}

	/**
	 * @param tokenStart
	 *            the start to identify a token e.g. ${date} -> ${ would be the
	 *            start token
	 * @return the {@link TokenReplacer} to allow method chaining
	 */
	public TokenReplacer withTokenStart(String tokenStart) {
		Validate.notEmpty(tokenStart);
		this.tokenStart = tokenStart;
		this.extractor.withTokenStart(tokenStart);
		return this;
	}

	/**
	 * @param tokenEnd
	 *            the end to identify a token e.g. ${date} -> } would be the end
	 *            token
	 * @return the {@link TokenReplacer} to allow method chaining
	 */
	public TokenReplacer withTokenEnd(String tokenEnd) {
		Validate.notEmpty(tokenEnd);
		this.tokenEnd = tokenEnd;
		this.extractor.withTokenEnd(tokenEnd);
		return this;
	}

	/**
	 * @param amountStart
	 *            the start to identify the amount part of the token e.g.
	 *            ${date[2]} -> [ would be the start
	 * @return the {@link TokenReplacer} to allow method chaining
	 */
	public TokenReplacer withAmountStart(String amountStart) {
		Validate.notEmpty(amountStart);
		this.extractor.withAmountStart(amountStart);
		return this;
	}

	/**
	 * @param amountEnd
	 *            the end to identify the amount part of the token e.g.
	 *            ${date[2]} -> ] would be the start
	 * @return the {@link TokenReplacer} to allow method chaining
	 */
	public TokenReplacer withAmountEnd(String amountEnd) {
		Validate.notEmpty(amountEnd);
		this.extractor.withAmountEnd(amountEnd);
		return this;
	}

	/**
	 * registers a static value for a given token. if you need dynamic behaviour
	 * then use {@link #register(Generator)}
	 * 
	 * @param token
	 *            the name of the token to be replaced e.g. for ${date} ->
	 *            "date"
	 * @param value
	 *            the static value that will be used when replacing the token
	 * @return the {@link TokenReplacer} to allow method chaining
	 */
	public TokenReplacer register(final String token, final String value) {
		this.register(new Generator() {

			@Override
			public String generate() {
				return value;
			}

			@Override
			public String forToken() {
				return token;
			}
		});
		return this;
	}

}
