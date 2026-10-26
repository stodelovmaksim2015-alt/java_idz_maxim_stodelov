import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            ProductService productService = new ProductService();
            ProductFileReader fileReader = new ProductFileReader();
            ConsoleMenu menu = new ConsoleMenu(scanner, productService, fileReader);
            menu.run();
        }
    }
}
