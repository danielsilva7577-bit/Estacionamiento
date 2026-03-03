package edu.lospedros.estacionamiento.validation;

import java.util.Locale;

/**
 * Utilidad para la validación y normalización de placas vehiculares.
 * <p>
 * Asegura que las placas cumplan con el formato estándar del sistema (7 caracteres alfanuméricos).
 * </p>
 */
public final class PlateValidator {
    private PlateValidator() {
    }

    /**
     * Normaliza una cadena de placa eliminando espacios y convirtiendo a mayúsculas.
     *
     * @param rawPlate La cadena de entrada.
     * @return La placa normalizada, o una cadena vacía si la entrada es nula.
     */
    public static String normalize(String rawPlate) {
        if (rawPlate == null) return "";
        return rawPlate.replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
    }

    /**
     * Verifica si la longitud de la placa es correcta.
     *
     * @param plate La placa a verificar.
     * @return {@code true} si tiene exactamente 7 caracteres.
     */
    public static boolean hasValidLength(String plate) {
        return plate != null && plate.length() == 7;
    }

    /**
     * Verifica si el formato de la placa es válido (solo letras y números).
     *
     * @param plate La placa a verificar.
     * @return {@code true} si cumple con el patrón alfanumérico.
     */
    public static boolean hasValidFormat(String plate) {
        return plate != null && plate.matches("[A-Z0-9]{7}");
    }

    /**
     * Realiza una validación completa de la placa (longitud y formato).
     *
     * @param plate La placa a validar.
     * @return {@code true} si la placa es válida para el sistema.
     */
    public static boolean isValid(String plate) {
        return hasValidLength(plate) && hasValidFormat(plate);
    }
}
