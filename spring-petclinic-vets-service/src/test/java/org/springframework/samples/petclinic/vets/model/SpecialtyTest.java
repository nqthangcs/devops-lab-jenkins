package org.springframework.samples.petclinic.vets.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyTest {

    private Specialty specialty;

    @BeforeEach
    void setUp() {
        specialty = new Specialty();
    }

    @Test
    void shouldSetAndGetName() {
        specialty.setName("dentistry");
        assertThat(specialty.getName()).isEqualTo("dentistry");
    }

    @Test
    void shouldHandleNullName() {
        specialty.setName(null);
        assertThat(specialty.getName()).isNull();
    }

    @Test
    void shouldReturnNullIdWhenNotSet() {
        assertThat(specialty.getId()).isNull();
    }
}
