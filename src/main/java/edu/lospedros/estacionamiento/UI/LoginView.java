package edu.lospedros.estacionamiento.UI;

import edu.lospedros.estacionamiento.account.Account;
import edu.lospedros.estacionamiento.account.Client;
import edu.lospedros.estacionamiento.languages.LanguageManager;
import edu.lospedros.estacionamiento.auth.AuthService;
import edu.lospedros.estacionamiento.auth.SessionContext;
import edu.lospedros.estacionamiento.validation.PlateValidator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Vista de inicio de sesión de la aplicación.
 * <p>
 * Proporciona la interfaz gráfica para que los usuarios inicien sesión,
 * entren como invitados o se registren como nuevos clientes.
 * </p>
 */
public class LoginView {
    private static final String PRIMARY_ORANGE = "#ea580c";
    private static final String PRIMARY_ORANGE_DARK = "#c2410c";
    private static final String SOFT_ORANGE = "#fed7aa";
    private static final String DIALOG_STYLE = "-fx-background-color: #fffaf5;";

    /**
     * Muestra la pantalla de inicio de sesión.
     *
     * @param stage           El escenario principal de JavaFX.
     * @param authService     El servicio de autenticación para validar credenciales.
     * @param onAuthenticated Callback que se ejecuta cuando el usuario se autentica correctamente.
     */
    public void show(Stage stage, AuthService authService, Consumer<SessionContext> onAuthenticated) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #fffaf5; -fx-font-family: 'Segoe UI';");

        VBox leftPanel = new VBox(10);
        leftPanel.setPrefWidth(360);
        leftPanel.setPadding(new Insets(36, 34, 36, 34));
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        leftPanel.setStyle("-fx-background-color: #fff3e6; -fx-border-color: #fed7aa; -fx-border-width: 0 1 0 0;");

        Label systemName = new Label(LanguageManager.get("login.system.name"));
        systemName.setWrapText(true);
        systemName.setMaxWidth(Double.MAX_VALUE);
        systemName.setStyle("-fx-font-size: 34px; -fx-font-weight: 800; -fx-text-fill: #9a3412;");
        Label engine = new Label(LanguageManager.get("login.system.subtitle"));
        engine.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: #c2410c;");

        Region leftSpacer = new Region();
        VBox.setVgrow(leftSpacer, Priority.ALWAYS);

        Label demos1 = new Label(LanguageManager.get("login.demo.admin"));
        demos1.setStyle("-fx-font-size: 11px; -fx-text-fill: #9a3412;");
        Label demos2 = new Label(LanguageManager.get("login.demo.client"));
        demos2.setStyle("-fx-font-size: 11px; -fx-text-fill: #9a3412;");

        leftPanel.getChildren().addAll(systemName, engine, leftSpacer, demos1, demos2);

        VBox rightPanel = new VBox(14);
        rightPanel.setPadding(new Insets(42));
        rightPanel.setAlignment(Pos.CENTER_LEFT);
        rightPanel.setStyle("-fx-background-color: #ffffff;");

        Label title = new Label(LanguageManager.get("login.title"));
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #9a3412;");

        Label subtitle = new Label(LanguageManager.get("login.subtitle"));
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #c2410c;");

        Label lblEmail = new Label(LanguageManager.get("login.email.label"));
        lblEmail.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #9a3412;");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText(LanguageManager.get("login.email.prompt"));
        txtEmail.setMaxWidth(360);
        txtEmail.setStyle("-fx-background-color: #fff7ed; -fx-text-fill: #7c2d12; -fx-prompt-text-fill: #b45309; -fx-border-color: #fdba74;");

        Label lblPassword = new Label(LanguageManager.get("login.password.label"));
        lblPassword.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #9a3412;");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText(LanguageManager.get("login.password.prompt"));
        txtPassword.setMaxWidth(360);
        txtPassword.setStyle("-fx-background-color: #fff7ed; -fx-text-fill: #7c2d12; -fx-prompt-text-fill: #b45309; -fx-border-color: #fdba74;");

        Label error = new Label();
        error.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px; -fx-font-weight: 600;");

        Button btnLogin = new Button(LanguageManager.get("login.btn.login"));
        String loginBase = "-fx-background-color: #ea580c; -fx-text-fill: white; -fx-font-weight: 800; -fx-padding: 10 16; -fx-border-width: 0;";
        String loginHover = "-fx-background-color: #c2410c; -fx-text-fill: white; -fx-font-weight: 800; -fx-padding: 10 16; -fx-border-width: 0;";
        btnLogin.setStyle(loginBase);
        btnLogin.setMinWidth(170);
        btnLogin.setCursor(Cursor.HAND);
        btnLogin.setOnMouseEntered(e -> btnLogin.setStyle(loginHover));
        btnLogin.setOnMouseExited(e -> btnLogin.setStyle(loginBase));
        btnLogin.setOnAction(e -> {
            Optional<SessionContext> session = authService.login(txtEmail.getText(), txtPassword.getText());
            if (session.isPresent()) {
                onAuthenticated.accept(session.get());
            } else {
                error.setText(LanguageManager.get("login.error.invalid"));
            }
        });

        Button btnGuest = new Button(LanguageManager.get("login.btn.guest"));
        String guestBase = "-fx-background-color: #fff7ed; -fx-text-fill: #c2410c; -fx-font-weight: 700; -fx-padding: 10 16; -fx-border-width: 0;";
        String guestHover = "-fx-background-color: #fed7aa; -fx-text-fill: #9a3412; -fx-font-weight: 700; -fx-padding: 10 16; -fx-border-width: 0;";
        btnGuest.setStyle(guestBase);
        btnGuest.setMinWidth(170);
        btnGuest.setCursor(Cursor.HAND);
        btnGuest.setOnMouseEntered(e -> btnGuest.setStyle(guestHover));
        btnGuest.setOnMouseExited(e -> btnGuest.setStyle(guestBase));
        btnGuest.setOnAction(e -> onAuthenticated.accept(authService.loginAsGuest()));

        Button btnRegister = new Button(LanguageManager.get("login.btn.register"));
        btnRegister.setStyle(guestBase);
        btnRegister.setMinWidth(170);
        btnRegister.setMaxWidth(360);
        btnRegister.setCursor(Cursor.HAND);
        btnRegister.setOnMouseEntered(e -> btnRegister.setStyle(guestHover));
        btnRegister.setOnMouseExited(e -> btnRegister.setStyle(guestBase));
        btnRegister.setOnAction(e -> registerUser(stage, authService));

        btnLogin.setMaxWidth(360);
        btnGuest.setMaxWidth(360);

        VBox actions = new VBox(10, btnLogin, btnGuest, btnRegister);
        actions.setAlignment(Pos.CENTER_LEFT);
        Region actionSpacer = new Region();
        VBox.setVgrow(actionSpacer, Priority.ALWAYS);

        rightPanel.getChildren().addAll(
                title,
                subtitle,
                lblEmail,
                txtEmail,
                lblPassword,
                txtPassword,
                actionSpacer,
                actions,
                error
        );

        root.setLeft(leftPanel);
        root.setCenter(rightPanel);

        stage.setScene(new Scene(root, 980, 560));
        stage.setTitle(LanguageManager.get("login.window.title"));
        stage.show();
        stage.centerOnScreen();
    }

    private void registerUser(Stage stage, AuthService authService) {
        while (true) {
            Optional<String> email = promptRequiredText(stage,
                    LanguageManager.get("admin.users.email.prompt"),
                    LanguageManager.get("error.validation.required")
            );
            if (email.isEmpty()) return;
            String normalizedEmail = email.get().toLowerCase(Locale.ROOT);
            if (!isValidEmail(normalizedEmail)) {
                showError(stage, "error.validation.title", LanguageManager.get("error.validation.email"));
                continue;
            }

            Optional<String> password = promptRequiredText(stage,
                    LanguageManager.get("admin.users.password.prompt"),
                    LanguageManager.get("error.validation.required")
            );
            if (password.isEmpty()) return;
            if (!isValidPassword(password.get())) {
                showError(stage, "error.validation.title", LanguageManager.get("error.validation.password"));
                continue;
            }

            Optional<Client.VehicleProfile> vehicle = promptClientVehicleProfile(stage);
            if (vehicle.isEmpty()) return;

            if (authService.registerClient(normalizedEmail, password.get(), vehicle.get())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(stage);
                alert.setTitle(LanguageManager.get("login.register.success.title"));
                alert.setHeaderText(LanguageManager.get("login.register.success.header"));
                alert.setContentText(LanguageManager.get("login.register.success.msg"));
                styleDialog(alert.getDialogPane());
                alert.showAndWait();
                return;
            } else {
                showError(stage, "error.state.title", LanguageManager.get("admin.users.duplicate"));
            }
        }
    }

    private Optional<Client.VehicleProfile> promptClientVehicleProfile(Stage stage) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("CARRO", List.of("CARRO", "MOTO", "CAMIONETA"));
        dialog.initOwner(stage);
        dialog.setTitle(LanguageManager.get("admin.users.vehicle.type.title"));
        dialog.setHeaderText(LanguageManager.get("admin.users.vehicle.type.prompt"));
        dialog.setContentText(LanguageManager.get("payment.method"));
        styleDialog(dialog.getDialogPane());

        Optional<String> typeSelected = dialog.showAndWait();
        if (typeSelected.isEmpty()) return Optional.empty();

        String vehicleType = switch (typeSelected.get()) {
            case "CARRO" -> "CAR";
            case "MOTO" -> "MOTORCYCLE";
            case "CAMIONETA" -> "TRUCK";
            default -> "CAR";
        };

        Optional<String> brand = promptRequiredText(stage,
                LanguageManager.get("admin.users.vehicle.brand.prompt"),
                LanguageManager.get("error.validation.required")
        );
        if (brand.isEmpty()) return Optional.empty();

        Optional<String> model = promptRequiredText(stage,
                LanguageManager.get("admin.users.vehicle.model.prompt"),
                LanguageManager.get("error.validation.required")
        );
        if (model.isEmpty()) return Optional.empty();

        Optional<String> color = promptRequiredText(stage,
                LanguageManager.get("admin.users.vehicle.color.prompt"),
                LanguageManager.get("error.validation.required")
        );
        if (color.isEmpty()) return Optional.empty();

        String plate;
        while (true) {
            Optional<String> plateText = promptRequiredText(stage,
                    LanguageManager.get("admin.users.vehicle.plate.prompt"),
                    LanguageManager.get("error.validation.required")
            );
            if (plateText.isEmpty()) return Optional.empty();
            plate = PlateValidator.normalize(plateText.get());
            if (!PlateValidator.hasValidLength(plate)) {
                showError(stage, "error.validation.title", LanguageManager.get("error.plate.length"));
                continue;
            }
            if (!PlateValidator.hasValidFormat(plate)) {
                showError(stage, "error.validation.title", LanguageManager.get("error.plate.format"));
                continue;
            }
            break;
        }

        return Optional.of(new Client.VehicleProfile(
                vehicleType,
                brand.get(),
                model.get(),
                color.get(),
                plate
        ));
    }

    private Optional<String> promptRequiredText(Stage stage, String prompt, String missingMessage) {
        while (true) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.initOwner(stage);
            dialog.setTitle(LanguageManager.get("payment.title"));
            dialog.setHeaderText(LanguageManager.get("dialog.input.header"));
            dialog.setContentText(prompt);
            styleDialog(dialog.getDialogPane());
            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty()) return Optional.empty();
            String value = result.get().trim();
            if (!value.isEmpty()) return Optional.of(value);
            showError(stage, "error.validation.title", missingMessage);
        }
    }

    private void showError(Stage stage, String titleKey, String message) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.initOwner(stage);
        error.setTitle(LanguageManager.get(titleKey));
        error.setHeaderText(LanguageManager.get("dialog.error.header"));
        error.setContentText(message);
        styleDialog(error.getDialogPane());
        error.showAndWait();
    }

    private void styleDialog(DialogPane pane) {
        pane.setStyle(DIALOG_STYLE + " -fx-border-color: " + SOFT_ORANGE + ";");
        for (Button button : pane.lookupAll(".button").stream().filter(n -> n instanceof Button).map(n -> (Button) n).toList()) {
            String base = "-fx-background-color: " + PRIMARY_ORANGE + "; -fx-text-fill: white; -fx-border-width: 0;";
            String hover = "-fx-background-color: " + PRIMARY_ORANGE_DARK + "; -fx-text-fill: white; -fx-border-width: 0;";
            button.setStyle(base);
            button.setCursor(Cursor.HAND);
            button.setOnMouseEntered(e -> button.setStyle(hover));
            button.setOnMouseExited(e -> button.setStyle(base));
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }
}
