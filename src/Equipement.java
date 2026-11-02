public class Equipement implements Facturable {
    private int id;
    private String nom;
    private TypeEquipement type;
    private EtatEquipement etat = EtatEquipement.OPERATIONNEL;
    private double tarifHoraire;

    public Equipement(int id, String nom, TypeEquipement type, double tarifHoraire) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.tarifHoraire = tarifHoraire;
    }

    public void declarerPanne() { etat = EtatEquipement.EN_PANNE; }
    public void reparer() { etat = EtatEquipement.OPERATIONNEL; }
    public boolean estOperationnel() { return etat == EtatEquipement.OPERATIONNEL; }

    @Override
    public String getLibelle() { return "Equipement " + nom; }

    @Override
    public double calculerPrix(double heures) { return tarifHoraire * heures; }

    public int getId() { return id; }
    public TypeEquipement getType() { return type; }
}