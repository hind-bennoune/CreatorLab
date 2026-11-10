package creatorlab.modele;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

// Entite : stocke une reservation ; les validations globales sont dans GestionStudio.
public class Reservation {
    private final int numero;
    private final ProjetCreatif projet;
    private final Studio studio;
    private final List<Equipement> equipements;
    private final LocalDateTime debut;
    private final LocalDateTime fin;
    private boolean annulee = false;
    private Facture facture;

    public Reservation(int numero, ProjetCreatif projet, Studio studio,
                       List<Equipement> equipements, LocalDateTime debut, LocalDateTime fin) {
        if (numero <= 0 || projet == null || studio == null || equipements == null
                || debut == null || fin == null
                || !debut.isBefore(fin)) {
            throw new IllegalArgumentException("Reservation invalide");
        }
        for (Equipement equipement : equipements) {
            if (equipement == null) throw new IllegalArgumentException("Equipement nul");
        }
        this.numero = numero;
        this.projet = projet;
        this.studio = studio;
        this.equipements = List.copyOf(equipements);
        this.debut = debut;
        this.fin = fin;
    }

    public boolean chevauche(LocalDateTime autreDebut, LocalDateTime autreFin) {
        return debut.isBefore(autreFin) && autreDebut.isBefore(fin);
    }
    public double getDureeHeures() {
        return Duration.between(debut, fin).toMinutes() / 60.0;
    }
    public boolean estActive() { return !annulee; }
    public void annuler() {
        if (facture != null) throw new IllegalStateException("Reservation deja facturee");
        annulee = true;
    }
    public void associerFacture(Facture facture) {
        if (facture == null || this.facture != null || annulee) {
            throw new IllegalStateException("Facture non associable");
        }
        this.facture = facture;
    }
    public int getNumero() { return numero; }
    public ProjetCreatif getProjet() { return projet; }
    public Studio getStudio() { return studio; }
    public List<Equipement> getEquipements() { return equipements; }
    public LocalDateTime getDebut() { return debut; }
    public LocalDateTime getFin() { return fin; }
    public Facture getFacture() { return facture; }
}
