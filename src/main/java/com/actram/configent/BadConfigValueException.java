package com.actram.configent;

/**
 *
 *
 * @author Peter André Johansen
 */
public class BadConfigValueException extends RuntimeException {
	private static final long serialVersionUID = 2518729916865012796L;

	public BadConfigValueException(String message) {
		super(message);
	}
}