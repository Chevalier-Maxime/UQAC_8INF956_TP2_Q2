package tp2_dico_en.service;

import java.util.List;

/*
 * Apache Felix OSGi tutorial.
**/


/**
 * A simple service interface that defines a dictionary service.
 * A dictionary service simply verifies the existence of a word.
**/
public interface DictionaryService
{
	/**
	 * 
	 * @return the language of the dictionary
	 */
	public String getLanguage();
	
    /**
     * Check for the existence of a word.
     * @param word the word to be checked.
     * @return true if the word is in the dictionary,
     *         false otherwise.
    **/
    public boolean checkWord(String word);
    
    /**
     * Check for the existence of words beginning with @param
     * @param word The begining of words
     * @return All words beginning with @param
     */
    public List<String> completeWord(String word);
}