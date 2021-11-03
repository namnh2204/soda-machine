package payment;

import common.CSVUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVPaymentRepositoryImpl implements PaymentRepository {

    @Override
    public Map<Long, Integer> getReserves() {
        try {
            return CSVUtil.readReservesFromCSV();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return new HashMap<>();
        }
    }

    @Override
    public void updateReserves(Map<Long, Integer> reserves) {
        try {
            CSVUtil.updateReservesToCSV(reserves);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public List<Coin> getSupportedNominalValues() {
        try {
            return CSVUtil.getSupportedNominalValues();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return new ArrayList<>();
        }
    }
}
