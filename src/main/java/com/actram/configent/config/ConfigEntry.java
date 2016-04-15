package com.actram.configent.config;

import java.util.function.Supplier;

import com.actram.configent.ValueChecker;

/**
 * Contains information about each item in a {@link Config}.
 *
 * @author Peter André Johansen
 */
class ConfigEntry<T> {
	private final ValueChecker<T> checker = new ValueChecker<>();
	private final Class<T> type;
	private final ConfigValidator<T> validator;
	private final Supplier<ConfigEntry<T>> defaultFactory;

	private T value;

	public ConfigEntry(Class<T> type, ConfigValidator<T> validator, Supplier<ConfigEntry<T>> defaultFactory) {
		assert type != null : "type cannot be null";
		assert validator != null : "validator cannot be null";
		assert defaultFactory != null : "default factory cannot be null";
		this.type = type;
		this.validator = validator;
		this.defaultFactory = defaultFactory;
	}

	public T cast() {
		return type.cast(value);
	}

	public T castDefault(T value) {
		return type.cast(value);
	}

	public Class<T> getType() {
		return type;
	}

	public T getValue() {
		return value;
	}

	public void validate(T value) {
		checker.setValue(value);
		validator.validate(checker);
	}
}