package seoulmate.security.env;

import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Properties;

public class CsvPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(resource.getReader())) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(",")) {
                    String[] parts = line.split(",", 2);
                    properties.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        return new PropertiesPropertySource(name != null ? name : "csv-property-source", properties);
    }
}
