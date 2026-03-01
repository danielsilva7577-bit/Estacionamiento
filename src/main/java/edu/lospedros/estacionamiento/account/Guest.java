package edu.lospedros.estacionamiento.account;

import edu.lospedros.estacionamiento.auth.PasswordHasher;
import edu.lospedros.estacionamiento.payment.PaymentProcessor;

import java.time.LocalDateTime;

public class Guest extends Account {
    private String temporalPaymentMethod = "";
    private LocalDateTime temporalDataCapturedAt;

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
