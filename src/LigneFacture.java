import java.math.BigDecimal;

public class LigneFacture {
    private final String designation;
    private final int quantite;
    private final BigDecimal prixUnitaire;

    public LigneFacture(String designation, int quantite, BigDecimal prixUnitaire) {
        if (designation == null || designation.isBlank() || quantite <= 0 ||
                prixUnitaire == null || prixUnitaire.signum() < 0) {
            throw new IllegalArgumentException("Ligne facture invalide");
        }
        this.designation = designation;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }
    public BigDecimal sousTotal() { return prixUnitaire.multiply(BigDecimal.valueOf(quantite)); }
    public String getDesignation() { return designation; }
    public int getQuantite() { return quantite; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
}
