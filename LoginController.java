package ph.edu.unc.pageantms.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ph.edu.unc.pageantms.dao.UserDAO;
import ph.edu.unc.pageantms.model.User;
import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        User loggedInUser = userDAO.validateLogin(user, pass);

        if (loggedInUser != null) {
            loadDashboard(loggedInUser);
        } else {
            errorLabel.setText("Invalid Login Credentials.");
        }
    }

    private void loadDashboard(User user) {
        try {
            String fxmlFile = "/view/Dashboard.fxml"; // Default for Admin/Tabulator

            // ROUTING LOGIC
            if (user.getRole().equalsIgnoreCase("Judge")) {
                fxmlFile = "/view/JudgeDashboard.fxml"; // New Judge Screen
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Pass User Data
            if (user.getRole().equalsIgnoreCase("Judge")) {
                JudgeDashboardController controller = loader.getController();
                controller.initData(user);
            } else {
                DashboardController controller = loader.getController();
                controller.initData(user);
            }

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.setTitle("Pageant Management System - " + user.getRole());

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error loading dashboard. Check Console.");
        }
    }
}