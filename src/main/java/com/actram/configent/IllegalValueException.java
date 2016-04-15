package com.actram.configent;

/**
 *
 *
 * @author Peter André Johansen
 */
public class IllegalValueException extends RuntimeException {
	private static final long serialVersionUID = 2518729916865012796L;

	public IllegalValueException(String message) {
		super(message);
	}
}