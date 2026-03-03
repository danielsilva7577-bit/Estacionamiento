package edu.lospedros.estacionamiento.process;

/**
 * Representa un espacio físico de estacionamiento.
 * <p>
 * Cada espacio tiene un número único, un tipo específico y un estado de ocupación.
 * </p>
 */
public class Space {
    /**
     * Tipos de espacios disponibles.
     */
    public enum TipoEspacio { MOTO, PROMEDIO, GRANDE }

    private final int numero;
    private double mensualidad;
    private boolean ocupado;
    private final TipoEspacio tipo;

    /**
     * Crea un espacio con configuración por defecto (libre y sin mensualidad).
     *
     * @param numero Número identificador del espacio.
     * @param tipo   Tipo de vehículo que admite.
     */
    public Space(int numero, TipoEspacio tipo) {
        this(numero, tipo, 0.0, false);
    }

    /**
     * Crea un espacio con configuración completa.
     *
     * @param numero      Número identificador.
     * @param tipo        Tipo de espacio.
     * @param mensualidad Costo mensual (si aplica).
     * @param ocupado     Estado inicial de ocupación.
     */
    public Space(int numero, TipoEspacio tipo, double mensualidad, boolean ocupado) {
        this.numero = numero;
        this.tipo = tipo;
        this.mensualidad = mensualidad;
        this.ocupado = ocupado;
    }

    public int getNumero() {
        return numero;
    }

    public double getMensualidad() {
        return mensualidad;
    }

    public void setMensualidad(double mensualidad) {
        this.mensualidad = mensualidad;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public TipoEspacio getTipo() {
        return tipo;
    }
}
