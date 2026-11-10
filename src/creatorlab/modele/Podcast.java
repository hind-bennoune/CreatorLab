package creatorlab.modele;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Un podcast : un animateur et ses invites autour d'une table, en audio ou filme.
public class Podcast extends ProjetCreatif {
    // Regle reelle : autour d'une table de podcast, on depasse rarement 5 invites.
    public static final int MAX_INVITES = 5;

    private final String theme;
    private final int nombreInvites;
    private final boolean avecVideo;

    // Constructeur complet : avec un theme (Tech, Sport, Business...).
    public Podcast(int id, Client client, String titre, String theme,
                   int nombreInvites, boolean avecVideo) {
        super(id, client, titre);
        if (theme == null || theme.isBlank()) {
            throw new IllegalArgumentException("Theme du podcast obligatoire");
        }
        if (nombreInvites < 0 || nombreInvites > MAX_INVITES) {
            throw new IllegalArgumentException("Nombre d'invites entre 0 et " + MAX_INVITES);
        }
        this.theme = theme;
        this.nombreInvites = nombreInvites;
        this.avecVideo = avecVideo;
    }

    // Constructeur court (utilise par Main et les tests) : theme "General" par defaut.
    public Podcast(int id, Client client, String titre, int nombreInvites, boolean avecVideo) {
        this(id, client, titre, "General", nombreInvites, avecVideo);
    }

    @Override
    public String getType() {
        return "PODCAST";
    }

    // L'animateur compte aussi : invites + 1.
    @Override
    public int getNombreParticipants() {
        return nombreInvites + 1;
    }

    // Le materiel d'un vrai studio podcast.
    @Override
    public Set<String> getMaterielRequis() {
        Set<String> materiel = new HashSet<>();
        // Pour enregistrer le son
        materiel.add("MICROPHONE");
        materiel.add("PIED_MICRO");
        materiel.add("FILTRE_ANTI_POP");
        materiel.add("CASQUE");
        materiel.add("INTERFACE_AUDIO"); // table de mixage OU interface audio
        // En plus, si le podcast est filme
        if (avecVideo) {
            materiel.add("CAMERA");
            materiel.add("ECLAIRAGE");
        }
        return Set.copyOf(materiel); // copie non modifiable
    }

    // Chaque participant (animateur inclus) a un micro, un casque,
    // un pied de micro et un filtre anti-pop personnels.
    @Override
    public Map<String, Integer> getQuantitesMaterielRequis() {
        Map<String, Integer> quantites = new HashMap<>(super.getQuantitesMaterielRequis());
        int participants = getNombreParticipants();
        quantites.put("MICROPHONE", participants);
        quantites.put("CASQUE", participants);
        quantites.put("PIED_MICRO", participants);
        quantites.put("FILTRE_ANTI_POP", participants);
        return Map.copyOf(quantites);
    }

    // En vrai, chaque personne a son propre micro et son propre casque.
    public int getNombreMicrosConseilles() {
        return getNombreParticipants();
    }

    public String getTheme() { return theme; }
    public int getNombreInvites() { return nombreInvites; }
    public boolean isAvecVideo() { return avecVideo; }

    @Override
    public String toString() {
        String format;
        if (avecVideo) {
            format = "audio + video";
        } else {
            format = "audio";
        }
        return "Podcast \"" + getTitre() + "\" (" + theme + ", " + format + ", "
                + getNombreParticipants() + " participants, "
                + getNombreMicrosConseilles() + " micros et casques conseilles)";
    }
}