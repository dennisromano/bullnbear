package org.dennisromano.dailyexchange.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DailyExchangeSpringBootApplicationTest {

    @Test
    @DisplayName("Should load the application context successfully")
    void contextLoads(ApplicationContext context) {
        assertNotNull(context, "The Spring application context should not be null");
    }

    @Test
    @DisplayName("Should ensure the main method runs without throwing exceptions")
    void mainMethodStarts() {
        DailyExchangeSpringBootApplication.main();
    }
}
