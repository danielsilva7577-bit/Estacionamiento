package edu.lospedros.estacionamiento.UI;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.scene.Cursor;

import java.util.Optional;

public class UiMessageDialog {
    private static final String PRIMARY_ORANGE = "#ea580c";
    private static final String PRIMARY_ORANGE_DARK = "#c2410c";
    private static final String SOFT_ORANGE = "#fed7aa";
    private static final String DIALOG_STYLE = "-fx-background-color: #fffaf5; -fx-background-radius: 0; -fx-border-radius: 0;";

    public static void info(Stage owner, String title, String header, String content) {
        show(owner, Alert.AlertType.INFORMATION, title, header, content);
    }

    public static void error(Stage owner, String title, String header, String content) {
        show(owner, Alert.AlertType.ERROR, title, header, content);
    }

    public static void warning(Stage owner, String title, String header, String content) {
        show(owner, Alert.AlertType.WARNING, title, header, content);
    }

    public static boolean confirm(Stage owner, String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        styleDialog(alert.getDialogPane());
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void show(Stage owner, Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        styleDialog(alert.getDialogPane());
        alert.showAndWait();
    }

    private static void styleDialog(DialogPane pane) {
        pane.setStyle(DIALOG_STYLE + " -fx-border-color: " + SOFT_ORANGE + ";");
        for (Button button : pane.lookupAll(".button").stream().filter(n -> n instanceof Button).map(n -> (Button) n).toList()) {
            String base = "-fx-background-color: " + PRIMARY_ORANGE + "; -fx-text-fill: white; -fx-border-width: 0; -fx-background-radius: 0; -fx-border-radius: 0;";
            String hover = "-fx-background-color: " + PRIMARY_ORANGE_DARK + "; -fx-text-fill: white; -fx-border-width: 0; -fx-background-radius: 0; -fx-border-radius: 0;";
            button.setStyle(base);
            button.setCursor(Cursor.HAND);
            button.setOnMouseEntered(e -> button.setStyle(hover));
            button.setOnMouseExited(e -> button.setStyle(base));
        }
    }
}
