# What's that for? 

Token Replacer is a simple and small Java Library that helps replacing tokens in strings.

You can replace tokens with simple static strings:

    String result = new Toky().register("number", "123").substitute("i can count to {number}");

or strings generated "on-the-fly": 

    String result = new Toky().register(new Token("number").replacedBy(new Generator() {
    
        @Override
        public void inject(String[] args) {
            // store the arguments
        }
        
        @Override
        public String generate() {
            return "123"; // some very sophisticated stuff happens here :)
        }
     })).substitute("i can count to {number}";

You can even pass arguments to the generator which makes it pretty powerful:

    String result = new Toky().register(new Token("number").replacedBy(new Generator() {
    
        @Override
        public void inject(String[] args) {
            // store the arguments
        }
        
        @Override
        public String generate() {
            return args[0] + args[1] + args[2]; // some very sophisticated stuff happens here :)
        }
     })).substitute("i can count to {number(1,2,3)}");
     
## Getting the Jar File

either via Maven

    <dependency>
        <groupId>de.marcelsauer</groupId>
        <artifactId>tokenreplacer</artifactId>
        <version>1.2</version>
    </dependency>

or just take the latest jar from the [downloads](http://github.com/niesfisch/tokenreplacer/downloads) section and put it in your classpath.

## Licence

Version >= 1.2 -> Apache 2.0 http://www.apache.org/licenses/LICENSE-2.0.txt

Version <= 1.1 -> GPL 3

## Release Notes

[Release Notes](http://github.com/niesfisch/tokenreplacer/blob/master/releasenotes.txt)
        
## Usage

<p>
simplest use case, only <b>static values</b>
</p>

<pre>
TokenReplacer toky = new Toky().register(&quot;number&quot;, &quot;123&quot;);
toky.substitute(&quot;i can count to {number}&quot;);
</pre>

<p>
is same as registering an <b>explicit {@link Token}</b>
</p>

<pre>
toky = new Toky().register(new Token(&quot;number&quot;).replacedBy(&quot;123&quot;));
toky.substitute(&quot;i can count to {number}&quot;);
</pre>

<p>
we can also use a <b>{@link Generator}</b> to <b>dynamically</b> get the
value (which here does not really make sense ;-)
</p>

<pre>
toky = new Toky().register(new Token(&quot;number&quot;).replacedBy(new Generator() {

 &#064;Override
 public void inject(String[] args) {
     // not relevant here
 }

 &#064;Override
 public String generate() {
     return &quot;123&quot;;
 }
}));
</pre>
<p>
here we use a generator and <b>pass the arguments</b> "a,b,c" to it, they
will be injected via {@link Generator#inject(String[] args)} before the call
to {@link Generator#generate()} is done. it is up to the generator to decide
what to do with them. this feature makes handling tokens pretty powerful
because you can write very dynamic generators.
</p>

<pre>
toky.substitute(&quot;i can count to {number(a,b,c)}&quot;);
</pre>

<p>
of course you can replace all default <b>delimiters</b> with your preferred
ones, just make sure start and end are different.
</p>

<pre>
toky.withTokenStart(&quot;*&quot;); // default is '{'
toky.withTokenEnd(&quot;#&quot;); // default is '}'
toky.withArgumentDelimiter(&quot;;&quot;); // default is ','
toky.withArgumentStart(&quot;[&quot;); // default is '('
toky.withArgumentEnd(&quot;]&quot;); // default is ')'
</pre>

<p>
by default Toky will throw IllegalStateExceptions if there was no matching
value or generator found for a token. you can <b>enable/disable generating
exceptions</b>.
</p>

<pre>
toky.doNotIgnoreMissingValues(); // which is the DEFAULT
</pre>

<p>
will turn error reporting for missing values <b>OFF</b>
</p>

<pre>
toky.ignoreMissingValues();
</pre>

<p>
you can <b>enable/disable generator caching</b>. if you enable caching once a
generator for a token returned a value this value will be used for all
subsequent tokens with the same name
</p>

<pre>
toky.enableGeneratorCaching();
toky.disableGeneratorCaching();
</pre>


## More Samples

Have a look at [the unit test of Toky](http://github.com/niesfisch/tokenreplacer/blob/master/src/test/java/de/marcelsauer/tokenreplacer/TokyTest.java) to see some more samples

## peeking into the source code and building from scratch

    $ git clone http://github.com/niesfisch/tokenreplacer.git tokenreplacer
    $ cd tokenreplacer
    $ mvn clean install
    
