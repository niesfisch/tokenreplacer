package de.marcelsauer.tokenreplacer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

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
 * 
 * THIS CLASS IS NOT PART OF THE PUBLIC API!
 */
class DefaultTokenExtractor implements TokenExtractor {

	private Set<Token> tokens = new HashSet<Token>();

	@Override
	public Set<Token> extract(final String input) {
		Validate.notEmpty(input);
		final Set<Token> matches = new HashSet<Token>();

		for (Token token : tokens) {
			final StringTokenizer st = new StringTokenizer(input, token.getTokenStart());
			while (st.hasMoreTokens()) {
				final String nextToken = st.nextToken();
				if (!nextToken.contains(token.getToken())) {
					continue;
				}
				if (nextToken.contains(token.getTokenEnd())) {
					final StringTokenizer en = new StringTokenizer(nextToken, token.getTokenEnd());
					while (en.hasMoreTokens()) {
						final String match = en.nextToken();
						if (nextToken.startsWith(match)) {
							if ("".equals(match.trim())) {
								throw new IllegalArgumentException("empty tokens are not supported, error string was: "
										+ input);
							}
							reportMatch(matches, match, token);
						}
					}
				}
			}
		}

		return matches;
	}

	/**
	 * @todo seems like duplicated logic here, refactor
	 */
	private Token extractToken(final String match, Token token) {
		String[] args = new String[] {};
		final StringTokenizer st = new StringTokenizer(match, Constants.DEFAULT_ARGS_START);
		while (st.hasMoreTokens()) {
			final String nextToken = st.nextToken();
			if (nextToken.contains(Constants.DEFAULT_ARGS_END)) {
				final StringTokenizer en = new StringTokenizer(nextToken, Constants.DEFAULT_ARGS_END);
				while (en.hasMoreTokens()) {
					final String argsMatch = en.nextToken();
					if (argsMatch != null && !"".equals(argsMatch.trim())) {
						args = extractArgsFrom(argsMatch);
					}
				}
			}
		}
		token.setArgs(args);
		return token;
	}

	private String[] extractArgsFrom(String args) {
		List<String> result = new ArrayList<String>();
		final StringTokenizer en = new StringTokenizer(args, Constants.DEFAULT_ARGS_SEPARATOR);
		while (en.hasMoreTokens()) {
			result.add(en.nextToken());

		}
		return result.toArray(new String[] {});
	}

	protected void reportMatch(final Set<Token> matches, final String match, Token token) {
		matches.add(extractToken(match, token));
	}

	@Override
	public void register(Token token) {
		this.tokens.add(token);
	}

}
