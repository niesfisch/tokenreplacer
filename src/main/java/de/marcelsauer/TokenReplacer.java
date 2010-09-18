package de.marcelsauer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.marcelsauer.TokenExtractor.Match;

/**
 * @author msauer
 * 
 */
public class TokenReplacer {

    private Map<String, Generator> generators = new HashMap<String, Generator>();
    private TokenExtractor extractor;
    private Generator latestGenerator;
    private String tokenStart = Constants.DEFAULT_TOKEN_START;
    private String tokenEnd = Constants.DEFAULT_TOKEN_END;

    public TokenReplacer(TokenExtractor extractor) {
        this.extractor = extractor;
    }

    public TokenReplacer() {
        this.extractor = new DefaultTokenExtractor();
    }

    public String substitute(String toSubstitute) {
        Map<String, Match> parts = extractor.extract(toSubstitute);

        for (String token : parts.keySet()) {
            Match match = parts.get(token);
            Generator generator = generators.get(match.tokenWithoutAmount);
            check(generator, token);
            String replacement = getReplacement(match, generator);
            String quotedPattern = Pattern.quote(tokenStart + token + tokenEnd);
            toSubstitute = toSubstitute.replaceAll(quotedPattern, replacement);
        }
        return toSubstitute;
    }

    private String getReplacement(Match match, Generator generator) {
        String value = generator.generate();

        StringBuffer replacement = new StringBuffer("");
        if (match.amount > 1) {
            for (int i = 0; i < match.amount; i++) {
                replacement.append(value);
            }
        } else {
            replacement.append(value);
        }
        return replacement.toString();
    }

    private void check(Generator generator, String token) {
        if (generator == null) {
            throw new IllegalStateException(String.format("no generator for key '%s' found", token));
        }
    }

    public TokenReplacer register(Generator generator) {
        this.latestGenerator = generator;
        return this;
    }

    public TokenReplacer forToken(String token) {
        this.generators.put(token, latestGenerator);
        return this;
    }

    public TokenReplacer withTokenStart(String tokenStart) {
        this.tokenStart = tokenStart;
        this.extractor.withTokenStart(tokenStart);
        return this;
    }

    public TokenReplacer withTokenEnd(String tokenEnd) {
        this.tokenEnd = tokenEnd;
        this.extractor.withTokenEnd(tokenEnd);
        return this;
    }

    public TokenReplacer withAmountStart(String amountStart) {
        this.extractor.withAmountStart(amountStart);
        return this;
    }
    
    public TokenReplacer withAmountEnd(String amountEnd) {
        this.extractor.withAmountEnd(amountEnd);
        return this;
    }
    
}
