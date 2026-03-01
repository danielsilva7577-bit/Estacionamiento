package edu.lospedros.estacionamiento.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class JsonParkingStateRepository implements ParkingStateRepository {
    private final Path filePath;

    public JsonParkingStateRepository(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath, "filePath");
    }

    @Override
    public ParkingState load() throws IOException {
        if (!Files.exists(filePath)) {
            return new ParkingState();
        }
        if (Files.size(filePath) == 0) {
            return new ParkingState();
        }

        Properties props = new Properties();
        try (InputStream in = new BufferedInputStream(Files.newInputStream(filePath))) {
            props.load(in);
        }

        ParkingState state = new ParkingState();

        int activeCount = parseInt(props.getProperty("active.count"), 0);
        for (int i = 0; i < activeCount; i++) {
            String prefix = "active." + i + ".";
            int spaceId = parseInt(props.getProperty(prefix + "spaceId"), -1);
            if (spaceId <= 0) continue;

            ActiveTicketRecord rec = new ActiveTicketRecord(
                    props.getProperty(prefix + "ticketId"),
                    props.getProperty(prefix + "vehicleType"),
                    props.getProperty(prefix + "licensePlate"),
                    parseDateTime(props.getProperty(prefix + "entryTime")),
                    parseDouble(props.getProperty(prefix + "hourlyRate"), 0.0)
            );
            state.getActiveTickets().put(spaceId, rec);
        }

        int exitCount = parseInt(props.getProperty("exit.count"), 0);
        for (int i = 0; i < exitCount; i++) {
            String prefix = "exit." + i + ".";
            ParkingExitRecord rec = new ParkingExitRecord(
                    props.getProperty(prefix + "ticketId"),
                    parseInt(props.getProperty(prefix + "spaceId"), 0),
                    props.getProperty(prefix + "vehicleType"),
                    props.getProperty(prefix + "licensePlate"),
                    parseDateTime(props.getProperty(prefix + "entryTime")),
                    parseDateTime(props.getProperty(prefix + "exitTime")),
                    parseBigDecimal(props.getProperty(prefix + "totalPaid")),
                    props.getProperty(prefix + "paymentMethod")
            );
            state.getExitHistory().add(rec);
        }

        return state;
    }

    @Override
    public void save(ParkingState state) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Properties props = new Properties();

        int index = 0;
        for (Map.Entry<Integer, ActiveTicketRecord> entry : state.getActiveTickets().entrySet()) {
            String prefix = "active." + index + ".";
            ActiveTicketRecord rec = entry.getValue();
            props.setProperty(prefix + "spaceId", String.valueOf(entry.getKey()));
            props.setProperty(prefix + "ticketId", safe(rec.getTicketId()));
            props.setProperty(prefix + "vehicleType", safe(rec.getVehicleType()));
            props.setProperty(prefix + "licensePlate", safe(rec.getLicensePlate()));
            props.setProperty(prefix + "entryTime", rec.getEntryTime() == null ? "" : rec.getEntryTime().toString());
            props.setProperty(prefix + "hourlyRate", String.valueOf(rec.getHourlyRate()));
            index++;
        }
        props.setProperty("active.count", String.valueOf(index));

        int exitIndex = 0;
        for (ParkingExitRecord rec : state.getExitHistory()) {
            String prefix = "exit." + exitIndex + ".";
            props.setProperty(prefix + "ticketId", safe(rec.getTicketId()));
            props.setProperty(prefix + "spaceId", String.valueOf(rec.getSpaceId()));
            props.setProperty(prefix + "vehicleType", safe(rec.getVehicleType()));
            props.setProperty(prefix + "licensePlate", safe(rec.getLicensePlate()));
            props.setProperty(prefix + "entryTime", rec.getEntryTime() == null ? "" : rec.getEntryTime().toString());
            props.setProperty(prefix + "exitTime", rec.getExitTime() == null ? "" : rec.getExitTime().toString());
            props.setProperty(prefix + "totalPaid", rec.getTotalPaid() == null ? "0" : rec.getTotalPaid().toString());
            props.setProperty(prefix + "paymentMethod", safe(rec.getPaymentMethod()));
            exitIndex++;
        }
        props.setProperty("exit.count", String.valueOf(exitIndex));

        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(filePath))) {
            props.store(out, "Parking state");
        }
    }

    private static int parseInt(String value, int fallback) {
        if (value == null || value.isBlank()) return fallback;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private static LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDateTime.parse(value.trim());
        } catch (Exception ex) {
            return null;
        }
    }

    private static BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private static double parseDouble(String value, double fallback) {
        if (value == null || value.isBlank()) return fallback;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
