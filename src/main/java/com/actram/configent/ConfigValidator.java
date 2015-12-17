package com.actram.configent;

/**
 *
 *
 * @author Peter André Johansen
 */
public interface ConfigValidator {

	/**
	 * @throws BadConfigValueException if the specified object is invalid
	 */
	public void validate(ConfigValueChecker checker) throws BadConfigValueException;

}