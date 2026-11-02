import java.util.Set;

public class Video extends ProjetCreatif {
    private int nombreParticipants;

    public Video(int id, Client client, String titre, int nombreParticipants) {
        super(id, client, titre);
        this.nombreParticipants = nombreParticipants;
    }

    @Override
    public TypeProduction getType() { return TypeProduction.VIDEO; }

    @Override
    public int getNombreParticipants() { return nombreParticipants; }

    @Override
    public Set<TypeEquipement> getMaterielRequis() {
        return Set.of(TypeEquipement.CAMERA);
    }
}