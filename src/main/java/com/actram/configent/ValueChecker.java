package com.actram.configent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Checks the value of an object to ensure safe casting at a later point. All
 * checking methods are chainable and throw a {@link IllegalValueException} if
 * the value is invalid.
 * <p>
 * In most cases you can perform checks using the methods that start with
 * {@code require}, but if you need to perform a custom check, you can use:
 * {@link #check(Consumer)} or the methods that start with {@code checkAgainst}
 * to check against collections.
 * <p>
 * Values that are {@code null} will automatically be handled, which means
 * {@link NullPointerException}s will never occur. By default, {@code null}
 * -values are not allowed and will throw a {@link IllegalValueException} if
 * checked. This can be changed by calling {@link #allowNull()}.
 * <p>
 * An example of a custom check to ensure the value is an even integer:
 * 
 * <pre>
 * 	check(value -> {
 * 		requireInteger();
 * 		final int n = (Integer) value;
 * 		if (n % != 0) {
 * 			fail("value must be an even integer");
 * 		}
 * 	});
 * </pre>
 * 
 * @author Peter André Johansen
 */
public class ValueChecker<T> {
	private T value;
	private boolean nullAllowed = false;

	/**
	 * Specifies that the value can be {@code null}.
	 */
	public ValueChecker<T> allowNull() {
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
	 * the check.
	 * 
	 * @function the function that checks the value
	 * @throws IllegalValueException if the value is invalid
	 */
	public ValueChecker<T> check(Consumer<T> function) {
		Objects.requireNonNull(function, "check function cannot be null");
		if (value != null) {
			function.accept(value);
		}
		if (!nullAllowed) {
			fail("value cannot be null");
		}
		return this;
	}

	/**
	 * Checks the value against every element in the specified array.
	 * <p>
	 * This method is a convenience for {@link #check(Consumer)} against the
	 * elements of an array.
	 * 
	 * @param values the array to check against
	 * @param function the function to check the value (first type) against a
	 *            value in the array (second type)
	 */
	@SuppressWarnings("unchecked")
	public <R> ValueChecker<?> checkAgainst(BiConsumer<T, R> function, R... values) {
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
	public <R> ValueChecker<T> checkAgainst(Collection<R> values, BiConsumer<T, R> function) {
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
	 * Throws an {@link IllegalValueException} with the given message.
	 * 
	 * @param message the exception message
	 * @throws IllegalValueException
	 */
	public void fail(String message) {
		Objects.requireNonNull(message, "exception message cannot be null");
		throw new IllegalValueException(message);
	}

	/**
	 * Calls {@link #fail(String)} with the specified formatted message.
	 */
	public void fail(String messageFormat, Object... args) {
		Objects.requireNonNull(args, "exception message format arguments cannot be null");
		throw new IllegalValueException(String.format(messageFormat, args));
	}

	/**
	 * Requires that the value is a {@link Comparable}.
	 */
	public ValueChecker<T> requireComparable() {
		return check(value -> {
			if (!(value instanceof Comparable<?>)) {
				fail("value must be comparable");
			}
		});
	}

	/**
	 * Requires that the value is in the given collection.
	 */
	public ValueChecker<T> requireIn(Collection<T> values) {
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
	public ValueChecker<T> requireIn(T... values) {
		Objects.requireNonNull(values, "list of values cannot be null");
		return requireNot(Arrays.asList(values));
	}

	/**
	 * Explicitly requires that the value is an {@code int}.
	 */
	private ValueChecker<T> requireInteger() {
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
	public ValueChecker<T> requireIntegerBetween(int min, int max) {
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
	public ValueChecker<T> requireIntegerMax(int max) {
		return requireInteger().check(value -> {
			if ((Integer) value > max) {
				fail("value must be an integer less than or equal to %s, currently: $s", max, value);
			}
		});
	}

	/**
	 * Requires that the value is an integer greater than or equal to the
	 * specified value.
	 */
	public ValueChecker<T> requireIntegerMin(int min) {
		return requireInteger().check(value -> {
			if ((Integer) value < min) {
				fail("value must be an integer greater than or equal to %s, currently: %s", min, value);
			}
		});
	}

	/**
	 * Requires that the value is a string and matches the given regex.
	 */
	public ValueChecker<T> requireMatch(String regex) {
		return requireMatch(regex, String.format("value must match %s, currently: %s", regex, value));
	}

	/**
	 * This method does the same as {@link #requireMatch(String)}, but lets you
	 * specify a more specific exception message.
	 */
	public ValueChecker<T> requireMatch(String regex, String customExceptionMessage) {
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
	public ValueChecker<T> requireNaturalInteger() {
		return requireInteger().check(value -> {
			if ((Integer) value > 0) {
				fail("value must be an integer greater than zero, currently: %s", value);
			}
		});
	}

	/**
	 * Requires that the value is a negative integer.
	 */
	public ValueChecker<T> requireNegativeInteger() {
		return requireInteger().check(value -> {
			if ((Integer) value < 0) {
				fail("value must be a negative integer, currently: %s", value);
			}
		});
	}

	/**
	 * Requires that the value is a string with a length greater than zero.
	 */
	public ValueChecker<T> requireNonEmptyString() {
		return requireString().check(value -> {
			if (((String) value).isEmpty()) {
				fail("value must be a non-empty string");
			}
		});
	}

	/**
	 * Requires that the value is not in the given collection.
	 */
	public ValueChecker<T> requireNot(Collection<T> values) {
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
	public ValueChecker<T> requireNot(Object... values) {
		Objects.requireNonNull(values, "list of values cannot be null");
		return requireNot(Arrays.asList(values));
	}

	/**
	 * Requires that the value is an integer greater than or equal to zero.
	 */
	public ValueChecker<T> requirePositiveInteger() {
		return requireInteger().check(value -> {
			if ((Integer) value >= 0) {
				fail("value must be positive integer, currently: %s", value);
			}
		});
	}

	/**
	 * Explicitly requires that the value is a {@link String}.
	 */
	private ValueChecker<T> requireString() {
		return requireType(String.class);
	}

	/**
	 * Requires that the value is assignable from every class specified.
	 */
	public ValueChecker<T> requireType(Class<?>... types) {
		Objects.requireNonNull(types, "list of types cannot be null");
		return requireType(Arrays.asList(types));
	}

	/**
	 * Requires that the value is assignable from every class in the given
	 * collection.
	 */
	public ValueChecker<T> requireType(Collection<Class<?>> types) {
		Objects.requireNonNull(types, "list of types cannot be null");
		for (Class<?> type : types) {
			Objects.requireNonNull(type, "a class cannot be null");
		}
		return checkAgainst(types, (value, type) -> {
			if (!value.getClass().isAssignableFrom(type)) {
				fail("value must be of type %s, currently: %s", type, value.getClass());
			}
		});
	}

	/**
	 * Resets the checker and sets the new value.
	 */
	public void setValue(T value) {
		nullAllowed = false;
		this.value = value;
	}
}