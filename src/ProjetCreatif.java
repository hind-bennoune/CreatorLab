import java.util.Set;

public abstract class ProjetCreatif {
    private final int id;
    private final Client client;
    private final String titre;
    private StatutProjet statut = StatutProjet.BROUILLON;

    protected ProjetCreatif(int id, Client client, String titre) {
        if (id <= 0 || client == null || titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Projet invalide");
        }
        this.id = id;
        this.client = client;
        this.titre = titre;
    }

    public abstract TypeProduction getType();
    public abstract int getNombreParticipants();
    public abstract Set<TypeEquipement> getMaterielRequis();

    public void planifier() {
        if (statut == StatutProjet.BROUILLON) statut = StatutProjet.PLANIFIE;
    }

    public int getId() { return id; }
    public Client getClient() { return client; }
    public String getTitre() { return titre; }
    public StatutProjet getStatut() { return statut; }
}
