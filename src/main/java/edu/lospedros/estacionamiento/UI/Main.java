package edu.lospedros.estacionamiento.UI;

import edu.lospedros.estacionamiento.account.repository.AccountRepository;
import edu.lospedros.estacionamiento.account.repository.FileAccountRepository;
import edu.lospedros.estacionamiento.auth.AuthService;
import edu.lospedros.estacionamiento.auth.PasswordHasher;
import edu.lospedros.estacionamiento.auth.SessionContext;
import javafx.application.Application;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        PasswordHasher hasher = new PasswordHasher();
        AccountRepository cuentaRepository = new FileAccountRepository(Paths.get("data", "accounts.properties"), hasher);
        AuthService authService = new AuthService(cuentaRepository, hasher);

        LoginView loginView = new LoginView();
        loginView.show(primaryStage, authService, session -> openParking(primaryStage, session));
    }

    private void openParking(Stage stage, SessionContext session) {
        PasswordHasher hasher = new PasswordHasher();
        AccountRepository cuentaRepository = new FileAccountRepository(Paths.get("data", "accounts.properties"), hasher);
        ParkingConsole parkingConsole = new ParkingConsole(session, cuentaRepository, hasher);
        parkingConsole.show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
