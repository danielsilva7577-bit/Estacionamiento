package edu.lospedros.estacionamiento.UI;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public final class UiMessageDialog {
    private static final String BG = "#fffaf5";
    private static final String BORDER = "#fed7aa";
    private static final String PRIMARY = "#ea580c";
    private static final String PRIMARY_DARK = "#c2410c";
    private static final String TEXT = "#7c2d12";
    private static final String MUTED = "#9a3412";

    private UiMessageDialog() {
    }

    public static void info(Stage owner, String title, String header, String message) {
        Dialog<ButtonType> dialog = build(owner, title, header, message);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        styleButtons(dialog.getDialogPane());
        dialog.showAndWait();
    }

    public static void warning(Stage owner, String title, String header, String message) {
        Dialog<ButtonType> dialog = build(owner, title, header, message);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        styleButtons(dialog.getDialogPane());
        dialog.showAndWait();
    }

    public static void error(Stage owner, String title, String header, String message) {
        Dialog<ButtonType> dialog = build(owner, title, header, message);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        styleButtons(dialog.getDialogPane());
        dialog.showAndWait();
    }

    public static boolean confirm(Stage owner, String title, String header, String message) {
        Dialog<ButtonType> dialog = build(owner, title, header, message);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleButtons(dialog.getDialogPane());
        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static Dialog<ButtonType> build(Stage owner, String title, String header, String message) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle(title == null ? "" : title);

        Label headerLabel = new Label(header == null ? "" : header);
        headerLabel.setWrapText(true);
        headerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: " + TEXT + ";");

        Label messageLabel = new Label(message == null ? "" : message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 500; -fx-text-fill: " + MUTED + ";");

        VBox content = new VBox(8, headerLabel, messageLabel);
        content.setPadding(new Insets(12));
        content.setStyle("-fx-background-color: " + BG + "; -fx-background-radius: 0;");

        DialogPane pane = dialog.getDialogPane();
        pane.setContent(content);
        pane.setStyle("-fx-background-color: " + BG + "; -fx-border-color: " + BORDER + "; -fx-background-radius: 0; -fx-border-radius: 0;");
        return dialog;
    }

    private static void styleButtons(DialogPane pane) {
        for (Button button : pane.lookupAll(".button").stream().filter(n -> n instanceof Button).map(n -> (Button) n).toList()) {
            String base = "-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-border-width: 0; -fx-background-radius: 0; -fx-border-radius: 0;";
            String hover = "-fx-background-color: " + PRIMARY_DARK + "; -fx-text-fill: white; -fx-border-width: 0; -fx-background-radius: 0; -fx-border-radius: 0;";
            button.setStyle(base);
            button.setOnMouseEntered(e -> button.setStyle(hover));
            button.setOnMouseExited(e -> button.setStyle(base));
        }
    }
}

