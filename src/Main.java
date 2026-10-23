import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Client youssef = new Client(1, "Youssef", "youssef@example.com");
        ProjetCreatif podcast = new Podcast(1, youssef, "TechTalk #12", 2, true);
        Studio studioB = new Studio(1, "B", 6, new BigDecimal("300"),
                Set.of(TypeProduction.PODCAST, TypeProduction.VIDEO));
        Equipement camera = new Equipement(1, "Camera Sony", TypeEquipement.CAMERA,
                new BigDecimal("100"));
        Equipement micro1 = new Equipement(2, "Micro 1", TypeEquipement.MICROPHONE,
                new BigDecimal("20"));
        Equipement micro2 = new Equipement(3, "Micro 2", TypeEquipement.MICROPHONE,
                new BigDecimal("20"));
        Equipement micro3 = new Equipement(4, "Micro 3", TypeEquipement.MICROPHONE,
                new BigDecimal("20"));
        GestionStudio gestion = new GestionStudio();
        LocalDateTime debut = LocalDateTime.of(2026, 11, 10, 14, 0);
        LocalDateTime fin = debut.plusHours(2);

        try {
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

            // Cas d'erreur : un autre client veut le meme studio au meme moment.
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
