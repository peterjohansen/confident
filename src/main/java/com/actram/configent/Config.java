package com.actram.configent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 *
 * @author Peter André Johansen
 */
public class Config {
	private final Map<String, ConfigEntry<?>> entries = new HashMap<>();

	/**
	 * Calls {@link #addEntry(String, Class, ConfigValidator)} with a config
	 * validator that only allows non-{@code null} values.
	 */
	void addEntry(String key, Class<?> type) {
		addEntry(key, type, checker -> {});
	}

	/**
	 * Adds an entry to the config. That is, the config is informed that it
	 * contains a value with the given key and type. The specified validator
	 * will be used to check which values are valid for this entry.
	 */
	<T> void addEntry(String key, Class<T> type, ConfigValidator<T> validator) {
		Objects.requireNonNull(key, "entry key cannot be null");
		entries.put(key, new ConfigEntry<T>(type, validator));
	}
}