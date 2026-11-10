package creatorlab.app;

import creatorlab.exceptions.ConflitReservationException;
import creatorlab.exceptions.ReservationException;
import creatorlab.modele.Client;
import creatorlab.modele.Equipement;
import creatorlab.modele.Facture;
import creatorlab.modele.LigneFacture;
import creatorlab.modele.Podcast;
import creatorlab.modele.ProjetCreatif;
import creatorlab.modele.Reservation;
import creatorlab.modele.Studio;
import creatorlab.modele.Video;
import creatorlab.service.GestionStudio;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class Main {

    // Construire un kit complet pour un podcast : les quantites sont importantes.
    private static List<Equipement> creerMaterielPodcast(int participants, boolean avecVideo) {
        List<Equipement> equipements = new ArrayList<>();
        int id = 1;
        for (int i = 1; i <= participants; i++) {
            equipements.add(new Equipement(id++, "Micro " + i, "MICROPHONE", 20.0));
            equipements.add(new Equipement(id++, "Casque " + i, "CASQUE", 5.0));
            equipements.add(new Equipement(id++, "Pied micro " + i, "PIED_MICRO", 4.0));
            equipements.add(new Equipement(id++, "Filtre " + i, "FILTRE_ANTI_POP", 2.0));
        }
        equipements.add(new Equipement(id++, "Interface audio", "INTERFACE_AUDIO", 15.0));
        if (avecVideo) {
            equipements.add(new Equipement(id++, "Camera", "CAMERA", 50.0));
            equipements.add(new Equipement(id++, "Eclairage", "ECLAIRAGE", 10.0));
        }
        return equipements;
    }

    public static void main(String[] args) {
        Client client = new Client(1, "Youssef", "youssef@example.com");
        ProjetCreatif podcast = new Podcast(1, client, "TechTalk", 2, true);
        Studio studio = new Studio(1, "Studio A", 5, 200.0, Set.of("PODCAST", "VIDEO"));
        List<Equipement> materiel = creerMaterielPodcast(podcast.getNombreParticipants(), true);
        GestionStudio gestion = new GestionStudio();
        LocalDateTime debut = LocalDateTime.of(2026, 11, 10, 14, 0);
        LocalDateTime fin = debut.plusHours(2);

        Function<Reservation, String> resume = r -> "Reservation n° " + r.getNumero()
                + " / Projet : " + r.getProjet().getTitre();
        BiFunction<String, Integer, String> formater =
                (libelle, quantite) -> quantite + " x " + libelle;
        Consumer<LigneFacture> afficherLigne = ligne ->
                System.out.printf("  - %s : %.2f DH%n",
                        formater.apply(ligne.getDesignation(), ligne.getQuantite()),
                        ligne.calculerSousTotal());
        BiConsumer<String, Double> afficherMontant = (nom, montant) ->
                System.out.printf("%s : %.2f DH%n", nom, montant);

        try {
            Reservation reservation = gestion.reserver(podcast, studio, materiel, debut, fin);
            System.out.println("=== CREATORLAB ===");
            System.out.println(resume.apply(reservation));
            System.out.println("Statut du projet : " + podcast.getStatut());
            Facture facture = gestion.genererFacture(reservation);
            System.out.println("Facture n° " + facture.getNumero());
            // List.forEach(Consumer) : executer l'action afficherLigne sur chaque ligne.
            facture.getLignes().forEach(afficherLigne);
            afficherMontant.accept("TOTAL", facture.calculerTotal());

            // Une deuxieme reservation REUSSIE dans un autre studio,
            // pour avoir plusieurs donnees a trier et filtrer avec Stream.
            Client autreClient = new Client(2, "Sara", "sara@example.com");
            ProjetCreatif video = new Video(2, autreClient, "Publicite", 2);
            Studio studioVideo = new Studio(2, "Studio B", 4, 150.0, Set.of("VIDEO"));
            List<Equipement> kitVideo = List.of(
                    new Equipement(100, "Camera video", "CAMERA", 50.0),
                    new Equipement(101, "Eclairage video", "ECLAIRAGE", 10.0),
                    new Equipement(102, "Trepied", "TREPIED", 5.0),
                    new Equipement(103, "Micro video", "MICROPHONE", 20.0));
            Reservation reservationVideo = gestion.reserver(video, studioVideo, kitVideo,
                    debut.minusHours(2), debut.minusHours(1));
            gestion.genererFacture(reservationVideo);

            // Tentative volontairement en conflit avec le podcast initial.
            gestion.reserver(video, studio, List.of(materiel.get(0)), debut, fin);
        } catch (ConflitReservationException e) {
            System.out.println("Conflit attendu : " + e.getMessage());
        } catch (ReservationException e) {
            System.out.println("Reservation refusee : " + e.getMessage());
        }

        // === NOUVELLE PARTIE : COLLECTIONS ET STREAMS ===
        System.out.println("\n=== COLLECTIONS ET STREAMS ===");
        System.out.println("Nombre de reservations actives : "
                + gestion.compterReservationsActives());
        System.out.println("Reservations triees par date :");
        gestion.getReservationsTrieesParDate().forEach(r ->
                System.out.println("  - " + r.getDebut() + " : " + r.getProjet().getTitre()));
        System.out.println("Reservations du client 1 : "
                + gestion.getReservationsParClient(1).size());
        System.out.println("Reservations actives par type (Map.forEach) :");
        // Map.forEach(BiConsumer) : recevoir la cle et sa valeur.
        gestion.compterReservationsActivesParType().forEach((type, nombre) ->
                System.out.println("  - " + type + " : " + nombre));
        System.out.println("Les 3 plus longues reservations :");
        gestion.getTroisReservationsLesPlusLongues().forEach(r ->
                System.out.println("  - " + r.getProjet().getTitre()
                        + " : " + r.getDureeHeures() + " h"));
        // Stream.forEach : apres le filtre, afficher chaque reservation VIDEO.
        System.out.println("Reservations VIDEO (Stream.forEach) :");
        gestion.getReservations().stream()
                .filter(r -> r.getProjet().getType().equals("VIDEO"))
                .forEach(r -> System.out.println("  - " + r.getProjet().getTitre()));
        gestion.rechercherReservation(1).ifPresent(r ->
                System.out.println("Recherche numero 1 : " + r.getProjet().getTitre()));
        afficherMontant.accept("TOTAL FACTURE (non encaisse)",
                gestion.calculerChiffreAffairesFacture());
    }
}
