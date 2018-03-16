package tp2_dictionnaire;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DictionaryImpl implements Dictionary {

	private List<String> fWords; //= Files.readAllLines(new File("liste_francais.txt").toPath(), Charset.defaultCharset());//Arrays.asList("Bienvenue", "Au", "Cours");
	private String fLanguage = "fr_FR";

	public DictionaryImpl(URL dico) {
		fWords = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(dico.openConnection().getInputStream()));
			while(br.ready()) {
				fWords.add(br.readLine());
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public String getLanguage() {
		return fLanguage;
	}

	public boolean check(String word) {
		return fWords.contains(word);
	}

	public String toString() {
		return fLanguage;
	}

}
