package edu.lospedros.estacionamiento.UI;

import edu.lospedros.estacionamiento.languages.LanguageManager;
import edu.lospedros.estacionamiento.auth.AuthService;
import edu.lospedros.estacionamiento.auth.SessionContext;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.function.Consumer;

public class LoginView {
    public void show(Stage stage, AuthService authService, Consumer<SessionContext> onAuthenticated) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #fffaf5; -fx-font-family: 'Segoe UI';");

        VBox leftPanel = new VBox(10);
        leftPanel.setPrefWidth(360);
        leftPanel.setPadding(new Insets(36, 34, 36, 34));
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        leftPanel.setStyle("-fx-background-color: #fff3e6; -fx-border-color: #fed7aa; -fx-border-width: 0 1 0 0;");

        Label systemName = new Label(LanguageManager.get("login.system.name"));
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

        TextField txtEmail = new TextField();
        txtEmail.setPromptText(LanguageManager.get("login.email.prompt"));
        txtEmail.setMaxWidth(360);
        txtEmail.setStyle("-fx-background-color: #fff7ed; -fx-text-fill: #7c2d12; -fx-prompt-text-fill: #b45309; -fx-border-color: #fdba74;");

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

        HBox actions = new HBox(10, btnLogin, btnGuest);
        actions.setAlignment(Pos.CENTER_LEFT);

        rightPanel.getChildren().addAll(title, subtitle, txtEmail, txtPassword, actions, error);

        root.setLeft(leftPanel);
        root.setCenter(rightPanel);

        stage.setScene(new Scene(root, 980, 560));
        stage.setTitle(LanguageManager.get("login.window.title"));
        stage.show();
        stage.centerOnScreen();
    }
}
