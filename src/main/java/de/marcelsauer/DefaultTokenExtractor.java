package de.marcelsauer;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author msauer
 * 
 */
public class DefaultTokenExtractor implements TokenExtractor {

    private String tokenStart = Constants.DEFAULT_TOKEN_START;
    private String tokenEnd = Constants.DEFAULT_TOKEN_END;
    private String amountStart = Constants.DEFAULT_AMOUNT_START;
    private String amountEnd = Constants.DEFAULT_AMOUNT_END;

    @Override
    public Map<String, Match> extract(String input) {
        Map<String, Match> matches = new HashMap<String, Match>();
        checkState();
        StringTokenizer st = new StringTokenizer(input, tokenStart);
        while (st.hasMoreTokens()) {
            String nextToken = st.nextToken();
            if (nextToken.contains(tokenEnd)) {
                StringTokenizer en = new StringTokenizer(nextToken, tokenEnd);
                while (en.hasMoreTokens()) {
                    String match = en.nextToken();
                    if (nextToken.startsWith(match)) {
                        if ("".equals(match.trim())) {
                            throw new IllegalArgumentException("empty tokens are not supported, error string was: "
                                    + input);
                        }
                        reportMatch(matches, match);
                    }
                }
            }
        }

        return matches;
    }

    protected void checkState() {
        if (tokenStart == null || "".equals(tokenStart.trim())) {
            throw new IllegalStateException("token start pattern must not be null or empty");
        }
        if (tokenEnd == null || "".equals(tokenEnd.trim())) {
            throw new IllegalStateException("token end pattern must not be null or empty");
        }
        if (amountStart == null || "".equals(amountStart.trim())) {
            throw new IllegalStateException("amount start pattern must not be null or empty");
        }
        if (amountEnd == null || "".equals(amountEnd.trim())) {
            throw new IllegalStateException("amount end pattern must not be null or empty");
        }
    }

    /**
     * @param match
     * @return
     * @todo seems like duplicated logic here, refactor
     */
    private Match extractMatch(String match) {
        String tokenWithoutAmount = "";
        int amount = 1;
        StringTokenizer st = new StringTokenizer(match, amountStart);
        while (st.hasMoreTokens()) {
            String nextToken = st.nextToken();
            if (nextToken.contains(amountEnd)) {
                StringTokenizer en = new StringTokenizer(nextToken, amountEnd);
                while (en.hasMoreTokens()) {
                    String amountMatch = en.nextToken();
                    if (amountMatch != null && !"".equals(amountMatch.trim())) {
                        amount = Integer.valueOf(amountMatch);
                    }
                }
            } else {
                tokenWithoutAmount = nextToken;
            }
        }
        return new Match(match, tokenWithoutAmount, amount);
    }

    protected void reportMatch(Map<String, Match> matches, String match) {
        Match extractedMatch = extractMatch(match);
        if (matches.containsKey(extractedMatch.match)) {
            matches.put(extractedMatch.match, extractedMatch);
        } else {
            matches.put(extractedMatch.match, extractedMatch);
        }
    }

    @Override
    public TokenExtractor withTokenEnd(String keyEnd) {
        this.tokenEnd = keyEnd;
        return this;
    }

    @Override
    public TokenExtractor withTokenStart(String keyStart) {
        this.tokenStart = keyStart;
        return this;
    }

    @Override
    public TokenExtractor withAmountStart(String amountStart) {
        this.amountStart = amountStart;
        return this;
    }

    @Override
    public TokenExtractor withAmountEnd(String amountEnd) {
        this.amountEnd = amountEnd;
        return this;
    }
}
