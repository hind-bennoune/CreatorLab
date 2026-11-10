package creatorlab.modele;


// Une ligne est creee pour appartenir a une facture.
public class LigneFacture {
    private final String designation;
    private final int quantite;
    private final double prixUnitaire;

    public LigneFacture(String designation, int quantite, double prixUnitaire) {
        if (designation == null || designation.isBlank() || quantite <= 0
                || !Double.isFinite(prixUnitaire) || prixUnitaire < 0) {
            throw new IllegalArgumentException("Ligne de facture invalide");
        }
        this.designation = designation;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }
    public double calculerSousTotal() { return quantite * prixUnitaire; }
    public String getDesignation() { return designation; }
    public int getQuantite() { return quantite; }
    public double getPrixUnitaire() { return prixUnitaire; }
}
