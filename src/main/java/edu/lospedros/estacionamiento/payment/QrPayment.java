package edu.lospedros.estacionamiento.payment;

public class QrPayment extends PaymentProcessor {
    private final String tokenQR;

    public QrPayment(String tokenQR) {
        this.tokenQR = (tokenQR == null) ? "" : tokenQR.trim();
    }

    public void leerQR() {
        // Simulacion de lectura QR.
    }

    @Override
    public boolean ejecutarCobro(double monto) {
        leerQR();
        return monto >= 0 && !tokenQR.isBlank();
    }

    @Override
    public String nombreMetodo() {
        return "QR";
    }
}
