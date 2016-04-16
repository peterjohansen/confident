package com.actram.configent.config;

import java.util.Map;

/**
 *
 *
 * @author Peter André Johansen
 */
public class Config {
	private final Map<String, ConfigItem> items;

	Config(Map<String, ConfigItem> items) {
		assert items != null : "items cannot be null";
		assert !items.keySet().contains(null) : "an item cannot be null";
		this.items = items;
	}

	@SuppressWarnings("unchecked")
	public <T> T getDefault(String key) {
		return (T) getItem(key).createDefault();
	}

	private ConfigItem getItem(String key) {
		return items.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(String key) {
		if (!hasEntry(key)) {
			throw new IllegalArgumentException("no config item with key: " + key);
		}
		return (T) items.get(key).getValue();
	}

	/**
	 * @return whether the config has an entry with the given key
	 */
	public boolean hasEntry(String key) {
		return items.containsKey(key);
	}
}