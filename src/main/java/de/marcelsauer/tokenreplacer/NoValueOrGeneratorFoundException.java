package de.marcelsauer.tokenreplacer;

/**
 * @author msauer
 */
public class NoValueOrGeneratorFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoValueOrGeneratorFoundException (String msg) {
		super(msg);
	}

}
