import java.util.Set;

public class Video extends ProjetCreatif {
    private final int nombreParticipants;

    public Video(int id, Client client, String titre, int nombreParticipants) {
        super(id, client, titre);
        if (nombreParticipants <= 0) throw new IllegalArgumentException("Participants invalides");
        this.nombreParticipants = nombreParticipants;
    }

    @Override public TypeProduction getType() { return TypeProduction.VIDEO; }
    @Override public int getNombreParticipants() { return nombreParticipants; }
    @Override public Set<TypeEquipement> getMaterielRequis() {
        return Set.of(TypeEquipement.CAMERA);
    }
}
