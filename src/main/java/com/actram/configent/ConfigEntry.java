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
	private final ConfigValidator validator;

	public ConfigEntry(String key, Class<T> type, ConfigValidator validator) {
		Objects.requireNonNull(key, "key cannot be null");
		Objects.requireNonNull(validator, "validator cannot be null");
		this.key = key;
		this.type = type;
		this.validator = validator;
	}

	public String getKey() {
		return key;
	}

	public void validate(Object value) throws BadConfigValueException {
		validator.validate(value);
	}
}