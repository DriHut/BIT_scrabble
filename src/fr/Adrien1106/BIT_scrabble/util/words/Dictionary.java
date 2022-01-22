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
	
	public static void loadFromRessource() {
        InputStream resourceStream = Dictionary.class.getResourceAsStream("/dictionary/collins_scrabble_words_2019.txt");
        load(resourceStream);
	}
	
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
	
	public static boolean isWord(String word) {
		return glossary.contains(word.toUpperCase());
	}
	
	@Deprecated
	public static List<String> getWords(String letters) {
		for (String word: glossary) {
			if (Arrays.asList(word.toCharArray()).containsAll(Arrays.asList(letters.toCharArray())) && word.length() == letters.length()) {
				
			}
		}
		return null;
	}
}
