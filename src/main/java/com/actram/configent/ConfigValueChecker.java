package com.actram.configent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * 
 * @author Peter André Johansen
 */
public class ConfigValueChecker<T> {
	private T value;

	private boolean nullAllowed = false;

	ConfigValueChecker() {}

	public ConfigValueChecker<T> allowNull() {
		if (nullAllowed) {
			throw new IllegalStateException("null values are already allowed");
		}
		nullAllowed = true;
		return this;
	}

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

	@SuppressWarnings("unchecked")
	public <R> ConfigValueChecker<?> checkAgainst(BiConsumer<T, R> function, R... array) {
		Objects.requireNonNull(array, "list of values cannot be null");
		return checkAgainst(Arrays.asList(array), function);
	}

	public <R> ConfigValueChecker<T> checkAgainst(Collection<R> collection, BiConsumer<T, R> function) {
		Objects.requireNonNull(collection, "list of values cannot be null");
		Objects.requireNonNull(function, "check function cannot be null");
		if (collection.size() == 0) {
			throw new IllegalArgumentException("list of values cannot be empty");
		}
		return check(value1 -> {
			for (R value2 : collection) {
				Objects.requireNonNull(value2, "value in list cannot be null");
				function.accept(value1, value2);
			}
		});
	}

	public void fail(String message) {
		Objects.requireNonNull(message, "exception message cannot be null");
		throw new BadConfigValueException(message);
	}

	public void fail(String messageFormat, Object... args) {
		Objects.requireNonNull(messageFormat, "exception message format cannot be null");
		Objects.requireNonNull(args, "exception message format arguments cannot be null");
		throw new BadConfigValueException(String.format(messageFormat, args));
	}

	public ConfigValueChecker<T> requireComparable() {
		return check(value -> {
			if (!(value instanceof Comparable<?>)) {
				fail("value must be comparable");
			}
		});
	}

	public ConfigValueChecker<T> requireIn(Collection<T> collection) {
		return checkAgainst(collection, (value1, value2) -> {
			if (value1.equals(value2)) {
				return;
			}
			fail("value must be in the list");
		});
	}

	@SuppressWarnings("unchecked")
	public ConfigValueChecker<T> requireIn(T... values) {
		Objects.requireNonNull(values, "list of values cannot be null");
		return requireNot(Arrays.asList(values));
	}

	public ConfigValueChecker<T> requireInteger() {
		return check(value -> {
			try {
				Integer.parseInt(value.toString());
			} catch (NumberFormatException e) {
				fail("value must be an integer");
			}
		});
	}

	public ConfigValueChecker<T> requireIntegerBetween(int min, int max) {
		return requireInteger().check(value -> {
			if ((Integer) value < min || (Integer) value > max) {
				fail("value must be an integer between or equal %s and %s, currently: %s", min, max, value);
			}
		});
	}

	public ConfigValueChecker<T> requireIntegerMax(int max) {
		return requireInteger().check(value -> {
			if ((Integer) value > max) {
				fail("value must be an integer less than or equal to %s, currently: $s", max, value);
			}
		});
	}

	public ConfigValueChecker<T> requireIntegerMin(int min) {
		return requireInteger().check(value -> {
			if ((Integer) value < min) {
				fail("value must be an integer greater than or equal to %s, currently: %s", min, value);
			}
		});
	}

	public ConfigValueChecker<T> requireMatch(String regex) {
		return requireMatch(regex, String.format("value must match %s, currently: %s", regex, value));
	}

	public ConfigValueChecker<T> requireMatch(String regex, String customExceptionMessage) {
		Objects.requireNonNull(regex, "regex cannot be null");
		Objects.requireNonNull(customExceptionMessage, "custom exception message cannot be null");
		return check(value -> {
			if (!((String) value).matches(regex)) {
				fail(customExceptionMessage);
			}
		});
	}

	public ConfigValueChecker<T> requireNaturalInteger() {
		return requireInteger().check(value -> {
			if ((Integer) value > 0) {
				fail("value must be an integer greater than zero, currently: %s", value);
			}
		});
	}

	public ConfigValueChecker<T> requireNegativeInteger() {
		return requireInteger().check(value -> {
			if ((Integer) value < 0) {
				fail("value must be a negative integer, currently: %s", value);
			}
		});
	}

	public ConfigValueChecker<T> requireNonEmptyString() {
		return requireString().check(value -> {
			if (((String) value).isEmpty()) {
				fail("value must be a non-empty string");
			}
		});
	}

	public ConfigValueChecker<T> requireNot(Object... values) {
		Objects.requireNonNull(values, "list of values cannot be null");
		return requireNot(Arrays.asList(values));
	}

	public ConfigValueChecker<T> requirePositiveInteger() {
		return requireInteger().check(value -> {
			if ((Integer) value >= 0) {
				fail("value must be positive integer, currently: %s", value);
			}
		});
	}

	public ConfigValueChecker<T> requireString() {
		return requireType(String.class);
	}

	public ConfigValueChecker<T> requireType(Class<?>... types) {
		Objects.requireNonNull(types, "list of types cannot be null");
		return requireType(Arrays.asList(types));
	}

	public ConfigValueChecker<T> requireType(Collection<Class<?>> types) {
		Objects.requireNonNull(types, "list of types cannot be null");
		return checkAgainst(types, (value, type) -> {
			if (!value.getClass().isAssignableFrom(type)) {
				fail("value must be of type %s, currently: %s", type, value.getClass());
			}
		});
	}

	void setValue(T value) {
		this.value = value;
	}
}