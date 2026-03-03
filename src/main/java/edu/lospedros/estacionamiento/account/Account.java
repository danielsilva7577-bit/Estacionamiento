package edu.lospedros.estacionamiento.account;

import edu.lospedros.estacionamiento.auth.PasswordHasher;

import java.util.Objects;

/**
 * Clase abstracta que representa una cuenta de usuario en el sistema.
 * <p>
 * Define los atributos básicos como ID, email y hash de contraseña,
 * así como los métodos para la autenticación.
 * </p>
 */
public abstract class Account {
    private final String id;
    private final String email;
    private final String passwordHash;

    /**
     * Constructor base para una cuenta.
     *
     * @param id           Identificador único de la cuenta.
     * @param email        Correo electrónico asociado.
     * @param passwordHash Hash de la contraseña del usuario.
     */
    protected Account(String id, String email, String passwordHash) {
        this.id = Objects.requireNonNull(id, "id");
        this.email = Objects.requireNonNull(email, "email");
        this.passwordHash = (passwordHash == null) ? "" : passwordHash;
    }

    /**
     * Obtiene el ID de la cuenta.
     *
     * @return El identificador único.
     */
    public String getId() {
        return id;
    }

    /**
     * Obtiene el correo electrónico de la cuenta.
     *
     * @return El email del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Obtiene el hash de la contraseña almacenada.
     *
     * @return El hash de la contraseña.
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Verifica si la contraseña proporcionada coincide con la almacenada.
     *
     * @param rawPassword La contraseña en texto plano a verificar.
     * @param hasher      El objeto {@link PasswordHasher} para realizar la comparación.
     * @return {@code true} si la contraseña es correcta, {@code false} en caso contrario.
     */
    public boolean autenticar(String rawPassword, PasswordHasher hasher) {
        return hasher.matches(rawPassword, passwordHash);
    }

    /**
     * Verifica si la cuenta tiene una contraseña configurada.
     *
     * @return {@code true} si existe un hash de contraseña válido.
     */
    public boolean autenticar() {
        return passwordHash != null && !passwordHash.isBlank();
    }

    /**
     * Obtiene el tipo de cuenta (e.g., "ADMIN", "CLIENT", "GUEST").
     *
     * @return Una cadena representando el rol de la cuenta.
     */
    public abstract String getTipo();
}
