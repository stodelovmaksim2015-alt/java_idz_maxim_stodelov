import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class ProductService {
    private static final BigDecimal PRICE_LIMIT = new BigDecimal("10000");
    // Уровень 2: собственная коллекция вместо java.util.ArrayList.
    private final List<Product> products = new MaxonArrayList<>();

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Нельзя добавить пустой товар.");
        }
        boolean idExists = products.stream().anyMatch(item -> item.getId() == product.getId());
        if (idExists) {
            throw new IllegalArgumentException("Товар с ID " + product.getId() + " уже существует.");
        }
        products.add(product);
    }



    public int getProductCount() {
        return products.size();
    }

    public Product getProductAt(int index) {
        return products.get(index);
    }

    public Product removeProductAt(int index) {
        return products.remove(index);
    }

    // Уровень 1: фильтрация, сортировка, map и вывод результата в ConsoleMenu.
    public List<String> processProducts() {
        return products.stream()
                .filter(product -> product.getPrice().compareTo(PRICE_LIMIT) > 0)
                .sorted(Comparator.comparing(Product::getPrice))
                .map(product -> product.getName() + " — " + product.getPrice().toPlainString() + " руб.")
                .toList();
    }

    // Уровень 3: сортируем копию товаров собственным алгоритмом, не меняя исходный список.
    public List<Product> sortProducts(SortAlgorithm<Product> algorithm, boolean ascending) {
        Objects.requireNonNull(algorithm, "Алгоритм сортировки не указан.");
        List<Product> result = new MaxonArrayList<>();
        result.addAll(products);
        Comparator<Product> comparator = Comparator.comparing(Product::getPrice);
        if (!ascending) {
            comparator = comparator.reversed();
        }
        algorithm.sort(result, comparator);
        return result;
    }
}
