package product;

import java.util.Objects;

/**
 * Item in stock.
 */
public class Product {
    private final Integer id;
    private final String name;
    private Long price;
    private int quantity;
    private int sales;

    public Product(Integer id, String name, Long price, int quantity, int sales) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.sales = sales;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getAvailableQuantity() {
        return quantity - sales;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + ". " + name + "(" + price + "VND)" + ": " + (quantity-sales) + " item(s)";
    }
}
