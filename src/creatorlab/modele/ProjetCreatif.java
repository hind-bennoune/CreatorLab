package creatorlab.modele;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// Abstraction : ce qui est commun aux podcasts, videos et shootings.
public abstract class ProjetCreatif {
    private final int id;
    private final Client client;
    private final String titre;
    private String statut = "BROUILLON";

    protected ProjetCreatif(int id, Client client, String titre) {
        if (id <= 0 || client == null || titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Projet invalide");
        }
        this.id = id;
        this.client = client;
        this.titre = titre;
    }

    public abstract String getType();
    public abstract int getNombreParticipants();
    public abstract Set<String> getMaterielRequis();

    // Par defaut, chaque type de materiel exige un seul exemplaire.
    // Les classes enfants peuvent preciser des quantites differentes.
    public Map<String, Integer> getQuantitesMaterielRequis() {
        Map<String, Integer> quantites = new HashMap<>();
        for (String type : getMaterielRequis()) {
            quantites.put(type, 1);
        }
        return Map.copyOf(quantites);
    }

    public void planifier() { statut = "PLANIFIE"; }
    public void remettreEnBrouillon() { statut = "BROUILLON"; }
    public int getId() { return id; }
    public Client getClient() { return client; }
    public String getTitre() { return titre; }
    public String getStatut() { return statut; }
}
