package com.actram.configent;

/**
 *
 *
 * @author Peter André Johansen
 */
public class BadConfigEntryException extends Exception {
	private static final long serialVersionUID = -8458752056497465894L;

	public BadConfigEntryException(String message) {
		super(message);
	}
}