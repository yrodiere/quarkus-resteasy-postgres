package org.acme;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(value = FlywayTestResource.class, restrictToAnnotatedClass = true)

public class FlywayTest {
    @Test
    public void test() {
        String s="";
    }
}
