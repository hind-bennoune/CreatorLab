package creatorlab.service;


import creatorlab.exceptions.ConflitReservationException;
import creatorlab.exceptions.MaterielIndisponibleException;
import creatorlab.exceptions.ReservationException;
import creatorlab.modele.Equipement;
import creatorlab.modele.Facturable;
import creatorlab.modele.Facture;
import creatorlab.modele.LigneFacture;
import creatorlab.modele.ProjetCreatif;
import creatorlab.modele.Reservation;
import creatorlab.modele.Studio;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.function.Predicate;

// Service : controle les regles metier et gere les reservations en memoire.
public class GestionStudio {
    private final List<Reservation> reservations = new ArrayList<>();
    private int prochainNumeroReservation = 1;
    private int prochainNumeroFacture = 1;

    public boolean verifierDisponibilite(Studio studio, LocalDateTime debut, LocalDateTime fin) {
        if (studio == null || debut == null || fin == null || !debut.isBefore(fin)) {
            throw new IllegalArgumentException("Creneau invalide");
        }
        // Predicate du cours : une condition testee avec une lambda.
        Predicate<Reservation> conflit = r -> r.estActive()
                && r.getStudio().getId() == studio.getId()
                && r.chevauche(debut, fin);
        // noneMatch : vrai si aucune reservation ne correspond a la condition.
        return reservations.stream().noneMatch(conflit);
    }

    public Reservation reserver(ProjetCreatif projet, Studio studio, List<Equipement> equipements,
                                LocalDateTime debut, LocalDateTime fin)
                                throws ReservationException {
        if (projet == null || studio == null || equipements == null || debut == null
                || fin == null || !debut.isBefore(fin)) {
            throw new IllegalArgumentException("Parametres de reservation invalides");
        }
        if (!studio.estCompatible(projet)) {
            throw new ReservationException("Studio incompatible avec la production ou la capacite");
        }
        if (!verifierDisponibilite(studio, debut, fin)) {
            throw new ConflitReservationException("Studio deja occupe sur ce creneau");
        }

        Set<Integer> ids = new HashSet<>();
        Map<String, Integer> quantitesPresentes = new HashMap<>();
        for (Equipement equipement : equipements) {
            if (equipement == null || !ids.add(equipement.getId())) {
                throw new MaterielIndisponibleException("Equipement nul ou duplique");
            }
            if (!equipement.estOperationnel()) {
                throw new MaterielIndisponibleException("Equipement en panne : " + equipement.getLibelle());
            }
            String type = equipement.getType();
            quantitesPresentes.put(type, quantitesPresentes.getOrDefault(type, 0) + 1);
            for (Reservation existante : reservations) {
                if (existante.estActive() && existante.chevauche(debut, fin)) {
                    for (Equipement utilise : existante.getEquipements()) {
                        if (utilise.getId() == equipement.getId()) {
                            throw new MaterielIndisponibleException("Materiel deja utilise : "
                                    + equipement.getLibelle());
                        }
                    }
                }
            }
        }
        // Comparer les quantites, pas seulement la presence de chaque type.
        for (Map.Entry<String, Integer> besoin : projet.getQuantitesMaterielRequis().entrySet()) {
            int disponible = quantitesPresentes.getOrDefault(besoin.getKey(), 0);
            if (disponible < besoin.getValue()) {
                throw new MaterielIndisponibleException("Materiel insuffisant : "
                        + besoin.getKey() + " (necessaire : " + besoin.getValue()
                        + ", fourni : " + disponible + ")");
            }
        }

        Reservation nouvelle = new Reservation(prochainNumeroReservation++,
                projet, studio, equipements, debut, fin);
        reservations.add(nouvelle);
        projet.planifier();
        return nouvelle;
    }

    public void annuler(Reservation reservation) {
        if (reservation == null || !reservations.contains(reservation)) {
            throw new IllegalArgumentException("Reservation inconnue");
        }
        reservation.annuler();

        // Un projet reste PLANIFIE s'il possede encore une reservation active.
        // Sinon, il revient en BROUILLON pour permettre une autre planification.
        boolean autreReservationActive = reservations.stream()
                .anyMatch(r -> r.getProjet() == reservation.getProjet() && r.estActive());
        if (!autreReservationActive) {
            reservation.getProjet().remettreEnBrouillon();
        }
    }

    public Facture genererFacture(Reservation reservation) throws ReservationException {
        if (reservation == null || !reservations.contains(reservation)) {
            throw new IllegalArgumentException("Reservation inconnue");
        }
        if (!reservation.estActive()) {
            throw new ReservationException("Une reservation annulee ne peut pas etre facturee");
        }
        if (reservation.getFacture() != null) return reservation.getFacture();

        // Polymorphisme : Studio et Equipement sont tous des Facturable.
        List<Facturable> ressources = new ArrayList<>();
        ressources.add(reservation.getStudio());
        ressources.addAll(reservation.getEquipements());
        // map transforme chaque objet Facturable en une LigneFacture.
        List<LigneFacture> lignes = ressources.stream()
                .map(ressource -> new LigneFacture(ressource.getLibelle(), 1,
                        ressource.calculerPrix(reservation.getDureeHeures())))
                .toList();
        Facture facture = new Facture(prochainNumeroFacture++, reservation, lignes);
        reservation.associerFacture(facture);
        return facture;
    }

    // Liste complete (copie non modifiable) : collections Java.
    public List<Reservation> getReservations() { return List.copyOf(reservations); }

    // filter + toList : uniquement les reservations non annulees.
    public List<Reservation> getReservationsActives() {
        return reservations.stream()
                .filter(Reservation::estActive)
                .toList();
    }

    // filter + sorted : historique d'un client, meme si une reservation est annulee.
    public List<Reservation> getReservationsParClient(int idClient) {
        if (idClient <= 0) throw new IllegalArgumentException("Identifiant client invalide");
        return reservations.stream()
                .filter(r -> r.getProjet().getClient().getId() == idClient)
                .sorted(Comparator.comparing(Reservation::getDebut)
                        .thenComparingInt(Reservation::getNumero))
                .toList();
    }

    // sorted : classement chronologique de toutes les reservations.
    public List<Reservation> getReservationsTrieesParDate() {
        return reservations.stream()
                .sorted(Comparator.comparing(Reservation::getDebut)
                        .thenComparingInt(Reservation::getNumero))
                .toList();
    }

    // findFirst : Optional evite de retourner null si le numero est introuvable.
    public Optional<Reservation> rechercherReservation(int numero) {
        return reservations.stream()
                .filter(r -> r.getNumero() == numero)
                .findFirst();
    }

    // count : combien de reservations sont encore actives ?
    public long compterReservationsActives() {
        return reservations.stream()
                .filter(Reservation::estActive)
                .count();
    }

    // groupingBy : compter les reservations ACTIVES par type de production.
    public Map<String, Long> compterReservationsActivesParType() {
        return reservations.stream()
                .filter(Reservation::estActive)
                .collect(Collectors.groupingBy(r -> r.getProjet().getType(),
                        TreeMap::new, Collectors.counting()));
    }

    // sorted + limit : les 3 reservations actives de plus longue duree.
    public List<Reservation> getTroisReservationsLesPlusLongues() {
        return reservations.stream()
                .filter(Reservation::estActive)
                .sorted(Comparator.comparingDouble(Reservation::getDureeHeures)
                        .reversed().thenComparingInt(Reservation::getNumero))
                .limit(3)
                .toList();
    }

    // map, filter, mapToDouble, sum : montant total FACTURE (non encaisse).
    public double calculerChiffreAffairesFacture() {
        return reservations.stream()
                .map(Reservation::getFacture)
                .filter(facture -> facture != null)
                .mapToDouble(Facture::calculerTotal)
                .sum();
    }
}
