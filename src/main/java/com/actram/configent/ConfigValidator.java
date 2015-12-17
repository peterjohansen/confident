package com.actram.configent;

/**
 * Validates values to ensure safe, unchecked casting at a later point. Values
 * are wrapped in and checked via an instance of {@link ConfigValueChecker}. A
 * {@link BadConfigValueException} is thrown in the value is invalid.
 *
 * @author Peter André Johansen
 */
public interface ConfigValidator<T> {

	/**
	 * Checks the value in the specified checker to ensure that it is valid.
	 * 
	 * @throws BadConfigValueException if the specified object is invalid
	 */
	public void validate(ConfigValueChecker<T> checker) throws BadConfigValueException;

}