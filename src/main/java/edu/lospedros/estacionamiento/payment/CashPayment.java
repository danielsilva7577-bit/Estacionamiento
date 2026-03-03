package edu.lospedros.estacionamiento.payment;

/**
 * Procesador para pagos en efectivo.
 * <p>
 * Asume que el pago siempre es exitoso si el monto es válido.
 * </p>
 */
public class CashPayment extends PaymentProcessor {
    @Override
    public boolean ejecutarCobro(double monto) {
        return monto >= 0;
    }

    @Override
    public String nombreMetodo() {
        return "EFECTIVO";
    }
}
