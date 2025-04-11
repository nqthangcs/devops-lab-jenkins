package org.springframework.samples.petclinic.customers.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class PetTest {

    private Pet pet;
    private Owner owner;
    private PetType petType;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setAddress("123 Main St");
        owner.setCity("Springfield");
        owner.setTelephone("5551234567");

        petType = new PetType();
        petType.setId(1);
        petType.setName("Dog");
    }

    @Test
    void shouldSetAndGetFields() {
        // Given
        pet.setId(2);
        pet.setName("Fluffy");
        pet.setBirthDate(new Date());
        pet.setType(petType);
        pet.setOwner(owner);

        // Then
        assertThat(pet.getId()).isEqualTo(2);
        assertThat(pet.getName()).isEqualTo("Fluffy");
        assertThat(pet.getBirthDate()).isNotNull();
        assertThat(pet.getType()).isSameAs(petType);
        assertThat(pet.getOwner()).isSameAs(owner);
    }

    @Test
    void shouldSetOwnerAndReflectInOwnerPets() {
        // When
        pet.setOwner(owner);

        // Then
        assertThat(pet.getOwner()).isSameAs(owner);
        assertThat(owner.getPetsInternal()).doesNotContain(pet); // setOwner doesn’t add to owner’s pets

        // Contrast with addPet
        Owner newOwner = new Owner();
        newOwner.setFirstName("Jane");
        newOwner.setLastName("Smith");
        newOwner.setAddress("456 Oak St");
        newOwner.setCity("Newtown");
        newOwner.setTelephone("5559876543");

        newOwner.addPet(pet);
        assertThat(pet.getOwner()).isSameAs(newOwner); // Updated to newOwner
        assertThat(newOwner.getPetsInternal()).contains(pet); // addPet adds to pets
    }

    @Test
    void shouldNotBeEqualWithDifferentFields() {
        // Given
        pet.setId(2);
        pet.setName("Fluffy");
        pet.setBirthDate(new Date());
        pet.setType(petType);
        pet.setOwner(owner);

        Pet pet2 = new Pet();
        pet2.setId(3); // Different ID
        pet2.setName("Fluffy");
        pet2.setBirthDate(pet.getBirthDate());
        pet2.setType(petType);
        pet2.setOwner(owner);

        // Then
        assertThat(pet).isNotEqualTo(pet2);
        assertThat(pet.hashCode()).isNotEqualTo(pet2.hashCode());
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentClass() {
        // Given
        pet.setId(2);
        pet.setName("Fluffy");

        // Then
        assertThat(pet).isNotEqualTo(null);
        assertThat(pet).isNotEqualTo(new Object());
    }
}
