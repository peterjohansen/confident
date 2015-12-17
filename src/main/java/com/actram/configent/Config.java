package com.actram.configent;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author Peter André Johansen
 */
public class Config {
	private static final Map<String, ConfigEntry<?>> entries = new HashMap<>();

	public void addEntry(String key) {
		addEntry(key, new Conf);
	}

	public void addEntry(String key, ConfigValidator validator) {
		entries.put(key, new ConfigEntry<>(key, type, validator));
	}
}