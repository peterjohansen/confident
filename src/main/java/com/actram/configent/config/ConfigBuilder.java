package com.actram.configent.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 
 * 
 * @author Peter André Johansen
 */
public class ConfigBuilder {
	public class ConfigItemBuilder<T> {

		// See builder setter methods for details
		// about each property's purpose.
		private String name;
		private Class<?> type;
		private ConfigValidator<T> constraints;
		private Supplier<T> defaultFactory;
		private Class<?> toType;
		private Function<?, T> typeMapper;

		/**
		 * @param originalItem the item to copy properties from (except name),
		 *            or {@code null} if no properties should be copied
		 * @param name the new item's name
		 */
		private ConfigItemBuilder(ConfigItemBuilder<T> originalItem, String name) {
			this.name = name;

			if (originalItem != null) {
				// Copy the original item's properties in the
				// same manner as the user would set them
				if (originalItem.type != null) //
					this.ofType(originalItem.type);
				if (originalItem.constraints != null) //
					this.withConstraints(originalItem.constraints);
				if (originalItem.defaultFactory != null) //
					this.defaultTo(originalItem.defaultFactory);
				if (originalItem.toType != null && originalItem.typeMapper != null) //
					this.mapToType(originalItem.toType, originalItem.typeMapper);
			}
		}

		/**
		 * Creates a brand new config builder item.
		 */
		private ConfigItemBuilder(String name) {
			this(null, name);
		}

		public ConfigItemBuilder<T> defaultTo(Supplier<T> defaultFactory) {
			if (this.defaultFactory != null) {
				throw new ConfigBuilderException("the default value has already been specified for: " + name);
			}
			Objects.requireNonNull(defaultFactory, "default factory cannot be null");
			this.defaultFactory = defaultFactory;
			return this;
		}

		public ConfigItemBuilder<T> mapToType(Class<?> type, Function<?, T> mapper) {
			assert ((toType == null) == (typeMapper == null)) : "type and mapper should both either be set or not set";
			if (this.toType != null && this.typeMapper != null) {
				throw new ConfigBuilderException("mapping has already been specified for: " + name);
			}
			Objects.requireNonNull(type, "type to map to cannot be null");
			Objects.requireNonNull(mapper, "type mapper cannot be null");

			this.toType = type;
			this.typeMapper = mapper;
			return this;
		}

		public ConfigItemBuilder<T> ofType(Class<?> type) {
			if (this.type != null) {
				throw new ConfigBuilderException("type has already been specified for: " + name);
			}
			Objects.requireNonNull(type, "configuration item's type cannot be null");

			this.type = type;
			return this;
		}

		public ConfigItemBuilder<T> withConstraints(ConfigValidator<T> val) {
			if (this.constraints != null) {
				throw new ConfigBuilderException("constraints have already been specified for: " + name);
			}
			Objects.requireNonNull(val, "configuration item's constraints cannot be null");

			this.constraints = val;
			return this;
		}
	}

	private final List<ConfigItemBuilder<?>> items = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public <T> ConfigItemBuilder<T> addCopy(String newName, String original) {
		for (ConfigItemBuilder<?> item : items) {
			if (original.equals(item.name)) {
				return (ConfigItemBuilder<T>) addItem(new ConfigItemBuilder<>(item, newName));
			}
		}
		throw new ConfigBuilderException("no previous configuration item with that name has been defined: " + original);
	}

	public <T> ConfigItemBuilder<T> addCopyOfPrevious(String newName) {
		ConfigItemBuilder<?> prev = getCurrentItem();
		if (prev == null) {
			throw new ConfigBuilderException("no previous configuration item has been defined");
		}
		return addCopy(newName, prev.name);
	}

	private <T> ConfigItemBuilder<T> addItem(ConfigItemBuilder<T> item) {
		assert item != null : "item cannot be null";
		items.add(item);
		return getCurrentItem();
	}

	public <T> ConfigItemBuilder<T> addItem(String name) {
		Objects.requireNonNull(name, "configuration item's name cannot be null");
		return addItem(new ConfigItemBuilder<>(name));
	}

	public Config build() {
		Map<String, ConfigItem> itemMap = new HashMap<>();
		for (ConfigItemBuilder<?> itemBuilder : items) {
			ConfigItem item = new ConfigItem( //
					itemBuilder.type, //
					itemBuilder.constraints, //
					itemBuilder.defaultFactory //
			);
			itemMap.put(itemBuilder.name, item);
		}
		return new Config(itemMap);
	}

	private <T> ConfigItemBuilder<T> getCurrentItem() {
		return (items.size() > 0 ? getItem(items.size() - 1) : null);
	}

	@SuppressWarnings("unchecked")
	private <T> ConfigItemBuilder<T> getItem(int index) {
		return (ConfigItemBuilder<T>) items.get(index);
	}
}