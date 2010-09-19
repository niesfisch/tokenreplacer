package de.marcelsauer.tokenreplacer;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

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

	private final Set<Token> tokens = new HashSet<Token>();

	private TokenExtractor extractor;

	/**
	 * use this constructor if you want to overwrite the extraction logic with
	 * your own implementation. normally not necessary. use
	 * {@link #TokenReplacer()} instead
	 * 
	 * @param extractor
	 */
	public TokenReplacer(final TokenExtractor extractor) {
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
		checkState();
		Set<Token> tokens = this.extractor.extract(toSubstitute);
		for (Token token : tokens) {
			final Generator generator = token.getGenerator();
			check(generator, token);
			generator.inject(token.getArgs());
			final String replacement = generator.generate();
			final String quotedPattern = Pattern.quote(token.getFullToken());
			toSubstitute = toSubstitute.replaceAll(quotedPattern, replacement);
		}
		return toSubstitute;
	}

	private void checkState() {
		Validate.notNull(this.extractor);
		if (this.tokens.size() == 0) {
			throw new IllegalStateException("please define at least one value or generator for a given token");
		}
	}

	private void check(final Generator generator, final Token token) {
		if (generator == null) {
			throw new IllegalStateException(String.format("no generator for key '%s' found", token.getToken()));
		}
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
		Validate.notEmpty(token);
		Validate.notNull(value);
		this.register(new Token(token).replacedBy(value));
		return this;
	}

	public TokenReplacer register(final Token token) {
		Validate.notNull(token);
		Validate.notNull(token.getGenerator(), "please specifiy a value or a generator for the token!");
		this.tokens.add(token);
		this.extractor.register(token);
		return this;
	}
}
