package de.marcelsauer;

import java.util.Map;

/**
 * @author msauer
 * 
 */
public interface TokenExtractor {

    Map<String, Match> extract(String input);

    TokenExtractor withTokenStart(String tokenStart);

    TokenExtractor withTokenEnd(String tokenEnd);

    TokenExtractor withAmountStart(String amountStart);

    TokenExtractor withAmountEnd(String amountEnd);

    public class Match {

        /**
         * e.g. random[1]
         */
        public final String match;

        /**
         * e.g. random
         */
        public final String tokenWithoutAmount;

        /**
         * e.g. 1
         */
        public final int amount;

        protected Match(String match, String tokenWithoutAmount, int amount) {
            this.match = match;
            this.tokenWithoutAmount = tokenWithoutAmount;
            this.amount = amount;
        }

        protected Match(String match, String tokenWithoutAmount) {
            this(match, tokenWithoutAmount, 1);
        }

        @Override
        public String toString() {
            return "Match [amount=" + amount + ", tokenWithoutAmount=" + tokenWithoutAmount + ", match=" + match + "]";
        }
    }

}
