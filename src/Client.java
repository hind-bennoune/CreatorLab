public class Client {
    private int id;
    private String nom;
    private String email;

    public Client(int id, String nom, String email) {
        this.id = id;
        this.nom = nom;
        this.email = email;
    }

    public void modifierEmail(String nouvelEmail) {
        if (nouvelEmail == null || !nouvelEmail.contains("@")) {
            throw new IllegalArgumentException("E-mail invalide");
        }
        email = nouvelEmail;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getEmail() { return email; }
}