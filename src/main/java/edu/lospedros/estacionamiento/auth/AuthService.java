package edu.lospedros.estacionamiento.auth;

import edu.lospedros.estacionamiento.account.Account;
import edu.lospedros.estacionamiento.account.Client;
import edu.lospedros.estacionamiento.account.Guest;
import edu.lospedros.estacionamiento.account.repository.AccountRepository;

import java.util.Optional;
import java.util.UUID;

public class AuthService {
    private final AccountRepository cuentaRepository;
    private final PasswordHasher passwordHasher;

    public AuthService(AccountRepository cuentaRepository, PasswordHasher passwordHasher) {
        this.cuentaRepository = cuentaRepository;
        this.passwordHasher = passwordHasher;
    }

    public Optional<SessionContext> login(String email, String password) {
        Optional<Account> account = cuentaRepository.findByEmail(email);
        if (account.isEmpty()) {
            return Optional.empty();
        }

        if (!account.get().autenticar(password, passwordHasher)) {
            return Optional.empty();
        }

        return Optional.of(new SessionContext(account.get()));
    }

    public SessionContext loginAsGuest() {
        String id = "guest-" + UUID.randomUUID();
        Guest guest = new Guest(id, "guest@local");
        return new SessionContext(guest);
    }

    public boolean registerClient(String email, String password, Client.VehicleProfile vehicle) {
        Client client = new Client(
                "client-" + UUID.randomUUID(),
                email,
                passwordHasher.hash(password),
                vehicle.getVehicleType(),
                vehicle.getVehicleBrand(),
                vehicle.getVehicleModel(),
                vehicle.getVehicleColor(),
                vehicle.getVehiclePlate()
        );
        return cuentaRepository.saveAccount(client);
    }
}
