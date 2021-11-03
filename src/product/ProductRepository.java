package product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> getProductList();

    Optional<Product> getProductById(int id);

    void saveAll(List<Product> updatedProducts);
}
