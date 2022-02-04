package fr.Adrien1106.BIT_scrabble.util.IO.configuration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import fr.Adrien1106.BIT_scrabble.util.IO.configuration.exceptions.ChildAlreadyExistException;
import fr.Adrien1106.BIT_scrabble.util.IO.configuration.exceptions.UnknownChildException;

public class ConfigurationFile extends ConfigurationSection {
	
	private File file;
	private String name;
	
	public ConfigurationFile(String name) {
		super(null, "root");
		this.name = name;
		
		URL url = ConfigurationFile.class.getResource("/");
		try {
			File parent_dir = new File(url.toURI());
			file = new File(parent_dir, "/configs/" + name + ".yml");
			file.getParentFile().mkdirs();
			if (!file.exists())
				saveFromDefault();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public void saveFromDefault() {
		try ( 
				InputStream in = getClass().getResourceAsStream("/" + name + ".yml");
				OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
				) {
	        byte[] buffer = new byte[1024];
	        int lengthRead;
	        while ((lengthRead = in.read(buffer)) > 0) {
	            out.write(buffer, 0, lengthRead);
	            out.flush();
	        }
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private ConfigurationElement getConfigurationElement(String path) {
		if (!childrens.containsKey(path.split("\\.")[0])) return null;
		try {
			return childrens.get(path.split("\\.")[0]).get(path.replace(path.split("\\.")[0] + ".", ""));
		} catch (UnknownChildException e) { e.printStackTrace(); }
		return null;
	}
	
	public void load() {
		Scanner reader;
		try {
			reader = new Scanner(file);
			
			String indent = "";
			ConfigurationSection parent = this;
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				if (line.replace(" ", "").startsWith("#")) continue;
				
				while (!line.startsWith(indent)) { // indent back as much as needed
					parent = parent.getParent();
					indent = indent.replaceFirst("  ", "");
				}
				
				if (line.endsWith(":")) { // section
					parent = new ConfigurationSection(parent, line.replace(" ", "").replace(":", ""));
					if (parent.getParent() != null) parent.getParent().addChild(parent);
					indent += "  ";
					continue;
				}
				
				if (line.contains(":")) {
					if (line.replace(line.split(":")[0] + ": ", "").startsWith("\"")) { // String configuration
						parent.addChild(new ConfigurationString(parent, line.replace(" ", "").split(":")[0], getStringValue(line)));
					} else { // integer configuration
						parent.addChild(new ConfigurationInteger(parent, line.replace(" ", "").split(":")[0], getIntegerValue(line)));
					}
				}
			}
			
			reader.close();
		} catch (FileNotFoundException | ChildAlreadyExistException e) { e.printStackTrace(); }
	}
	
	private int getIntegerValue(String line) {
		line = line.replace(line.split(":")[0] + ": ", "");
		return Integer.valueOf(line.split(" ")[0]);
	}

	private String getStringValue(String line) {
		line = line.replace(line.split(":")[0] + ": ", "").replaceFirst("\"", "");
		int i;
		boolean skip = false;
		for (i = 0; i < line.length(); i++) {
			if (skip) {
				skip = false;
				continue;
			}
			if (line.substring(i, i+1).equals("\\")) skip = true;
			if (line.substring(i, i+1).equals("\"")) break;
		}
		return line.substring(0, i).replace("\\\"", "\"");
	}

	public void save() {
		FileWriter writer;
		try {
			writer = new FileWriter(file);
			Iterator<ConfigurationElement> root_iterator = childrens.values().iterator();
			List<Iterator<ConfigurationElement>> iterators = new ArrayList<>(Arrays.asList(root_iterator));
			int i = 0;
			String indent = "";
			ConfigurationElement element;
			while (root_iterator.hasNext()) {
				element = iterators.get(i).next();
				writer.write(indent + element.toString().replace("\n", "\n" + indent) + "\n");
				if (element instanceof ConfigurationSection) {
					i++;
					indent += "  ";
					iterators.add(((ConfigurationSection) element).childrenIterator());
				}
				while (!iterators.get(i).hasNext() && i != 0) {
					iterators.remove(i);
					i--;
					indent = indent.replaceFirst("  ", "");
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public String getString(String path) {
		ConfigurationElement element = getConfigurationElement(path);
		if (element instanceof ConfigurationString) return ((ConfigurationString) element).getString();
		return null;
	}
	
	public int getInt(String path) {
		ConfigurationElement element = getConfigurationElement(path);
		if (element instanceof ConfigurationInteger) return ((ConfigurationInteger) element).getInt();
		return 0;
	}
	
	public void putString(String path, String string) {
		ConfigurationElement element = getConfigurationElement(path);
		if (element instanceof ConfigurationString) ((ConfigurationString) element).setString(string);
	}
	
	public void putInt(String path, int integer) {
		ConfigurationElement element = getConfigurationElement(path);
		if (element instanceof ConfigurationInteger) ((ConfigurationInteger) element).setInt(integer);
	}
	
	public ConfigurationSection getSection(String path) {
		ConfigurationElement element = getConfigurationElement(path);
		if (element instanceof ConfigurationSection) return (ConfigurationSection) element;
		return null;
	}

	public List<String> getStringList(String path) {
		List<String> list = new ArrayList<>();
		String[] component = path.split("\\.");
		Scanner reader;
		try {
			reader = new Scanner(file);
			
			for (int i = 0; i < component.length; i++) component[i] += ":";
			String indent = "";
			int i = 0;
			
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				if (line.replace(" ", "").startsWith("#")) continue;
				if (!line.startsWith(indent)) {
					reader.close();
					break;
				}
				if (i == component.length && line.replace(indent + "- ", "").length() > 0) {
					list.add(line.replace(indent + "- ", ""));
				} else if (line.startsWith(indent + component[i])) {
					i++;
					indent += "  ";
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {}
		if (list.isEmpty()) return null;
		return list;
	}
	
	public List<Integer> getIntList(String path) {
		List<String> strings = getStringList(path);
		if (strings == null) return null;
		List<Integer> list = new ArrayList<>();
		try {
			for (String string: strings) list.add(Integer.valueOf(string));
			return list;
		} catch (NumberFormatException e) {}
		return null;
	}

	public static void main(String[] args) {
		ConfigurationFile config = new ConfigurationFile("ServerConfig");
		config.saveFromDefault();
		config.load();
		System.out.println(config.getString("client.test.sub-test"));
		System.out.println(config.getString("client.test.sub-test-1"));
		System.out.println(config.getString("client.test-2"));
		System.out.println(config.getString("client.test-3.sub-test-3"));
		System.out.println(config.getInt("testings.list"));
		config.putString("client.test.sub-test", "changed");
		System.out.println(config.getString("client.test.sub-test"));
		System.out.println(config.getString("list-3"));
		config.putInt("testings.list",5);
		System.out.println(config.getInt("testings.list"));
		config.save();
	}

}
