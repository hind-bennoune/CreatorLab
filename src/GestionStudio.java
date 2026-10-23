import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Service metier : garde la coherence entre projets, studios et materiel.
public class GestionStudio {
    private final List<Reservation> reservations = new ArrayList<>();
    private int prochainNumeroReservation = 1;
    private int prochainNumeroFacture = 1;

    public boolean verifierDisponibilite(Studio studio, LocalDateTime debut, LocalDateTime fin) {
        return reservations.stream().noneMatch(r -> r.estActive()
                && r.getStudio().getId() == studio.getId() && r.chevauche(debut, fin));
    }

    public Reservation reserver(ProjetCreatif projet, Studio studio, List<Equipement> equipements,
                                LocalDateTime debut, LocalDateTime fin) throws ReservationException {
        if (projet == null || studio == null || equipements == null ||
                debut == null || fin == null || !debut.isBefore(fin)) {
            throw new IllegalArgumentException("Parametres de reservation invalides");
        }
        if (projet.getStatut() == StatutProjet.LIVRE) {
            throw new ReservationException("Un projet livre ne peut pas etre reserve");
        }
        if (!studio.estCompatible(projet)) {
            throw new ReservationException("Studio incompatible avec le projet");
        }
        if (!verifierDisponibilite(studio, debut, fin)) {
            throw new ConflitReservationException("Studio deja reserve a ce creneau");
        }

        Set<Integer> ids = new HashSet<>();
        for (Equipement eq : equipements) {
            if (eq == null || !ids.add(eq.getId())) {
                throw new MaterielIndisponibleException("Equipement nul ou repete");
            }
            if (!eq.estOperationnel()) {
                throw new MaterielIndisponibleException(eq.getLibelle() + " est en panne");
            }
            for (Reservation r : reservations) {
                boolean dejaReserve = r.getEquipements().stream()
                        .anyMatch(e -> e.getId() == eq.getId());
                if (r.estActive() && r.chevauche(debut, fin) && dejaReserve) {
                    throw new MaterielIndisponibleException(eq.getLibelle() + " deja utilise");
                }
            }
        }
        Set<TypeEquipement> typesFournis = equipements.stream()
                .map(Equipement::getType).collect(Collectors.toSet());
        if (!typesFournis.containsAll(projet.getMaterielRequis())) {
            throw new MaterielIndisponibleException("Materiel obligatoire manquant : "
                    + projet.getMaterielRequis());
        }

        Reservation r = new Reservation(prochainNumeroReservation++, projet, studio,
                equipements, debut, fin);
        reservations.add(r);
        projet.planifier();
        return r;
    }

    public void annuler(Reservation reservation) {
        if (reservation == null || !reservations.contains(reservation)) {
            throw new IllegalArgumentException("Reservation inconnue");
        }
        reservation.annuler();
    }

    public Facture genererFacture(Reservation reservation) throws ReservationException {
        if (reservation == null || !reservations.contains(reservation)) {
            throw new IllegalArgumentException("Reservation inconnue");
        }
        if (!reservation.estActive()) {
            throw new ReservationException("Impossible de facturer une reservation annulee");
        }
        if (reservation.getFacture() != null) return reservation.getFacture();

        // Polymorphisme : Studio et Equipement sont traites comme Facturable.
        List<Facturable> ressources = new ArrayList<>();
        ressources.add(reservation.getStudio());
        ressources.addAll(reservation.getEquipements());
        List<LigneFacture> lignes = new ArrayList<>();
        for (Facturable ressource : ressources) {
            lignes.add(new LigneFacture(ressource.getLibelle(), 1,
                    ressource.calculerPrix(reservation.getDuree())));
        }
        Facture facture = new Facture(prochainNumeroFacture++, reservation, lignes);
        reservation.associerFacture(facture);
        return facture;
    }

    public List<Reservation> getReservations() { return List.copyOf(reservations); }
}
