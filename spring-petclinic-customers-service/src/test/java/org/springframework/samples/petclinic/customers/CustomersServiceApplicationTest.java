package org.springframework.samples.petclinic.customers;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

class CustomersServiceApplicationTest {

    @Test
    void shouldRunSpringApplication() {
        // Given
        String[] args = new String[]{"--spring.profiles.active=test"};

        // Mock SpringApplication.run as a static method
        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            // When
            CustomersServiceApplication.main(args);

            // Then
            springApplicationMock.verify(() ->
                SpringApplication.run(CustomersServiceApplication.class, args));
        }
    }
}
