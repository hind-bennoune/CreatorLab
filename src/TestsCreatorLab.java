import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

// Petits tests sans JUnit : si un test echoue, le programme s'arrete avec une erreur
public class TestsCreatorLab {
    private static int nbTests = 0;

    private static void verifier(boolean ok, String nom) {
        nbTests++;
        if (!ok) throw new AssertionError("ECHEC : " + nom);
        System.out.println("OK " + nbTests + " - " + nom);
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client(1, "Sara", "sara@example.com");
        ProjetCreatif p = new Podcast(1, client, "Podcast", 1, true);
        Studio s = new Studio(1, "A", 4, 120, Set.of(TypeProduction.PODCAST));
        Studio s2 = new Studio(2, "B", 4, 120, Set.of(TypeProduction.PODCAST));
        Equipement mic = new Equipement(1, "Micro", TypeEquipement.MICROPHONE, 20);
        Equipement cam = new Equipement(2, "Camera", TypeEquipement.CAMERA, 40);
        GestionStudio g = new GestionStudio();
        LocalDateTime debut = LocalDateTime.of(2026, 11, 12, 10, 0);
        LocalDateTime fin = debut.plusHours(2);

        // Test 1 : heritage
        verifier(p.getType() == TypeProduction.PODCAST, "Heritage / Override");

        // Test 2 : interface et polymorphisme (120 DH/h pendant 1h30 = 180)
        Facturable prix = s;
        verifier(prix.calculerPrix(1.5) == 180.0, "Interface Facturable / polymorphisme");

        // Test 3 et 4 : reservation valide, studio occupe ensuite
        Reservation r = g.reserver(p, s, List.of(mic, cam), debut, fin);
        verifier(r.estActive(), "Reservation confirmee");
        verifier(!g.verifierDisponibilite(s, debut, fin), "Studio occupe");

        // Test 5 : conflit de studio
        try {
            g.reserver(p, s, List.of(mic, cam), debut, fin);
            throw new AssertionError("Un conflit devait etre detecte");
        } catch (ConflitReservationException attendu) {
            verifier(true, "Exception de conflit de studio");
        }

        // Test 6 : autre studio mais materiel deja utilise
        try {
            g.reserver(p, s2, List.of(mic, cam), debut, fin);
            throw new AssertionError("Un conflit materiel devait etre detecte");
        } catch (MaterielIndisponibleException attendu) {
            verifier(true, "Exception materiel deja utilise");
        }

        // Test 7 et 8 : facture (240 + 40 + 80 = 360) et pas de double facture
        Facture facture = g.genererFacture(r);
        verifier(facture.calculerTotal() == 360.0, "Facture / composition / tarification");
        verifier(g.genererFacture(r) == facture, "Pas de double facture");

        // Test 9 et 10 : creneaux consecutifs et annulation
        Reservation r2 = g.reserver(p, s, List.of(mic, cam), fin, fin.plusHours(1));
        verifier(r2.estActive(), "Creneaux consecutifs autorises");
        g.annuler(r2);
        verifier(g.verifierDisponibilite(s, fin, fin.plusHours(1)), "Annulation libere le studio");

        // Test 11 : equipement en panne
        mic.declarerPanne();
        try {
            g.reserver(p, s2, List.of(mic, cam), fin.plusHours(1), fin.plusHours(2));
            throw new AssertionError("Un equipement en panne devait etre refuse");
        } catch (MaterielIndisponibleException attendu) {
            verifier(true, "Exception materiel en panne");
        }

        System.out.println("Tous les " + nbTests + " tests ont reussi.");
    }
}