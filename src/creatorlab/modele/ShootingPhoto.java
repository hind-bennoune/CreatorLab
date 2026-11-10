package creatorlab.modele;


import java.util.Set;

public class ShootingPhoto extends ProjetCreatif {
    private final int nombrePhotos;
    private final int nombreParticipants;

    public ShootingPhoto(int id, Client client, String titre,
                         int nombrePhotos, int nombreParticipants) {
        super(id, client, titre);
        if (nombrePhotos <= 0 || nombreParticipants <= 0) {
            throw new IllegalArgumentException("Shooting invalide");
        }
        this.nombrePhotos = nombrePhotos;
        this.nombreParticipants = nombreParticipants;
    }

    @Override public String getType() { return "PHOTO"; }
    @Override public int getNombreParticipants() { return nombreParticipants; }
    @Override public Set<String> getMaterielRequis() {
        return Set.of("APPAREIL_PHOTO", "ECLAIRAGE");
    }
    public int getNombrePhotos() { return nombrePhotos; }
}
