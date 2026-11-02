import java.util.Set;

public class Podcast extends ProjetCreatif {
    private int nombreInvites;
    private boolean avecVideo;

    public Podcast(int id, Client client, String titre, int nombreInvites, boolean avecVideo) {
        super(id, client, titre); // appel au constructeur de la classe mere
        this.nombreInvites = nombreInvites;
        this.avecVideo = avecVideo;
    }

    @Override
    public TypeProduction getType() { return TypeProduction.PODCAST; }

    @Override
    public int getNombreParticipants() { return nombreInvites + 1; } // invites + animateur

    @Override
    public Set<TypeEquipement> getMaterielRequis() {
        if (avecVideo) {
            return Set.of(TypeEquipement.MICROPHONE, TypeEquipement.CAMERA);
        }
        return Set.of(TypeEquipement.MICROPHONE);
    }
}