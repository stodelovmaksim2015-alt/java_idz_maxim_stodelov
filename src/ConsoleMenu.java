import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleMenu {
    private final Scanner scanner;
    private final ProductService productService;
    private final ProductFileReader fileReader;

    public ConsoleMenu(Scanner scanner, ProductService productService, ProductFileReader fileReader) {
        this.scanner = scanner;
        this.productService = productService;
        this.fileReader = fileReader;
    }


    public void run() {
        while (true) {
            System.out.println("\n===== МЕНЮ ТОВАРОВ =====");
            System.out.println("1. Добавить товар вручную");
            System.out.println("2. Загрузить товары из TXT-файла");
            System.out.println("3. Обработать товары через Stream API (уровень 1)");
            System.out.println("4. Показать все товары (MaxonArrayList уровень 2)");
            System.out.println("5. Получить товар по номеру (get)");
            System.out.println("6. Удалить товар по номеру (remove)");
            System.out.println("7. Собственная сортировка (уровень 3)");
            System.out.println("0. Выход");
            System.out.print("Выберите пункт меню: ");

            if (!scanner.hasNextLine()) {
                System.out.println("Ввод завершён.");
                return;
            }


            String command = scanner.nextLine().trim();
            switch (command) {
                case "1" -> addManually();
                case "2" -> loadFromFile();
                case "3" -> process();
                case "4" -> showAllProducts();
                case "5" -> showProductAt();
                case "6" -> removeProductAt();
                case "7" -> sortWithMaxonAlgorithm();
                case "0" -> {
                    System.out.println("Программа завершена.");
                    return;
                }
                default -> System.out.println("Неизвестный пункт меню. Попробуйте ещё раз.");
            }
        }
    }

    private void addManually() {
        try {
            System.out.print("ID товара: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Название товара: ");
            String name = scanner.nextLine();

            System.out.print("Цена товара: ");
            BigDecimal price = new BigDecimal(scanner.nextLine().trim().replace(',', '.'));

            System.out.print("Количество товара: ");
            int quantity = Integer.parseInt(scanner.nextLine().trim());

            productService.addProduct(new Product(id, name, price, quantity));
            System.out.println("Товар добавлен. Всего товаров: " + productService.getProductCount());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("Не удалось завершить ввод товара.");
        }
    }

    private void loadFromFile() {
        System.out.print("Введите путь к TXT-файлу (например, data/products.txt): ");
        if (!scanner.hasNextLine()) {
            System.out.println("Путь к файлу не введён.");
            return;
        }

        String fileName = scanner.nextLine().trim();
        if (fileName.isEmpty()) {
            System.out.println("Путь к файлу не может быть пустым.");
            return;
        }

        try {
            List<Product> imported = fileReader.readProducts(Path.of(fileName));
            int loaded = 0;
            int skipped = 0;

            for (Product product : imported) {
                try {
                    productService.addProduct(product);
                    loaded++;
                } catch (IllegalArgumentException e) {
                    System.out.println("Пропущен товар: " + e.getMessage());
                    skipped++;
                }
            }
            System.out.println("Загружено: " + loaded + ", пропущено: " + skipped
                    + ", всего товаров: " + productService.getProductCount());
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка данных в файле: " + e.getMessage());
        }
    }

    private void process() {
        System.out.println("\nТовары с ценой выше 10000 руб. (по возрастанию цены):");
        List<String> result = productService.processProducts();
        if (result.isEmpty()) {
            System.out.println("Подходящих товаров нет.");
            return;
        }
        result.forEach(System.out::println);
    }

    private void showAllProducts() {
        int count = productService.getProductCount();
        if (count == 0) {
            System.out.println("Коллекция товаров пуста.");
            return;
        }

        System.out.println("\nСодержимое MaxonArrayList (элементов: " + count + "):");
        for (int index = 0; index < count; index++) {
            printProduct(index + 1, productService.getProductAt(index));
        }
    }

    private void showProductAt() {
        int index = askProductIndex();
        if (index == -1) {
            return;
        }
        printProduct(index + 1, productService.getProductAt(index));
    }

    private void removeProductAt() {
        int index = askProductIndex();
        if (index == -1) {
            return;
        }
        Product removed = productService.removeProductAt(index);
        System.out.println("Удалён товар: " + removed.getName() + " (ID " + removed.getId() + ")");
        System.out.println("Осталось товаров: " + productService.getProductCount());
    }

    private void sortWithMaxonAlgorithm() {
        if (productService.getProductCount() == 0) {
            System.out.println("Коллекция товаров пуста. Сначала добавьте товары.");
            return;
        }

        System.out.println("\nВыберите собственный алгоритм:");
        System.out.println("1. MaxonMergeSort (сортировка слиянием)");
        System.out.println("2. MaxonQuickSort (быстрая сортировка)");
        System.out.print("Номер алгоритма: ");
        if (!scanner.hasNextLine()) {
            System.out.println("Ввод завершён.");
            return;
        }

        // Полиморфизм: общий тип SortAlgorithm, реализация выбирается пользователем.
        String choice = scanner.nextLine().trim();
        SortAlgorithm<Product> algorithm;
        String algorithmName;
        switch (choice) {
            case "1" -> {
                algorithm = new MaxonMergeSort<>();
                algorithmName = "MaxonMergeSort";
            }
            case "2" -> {
                algorithm = new MaxonQuickSort<>();
                algorithmName = "MaxonQuickSort";
            }
            default -> {
                System.out.println("Неизвестный алгоритм. Сортировка отменена.");
                return;
            }
        }

        System.out.println("\nВыберите направление сортировки по цене:");
        System.out.println("1. По возрастанию");
        System.out.println("2. По убыванию");
        System.out.print("Направление: ");
        if (!scanner.hasNextLine()) {
            System.out.println("Ввод завершён.");
            return;
        }
        String direction = scanner.nextLine().trim();
        if (!direction.equals("1") && !direction.equals("2")) {
            System.out.println("Неизвестное направление. Сортировка отменена.");
            return;
        }

        boolean ascending = direction.equals("1");
        List<Product> sorted = productService.sortProducts(algorithm, ascending);
        System.out.println("\nРезультат " + algorithmName + " (цена "
                + (ascending ? "по возрастанию" : "по убыванию") + "):");
        for (int i = 0; i < sorted.size(); i++) {
            printProduct(i + 1, sorted.get(i));
        }
    }

    private int askProductIndex() {
        int count = productService.getProductCount();
        if (count == 0) {
            System.out.println("Коллекция товаров пуста.");
            return -1;
        }

        System.out.print("Введите номер товара от 1 до " + count + " (не ID): ");
        if (!scanner.hasNextLine()) {
            System.out.println("Ввод завершён.");
            return -1;
        }

        try {
            int number = Integer.parseInt(scanner.nextLine().trim());
            if (number < 1 || number > count) {
                System.out.println("Ошибка: номер должен быть от 1 до " + count + ".");
                return -1;
            }
            return number - 1;
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: необходимо ввести целый номер товара.");
            return -1;
        }
    }

    private void printProduct(int number, Product product) {
        System.out.println(number + ". ID: " + product.getId()
                + " | " + product.getName()
                + " | Цена: " + product.getPrice().toPlainString() + " руб."
                + " | Количество: " + product.getQuantity());
    }
}
