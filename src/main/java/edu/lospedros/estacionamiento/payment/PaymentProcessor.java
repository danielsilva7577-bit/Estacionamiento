package edu.lospedros.estacionamiento.payment;

/**
 * Interfaz abstracta para procesar pagos.
 * <p>
 * Define el contrato que deben seguir todos los métodos de pago soportados.
 * </p>
 */
public abstract class PaymentProcessor {
    /**
     * Ejecuta el cobro del monto especificado.
     *
     * @param monto Cantidad a cobrar.
     * @return {@code true} si el pago fue exitoso.
     */
    public abstract boolean ejecutarCobro(double monto);

    /**
     * Obtiene el nombre legible del método de pago.
     *
     * @return Nombre del método (e.g., "EFECTIVO", "QR").
     */
    public abstract String nombreMetodo();
}
