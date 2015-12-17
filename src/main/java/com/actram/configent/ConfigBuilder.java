package com.actram.configent;

/**
 * 
 *
 * @author Peter André Johansen
 */
public class ConfigBuilder {
	private final Config config = new Config();

	public <T> ConfigBuilder needs(String key, Class<T> type, ConfigValidator<T> validator) {
		
		return this;
	}

	public Config build() {
		return this.config;
	}
}