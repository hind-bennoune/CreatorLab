import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Reservation {
    private final int id;
    private final ProjetCreatif projet;
    private final Studio studio;
    private final List<Equipement> equipements;
    private final LocalDateTime debut;
    private final LocalDateTime fin;
    private StatutReservation statut = StatutReservation.CONFIRMEE;
    private Facture facture;

    public Reservation(int id, ProjetCreatif projet, Studio studio, List<Equipement> equipements,
                       LocalDateTime debut, LocalDateTime fin) {
        if (id <= 0 || projet == null || studio == null || equipements == null ||
                equipements.stream().anyMatch(e -> e == null) || debut == null || fin == null || !debut.isBefore(fin)) {
            throw new IllegalArgumentException("Reservation invalide");
        }
        this.id = id;
        this.projet = projet;
        this.studio = studio;
        this.equipements = List.copyOf(equipements);
        this.debut = debut;
        this.fin = fin;
    }

    public boolean chevauche(LocalDateTime autreDebut, LocalDateTime autreFin) {
        return debut.isBefore(autreFin) && autreDebut.isBefore(fin);
    }
    public Duration getDuree() { return Duration.between(debut, fin); }
    public boolean estActive() { return statut == StatutReservation.CONFIRMEE; }
    public void annuler() {
        if (facture != null) throw new IllegalStateException("Facture deja emise");
        statut = StatutReservation.ANNULEE;
    }
    void associerFacture(Facture nouvelleFacture) {
        if (facture != null) throw new IllegalStateException("Facture deja emise");
        facture = nouvelleFacture;
    }
    public Facture getFacture() { return facture; }
    public int getId() { return id; }
    public ProjetCreatif getProjet() { return projet; }
    public Studio getStudio() { return studio; }
    public List<Equipement> getEquipements() { return equipements; }
    public LocalDateTime getDebut() { return debut; }
    public LocalDateTime getFin() { return fin; }
    public StatutReservation getStatut() { return statut; }
}
