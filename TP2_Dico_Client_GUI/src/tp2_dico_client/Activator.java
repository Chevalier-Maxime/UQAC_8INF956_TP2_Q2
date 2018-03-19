/*
 * Apache Felix OSGi tutorial.
**/

package tp2_dico_client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.io.IOException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import tp2_dico_en.service.*;

/**
 * This class implements a bundle that uses a dictionary
 * service to check for the proper spelling of a word by
 * check for its existence in the dictionary. This bundle
 * uses the first service that it finds and does not monitor
 * the dynamic availability of the service (i.e., it does not
 * listen for the arrival or departure of dictionary services).
 * When starting this bundle, the thread calling the start()
 * method is used to read words from standard input. You can
 * stop checking words by entering an empty line, but to start
 * checking words again you must stop and then restart the bundle.
**/
public class Activator extends Observable implements BundleActivator, ServiceListener
{
	// Bundle's context.
    private BundleContext m_context = null;
	// List of available dictionary service references.
    private ArrayList m_refList = new ArrayList();
    // Maps service references to service objects.
    private HashMap m_refToObjMap = new HashMap();
    
    /**
     * Implements BundleActivator.start(). Queries for
     * all available dictionary services. If none are found it
     * simply prints a message and returns, otherwise it reads
     * words from standard input and checks for their existence
     * from the first dictionary that it finds.
     * (NOTE: It is very bad practice to use the calling thread
     * to perform a lengthy process like this; this is only done
     * for the purpose of the tutorial.)
     * @param context the framework context for the bundle.
    **/
    public void start(BundleContext context) throws Exception
    {
    	m_context = context;
    	
    	synchronized (m_refList)
        {
            // Listen for events pertaining to dictionary services.
            m_context.addServiceListener(this,
                "(&(objectClass=" + DictionaryService.class.getName() + ")" +
                "(Language=*))");

            // Query for all dictionary services.
            ServiceReference[] refs = m_context.getServiceReferences(
                DictionaryService.class.getName(), "(Language=*)");

            // Add any dictionaries to the service reference list.
            if (refs != null)
            {
                for (int i = 0; i < refs.length; i++)
                {
                    // Get the service object.
                    Object service = m_context.getService(refs[i]);

                    // Make that the service is not being duplicated.
                    if ((service != null) &&
                        (m_refToObjMap.get(refs[i]) == null))
                    {
                        // Add to the reference list.
                        m_refList.add(refs[i]);
                        // Map reference to service object for easy look up.
                        m_refToObjMap.put(refs[i], service);
                    }
                }
            }
            
            Runnable r = new FenetrePrincipale(m_refList, m_refToObjMap);
            this.addObserver((FenetrePrincipale)r);
        	Thread t = new Thread(r);
        	t.start();
        }
    	
    	
        // Query for all service references matching any language.
//        ServiceReference[] refs = context.getServiceReferences(
//            DictionaryService.class.getName(), "(Language=*)");
//
//        if (refs != null)
//        {
//        	DictionaryService dictionary =
//                    (DictionaryService) context.getService(refs[0]);
        	
//        	Runnable r = new FenetrePrincipale(m_refList,m_refToObjMap);
//        	Thread t = new Thread(r);
//        	t.start();
        	
        	// Unget the dictionary service.
            //context.ungetService(refs[0]);
            
//            try
//            {
//                System.out.println("Enter a blank line to exit.");
//                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//                String word = "";
//
//                // Loop endlessly.
//                while (true)
//                {
//                    // Ask the user to enter a word.
//                    System.out.print("Enter word: ");
//                    word = in.readLine();
//
//                    // If the user entered a blank line, then
//                    // exit the loop.
//                    if (word.length() == 0)
//                    {
//                        break;
//                    }
//
//                    // First, get a dictionary service and then check
//                    // if the word is correct.
//                    DictionaryService dictionary =
//                        (DictionaryService) context.getService(refs[0]);
//                    if (dictionary.checkWord(word))
//                    {
//                        System.out.println("Correct.");
//                    }
//                    else
//                    {
//                        System.out.println("Incorrect.");
//                        List<String> wordsWhoBeginWith = dictionary.completeWord(word);
//                        if(wordsWhoBeginWith.size() !=0){
//                        	System.out.println("Words who begin with "+word+" :");
//                        	for (String string : wordsWhoBeginWith) {
//								System.out.println(string);
//							}
//                        }
//                        
//                    }
//
//                    // Unget the dictionary service.
//                    context.ungetService(refs[0]);
//                }
//            } catch (IOException ex) { }
//        }
//        else
//        {
//            System.out.println("Couldn't find any dictionary service...");
//        }
    }

    /**
     * Implements BundleActivator.stop(). Does nothing since
     * the framework will automatically unget any used services.
     * @param context the framework context for the bundle.
    **/
    public void stop(BundleContext context)
    {
        // NOTE: The service is automatically released.
    }

	@Override
	 /**
     * Implements ServiceListener.serviceChanged(). Monitors
     * the arrival and departure of dictionary services, adding and
     * removing them from the service reference list, respectively.
     * In the case where no more dictionary services are available,
     * the spell checker service is unregistered. As soon as any dictionary
     * service becomes available, the spell checker service is
     * reregistered.
     * @param event the fired service event.
    **/
    public void serviceChanged(ServiceEvent event)
    {
        synchronized (m_refList)
        {
            // Add the new dictionary service to the service list.
            if (event.getType() == ServiceEvent.REGISTERED)
            {
                // Get the service object.
                Object service = m_context.getService(event.getServiceReference());

                // Make that the service is not being duplicated.
                if ((service != null) &&
                    (m_refToObjMap.get(event.getServiceReference()) == null))
                {
                    // Add to the reference list.
                    m_refList.add(event.getServiceReference());
                    // Map reference to service object for easy look up.
                    m_refToObjMap.put(event.getServiceReference(), service);
                    
                    setChanged();
                    notifyObservers();
                }
                else if (service != null)
                {
                    m_context.ungetService(event.getServiceReference());
                }
            }
            // Remove the departing service from the service list.
            else if (event.getType() == ServiceEvent.UNREGISTERING)
            {
                // Make sure the service is in the list.
                if (m_refToObjMap.get(event.getServiceReference()) != null)
                {
                    // Unget the service object.
                    m_context.ungetService(event.getServiceReference());
                    // Remove service reference.
                    m_refList.remove(event.getServiceReference());
                    // Remove service reference from map.
                    m_refToObjMap.remove(event.getServiceReference());

                    setChanged();
                    notifyObservers();
                }
                
            }
            
        }
    }
}