package customer;

import java.util.Objects;

/**
 * Ordered item on customer's cart
 */
public class OrderItem {
    private final int id;
    private final String name;
    private final Long itemPrice;
    private int quantity;

    public OrderItem(int id, String name, Long itemPrice, int quantity) {
        this.id = id;
        this.name = name;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getItemPrice() {
        return itemPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(int number) {
        this.quantity += number;
    }

    public Long getTotalPrice() {
        return this.itemPrice * this.quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem product = (OrderItem) o;
        return id == product.id && quantity == product.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantity);
    }

    @Override
    public String toString() {
        return name + "(" + itemPrice + "): " + quantity;
    }
}
