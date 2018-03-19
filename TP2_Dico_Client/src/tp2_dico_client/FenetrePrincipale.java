package tp2_dico_client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.osgi.framework.ServiceReference;

import tp2_dico_en.service.DictionaryService;

public class FenetrePrincipale extends JFrame implements ActionListener,Runnable, Observer{

	private JTextField entry;
	private JLabel rechercheLabel;
	private JLabel result;
	private JList listesMots;
	private JList listeDico;
	private DefaultListModel<String> lm;
	private DefaultListModel<String> ld;
	
	private ArrayList m_refList;
	private HashMap m_refToObjMap = new HashMap();
	
	public FenetrePrincipale(ArrayList refList, HashMap refToObjMap){
		m_refList = refList;
		m_refToObjMap = refToObjMap;
		
	}
	
	private void initiateComponnents() {
		entry = new JTextField(15);
		rechercheLabel = new JLabel("Recherche : ");
		result = new JLabel();
		lm = new DefaultListModel<String>();
		listesMots = new JList<String>(lm);
		listesMots.disable();
		
		ld = new DefaultListModel<String>();
		listeDico = new JList<String>(ld);
		this.update(null, null);
		
		//setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("TextFieldDemo");
        
        entry.addActionListener(this);
		
      //Add Components to this panel.
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
 
        c.fill = GridBagConstraints.HORIZONTAL;
        add(rechercheLabel,c);
        add(entry, c);
        
        JLabel labelDico = new JLabel("Choix Dictionnaire");
        add(labelDico,c);
        
        JScrollPane listDicoScroller = new JScrollPane(listeDico);
        listDicoScroller.setPreferredSize(new Dimension(250, 100));
        add(listDicoScroller,c);
 
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        
        result.setFont(new Font("Serif", Font.PLAIN, 14));
        add(result, c);
        
        add(new JLabel("Propositions "),c);
        
        JScrollPane listScroller = new JScrollPane(listesMots);
        listScroller.setPreferredSize(new Dimension(250, 100));
        add(listScroller, c);
        
        
        setSize(400,300);
        setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(m_refList.size() ==0){
			JOptionPane.showMessageDialog(new JFrame(),
				    "No dictionary detected",
				    "Service Error",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		lm.clear();
		//listesMots.
		
		// If the user entered a blank line, then
        // exit the loop.
        if (entry.getText().length() == 0)
        {
        	return;
        }
        
        String dicoToLookInto = (String) listeDico.getSelectedValue();
     // Check each available dictionary for the current word.
        for (int i = 0; i < m_refList.size(); i++)
        {
            DictionaryService dictionary =
                (DictionaryService) m_refToObjMap.get(m_refList.get(i));

            
            if(dicoToLookInto.equals("All") || dicoToLookInto.equals(dictionary.getLanguage()))
	            if (dictionary.checkWord(entry.getText()))
	            {
	            	result.setText("Correct");
		            result.setForeground(Color.green);
	               return;
	            }
        }
        
        
//        for(int i = 0; i < dico.length; i++)
//	        if (((DictionaryService)dico[i]).checkWord(entry.getText()))
//	        {
//	            result.setText("Correct");
//	            result.setForeground(Color.green);
//	            return;
//	        }
        
        result.setText("Incorrect !");
        result.setForeground(Color.RED);
        
        //DefaultListModel<String> lm = new DefaultListModel<String>();
        //lm.clear();
        
     // Check each available dictionary for the current word.
        for (int i = 0; i < m_refList.size(); i++)
        {
            DictionaryService dictionary =
                (DictionaryService) m_refToObjMap.get(m_refList.get(i));
            
            if(dicoToLookInto.equals("All") || dicoToLookInto.equals(dictionary.getLanguage())){
            	List<String> fullWords = dictionary.completeWord(entry.getText());
		        for (String string : fullWords) {
					lm.addElement(string);
				}
            }
            
        }
//        for(int i = 0; i < dico.length; i++){
//        	List<String> fullWords = ((DictionaryService) dico[i]).completeWord(entry.getText());
//	        for (String string : fullWords) {
//				lm.addElement(string);
//			}
//        }
		
	}

	@Override
	public void run() {
		initiateComponnents();
		
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		ld.clear();
		ld.addElement("All");
		listeDico.setSelectedIndex(0);
		for (int i = 0; i < m_refList.size(); i++)
        {
            DictionaryService dictionary =
                (DictionaryService) m_refToObjMap.get(m_refList.get(i));
            
            ld.addElement(dictionary.getLanguage());
            
        }
		if(ld.size() == 1){
			result.setText("NO DICTIONARY AVAILABLE");
			result.setForeground(Color.RED);
		}
		
	}

	
}
