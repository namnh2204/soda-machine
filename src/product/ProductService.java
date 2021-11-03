package product;

import customer.OrderItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ProductService {
    private final ProductRepository repository;
    public ProductService(ProductRepository productRepository) {
        this.repository = productRepository;
    }

    public List<Product> listProducts() {
        return repository.getProductList();
    }

    public Optional<Product> getProductById(int id) {
        return repository.getProductById(id);
    }

    public void updateProductQuantity(List<OrderItem> orderItems) {
        List<Product> updatedProducts = new ArrayList<>(Collections.emptyList());
        for (OrderItem orderItem : orderItems) {
            Product product = getProductById(orderItem.getId())
                    .orElseThrow(() -> new ProductException("Product not found"));
            product.setSales(product.getSales() + orderItem.getQuantity());
            updatedProducts.add(product);
        }

        this.repository.saveAll(updatedProducts);
    }
}
