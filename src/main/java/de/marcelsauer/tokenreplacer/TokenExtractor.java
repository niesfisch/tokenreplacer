package de.marcelsauer.tokenreplacer;

import java.util.Set;

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
interface TokenExtractor {

	Set<Token> extract(String input);

	void register(Token token);

	// class Match {
	//
	// final Token token;
	// final String[] args;
	// final String fullToken;
	//
	// Match(Token token, String[] args, String fullToken) {
	// Validate.notNull(token);
	// Validate.notNull(args);
	// Validate.notEmpty(fullToken);
	// this.token = token;
	// this.args = args;
	// this.fullToken = fullToken;
	// }
	//
	// @Override
	// public String toString() {
	// return "Match [args=" + Arrays.toString(args) + ", fullToken=" +
	// fullToken + ", token=" + token + "]";
	// }
	//
	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;
	// result = prime * result + Arrays.hashCode(args);
	// result = prime * result + ((token == null) ? 0 : token.hashCode());
	// return result;
	// }
	//
	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj)
	// return true;
	// if (obj == null)
	// return false;
	// if (getClass() != obj.getClass())
	// return false;
	// Match other = (Match) obj;
	// if (!Arrays.equals(args, other.args))
	// return false;
	// if (token == null) {
	// if (other.token != null)
	// return false;
	// } else if (!token.equals(other.token))
	// return false;
	// return true;
	// }
	// }

}
