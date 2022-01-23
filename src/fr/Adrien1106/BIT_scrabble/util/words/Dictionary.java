package fr.Adrien1106.BIT_scrabble.util.words;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dictionary {
	
	private static List<String> glossary;
	
	/**
	 * Read the dictionary from the default resource
	 */
	public static void loadFromRessource() {
        InputStream resourceStream = Dictionary.class.getResourceAsStream("/dictionary/collins_scrabble_words_2019.txt");
        load(resourceStream);
	}
	
	/**
	 * Load a list of word from an input stream
	 * @param in - input stream to use for the dictionary
	 */
	private static void load(InputStream in) {
		glossary = new ArrayList<>();
		
		try {
	        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                String[] splitLine = line.split("\t");
	
	                if (splitLine.length == 2) {
	                	glossary.add(splitLine[0]);
	                }
	            }
	        }
        } catch(IOException e){
            System.out.println("Could not load dictionary: " + e.getMessage());
            e.printStackTrace();
        }
        
	}
	
	/**
	 * Checks if word is in the loaded dictionary
	 * @param word - word to be checked
	 * @return if the word is part of the dictionary
	 */
	public static boolean isWord(String word) {
		return glossary.contains(word.toUpperCase());
	}
	
	/**
	 * Determine what possible words can be found from the string of given letters
	 * @param letters - letters to use to make word
	 * @return a list of possible words
	 */
	@Deprecated
	public static List<String> getWords(String letters) {
		for (String word: glossary) {
			if (Arrays.asList(word.toCharArray()).containsAll(Arrays.asList(letters.toCharArray())) && word.length() == letters.length()) {
				
			}
		}
		return null;
	}
}
