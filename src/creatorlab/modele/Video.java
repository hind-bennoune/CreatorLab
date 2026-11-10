package creatorlab.modele;

import java.util.Set;

public class Video extends ProjetCreatif {

    private final int nombreParticipants;

    public Video(int id, Client client, String titre,
                 int nombreParticipants) {

        super(id, client, titre);

        if (nombreParticipants <= 0) {
            throw new IllegalArgumentException(
                "Le nombre de participants doit etre superieur a zero"
            );
        }

        this.nombreParticipants = nombreParticipants;
    }

    @Override
    public String getType() {
        return "VIDEO";
    }

    @Override
    public int getNombreParticipants() {
        return nombreParticipants;
    }

    @Override
    public Set<String> getMaterielRequis() {
        return Set.of(
            "CAMERA",
            "ECLAIRAGE",
            "TREPIED",
            "MICROPHONE"
        );
    }
}