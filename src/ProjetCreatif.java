import java.util.Set;

// Classe abstraite : on ne cree jamais un "projet" generique,
// toujours un Podcast, une Video ou un ShootingPhoto.
public abstract class ProjetCreatif {
    private int id;
    private Client client;
    private String titre;
    private StatutProjet statut = StatutProjet.BROUILLON;

    public ProjetCreatif(int id, Client client, String titre) {
        this.id = id;
        this.client = client;
        this.titre = titre;
    }

    // Methodes que chaque type de projet doit definir
    public abstract TypeProduction getType();
    public abstract int getNombreParticipants();
    public abstract Set<TypeEquipement> getMaterielRequis();

    public void planifier() {
        if (statut == StatutProjet.BROUILLON) {
            statut = StatutProjet.PLANIFIE;
        }
    }

    public int getId() { return id; }
    public Client getClient() { return client; }
    public String getTitre() { return titre; }
    public StatutProjet getStatut() { return statut; }
}