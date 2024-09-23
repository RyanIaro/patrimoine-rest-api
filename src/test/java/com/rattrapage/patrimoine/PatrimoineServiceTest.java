package com.rattrapage.patrimoine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rattrapage.patrimoine.exceptions.PatrimoineNotFoundException;
import com.rattrapage.patrimoine.exceptions.PatrimoineStorageException;
import com.rattrapage.patrimoine.model.Patrimoine;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rattrapage.patrimoine.service.PatrimoineService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

class PatrimoineServiceTest {

    private PatrimoineService patrimoineService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = mock(ObjectMapper.class);
        objectMapper.findAndRegisterModules();
        patrimoineService = new PatrimoineService();
    }

    @Test
    void shouldCreateOrUpdatePatrimoine() {
        Patrimoine patrimoine = new Patrimoine();
        patrimoine.setId(UUID.randomUUID().toString());
        patrimoine.setPossesseur("Test User");
        patrimoine.setDerniereModification(LocalDateTime.now());

        Patrimoine createdPatrimoine = patrimoineService.createOrUpdate(patrimoine);
        Assertions.assertNotNull(createdPatrimoine);
        Assertions.assertEquals(patrimoine.getId(), createdPatrimoine.getId());
        Assertions.assertEquals(patrimoine.getPossesseur(), createdPatrimoine.getPossesseur());
        Assertions.assertTrue(createdPatrimoine.getDerniereModification().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    void shouldThrowPatrimoineStorageExceptionOnFailedCreate() throws IOException {
        Patrimoine patrimoine = new Patrimoine();
        patrimoine.setId(UUID.randomUUID().toString());

        doThrow(new IOException("Erreur d'écriture")).when(objectMapper).writeValueAsString(patrimoine);

        Assertions.assertThrows(PatrimoineStorageException.class, () -> patrimoineService.createOrUpdate(patrimoine));
    }

    @Test
    void shouldGetPatrimoine() throws JsonProcessingException {
        String id = UUID.randomUUID().toString();
        Patrimoine expectedPatrimoine = new Patrimoine(id, "Test User", LocalDateTime.now());

        ObjectMapper mockedObjectMapper = mock(ObjectMapper.class);
        when(mockedObjectMapper.readValue(anyString(), eq(Patrimoine.class))).thenReturn(expectedPatrimoine);

        PatrimoineService patrimoineService = new PatrimoineService();
        Patrimoine retrievedPatrimoine = patrimoineService.get(id);

        Assertions.assertEquals(expectedPatrimoine.getId(), retrievedPatrimoine.getId());
        Assertions.assertEquals(expectedPatrimoine.getPossesseur(), retrievedPatrimoine.getPossesseur());
        Assertions.assertEquals(expectedPatrimoine.getDerniereModification(), retrievedPatrimoine.getDerniereModification());
    }

    @Test
    void shouldThrowPatrimoineNotFoundExceptionWhenGetFails() {
        String id = UUID.randomUUID().toString();
        File file = new File("patrimoines", id + ".json");
        Assertions.assertFalse(file.exists());

        Assertions.assertThrows(PatrimoineNotFoundException.class, () -> patrimoineService.get(id));
    }

    @Test
    void shouldThrowPatrimoineStorageExceptionOnFailedGet() throws IOException {
        String id = UUID.randomUUID().toString();
        File file = new File("patrimoines", id + ".json");
        Assertions.assertTrue(file.exists());

        doThrow(new IOException("Erreur de lecture")).when(objectMapper).readValue(anyString(), eq(Patrimoine.class));

        Assertions.assertThrows(PatrimoineStorageException.class, () -> patrimoineService.get(id));
    }
}