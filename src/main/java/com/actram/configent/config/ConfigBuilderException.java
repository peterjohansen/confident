package com.actram.configent.config;

/**
 * Thrown when the builder receives modifications that are illegal or invalid.
 * 
 * @author Peter André Johansen
 */
public class ConfigBuilderException extends RuntimeException {
	private static final long serialVersionUID = -5044350203924305108L;

	public ConfigBuilderException(String message) {
		super(message);
	}
}