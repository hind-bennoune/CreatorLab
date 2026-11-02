// Exception verifiee : le code appelant doit la traiter (try/catch)
public class ReservationException extends Exception {
    public ReservationException(String message) {
        super(message);
    }
}