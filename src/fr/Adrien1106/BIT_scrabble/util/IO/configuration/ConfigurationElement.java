package fr.Adrien1106.BIT_scrabble.util.IO.configuration;

import fr.Adrien1106.BIT_scrabble.util.IO.configuration.exceptions.UnknownChildException;

public interface ConfigurationElement {

	public ConfigurationSection getParent();
	
	public ConfigurationElement get(String children) throws UnknownChildException;
	
	public String getKey();
	
	public String getPath();
}
