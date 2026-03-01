package edu.lospedros.estacionamiento.process;

import edu.lospedros.estacionamiento.data.Motorcycle;
import edu.lospedros.estacionamiento.data.Vehicle;
import edu.lospedros.estacionamiento.data.LargeVehicle;
import edu.lospedros.estacionamiento.data.StandardVehicle;

import java.util.List;

public class SpaceManager {
    private final List<Space> espacios;
    public int lugaresMotos = 20;
    public int lugaresGrandes = 30;
    public int lugaresPromedio = 50;
    public int ocupadosMotos;
    public int ocupadosGrandes;
    public int ocupadosPromedio;

    public SpaceManager(List<Space> espacios) {
        this.espacios = espacios;
        recomputarContadores();
    }

    public boolean verificarDisponibilidad(Vehicle v) {
        Space.TipoEspacio requerido = tipoParaVehiculo(v);
        return espacios.stream().anyMatch(e -> e.getTipo() == requerido && !e.isOcupado());
    }

    public void contabilizarIngreso(Vehicle v) {
        if (v instanceof Motorcycle) {
            ocupadosMotos++;
        } else if (v instanceof LargeVehicle) {
            ocupadosGrandes++;
        } else {
            ocupadosPromedio++;
        }
    }

    public void contabilizarSalida(Vehicle v) {
        if (v instanceof Motorcycle) {
            ocupadosMotos = Math.max(0, ocupadosMotos - 1);
        } else if (v instanceof LargeVehicle) {
            ocupadosGrandes = Math.max(0, ocupadosGrandes - 1);
        } else {
            ocupadosPromedio = Math.max(0, ocupadosPromedio - 1);
        }
    }

    public Space obtenerEspacio(int numero) {
        return espacios.stream().filter(e -> e.getNumero() == numero).findFirst().orElse(null);
    }

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

    public static Space.TipoEspacio tipoParaVehiculo(Vehicle v) {
        if (v instanceof Motorcycle) return Space.TipoEspacio.MOTO;
        if (v instanceof LargeVehicle) return Space.TipoEspacio.GRANDE;
        return Space.TipoEspacio.PROMEDIO;
    }
}
