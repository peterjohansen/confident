package com.actram.configent.config;

import java.util.Objects;
import java.util.function.Supplier;

import com.actram.configent.ValueChecker;

/**
 * Contains information about each configuration item in a {@link Config}. Used internally only.
 *
 * @author Peter André Johansen
 */
class ConfigItem {
	public static class Builder {
		private Config.Builder builder;
		private ConfigItem item;
		private boolean sameDefaultValueReference;

		Builder(Config.Builder builder, String id) {
			this.builder = builder;
			Objects.requireNonNull(id, "config item id cannot be null");
			if (id.isEmpty()) throw new IllegalArgumentException("id cannot be empty");
			this.item = new ConfigItem(id);
		}

		public Builder addItem(String id) {
			itemDone();
			return builder.addItem(id);
		}

		public Config build() {
			itemDone();
			return builder.build();
		}

		void itemDone() {
			if (item.validator != null) {
				item.validate(item.defaultFactory == null ? null : item.createDefault());
			}
			if (item.validator != null && sameDefaultValueReference) {
				item.validate(item.defaultFactory.get());
			}
			builder.items.add(item);
		}

		public Builder setDefaultValue(Object value) {
			Objects.requireNonNull(value, "default value cannot be null");
			if (item.defaultFactory != null) throw new IllegalStateException("default value is already set");
			this.item.defaultFactory = () -> value;
			this.sameDefaultValueReference = true;
			return this;
		}

		public Builder setDefaultValue(Supplier<Object> valueSupplier) {
			Objects.requireNonNull(valueSupplier, "default value supplier canot be null");
			if (item.defaultFactory != null) throw new IllegalStateException("default value supplier is already set");
			this.item.defaultFactory = valueSupplier;
			this.sameDefaultValueReference = false;
			return this;
		}

		public Builder setType(Class<?> type) {
			Objects.requireNonNull(type, "config item type cannot be null");
			if (item.type != null) throw new IllegalStateException("config item type is already set");
			this.item.type = type;
			return this;
		}

		public Builder setValidator(ConfigValidator value) {
			Objects.requireNonNull(value, "config item value cannot be null");
			if (item.value != null) throw new IllegalStateException("config item value is already set");
			this.item.validator = value;
			return this;
		}
	}

	private static final ValueChecker<Object> checker = new ValueChecker<>();

	private String id;
	private Class<?> type;
	private ConfigValidator validator;
	private Supplier<Object> defaultFactory;

	private Object value;

	private ConfigItem(ConfigItem item) {
		assert item != null;
		assert item.id != null;
		assert item.type != null;
		assert item.validator != null;
		assert item.defaultFactory != null;
		this.id = item.id;
		this.type = item.type;
		this.validator = item.validator;
		this.defaultFactory = item.defaultFactory;
	}

	private ConfigItem(String id) {
		assert id != null : "id cannot be null";
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	private <T> T cast(Object value) {
		return (T) type.cast(value);
	}

	public <T> T createDefault() {
		return cast(defaultFactory.get());
	}

	public String getID() {
		return id;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		validate(value);
		this.value = value;
	}

	private <T> void validate(T value) {
		checker.setValue(cast(value));
		validator.validate(checker);
	}
}