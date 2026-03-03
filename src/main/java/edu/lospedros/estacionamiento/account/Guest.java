package edu.lospedros.estacionamiento.account;

import edu.lospedros.estacionamiento.auth.PasswordHasher;
import edu.lospedros.estacionamiento.payment.PaymentProcessor;

import java.time.LocalDateTime;

/**
 * Representa una cuenta de usuario invitado.
 * <p>
 * Los invitados no tienen credenciales persistentes y sus datos son temporales
 * durante la sesión de estacionamiento.
 * </p>
 */
public class Guest extends Account {
    private String temporalPaymentMethod = "";
    private LocalDateTime temporalDataCapturedAt;

    /**
     * Crea una nueva cuenta de invitado.
     *
     * @param id    Identificador único temporal.
     * @param email Correo electrónico (opcional o genérico).
     */
    public Guest(String id, String email) {
        super(id, email, "");
    }

    @Override
    public boolean autenticar(String rawPassword, PasswordHasher hasher) {
        return false;
    }

    @Override
    public String getTipo() {
        return "GUEST";
    }

    /**
     * Almacena datos temporales de pago para la sesión actual.
     *
     * @param p El procesador de pago utilizado.
     */
    public void proporcionarDatosTemporales(PaymentProcessor p) {
        if (p == null) {
            temporalPaymentMethod = "";
            temporalDataCapturedAt = LocalDateTime.now();
            return;
        }
        temporalPaymentMethod = p.nombreMetodo();
        temporalDataCapturedAt = LocalDateTime.now();
    }

    public String getTemporalPaymentMethod() {
        return temporalPaymentMethod;
    }

    public LocalDateTime getTemporalDataCapturedAt() {
        return temporalDataCapturedAt;
    }
}
