package creatorlab.modele;


import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

public class Facture {
    // Supplier du cours : ne recoit rien et fournit la date du jour.
    private static final Supplier<LocalDate> DATE_DU_JOUR = () -> LocalDate.now();
    private final int numero;
    private final LocalDate dateEmission;
    private final Reservation reservation;
    private final List<LigneFacture> lignes;

    public Facture(int numero, Reservation reservation, List<LigneFacture> lignes) {
        if (numero <= 0 || reservation == null || lignes == null || lignes.isEmpty()) {
            throw new IllegalArgumentException("Facture invalide");
        }
        for (LigneFacture ligne : lignes) {
            if (ligne == null) throw new IllegalArgumentException("Ligne nulle");
        }
        this.numero = numero;
        this.reservation = reservation;
        this.lignes = List.copyOf(lignes);
        this.dateEmission = DATE_DU_JOUR.get();
    }

    public double calculerTotal() {
        // mapToDouble + sum : total de toutes les lignes, sans boucle manuelle.
        return lignes.stream()
                .mapToDouble(LigneFacture::calculerSousTotal)
                .sum();
    }
    public int getNumero() { return numero; }
    public LocalDate getDateEmission() { return dateEmission; }
    public Reservation getReservation() { return reservation; }
    public List<LigneFacture> getLignes() { return lignes; }
}
