package edu.lospedros.estacionamiento.account;

import edu.lospedros.estacionamiento.data.Vehicle;
import edu.lospedros.estacionamiento.data.LargeVehicle;
import edu.lospedros.estacionamiento.data.StandardVehicle;
import edu.lospedros.estacionamiento.payment.PaymentProcessor;
import edu.lospedros.estacionamiento.validation.PlateValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Representa una cuenta de cliente regular.
 * <p>
 * Los clientes pueden registrar múltiples vehículos y tener métodos de pago preferidos.
 * </p>
 */
public class Client extends Account {
    /**
     * Número máximo de vehículos permitidos por cliente.
     */
    public static final int MAX_VEHICLES = 3;

    /**
     * Perfil inmutable de un vehículo asociado a un cliente.
     */
    public static final class VehicleProfile {
        private final String vehicleType;
        private final String vehicleBrand;
        private final String vehicleModel;
        private final String vehicleColor;
        private final String vehiclePlate;

        public VehicleProfile(String vehicleType, String vehicleBrand, String vehicleModel, String vehicleColor, String vehiclePlate) {
            this.vehicleType = normalizeVehicleType(vehicleType);
            this.vehicleBrand = normalizeText(vehicleBrand);
            this.vehicleModel = normalizeText(vehicleModel);
            this.vehicleColor = normalizeText(vehicleColor);
            this.vehiclePlate = PlateValidator.normalize(vehiclePlate);
        }

        public String getVehicleType() {
            return vehicleType;
        }

        public String getVehicleBrand() {
            return vehicleBrand;
        }

        public String getVehicleModel() {
            return vehicleModel;
        }

        public String getVehicleColor() {
            return vehicleColor;
        }

        public String getVehiclePlate() {
            return vehiclePlate;
        }
    }

    private final List<VehicleProfile> vehicles = new ArrayList<>();
    private String preferredPaymentMethod = "";

    /**
     * Crea un cliente sin vehículos iniciales.
     *
     * @param id           Identificador único.
     * @param email        Correo electrónico.
     * @param passwordHash Hash de la contraseña.
     */
    public Client(String id, String email, String passwordHash) {
        super(id, email, passwordHash);
    }

    /**
     * Crea un cliente con un vehículo inicial.
     *
     * @param id           Identificador único.
     * @param email        Correo electrónico.
     * @param passwordHash Hash de la contraseña.
     * @param vehicleType  Tipo de vehículo.
     * @param vehicleBrand Marca del vehículo.
     * @param vehicleModel Modelo del vehículo.
     * @param vehicleColor Color del vehículo.
     * @param vehiclePlate Placa del vehículo.
     */
    public Client(String id, String email, String passwordHash, String vehicleType, String vehicleBrand, String vehicleModel, String vehicleColor, String vehiclePlate) {
        super(id, email, passwordHash);
        addVehicle(vehicleType, vehicleBrand, vehicleModel, vehicleColor, vehiclePlate);
    }

    @Override
    public String getTipo() {
        return "CLIENT";
    }

    public double saldoCredito;

    /**
     * Registra un vehículo en la cuenta del cliente.
     *
     * @param v El vehículo a registrar.
     */
    public void registrarVehiculo(Vehicle v) {
        if (v == null) return;
        String type = (v instanceof LargeVehicle) ? "TRUCK" : (v instanceof StandardVehicle ? "CAR" : "MOTORCYCLE");
        String plate = PlateValidator.normalize(v.getPlaca());
        if (!PlateValidator.isValid(plate)) return;
        addVehicle(type, v.getMarca(), "N/A", "N/A", plate);
    }

    /**
     * Configura el método de pago preferido del cliente.
     *
     * @param p El procesador de pago.
     */
    public void configurarPago(PaymentProcessor p) {
        if (p == null) {
            preferredPaymentMethod = "";
            return;
        }
        preferredPaymentMethod = p.nombreMetodo();
    }

    /**
     * Obtiene la lista de vehículos registrados.
     *
     * @return Una copia de la lista de perfiles de vehículos.
     */
    public List<VehicleProfile> getVehicles() {
        return new ArrayList<>(vehicles);
    }

    /**
     * Añade un nuevo vehículo a la cuenta.
     *
     * @param vehicleType  Tipo de vehículo.
     * @param vehicleBrand Marca.
     * @param vehicleModel Modelo.
     * @param vehicleColor Color.
     * @param vehiclePlate Placa.
     * @return {@code true} si se añadió correctamente, {@code false} si se alcanzó el límite o la placa está duplicada.
     */
    public boolean addVehicle(String vehicleType, String vehicleBrand, String vehicleModel, String vehicleColor, String vehiclePlate) {
        if (vehicles.size() >= MAX_VEHICLES) return false;
        VehicleProfile profile = new VehicleProfile(vehicleType, vehicleBrand, vehicleModel, vehicleColor, vehiclePlate);
        if (profile.getVehicleType().isBlank()) return false;
        if (!PlateValidator.isValid(profile.getVehiclePlate())) return false;
        boolean duplicatedPlate = vehicles.stream().anyMatch(v -> v.getVehiclePlate().equals(profile.getVehiclePlate()));
        if (duplicatedPlate) return false;
        vehicles.add(profile);
        return true;
    }

    /**
     * Elimina todos los vehículos registrados.
     */
    public void clearVehicles() {
        vehicles.clear();
    }

    private VehicleProfile primaryVehicle() {
        return vehicles.isEmpty() ? null : vehicles.get(0);
    }

    // Backward-compatible accessors used by existing UI flow.
    public String getVehicleType() {
        VehicleProfile v = primaryVehicle();
        return v == null ? "" : v.getVehicleType();
    }

    public String getVehicleBrand() {
        VehicleProfile v = primaryVehicle();
        return v == null ? "" : v.getVehicleBrand();
    }

    public String getVehicleModel() {
        VehicleProfile v = primaryVehicle();
        return v == null ? "" : v.getVehicleModel();
    }

    public String getVehicleColor() {
        VehicleProfile v = primaryVehicle();
        return v == null ? "" : v.getVehicleColor();
    }

    public String getVehiclePlate() {
        VehicleProfile v = primaryVehicle();
        return v == null ? "" : v.getVehiclePlate();
    }

    public String getPreferredPaymentMethod() {
        return preferredPaymentMethod;
    }

    private static String normalizeVehicleType(String raw) {
        if (raw == null || raw.isBlank()) return "";
        String key = raw.trim().toUpperCase(Locale.ROOT);
        return switch (key) {
            case "CAR", "CARRO", "PROMEDIO" -> "CAR";
            case "MOTO", "MOTORCYCLE" -> "MOTORCYCLE";
            case "TRUCK", "CAMIONETA", "GRANDE" -> "TRUCK";
            default -> "";
        };
    }

    private static String normalizeText(String raw) {
        if (raw == null) return "";
        return raw.trim();
    }
}
