package com.actram.configent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 
 *
 * @author Peter André Johansen
 */
public class ConfigBuilder {

	/**
	 * 
	 * 
	 * @author Peter André Johansen
	 */
	public static class ConfigEntryBuilder {
		private final String key;

		private Class<?> type;

		private Supplier<?> defaultValueSupplier;
		private Object initalDefaultValue;

		private ConfigValidator<?> validator;

		private ConfigEntryBuilder(String key) {
			Objects.requireNonNull(key, "config entry key cannot be null");
			this.key = key;
		}

		public ConfigEntryBuilder withDefault(Object defaultValue) {
			if (this.defaultValueSupplier != null) {
				throw new ConfigBuilderException("default value has already been specified");
			}
			Objects.requireNonNull(defaultValueSupplier, "default value supplier cannot be null");
			this.initalDefaultValue = defaultValueSupplier.get();
			this.defaultValueSupplier = defaultValueSupplier;
			return this;
		}

		public ConfigEntryBuilder withDefault(Supplier<?> defaultValueSupplier) {
			if (this.defaultValueSupplier != null) {
				throw new ConfigBuilderException("default value has already been specified");
			}
			Objects.requireNonNull(defaultValueSupplier, "default value supplier cannot be null");
			this.initalDefaultValue = defaultValueSupplier.get();
			this.defaultValueSupplier = defaultValueSupplier;
			return this;
		}

		public ConfigEntryBuilder withType(Class<?> type) {
			if (this.type != null) {
				throw new ConfigBuilderException("value type has already been specified");
			}
			Objects.requireNonNull(type, "value type cannot be null");
			this.type = type;
			return this;
		}

		public <T> ConfigEntryBuilder withValidation(ConfigValidator<T> validator) {
			if (this.validator != null) {
				throw new ConfigBuilderException("value validator has already been specified");
			}
			Objects.requireNonNull(validator, "value validator cannot be null");
			this.validator = validator;
			return this;
		}
	}

	private final List<ConfigEntryBuilder> entries = new ArrayList<>();

	public Config build() {
		return null;
	}

	public ConfigEntryBuilder newEntry(String key) {
		Objects.requireNonNull(key, "config entry key cannot be null");
		entries.add(new ConfigEntryBuilder(key));
		return entries.get(entries.size() - 1);
	}
}