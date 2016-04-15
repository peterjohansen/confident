package com.actram.configent.config;

import com.actram.configent.IllegalValueException;
import com.actram.configent.ValueChecker;

/**
 * Validates values to ensure safe casting at a later point. Values are wrapped
 * in and checked via a {@link ValueChecker}. An {@link IllegalValueException}
 * is thrown in the value is invalid.
 *
 * @author Peter André Johansen
 */
public interface ConfigValidator<T> {

	/**
	 * Checks the value in the specified checker to ensure that it is valid.
	 * 
	 * @throws IllegalValueException if the specified object is invalid
	 */
	public void validate(ValueChecker<T> checker) throws IllegalValueException;

}