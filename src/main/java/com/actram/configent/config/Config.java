package com.actram.configent.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 *
 * @author Peter André Johansen
 */
public class Config {
	/**
	 * @author Peter André Johansen
	 */
	public static class Builder { // Access to builder for users of the library
		final List<ConfigItem> items = new ArrayList<>();

		public ConfigItem.Builder addItem(String id) {
			return new ConfigItem.Builder((Config.Builder) this, id);
		}

		Config build() {
			Map<String, ConfigItem> map = new HashMap<>();
			for (ConfigItem item : items) {
				map.put(item.getID(), item);
			}
			return new Config(map);
		}
	}

	private final Map<String, ConfigItem> items;

	Config(Map<String, ConfigItem> items) {
		assert items != null : "items cannot be null";
		assert !items.keySet().contains(null) : "an item id cannot be null";
		assert !items.values().contains(null) : "an item cannot be null";
		this.items = items;
	}

	@SuppressWarnings("unchecked")
	public <T> T getDefault(String id) {
		return (T) getItem(id).createDefault();
	}

	private ConfigItem getItem(String id) {
		Objects.requireNonNull(id, "an id cannot be null");
		if (id.isEmpty()) throw new IllegalArgumentException("an id cannot be empty");
		return items.get(id);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(String id) {
		if (!hasEntry(id)) {
			throw new IllegalArgumentException("no config item with key: " + id);
		}
		return (T) items.get(id).getValue();
	}

	/**
	 * @return whether the config has an entry with the given id
	 */
	public boolean hasEntry(String id) {
		return items.containsKey(id);
	}
}