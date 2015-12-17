package com.actram.configent;

import java.util.Objects;

/**
 * Contains type information for each entry in a {@link Config} so seamless
 * casting can be performed.
 *
 * @author Peter André Johansen
 */
class ConfigEntry<T> {
	private final ConfigValueChecker<T> checker = new ConfigValueChecker<>();

	private final Class<T> type;
	private final ConfigValidator<T> validator;

	public ConfigEntry(Class<T> type, ConfigValidator<T> validator) {
		Objects.requireNonNull(type, "type cannot be null");
		Objects.requireNonNull(validator, "validator cannot be null");
		this.type = type;
		this.validator = validator;
	}

	public T cast(Object value) {
		return type.cast(value);
	}

	public void validate(T value) {
		checker.setValue(value);
		validator.validate(checker);
	}
}