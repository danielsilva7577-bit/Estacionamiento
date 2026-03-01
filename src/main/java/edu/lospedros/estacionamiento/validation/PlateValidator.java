package edu.lospedros.estacionamiento.validation;

import java.util.Locale;

public final class PlateValidator {
    private PlateValidator() {
    }

    public static String normalize(String rawPlate) {
        if (rawPlate == null) return "";
        return rawPlate.replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
    }

    public static boolean hasValidLength(String plate) {
        return plate != null && plate.length() == 7;
    }

    public static boolean hasValidFormat(String plate) {
        return plate != null && plate.matches("[A-Z0-9]{7}");
    }

    public static boolean isValid(String plate) {
        return hasValidLength(plate) && hasValidFormat(plate);
    }
}
