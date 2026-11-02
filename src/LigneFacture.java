public class LigneFacture {
    private String designation;
    private int quantite;
    private double prixUnitaire;

    public LigneFacture(String designation, int quantite, double prixUnitaire) {
        this.designation = designation;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    public double sousTotal() { return prixUnitaire * quantite; }

    public String getDesignation() { return designation; }
    public int getQuantite() { return quantite; }
    public double getPrixUnitaire() { return prixUnitaire; }
}