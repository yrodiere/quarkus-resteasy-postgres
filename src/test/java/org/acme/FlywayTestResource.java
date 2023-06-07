package org.acme;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.eclipse.microprofile.config.ConfigProvider;
import org.flywaydb.core.Flyway;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class FlywayTestResource implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {

        copyApplicationPropertiesToSystemProperties();

        var config = ConfigProvider.getConfig();
        var url = config.getValue("quarkus.datasource.jdbc.url", String.class);
        var username = config.getValue("quarkus.datasource.username", String.class);
        var password = config.getValue("quarkus.datasource.password", String.class);
        var cleanDisabled = !config.getValue("quarkus.flyway.clean-at-start", Boolean.class);

        Flyway flyway = Flyway.configure()
                .dataSource(url, username, password)
                .cleanDisabled(cleanDisabled)
                .load();

        flyway.clean();
        flyway.migrate();

        return Map.of("some.service.url", "localhost:");
    }

    @Override
    public synchronized void stop() {
    }

    @Override
    public void inject(TestInjector testInjector) {
    }

    public void copyApplicationPropertiesToSystemProperties() {
        Properties properties = new Properties();

        String path = this.getClass().getClassLoader().getResource("application.properties").getPath();

        try (FileInputStream input = new FileInputStream(path)) {
            properties.load(input);

            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                System.setProperty(key, value);
            }

            System.out.println("Properties copied to environment variables successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}