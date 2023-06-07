package org.acme;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.eclipse.microprofile.config.ConfigProvider;
import org.flywaydb.core.Flyway;

import java.util.Map;

public class FlywayTestResource implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {

        var config = ConfigProvider.getConfig();
        var url = config.getValue("quarkus.datasource.jdbc.url", String.class);
        var username = config.getValue("quarkus.datasource.username", String.class);
        var password = config.getValue("quarkus.datasource.password", String.class);

        Flyway flyway = Flyway.configure().dataSource(url, username, password).load();
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
}