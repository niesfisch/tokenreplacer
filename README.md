 [<img src="https://api.travis-ci.org/niesfisch/tokenreplacer.png"/>](http://travis-ci.org/niesfisch/tokenreplacer/builds)

# What's that for? 

Token Replacer is a simple and small Java Library that helps replacing tokens in strings.

You can replace tokens with **simple static strings**:
```Java
String toReplace = "i can count to {number}";
String result = new Toky().register("number", "123").substitute(toReplace);
System.out.println(result); // i can count to 123
```
or strings generated **"on-the-fly"**: 
```Java
String toReplace = "i can count to {number}";
String result = new Toky().register(new Token("number").replacedBy(new Generator() {
    
	@Override
	public void inject(String[] args) {
	    // store the arguments
	}

	@Override
	public String generate() {
	    return "123"; // some very sophisticated stuff happens here :), we just return 123 to keep it simple
	}
})).substitute(toReplace);
System.out.println(result); // i can count to 123
```
You can even **pass arguments** to the generator which makes it pretty powerful:
```Java
String toReplace = "i can count to {number(1,2,3)}";
String result = new Toky().register(new Token("number").replacedBy(new Generator() {
    
	@Override
	public void inject(String[] args) {
	    // store the arguments
	}

	@Override
	public String generate() {
	    return args[0] + args[1] + args[2]; // some very sophisticated stuff happens here :)
	}
})).substitute(toReplace);
System.out.println(result); // i can count to 123
```
If you prefer to use **index based tokens**, you can also use this:
```Java
toky.register(new String[] { "one", "two", "three" });
toky.substitute("abc {0} {1} {2} def"); // will produce "abc one two three def";
```

## Getting the Jar File

via Maven:
```
<dependency>
    <groupId>de.marcelsauer</groupId>
    <artifactId>tokenreplacer</artifactId>
    <version>1.3.2</version>
</dependency>
```
or just take the latest "tokenreplacer-x.y.jar" from the [downloads](http://github.com/niesfisch/tokenreplacer/downloads) section and put it in your classpath.
If you also need the sources and javadoc download the "tokenreplacer-x.y-sources.jar" / "tokenreplacer-x.y-javadoc.jar".

## Licence

Version >= 1.2 -> Apache 2.0 http://www.apache.org/licenses/LICENSE-2.0.txt

Version <= 1.1 -> GPL 3

## Release Notes

[Release Notes](http://github.com/niesfisch/tokenreplacer/blob/master/releasenotes.txt)
        
## Usage

simplest use case, only **static values**

```Java
TokenReplacer toky = new Toky().register("number", "123");
toky.substitute("i can count to {number}");
```

is same as registering an **explicit {@link Token}**

```Java
toky = new Toky().register(new Token("number").replacedBy("123"));
toky.substitute("i can count to {number}");
```

we can also use a **{@link Generator}** to **dynamically** get the
value (which here does not really make sense ;-))

```Java
toky = new Toky().register(new Token("number").replacedBy(new Generator() {

	 @Override
	 public void inject(String[] args) {
	     // not relevant here
	 }

	 @Override
	 public String generate() {
	     return "123";
	 }
}));
```

here we use a generator and **pass the arguments** "a,b,c" to it, they
will be injected via {@link Generator#inject(String[] args)} before the call
to {@link Generator#generate()} is done. it is up to the generator to decide
what to do with them. this feature makes handling tokens pretty powerful
because you can write very dynamic generators.

```Java
toky.substitute("i can count to {number(a,b,c)}");
```

if you prefer to use **index based tokens**, you can also use this:
 
```Java
toky.register(new String[] { "one", "two", "three" });
toky.substitute("abc {0} {1} {2} def"); // will produce "abc one two three def";
```

of course you can replace all default **delimiters** with your preferred
ones, just make sure start and end are different.

```Java
toky.withTokenStart("*"); // default is '{'
toky.withTokenEnd("#"); // default is '}'
toky.withArgumentDelimiter(";"); // default is ','
toky.withArgumentStart("["); // default is '('
toky.withArgumentEnd("]"); // default is ')'
```

by default Toky will throw IllegalStateExceptions if there was no matching
value or generator found for a token. you can **enable/disable generating
exceptions**.

```Java
toky.doNotIgnoreMissingValues(); // which is the DEFAULT
```

will turn error reporting for missing values <b>OFF</b>

```Java
toky.ignoreMissingValues();
```

you can **enable/disable generator caching**. if you enable caching once a
generator for a token returned a value this value will be used for all
subsequent tokens with the same name

```Java
toky.enableGeneratorCaching();
toky.disableGeneratorCaching();
```


## More Samples

Have a look at [the unit test of Toky](http://github.com/niesfisch/tokenreplacer/blob/master/src/test/java/de/marcelsauer/tokenreplacer/TokyTest.java) to see some more samples

## peeking into the source code and building from scratch

    $ git clone http://github.com/niesfisch/tokenreplacer.git tokenreplacer
    $ cd tokenreplacer
    $ mvn clean install

