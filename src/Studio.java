import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Set;

public class Studio implements Facturable {
    private final int id;
    private final String nom;
    private final int capacite;
    private final BigDecimal tarifHoraire;
    private final Set<TypeProduction> typesAcceptes;

    public Studio(int id, String nom, int capacite, BigDecimal tarifHoraire,
                  Set<TypeProduction> typesAcceptes) {
        if (id <= 0 || nom == null || nom.isBlank() || capacite <= 0 ||
                tarifHoraire == null || tarifHoraire.signum() < 0 ||
                typesAcceptes == null || typesAcceptes.isEmpty()) {
            throw new IllegalArgumentException("Studio invalide");
        }
        this.id = id;
        this.nom = nom;
        this.capacite = capacite;
        this.tarifHoraire = tarifHoraire;
        this.typesAcceptes = Set.copyOf(typesAcceptes);
    }

    public boolean estCompatible(ProjetCreatif projet) {
        return typesAcceptes.contains(projet.getType())
                && capacite >= projet.getNombreParticipants();
    }

    @Override public String getLibelle() { return "Studio " + nom; }
    @Override public BigDecimal calculerPrix(Duration duree) {
        return tarifHoraire.multiply(BigDecimal.valueOf(duree.toMinutes()))
                           .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }
    public int getId() { return id; }
    public String getNom() { return nom; }
}
