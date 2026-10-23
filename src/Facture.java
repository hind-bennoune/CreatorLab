import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Facture {
    private final int numero;
    private final LocalDate dateEmission;
    private final Reservation reservation;
    private final List<LigneFacture> lignes;

    public Facture(int numero, Reservation reservation, List<LigneFacture> lignes) {
        if (numero <= 0 || reservation == null || lignes == null || lignes.isEmpty()) {
            throw new IllegalArgumentException("Facture invalide");
        }
        this.numero = numero;
        this.reservation = reservation;
        this.lignes = List.copyOf(lignes);
        this.dateEmission = LocalDate.now();
    }

    public BigDecimal calculerTotal() {
        return lignes.stream().map(LigneFacture::sousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public int getNumero() { return numero; }
    public LocalDate getDateEmission() { return dateEmission; }
    public Reservation getReservation() { return reservation; }
    public List<LigneFacture> getLignes() { return lignes; }
}
