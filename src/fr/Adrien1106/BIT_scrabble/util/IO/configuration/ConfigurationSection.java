package fr.Adrien1106.BIT_scrabble.util.IO.configuration;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import fr.Adrien1106.BIT_scrabble.util.IO.configuration.exceptions.ChildAlreadyExistException;
import fr.Adrien1106.BIT_scrabble.util.IO.configuration.exceptions.UnknownChildException;

public class ConfigurationSection implements ConfigurationElement {

	protected ConfigurationSection parent;
	protected String key;
	protected Map<String, ConfigurationElement> childrens;
	
	public ConfigurationSection(ConfigurationSection parent, String key) {
		this.parent = parent;
		this.key = key;
		this.childrens = new LinkedHashMap<>();
	}
	
	@Override
	public ConfigurationSection getParent() {
		return parent;
	}
	
	@Override
	public ConfigurationElement get(String children) throws UnknownChildException {
		if (children.isEmpty()) return this;
		String child = children.split("\\.")[0];
		if (!childrens.containsKey(child)) throw new UnknownChildException(getPath() + "." + children);
		return childrens.get(child).get(children.replace(child + ".", ""));
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getPath() {
		return parent == null? key : parent.getPath() + "." + key;
	}
	
	public void addChild(ConfigurationElement element) throws ChildAlreadyExistException {
		if (childrens.containsKey(element.getKey())) throw new ChildAlreadyExistException(element.getPath());
		childrens.put(element.getKey(), element);
	}
	
	@Override
	public String toString() {
		return key + ":";
	}

	
	public Iterator<ConfigurationElement> childrenIterator() {
		return childrens.values().iterator();
	}
}
