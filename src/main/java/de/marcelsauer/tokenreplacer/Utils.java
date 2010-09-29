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
 */
final class Utils {
	static String join(String[] s, String delimiter) {
		StringBuilder buffer = new StringBuilder();
		for (String part : s) {
			buffer.append(part);
			buffer.append(delimiter);
		}
		return buffer.toString().replaceFirst(",$", "");
	}

}
