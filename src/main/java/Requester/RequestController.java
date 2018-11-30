package Requester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.annotation.PreDestroy;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import requeterRezo.Mot;
import requeterRezo.RequeterRezoDump;
import requeterRezo.Voisin;

@RestController
public class RequestController {

	private static String delais_peremption = "7j";
	private static String taille_max = "100mo";
	private static RequeterRezoDump rezo = new RequeterRezoDump(delais_peremption, taille_max);
	private static HashMap<Character, TreeSet<String>> mapWord; 


	public RequestController() {
		super();

		if (mapWord == null) {
			mapWord = new HashMap<Character, TreeSet<String>>();
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("RequeterRezo"+File.separator+"words.txt"), "UTF-8"));
				String line;
				while ((line = br.readLine()) != null) {
					String[] split = line.split(";");
					if (split.length > 1) {
						String word = split[1].toLowerCase();
						if (!word.equals("")) {
							Character initial = word.charAt(0);
							if (!mapWord.containsKey(initial)) {
								mapWord.put(initial, new TreeSet<String>());
							}
							mapWord.get(initial).add(word);
						}	
					}
				}
				br.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	@PreDestroy
	public static void cleanUp() throws Exception {
		rezo.sauvegarder();
	}

	public static Mot getWord(String mot) {
		String path = "cache"+File.separator+mot+".txt";
		Mot word = null;
		if (Files.exists(Paths.get(path))) {
			word = Mot.lire(path);
		} else {
			word = rezo.requete(mot);
			if(word != null) {
				Mot.Ecrire(word, path);
			}
		}
		return word;
	}

	@RequestMapping("/diko")
	public static Mot request(@RequestParam(value="q") String mot) {
		return getWord(mot);
	}

	@RequestMapping("/diko/relation")
	public static ArrayList<Voisin> requestRelation(@RequestParam(value="q") String mot, @RequestParam(value="r") String relation) throws Exception {
		return getWord(mot).getRelations_sortantes(relation);
	}

	@RequestMapping("/diko/word")
	public static ArrayList<String> requestWord(@RequestParam(value="q") String begin) throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		boolean b = false;
		for (String str : mapWord.get(begin.charAt(0))) {
			if (str.startsWith(begin)) {
				list.add(str);
				b = true;
			} else if (b) {
				break;
			}	
		}	
		return list;
	}
	
}
