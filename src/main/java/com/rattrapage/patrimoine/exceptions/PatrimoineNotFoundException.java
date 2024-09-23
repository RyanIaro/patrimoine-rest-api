package com.rattrapage.patrimoine.exceptions;

public class PatrimoineNotFoundException extends RuntimeException{
    public PatrimoineNotFoundException(String id) {
        super("Patrimoine non trouvé avec l'id : " + id);
    }
}
