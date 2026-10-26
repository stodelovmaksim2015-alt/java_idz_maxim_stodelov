import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;


/** Автоматические проверки*/
public class MaxonArrayListTest {
    public static void main(String[] args) throws IOException {
        testDynamicArray();
        testListIntegration();
        testServiceAndFile();
        System.out.println("Все тесты MaxonArrayList и уровней 1–2 пройдены.");
    }

    private static void testDynamicArray() {
        MaxonArrayList<Integer> list = new MaxonArrayList<>();
        check(list.size() == 0, "Пустой список");
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        check(list.size() == 100, "Автоматическое расширение");
        check(list.get(0) == 0 && list.get(99) == 99, "get после расширения");
        check(list.remove(0) == 0 && list.remove(98) == 99, "Удаление первого/последнего");
        check(list.remove(48) == 49 && list.get(48) == 50, "Удаление из середины со сдвигом");
        list.add(1, 777);
        check(list.get(1) == 777 && list.size() == 98, "Вставка по индексу");
        check(list.set(1, 888) == 777 && list.get(1) == 888, "set");
        check(list.remove(Integer.valueOf(888)) && list.size() == 97, "remove(Object)");
        expectException(IndexOutOfBoundsException.class, () -> list.get(-1));
        expectException(IndexOutOfBoundsException.class, () -> list.get(list.size()));
        expectException(IndexOutOfBoundsException.class, () -> list.remove(list.size()));
        expectException(IndexOutOfBoundsException.class, () -> list.add(list.size() + 1, 3));
        list.clear();
        check(list.isEmpty(), "clear");
        list.add(42);
        check(list.get(0) == 42, "Повторное использование после clear");
    }

    private static void testListIntegration() {
        List<String> list = new MaxonArrayList<>();
        list.add("первый");
        list.add("второй");
        list.add("третий");
        check(list.stream().filter(s -> s.contains("р")).count() == 3,
                "Stream API поверх собственной коллекции");
        Iterator<String> iterator = list.iterator();
        check(iterator.next().equals("первый"), "Итератор AbstractList");
        iterator.remove();
        check(list.get(0).equals("второй"), "Удаление через итератор");
        Iterator<String> oldIterator = list.iterator();
        list.add("новый");
        expectException(ConcurrentModificationException.class, oldIterator::next);
    }

    private static void testServiceAndFile() throws IOException {
        ProductService service = new ProductService();
        List<Product> input = new ProductFileReader().readProducts(Path.of("data/products.txt"));
        check(input instanceof MaxonArrayList<?>, "Загрузка в MaxonArrayList");
        for (Product product : input) {
            service.addProduct(product);
        }
        check(service.getProductCount() == 7, "Импорт семи товаров");
        check(service.getProductAt(0).getName().equals("Ноутбук"), "get товара");
        check(service.processProducts().stream().map(s -> s.split(" — ")[0]).toList()
                        .equals(List.of("Кресло", "Монитор", "Телефон", "Ноутбук")),
                "Stream API первого уровня");
        check(service.removeProductAt(0).getId() == 1, "Удаление товара");
        check(service.getProductCount() == 6, "size после удаления");
        service.addProduct(new Product(1, "Ноутбук", new BigDecimal("85000"), 12));
        check(service.getProductCount() == 7, "Повторное добавление удалённого ID");
        expectException(IllegalArgumentException.class,
                () -> service.addProduct(new Product(1, "Повтор", new BigDecimal("100"), 2)));
    }




    private static void expectException(Class<? extends Throwable> exceptionType, Runnable action) {
        try {
            action.run();
        } catch (Throwable error) {
            if (exceptionType.isInstance(error)) {
                return;
            }
            throw new AssertionError("Ожидалось " + exceptionType.getSimpleName(), error);
        }
        throw new AssertionError("Ожидалось исключение " + exceptionType.getSimpleName());
    }


    private static void check(boolean condition, String name) {
        if (!condition) {
            throw new AssertionError("Проверка не пройдена: " + name);
        }
    }
}
