package ph.edu.unc.pageantms.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import ph.edu.unc.pageantms.model.User;
import ph.edu.unc.pageantms.util.DatabaseConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class JudgeDashboardController {

    @FXML private Label lblJudgeName;
    @FXML private ComboBox<String> cmbSegments;
    @FXML private TableView<Map<String, String>> tblScoring;
    @FXML private TableColumn<Map<String, String>, String> colCandidate;

    // Removed Results/Ranking Tables Variables

    private User currentUser;
    private String currentJudgeID;
    private Map<String, String> segmentMap = new HashMap<>();

    public void initData(User user) {
        this.currentUser = user;
        // In a real app, you would query the Judge table to get the Name using the UserID
        this.currentJudgeID = user.getUsername();
        lblJudgeName.setText("Judge ID: " + currentJudgeID);

        loadSegments();
    }

    private void loadSegments() {
        // Fetch all available segments (or filter by Round assigned to Judge)
        String sql = "SELECT Segment_Name, Segment_ID FROM Segment";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            ObservableList<String> segmentNames = FXCollections.observableArrayList();
            while (rs.next()) {
                String name = rs.getString("Segment_Name");
                String id = rs.getString("Segment_ID");
                segmentMap.put(name, id);
                segmentNames.add(name);
            }
            cmbSegments.setItems(segmentNames);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void loadScoringData() {
        String selectedSegmentName = cmbSegments.getValue();
        if (selectedSegmentName == null) {
            showAlert("Selection Error", "Please select a segment first.");
            return;
        }
        String segmentId = segmentMap.get(selectedSegmentName);

        setupDynamicColumns(segmentId);
        loadCandidates(segmentId);
        // loadResults() removed entirely
    }

    // 1. DYNAMIC COLUMNS: Creates columns based on Criteria
    private void setupDynamicColumns(String segmentId) {
        tblScoring.getColumns().clear();
        tblScoring.getColumns().add(colCandidate); // Add fixed Candidate column back

        // Setup Candidate Column
        colCandidate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("name")));

        String sql = "SELECT c.Criteria_Name, ac.Max_Threshold, ac.AssignedCriteria_ID " +
                "FROM AssignedCriteria ac " +
                "JOIN Criteria c ON ac.Criteria_ID = c.Criteria_ID " +
                "WHERE ac.Segment_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, segmentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String criteriaName = rs.getString("Criteria_Name");
                String maxScore = rs.getString("Max_Threshold");
                String criteriaId = rs.getString("AssignedCriteria_ID");
                String colKey = "score_" + criteriaId; // Unique Key for the map

                // Create a Column for this Criteria
                TableColumn<Map<String, String>, String> col = new TableColumn<>(criteriaName + "\n(Max " + maxScore + ")");
                col.setPrefWidth(120);

                // Allow Editing
                col.setCellFactory(TextFieldTableCell.forTableColumn());
                col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOrDefault(colKey, "0")));

                // Save Logic: Update the Map when user hits Enter
                col.setOnEditCommit(event -> {
                    String newVal = event.getNewValue();
                    // Basic Validation: Check if number
                    if (newVal.matches("\\d+(\\.\\d+)?")) {
                        event.getRowValue().put(colKey, newVal);
                    } else {
                        showAlert("Invalid Input", "Please enter a valid number.");
                        tblScoring.refresh(); // Revert visual change
                    }
                });

                tblScoring.getColumns().add(col);
            }
        } catch (SQLException e) { e.printStackTrace(); }

        tblScoring.setEditable(true);
    }

    // 2. LOAD ROWS: Candidates
    private void loadCandidates(String segmentId) {
        ObservableList<Map<String, String>> rows = FXCollections.observableArrayList();

        // Fetch candidates linked to the Contest of this Segment
        // (Simplified join for demo purposes)
        String sql = "SELECT c.Candidate_ID, c.Represents, c.Candidate_No FROM Candidate c ORDER BY c.Candidate_No ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("id", rs.getString("Candidate_ID"));
                row.put("name", "#" + rs.getInt("Candidate_No") + " - " + rs.getString("Represents"));
                rows.add(row);
            }
            tblScoring.setItems(rows);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleSubmitScores() {
        // Logic to iterate through table items and INSERT into Score table would go here
        // For now, just a confirmation
        if (tblScoring.getItems().isEmpty()) {
            showAlert("No Data", "No candidates to score.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Submission");
        alert.setHeaderText("Submit Scores?");
        alert.setContentText("Are you sure you want to submit these scores? This cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Save to DB code here...
                showAlert("Success", "Scores have been submitted to the Tabulation system.");
            }
        });
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblJudgeName.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}