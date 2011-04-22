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

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Before;
import org.junit.Test;

/**
 * @author msauer
 */
public class FileReadingTokyTest {

	private TokenReplacer toky;

	@Before
	public void setUp() {
		this.toky = new Toky();
		this.toky.register("token1", "1");
		this.toky.register("token2", "2");
		this.toky.register("token3", "3");
		this.toky.register("token4", "4");
		this.toky.register("token5", new Generator() {

			private String[] args;

			@Override
			public void inject(String[] args) {
				this.args = args;
			}

			@Override
			public String generate() {
				StringBuilder result = new StringBuilder();
				for (String s : args) {
					result.append(s);
				}
				return result.toString();
			}
		});
	}

	@Test
	public void thatFileContentIsReplacedCorrectly() {
		InputStream input = getClass().getClassLoader().getResourceAsStream("sampleInput_en.txt");
		InputStream expected = getClass().getClassLoader().getResourceAsStream("sampleExpectedOutput_en.txt");
		String contentToReplace = getContents(input);
		String expectedResult = getContents(expected);
		assertEquals(expectedResult, toky.substitute(contentToReplace));
	}

	private String getContents(InputStream in) {
		StringBuilder contents = new StringBuilder();
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(in));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return contents.toString();
	}

}
