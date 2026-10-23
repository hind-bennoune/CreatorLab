import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

// Petits tests autonomes sans dependance JUnit, pour commencer facilement.
public class TestsCreatorLab {
    private static int nbTests = 0;
    private static void verifier(boolean ok, String nom) {
        nbTests++;
        if (!ok) throw new AssertionError("ECHEC : " + nom);
        System.out.println("OK " + nbTests + " - " + nom);
    }
    public static void main(String[] args) throws Exception {
        Client client = new Client(1,"Sara","sara@example.com");
        ProjetCreatif p = new Podcast(1,client,"Podcast",1,true);
        Studio s = new Studio(1,"A",4,new BigDecimal("120"),Set.of(TypeProduction.PODCAST));
        Studio s2 = new Studio(2,"B",4,new BigDecimal("120"),Set.of(TypeProduction.PODCAST));
        Equipement mic = new Equipement(1,"Micro",TypeEquipement.MICROPHONE,new BigDecimal("20"));
        Equipement cam = new Equipement(2,"Camera",TypeEquipement.CAMERA,new BigDecimal("40"));
        GestionStudio g = new GestionStudio();
        LocalDateTime debut = LocalDateTime.of(2026,11,12,10,0);
        LocalDateTime fin = debut.plusHours(2);
        verifier(p instanceof ProjetCreatif && p.getType()==TypeProduction.PODCAST,"Heritage / Override");
        Facturable prix = s;
        verifier(prix.calculerPrix(Duration.ofMinutes(90)).compareTo(new BigDecimal("180.00"))==0,
                 "Interface Facturable / polymorphisme");
        Reservation r = g.reserver(p,s,List.of(mic,cam),debut,fin);
        verifier(r.estActive(),"Reservation confirmee");
        verifier(!g.verifierDisponibilite(s,debut,fin),"Studio occupe");
        try {
            g.reserver(p,s,List.of(mic,cam),debut,fin);
            throw new AssertionError("Un conflit devait etre detecte");
        } catch (ConflitReservationException attendu) {
            verifier(true,"Exception de conflit de studio");
        }
        try {
            g.reserver(p,s2,List.of(mic,cam),debut,fin);
            throw new AssertionError("Un conflit materiel devait etre detecte");
        } catch (MaterielIndisponibleException attendu) {
            verifier(true,"Exception materiel deja utilise");
        }
        Facture facture = g.genererFacture(r);
        verifier(facture.calculerTotal().compareTo(new BigDecimal("360.00"))==0,
                 "Facture / composition / tarification");
        verifier(g.genererFacture(r)==facture,"Pas de double facture");
        Reservation r2 = g.reserver(p,s,List.of(mic,cam),fin,fin.plusHours(1));
        verifier(r2.estActive(),"Creneaux consecutifs autorises");
        g.annuler(r2);
        verifier(g.verifierDisponibilite(s,fin,fin.plusHours(1)),"Annulation libere le studio");
        mic.declarerPanne();
        try {
            g.reserver(p,s2,List.of(mic,cam),fin.plusHours(1),fin.plusHours(2));
            throw new AssertionError("Un equipement en panne devait etre refuse");
        } catch (MaterielIndisponibleException attendu) {
            verifier(true,"Exception materiel en panne");
        }
        System.out.println("Tous les " + nbTests + " tests ont reussi.");
    }
}
