package creatorlab.modele;


import creatorlab.fonctionnel.CalculTarif;

public class Equipement implements Facturable {
    private static final CalculTarif TARIF_SIMPLE = (tarif, heures) -> tarif * heures;
    private final int id;
    private final String nom;
    private final String type;
    private boolean operationnel = true;
    private final double tarifHoraire;

    public Equipement(int id, String nom, String type, double tarifHoraire) {
        if (id <= 0 || nom == null || nom.isBlank() || type == null || type.isBlank()
                || !Double.isFinite(tarifHoraire) || tarifHoraire < 0) {
            throw new IllegalArgumentException("Equipement invalide");
        }
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.tarifHoraire = tarifHoraire;
    }

    public void declarerPanne() { operationnel = false; }
    public void reparer() { operationnel = true; }
    public boolean estOperationnel() { return operationnel; }
    public int getId() { return id; }
    public String getType() { return type; }
    @Override public String getLibelle() { return "Equipement " + nom; }
    @Override public double calculerPrix(double dureeHeures) {
        if (!Double.isFinite(dureeHeures) || dureeHeures <= 0) {
            throw new IllegalArgumentException("Duree invalide");
        }
        return TARIF_SIMPLE.calculer(tarifHoraire, dureeHeures);
    }
}
