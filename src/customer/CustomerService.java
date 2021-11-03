package customer;

import product.Product;
import product.ProductException;
import product.ProductService;

import java.util.*;

/**
 * CustomerService handle all about customer.
 */
public class CustomerService {
    private List<OrderItem> orderItems;

    // depositedCoins is map with key = nominalValue and value = count
    private Map<Long, Integer> depositedCoins;
    private final ProductService productService;

    public CustomerService(ProductService productService) {
        this.productService = productService;
        this.orderItems = new ArrayList<>();
        this.depositedCoins = new HashMap<>();
    }

    public void deposit(Long nominalValue, Integer count) {
        if (!this.depositedCoins.containsKey(nominalValue)) {
            this.depositedCoins.put(nominalValue, count);
        } else {
            this.depositedCoins.put(nominalValue,
                    this.depositedCoins.get(nominalValue) + count);
        }
    }

    public void addOrderItem(OrderItem orderItem) {
        verifyOrderItemAvailable(orderItem);
        boolean itemIsAdded = false;

        // orderItem in orderItems -> increase count of exists item in orderItems
        for (OrderItem item : this.orderItems) {
            if (item.getId() == orderItem.getId()) {
                item.addQuantity(orderItem.getQuantity());
                itemIsAdded = true;
                break;
            }
        }

        // orderItem not in orderItems -> add orderItem
        if (!itemIsAdded) {
            this.orderItems.add(orderItem);
        }
    }

    public Long getCustomerBalance() {
        long total = 0L;
        for (Map.Entry<Long, Integer> coin : this.depositedCoins.entrySet()) {
            total += coin.getKey() * coin.getValue();
        }
        return total;
    }

    public List<OrderItem> getOrderItems() {
        return this.orderItems;
    }

    public Long getOrderItemsPrice() {
        Long totalPrice = 0L;
        for (OrderItem item : this.orderItems) {
            Long totalPriceOfItem = item.getTotalPrice();
            totalPrice += totalPriceOfItem;
        }
        return totalPrice;
    }

    public void emptyOrderAndDepositedCoins() {
        this.orderItems = new ArrayList<>();
        this.depositedCoins = new HashMap<>();
    }

    private void verifyOrderItemAvailable(OrderItem orderItem) throws ProductException {
        int quantityInOrder = this.getOrderItemQuantity(orderItem.getId());
        int quantityNewOrder = orderItem.getQuantity();
        Product product = this.productService.getProductById(orderItem.getId())
                .orElseThrow(() -> new ProductException("Product not found"));
        if (product.getAvailableQuantity() -  quantityInOrder - quantityNewOrder  < 0) {
            throw new ProductException(String.format("Product is out of stock. Remaining %d items",
                    product.getAvailableQuantity()));
        }
    }

    private int getOrderItemQuantity(int orderItemId) {
        for (OrderItem item : this.orderItems) {
            if (orderItemId == item.getId()) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    public Map<Long, Integer> getDepositedCoins() {
        return depositedCoins;
    }

    public void cancelOrder() {
        this.orderItems = Collections.emptyList();
    }
}
