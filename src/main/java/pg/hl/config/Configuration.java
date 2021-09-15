package pg.hl.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Getter
@Setter
@ToString
public final class Configuration {
    private static Configuration instance;

    public static Configuration getInstance() throws IOException {
        if (instance == null) {
            instance = createInstance();
        }
        return instance;
    }

    private static Configuration createInstance() throws IOException {
        var filename = "postgresHighLoad.yml";
        var path = Paths.get(filename);
        if (Files.exists(path)) {
            try (InputStream inputStream = Files.newInputStream(path)) {
                System.out.println("Load config from file");
                return load(inputStream);
            }
        }

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename)) {
            return load(inputStream);
        }
    }

    private static Configuration load(InputStream inputStream) {
        return new Yaml().loadAs(inputStream, Configuration.class);
    }

    private Startup startup;
    private Connection connection;
    private Hibernate hibernate;
    private Benchmark benchmark;
    private Entities entities;
}

