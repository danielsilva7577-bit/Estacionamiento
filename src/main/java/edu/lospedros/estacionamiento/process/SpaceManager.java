package edu.lospedros.estacionamiento.process;

import edu.lospedros.estacionamiento.data.Motorcycle;
import edu.lospedros.estacionamiento.data.Vehicle;
import edu.lospedros.estacionamiento.data.LargeVehicle;
import edu.lospedros.estacionamiento.data.StandardVehicle;

import java.util.List;

/**
 * Gestor de la disponibilidad y contabilidad de espacios.
 * <p>
 * Mantiene contadores en tiempo real de la ocupación por tipo de vehículo
 * y verifica si hay lugares disponibles.
 * </p>
 */
public class SpaceManager {
    private final List<Space> espacios;
    public int lugaresMotos = 20;
    public int lugaresGrandes = 30;
    public int lugaresPromedio = 50;
    public int ocupadosMotos;
    public int ocupadosGrandes;
    public int ocupadosPromedio;

    /**
     * Inicializa el gestor con una lista de espacios.
     *
     * @param espacios Lista total de espacios del estacionamiento.
     */
    public SpaceManager(List<Space> espacios) {
        this.espacios = espacios;
        recomputarContadores();
    }

    /**
     * Verifica si hay disponibilidad para un tipo de vehículo específico.
     *
     * @param v El vehículo que busca estacionamiento.
     * @return {@code true} si hay al menos un espacio libre compatible.
     */
    public boolean verificarDisponibilidad(Vehicle v) {
        Space.TipoEspacio requerido = tipoParaVehiculo(v);
        return espacios.stream().anyMatch(e -> e.getTipo() == requerido && !e.isOcupado());
    }

    /**
     * Actualiza los contadores tras el ingreso de un vehículo.
     *
     * @param v El vehículo que ingresó.
     */
    public void contabilizarIngreso(Vehicle v) {
        if (v instanceof Motorcycle) {
            ocupadosMotos++;
        } else if (v instanceof LargeVehicle) {
            ocupadosGrandes++;
        } else {
            ocupadosPromedio++;
        }
    }

    /**
     * Actualiza los contadores tras la salida de un vehículo.
     *
     * @param v El vehículo que salió.
     */
    public void contabilizarSalida(Vehicle v) {
        if (v instanceof Motorcycle) {
            ocupadosMotos = Math.max(0, ocupadosMotos - 1);
        } else if (v instanceof LargeVehicle) {
            ocupadosGrandes = Math.max(0, ocupadosGrandes - 1);
        } else {
            ocupadosPromedio = Math.max(0, ocupadosPromedio - 1);
        }
    }

    /**
     * Busca un espacio por su número identificador.
     *
     * @param numero El número del espacio.
     * @return El objeto {@link Space} correspondiente, o {@code null} si no existe.
     */
    public Space obtenerEspacio(int numero) {
        return espacios.stream().filter(e -> e.getNumero() == numero).findFirst().orElse(null);
    }

    /**
     * Recalcula los contadores de ocupación basándose en el estado actual de la lista de espacios.
     * Útil para sincronizar el estado tras cargar datos persistidos.
     */
    public void recomputarContadores() {
        ocupadosMotos = 0;
        ocupadosGrandes = 0;
        ocupadosPromedio = 0;
        for (Space e : espacios) {
            if (!e.isOcupado()) continue;
            switch (e.getTipo()) {
                case MOTO -> ocupadosMotos++;
                case GRANDE -> ocupadosGrandes++;
                case PROMEDIO -> ocupadosPromedio++;
            }
        }
    }

    /**
     * Determina el tipo de espacio requerido para un vehículo dado.
     *
     * @param v El vehículo.
     * @return El {@link Space.TipoEspacio} correspondiente.
     */
    public static Space.TipoEspacio tipoParaVehiculo(Vehicle v) {
        if (v instanceof Motorcycle) return Space.TipoEspacio.MOTO;
        if (v instanceof LargeVehicle) return Space.TipoEspacio.GRANDE;
        return Space.TipoEspacio.PROMEDIO;
    }
}
