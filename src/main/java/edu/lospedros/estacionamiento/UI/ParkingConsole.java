package edu.lospedros.estacionamiento.UI;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import edu.lospedros.estacionamiento.languages.LanguageManager;
import edu.lospedros.estacionamiento.account.Administrator;
import edu.lospedros.estacionamiento.account.Client;
import edu.lospedros.estacionamiento.account.Account;
import edu.lospedros.estacionamiento.account.repository.AccountRepository;
import edu.lospedros.estacionamiento.auth.PasswordHasher;
import edu.lospedros.estacionamiento.auth.SessionContext;
import edu.lospedros.estacionamiento.data.Motorcycle;
import edu.lospedros.estacionamiento.data.Ticket;
import edu.lospedros.estacionamiento.data.Vehicle;
import edu.lospedros.estacionamiento.data.LargeVehicle;
import edu.lospedros.estacionamiento.data.StandardVehicle;
import edu.lospedros.estacionamiento.payment.CashPayment;
import edu.lospedros.estacionamiento.payment.QrPayment;
import edu.lospedros.estacionamiento.payment.CreditCardPayment;
import edu.lospedros.estacionamiento.payment.DebitCardPayment;
import edu.lospedros.estacionamiento.payment.PaymentProcessor;
import edu.lospedros.estacionamiento.persistence.ActiveTicketRecord;
import edu.lospedros.estacionamiento.persistence.JsonParkingStateRepository;
import edu.lospedros.estacionamiento.persistence.ParkingExitRecord;
import edu.lospedros.estacionamiento.persistence.ParkingState;
import edu.lospedros.estacionamiento.persistence.ParkingStateRepository;
import edu.lospedros.estacionamiento.process.Space;
import edu.lospedros.estacionamiento.process.ParkingSystem;
import edu.lospedros.estacionamiento.validation.PlateValidator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ParkingConsole {
    private enum VehicleCategory {
        CAR("tab.cars", "vehicle.car"),
        MOTORCYCLE("tab.motorcycles", "vehicle.motorcycle"),
        TRUCK("tab.trucks", "vehicle.truck");

        private final String tabKey;
        private final String vehicleKey;

        VehicleCategory(String tabKey, String vehicleKey) {
            this.tabKey = tabKey;
            this.vehicleKey = vehicleKey;
        }

        public String tabLabel() {
            return LanguageManager.get(tabKey);
        }

        public String vehicleLabel() {
            return LanguageManager.get(vehicleKey);
        }

        public Vehicle createVehiculo(String plate) {
            return switch (this) {
                case MOTORCYCLE -> new Motorcycle(plate);
                case TRUCK -> new LargeVehicle(plate);
                case CAR -> new StandardVehicle(plate);
            };
        }

        public String persistedName() {
            return switch (this) {
                case MOTORCYCLE -> "MOTO";
                case TRUCK -> "GRANDE";
                case CAR -> "PROMEDIO";
            };
        }

        public static Optional<VehicleCategory> fromPersisted(String value) {
            if (value == null || value.isBlank()) return Optional.empty();
            String key = value.trim().toUpperCase(Locale.ROOT);
            return switch (key) {
                case "MOTO", "MOTORCYCLE" -> Optional.of(MOTORCYCLE);
                case "GRANDE", "TRUCK" -> Optional.of(TRUCK);
                case "PROMEDIO", "CAR", "MEDIUM" -> Optional.of(CAR);
                default -> Optional.empty();
            };
        }

        public static VehicleCategory fromVehiculo(Vehicle v) {
            if (v instanceof Motorcycle) return MOTORCYCLE;
            if (v instanceof LargeVehicle) return TRUCK;
            return CAR;
        }
    }

    private static final int MOTORCYCLE_SPOTS = 20;
    private static final int TRUCK_SPOTS = 30;
    private static final int CAR_SPOTS = 50;
    private static final int TOTAL_SPOTS = MOTORCYCLE_SPOTS + TRUCK_SPOTS + CAR_SPOTS;

    private static final String APP_BG = "#fffaf5";
    private static final String SIDEBAR_BG = "#fff3e6";
    private static final String PRIMARY_ORANGE = "#ea580c";
    private static final String PRIMARY_ORANGE_DARK = "#c2410c";
    private static final String ORANGE_LIGHT = "#fed7aa";
    private static final String TEXT_DARK = "#7c2d12";
    private static final String SOFT_ORANGE = "#fed7aa";
    private static final String CARD_FREE_STYLE = "-fx-background-color: #ffffff; -fx-border-color: #fdba74; -fx-border-width: 1;";
    private static final String CARD_OCCUPIED_STYLE = "-fx-background-color: #fff7ed; -fx-border-color: #fb923c; -fx-border-width: 1;";
    private static final String STATUS_FREE_STYLE = "-fx-text-fill: #16a34a; -fx-font-size: 12px; -fx-font-weight: 600;";
    private static final String STATUS_OCCUPIED_STYLE = "-fx-text-fill: #ea580c; -fx-font-size: 12px; -fx-font-weight: 600;";
    private static final String ACTION_REGISTER_STYLE = "-fx-background-color: " + PRIMARY_ORANGE + "; -fx-text-fill: #ffffff; -fx-font-size: 12px; -fx-font-weight: 600; -fx-border-width: 0;";
    private static final String ACTION_REGISTER_HOVER_STYLE = "-fx-background-color: " + PRIMARY_ORANGE_DARK + "; -fx-text-fill: #ffffff; -fx-font-size: 12px; -fx-font-weight: 600; -fx-border-width: 0;";
    private static final String ACTION_EXIT_STYLE = "-fx-background-color: #fff7ed; -fx-text-fill: " + PRIMARY_ORANGE + "; -fx-font-size: 12px; -fx-font-weight: 600; -fx-border-width: 0;";
    private static final String ACTION_EXIT_HOVER_STYLE = "-fx-background-color: " + ORANGE_LIGHT + "; -fx-text-fill: " + PRIMARY_ORANGE_DARK + "; -fx-font-size: 12px; -fx-font-weight: 600; -fx-border-width: 0;";
    private static final String SIDEBAR_BUTTON_STYLE = "-fx-background-color: transparent; -fx-text-fill: " + TEXT_DARK + "; -fx-alignment: CENTER_LEFT; -fx-padding: 10 12; -fx-font-size: 13px; -fx-font-weight: 600; -fx-border-width: 0;";
    private static final String SIDEBAR_BUTTON_HOVER_STYLE = "-fx-background-color: " + ORANGE_LIGHT + "; -fx-text-fill: " + TEXT_DARK + "; -fx-alignment: CENTER_LEFT; -fx-padding: 10 12; -fx-font-size: 13px; -fx-font-weight: 600; -fx-border-width: 0;";
    private static final String SIDEBAR_BUTTON_ACTIVE_STYLE = "-fx-background-color: " + PRIMARY_ORANGE + "; -fx-text-fill: #ffffff; -fx-alignment: CENTER_LEFT; -fx-padding: 10 12; -fx-font-size: 13px; -fx-font-weight: 600; -fx-border-width: 0;";
    private static final String DIALOG_STYLE = "-fx-background-color: #fffaf5;";

    private final SessionContext session;
    private final AccountRepository cuentaRepository;
    private final PasswordHasher passwordHasher;
    private Stage stage;
    private BorderPane rootLayout;
    private VBox contentContainer;
    private ParkingSystem sistema;
    private ParkingStateRepository stateRepository;
    private final Map<Integer, Ticket> activeTickets = new HashMap<>();
    private final List<ParkingExitRecord> exitHistory = new ArrayList<>();
    private final Map<String, String> ticketVehicleNotes = new HashMap<>();
    private String pendingVehicleNote;
    private Client.VehicleProfile selectedClientVehicle;
    private VehicleCategory selectedCategory = VehicleCategory.CAR;
    private boolean adminViewActive;

    public ParkingConsole(SessionContext session, AccountRepository cuentaRepository, PasswordHasher passwordHasher) {
        this.session = session;
        this.cuentaRepository = cuentaRepository;
        this.passwordHasher = passwordHasher;
    }

    public void show(Stage stage) {
        this.stage = stage;
        this.sistema = ParkingSystem.crearDefault();
        this.stateRepository = new JsonParkingStateRepository(Paths.get("data", "parking-state.json"));
        if (!initializeClientVehicleSelection()) return;
        loadPersistedState();
        renderUI();
    }

    private void renderUI() {
        rootLayout = new BorderPane();
        rootLayout.setStyle("-fx-background-color: " + APP_BG + "; -fx-font-family: 'Segoe UI';");
        rootLayout.setLeft(createSidebar());

        contentContainer = new VBox(12);
        contentContainer.setPadding(new Insets(18));
        rootLayout.setCenter(contentContainer);
        refreshMainContent();

        Scene scene = new Scene(rootLayout, 1150, 760);
        stage.setTitle(LanguageManager.get("window.title"));
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(300);
        sidebar.setMinWidth(300);
        sidebar.setPadding(new Insets(20, 14, 20, 14));
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_BG + "; -fx-border-color: " + SOFT_ORANGE + "; -fx-border-width: 0 1 0 0;");

        Label appLabel = new Label(LanguageManager.get("window.title"));
        appLabel.setWrapText(true);
        appLabel.setMaxWidth(Double.MAX_VALUE);
        appLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 700; -fx-text-fill: #7c2d12;");

        String role = session.isAdmin() ? "ADMIN" : (session.isGuest() ? "GUEST" : "CLIENT");
        Label userInfo = new Label(session.getCurrentAccount().getEmail() + " | " + role);
        userInfo.setWrapText(true);
        userInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: #9a3412;");

        Label navTitle = new Label(LanguageManager.get("dashboard.nav.section"));
        navTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: #c2410c; -fx-font-weight: 700;");

        VBox nav;
        if (session.getCurrentAccount() instanceof Client) {
            nav = new VBox(6, createVehicleNavButton(selectedCategory));
        } else {
            Button btnCars = createVehicleNavButton(VehicleCategory.CAR);
            Button btnMotorcycles = createVehicleNavButton(VehicleCategory.MOTORCYCLE);
            Button btnTrucks = createVehicleNavButton(VehicleCategory.TRUCK);
            nav = new VBox(6, btnCars, btnMotorcycles, btnTrucks);
        }

        VBox clientSection = new VBox();
        if (session.getCurrentAccount() instanceof Client) {
            Button btnAddVehicle = new Button(LanguageManager.get("client.vehicles.add"));
            btnAddVehicle.setMaxWidth(Double.MAX_VALUE);
            btnAddVehicle.setStyle(SIDEBAR_BUTTON_STYLE);
            applyHover(btnAddVehicle, SIDEBAR_BUTTON_STYLE, SIDEBAR_BUTTON_HOVER_STYLE);
            btnAddVehicle.setOnAction(e -> addVehicleToCurrentClient());
            Button btnRemoveVehicle = new Button(LanguageManager.get("client.vehicles.remove"));
            btnRemoveVehicle.setMaxWidth(Double.MAX_VALUE);
            btnRemoveVehicle.setStyle(SIDEBAR_BUTTON_STYLE);
            applyHover(btnRemoveVehicle, SIDEBAR_BUTTON_STYLE, SIDEBAR_BUTTON_HOVER_STYLE);
            btnRemoveVehicle.setOnAction(e -> removeVehicleFromCurrentClient());
            Button btnChangeVehicle = new Button(LanguageManager.get("client.vehicles.change"));
            btnChangeVehicle.setMaxWidth(Double.MAX_VALUE);
            btnChangeVehicle.setStyle(SIDEBAR_BUTTON_STYLE);
            applyHover(btnChangeVehicle, SIDEBAR_BUTTON_STYLE, SIDEBAR_BUTTON_HOVER_STYLE);
            btnChangeVehicle.setOnAction(e -> {
                if (initializeClientVehicleSelection()) {
                    refreshMainContent();
                    rootLayout.setLeft(createSidebar());
                }
            });
            clientSection.getChildren().addAll(new Separator(), btnAddVehicle, btnRemoveVehicle, btnChangeVehicle);
            clientSection.setSpacing(8);
        }

        VBox adminSection = new VBox();
        if (session.isAdmin()) {
            Label adminTitle = new Label("ADMIN");
            adminTitle.setText(LanguageManager.get("dashboard.admin.section"));
            adminTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: #c2410c; -fx-font-weight: 700;");
        Button btnAdmin = new Button(LanguageManager.get("admin.tab"));
        btnAdmin.setMaxWidth(Double.MAX_VALUE);
        btnAdmin.setStyle(SIDEBAR_BUTTON_STYLE);
        applyHover(btnAdmin, SIDEBAR_BUTTON_STYLE, SIDEBAR_BUTTON_HOVER_STYLE);
        btnAdmin.setOnAction(e -> {
            adminViewActive = true;
            refreshMainContent();
            rootLayout.setLeft(createSidebar());
        });
            adminSection.getChildren().addAll(new Separator(), adminTitle, btnAdmin);
            adminSection.setSpacing(8);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox lang = new HBox(8);
        Button btnEs = new Button("ES");
        Button btnEn = new Button("EN");
        String langBase = "-fx-background-color: #fff7ed; -fx-text-fill: " + TEXT_DARK + "; -fx-border-width: 0;";
        String langHover = "-fx-background-color: " + ORANGE_LIGHT + "; -fx-text-fill: " + PRIMARY_ORANGE_DARK + "; -fx-border-width: 0;";
        btnEs.setStyle(langBase);
        btnEn.setStyle(langBase);
        applyHover(btnEs, langBase, langHover);
        applyHover(btnEn, langBase, langHover);
        btnEs.setOnAction(e -> {
            LanguageManager.setLocale("es");
            renderUI();
        });
        btnEn.setOnAction(e -> {
            LanguageManager.setLocale("en");
            renderUI();
        });
        lang.getChildren().addAll(btnEs, btnEn);

        sidebar.getChildren().addAll(appLabel, userInfo, new Separator(), navTitle, nav, clientSection, adminSection, spacer, lang);
        return sidebar;
    }

    private Button createVehicleNavButton(VehicleCategory category) {
        Button button = new Button(category.vehicleLabel());
        button.setMaxWidth(Double.MAX_VALUE);
        boolean selected = !adminViewActive && selectedCategory == category;
        if (selected) {
            button.setStyle(SIDEBAR_BUTTON_ACTIVE_STYLE);
            applyHover(button, SIDEBAR_BUTTON_ACTIVE_STYLE, SIDEBAR_BUTTON_ACTIVE_STYLE);
        } else {
            button.setStyle(SIDEBAR_BUTTON_STYLE);
            applyHover(button, SIDEBAR_BUTTON_STYLE, SIDEBAR_BUTTON_HOVER_STYLE);
        }
        button.setOnAction(e -> {
            adminViewActive = false;
            selectedCategory = category;
            refreshMainContent();
            rootLayout.setLeft(createSidebar());
        });
        return button;
    }

    private void refreshMainContent() {
        contentContainer.getChildren().clear();

        if (adminViewActive) {
            contentContainer.getChildren().add(createAdminPanel());
            return;
        }

        Label title = new Label(selectedCategory.vehicleLabel());
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: 700; -fx-text-fill: #9a3412;");

        int occupiedInCategory = getOccupiedSpotsCount(selectedCategory);
        int availableInCategory = getAvailableSpotsCount(selectedCategory);

        HBox metrics = new HBox(10);
        metrics.setAlignment(Pos.CENTER_LEFT);
        metrics.getChildren().addAll(
                buildStatBadge(occupiedInCategory, LanguageManager.get("label.active.spots"), "#ffedd5", "#9a3412"),
                buildStatBadge(availableInCategory, LanguageManager.get("label.available.spots"), "#dcfce7", "#166534")
        );

        Label hint = new Label(LanguageManager.get("dashboard.filter.hint") + " " + selectedCategory.vehicleLabel() + ".");
        hint.setStyle("-fx-font-size: 12px; -fx-text-fill: #9a3412;");

        ScrollPane scroll = createVehicleGrid(selectedCategory);
        contentContainer.getChildren().addAll(title, metrics, hint, scroll);
    }

    private ScrollPane createVehicleGrid(VehicleCategory category) {
        int startId = switch (category) {
            case MOTORCYCLE -> 1;
            case TRUCK -> MOTORCYCLE_SPOTS + 1;
            case CAR -> MOTORCYCLE_SPOTS + TRUCK_SPOTS + 1;
        };
        int count = switch (category) {
            case MOTORCYCLE -> MOTORCYCLE_SPOTS;
            case TRUCK -> TRUCK_SPOTS;
            case CAR -> CAR_SPOTS;
        };
        int columns = switch (category) {
            case MOTORCYCLE -> 5;
            case TRUCK -> 6;
            case CAR -> 8;
        };

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(8));

        for (int i = 0; i < count; i++) {
            int spaceId = startId + i;
            VBox card = createSpaceCard(spaceId, category);
            grid.add(card, i % columns, i / columns);
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scroll;
    }

    private VBox createAdminPanel() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(12));
        root.setStyle("-fx-background-color: #ffffff; -fx-border-color: " + SOFT_ORANGE + ";");

        Label sectionUsers = new Label(LanguageManager.get("admin.users.title"));
        sectionUsers.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: #9a3412;");

        ListView<String> usersList = new ListView<>();
        usersList.setPrefHeight(220);

        Runnable refreshUsers = () -> {
            usersList.getItems().clear();
            for (Account c : cuentaRepository.findAll()) {
                usersList.getItems().add(c.getEmail() + " | " + c.getTipo());
            }
        };

        Button btnAddUser = new Button(LanguageManager.get("admin.users.add"));
        btnAddUser.setStyle(ACTION_REGISTER_STYLE);
        applyHover(btnAddUser, ACTION_REGISTER_STYLE, ACTION_REGISTER_HOVER_STYLE);
        btnAddUser.setOnAction(e -> {
            ChoiceDialog<String> roleDialog = new ChoiceDialog<>("CLIENT", List.of("CLIENT", "ADMIN"));
            roleDialog.initOwner(stage);
            styleDialog(roleDialog.getDialogPane());
            Optional<String> role = roleDialog.showAndWait();
            if (role.isEmpty()) return;
            while (true) {
                Optional<String> email = promptRequiredText(
                        LanguageManager.get("admin.users.email.prompt"),
                        LanguageManager.get("error.validation.required")
                );
                if (email.isEmpty()) return;
                String normalizedEmail = email.get().toLowerCase(Locale.ROOT);
                if (!isValidEmail(normalizedEmail)) {
                    showError("error.validation.title", LanguageManager.get("error.validation.email"));
                    continue;
                }

                Optional<String> password = promptRequiredText(
                        LanguageManager.get("admin.users.password.prompt"),
                        LanguageManager.get("error.validation.required")
                );
                if (password.isEmpty()) return;
                if (!isValidPassword(password.get())) {
                    showError("error.validation.title", LanguageManager.get("error.validation.password"));
                    continue;
                }

                Account newAccount;
                if ("ADMIN".equals(role.get())) {
                    newAccount = new Administrator("admin-" + UUID.randomUUID(), normalizedEmail, passwordHasher.hash(password.get()));
                } else {
                    Client cliente = new Client(
                            "client-" + UUID.randomUUID(),
                            normalizedEmail,
                            passwordHasher.hash(password.get())
                    );

                    Optional<List<Client.VehicleProfile>> vehicles = promptClientVehicles();
                    if (vehicles.isEmpty() || vehicles.get().isEmpty()) return;

                    boolean vehicleValidationFailed = false;
                    for (Client.VehicleProfile vehicle : vehicles.get()) {
                        if (cliente.getVehicles().size() >= Client.MAX_VEHICLES) {
                            showError("error.validation.title", LanguageManager.get("admin.users.vehicle.limit"));
                            vehicleValidationFailed = true;
                            break;
                        }
                        String plate = PlateValidator.normalize(vehicle.getVehiclePlate());
                        boolean duplicatePlate = cliente.getVehicles().stream()
                                .anyMatch(v -> v.getVehiclePlate().equalsIgnoreCase(plate));
                        if (duplicatePlate) {
                            showError("error.validation.title", LanguageManager.get("error.plate.duplicate"));
                            vehicleValidationFailed = true;
                            break;
                        }
                        if (!cliente.addVehicle(
                                vehicle.getVehicleType(),
                                vehicle.getVehicleBrand(),
                                vehicle.getVehicleModel(),
                                vehicle.getVehicleColor(),
                                vehicle.getVehiclePlate()
                        )) {
                            showError("error.validation.title", LanguageManager.get("error.vehicle.add"));
                            vehicleValidationFailed = true;
                            break;
                        }
                    }
                    if (vehicleValidationFailed) {
                        continue;
                    }
                    newAccount = cliente;
                }

                if (!cuentaRepository.saveAccount(newAccount)) {
                    showError("error.state.title", LanguageManager.get("admin.users.duplicate"));
                    continue;
                }
                refreshUsers.run();
                return;
            }
        });

        Button btnDeleteUser = new Button(LanguageManager.get("admin.users.delete"));
        btnDeleteUser.setStyle(ACTION_EXIT_STYLE);
        applyHover(btnDeleteUser, ACTION_EXIT_STYLE, ACTION_EXIT_HOVER_STYLE);
        btnDeleteUser.setOnAction(e -> {
            String selected = usersList.getSelectionModel().getSelectedItem();
            if (selected == null || selected.isBlank()) return;

            String email = selected.split("\\|")[0].trim();
            if (email.equalsIgnoreCase(session.getCurrentAccount().getEmail())) {
                showError("error.state.title", LanguageManager.get("admin.users.delete.self"));
                return;
            }
            if (!cuentaRepository.deleteByEmail(email)) {
                showError("error.state.title", LanguageManager.get("admin.users.delete.fail"));
                return;
            }
            refreshUsers.run();
        });

        HBox userActions = new HBox(8, btnAddUser, btnDeleteUser);
        userActions.setAlignment(Pos.CENTER_LEFT);

        Label sectionReports = new Label(LanguageManager.get("admin.report.title"));
        sectionReports.setStyle("-fx-font-size: 16px; -fx-font-weight: 700;");

        Label lblTotalExits = new Label();
        Label lblTotalIncome = new Label();
        Label lblTodayIncome = new Label();

        Runnable refreshReport = () -> {
            BigDecimal total = BigDecimal.ZERO;
            BigDecimal today = BigDecimal.ZERO;
            LocalDateTime now = LocalDateTime.now();

            for (ParkingExitRecord r : exitHistory) {
                if (r.getTotalPaid() != null) {
                    total = total.add(r.getTotalPaid());
                    if (r.getExitTime() != null && r.getExitTime().toLocalDate().equals(now.toLocalDate())) {
                        today = today.add(r.getTotalPaid());
                    }
                }
            }

            lblTotalExits.setText(LanguageManager.get("admin.report.exits") + ": " + exitHistory.size());
            lblTotalIncome.setText(LanguageManager.get("admin.report.total") + ": $" + total.setScale(2, RoundingMode.HALF_UP));
            lblTodayIncome.setText(LanguageManager.get("admin.report.today") + ": $" + today.setScale(2, RoundingMode.HALF_UP));
        };

        Button btnRefresh = new Button(LanguageManager.get("admin.refresh"));
        String refreshBase = "-fx-background-color: #fff7ed; -fx-text-fill: #9a3412; -fx-border-width: 0;";
        String refreshHover = "-fx-background-color: " + ORANGE_LIGHT + "; -fx-text-fill: " + PRIMARY_ORANGE_DARK + "; -fx-border-width: 0;";
        btnRefresh.setStyle(refreshBase);
        applyHover(btnRefresh, refreshBase, refreshHover);
        btnRefresh.setOnAction(e -> {
            refreshUsers.run();
            refreshReport.run();
        });

        refreshUsers.run();
        refreshReport.run();

        root.getChildren().addAll(
                sectionUsers,
                usersList,
                userActions,
                new Separator(),
                sectionReports,
                lblTotalExits,
                lblTotalIncome,
                lblTodayIncome,
                btnRefresh
        );

        return root;
    }

    private VBox createSpaceCard(int spaceId, VehicleCategory category) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12));
        card.setPrefSize(165, 125);
        card.setStyle(CARD_FREE_STYLE);

        Label lblId = new Label(LanguageManager.get("label.space") + " #" + spaceId);
        lblId.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: #0f172a;");

        Label lblType = new Label(category.vehicleLabel());
        lblType.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        Label lblStatus = new Label(LanguageManager.get("status.free"));
        lblStatus.setStyle(STATUS_FREE_STYLE);

        Button btnAction = new Button(LanguageManager.get("btn.register.short"));
        btnAction.setStyle(ACTION_REGISTER_STYLE);
        applyHover(btnAction, ACTION_REGISTER_STYLE, ACTION_REGISTER_HOVER_STYLE);

        if (activeTickets.containsKey(spaceId)) {
            markAsOccupied(card, lblStatus, btnAction);
        }

        btnAction.setOnAction(e -> {
            if (activeTickets.containsKey(spaceId)) {
                processExit(spaceId, card, lblStatus, btnAction);
            } else {
                registerEntry(spaceId, category, card, lblStatus, btnAction);
            }
        });

        card.getChildren().addAll(lblId, lblType, lblStatus, btnAction);
        return card;
    }

    private void loadPersistedState() {
        try {
            ParkingState persisted = stateRepository.load();
            activeTickets.clear();
            exitHistory.clear();
            if (persisted.getExitHistory() != null) {
                exitHistory.addAll(persisted.getExitHistory());
            }

            for (Map.Entry<Integer, ActiveTicketRecord> entry : persisted.getActiveTickets().entrySet()) {
                int spaceId = entry.getKey();
                if (spaceId <= 0 || spaceId > TOTAL_SPOTS) continue;
                ActiveTicketRecord record = entry.getValue();
                if (record == null) continue;

                Optional<VehicleCategory> category = VehicleCategory.fromPersisted(record.getVehicleType());
                if (category.isEmpty() || record.getLicensePlate() == null || record.getLicensePlate().isBlank()) {
                    continue;
                }

                Space espacio = sistema.getEspacio(spaceId);
                if (espacio == null || espacio.isOcupado()) continue;

                Vehicle vehiculo = category.get().createVehiculo(record.getLicensePlate());
                int folio = parseFolio(record.getTicketId());
                Ticket ticket = new Ticket(folio, record.getEntryTime() == null ? LocalDateTime.now() : record.getEntryTime());
                ticket.vincularDatos(vehiculo, null);
                ticket.setEspacioNumero(spaceId);
                ticket.setTarifaPorHoraAplicada(record.getHourlyRate());

                espacio.setOcupado(true);
                sistema.getGestor().contabilizarIngreso(vehiculo);
                activeTickets.put(spaceId, ticket);
            }
        } catch (IOException ex) {
            showPersistenceError(ex);
        }
    }

    private void persistState() {
        ParkingState state = new ParkingState();
        Map<Integer, ActiveTicketRecord> serializedTickets = new HashMap<>();

        for (Map.Entry<Integer, Ticket> entry : activeTickets.entrySet()) {
            int spaceId = entry.getKey();
            Ticket ticket = entry.getValue();
            Vehicle vehiculo = ticket.getVehiculo();
            VehicleCategory category = VehicleCategory.fromVehiculo(vehiculo);

            serializedTickets.put(spaceId, new ActiveTicketRecord(
                    ticket.getId(),
                    category.persistedName(),
                    vehiculo.getPlaca(),
                    ticket.getEntryTime(),
                    ticket.getTarifaPorHoraAplicada()
            ));
        }

        state.setActiveTickets(serializedTickets);
        state.setExitHistory(new ArrayList<>(exitHistory));

        try {
            stateRepository.save(state);
        } catch (IOException ex) {
            showPersistenceError(ex);
        }
    }

    private void registerEntry(int spaceId, VehicleCategory category, VBox card, Label lblStatus, Button btnAction) {
        Optional<Vehicle> vehicleResult = resolveVehicleForEntry(category);
        if (vehicleResult.isEmpty()) return;

        Space espacio = sistema.getEspacio(spaceId);
        Ticket ticket = sistema.registrarIngreso(session.getCurrentAccount(), espacio, vehicleResult.get(), new CashPayment());
        if (ticket == null) {
            showError("error.not.available.title", LanguageManager.get("error.not.available.msg") + " #" + spaceId + ".");
            return;
        }

        activeTickets.put(spaceId, ticket);
        if (pendingVehicleNote != null && !pendingVehicleNote.isBlank()) {
            ticketVehicleNotes.put(ticket.getId(), pendingVehicleNote);
            pendingVehicleNote = null;
        }
        markAsOccupied(card, lblStatus, btnAction);
        persistState();
        refreshMainContent();
    }

    private void processExit(int spaceId, VBox card, Label lblStatus, Button btnAction) {
        Ticket ticket = activeTickets.get(spaceId);
        if (ticket == null) {
            showError("error.no.ticket.title", LanguageManager.get("error.no.ticket.msg") + " #" + spaceId + ".");
            return;
        }

        Optional<PaymentProcessor> paymentProcessorResult = promptPaymentProcessor(BigDecimal.valueOf(ticket.getTarifaPorHoraAplicada()));
        if (paymentProcessorResult.isEmpty()) return;

        PaymentProcessor paymentProcessor = paymentProcessorResult.get();
        ticket.setProcesadorPago(paymentProcessor);

        if (!sistema.registrarSalida(ticket)) {
            showError("error.payment.title", LanguageManager.get("error.payment.msg"));
            return;
        }

        BigDecimal total = BigDecimal.valueOf(ticket.getMontoACobrar()).setScale(2, RoundingMode.HALF_UP);

        VehicleCategory category = VehicleCategory.fromVehiculo(ticket.getVehiculo());
        exitHistory.add(new ParkingExitRecord(
                ticket.getId(),
                spaceId,
                category.persistedName(),
                ticket.getVehiculo().getPlaca(),
                ticket.getEntryTime(),
                ticket.getExitTime(),
                total,
                paymentProcessor.nombreMetodo()
        ));

        activeTickets.remove(spaceId);
        releaseSpace(card, lblStatus, btnAction);
        persistState();
        refreshMainContent();

        Duration duration = ticket.calculateParkingDuration();
        long minutes = duration.toMinutes();
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle(LanguageManager.get("alert.exit.title"));
        alert.setHeaderText(LanguageManager.get("alert.exit.header"));
        alert.setContentText(
                LanguageManager.get("receipt.plate") + ": " + ticket.getVehiculo().getPlaca() + "\n" +
                LanguageManager.get("receipt.time") + ": " + hours + "h " + remainingMinutes + "m\n" +
                LanguageManager.get("receipt.total") + ": $" + total + "\n" +
                LanguageManager.get("receipt.payment") + ": " + paymentProcessor.nombreMetodo() +
                buildGuestTicketNote(ticket.getId())
        );
        styleDialog(alert.getDialogPane());
        alert.showAndWait();
        ticketVehicleNotes.remove(ticket.getId());
    }

    private Optional<Vehicle> openRegisterForm(VehicleCategory category) {
        pendingVehicleNote = null;
        String plateValue = "";
        String brandValue = "";
        String modelValue = "";
        String colorValue = "";

        while (true) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(stage);
            dialog.setTitle(LanguageManager.get("dialog.entry.title"));
            dialog.setHeaderText(LanguageManager.get("dialog.entry.header") + " " + category.vehicleLabel());
            styleDialog(dialog.getDialogPane());

            ButtonType regBtn = new ButtonType(LanguageManager.get("btn.register.short"), ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(regBtn, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 50, 10, 10));

            TextField txtPlaca = new TextField(plateValue);
            txtPlaca.setPromptText(LanguageManager.get("prompt.plate"));
            TextField txtMarca = new TextField(brandValue);
            TextField txtModelo = new TextField(modelValue);
            TextField txtColor = new TextField(colorValue);

            grid.add(new Label(LanguageManager.get("label.plate") + ":"), 0, 0);
            grid.add(txtPlaca, 1, 0);
            if (session.isGuest()) {
                txtMarca.setPromptText(LanguageManager.get("admin.users.vehicle.brand.prompt"));
                txtModelo.setPromptText(LanguageManager.get("admin.users.vehicle.model.prompt"));
                txtColor.setPromptText(LanguageManager.get("admin.users.vehicle.color.prompt"));
                grid.add(new Label(LanguageManager.get("admin.users.vehicle.brand.prompt")), 0, 1);
                grid.add(txtMarca, 1, 1);
                grid.add(new Label(LanguageManager.get("admin.users.vehicle.model.prompt")), 0, 2);
                grid.add(txtModelo, 1, 2);
                grid.add(new Label(LanguageManager.get("admin.users.vehicle.color.prompt")), 0, 3);
                grid.add(txtColor, 1, 3);
            }

            dialog.getDialogPane().setContent(grid);
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isEmpty() || result.get() != regBtn) return Optional.empty();

            plateValue = txtPlaca.getText() == null ? "" : txtPlaca.getText().trim();
            brandValue = txtMarca.getText() == null ? "" : txtMarca.getText().trim();
            modelValue = txtModelo.getText() == null ? "" : txtModelo.getText().trim();
            colorValue = txtColor.getText() == null ? "" : txtColor.getText().trim();

            String plate = PlateValidator.normalize(plateValue);
            if (plate.isEmpty()) {
                showError("error.missing.title", LanguageManager.get("error.missing.plate.msg"));
                continue;
            }
            if (!PlateValidator.hasValidLength(plate)) {
                showError("error.missing.title", LanguageManager.get("error.plate.length"));
                continue;
            }
            if (!PlateValidator.hasValidFormat(plate)) {
                showError("error.missing.title", LanguageManager.get("error.plate.format"));
                continue;
            }

            if (session.isGuest()) {
                if (brandValue.isBlank() || modelValue.isBlank() || colorValue.isBlank()) {
                    showError("error.missing.title", LanguageManager.get("admin.users.vehicle.guest.required"));
                    continue;
                }
                pendingVehicleNote = "\n" +
                        LanguageManager.get("admin.users.vehicle.brand.prompt") + " " + brandValue + "\n" +
                        LanguageManager.get("admin.users.vehicle.model.prompt") + " " + modelValue + "\n" +
                        LanguageManager.get("admin.users.vehicle.color.prompt") + " " + colorValue;
            }
            return Optional.of(category.createVehiculo(plate));
        }
    }

    private Optional<Vehicle> resolveVehicleForEntry(VehicleCategory category) {
        if (session.getCurrentAccount() instanceof Client cliente) {
            if (selectedClientVehicle != null) {
                Optional<VehicleCategory> selectedVehicleCategory = categoryFromVehicleType(selectedClientVehicle.getVehicleType());
                if (selectedVehicleCategory.isPresent()
                        && selectedVehicleCategory.get() == category
                        && PlateValidator.isValid(selectedClientVehicle.getVehiclePlate())) {
                    pendingVehicleNote = "\n" +
                            LanguageManager.get("admin.users.vehicle.brand.prompt") + " " + selectedClientVehicle.getVehicleBrand() + "\n" +
                            LanguageManager.get("admin.users.vehicle.model.prompt") + " " + selectedClientVehicle.getVehicleModel() + "\n" +
                            LanguageManager.get("admin.users.vehicle.color.prompt") + " " + selectedClientVehicle.getVehicleColor();
                    return Optional.of(category.createVehiculo(selectedClientVehicle.getVehiclePlate()));
                }
            }

            List<Client.VehicleProfile> matches = cliente.getVehicles().stream()
                    .filter(v -> category.name().equalsIgnoreCase(v.getVehicleType()))
                    .toList();

            if (matches.isEmpty()) {
                showError("error.missing.title", LanguageManager.get("client.vehicles.none.for.category"));
                return Optional.empty();
            }

            Client.VehicleProfile selectedVehicle;
            if (matches.size() == 1) {
                selectedVehicle = matches.get(0);
            } else {
                Optional<Client.VehicleProfile> selected = promptSelectClientVehicle(matches);
                if (selected.isEmpty()) return Optional.empty();
                selectedVehicle = selected.get();
            }

            selectedClientVehicle = selectedVehicle;
            selectedCategory = category;

            pendingVehicleNote = "\n" +
                    LanguageManager.get("admin.users.vehicle.brand.prompt") + " " + selectedVehicle.getVehicleBrand() + "\n" +
                    LanguageManager.get("admin.users.vehicle.model.prompt") + " " + selectedVehicle.getVehicleModel() + "\n" +
                    LanguageManager.get("admin.users.vehicle.color.prompt") + " " + selectedVehicle.getVehicleColor();
            return Optional.of(category.createVehiculo(selectedVehicle.getVehiclePlate()));
        }
        return openRegisterForm(category);
    }

    private Optional<PaymentProcessor> promptPaymentProcessor(BigDecimal total) {
        ChoiceDialog<String> methodDialog = new ChoiceDialog<>("EFECTIVO", List.of("EFECTIVO", "TARJETA_DEBITO", "TARJETA_CREDITO", "QR"));
        methodDialog.initOwner(stage);
        methodDialog.setTitle(LanguageManager.get("payment.title"));
        methodDialog.setHeaderText(LanguageManager.get("payment.select") + " ($" + total + ")");
        methodDialog.setContentText(LanguageManager.get("payment.method"));
        styleDialog(methodDialog.getDialogPane());

        Optional<String> methodResult = methodDialog.showAndWait();
        if (methodResult.isEmpty()) return Optional.empty();

        String method = methodResult.get();
        if ("EFECTIVO".equals(method)) return Optional.of(new CashPayment());

        if ("QR".equals(method)) {
            Optional<String> token = promptQrPayment(total);
            if (token.isEmpty()) return Optional.empty();
            return Optional.of(new QrPayment(token.get()));
        }

        Optional<String> cardNumber = promptText(LanguageManager.get("payment.card.number"));
        if (cardNumber.isEmpty()) return Optional.empty();

        Optional<String> holder = promptText(LanguageManager.get("payment.card.holder"));
        if (holder.isEmpty()) return Optional.empty();

        Optional<String> nipText = promptText(LanguageManager.get("payment.card.nip"));
        if (nipText.isEmpty()) return Optional.empty();

        int nip;
        try {
            nip = Integer.parseInt(nipText.get().trim());
        } catch (NumberFormatException ex) {
            showError("error.payment.title", LanguageManager.get("error.payment.nip"));
            return Optional.empty();
        }

        if ("TARJETA_DEBITO".equals(method)) {
            return Optional.of(new DebitCardPayment(cardNumber.get(), holder.get(), nip));
        }
        return Optional.of(new CreditCardPayment(cardNumber.get(), holder.get(), nip));
    }

    private Optional<String> promptQrPayment(BigDecimal total) {
        String token = generateQrToken();
        String qrContent = "parking://pay?token=" + token + "&amount=" + total.setScale(2, RoundingMode.HALF_UP);

        WritableImage qrImage;
        try {
            qrImage = buildQrImage(qrContent, 280);
        } catch (WriterException ex) {
            showError("error.payment.title", LanguageManager.get("payment.qr.generate.error"));
            return Optional.empty();
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle(LanguageManager.get("payment.title"));
        dialog.setHeaderText(LanguageManager.get("payment.qr.header") + " ($" + total.setScale(2, RoundingMode.HALF_UP) + ")");
        styleDialog(dialog.getDialogPane());

        ButtonType confirmBtn = new ButtonType(LanguageManager.get("payment.qr.confirm"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmBtn, ButtonType.CANCEL);

        ImageView qrView = new ImageView(qrImage);
        qrView.setFitWidth(280);
        qrView.setFitHeight(280);
        qrView.setPreserveRatio(true);

        Label instruction = new Label(LanguageManager.get("payment.qr.scan"));
        Label tokenLabel = new Label(LanguageManager.get("payment.qr.token") + " " + token);

        VBox content = new VBox(10, instruction, qrView, tokenLabel);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != confirmBtn) return Optional.empty();
        return Optional.of(token);
    }

    private WritableImage buildQrImage(String content, int size) throws WriterException {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);
        WritableImage image = new WritableImage(size, size);
        PixelWriter writer = image.getPixelWriter();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                writer.setColor(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return image;
    }

    private String generateQrToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase(Locale.ROOT);
    }

    private Optional<String> promptText(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(stage);
        dialog.setTitle(LanguageManager.get("payment.title"));
        dialog.setHeaderText(LanguageManager.get("dialog.input.header"));
        dialog.setContentText(prompt);
        styleDialog(dialog.getDialogPane());
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().trim().isEmpty()) return Optional.empty();
        return Optional.of(result.get().trim());
    }

    private Optional<String> promptRequiredText(String prompt, String missingMessage) {
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
            showError("error.validation.title", missingMessage);
        }
    }

    private void markAsOccupied(VBox card, Label lblStatus, Button btnAction) {
        lblStatus.setText(LanguageManager.get("status.occupied"));
        lblStatus.setStyle(STATUS_OCCUPIED_STYLE);
        card.setStyle(CARD_OCCUPIED_STYLE);
        btnAction.setText(LanguageManager.get("btn.exit"));
        btnAction.setStyle(ACTION_EXIT_STYLE);
        applyHover(btnAction, ACTION_EXIT_STYLE, ACTION_EXIT_HOVER_STYLE);
    }

    private void releaseSpace(VBox card, Label lblStatus, Button btnAction) {
        lblStatus.setText(LanguageManager.get("status.free"));
        lblStatus.setStyle(STATUS_FREE_STYLE);
        card.setStyle(CARD_FREE_STYLE);
        btnAction.setText(LanguageManager.get("btn.register.short"));
        btnAction.setStyle(ACTION_REGISTER_STYLE);
        applyHover(btnAction, ACTION_REGISTER_STYLE, ACTION_REGISTER_HOVER_STYLE);
    }

    private void showError(String titleKey, String message) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.initOwner(stage);
        error.setTitle(LanguageManager.get(titleKey));
        error.setHeaderText(LanguageManager.get("dialog.error.header"));
        error.setContentText(message);
        styleDialog(error.getDialogPane());
        error.showAndWait();
    }

    private void showPersistenceError(IOException ex) {
        Alert warning = new Alert(Alert.AlertType.WARNING);
        warning.initOwner(stage);
        warning.setTitle(LanguageManager.get("error.persistence.title"));
        warning.setHeaderText(LanguageManager.get("dialog.warning.header"));
        warning.setContentText(LanguageManager.get("error.persistence.msg") + "\n" + ex.getMessage());
        styleDialog(warning.getDialogPane());
        warning.showAndWait();
    }

    private void styleDialog(DialogPane pane) {
        pane.setStyle(DIALOG_STYLE + " -fx-border-color: " + SOFT_ORANGE + ";");
        for (Button button : pane.lookupAll(".button").stream().filter(n -> n instanceof Button).map(n -> (Button) n).toList()) {
            String base = "-fx-background-color: " + PRIMARY_ORANGE + "; -fx-text-fill: white; -fx-border-width: 0;";
            String hover = "-fx-background-color: " + PRIMARY_ORANGE_DARK + "; -fx-text-fill: white; -fx-border-width: 0;";
            button.setStyle(base);
            applyHover(button, base, hover);
        }
    }

    private void applyHover(Button button, String baseStyle, String hoverStyle) {
        button.setStyle(baseStyle);
        button.setCursor(Cursor.HAND);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
    }

    private VBox buildStatBadge(int value, String label, String bgColor, String textColor) {
        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: " + textColor + ";");

        Label textLabel = new Label(label);
        textLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: " + textColor + ";");

        VBox badge = new VBox(2, valueLabel, textLabel);
        badge.setPadding(new Insets(8, 12, 8, 12));
        badge.setStyle("-fx-background-color: " + bgColor + "; -fx-border-color: #fed7aa; -fx-border-width: 1;");
        return badge;
    }

    private int getAvailableSpotsCount(VehicleCategory category) {
        return Math.max(0, getTotalSpotsCount(category) - getOccupiedSpotsCount(category));
    }

    private int getOccupiedSpotsCount(VehicleCategory category) {
        int occupied = 0;
        for (Ticket ticket : activeTickets.values()) {
            Vehicle vehiculo = ticket.getVehiculo();
            if (vehiculo != null && VehicleCategory.fromVehiculo(vehiculo) == category) {
                occupied++;
            }
        }
        return occupied;
    }

    private int getTotalSpotsCount(VehicleCategory category) {
        return switch (category) {
            case MOTORCYCLE -> MOTORCYCLE_SPOTS;
            case TRUCK -> TRUCK_SPOTS;
            case CAR -> CAR_SPOTS;
        };
    }

    private Optional<Client.VehicleProfile> promptSelectClientVehicle(List<Client.VehicleProfile> vehicles) {
        return promptSelectClientVehicle(
                vehicles,
                LanguageManager.get("client.vehicles.select.title"),
                LanguageManager.get("client.vehicles.select.prompt")
        );
    }

    private Optional<Client.VehicleProfile> promptSelectClientVehicle(List<Client.VehicleProfile> vehicles, String title, String header) {
        List<String> labels = new ArrayList<>();
        for (Client.VehicleProfile v : vehicles) {
            labels.add(v.getVehiclePlate() + " | " + v.getVehicleBrand() + " " + v.getVehicleModel() + " | " + v.getVehicleColor());
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(labels.get(0), labels);
        dialog.initOwner(stage);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(LanguageManager.get("payment.method"));
        styleDialog(dialog.getDialogPane());

        Optional<String> selected = dialog.showAndWait();
        if (selected.isEmpty()) return Optional.empty();
        int idx = labels.indexOf(selected.get());
        if (idx < 0 || idx >= vehicles.size()) return Optional.empty();
        return Optional.of(vehicles.get(idx));
    }

    private void addVehicleToCurrentClient() {
        if (!(session.getCurrentAccount() instanceof Client cliente)) return;
        if (cliente.getVehicles().size() >= Client.MAX_VEHICLES) {
            showError("error.validation.title", LanguageManager.get("admin.users.vehicle.limit"));
            return;
        }

        Optional<Client.VehicleProfile> profile = promptClientVehicleProfile(cliente.getVehicles().size() + 1);
        if (profile.isEmpty()) return;

        String plate = PlateValidator.normalize(profile.get().getVehiclePlate());
        boolean duplicatePlate = cliente.getVehicles().stream()
                .anyMatch(v -> v.getVehiclePlate().equalsIgnoreCase(plate));
        if (duplicatePlate) {
            showError("error.validation.title", LanguageManager.get("error.plate.duplicate"));
            return;
        }

        boolean added = cliente.addVehicle(
                profile.get().getVehicleType(),
                profile.get().getVehicleBrand(),
                profile.get().getVehicleModel(),
                profile.get().getVehicleColor(),
                profile.get().getVehiclePlate()
        );
        if (!added) {
            showError("error.validation.title", LanguageManager.get("error.vehicle.add"));
            return;
        }

        if (!persistCurrentClientChanges(cliente)) {
            showError("error.state.title", LanguageManager.get("error.state.client.vehicle.save"));
            return;
        }

        selectedClientVehicle = profile.get();
        getClientVehicleCategory().ifPresent(category -> selectedCategory = category);
        refreshMainContent();
        rootLayout.setLeft(createSidebar());
    }

    private void removeVehicleFromCurrentClient() {
        if (!(session.getCurrentAccount() instanceof Client cliente)) return;
        List<Client.VehicleProfile> vehicles = cliente.getVehicles();
        if (vehicles.isEmpty()) {
            showError("error.missing.title", LanguageManager.get("client.vehicles.none.for.category"));
            return;
        }
        if (vehicles.size() == 1) {
            showError("error.validation.title", LanguageManager.get("client.vehicles.remove.last"));
            return;
        }

        Optional<Client.VehicleProfile> selected = promptSelectClientVehicle(
                vehicles,
                LanguageManager.get("client.vehicles.remove.title"),
                LanguageManager.get("client.vehicles.remove.prompt")
        );
        if (selected.isEmpty()) return;

        String selectedPlate = selected.get().getVehiclePlate();
        boolean currentlyParked = activeTickets.values().stream()
                .map(Ticket::getVehiculo)
                .filter(v -> v != null && v.getPlaca() != null)
                .anyMatch(v -> PlateValidator.normalize(v.getPlaca()).equals(PlateValidator.normalize(selectedPlate)));
        if (currentlyParked) {
            showError("error.validation.title", LanguageManager.get("client.vehicles.remove.parked"));
            return;
        }

        List<Client.VehicleProfile> remaining = vehicles.stream()
                .filter(v -> !v.getVehiclePlate().equalsIgnoreCase(selectedPlate))
                .toList();

        List<Client.VehicleProfile> backup = new ArrayList<>(vehicles);
        cliente.clearVehicles();
        for (Client.VehicleProfile vehicle : remaining) {
            cliente.addVehicle(
                    vehicle.getVehicleType(),
                    vehicle.getVehicleBrand(),
                    vehicle.getVehicleModel(),
                    vehicle.getVehicleColor(),
                    vehicle.getVehiclePlate()
            );
        }

        if (!persistCurrentClientChanges(cliente)) {
            cliente.clearVehicles();
            for (Client.VehicleProfile vehicle : backup) {
                cliente.addVehicle(
                        vehicle.getVehicleType(),
                        vehicle.getVehicleBrand(),
                        vehicle.getVehicleModel(),
                        vehicle.getVehicleColor(),
                        vehicle.getVehiclePlate()
                );
            }
            showError("error.state.title", LanguageManager.get("error.state.client.vehicle.save"));
            return;
        }

        if (selectedClientVehicle != null && selectedPlate.equalsIgnoreCase(selectedClientVehicle.getVehiclePlate())) {
            selectedClientVehicle = cliente.getVehicles().get(0);
            getClientVehicleCategory().ifPresent(category -> selectedCategory = category);
        }
        refreshMainContent();
        rootLayout.setLeft(createSidebar());
    }

    private boolean persistCurrentClientChanges(Client cliente) {
        return cuentaRepository.updateAccount(cliente);
    }

    private boolean initializeClientVehicleSelection() {
        if (!(session.getCurrentAccount() instanceof Client cliente)) return true;
        if (cliente.getVehicles().isEmpty()) {
            showError("error.missing.title", LanguageManager.get("client.vehicles.none.for.category"));
            return false;
        }
        Optional<Client.VehicleProfile> selected = promptSelectClientVehicle(
                cliente.getVehicles(),
                LanguageManager.get("client.vehicles.select.title"),
                LanguageManager.get("client.vehicles.select.on.login")
        );
        if (selected.isEmpty()) return false;
        selectedClientVehicle = selected.get();
        Optional<VehicleCategory> category = categoryFromVehicleType(selectedClientVehicle.getVehicleType());
        if (category.isEmpty()) return false;
        selectedCategory = category.get();
        return true;
    }

    private String buildGuestTicketNote(String ticketId) {
        if (ticketId == null || ticketId.isBlank()) return "";
        String note = ticketVehicleNotes.get(ticketId);
        if (note == null || note.isBlank()) return "";
        return note;
    }

    private int parseFolio(String ticketId) {
        if (ticketId == null || ticketId.isBlank()) return 0;
        String clean = ticketId.trim();
        int idx = clean.lastIndexOf('-');
        if (idx >= 0 && idx + 1 < clean.length()) {
            try {
                return Integer.parseInt(clean.substring(idx + 1));
            } catch (NumberFormatException ignored) {
            }
        }
        return Math.abs(clean.hashCode());
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    private Optional<VehicleCategory> getClientVehicleCategory() {
        if (!(session.getCurrentAccount() instanceof Client)) return Optional.empty();
        if (selectedClientVehicle == null) return Optional.empty();
        return categoryFromVehicleType(selectedClientVehicle.getVehicleType());
    }

    private Optional<VehicleCategory> categoryFromVehicleType(String type) {
        if (type == null || type.isBlank()) return Optional.empty();
        String normalized = type.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "CAR", "PROMEDIO" -> Optional.of(VehicleCategory.CAR);
            case "MOTORCYCLE", "MOTO" -> Optional.of(VehicleCategory.MOTORCYCLE);
            case "TRUCK", "GRANDE" -> Optional.of(VehicleCategory.TRUCK);
            default -> Optional.empty();
        };
    }

    private Optional<VehicleCategory> promptClientVehicleType() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("CARRO", List.of("CARRO", "MOTO", "CAMIONETA"));
        dialog.initOwner(stage);
        dialog.setTitle(LanguageManager.get("admin.users.vehicle.type.title"));
        dialog.setHeaderText(LanguageManager.get("admin.users.vehicle.type.prompt"));
        dialog.setContentText(LanguageManager.get("payment.method"));
        styleDialog(dialog.getDialogPane());

        Optional<String> selected = dialog.showAndWait();
        if (selected.isEmpty()) return Optional.empty();

        return switch (selected.get()) {
            case "CARRO" -> Optional.of(VehicleCategory.CAR);
            case "MOTO" -> Optional.of(VehicleCategory.MOTORCYCLE);
            case "CAMIONETA" -> Optional.of(VehicleCategory.TRUCK);
            default -> Optional.empty();
        };
    }

    private Optional<List<Client.VehicleProfile>> promptClientVehicles() {
        List<Client.VehicleProfile> vehicles = new ArrayList<>();

        for (int i = 0; i < Client.MAX_VEHICLES; i++) {
            Optional<Client.VehicleProfile> profile = promptClientVehicleProfile(i + 1);
            if (profile.isEmpty()) {
                if (i == 0) return Optional.empty();
                break;
            }
            vehicles.add(profile.get());

            if (i + 1 >= Client.MAX_VEHICLES) break;
            if (!promptAddAnotherVehicle()) break;
        }

        return Optional.of(vehicles);
    }

    private Optional<Client.VehicleProfile> promptClientVehicleProfile(int index) {
        Optional<VehicleCategory> vehicleType = promptClientVehicleType();
        if (vehicleType.isEmpty()) return Optional.empty();

        Optional<String> brand = promptRequiredText(
                LanguageManager.get("admin.users.vehicle.brand.prompt") + " #" + index,
                LanguageManager.get("error.validation.required")
        );
        if (brand.isEmpty()) return Optional.empty();

        Optional<String> model = promptRequiredText(
                LanguageManager.get("admin.users.vehicle.model.prompt") + " #" + index,
                LanguageManager.get("error.validation.required")
        );
        if (model.isEmpty()) return Optional.empty();

        Optional<String> color = promptRequiredText(
                LanguageManager.get("admin.users.vehicle.color.prompt") + " #" + index,
                LanguageManager.get("error.validation.required")
        );
        if (color.isEmpty()) return Optional.empty();

        String plate;
        while (true) {
            Optional<String> plateText = promptRequiredText(
                    LanguageManager.get("admin.users.vehicle.plate.prompt") + " #" + index,
                    LanguageManager.get("error.validation.required")
            );
            if (plateText.isEmpty()) return Optional.empty();
            plate = PlateValidator.normalize(plateText.get());
            if (!PlateValidator.hasValidLength(plate)) {
                showError("error.validation.title", LanguageManager.get("error.plate.length"));
                continue;
            }
            if (!PlateValidator.hasValidFormat(plate)) {
                showError("error.validation.title", LanguageManager.get("error.plate.format"));
                continue;
            }
            break;
        }

        return Optional.of(new Client.VehicleProfile(
                vehicleType.get().name(),
                brand.get(),
                model.get(),
                color.get(),
                plate
        ));
    }

    private boolean promptAddAnotherVehicle() {
        String no = LanguageManager.get("common.no");
        String yes = LanguageManager.get("common.yes");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(no, List.of(no, yes));
        dialog.initOwner(stage);
        dialog.setTitle(LanguageManager.get("admin.users.vehicle.more.title"));
        dialog.setHeaderText(LanguageManager.get("admin.users.vehicle.more.prompt"));
        dialog.setContentText(LanguageManager.get("payment.method"));
        styleDialog(dialog.getDialogPane());
        Optional<String> result = dialog.showAndWait();
        return result.isPresent() && yes.equalsIgnoreCase(result.get());
    }
}
