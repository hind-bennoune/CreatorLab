// Exception metier verifiee : le code appelant doit la traiter.
public class ReservationException extends Exception {
    public ReservationException(String message) { super(message); }
}
