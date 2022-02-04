package fr.Adrien1106.BIT_scrabble.util.IO.configuration;

import fr.Adrien1106.BIT_scrabble.util.IO.configuration.exceptions.UnknownChildException;

public class ConfigurationValue<T> implements ConfigurationElement {
	
	protected ConfigurationSection parent;
	protected String key;
	protected T value;
	
	public ConfigurationValue(ConfigurationSection parent, String key, T value) {
		this.parent = parent;
		this.key = key;
		this.value = value;
	}

	@Override
	public ConfigurationSection getParent() {
		return parent;
	}

	@Override
	public ConfigurationElement get(String children) throws UnknownChildException {
		if (!children.equals(key)) throw new UnknownChildException(getPath());
		return this;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getPath() {
		return parent.getPath() + "." + key;
	}
}
