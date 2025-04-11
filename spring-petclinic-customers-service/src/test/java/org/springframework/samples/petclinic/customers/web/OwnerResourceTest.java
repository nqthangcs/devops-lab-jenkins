package org.springframework.samples.petclinic.customers.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.web.mapper.OwnerEntityMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OwnerResource.class)
@ActiveProfiles("test")
class OwnerResourceTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private OwnerRepository ownerRepository;

    @MockitoBean
    private OwnerEntityMapper ownerEntityMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Owner setupOwner(int id, String firstName, String lastName) {
        Owner owner = new Owner();
        owner.setFirstName(firstName);
        owner.setLastName(lastName);
        owner.setAddress("123 Main St");
        owner.setCity("Springfield");
        owner.setTelephone("5551234567");
        return owner;
    }

    private OwnerRequest setupOwnerRequest(String firstName, String lastName) {
        return new OwnerRequest(firstName, lastName, "123 Main St", "Springfield", "5551234567");
    }

    @Test
    void shouldCreateOwner() throws Exception {
        // Given
        OwnerRequest ownerRequest = setupOwnerRequest("George", "Bush");
        Owner owner = setupOwner(1, "George", "Bush");
        given(ownerEntityMapper.map(any(Owner.class), any(OwnerRequest.class))).willReturn(owner);
        given(ownerRepository.save(any(Owner.class))).willReturn(owner);

        // When & Then
        mvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ownerRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value("George"))
            .andExpect(jsonPath("$.lastName").value("Bush"));

        verify(ownerRepository).save(any(Owner.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidOwnerRequest() throws Exception {
        // Given
        OwnerRequest invalidRequest = new OwnerRequest("", "Bush", "123 Main St", "Springfield", "5551234567"); // Empty firstName

        // When & Then
        mvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindOwnerById() throws Exception {
        // Given
        Owner owner = setupOwner(1, "George", "Bush");
        given(ownerRepository.findById(1)).willReturn(Optional.of(owner));

        // When & Then
        mvc.perform(get("/owners/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("George"))
            .andExpect(jsonPath("$.lastName").value("Bush"));
    }

    @Test
    void shouldReturnBadRequestForInvalidOwnerId() throws Exception {
        // When & Then
        mvc.perform(get("/owners/0")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest()); // @Min(1) violation
    }

    @Test
    void shouldFindAllOwners() throws Exception {
        // Given
        Owner owner1 = setupOwner(1, "George", "Bush");
        Owner owner2 = setupOwner(2, "Jane", "Doe");
        given(ownerRepository.findAll()).willReturn(List.of(owner1, owner2));

        // When & Then
        mvc.perform(get("/owners")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("George"))
            .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    void shouldUpdateOwner() throws Exception {
        // Given
        Owner existingOwner = setupOwner(1, "George", "Bush");
        Owner updatedOwner = setupOwner(1, "George", "Washington");
        OwnerRequest ownerRequest = setupOwnerRequest("George", "Washington");
        given(ownerRepository.findById(1)).willReturn(Optional.of(existingOwner));
        given(ownerEntityMapper.map(existingOwner, ownerRequest)).willReturn(updatedOwner);
        given(ownerRepository.save(any(Owner.class))).willReturn(updatedOwner);

        // When & Then
        mvc.perform(put("/owners/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ownerRequest)))
            .andExpect(status().isNoContent());

        verify(ownerRepository).save(existingOwner);
    }

    @Test
    void shouldReturnBadRequestForInvalidOwnerIdOnUpdate() throws Exception {
        // Given
        OwnerRequest ownerRequest = setupOwnerRequest("George", "Washington");

        // When & Then
        mvc.perform(put("/owners/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ownerRequest)))
            .andExpect(status().isBadRequest()); // @Min(1) violation
    }
}
