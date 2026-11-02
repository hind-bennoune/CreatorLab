import java.util.Set;

public class ShootingPhoto extends ProjetCreatif {
    private int nombrePhotos;
    private int nombreParticipants;

    public ShootingPhoto(int id, Client client, String titre, int nombrePhotos, int nombreParticipants) {
        super(id, client, titre);
        this.nombrePhotos = nombrePhotos;
        this.nombreParticipants = nombreParticipants;
    }

    @Override
    public TypeProduction getType() { return TypeProduction.PHOTO; }

    @Override
    public int getNombreParticipants() { return nombreParticipants; }

    @Override
    public Set<TypeEquipement> getMaterielRequis() {
        return Set.of(TypeEquipement.APPAREIL_PHOTO, TypeEquipement.ECLAIRAGE);
    }

    public int getNombrePhotos() { return nombrePhotos; }
}