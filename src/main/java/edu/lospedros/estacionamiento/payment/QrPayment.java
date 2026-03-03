package edu.lospedros.estacionamiento.payment;

/**
 * Procesador para pagos mediante código QR.
 * <p>
 * Valida la existencia de un token QR válido.
 * </p>
 */
public class QrPayment extends PaymentProcessor {
    private final String tokenQR;

    /**
     * Crea un procesador de pago QR.
     *
     * @param tokenQR El token generado o escaneado.
     */
    public QrPayment(String tokenQR) {
        this.tokenQR = (tokenQR == null) ? "" : tokenQR.trim();
    }

    /**
     * Simula el escaneo del código QR.
     */
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
