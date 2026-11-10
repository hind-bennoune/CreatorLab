package creatorlab.modele;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// Un projet de diffusion en direct : interview, emission, formation...
public class Streaming extends ProjetCreatif {

    private final String plateforme;
    private final int nombreParticipants;

    public Streaming(int id, Client client, String titre,
                     String plateforme, int nombreParticipants) {

        super(id, client, titre);

        if (plateforme == null || plateforme.isBlank()) {
            throw new IllegalArgumentException(
                "La plateforme de streaming est obligatoire"
            );
        }

        if (nombreParticipants <= 0) {
            throw new IllegalArgumentException(
                "Le nombre de participants doit etre superieur a zero"
            );
        }

        this.plateforme = plateforme;
        this.nombreParticipants = nombreParticipants;
    }

    @Override
    public String getType() {
        return "STREAMING";
    }

    @Override
    public int getNombreParticipants() {
        return nombreParticipants;
    }

    @Override
    public Set<String> getMaterielRequis() {
        return Set.of(
            "CAMERA",
            "TREPIED",
            "ECLAIRAGE",
            "MICROPHONE",
            "PIED_MICRO",
            "FILTRE_ANTI_POP",
            "CASQUE",
            "INTERFACE_AUDIO",
            "ORDINATEUR",
            "CARTE_ACQUISITION"
        );
    }

    // On prevoit un kit audio personnel par intervenant en direct.
    @Override
    public Map<String, Integer> getQuantitesMaterielRequis() {
        Map<String, Integer> quantites = new HashMap<>(super.getQuantitesMaterielRequis());
        quantites.put("MICROPHONE", nombreParticipants);
        quantites.put("CASQUE", nombreParticipants);
        quantites.put("PIED_MICRO", nombreParticipants);
        quantites.put("FILTRE_ANTI_POP", nombreParticipants);
        return Map.copyOf(quantites);
    }

    public String getPlateforme() {
        return plateforme;
    }

    @Override
    public String toString() {
        return "Streaming \"" + getTitre() + "\" sur "
                + plateforme + " ("
                + nombreParticipants + " participants)";
    }
}