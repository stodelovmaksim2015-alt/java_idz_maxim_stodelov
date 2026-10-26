import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ProductFileReader {
    public List<Product> readProducts(Path path) throws IOException {
        // Результат чтения также хранится в собственной коллекции.
        List<Product> result = new MaxonArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank() || line.stripLeading().startsWith("#")) {
                    continue;
                }

                try {
                    String[] fields = line.split(";", -1);
                    if (fields.length != 4) {
                        throw new IllegalArgumentException("требуются четыре поля через точку с запятой.");
                    }

                    int id = Integer.parseInt(fields[0].trim());
                    String name = fields[1].trim();
                    BigDecimal price = new BigDecimal(fields[2].trim().replace(',', '.'));
                    int quantity = Integer.parseInt(fields[3].trim());
                    result.add(new Product(id, name, price, quantity));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Ошибка в строке " + lineNumber + ": " + e.getMessage(), e);
                }
            }
        }
        return result;
    }
}
