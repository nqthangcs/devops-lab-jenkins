package org.springframework.samples.petclinic.customers.web.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.samples.petclinic.customers.web.OwnerRequest;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerEntityMapperTest {

    private OwnerEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OwnerEntityMapper();
    }

    @Test
    void shouldMapAllFieldsFromRequestToOwner() {
        // Given
        Owner owner = new Owner();
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "Springfield", "5551234567");

        // When
        Owner result = mapper.map(owner, request);

        // Then
        assertThat(result).isSameAs(owner); // Same instance returned
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getAddress()).isEqualTo("123 Main St");
        assertThat(result.getCity()).isEqualTo("Springfield");
        assertThat(result.getTelephone()).isEqualTo("5551234567");
    }

    @Test
    void shouldHandleNullFieldsInRequest() {
        // Given
        Owner owner = new Owner();
        owner.setAddress("Old Address"); // Pre-existing value
        // Note: OwnerRequest constructor won't allow nulls due to @NotBlank, but mapper can handle nulls if passed
        OwnerRequest request = new OwnerRequest(null, null, null, null, null); // Assuming bypassing validation for test

        // When
        Owner result = mapper.map(owner, request);

        // Then
        assertThat(result).isSameAs(owner);
        assertThat(result.getFirstName()).isNull();
        assertThat(result.getLastName()).isNull();
        assertThat(result.getAddress()).isNull();
        assertThat(result.getCity()).isNull();
        assertThat(result.getTelephone()).isNull();
    }

    @Test
    void shouldOverwriteExistingOwnerFields() {
        // Given
        Owner owner = new Owner();
        owner.setFirstName("OldFirst");
        owner.setLastName("OldLast");
        owner.setAddress("Old Address");
        owner.setCity("Old City");
        owner.setTelephone("1111111111");
        OwnerRequest request = new OwnerRequest("Jane", "Smith", "456 New St", "Newtown", "2222222222");

        // When
        Owner result = mapper.map(owner, request);

        // Then
        assertThat(result).isSameAs(owner);
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getAddress()).isEqualTo("456 New St");
        assertThat(result.getCity()).isEqualTo("Newtown");
        assertThat(result.getTelephone()).isEqualTo("2222222222");
    }

    @Test
    void shouldHandlePartialRequestFieldsWithValidValues() {
        // Given
        Owner owner = new Owner();
        owner.setFirstName("OldFirst");
        owner.setLastName("OldLast");
        owner.setAddress("Old Address");
        owner.setCity("Old City");
        owner.setTelephone("1111111111");
        // Using placeholder values for null fields to satisfy @NotBlank
        OwnerRequest request = new OwnerRequest("Jane", "Smith", "N/A", "Newtown", "N/A");

        // When
        Owner result = mapper.map(owner, request);

        // Then
        assertThat(result).isSameAs(owner);
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getAddress()).isEqualTo("N/A");
        assertThat(result.getCity()).isEqualTo("Newtown");
        assertThat(result.getTelephone()).isEqualTo("N/A");
    }

    @Test
    void shouldPreserveUnmappedFields() {
        // Given
        Owner owner = new Owner();
        owner.setAddress("Old Address");

        PetType petType = new PetType();
        petType.setName("Dog");

        Pet pet = new Pet();
        pet.setName("Fluffy");
        pet.setBirthDate(new Date());
        pet.setType(petType);
        owner.addPet(pet); // Adds pet and sets owner reference

        OwnerRequest request = new OwnerRequest("Jane", "Smith", "456 New St", "Newtown", "2222222222");

        // When
        Owner result = mapper.map(owner, request);

        // Then
        assertThat(result).isSameAs(owner);
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getAddress()).isEqualTo("456 New St");
        assertThat(result.getCity()).isEqualTo("Newtown");
        assertThat(result.getTelephone()).isEqualTo("2222222222");
        assertThat(result.getPets()).hasSize(1);
        assertThat(result.getPets().get(0).getName()).isEqualTo("Fluffy");
        assertThat(result.getPets().get(0).getType().getName()).isEqualTo("Dog");
        assertThat(result.getPets().get(0).getOwner()).isSameAs(result); // Owner reference preserved
    }
}
