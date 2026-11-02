import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Reservation {
    private int id;
    private ProjetCreatif projet;
    private Studio studio;
    private List<Equipement> equipements;
    private LocalDateTime debut;
    private LocalDateTime fin;
    private StatutReservation statut = StatutReservation.CONFIRMEE;
    private Facture facture;

    public Reservation(int id, ProjetCreatif projet, Studio studio,
                       List<Equipement> equipements, LocalDateTime debut, LocalDateTime fin) {
        this.id = id;
        this.projet = projet;
        this.studio = studio;
        this.equipements = equipements;
        this.debut = debut;
        this.fin = fin;
    }

    // Deux creneaux se chevauchent si chacun commence avant la fin de l'autre
    public boolean chevauche(LocalDateTime autreDebut, LocalDateTime autreFin) {
        return debut.isBefore(autreFin) && autreDebut.isBefore(fin);
    }

    public double getDureeHeures() {
        return Duration.between(debut, fin).toMinutes() / 60.0;
    }

    public boolean estActive() { return statut == StatutReservation.CONFIRMEE; }

    public void annuler() {
        if (facture != null) {
            throw new IllegalStateException("Facture deja emise");
        }
        statut = StatutReservation.ANNULEE;
    }

    public int getId() { return id; }
    public ProjetCreatif getProjet() { return projet; }
    public Studio getStudio() { return studio; }
    public List<Equipement> getEquipements() { return equipements; }
    public Facture getFacture() { return facture; }
    public void setFacture(Facture facture) { this.facture = facture; }
    public LocalDateTime getDebut() { return debut; }
    public LocalDateTime getFin() { return fin; }
    public StatutReservation getStatut() { return statut; }
}