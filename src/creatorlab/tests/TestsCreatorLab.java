package creatorlab.tests;

import creatorlab.exceptions.ConflitReservationException;
import creatorlab.exceptions.MaterielIndisponibleException;
import creatorlab.exceptions.ReservationException;
import creatorlab.fonctionnel.CalculTarif;
import creatorlab.modele.Client;
import creatorlab.modele.Equipement;
import creatorlab.modele.Facturable;
import creatorlab.modele.Facture;
import creatorlab.modele.Podcast;
import creatorlab.modele.ProjetCreatif;
import creatorlab.modele.Reservation;
import creatorlab.modele.ShootingPhoto;
import creatorlab.modele.Streaming;
import creatorlab.modele.Studio;
import creatorlab.modele.Video;
import creatorlab.service.GestionStudio;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// Tests simples, sans JUnit (comme dans les exercices du cours).
public class TestsCreatorLab {
    private static int total;

    private static void verifier(boolean condition, String nom) {
        total++;
        if (!condition) throw new AssertionError("ECHEC : " + nom);
        System.out.println("OK " + total + " - " + nom);
    }

    private static List<Equipement> creerMaterielPodcast(int participants, boolean avecVideo) {
        List<Equipement> equipements = new ArrayList<>();
        int id = 1;
        for (int i = 1; i <= participants; i++) {
            equipements.add(new Equipement(id++, "Micro " + i, "MICROPHONE", 20.0));
            equipements.add(new Equipement(id++, "Casque " + i, "CASQUE", 5.0));
            equipements.add(new Equipement(id++, "Pied " + i, "PIED_MICRO", 4.0));
            equipements.add(new Equipement(id++, "Filtre " + i, "FILTRE_ANTI_POP", 2.0));
        }
        equipements.add(new Equipement(id++, "Interface audio", "INTERFACE_AUDIO", 15.0));
        if (avecVideo) {
            equipements.add(new Equipement(id++, "Camera", "CAMERA", 40.0));
            equipements.add(new Equipement(id++, "Eclairage", "ECLAIRAGE", 10.0));
        }
        return equipements;
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client(1, "Sara", "sara@example.com");
        ProjetCreatif podcast = new Podcast(1, client, "Emission", 1, true);
        Studio a = new Studio(1, "A", 4, 120.0, Set.of("PODCAST"));
        Studio b = new Studio(2, "B", 4, 120.0, Set.of("PODCAST", "VIDEO"));
        List<Equipement> materiel = creerMaterielPodcast(podcast.getNombreParticipants(), true);
        Equipement micro = materiel.get(0);
        Equipement camera = materiel.get(materiel.size() - 2);
        GestionStudio gestion = new GestionStudio();
        LocalDateTime debut = LocalDateTime.of(2026, 11, 12, 10, 0);
        LocalDateTime fin = debut.plusHours(2);

        verifier(podcast instanceof ProjetCreatif && podcast.getType().equals("PODCAST"),
                "Heritage et methode redefinie");
        verifier(podcast.getNombreParticipants() == 2, "Participants podcast");
        verifier(podcast.getStatut().equals("BROUILLON"), "Projet initialement en brouillon");
        verifier(podcast.getQuantitesMaterielRequis().get("MICROPHONE") == 2,
                "Deux microphones obligatoires pour deux participants");
        verifier(podcast.getQuantitesMaterielRequis().get("CASQUE") == 2,
                "Deux casques obligatoires pour deux participants");
        Facturable ressource = a;
        verifier(ressource.calculerPrix(1.5) == 180.0, "Interface et polymorphisme");
        CalculTarif lambda = (prix, duree) -> prix * duree;
        verifier(lambda.calculer(20, 2) == 40, "Interface fonctionnelle et lambda");

        // Controle des quantites : une seule absence de micro suffit a refuser.
        List<Equipement> sansUnMicro = new ArrayList<>(materiel);
        sansUnMicro.remove(micro);
        try {
            gestion.reserver(podcast, a, sansUnMicro, debut, fin);
            throw new AssertionError("Quantite insuffisante non detectee");
        } catch (MaterielIndisponibleException ok) {
            verifier(ok.getMessage().contains("MICROPHONE"), "Quantite insuffisante de microphones");
        }
        verifier(gestion.getReservations().isEmpty(), "Une reservation refusee n'est pas enregistree");
        verifier(podcast.getStatut().equals("BROUILLON"), "Projet reste brouillon si reservation refusee");

        Reservation r1 = gestion.reserver(podcast, a, materiel, debut, fin);
        verifier(r1.estActive(), "Reservation confirmee");
        verifier(podcast.getStatut().equals("PLANIFIE"), "Projet planifie");
        verifier(!gestion.verifierDisponibilite(a, debut, fin), "Predicate : studio occupe");
        try {
            gestion.reserver(podcast, a, materiel, debut, fin);
            throw new AssertionError("Conflit de studio non detecte");
        } catch (ConflitReservationException ok) {
            verifier(true, "Exception : conflit studio");
        }
        try {
            gestion.reserver(podcast, b, materiel, debut, fin);
            throw new AssertionError("Conflit de materiel non detecte");
        } catch (MaterielIndisponibleException ok) {
            verifier(true, "Exception : materiel deja utilise");
        }
        Facture facture = gestion.genererFacture(r1);
        // Studio : 120*2=240 ; equipements : (2*(20+5+4+2)+15+40+10)*2=254.
        verifier(facture.calculerTotal() == 494.0, "Facture : studio + tout le materiel = 494 DH");
        verifier(gestion.genererFacture(r1) == facture, "Facture non dupliquee");
        // List.forEach : parcourir toutes les lignes avec une lambda Consumer.
        List<String> lignesVisitees = new ArrayList<>();
        facture.getLignes().forEach(ligne -> lignesVisitees.add(ligne.getDesignation()));
        verifier(lignesVisitees.size() == facture.getLignes().size()
                        && lignesVisitees.contains(a.getLibelle()),
                "List.forEach : toutes les lignes de la facture sont parcourues");
        Reservation r2 = gestion.reserver(podcast, a, materiel, fin, fin.plusHours(1));
        verifier(r2.estActive(), "Creneaux consecutifs admis");
        gestion.annuler(r2);
        verifier(gestion.verifierDisponibilite(a, fin, fin.plusHours(1)),
                "Annulation libere le studio");
        verifier(podcast.getStatut().equals("PLANIFIE"),
                "Projet reste PLANIFIE si une autre reservation est active");
        try {
            gestion.genererFacture(r2);
            throw new AssertionError("Facturation annulee non refusee");
        } catch (ReservationException ok) {
            verifier(true, "Facturer une reservation annulee est interdit");
        }
        try {
            gestion.annuler(r1);
            throw new AssertionError("Annulation facturee non refusee");
        } catch (IllegalStateException ok) {
            verifier(true, "Annuler une reservation facturee est interdit");
        }
        micro.declarerPanne();
        try {
            gestion.reserver(podcast, b, materiel, fin.plusHours(1), fin.plusHours(2));
            throw new AssertionError("Panne non detectee");
        } catch (MaterielIndisponibleException ok) {
            verifier(true, "Exception : equipement en panne");
        }
        micro.reparer();
        try {
            gestion.reserver(podcast, b, List.of(camera), fin.plusHours(1), fin.plusHours(2));
            throw new AssertionError("Materiel manquant non detecte");
        } catch (MaterielIndisponibleException ok) {
            verifier(true, "Materiel obligatoire manquant");
        }
        List<Equipement> avecDoublon = new ArrayList<>(materiel);
        avecDoublon.add(micro);
        try {
            gestion.reserver(podcast, b, avecDoublon, fin.plusHours(1), fin.plusHours(2));
            throw new AssertionError("Doublon non detecte");
        } catch (MaterielIndisponibleException ok) {
            verifier(true, "Materiel en double refuse");
        }
        ProjetCreatif shooting = new ShootingPhoto(3, client, "Photos", 10, 3);
        verifier(shooting.getMaterielRequis().contains("ECLAIRAGE"), "Polymorphisme shooting");
        verifier(shooting.getQuantitesMaterielRequis().get("ECLAIRAGE") == 1,
                "Materiel photo : quantite par defaut a un");
        ProjetCreatif video = new Video(4, client, "Video", 6);
        try {
            gestion.reserver(video, b, List.of(camera), fin.plusHours(3), fin.plusHours(4));
            throw new AssertionError("Capacite non controlee");
        } catch (ReservationException ok) {
            verifier(true, "Studio de capacite insuffisante refuse");
        }
        try {
            new Client(4, "", "email");
            throw new AssertionError("Client invalide accepte");
        } catch (IllegalArgumentException ok) {
            verifier(true, "Validation des donnees / IllegalArgumentException");
        }
        ProjetCreatif live = new Streaming(5, client, "Direct", "YouTube", 3);
        verifier(live.getQuantitesMaterielRequis().get("MICROPHONE") == 3
                        && live.getQuantitesMaterielRequis().get("CASQUE") == 3,
                "Streaming : un microphone et un casque par participant");
        verifier(gestion.getReservations().size() == 2, "Liste des reservations conservee");

        // Autre scenario : retour en brouillon seulement apres la derniere annulation.
        GestionStudio gestionStatuts = new GestionStudio();
        ProjetCreatif autrePodcast = new Podcast(6, client, "Essai", 1, false);
        List<Equipement> audio = creerMaterielPodcast(2, false);
        Reservation premier = gestionStatuts.reserver(autrePodcast, a, audio, debut, fin);
        Reservation deuxieme = gestionStatuts.reserver(autrePodcast, a, audio, fin, fin.plusHours(1));
        gestionStatuts.annuler(premier);
        verifier(autrePodcast.getStatut().equals("PLANIFIE"),
                "Annuler une reservation conserve PLANIFIE s'il en reste une");
        gestionStatuts.annuler(deuxieme);
        verifier(autrePodcast.getStatut().equals("BROUILLON"),
                "Derniere annulation remet le projet en BROUILLON");
        verifier(gestionStatuts.verifierDisponibilite(a, debut, fin.plusHours(1)),
                "Les creneaux annules sont a nouveau disponibles");

        // === TESTS DES COLLECTIONS ET DES STREAMS ===
        verifier(gestion.getReservationsActives().size() == 1,
                "Stream filter : ignorer une reservation annulee");
        verifier(gestion.getReservationsParClient(client.getId()).size() == 2,
                "Stream filter : historique des reservations du client");
        verifier(gestion.getReservationsTrieesParDate().get(0) == r1,
                "Stream sorted : trier chronologiquement");
        verifier(gestion.rechercherReservation(r1.getNumero()).orElseThrow() == r1,
                "Stream findFirst : trouver une reservation");
        verifier(gestion.rechercherReservation(999).isEmpty(),
                "Optional : numero introuvable sans null");
        verifier(gestion.compterReservationsActives() == 1,
                "Stream count : nombre de reservations actives");
        verifier(gestion.compterReservationsActivesParType().get("PODCAST") == 1L,
                "Collectors.groupingBy : comptage par type");
        // Map.forEach : pour chaque entree, la lambda recoit (type, nombre).
        List<String> groupesParcourus = new ArrayList<>();
        gestion.compterReservationsActivesParType().forEach((type, nombre) ->
                groupesParcourus.add(type + "=" + nombre));
        verifier(groupesParcourus.equals(List.of("PODCAST=1")),
                "Map.forEach : chaque couple cle-valeur est parcouru");
        // Stream.forEach : terminal apres filter, sans creer une nouvelle liste.
        List<Integer> numerosActifs = new ArrayList<>();
        gestion.getReservations().stream()
                .filter(Reservation::estActive)
                .forEach(r -> numerosActifs.add(r.getNumero()));
        verifier(numerosActifs.equals(List.of(r1.getNumero())),
                "Stream.forEach : parcourir uniquement les reservations actives");
        verifier(gestion.calculerChiffreAffairesFacture() == 494.0,
                "mapToDouble sum : total des factures emises");
        verifier(gestion.getTroisReservationsLesPlusLongues().equals(List.of(r1)),
                "Stream limit : top des reservations actives");
        verifier(facture.getLignes().stream().mapToDouble(l -> l.calculerSousTotal()).sum()
                == facture.calculerTotal(),
                "Facture : calcul du total avec Stream");
        try {
            gestion.getReservationsActives().clear();
            throw new AssertionError("La liste retournee devrait etre non modifiable");
        } catch (UnsupportedOperationException ok) {
            verifier(true, "toList : liste resultat non modifiable");
        }
        try {
            gestion.getReservationsParClient(0);
            throw new AssertionError("Identifiant client invalide accepte");
        } catch (IllegalArgumentException ok) {
            verifier(true, "Recherche client : identifiant valide");
        }

        // Cas complementaire : deux types de projet, dates inversees, et annulation.
        GestionStudio gestionAnalyse = new GestionStudio();
        Client client2 = new Client(7, "Nadia", "nadia@example.com");
        ProjetCreatif pAudio = new Podcast(8, client, "Cours audio", 1, false);
        ProjetCreatif pVideo = new Video(9, client2, "Spot", 2);
        Studio studioAudio = new Studio(10, "Audio", 5, 90.0, Set.of("PODCAST"));
        Studio studioVideo = new Studio(11, "Video", 5, 150.0, Set.of("VIDEO"));
        List<Equipement> kitAudio = creerMaterielPodcast(2, false);
        List<Equipement> kitVideo = List.of(
                new Equipement(101, "Camera", "CAMERA", 40.0),
                new Equipement(102, "Lampe", "ECLAIRAGE", 10.0),
                new Equipement(103, "Trepied", "TREPIED", 5.0),
                new Equipement(104, "Micro", "MICROPHONE", 20.0));
        Reservation reservationAudio = gestionAnalyse.reserver(pAudio, studioAudio, kitAudio,
                debut.plusDays(1), debut.plusDays(1).plusHours(3));
        Reservation videoRes = gestionAnalyse.reserver(pVideo, studioVideo, kitVideo,
                debut, debut.plusHours(1));
        verifier(gestionAnalyse.getReservationsTrieesParDate().get(0) == videoRes,
                "sorted : dates triees et non ordre d'insertion");
        verifier(gestionAnalyse.getReservationsParClient(client2.getId()).equals(List.of(videoRes)),
                "filter : retrouver uniquement le bon client");
        verifier(gestionAnalyse.compterReservationsActivesParType().size() == 2,
                "groupingBy : deux types de production");
        verifier(gestionAnalyse.getTroisReservationsLesPlusLongues().get(0) == reservationAudio,
                "sorted reversed : reservation la plus longue d'abord");
        verifier(gestionAnalyse.calculerChiffreAffairesFacture() == 0.0,
                "sum : aucune facture encore emise");
        Facture factureVideo = gestionAnalyse.genererFacture(videoRes);
        verifier(gestionAnalyse.calculerChiffreAffairesFacture() == factureVideo.calculerTotal(),
                "map filter sum : seul le montant effectivement facture est compte");
        gestionAnalyse.annuler(reservationAudio);
        verifier(gestionAnalyse.compterReservationsActives() == 1
                        && !gestionAnalyse.compterReservationsActivesParType().containsKey("PODCAST"),
                "filter : annulation exclue des statistiques actives");

        System.out.println("SUCCES : " + total + " tests reussis.");
    }
}
