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

/**
 * TODO java doc
 * 
 * @author msauer
 * @see Toky
 */
public class Token {

	private final String token;
	private Generator generator;
	private String fullToken;

	/**
	 * @param token
	 *            e.g. {amount} -> 'amount' would be the token, must not be null
	 *            or empty
	 */
	public Token(String token) {
		Validate.notEmpty(token);
		this.token = token;
	}

	/**
	 * @param value
	 *            the static value to use for the token. if you want to
	 *            dynamically generate a value (and possibly supply arguments)
	 *            then use {@link #replacedBy(Generator).}. must not be null
	 * @return the {@link #Token} to allow method chaining
	 */
	public Token replacedBy(final String value) {
		Validate.notNull(value);
		this.generator = new Generator() {

			@Override
			public String generate() {
				return value;
			}

			@Override
			public void inject(String[] args) {
				// no need here as we have a static value
			}
		};
		return this;
	}

	/**
	 * @return the {@link #Token}
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param generator
	 *            the {@link #Generator} to use when replacing the value. if you
	 *            only have a static value (something constant) than you can also use
	 *            {@link #replacedBy(String)}. must not be null
	 * @return the {@link #Token} to allow method chaining
	 */
	public Token replacedBy(Generator generator) {
		this.generator = generator;
		return this;
	}

	/**
	 * @return the {@link #Generator} associated with the {@link #Token}. can be null
	 */
	public Generator getGenerator() {
		return generator;
	}

	@Override
	public String toString() {
		return "Token [fullToken=" + fullToken + ", generator=" + generator + ", token=" + token + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
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
		return true;
	}
}
