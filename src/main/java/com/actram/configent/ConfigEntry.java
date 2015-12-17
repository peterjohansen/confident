package com.actram.configent;

import java.util.Objects;

/**
 *
 *
 * @author Peter André Johansen
 */
class ConfigEntry<T> {
	private final String key;
	private final Class<T> type;
	private final ConfigValidator<T> validator;

	private final ConfigValueChecker<T> checker = new ConfigValueChecker<>();

	public ConfigEntry(String key, Class<T> type, ConfigValidator<T> validator) {
		Objects.requireNonNull(key, "key cannot be null");
		Objects.requireNonNull(validator, "validator cannot be null");
		this.key = key;
		this.type = type;
		this.validator = validator;
	}

	public T cast(Object value) {
		return type.cast(value);
	}

	public String getKey() {
		return key;
	}

	public void validate(T value) {
		checker.setValue(value);
		validator.validate(checker);
	}
}