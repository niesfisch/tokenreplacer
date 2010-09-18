package de.marcelsauer.tokenreplacer;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.Validate;

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
public class DefaultTokenExtractor implements TokenExtractor {

	private String tokenStart = Constants.DEFAULT_TOKEN_START;
	private String tokenEnd = Constants.DEFAULT_TOKEN_END;
	private String amountStart = Constants.DEFAULT_AMOUNT_START;
	private String amountEnd = Constants.DEFAULT_AMOUNT_END;

	@Override
	public Map<String, Match> extract(String input) {
		Validate.notEmpty(input);
		final Map<String, Match> matches = new HashMap<String, Match>();
		checkState();
		final StringTokenizer st = new StringTokenizer(input, tokenStart);
		while (st.hasMoreTokens()) {
			final String nextToken = st.nextToken();
			if (nextToken.contains(tokenEnd)) {
				final StringTokenizer en = new StringTokenizer(nextToken, tokenEnd);
				while (en.hasMoreTokens()) {
					final String match = en.nextToken();
					if (nextToken.startsWith(match)) {
						if ("".equals(match.trim())) {
							throw new IllegalArgumentException("empty tokens are not supported, error string was: "
									+ input);
						}
						reportMatch(matches, match);
					}
				}
			}
		}

		return matches;
	}

	protected void checkState() {
		if (tokenStart == null || "".equals(tokenStart.trim())) {
			throw new IllegalStateException("token start pattern must not be null or empty");
		}
		if (tokenEnd == null || "".equals(tokenEnd.trim())) {
			throw new IllegalStateException("token end pattern must not be null or empty");
		}
		if (amountStart == null || "".equals(amountStart.trim())) {
			throw new IllegalStateException("amount start pattern must not be null or empty");
		}
		if (amountEnd == null || "".equals(amountEnd.trim())) {
			throw new IllegalStateException("amount end pattern must not be null or empty");
		}
	}

	/**
	 * @param match
	 * @return
	 * @todo seems like duplicated logic here, refactor
	 */
	private Match extractMatch(String match) {
		String tokenWithoutAmount = "";
		int amount = 1;
		final StringTokenizer st = new StringTokenizer(match, amountStart);
		while (st.hasMoreTokens()) {
			final String nextToken = st.nextToken();
			if (nextToken.contains(amountEnd)) {
				final StringTokenizer en = new StringTokenizer(nextToken, amountEnd);
				while (en.hasMoreTokens()) {
					final String amountMatch = en.nextToken();
					if (amountMatch != null && !"".equals(amountMatch.trim())) {
						amount = Integer.valueOf(amountMatch);
					}
				}
			} else {
				tokenWithoutAmount = nextToken;
			}
		}
		return new Match(match, tokenWithoutAmount, amount);
	}

	protected void reportMatch(Map<String, Match> matches, String match) {
		final Match extractedMatch = extractMatch(match);
		if (matches.containsKey(extractedMatch.match)) {
			matches.put(extractedMatch.match, extractedMatch);
		} else {
			matches.put(extractedMatch.match, extractedMatch);
		}
	}

	@Override
	public TokenExtractor withTokenEnd(String tokenEnd) {
		Validate.notEmpty(tokenEnd);
		this.tokenEnd = tokenEnd;
		return this;
	}

	@Override
	public TokenExtractor withTokenStart(String tokenStart) {
		Validate.notEmpty(tokenStart);
		this.tokenStart = tokenStart;
		return this;
	}

	@Override
	public TokenExtractor withAmountStart(String amountStart) {
		Validate.notEmpty(amountStart);
		this.amountStart = amountStart;
		return this;
	}

	@Override
	public TokenExtractor withAmountEnd(String amountEnd) {
		Validate.notEmpty(amountEnd);
		this.amountEnd = amountEnd;
		return this;
	}
}
