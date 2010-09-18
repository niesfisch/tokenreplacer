package de.marcelsauer.tokenreplacer;

import java.util.Map;

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
public interface TokenExtractor {

	/**
	 * @param input
	 *            input string to extract all possible tokens that need to be
	 *            replaced
	 * @return the map with key->value matches e.g. amount[2] -> Match
	 *         (amount[2], amount, 2)
	 */
	Map<String, Match> extract(String input);

	/**
	 * @param tokenStart
	 *            e.g. $(
	 * @return the token extractor to allow method chaining
	 */
	TokenExtractor withTokenStart(String tokenStart);

	/**
	 * @param tokenEnd
	 *            e.g. )
	 * @return the token extractor to allow method chaining
	 */
	TokenExtractor withTokenEnd(String tokenEnd);

	/**
	 * @param amountStart
	 *            e.g. [
	 * @return the token extractor to allow method chaining
	 */
	TokenExtractor withAmountStart(String amountStart);

	/**
	 * @param amountEnd
	 *            e.g. ]
	 * @return the token extractor to allow method chaining
	 */
	TokenExtractor withAmountEnd(String amountEnd);

	class Match {

		/**
		 * e.g. random[1]
		 */
		public final String match;

		/**
		 * e.g. random
		 */
		public final String tokenWithoutAmount;

		/**
		 * e.g. 1
		 */
		public final int amount;

		protected Match(String match, String tokenWithoutAmount, int amount) {
			this.match = match;
			this.tokenWithoutAmount = tokenWithoutAmount;
			this.amount = amount;
		}

		protected Match(String match, String tokenWithoutAmount) {
			this(match, tokenWithoutAmount, 1);
		}

		@Override
		public String toString() {
			return "Match [amount=" + amount + ", tokenWithoutAmount=" + tokenWithoutAmount + ", match=" + match + "]";
		}
	}

}
