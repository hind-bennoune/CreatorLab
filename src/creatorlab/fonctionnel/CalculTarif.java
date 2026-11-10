package creatorlab.fonctionnel;


// Une seule methode abstraite : une lambda peut l'implementer.
@FunctionalInterface
public interface CalculTarif {
    double calculer(double tarifHoraire, double dureeHeures);
}
