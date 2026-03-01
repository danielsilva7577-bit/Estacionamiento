package edu.lospedros.estacionamiento.account;

import edu.lospedros.estacionamiento.auth.PasswordHasher;

import java.util.Objects;

public abstract class Account {
    private final String id;
    private final String email;
    private final String passwordHash;

    protected Account(String id, String email, String passwordHash) {
        this.id = Objects.requireNonNull(id, "id");
        this.email = Objects.requireNonNull(email, "email");
        this.passwordHash = (passwordHash == null) ? "" : passwordHash;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean autenticar(String rawPassword, PasswordHasher hasher) {
        return hasher.matches(rawPassword, passwordHash);
    }

    // Firma compatible con el diagrama UML.
    public boolean autenticar() {
        return passwordHash != null && !passwordHash.isBlank();
    }

    public abstract String getTipo();
}
