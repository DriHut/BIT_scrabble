package fr.Adrien1106.BIT_scrabble.util.IO.configuration;

public class ConfigurationString  extends ConfigurationValue<String> {
	
	public ConfigurationString(ConfigurationSection parent, String key, String string) {
		super(parent, key, string);
	}
	
	public String getString() {
		return value;
	}
	
	public void setString(String string) {
		this.value = string;
	}
	
	@Override
	public String toString() {
		return key + ": \"" + value + "\"";
	}

}
