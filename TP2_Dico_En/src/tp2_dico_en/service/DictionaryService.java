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
     * Check for the existence of a word.
     * @param word the word to be checked.
     * @return true if the word is in the dictionary,
     *         false otherwise.
    **/
    public boolean checkWord(String word);
    
    
    public List<String> completeWord(String word);
}