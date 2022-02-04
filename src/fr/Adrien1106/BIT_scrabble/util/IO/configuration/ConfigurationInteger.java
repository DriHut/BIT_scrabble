package fr.Adrien1106.BIT_scrabble.util.IO.configuration;

public class ConfigurationInteger extends ConfigurationValue<Integer> {
	
	public ConfigurationInteger(ConfigurationSection parent, String key, int integer) {
		super(parent, key, integer);
	}
	
	public int getInt() {
		return value;
	}
	
	public void setInt(int integer) {
		this.value = integer;
	}
	
	@Override
	public String toString() {
		return key + ": " + value;
	}
}
