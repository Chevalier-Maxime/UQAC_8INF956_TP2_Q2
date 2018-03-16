package tp2_dico_en;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/*
 * Apache Felix OSGi tutorial.
**/


//Dictionnaires text dispo ici : http://www.gwicks.net/dictionaries.htm

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceEvent;

import tp2_dico_en.service.DictionaryService;

/**
 * This class implements a simple bundle that uses the bundle
 * context to register an English language dictionary service
 * with the OSGi framework. The dictionary service interface is
 * defined in a separate class file and is implemented by an
 * inner class.
**/
public class Activator implements BundleActivator
{
    /**
     * Implements BundleActivator.start(). Registers an
     * instance of a dictionary service using the bundle context;
     * attaches properties to the service that can be queried
     * when performing a service look-up.
     * @param context the framework context for the bundle.
    **/
    public void start(BundleContext context)
    {
        Hashtable<String, String> props = new Hashtable<String, String>();
        props.put("Language", "English");
        
        URL dico = context.getBundle().getResource("engmix.txt");
        context.registerService(
            DictionaryService.class.getName(), new DictionaryImpl(dico), props);
    }

    /**
     * Implements BundleActivator.stop(). Does nothing since
     * the framework will automatically unregister any registered services.
     * @param context the framework context for the bundle.
    **/
    public void stop(BundleContext context)
    {
        // NOTE: The service is automatically unregistered.
    }

    /**
     * A private inner class that implements a dictionary service;
     * see DictionaryService for details of the service.
    **/
    private static class DictionaryImpl implements DictionaryService
    {
        // The set of words contained in the dictionary.
        List<String> m_dictionary = new ArrayList<String>();

        
        public DictionaryImpl(URL dicoPath){
        	try {
    			BufferedReader br = new BufferedReader(new InputStreamReader(dicoPath.openConnection().getInputStream()));
    			while(br.ready()) {
    				m_dictionary.add(br.readLine());
    			}
    			br.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
        /**
         * Implements DictionaryService.checkWord(). Determines
         * if the passed in word is contained in the dictionary.
         * @param word the word to be checked.
         * @return true if the word is in the dictionary,
         *         false otherwise.
        **/
        public boolean checkWord(String word)
        {
        	return m_dictionary.contains(word);
        }
		
        @Override
		public List<String> completeWord(String word) {
        	LinkedList<String> result = new LinkedList<String>();
        	
        	for (String dictionary_entry : m_dictionary) {
				if (dictionary_entry.startsWith(word))
					result.add(dictionary_entry);
			}
        	
			return result;
		}
        
        
    }
}
