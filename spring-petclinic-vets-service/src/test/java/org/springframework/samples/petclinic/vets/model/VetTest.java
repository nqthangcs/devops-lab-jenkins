package org.springframework.samples.petclinic.vets.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VetTest {

    private Vet vet;
    private Specialty specialty1;
    private Specialty specialty2;

    @BeforeEach
    void setUp() {
        vet = new Vet();
        specialty1 = new Specialty();
        specialty1.setName("radiology");
        specialty2 = new Specialty();
        specialty2.setName("surgery");
    }

    @Test
    void shouldSetAndGetId() {
        vet.setId(1);
        assertThat(vet.getId()).isEqualTo(1);
        vet.setId(null);
        assertThat(vet.getId()).isNull();
    }

    @Test
    void shouldSetAndGetFirstName() {
        vet.setFirstName("John");
        assertThat(vet.getFirstName()).isEqualTo("John");
        vet.setFirstName(null);
        assertThat(vet.getFirstName()).isNull();
    }

    @Test
    void shouldSetAndGetLastName() {
        vet.setLastName("Doe");
        assertThat(vet.getLastName()).isEqualTo("Doe");
        vet.setLastName(null);
        assertThat(vet.getLastName()).isNull();
    }

    @Test
    void shouldInitializeSpecialtiesLazily() {
        assertThat(vet.getSpecialtiesInternal())
            .isNotNull()
            .isInstanceOf(HashSet.class)
            .isEmpty();
    }

    @Test
    void shouldAddSpecialty() {
        vet.addSpecialty(specialty1);
        assertThat(vet.getSpecialtiesInternal()).containsExactly(specialty1);
        assertThat(vet.getNrOfSpecialties()).isEqualTo(1);

        vet.addSpecialty(specialty2);
        assertThat(vet.getSpecialtiesInternal()).containsExactlyInAnyOrder(specialty1, specialty2);
        assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
    }

    @Test
    void shouldReturnSortedSpecialties() {
        vet.addSpecialty(specialty2); // surgery
        vet.addSpecialty(specialty1); // radiology

        List<Specialty> specialties = vet.getSpecialties();
        assertThat(specialties)
            .hasSize(2)
            .extracting(Specialty::getName)
            .containsExactly("radiology", "surgery");
    }

    @Test
    void shouldReturnEmptySpecialtiesListWhenNoneAdded() {
        List<Specialty> specialties = vet.getSpecialties();
        assertThat(specialties).isEmpty();
    }

    @Test
    void shouldReturnUnmodifiableSpecialtiesList() {
        vet.addSpecialty(specialty1);
        List<Specialty> specialties = vet.getSpecialties();
        assertThatThrownBy(() -> specialties.add(specialty2))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldReturnCorrectNumberOfSpecialties() {
        assertThat(vet.getNrOfSpecialties()).isEqualTo(0);
        vet.addSpecialty(specialty1);
        assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
        vet.addSpecialty(specialty2);
        assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
    }
}
