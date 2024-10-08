package com.rattrapage.patrimoine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rattrapage.patrimoine.exceptions.PatrimoineNotFoundException;
import com.rattrapage.patrimoine.exceptions.PatrimoineStorageException;
import com.rattrapage.patrimoine.model.Patrimoine;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

@Service
@Getter
@Setter
public class PatrimoineService {
    private static final String STORAGE_DIRECTORY = "patrimoines";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private void createStorageDirectoryIfNotExists() {
        File directory = new File(STORAGE_DIRECTORY);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new RuntimeException("Impossible de créer le répertoire de stockage");
            }
        }
    }

    public PatrimoineService() {
        objectMapper.findAndRegisterModules();
        createStorageDirectoryIfNotExists();
    }

    public Patrimoine createOrUpdate(Patrimoine patrimoine) {
        patrimoine.setDerniereModification(LocalDateTime.now());
        try {
            String json = objectMapper.writeValueAsString(patrimoine);
            File file = new File(STORAGE_DIRECTORY, patrimoine.getId() + ".json");
            try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
                out.println(json);
            }
            return patrimoine;
        } catch (IOException e) {
            throw new PatrimoineStorageException("Erreur lors de la sauvegarde du patrimoine avec l'id : " + patrimoine.getId(), e);
        }
    }

    public Patrimoine get(String id) {
        File file = new File(STORAGE_DIRECTORY, id + ".json");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                return objectMapper.readValue(content.toString(), Patrimoine.class);
            } catch (IOException e) {
                throw new PatrimoineStorageException("Erreur lors de la lecture du patrimoine avec l'id : " + id, e);
            }
        }
        throw new PatrimoineNotFoundException(id);
    }

}
