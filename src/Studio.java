import java.util.Set;

public class Studio implements Facturable {
    private int id;
    private String nom;
    private int capacite;
    private double tarifHoraire;
    private Set<TypeProduction> typesAcceptes;

    public Studio(int id, String nom, int capacite, double tarifHoraire,
                  Set<TypeProduction> typesAcceptes) {
        this.id = id;
        this.nom = nom;
        this.capacite = capacite;
        this.tarifHoraire = tarifHoraire;
        this.typesAcceptes = typesAcceptes;
    }

    // Le studio accepte le type de projet ET a assez de places
    public boolean estCompatible(ProjetCreatif projet) {
        return typesAcceptes.contains(projet.getType())
                && capacite >= projet.getNombreParticipants();
    }

    @Override
    public String getLibelle() { return "Studio " + nom; }

    @Override
    public double calculerPrix(double heures) { return tarifHoraire * heures; }

    public int getId() { return id; }
    public String getNom() { return nom; }
}