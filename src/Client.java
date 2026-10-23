public class Client {
    private final int id;
    private String nom;
    private String email;

    public Client(int id, String nom, String email) {
        if (id <= 0 || nom == null || nom.isBlank()
                || email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Informations client invalides");
        }
        this.id = id;
        this.nom = nom;
        this.email = email;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getEmail() { return email; }

    public void modifierEmail(String nouvelEmail) {
        if (nouvelEmail == null || !nouvelEmail.contains("@")) {
            throw new IllegalArgumentException("E-mail invalide");
        }
        email = nouvelEmail;
    }
}
