import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Facture {
    private int numero;
    private LocalDate dateEmission = LocalDate.now();
    private List<LigneFacture> lignes = new ArrayList<>();

    public Facture(int numero) {
        this.numero = numero;
    }

    public void ajouterLigne(LigneFacture ligne) {
        lignes.add(ligne);
    }

    public double calculerTotal() {
        double total = 0;
        for (LigneFacture ligne : lignes) {
            total += ligne.sousTotal();
        }
        return total;
    }

    public int getNumero() { return numero; }
    public LocalDate getDateEmission() { return dateEmission; }
    public List<LigneFacture> getLignes() { return lignes; }
}