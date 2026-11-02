// Interface = contrat commun : tout ce qui est facturable a un libelle et un prix
public interface Facturable {
    String getLibelle();
    double calculerPrix(double heures);
}