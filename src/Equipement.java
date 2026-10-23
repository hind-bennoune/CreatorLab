import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

public class Equipement implements Facturable {
    private final int id;
    private final String nom;
    private final TypeEquipement type;
    private EtatEquipement etat = EtatEquipement.OPERATIONNEL;
    private final BigDecimal tarifHoraire;

    public Equipement(int id, String nom, TypeEquipement type, BigDecimal tarifHoraire) {
        if (id <= 0 || nom == null || nom.isBlank() || type == null ||
                tarifHoraire == null || tarifHoraire.signum() < 0) {
            throw new IllegalArgumentException("Equipement invalide");
        }
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.tarifHoraire = tarifHoraire;
    }

    public void declarerPanne() { etat = EtatEquipement.EN_PANNE; }
    public void reparer() { etat = EtatEquipement.OPERATIONNEL; }
    public boolean estOperationnel() { return etat == EtatEquipement.OPERATIONNEL; }
    public int getId() { return id; }
    public TypeEquipement getType() { return type; }
    @Override public String getLibelle() { return "Equipement " + nom; }
    @Override public BigDecimal calculerPrix(Duration duree) {
        return tarifHoraire.multiply(BigDecimal.valueOf(duree.toMinutes()))
                           .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }
}
