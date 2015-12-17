package com.actram.configent;

/**
 *
 *
 * @author Peter André Johansen
 */
public interface ConfigValidator<T> {

	/**
	 * @throws BadConfigValueException if the specified object is invalid
	 */
	public void validate(ConfigValueChecker<T> checker) throws BadConfigValueException;

}