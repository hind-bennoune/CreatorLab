import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        // Donnees de depart
        Client youssef = new Client(1, "Youssef", "youssef@example.com");
        ProjetCreatif podcast = new Podcast(1, youssef, "TechTalk #12", 2, true);
        Studio studioB = new Studio(1, "B", 6, 300,
                Set.of(TypeProduction.PODCAST, TypeProduction.VIDEO));
        Equipement camera = new Equipement(1, "Camera Sony", TypeEquipement.CAMERA, 100);
        Equipement micro1 = new Equipement(2, "Micro 1", TypeEquipement.MICROPHONE, 20);
        Equipement micro2 = new Equipement(3, "Micro 2", TypeEquipement.MICROPHONE, 20);
        Equipement micro3 = new Equipement(4, "Micro 3", TypeEquipement.MICROPHONE, 20);

        GestionStudio gestion = new GestionStudio();
        LocalDateTime debut = LocalDateTime.of(2026, 11, 10, 14, 0);
        LocalDateTime fin = debut.plusHours(2);

        try {
            // 1. Reservation valide + facture
            Reservation r = gestion.reserver(podcast, studioB,
                    List.of(camera, micro1, micro2, micro3), debut, fin);
            System.out.println("Reservation N° " + r.getId() + " CONFIRMEE");
            System.out.println("Projet : " + podcast.getTitre() + " (" + podcast.getType() + ")");

            Facture f = gestion.genererFacture(r);
            System.out.println("Facture N° " + f.getNumero());
            for (LigneFacture ligne : f.getLignes()) {
                System.out.println(" - " + ligne.getDesignation() + " : " + ligne.sousTotal() + " DH");
            }
            System.out.println("TOTAL : " + f.calculerTotal() + " DH");

            // 2. Phase 4 : filtrage et statistiques avec les streams
            System.out.println("Reservations de Youssef : " + gestion.reservationsDuClient(1).size());
            System.out.println("Chiffre d'affaires : " + gestion.chiffreAffaires() + " DH");

            // 3. Phase 4 : interfaces fonctionnelles du cours (Predicate + Consumer)
            Predicate<Reservation> active = res -> res.estActive();
            Consumer<Reservation> afficher = res ->
                    System.out.println("Reservation " + res.getId() + " : " + res.getProjet().getTitre());
            System.out.println("Reservations actives :");
            gestion.getReservations().stream().filter(active).forEach(afficher);

            // 3 bis. Autres interfaces fonctionnelles du cours
            // Function : 1 entree -> 1 resultat
            Function<Reservation, String> resume = res ->
                    res.getProjet().getTitre() + " (" + res.getStudio().getLibelle() + ")";
            gestion.getReservations().stream().map(resume).forEach(System.out::println);

            // Supplier : aucune entree -> fournit une valeur
            Supplier<LocalDateTime> debutParDefaut = () -> LocalDateTime.of(2026, 11, 10, 14, 0);
            System.out.println("Debut par defaut : " + debutParDefaut.get());

            // BiFunction : 2 entrees -> 1 resultat
            BiFunction<Studio, Double, Double> prixStudio = (s, h) -> s.calculerPrix(h);
            System.out.println("Studio B pendant 3h : " + prixStudio.apply(studioB, 3.0) + " DH");

            // BiConsumer : 2 entrees -> une action, sans resultat
            BiConsumer<String, Double> afficherLigne = (nom, prix) ->
                    System.out.println(" - " + nom + " : " + prix + " DH");
            afficherLigne.accept("Camera", camera.calculerPrix(2));

            // 4. Cas d'erreur : un autre client veut le meme studio au meme moment
            Client sara = new Client(2, "Sara", "sara@example.com");
            ProjetCreatif autreProjet = new Video(2, sara, "Pub locale", 2);
            gestion.reserver(autreProjet, studioB, List.of(camera), debut, fin);

        } catch (ConflitReservationException e) {
            System.out.println("CONFLIT DETECTE : " + e.getMessage());
        } catch (ReservationException e) {
            System.out.println("RESERVATION REFUSEE : " + e.getMessage());
        }
    }
}