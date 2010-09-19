package de.marcelsauer.tokenreplacer;

public class Token {

	private final String token;
	private String tokenStart = Constants.DEFAULT_TOKEN_START;
	private String tokenEnd = Constants.DEFAULT_TOKEN_END;
	private String amountStart = Constants.DEFAULT_AMOUNT_START;
	private String amountEnd = Constants.DEFAULT_AMOUNT_END;
	private Generator generator;

	public Token(String token) {
		this.token = token;
	}

	public Token replacedBy(final String value) {
		this.generator = new Generator() {

			@Override
			public String generate() {
				return value;
			}
		};
		return this;
	}

	public Token withTokenStart(String tokenStart) {
		this.tokenStart = tokenStart;
		return this;
	}

	public Token withTokenEnd(String tokenEnd) {
		this.tokenEnd = tokenEnd;
		return this;
	}

	public String getToken() {
		return token;
	}

	public String getTokenEnd() {
		return tokenEnd;
	}

	public String getTokenStart() {
		return tokenStart;
	}

	public Token replacedBy(Generator generator) {
		this.generator = generator;
		return this;
	}

	public Generator getGenerator() {
		return generator;
	}

	public Token withAmountStart(String amountStart) {
		Validate.notEmpty(amountStart);
		this.amountStart = amountStart;
		return this;
	}

	public Token withAmountEnd(String amountEnd) {
		Validate.notEmpty(amountEnd);
		this.amountEnd = amountEnd;
		return this;
	}

	public String getAmountStart() {
		return amountStart;
	}

	public String getAmountEnd() {
		return amountEnd;
	}

	@Override
	public String toString() {
		return "Token [amountEnd=" + amountEnd + ", amountStart=" + amountStart + ", generator=" + generator
				+ ", token=" + token + ", tokenEnd=" + tokenEnd + ", tokenStart=" + tokenStart + "]";
	}
}
