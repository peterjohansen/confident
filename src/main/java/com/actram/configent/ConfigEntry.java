package com.actram.configent;

import java.util.Objects;

/**
 *
 *
 * @author Peter André Johansen
 */
class ConfigEntry {
	private final String key;
	private final ConfigValidator validator;

	public ConfigEntry(String key, ConfigValidator validator) {
		Objects.requireNonNull(key, "key cannot be null");
		Objects.requireNonNull(validator, "validator cannot be null");
		this.key = key;
		this.validator = validator;
	}
	
	public String getKey() {
		return key;
	}
	
	public void validate(Object value) {
		
	}
}