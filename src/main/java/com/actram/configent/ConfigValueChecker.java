package com.actram.configent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Checks the value of a object to ensure safe, unchecked casting at a later
 * point. All checking methods are chainable and throw a
 * {@link BadConfigValueException} if the value is invalid.
 * <p>
 * In most cases you can perform checks using the methods that start with
 * {@code require}, but if you need to perform a custom check, you can use:
 * {@link #check(Consumer)} or the methods that start with {@code checkAgainst}
 * to check against collections.
 * <p>
 * Values that are {@code null} will automatically be handled for you, which
 * means {@link NullPointerException}s will never occur. By default {@code null}
 * -values are not allowed and will throw a {@link BadConfigValueException} if
 * checked. This can be changed by calling {@link #allowNull()}.
 * 
 * @author Peter André Johansen
 */
public class ConfigValueChecker<T> {
	private T value;

	private boolean nullAllowed = false;

	/**
	 * Only allow instantiation from within this package.
	 */
	ConfigValueChecker() {}

	/**
	 * Specifies that the value can be {@code null}.
	 * 
	 * @return
	 */
	public ConfigValueChecker<T> allowNull() {
		if (nullAllowed) {
			throw new IllegalStateException("null values are already allowed");
		}
		nullAllowed = true;
		return this;
	}

	/**
	 * Performs a custom check on the value.
	 * <p>
	 * <strong>Note:</strong> You can assume the value is not {@code null} in
	 * the check. The check is performed for you and will act accordingly
	 * depending on if {@link #allowNull()} has been called.
	 * 
	 * @function the function that checks the value
	 * @throws BadConfigValueException if the value is invalid
	 */
	public ConfigValueChecker<T> check(Consumer<T> function) {
		Objects.requireNonNull(function, "check function cannot be null");
		if (value != null) {
			function.accept(value);
		}
		if (!nullAllowed) {
			fail("value cannot be null");
		}
		return this;
	}

	// /**
	*

	Checks the
	value against
	every element
	in the
	specified array.*<p>*
	This method
	is a convenience for

	{@link #check(Consumer)}

	against the*
	elements of
	an array.**
	@param values the
	array to
	check against*
	@param function the
	function to
	check the

	value (first type) against a
	 *            value in

	the array (second type)
	 */

	@SuppressWarnings("unchecked")
	public <R> ConfigValueChecker<?> checkAgainst(BiConsumer<T, R> function, R... values) {
		Objects.requireNonNull(values, "list of values cannot be null");
		return checkAgainst(Arrays.asList(values), function);
	}

	/**
	 * Checks the value against every element in the specified collection.
	 * <p>
	 * This method is a convenience for {@link #check(Consumer)} against the
	 * elements of a collection.
	 * <p>
	 * <strong>Note:</strong> The checking will occur in the order specified by
	 * the {@link Collection}.
	 * 
	 * @param values the collection to check against
	 * @param function the function to check the value (first type) against a
	 *            value in the collection (second type)
	 */
	public <R> ConfigValueChecker<T> checkAgainst(Collection<R> values, BiConsumer<T, R> function) {
		Objects.requireNonNull(values, "list of values cannot be null");
		Objects.requireNonNull(function, "check function cannot be null");
		if (values.size() == 0) {
			throw new IllegalArgumentException("list of values cannot be empty");
		}
		return check(value1 -> {
			for (R value2 : values) {
				Objects.requireNonNull(value2, "value in list cannot be null");
				function.accept(value1, value2);
			}
		});
	}

	/**
	 * Throws a {@link BadConfigValueException} with the given message.
	 * 
	 * @param message the exception message
	 * @throws BadConfigValueException the
	 */
	public void fail(String message) {
		Objects.requireNonNull(message, "exception message cannot be null");
		throw new BadConfigValueException(message);
	}

	/**
	 * Calls {@link #fail(String)} with the specified formatted message.
	 */
	public void fail(String messageFormat, Object... args) {
		Objects.requireNonNull(messageFormat, "exception message format cannot be null");
		Objects.requireNonNull(args, "exception message format arguments cannot be null");
		throw new BadConfigValueException(String.format(messageFormat, args));
	}

	/**
	 * Requires that the value is a {@link Comparable}.
	 */
	public ConfigValueChecker<T> requireComparable() {
		return check(value -> {
			if (!(value instanceof Comparable<?>)) {
				fail("value must be comparable");
			}
		});
	}

	/**
	 * Requires that the value is in the given collection.
	 */
	public ConfigValueChecker<T> requireIn(Collection<T> values) {
		return check(value -> {
			if (!values.contains(value)) {
				fail("value must be in the list");
			}
		});
	}

	/**
	 * Requires that the value is in the given array.
	 */
	@SuppressWarnings("unchecked")
	public ConfigValueChecker<T> requireIn(T... values) {
		Objects.requireNonNull(values, "list of values cannot be null");
		return requireNot(Arrays.asList(values));
	}

	/**
	 * Requires that the value is an {@code int}.
	 */
	public ConfigValueChecker<T> requireInteger() {
		return check(value -> {
			try {
				Integer.parseInt(value.toString());
			} catch (NumberFormatException e) {
				fail("value must be an integer");
			}
		});
	}

	/**
	 * Requires that the value is an integer between the specified range
	 * (inclusive on both ends).
	 */
	public ConfigValueChecker<T> requireIntegerBetween(int min, int max) {
		return requireInteger().check(value -> {
			if ((Integer) value < min || (Integer) value > max) {
				fail("value must be an integer between or equal %s and %s, currently: %s", min, max, value);
			}
		});
	}

	/**
	 * Requires that the value is an integer less than or equal to the specified
	 * value.
	 */
	public ConfigValueChecker<T> requireIntegerMax(int max) {
		return requireInteger().check(value -> {
			if ((Integer) value > max) {
				fail("value must be an integer less than or equal to %s, currently: $s", max, value);
			}
		});
	}

	/**
	 * Requires that the value is an integer greater than or equal to the
	 * specified value.
	 * 
	 * @param min
	 * @return
	 */
	public ConfigValueChecker<T> requireIntegerMin(int min) {
		return requireInteger().check(value -> {
			if ((Integer) value < min) {
				fail("value must be an integer greater than or equal to %s, currently: %s", min, value);
			}
		});
	}

	/**
	 * Requires that the value is a string and matches the given regex.
	 */
	public ConfigValueChecker<T> requireMatch(String regex) {
		return requireMatch(regex, String.format("value must match %s, currently: %s", regex, value));
	}

	/**
	 * This method does the same as {@link #requireMatch(String)}, but lets you
	 * specify a more specific exception message.
	 */
	public ConfigValueChecker<T> requireMatch(String regex, String customExceptionMessage) {
		Objects.requireNonNull(regex, "regex cannot be null");
		Objects.requireNonNull(customExceptionMessage, "custom exception message cannot be null");
		return check(value -> {
			if (!((String) value).matches(regex)) {
				fail(customExceptionMessage);
			}
		});
	}

	/**
	 * Requires that the value is an integer greater than zero.
	 */
	public ConfigValueChecker<T> requireNaturalInteger() {
		return requireInteger().check(value -> {
			if ((Integer) value > 0) {
				fail("value must be an integer greater than zero, currently: %s", value);
			}
		});
	}

	/**
	 * Requires that the value is a negative integer.
	 */
	public ConfigValueChecker<T> requireNegativeInteger() {
		return requireInteger().check(value -> {
			if ((Integer) value < 0) {
				fail("value must be a negative integer, currently: %s", value);
			}
		});
	}

	/**
	 * Requires that the value is a string with a length greater than zero.
	 */
	public ConfigValueChecker<T> requireNonEmptyString() {
		return requireString().check(value -> {
			if (((String) value).isEmpty()) {
				fail("value must be a non-empty string");
			}
		});
	}

	/**
	 * Requires that the value is not in the given collection.
	 */
	public ConfigValueChecker<T> requireNot(Collection<T> values) {
		Objects.requireNonNull(values, "list of values cannot be null");
		return check(value -> {
			if (values.contains(value)) {
				fail("value cannot be in the list");
			}
		});
	}

	/**
	 * Requires that the value is not in the given array.
	 */
	public ConfigValueChecker<T> requireNot(Object... values) {
		Objects.requireNonNull(values, "list of values cannot be null");
		return requireNot(Arrays.asList(values));
	}

	/**
	 * Requires that the value is an integer greater than or equal to zero.
	 */
	public ConfigValueChecker<T> requirePositiveInteger() {
		return requireInteger().check(value -> {
			if ((Integer) value >= 0) {
				fail("value must be positive integer, currently: %s", value);
			}
		});
	}

	/**
	 * Requires that the value is a {@link String}.
	 */
	public ConfigValueChecker<T> requireString() {
		return requireType(String.class);
	}

	/**
	 * Requires that the value is an instance of every class in the given array.
	 */
	public ConfigValueChecker<T> requireType(Class<?>... types) {
		Objects.requireNonNull(types, "list of types cannot be null");
		return requireType(Arrays.asList(types));
	}

	/**
	 * Requires that the value is an instance of every class in the given
	 * collection.
	 */
	public ConfigValueChecker<T> requireType(Collection<Class<?>> types) {
		Objects.requireNonNull(types, "list of types cannot be null");
		return checkAgainst(types, (value, type) -> {
			if (!value.getClass().isAssignableFrom(type)) {
				fail("value must be of type %s, currently: %s", type, value.getClass());
			}
		});
	}

	/**
	 * Sets the value this checker should perform checks on and resets it to not
	 * allow {@code null}-values.
	 */
	void setValue(T value) {
		nullAllowed = false;
		this.value = value;
	}
}