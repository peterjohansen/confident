package com.actram.configent.config;

import java.util.function.Supplier;

import com.actram.configent.ValueChecker;

/**
 * Contains information about each configuration item in a {@link Config}. Used
 * internally only.
 *
 * @author Peter André Johansen
 */
class ConfigItem {
	final ValueChecker<Object> checker = new ValueChecker<>();
	final Class<?> type;
	final ConfigValidator<Object> validator;
	final Supplier<?> defaultFactory;

	private Object value;

	@SuppressWarnings("unchecked")
	public ConfigItem(Class<?> type, ConfigValidator<?> validator, Supplier<?> defaultFactory) {
		assert type != null : "type cannot be null";
		assert validator != null : "validator cannot be null";
		assert defaultFactory != null : "default factory cannot be null";
		this.type = type;
		this.validator = (ConfigValidator<Object>) validator;
		this.defaultFactory = defaultFactory;
	}

	@SuppressWarnings("unchecked")
	private <T> T cast(Object value) {
		return (T) type.cast(value);
	}

	public <T> T createDefault() {
		return cast(defaultFactory.get());
	}

	public <T> T getValue() {
		return cast(value);
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