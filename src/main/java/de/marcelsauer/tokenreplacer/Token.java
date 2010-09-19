package de.marcelsauer.tokenreplacer;

import java.util.Arrays;

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
public class Token {

	private final String token;
	private String tokenStart = Constants.DEFAULT_TOKEN_START;
	private String tokenEnd = Constants.DEFAULT_TOKEN_END;
	private Generator generator;
	private String[] args = new String[] {};
	private String fullToken;

	public Token(String token) {
		this.token = token;
		refreshFullToken();
	}

	public Token replacedBy(final String value) {
		this.generator = new Generator() {

			@Override
			public String generate() {
				return value;
			}

			@Override
			public void inject(String[] args) {
				// no need here
			}
		};
		return this;
	}

	public Token withTokenStart(String tokenStart) {
		this.tokenStart = tokenStart;
		refreshFullToken();
		return this;
	}

	public Token withTokenEnd(String tokenEnd) {
		this.tokenEnd = tokenEnd;
		refreshFullToken();
		return this;
	}

	public String getToken() {
		return token;
	}

	public String getTokenEnd() {
		return tokenEnd;
	}

	public String getTokenStart() {
		return tokenStart;
	}

	public Token replacedBy(Generator generator) {
		this.generator = generator;
		return this;
	}

	public Generator getGenerator() {
		return generator;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
		refreshFullToken(args);
	}

	private void refreshFullToken(String[] args) {
		if (this.args.length > 0) {
			this.fullToken = this.tokenStart + this.token + Constants.DEFAULT_ARGS_START
					+ Utils.join(args, Constants.DEFAULT_ARGS_SEPARATOR) + Constants.DEFAULT_ARGS_END + this.tokenEnd;
		} else {
			this.fullToken = this.tokenStart + this.token + this.tokenEnd;
		}
	}

	private void refreshFullToken() {
		this.refreshFullToken(this.args);
	}

	@Override
	public String toString() {
		return "Token [args=" + Arrays.toString(args) + ", generator=" + generator + ", token=" + token + ", tokenEnd="
				+ tokenEnd + ", tokenStart=" + tokenStart + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		result = prime * result + ((tokenEnd == null) ? 0 : tokenEnd.hashCode());
		result = prime * result + ((tokenStart == null) ? 0 : tokenStart.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token other = (Token) obj;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		if (tokenEnd == null) {
			if (other.tokenEnd != null)
				return false;
		} else if (!tokenEnd.equals(other.tokenEnd))
			return false;
		if (tokenStart == null) {
			if (other.tokenStart != null)
				return false;
		} else if (!tokenStart.equals(other.tokenStart))
			return false;
		return true;
	}

	public String getFullToken() {
		return this.fullToken;
	}
}
