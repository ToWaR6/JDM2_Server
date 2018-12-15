package Requester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import javax.annotation.PreDestroy;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import requeterRezo.Mot;
import requeterRezo.RequeterRezoDump;
import requeterRezo.Voisin;

@CrossOrigin
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
	
	@RequestMapping("/diko")
	public static Mot request(@RequestParam(value="mot") String mot) {
		return rezo.requete(mot);
	}

	@RequestMapping("/diko/relation")
	public static ArrayList<Voisin> requestRelation(@RequestParam(value="mot") String mot, @RequestParam(value="relation") String relation) throws Exception {
		return rezo.requete(mot).getRelations_sortantes(relation);
	}

	@RequestMapping("/diko/word")
	public static List<String> requestWord(@RequestParam(value="begin") String begin) throws Exception {
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
		return list.subList(0, 25);
	}
	
	@RequestMapping("/diko/definition")
	public static ResultDefinition requestDescription(@RequestParam(value="mot") String mot) {
		Mot word = rezo.requete(mot);
		if (word != null) {
			String def = word.getDefinition();
			if (def.startsWith("Pas de définition")) {
				return new ResultDefinition(false, word.getRelations_sortantes("r_raff_sem"));
			} else {
				return new ResultDefinition(true, def);
			}
		}
		return null;
	}
	
	
	public static class ResultDefinition {
		private boolean exist;
		private Object value;
		
		public ResultDefinition() {
			super();
		}
		
		public ResultDefinition(boolean exist, Object value) {
			super();
			this.exist = exist;
			this.value = value;
		}
		
		public boolean isExist() {
			return exist;
		}
		
		public void setExist(boolean exist) {
			this.exist = exist;
		}
		
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
		
	}
	
}
