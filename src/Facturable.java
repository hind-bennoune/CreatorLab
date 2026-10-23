import java.math.BigDecimal;
import java.time.Duration;

// Une interface = un contrat commun, sans imposer comment calculer le tarif.
public interface Facturable {
    String getLibelle();
    BigDecimal calculerPrix(Duration duree);
}
