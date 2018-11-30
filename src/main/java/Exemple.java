import java.util.ArrayList;

import requeterRezo.Filtre;
import requeterRezo.Mot;
import requeterRezo.RequeterRezoDump;
import requeterRezo.Voisin;

public class Exemple {

	public static void main(String[] args) {
		//D�lais � partir duquel un mot du cache doit �tre redemander au serveur
		String delais_peremption = "7j";
		//Taille maximale du cache
		String taille_max = "100mo";
		RequeterRezoDump rezo = new RequeterRezoDump(delais_peremption, taille_max);		
		//requ�te basique
		Mot chaton = rezo.requete("chaton");
		//Une requete peut renvoyer null si le serveur ne r�pond pas ou que le mot n'existe pas
		if(chaton != null) {
			System.out.println(chaton);		
			Mot.Ecrire(chaton, "chaton.txt");
		}

		//requ�te avec filtre sur le type de relation ainsi que sur la direction
		Mot chatAvecFiltre = rezo.requete("chat", "r_isa", Filtre.FiltreRelationsEntrantes);
		System.out.println("Les voisins sortants de \"chat\" pour la relation \"isa\" sont : ");
		//Uniquement les relations sortantes car les relations entrantes ont �t� filtr�es 
		for(Voisin voisin : chatAvecFiltre.getRelations_sortantes("r_isa")) {
			System.out.println("\t"+voisin.toString());
		}

		//Possibilit� de filtrer plusieurs types de relations
		//Fonctionne avec le nom des relations ou leur id (ex. 4=r_lemma)
		ArrayList<Mot> chatAvecMultiplesFiltres = rezo.requeteMultiple("chat", "r_isa;r_has_part;4");
		System.out.println("Le voisinage de chat avec les relations r_isa, r_has_part et r_lemma ont respectivement : ");
		for(Mot chat : chatAvecMultiplesFiltres) {
			//traitement			
			System.out.println("\t"+chat.getVoisinage().size() + " �l�ments");
		}

		//� ne pas oublier : essentiel pour la gestion du cache !
		rezo.sauvegarder();
	}

}
