package org.springframework.samples.petclinic.customers.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetRepository;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PetResource.class)
@ActiveProfiles("test")
class PetResourceTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PetRepository petRepository;

    @MockitoBean
    private OwnerRepository ownerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Pet setupPet(int id, String name, PetType type, Owner owner) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setType(type);
        pet.setBirthDate(new Date());
        if (owner != null) {
            owner.addPet(pet); // Sets pet.owner
        }
        return pet;
    }

    private Owner setupOwner(int id, String firstName, String lastName) {
        Owner owner = new Owner();
        owner.setFirstName(firstName);
        owner.setLastName(lastName);
        owner.setAddress("123 Main St");
        owner.setCity("Springfield");
        owner.setTelephone("5551234567");
        return owner;
    }

    private PetType setupPetType(int id, String name) {
        PetType petType = new PetType();
        petType.setId(id);
        petType.setName(name);
        return petType;
    }

    @Test
    void shouldGetPetTypes() throws Exception {
        PetType dog = setupPetType(1, "Dog");
        PetType cat = setupPetType(2, "Cat");
        List<PetType> petTypes = List.of(dog, cat);

        given(petRepository.findPetTypes()).willReturn(petTypes);

        mvc.perform(get("/petTypes")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Dog"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Cat"));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingPetForNonExistentOwner() throws Exception {
        PetRequest petRequest = new PetRequest(0, new Date(), "Basil", 6);

        given(ownerRepository.findById(999)).willReturn(Optional.empty());

        mvc.perform(post("/owners/999/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestForInvalidOwnerIdWhenCreatingPet() throws Exception {
        PetRequest petRequest = new PetRequest(0, new Date(), "Basil", 6);

        mvc.perform(post("/owners/0/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdatePet() throws Exception {
        PetType petType = setupPetType(6, "Hamster");
        Owner owner = setupOwner(1, "George", "Bush");
        Pet existingPet = setupPet(2, "Basil", petType, owner);
        PetRequest petRequest = new PetRequest(2, new Date(), "Max", 6);
        Pet updatedPet = setupPet(2, "Max", petType, owner);

        given(petRepository.findById(2)).willReturn(Optional.of(existingPet));
        given(petRepository.findPetTypeById(6)).willReturn(Optional.of(petType));
        given(petRepository.save(any(Pet.class))).willReturn(updatedPet);

        mvc.perform(put("/owners/1/pets/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petRequest)))
            .andExpect(status().isNoContent());

        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentPet() throws Exception {
        PetRequest petRequest = new PetRequest(999, new Date(), "Max", 6);

        given(petRepository.findById(999)).willReturn(Optional.empty());

        mvc.perform(put("/owners/1/pets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAPetInJsonFormat() throws Exception {
        Owner owner = setupOwner(1, "George", "Bush");
        PetType petType = setupPetType(6, "Hamster");
        Pet pet = setupPet(2, "Basil", petType, owner);

        given(petRepository.findById(2)).willReturn(Optional.of(pet));

        mvc.perform(get("/owners/1/pets/2")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("Basil"))
            .andExpect(jsonPath("$.owner").value("George Bush"))
            .andExpect(jsonPath("$.type.id").value(6));
    }

    @Test
    void shouldReturnNotFoundForNonExistentPet() throws Exception {
        given(petRepository.findById(999)).willReturn(Optional.empty());

        mvc.perform(get("/owners/1/pets/999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}
