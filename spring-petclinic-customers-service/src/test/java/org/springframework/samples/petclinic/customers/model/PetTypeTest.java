package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PetTypeTest {

    private PetType petType;

    @BeforeEach
    void setUp() {
        petType = new PetType();
    }

    @Test
    void shouldSetAndGetId() {
        // Given
        petType.setId(1);

        // Then
        assertThat(petType.getId()).isEqualTo(1);
    }

    @Test
    void shouldSetAndGetName() {
        // Given
        petType.setName("Dog");

        // Then
        assertThat(petType.getName()).isEqualTo("Dog");
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        petType.setId(null);
        petType.setName(null);

        // Then
        assertThat(petType.getId()).isNull();
        assertThat(petType.getName()).isNull();
    }

    @Test
    void shouldSetAndGetAllFields() {
        // Given
        petType.setId(2);
        petType.setName("Cat");

        // Then
        assertThat(petType.getId()).isEqualTo(2);
        assertThat(petType.getName()).isEqualTo("Cat");
    }
}
