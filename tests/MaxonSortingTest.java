import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/** Проверки уровня 3 и регрессионные проверки уровней 1–2 */
public class MaxonSortingTest {
    public static void main(String[] args) throws IOException {
        testAlgorithmsOnIntegers();
        testAlgorithmsOnStrings();
        testProductServiceAndOriginalOrder();
        testMenuIntegration();
        System.out.println("Все проверки сортировки и совместимости уровней 1–3 пройдены.");
    }


    private static void testAlgorithmsOnIntegers() {
        for (SortAlgorithm<Integer> algorithm : integerAlgorithms()) {
            checkSorted(algorithm, List.of(), Comparator.naturalOrder());
            checkSorted(algorithm, List.of(1), Comparator.naturalOrder());
            checkSorted(algorithm, List.of(3, 2, 2, 1, -1, 3), Comparator.naturalOrder());
            checkSorted(algorithm, List.of(3, 2, 2, 1, -1, 3), Comparator.reverseOrder());
            checkSorted(algorithm, List.of(7, 7, 7, 7, 7), Comparator.naturalOrder());

            Random random = new Random(20261018);
            MaxonArrayList<Integer> numbers = new MaxonArrayList<>();
            for (int i = 0; i < 2000; i++) {
                numbers.add(random.nextInt(201) - 100);
            }
            checkSorted(algorithm, numbers, Comparator.naturalOrder());
            checkSorted(algorithm, numbers, Comparator.reverseOrder());
        }
    }

    private static void testAlgorithmsOnStrings() {
        for (SortAlgorithm<String> algorithm : List.<SortAlgorithm<String>>of(
                new MaxonMergeSort<>(), new MaxonQuickSort<>())) {
            checkSorted(algorithm, List.of("z", "b", "aa", "x"), Comparator.naturalOrder());
            checkSorted(algorithm, List.of("z", "b", "aa", "x"), Comparator.reverseOrder());
        }
    }

    private static void testProductServiceAndOriginalOrder() throws IOException {
        ProductService service = new ProductService();
        for (Product product : new ProductFileReader().readProducts(Path.of("data/products.txt"))) {
            service.addProduct(product);
        }
        check(service.getProductCount() == 7, "Количество товаров из файла");
        check(service.processProducts().stream().map(s -> s.split(" — ")[0]).toList()
                .equals(List.of("Кресло", "Монитор", "Телефон", "Ноутбук")),
                "Фильтрация + sorted + map через Stream API уровня 1");
        for (SortAlgorithm<Product> algorithm : List.<SortAlgorithm<Product>>of(
                new MaxonMergeSort<>(), new MaxonQuickSort<>())) {
            List<Product> ascending = service.sortProducts(algorithm, true);
            List<Product> descending = service.sortProducts(algorithm, false);
            check(ascending instanceof MaxonArrayList<?>, "Собственная коллекция в результате");
            check(ascending.stream().map(Product::getName).toList().equals(List.of(
                    "Мышь", "Клавиатура", "Флешка", "Кресло", "Монитор", "Телефон", "Ноутбук")),
                    "Все товары по возрастанию цены");
            check(descending.stream().map(Product::getName).toList().equals(List.of(
                    "Ноутбук", "Телефон", "Монитор", "Кресло", "Флешка", "Клавиатура", "Мышь")),
                    "Все товары по убыванию цены");
            check(service.getProductAt(0).getName().equals("Ноутбук"), "Исходный порядок не изменён");
        }
        check(service.removeProductAt(1).getName().equals("Мышь"), "remove из MaxonArrayList");
        service.addProduct(new Product(8, "Планшет", new BigDecimal("12000"), 4));
        check(service.getProductCount() == 7, "add/size собственной коллекции");
    }

    private static void testMenuIntegration() {
        String[] scenarios = {
                "2\ndata/products.txt\n7\n1\n1\n7\n2\n2\n0\n",
                "2\ndata/products.txt\n7\n2\n1\n7\n1\n2\n0\n",
                "7\n0\n",                            // Пустой список
                "2\ndata/products.txt\n7\n99\n0\n", // Неверный алгоритм
                "2\ndata/products.txt\n7\n1\n99\n0\n" // Неверное направление
        };
        for (int i = 0; i < scenarios.length; i++) {
            String output = runMenu(scenarios[i]);
            check(output.contains("Программа завершена."), "Меню завершилось в сценарии " + i);
            if (i < 2) {
                check(output.contains("MaxonMergeSort") && output.contains("MaxonQuickSort"),
                        "Выбор обоих алгоритмов в сценарии " + i);
                check(output.contains("ID: 1") && output.contains("ID: 7"),
                        "Результат сортировки в сценарии " + i);
            }
        }
    }

    private static String runMenu(String input) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try (PrintStream printStream = new PrintStream(bytes, true, StandardCharsets.UTF_8);
             Scanner scanner = new Scanner(input)) {
            System.setOut(printStream);
            new ConsoleMenu(scanner, new ProductService(), new ProductFileReader()).run();
        } finally {
            System.setOut(originalOut);
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }

    private static List<SortAlgorithm<Integer>> integerAlgorithms() {
        return List.of(new MaxonMergeSort<>(), new MaxonQuickSort<>());
    }

    private static <T> void checkSorted(SortAlgorithm<T> algorithm, List<T> original,
                                        Comparator<? super T> comparator) {
        List<T> values = new MaxonArrayList<>();
        values.addAll(original);
        Map<T, Integer> before = frequencies(values);
        algorithm.sort(values, comparator);
        check(before.equals(frequencies(values)), "Алгоритм сохранил элементы");
        for (int i = 1; i < values.size(); i++) {
            check(comparator.compare(values.get(i - 1), values.get(i)) <= 0,
                    "Некорректная сортировка: " + algorithm.getClass().getSimpleName());
        }
    }

    private static <T> Map<T, Integer> frequencies(List<T> values) {
        Map<T, Integer> frequencies = new HashMap<>();
        for (T value : values) {
            frequencies.merge(value, 1, Integer::sum);
        }
        return frequencies;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
