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
 * @author msauer
 * @see Toky
 */
public interface Generator {

	/**
	 * @return the generated String that will be used to replace a {@link Token}
	 */
	String generate();

	/**
	 * @param args
	 *            to inject into the Generator. will be called before the call
	 *            to {@link #generate()}. will never be null but can be of size
	 *            0!
	 */
	void inject(String[] args);

}
