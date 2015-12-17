package com.actram.configent;

/**
 *
 *
 * @author Peter André Johansen
 */
public abstract class ConfigValidator {
	public ConfigValidator() {
		
	}
	
	/**
	 * 
	 * @param value
	 * @throws BadConfigEntryException if the specified object is invalid
	 */
	public abstract void validate(Object value) throws BadConfigEntryException;
}