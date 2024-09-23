package com.rattrapage.patrimoine.controller;

import com.rattrapage.patrimoine.exceptions.PatrimoineNotFoundException;
import com.rattrapage.patrimoine.exceptions.PatrimoineStorageException;
import com.rattrapage.patrimoine.model.Patrimoine;
import com.rattrapage.patrimoine.service.PatrimoineService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patrimoines")
@AllArgsConstructor
public class PatrimoineController {
    @Autowired
    private PatrimoineService patrimoineService;

    @PutMapping("/{id}")
    public ResponseEntity<Patrimoine> createOrUpdatePatrimoine(@PathVariable String id, @RequestBody Patrimoine toCreateOrUpdate) {
        toCreateOrUpdate.setId(id);
        try {
            Patrimoine response = patrimoineService.createOrUpdate(toCreateOrUpdate);
            return ResponseEntity.ok(response);
        } catch (PatrimoineStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patrimoine> getPatrimoine(@PathVariable String id) {
        try {
            Patrimoine patrimoine = patrimoineService.get(id);
            return ResponseEntity.ok(patrimoine);
        } catch (PatrimoineNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (PatrimoineStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
