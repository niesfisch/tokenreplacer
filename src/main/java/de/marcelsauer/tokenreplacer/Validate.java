/**
 * Copyright (C) 2010 the original author or authors.
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
 * @author msauer
 * 
 */
final class Validate {
	static void notEmpty(final String string, final String message) {
		if (string == null || string.isEmpty()) {
			throw new IllegalArgumentException(message);
		}
	}

	static void notEmpty(final String string) {
		notEmpty(string, "the provided string is empty!");
	}

	static void notNull(final Object o) {
		notNull(o, "the object was null!");
	}

	static void isTrue(boolean test) {
		if (!test) {
			throw new IllegalArgumentException("given condition was not true!");
		}
	}

	static void notNull(Object o, String message) {
		if (o == null) {
			throw new IllegalArgumentException(message);
		}
	}

}
