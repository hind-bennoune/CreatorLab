package creatorlab.modele;


// Interface classique : deux methodes abstraites, pas une interface fonctionnelle.
public interface Facturable {
    String getLibelle();
    double calculerPrix(double dureeHeures);
}
