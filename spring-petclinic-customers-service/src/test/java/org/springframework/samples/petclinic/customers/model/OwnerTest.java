package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OwnerTest {

    private Owner owner;

    @BeforeEach
    void setUp() {
        owner = new Owner();
    }

    @Test
    void shouldSetAndGetFields() {
        // Given
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setAddress("123 Main St");
        owner.setCity("Springfield");
        owner.setTelephone("5551234567");

        // Then
        assertThat(owner.getFirstName()).isEqualTo("John");
        assertThat(owner.getLastName()).isEqualTo("Doe");
        assertThat(owner.getAddress()).isEqualTo("123 Main St");
        assertThat(owner.getCity()).isEqualTo("Springfield");
        assertThat(owner.getTelephone()).isEqualTo("5551234567");
    }

    @Test
    void shouldInitializePetsWhenNull() {
        // Given
        // pets is null by default

        // When
        Set<Pet> pets = owner.getPetsInternal();

        // Then
        assertThat(pets).isNotNull();
        assertThat(pets).isEmpty();
    }

    @Test
    void shouldAddPetAndSetOwner() {
        // Given
        PetType petType = new PetType();
        petType.setId(1);
        petType.setName("Dog");

        Pet pet = new Pet();
        pet.setName("Fluffy");
        pet.setType(petType);
        pet.setBirthDate(new Date());

        // When
        owner.addPet(pet);

        // Then
        assertThat(owner.getPetsInternal()).containsExactly(pet);
        assertThat(pet.getOwner()).isSameAs(owner);
    }

    @Test
    void shouldReturnSortedUnmodifiablePets() {
        // Given
        PetType petType = new PetType();
        petType.setId(1);
        petType.setName("Dog");

        Pet pet1 = new Pet();
        pet1.setName("Zoe");
        pet1.setType(petType);
        pet1.setBirthDate(new Date());

        Pet pet2 = new Pet();
        pet2.setName("Alice");
        pet2.setType(petType);
        pet2.setBirthDate(new Date());

        owner.addPet(pet1);
        owner.addPet(pet2);

        // When
        List<Pet> pets = owner.getPets();

        // Then
        assertThat(pets).hasSize(2);
        assertThat(pets.get(0).getName()).isEqualTo("Alice"); // Sorted by name
        assertThat(pets.get(1).getName()).isEqualTo("Zoe");
        assertThat(pets).isInstanceOf(List.class);

        // Verify unmodifiable
        assertThatThrownBy(() -> pets.add(new Pet()))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldReturnEmptyPetsWhenNoneAdded() {
        // When
        List<Pet> pets = owner.getPets();

        // Then
        assertThat(pets).isEmpty();
    }
}
