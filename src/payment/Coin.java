package payment;

public class Coin {
    private final Integer id;
    private final Long nominalValue;
    private int count;

    public Coin(Integer id, Long nominalValue) {
        this.id = id;
        this.nominalValue = nominalValue;
        this.count = 1;
    }

    public Integer getId() {
        return id;
    }

    public Long getNominalValue() {
        return nominalValue;
    }

    @Override
    public String toString() {
        return id + ". " + nominalValue + " VND";
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void add(int count) {
        this.count += count;
    }
}
