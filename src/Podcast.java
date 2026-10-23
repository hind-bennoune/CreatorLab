import java.util.Set;

public class Podcast extends ProjetCreatif {
    private final int nombreInvites;
    private final boolean avecVideo;

    public Podcast(int id, Client client, String titre, int nombreInvites, boolean avecVideo) {
        super(id, client, titre); // appel au constructeur de la classe mere
        if (nombreInvites < 0) throw new IllegalArgumentException("Invites negatifs");
        this.nombreInvites = nombreInvites;
        this.avecVideo = avecVideo;
    }

    @Override public TypeProduction getType() { return TypeProduction.PODCAST; }
    @Override public int getNombreParticipants() { return nombreInvites + 1; }
    @Override public Set<TypeEquipement> getMaterielRequis() {
        return avecVideo
            ? Set.of(TypeEquipement.MICROPHONE, TypeEquipement.CAMERA)
            : Set.of(TypeEquipement.MICROPHONE);
    }
}
