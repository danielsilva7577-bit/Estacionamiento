package edu.lospedros.estacionamiento.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para el hash y verificación de contraseñas.
 * <p>
 * Utiliza el algoritmo SHA-256 para almacenar contraseñas de forma segura.
 * </p>
 */
public class PasswordHasher {
    /**
     * Genera un hash SHA-256 de la contraseña proporcionada.
     *
     * @param rawPassword La contraseña en texto plano.
     * @return El hash hexadecimal de la contraseña, o una cadena vacía si la entrada es nula.
     */
    public String hash(String rawPassword) {
        if (rawPassword == null) return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 no disponible", ex);
        }
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash almacenado.
     *
     * @param rawPassword  La contraseña en texto plano a verificar.
     * @param expectedHash El hash esperado.
     * @return {@code true} si coinciden, {@code false} en caso contrario.
     */
    public boolean matches(String rawPassword, String expectedHash) {
        if (expectedHash == null || expectedHash.isBlank()) return false;
        return hash(rawPassword).equals(expectedHash);
    }
}
