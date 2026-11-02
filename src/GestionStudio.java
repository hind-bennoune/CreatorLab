import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GestionStudio {
    private List<Reservation> reservations = new ArrayList<>();
    private int prochainNumeroReservation = 1;
    private int prochainNumeroFacture = 1;

    // Le studio est libre si aucune reservation active ne chevauche le creneau
    public boolean verifierDisponibilite(Studio studio, LocalDateTime debut, LocalDateTime fin) {
        for (Reservation r : reservations) {
            if (r.estActive() && r.getStudio().getId() == studio.getId()
                    && r.chevauche(debut, fin)) {
                return false;
            }
        }
        return true;
    }

    // Meme logique pour un equipement
    private boolean equipementLibre(Equipement eq, LocalDateTime debut, LocalDateTime fin) {
        for (Reservation r : reservations) {
            if (r.estActive() && r.chevauche(debut, fin) && r.getEquipements().contains(eq)) {
                return false;
            }
        }
        return true;
    }

    public Reservation reserver(ProjetCreatif projet, Studio studio, List<Equipement> equipements,
                                LocalDateTime debut, LocalDateTime fin) throws ReservationException {
        // 1. Verifications generales
        if (!debut.isBefore(fin)) {
            throw new IllegalArgumentException("La date de debut doit preceder la fin");
        }
        if (projet.getStatut() == StatutProjet.LIVRE) {
            throw new ReservationException("Un projet livre ne peut pas etre reserve");
        }
        if (!studio.estCompatible(projet)) {
            throw new ReservationException("Studio incompatible avec le projet");
        }
        // 2. Studio libre ?
        if (!verifierDisponibilite(studio, debut, fin)) {
            throw new ConflitReservationException("Studio deja reserve a ce creneau");
        }
        // 3. Chaque equipement : en etat de marche et libre ?
        List<TypeEquipement> typesFournis = new ArrayList<>();
        for (Equipement eq : equipements) {
            if (!eq.estOperationnel()) {
                throw new MaterielIndisponibleException(eq.getLibelle() + " est en panne");
            }
            if (!equipementLibre(eq, debut, fin)) {
                throw new MaterielIndisponibleException(eq.getLibelle() + " deja utilise");
            }
            typesFournis.add(eq.getType());
        }
        // 4. Le materiel obligatoire est-il present ?
        for (TypeEquipement requis : projet.getMaterielRequis()) {
            if (!typesFournis.contains(requis)) {
                throw new MaterielIndisponibleException("Materiel obligatoire manquant : " + requis);
            }
        }
        // 5. Tout est bon : on enregistre
        Reservation r = new Reservation(prochainNumeroReservation++, projet, studio,
                equipements, debut, fin);
        reservations.add(r);
        projet.planifier();
        return r;
    }

    public void annuler(Reservation reservation) {
        reservation.annuler();
    }

    public Facture genererFacture(Reservation reservation) throws ReservationException {
        if (!reservation.estActive()) {
            throw new ReservationException("Impossible de facturer une reservation annulee");
        }
        if (reservation.getFacture() != null) {
            return reservation.getFacture(); // deja facturee
        }
        double heures = reservation.getDureeHeures();

        // Polymorphisme : Studio et Equipement sont traites comme Facturable
        List<Facturable> ressources = new ArrayList<>();
        ressources.add(reservation.getStudio());
        ressources.addAll(reservation.getEquipements());

        Facture facture = new Facture(prochainNumeroFacture++);
        for (Facturable ressource : ressources) {
            facture.ajouterLigne(new LigneFacture(ressource.getLibelle(), 1,
                    ressource.calculerPrix(heures)));
        }
        reservation.setFacture(facture);
        return facture;
    }

    // ---------- PHASE 4 : Lambda et Streams ----------

    // Filtrer : les reservations d'un client
    public List<Reservation> reservationsDuClient(int idClient) {
        return reservations.stream()
                .filter(r -> r.getProjet().getClient().getId() == idClient)
                .collect(Collectors.toList());
    }

    // Trier : par date de debut
    public List<Reservation> reservationsTriees() {
        return reservations.stream()
                .sorted(Comparator.comparing(Reservation::getDebut))
                .collect(Collectors.toList());
    }

    // Statistique : somme des factures emises
    public double chiffreAffaires() {
        return reservations.stream()
                .filter(r -> r.getFacture() != null)
                .mapToDouble(r -> r.getFacture().calculerTotal())
                .sum();
    }

    public List<Reservation> getReservations() { return reservations; }
}