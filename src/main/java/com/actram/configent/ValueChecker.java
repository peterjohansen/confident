package com.actram.configent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Checks the value of an object to ensure safe casting at a later point. All checking methods are chainable and throw an
 * {@link IllegalValueException} if the value is invalid.
 * <p>
 * In most cases you can perform checks using the methods that start with {@code require}, but if you need to perform a custom
 * check, you can use: {@link #check(Consumer)} or the methods that start with {@code checkAgainst} to check against
 * collections. Values that are {@code null} will automatically be handled in custom checks as long as you use these methods,
 * which means {@link NullPointerException}s will never occur.
 * <p>
 * By default, values that are {@code null} are not allowed and will throw an {@link IllegalValueException} if checked. This can
 * be changed by calling {@link #allowNull()}.
 * <p>
 * An example of a custom check to ensure the value is an even integer:
 * 
 * <pre>
 * check(value -> {
 * 	requireInteger();
 * 	final int n = (Integer) value;
 * 	if (n % 2 != 0) {
 * 		fail("value must be an even integer: " + n);
 * 	}
 * });
 * </pre>
 * 
 * @author Peter André Johansen
 */
public class ValueChecker<T> {
	private T value;
	private boolean nullAllowed = false;

	/**
	 * Specifies that the value can be {@code null}.
	 * <p>
	 * <b>Note:</b> This method must be called first if any method involving assuming the value's type is called.
	 */
	public final ValueChecker<T> allowNull() {
		if (nullAllowed) {
			throw new IllegalStateException("null values are already allowed");
		}
		nullAllowed = true;
		return this;
	}

	/**
	 * Performs a custom check on the value.
	 * <p>
	 * <strong>Note:</strong> You can assume the value is not {@code null} in the check.
	 * 
	 * @function the function that checks the value
	 * @throws IllegalValueException if the value is invalid
	 */
	public final ValueChecker<T> check(Consumer<T> function) {
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
	 * This method is a convenience for {@link #check(Consumer)} against the elements of an array.
	 * 
	 * @param values the array to check against
	 * @param function the function to check the value (first type) against a value in the array (second type)
	 */
	@SuppressWarnings("unchecked")
	public final <R> ValueChecker<?> checkAgainst(BiConsumer<T, R> function, R... values) {
		Objects.requireNonNull(values, "list of values cannot be null");
		return checkAgainst(Arrays.asList(values), function);
	}

	/**
	 * Checks the value against every element in the specified collection.
	 * <p>
	 * This method is a convenience for {@link #check(Consumer)} against the elements of a collection.
	 * <p>
	 * <strong>Note:</strong> The checking will occur in the order specified by the {@link Collection}.
	 * 
	 * @param values the collection to check against
	 * @param function the function to check the value (first type) against a value in the collection (second type)
	 */
	public final <R> ValueChecker<T> checkAgainst(Collection<R> values, BiConsumer<T, R> function) {
		Objects.requireNonNull(values, "list of values cannot be null");
		if (values.contains(null)) throw new NullPointerException("a value cannot be null");
		Objects.requireNonNull(function, "check function cannot be null");
		if (values.size() == 0) {
			throw new IllegalArgumentException("list of values cannot be empty");
		}
		return check(value1 -> {
			for (R value2 : values) {
				function.accept(value1, value2);
			}
		});
	}

	/**
	 * Throws an {@link IllegalValueException} with the given message. The exception message should, if possible, specify
	 * specifically why the value failed a check.
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
	public final void fail(String messageFormat, Object... args) {
		Objects.requireNonNull(args, "exception message format arguments cannot be null");
		throw new IllegalValueException(String.format(messageFormat, args));
	}

	/**
	 * @return whether {@code null} values are allowed
	 */
	public boolean isNullAllowed() {
		return nullAllowed;
	}

	/**
	 * Convenience method to require that the value is a {@link boolean}.
	 */
	public final ValueChecker<T> requireBoolean() {
		return requireType(Boolean.class);
	}

	/**
	 * Convenience method to require that the value is a {@link char}.
	 */
	public final ValueChecker<T> requireCharacter() {
		return requireType(Character.class);
	}

	/**
	 * Requires that the value is a {@link Comparable}.
	 */
	public final ValueChecker<T> requireComparable() {
		return check(value -> {
			if (!(value instanceof Comparable<?>)) {
				fail("value must be comparable");
			}
		});
	}

	/**
	 * Convenience method to require that the value is a {@link double}.
	 */
	public final ValueChecker<T> requireDouble() {
		return requireType(Double.class);
	}

	/**
	 * Calls {@link #requireIn(Collection, String)} with a generic exception message.
	 */
	public final ValueChecker<T> requireIn(Collection<T> values) {
		return requireIn(values, "value must be in the list");
	}

	/**
	 * Requires that the value is in the given collection.
	 * 
	 * @param exceptionMessage the specific exception message to use if the value is not found in the collection
	 */
	public final ValueChecker<T> requireIn(Collection<T> values, String exceptionMessage) {
		return check(value -> {
			if (!values.contains(value)) {
				fail(exceptionMessage);
			}
		});
	}

	/**
	 * Requires that the value is in the given array.
	 * 
	 * @param exceptionMessage the specific exception message to use if the value is not found in the array
	 */
	@SafeVarargs
	public final ValueChecker<T> requireIn(String exceptionMessage, T... values) {
		Objects.requireNonNull(values, "list of values cannot be null");
		return requireIn(Arrays.asList(values), exceptionMessage);
	}

	/**
	 * Calls {@link #requireIn(Object[], String)} with a generic exception message.
	 */
	@SafeVarargs
	public final ValueChecker<T> requireIn(T... values) {
		return this.requireIn("value must be in the array", values);
	}

	/**
	 * Convenience method to require that the value is an {@link int}.
	 */
	public final ValueChecker<T> requireInteger() {
		return requireType(Integer.class);
	}

	/**
	 * Requires that the value is an integer between the specified range (inclusive on both ends).
	 */
	public final ValueChecker<T> requireIntegerBetween(int min, int max) {
		return requireInteger().check(value -> {
			if ((Integer) value < min || (Integer) value > max) {
				fail("value must be an integer between or equal %s and %s, currently: %s", min, max, value);
			}
		});
	}

	/**
	 * Requires that the value is an integer less than or equal to the specified value.
	 */
	public final ValueChecker<T> requireIntegerMax(int max) {
		return requireInteger().check(value -> {
			if ((Integer) value > max) {
				fail("value must be an integer less than or equal to %s, currently: $s", max, value);
			}
		});
	}

	/**
	 * Requires that the value is an integer greater than or equal to the specified value.
	 */
	public final ValueChecker<T> requireIntegerMin(int min) {
		return requireInteger().check(value -> {
			if ((Integer) value < min) {
				fail("value must be an integer greater than or equal to %s, currently: %s", min, value);
			}
		});
	}

	/**
	 * Calls {@link #requireMatch(String, String)} with a generic exception message.
	 */
	public final ValueChecker<T> requireMatch(String regex) {
		return requireMatch(regex, String.format("value must match %s, currently: %s", regex, value));
	}

	/**
	 * Requires that the value is a string and matches the given regex.
	 * 
	 * @param exceptionMessage the specific exception message to use if the value does not match the regex
	 */
	public final ValueChecker<T> requireMatch(String regex, String exceptionMessage) {
		Objects.requireNonNull(regex, "regex cannot be null");
		return check(value -> {
			if (!((String) value).matches(regex)) {
				fail(exceptionMessage);
			}
		});
	}

	/**
	 * Requires that the value is an integer greater than zero.
	 */
	public final ValueChecker<T> requireNaturalInteger() {
		return requireInteger().check(value -> {
			if ((Integer) value > 0) {
				fail("value must be an integer greater than zero, currently: %s", value);
			}
		});
	}

	/**
	 * Requires that the value is a negative integer.
	 */
	public final ValueChecker<T> requireNegativeInteger() {
		return requireInteger().check(value -> {
			if ((Integer) value < 0) {
				fail("value must be a negative integer, currently: %s", value);
			}
		});
	}

	/**
	 * Requires that the value is a string with a length greater than zero.
	 */
	public final ValueChecker<T> requireNonEmptyString() {
		return requireString().check(value -> {
			if (((String) value).isEmpty()) {
				fail("value must be a non-empty string");
			}
		});
	}

	/**
	 * Calls {@link #requireNot(Collection, String)} with a generic exception message.
	 */
	public final ValueChecker<T> requireNot(Collection<T> values) {
		return this.requireNot(values, "value cannot be in the list");
	}

	/**
	 * Requires that the value is not in the given collection.
	 * 
	 * @param exceptionMessage the specific exception message to use if the value is found in the collection
	 */
	public final ValueChecker<T> requireNot(Collection<T> values, String exceptionMessage) {
		Objects.requireNonNull(values, "list of values cannot be null");
		return check(value -> {
			if (values.contains(value)) {
				fail(exceptionMessage);
			}
		});
	}

	/**
	 * Requires that the value is not in the given array.
	 * 
	 * @param exceptionMessage the specific exception message to use if the value is found in the array
	 */
	@SafeVarargs
	public final ValueChecker<T> requireNot(String exceptionMessage, T... values) {
		Objects.requireNonNull(values, "list of values cannot be null");
		return requireNot(Arrays.asList(values), exceptionMessage);
	}

	/**
	 * Calls {@link #requireNot(String, Object...)} with a generic exception message.
	 */
	@SafeVarargs
	public final ValueChecker<T> requireNot(T... values) {
		Objects.requireNonNull(values, "list of values cannot be null");
		return requireNot(Arrays.asList(values), "value cannot be in the array");
	}

	/**
	 * Calls {@link #requirePassTest(Predicate, String)} with a generic exception message.
	 */
	public final ValueChecker<T> requirePassTest(Predicate<T> predicate) {
		return this.requirePassTest(predicate, "value must pass predicate");
	}

	/**
	 * Requires that the value passes the given predicate.
	 * 
	 * @param exceptionMessage the specific exception message to use if the value fails the test
	 */
	public final ValueChecker<T> requirePassTest(Predicate<T> predicate, String exceptionMessage) {
		Objects.requireNonNull(predicate, "predicate cannot be null");
		return check(value -> {
			if (!predicate.test(value)) {
				fail(exceptionMessage);
			}
		});
	}

	/**
	 * Requires that the value is an integer greater than or equal to zero.
	 */
	public final ValueChecker<T> requirePositiveInteger() {
		return requireInteger().check(value -> {
			if ((Integer) value >= 0) {
				fail("value must be positive integer, currently: %s", value);
			}
		});
	}

	/**
	 * Convenience method to require that the value is a {@link String}.
	 */
	public final ValueChecker<T> requireString() {
		return requireType(String.class);
	}

	/**
	 * Requires that the value is assignable from every class specified.
	 */
	public final ValueChecker<T> requireType(Class<?>... types) {
		Objects.requireNonNull(types, "list of types cannot be null");
		return requireType(Arrays.asList(types));
	}

	/**
	 * Requires that the value is assignable from every class in the given collection.
	 */
	public final ValueChecker<T> requireType(Collection<Class<?>> types) {
		Objects.requireNonNull(types, "list of types cannot be null");
		if (types.contains(null)) throw new NullPointerException("a class cannot be null");
		return checkAgainst(types, (value, type) -> {
			if (!value.getClass().isAssignableFrom(type)) {
				fail("value must be of type %s, currently: %s", type, value.getClass());
			}
		});
	}

	/**
	 * Resets the checker and sets the new value to perform checks on.
	 */
	public final void setValue(T value) {
		nullAllowed = false;
		this.value = value;
	}
}