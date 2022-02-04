package fr.Adrien1106.BIT_scrabble.util.IO.configuration;

import java.util.ArrayList;
import java.util.List;

import fr.Adrien1106.BIT_scrabble.util.IO.configuration.exceptions.UnknownChildException;

public class ConfigurationList<T> implements ConfigurationElement {
	
	protected ConfigurationSection parent;
	protected String key;
	protected List<T> values;
	
	public ConfigurationList(ConfigurationSection parent, String key) {
		this.parent = parent;
		this.key = key;
		this.values = new ArrayList<>();
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
