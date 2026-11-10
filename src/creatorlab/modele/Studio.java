package creatorlab.modele;


import creatorlab.fonctionnel.CalculTarif;
import java.util.Set;

public class Studio implements Facturable {
    private static final CalculTarif TARIF_SIMPLE = (tarif, heures) -> tarif * heures;
    private final int id;
    private final String nom;
    private final int capacite;
    private final double tarifHoraire;
    private final Set<String> typesAcceptes;

    public Studio(int id, String nom, int capacite,
                  double tarifHoraire, Set<String> typesAcceptes) {
        if (id <= 0 || nom == null || nom.isBlank() || capacite <= 0
                || !Double.isFinite(tarifHoraire) || tarifHoraire < 0
                || typesAcceptes == null || typesAcceptes.isEmpty()) {
            throw new IllegalArgumentException("Studio invalide");
        }
        this.id = id;
        this.nom = nom;
        this.capacite = capacite;
        this.tarifHoraire = tarifHoraire;
        this.typesAcceptes = Set.copyOf(typesAcceptes);
    }

    public boolean estCompatible(ProjetCreatif projet) {
        return projet != null && typesAcceptes.contains(projet.getType())
                && capacite >= projet.getNombreParticipants();
    }

    @Override public String getLibelle() { return "Studio " + nom; }
    @Override public double calculerPrix(double dureeHeures) {
        if (!Double.isFinite(dureeHeures) || dureeHeures <= 0) {
            throw new IllegalArgumentException("Duree invalide");
        }
        return TARIF_SIMPLE.calculer(tarifHoraire, dureeHeures);
    }
    public int getId() { return id; }
    public String getNom() { return nom; }
    public int getCapacite() { return capacite; }
}
