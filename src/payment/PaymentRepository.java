package payment;

import java.util.List;
import java.util.Map;

public interface PaymentRepository {
    Map<Long, Integer> getReserves();
    void updateReserves(Map<Long, Integer> reserves);
    List<Coin> getSupportedNominalValues();
}
