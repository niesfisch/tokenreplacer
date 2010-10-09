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

/**
 * <p>
 * Toky is a token replacer for Strings. It will replace the found token with a
 * provided static value or a dynamically generated value created by a
 * {@link Generator}. Toky itself <b>IS NOT THREAD SAFE</b>. so handling Toky in
 * a multi threaded environment should be synchronized by the client.
 * </p>
 * 
 * <p>
 * simplest use case, only <b>static values</b>
 * </p>
 * 
 * <pre>
 * TokenReplacer toky = new Toky().register(&quot;number&quot;, &quot;123&quot;);
 * toky.substitute(&quot;i can count to {number}&quot;);
 * </pre>
 * 
 * <p>
 * is same as registering an <b>explicit {@link Token}</b>
 * </p>
 * 
 * <pre>
 * toky = new Toky().register(new Token(&quot;number&quot;).replacedBy(&quot;123&quot;));
 * toky.substitute(&quot;i can count to {number}&quot;);
 * </pre>
 * 
 * <p>
 * we can also use a <b>{@link Generator}</b> to <b>dynamically</b> get the
 * value (which here does not really make sense ;-)
 * </p>
 * 
 * <pre>
 * toky = new Toky().register(new Token(&quot;number&quot;).replacedBy(new Generator() {
 * 
 * 	&#064;Override
 * 	public void inject(String[] args) {
 * 		// not relevant here
 * 	}
 * 
 * 	&#064;Override
 * 	public String generate() {
 * 		return &quot;123&quot;;
 * 	}
 * }));
 * </pre>
 * <p>
 * here we use a generator and <b>pass the arguments</b> "a,b,c" to it, they
 * will be injected via {@link Generator#inject(String[] args)} before the call
 * to {@link Generator#generate()} is done. it is up to the generator to decide
 * what to do with them. this feature makes handling tokens pretty powerful
 * because you can write very dynamic generators.
 * </p>
 * 
 * <pre>
 * toky.substitute(&quot;i can count to {number(a,b,c)}&quot;);
 * </pre>
 * 
 * if you prefer to use <b>index based tokens</b>, you can also use this:
 * 
 * <pre>
 * toky.register(new String[] { &quot;one&quot;, &quot;two&quot;, &quot;three&quot; });
 * toky.substitute(&quot;abc {0} {1} {2} def&quot;)); // will produce &quot;abc one two three def&quot;
 * </pre>
 * 
 * <p>
 * of course you can replace all default <b>delimiters</b> with your preferred
 * ones, just make sure start and end are different.
 * </p>
 * 
 * <pre>
 * toky.withTokenStart(&quot;*&quot;); // default is '{'
 * toky.withTokenEnd(&quot;#&quot;); // default is '}'
 * toky.withArgumentDelimiter(&quot;;&quot;); // default is ','
 * toky.withArgumentStart(&quot;[&quot;); // default is '('
 * toky.withArgumentEnd(&quot;]&quot;); // default is ')'
 * </pre>
 * 
 * <p>
 * by default Toky will throw IllegalStateExceptions if there was no matching
 * value or generator found for a token. you can <b>enable/disable generating
 * exceptions</b>.
 * </p>
 * 
 * <pre>
 * toky.doNotIgnoreMissingValues(); // which is the DEFAULT
 * </pre>
 * 
 * <p>
 * will turn error reporting for missing values <b>OFF</b>
 * </p>
 * 
 * <pre>
 * toky.ignoreMissingValues();
 * </pre>
 * 
 * <p>
 * you can <b>enable/disable generator caching</b>. if you enable caching once a
 * generator for a token returned a value this value will be used for all
 * subsequent tokens with the same name otherwise the generator will be called
 * once for every token. <br/>
 * <br/>
 * 
 * e.g. {counter}{counter}{counter}<br/>
 * <br/>
 * 
 * with a registered generator will result in 3 calls to the generator
 * (resulting in poorer performance). so, if you know your generator will always
 * return the same value enable caching.
 * </p>
 * 
 * <pre>
 * toky.enableGeneratorCaching();
 * toky.disableGeneratorCaching();
 * </pre>
 * 
 * @author msauer
 */
public class Toky implements TokenReplacer {

	/**
	 * the actual underlaying implementation that will be used. allows us to
	 * replace it without changing clients working with {@link Toky} solely
	 * based on the {@link TokenReplacer} API.
	 */
	private final TokenReplacer impl;

	/**
	 * allows clients of {@link Toky} to provided their own implementation of
	 * the {@link TokenReplacer} that will be called. use this if you know what
	 * you are doing :-)
	 * 
	 * otherwise always use the normal {@link #Toky()} constructor that will use
	 * the correct implementation!
	 * 
	 * @param impl
	 */
	public Toky(TokenReplacer impl) {
		this.impl = impl;
	}

	/**
	 * constructs a new {@link TokenReplacer}.
	 */
	public Toky() {
		this.impl = new CharSequenceTokenReplacer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#register(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public TokenReplacer register(String token, String value) {
		return impl.register(token, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.marcelsauer.tokenreplacer.TokenReplacer#register(de.marcelsauer.
	 * tokenreplacer.Token)
	 */
	@Override
	public TokenReplacer register(Token token) {
		return impl.register(token);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#substitute(java.lang.String)
	 */
	@Override
	public String substitute(String toSubstitute) {
		return impl.substitute(toSubstitute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#withArgumentDelimiter(java
	 * .lang.String)
	 */
	@Override
	public TokenReplacer withArgumentDelimiter(String argsSep) {
		return impl.withArgumentDelimiter(argsSep);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#withArgumentEnd(java.lang.
	 * String)
	 */
	@Override
	public TokenReplacer withArgumentEnd(String argsEnd) {
		return impl.withArgumentEnd(argsEnd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#withArgumentStart(java.lang
	 * .String)
	 */
	@Override
	public TokenReplacer withArgumentStart(String argsStart) {
		return impl.withArgumentStart(argsStart);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#withTokenEnd(java.lang.String)
	 */
	@Override
	public TokenReplacer withTokenEnd(String tokenEnd) {
		return impl.withTokenEnd(tokenEnd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#withTokenStart(java.lang.String
	 * )
	 */
	@Override
	public TokenReplacer withTokenStart(String tokenStart) {
		return impl.withTokenStart(tokenStart);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#register(java.lang.String,
	 * de.marcelsauer.tokenreplacer.Generator)
	 */
	@Override
	public TokenReplacer register(String token, Generator Generator) {
		return impl.register(token, Generator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.marcelsauer.tokenreplacer.TokenReplacer#doNotIgnoreMissingValues()
	 */
	@Override
	public TokenReplacer doNotIgnoreMissingValues() {
		return impl.doNotIgnoreMissingValues();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#ignoreMissingValues()
	 */
	@Override
	public TokenReplacer ignoreMissingValues() {
		return this.impl.ignoreMissingValues();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#enableGeneratorCaching()
	 */
	@Override
	public TokenReplacer enableGeneratorCaching() {
		return impl.enableGeneratorCaching();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#disableGeneratorCaching()
	 */
	@Override
	public TokenReplacer disableGeneratorCaching() {
		return impl.disableGeneratorCaching();
	}

	/* (non-Javadoc)
	 * @see de.marcelsauer.tokenreplacer.TokenReplacer#register(java.lang.String[])
	 */
	@Override
	public TokenReplacer register(String[] replacements) {
		return impl.register(replacements);
	}

}
