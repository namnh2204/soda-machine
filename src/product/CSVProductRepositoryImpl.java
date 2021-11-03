package product;

import common.CSVUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CSVProductRepositoryImpl implements ProductRepository {
    public CSVProductRepositoryImpl() {
    }

    public List<Product> getProductList() {
        try {
            return CSVUtil.readFromCSV();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return new ArrayList<>();
    }

    public Optional<Product> getProductById(int id) {
        List<Product> productList = getProductList();
        for (Product product : productList) {
            if (product.getId() == id) {
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

    public void saveAll(List<Product> updatedProducts) {
        try {
            CSVUtil.saveToCSV(updatedProducts);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
