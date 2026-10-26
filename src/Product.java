import java.math.BigDecimal;

public class Product {
    private final int id;
    private final String name;
    private final BigDecimal price;
    private final int quantity;

    public Product(int id, String name, BigDecimal price, int quantity) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID товара должен быть больше или равен нулю.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название товара не может быть пустым.");
        }
        if (price == null || price.signum() < 0 || price.scale() > 2) {
            throw new IllegalArgumentException(
                    "Цена должна быть неотрицательной, с точностью до двух знаков после запятой.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Количество товара не может быть отрицательным.");
        }

        this.id = id;
        this.name = name.trim();
        this.price = price;
        this.quantity = quantity;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
