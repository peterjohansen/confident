package com.actram.configent.config;

import java.util.function.Function;

/**
 * 
 * 
 * @author Peter André Johansen
 */
public class ConfigBuilder {

	/** The type of the source input. */
	private Class<?> oftype;

	/** The type of the final value. */
	private Class<?> toType;
	
	/** */
//	private 

	public ConfigBuilder addItem(String name) {
		// TODO
		return this;
	}

	public Config build() {
		return new Config(null, null);
	}

	public <T> ConfigBuilder ofType(Class<T> type) {
		// TODO
		return this;
	}

	public ConfigBuilder sameAs(String itemName) {
		// TODO
		return this;
	}

	public ConfigBuilder sameAsPrevious() {
		// TODO
		return this;
	}

	public <T> ConfigBuilder toType(Class<T> type, Function<?, T> mapper) {
		// TODO
		return this;
	}

	public ConfigBuilder withConstraints(ConfigValidator<?> val) {
		// TODO
		return this;
	}
}