package org.springframework.samples.petclinic.visits.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class VisitTest {

    @Test
    void shouldSetAndGetId() {
        Visit visit = new Visit();
        visit.setId(1);
        assertEquals(1, visit.getId());
    }

    @Test
    void shouldSetAndGetDate() {
        Visit visit = new Visit();
        Date date = new Date();
        visit.setDate(date);
        assertEquals(date, visit.getDate());
    }

    @Test
    void shouldHaveDefaultDate() {
        Visit visit = new Visit();
        assertNotNull(visit.getDate(), "Date should be initialized by default");
    }

    @Test
    void shouldSetAndGetDescription() {
        Visit visit = new Visit();
        visit.setDescription("Checkup");
        assertEquals("Checkup", visit.getDescription());
    }

    @Test
    void shouldSetAndGetPetId() {
        Visit visit = new Visit();
        visit.setPetId(123);
        assertEquals(123, visit.getPetId());
    }

    @Test
    void shouldBuildVisitWithAllFields() {
        Date date = new Date();
        Visit visit = Visit.VisitBuilder.aVisit()
            .id(1)
            .date(date)
            .description("Checkup")
            .petId(123)
            .build();

        assertEquals(1, visit.getId());
        assertEquals(date, visit.getDate());
        assertEquals("Checkup", visit.getDescription());
        assertEquals(123, visit.getPetId());
    }

    @Test
    void shouldBuildVisitWithDefaultDate() {
        Visit visit = Visit.VisitBuilder.aVisit()
            .id(1)
            .description("Checkup")
            .petId(123)
            .build();

        assertEquals(1, visit.getId());
        assertNull(visit.getDate(), "Date should be initialized by default");
        assertEquals("Checkup", visit.getDescription());
        assertEquals(123, visit.getPetId());
    }

    @Test
    void shouldHandleNullDescription() {
        Visit visit = Visit.VisitBuilder.aVisit()
            .id(1)
            .petId(123)
            .build();

        assertEquals(1, visit.getId());
        assertNull(visit.getDescription());
        assertEquals(123, visit.getPetId());
    }

    @Test
    void shouldBuildVisitWithMinimalFields() {
        Visit visit = Visit.VisitBuilder.aVisit()
            .petId(123)
            .build();

        assertNull(visit.getId());
        assertNull(visit.getDate());
        assertNull(visit.getDescription());
        assertEquals(123, visit.getPetId());
    }
}
