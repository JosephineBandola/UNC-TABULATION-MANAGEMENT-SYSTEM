package ph.edu.unc.pageantms.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ph.edu.unc.pageantms.model.User;
import ph.edu.unc.pageantms.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private StackPane contentArea;

    // --- SIDEBAR ELEMENTS ---
    @FXML private Label lblParticipant;
    @FXML private Button btnApplicants;
    @FXML private Button btnCandidates;

    @FXML private Label lblContest;
    @FXML private Button btnRounds;
    @FXML private Button btnCriteria;
    @FXML private Button btnJudges;

    // ROUND DETAILS
    @FXML private Label lblRoundDetails;
    @FXML private Button btnSegments;
    @FXML private Button btnAssignCriteria;
    @FXML private Button btnAssignJudge;

    // SCORING
    @FXML private Label lblScoring;
    @FXML private Button btnJudgeScores;
    @FXML private Button btnResults;
    @FXML private Button btnScoreEntry;

    private User currentUser;
    private Map<String, String> segmentMap = new HashMap<>();
    private Map<String, String> judgeMap = new HashMap<>();

    public void initData(User user) {
        this.currentUser = user;
        if (user != null) {
            welcomeLabel.setText("Dashboard - " + user.getRole());
            configureAccess();
        }
    }

    private void configureAccess() {
        String role = currentUser.getRole();

        // 1. DEFAULT: Hide Everything
        setGroupVisible(false, lblParticipant, btnApplicants, btnCandidates);
        setGroupVisible(false, lblContest, btnRounds, btnCriteria, btnJudges);
        setGroupVisible(false, lblRoundDetails, btnSegments, btnAssignCriteria, btnAssignJudge);
        setGroupVisible(false, lblScoring, btnJudgeScores, btnResults);
        btnScoreEntry.setVisible(false); btnScoreEntry.setManaged(false);

        // 2. CONFIGURE BASED ON ROLE
        if (role.equalsIgnoreCase("Organizer")) {
            setGroupVisible(true, lblParticipant, btnApplicants, btnCandidates);
            setGroupVisible(true, lblContest, btnRounds, btnCriteria, btnJudges);
            setGroupVisible(true, lblRoundDetails, btnSegments, btnAssignCriteria, btnAssignJudge);
            setGroupVisible(true, lblScoring, btnResults); // Admin sees Results, not Scoresheets

        } else if (role.equalsIgnoreCase("Head Tabulator")) {
            setGroupVisible(true, lblParticipant, btnApplicants, btnCandidates);
            setGroupVisible(true, lblContest, btnRounds); // Only Rounds
            setGroupVisible(true, lblRoundDetails, btnSegments, btnAssignCriteria, btnAssignJudge);
            setGroupVisible(true, lblScoring, btnJudgeScores, btnResults);

        } else if (role.equalsIgnoreCase("Tabulator")) {
            setGroupVisible(true, lblParticipant, btnApplicants, btnCandidates);
            setGroupVisible(true, lblContest, btnRounds);
            setGroupVisible(true, lblRoundDetails, btnSegments, btnAssignCriteria, btnAssignJudge);
            setGroupVisible(true, lblScoring, btnJudgeScores, btnResults);

        } else if (role.equalsIgnoreCase("Judge")) {
            btnScoreEntry.setVisible(true); btnScoreEntry.setManaged(true);
            showScoreEntry();
        }
    }

    private void setGroupVisible(boolean visible, Label lbl, Button... btns) {
        lbl.setVisible(visible); lbl.setManaged(visible);
        for(Button b : btns) { b.setVisible(visible); b.setManaged(visible); }
    }

    // --- NAVIGATION ACTIONS ---
    @FXML private void showApplicants() { loadPlaceholder("Applicants List"); }
    @FXML private void showCandidates() { loadPlaceholder("Official Candidates"); }
    @FXML private void showRounds() { loadPlaceholder("Rounds Management"); }
    @FXML private void showCriteria() { loadPlaceholder("Criteria Library"); }
    @FXML private void showJudges() { loadPlaceholder("List of Judges"); }
    @FXML private void showSegments() { loadPlaceholder("Segments per Round"); }
    @FXML private void showAssignCriteria() { loadPlaceholder("Criterions per Segment"); }
    @FXML private void showAssignJudge() { loadPlaceholder("Judges per Round"); }
    @FXML private void showScoreEntry() { loadPlaceholder("Judge Score Entry"); }

    // =========================================================================
    // VIEW: OFFICIAL RESULTS (GOLD THEME)
    // =========================================================================
    @FXML
    private void showResults() {
        contentArea.getChildren().clear();

        // FIXED: Title is now GOLD (#927400), not Green
        Label title = new Label("Official Results & Ranking");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #927400;");

        TableView<Map<String, String>> table = new TableView<>();
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Map<String, String>, String> colRank = new TableColumn<>("Rank");
        colRank.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("rank")));

        TableColumn<Map<String, String>, String> colName = new TableColumn<>("Candidate");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("name")));
        colName.setPrefWidth(200);

        TableColumn<Map<String, String>, String> colScore = new TableColumn<>("Total Score");
        colScore.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("total")));

        table.getColumns().addAll(colRank, colName, colScore);
        loadResultsData(table);

        VBox layout = new VBox(15, title, new Label("Consolidated scores from all judges."), table);
        layout.setPadding(new Insets(20));
        contentArea.getChildren().add(layout);
    }

    private void loadResultsData(TableView<Map<String, String>> table) {
        ObservableList<Map<String, String>> rows = FXCollections.observableArrayList();
        String sql = "SELECT c.Represents, c.Candidate_No, SUM(s.Raw_Score) as FinalScore FROM Candidate c LEFT JOIN Score s ON c.Candidate_ID = s.Candidate_ID GROUP BY c.Candidate_ID ORDER BY FinalScore DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            int rank = 1;
            while(rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("rank", String.valueOf(rank++));
                row.put("name", "#" + rs.getString("Candidate_No") + " " + rs.getString("Represents"));
                double score = rs.getDouble("FinalScore");
                row.put("total", String.format("%.2f", score));
                rows.add(row);
            }
            table.setItems(rows);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // =========================================================================
    // VIEW: ASSIGNED JUDGE SCORES (DARK + GOLD THEME)
    // =========================================================================
    @FXML
    private void showJudgeScoreAnalysis() {
        contentArea.getChildren().clear();

        // FIXED: Title is now GOLD (#927400), not Blue
        Label title = new Label("Assigned Judge Scoring Record");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #927400;");

        ComboBox<String> cmbSegments = new ComboBox<>();
        cmbSegments.setPromptText("Select Segment");
        cmbSegments.setPrefWidth(200);

        ComboBox<String> cmbJudges = new ComboBox<>();
        cmbJudges.setPromptText("Select Assigned Judge");
        cmbJudges.setPrefWidth(200);
        cmbJudges.setDisable(true);

        // FIXED: Button is now GOLD (#927400), not Maroon
        Button btnLoad = new Button("View Scores");
        btnLoad.setStyle("-fx-background-color: #927400; -fx-text-fill: white; -fx-font-weight: bold;");

        TableView<Map<String, String>> table = new TableView<>();
        VBox.setVgrow(table, Priority.ALWAYS);

        loadSegments(cmbSegments);

        cmbSegments.setOnAction(e -> {
            String segName = cmbSegments.getValue();
            if (segName != null) {
                String segId = segmentMap.get(segName);
                loadJudgesForSegment(segId, cmbJudges);
                cmbJudges.setDisable(false);
            }
        });

        btnLoad.setOnAction(e -> {
            String segName = cmbSegments.getValue();
            String judgeName = cmbJudges.getValue();
            if (segName != null && judgeName != null) {
                String segId = segmentMap.get(segName);
                String assignedJudgeId = judgeMap.get(judgeName);
                buildScoreTable(table, segId, assignedJudgeId);
            }
        });

        // FIXED: Background is now DARK (#222222) with GOLD Border, not White
        Label lblSeg = new Label("Segment:"); lblSeg.setStyle("-fx-text-fill: #EBEBEB;");
        Label lblJudge = new Label("Judge:"); lblJudge.setStyle("-fx-text-fill: #EBEBEB;");

        HBox filters = new HBox(10, lblSeg, cmbSegments, lblJudge, cmbJudges, btnLoad);
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setStyle("-fx-background-color: #222222; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #927400; -fx-border-radius: 5;");

        VBox layout = new VBox(15, title, filters, table);
        layout.setPadding(new Insets(10));
        contentArea.getChildren().add(layout);
    }

    // --- DB HELPERS ---
    private void loadSegments(ComboBox<String> cmb) {
        String sql = "SELECT Segment_Name, Segment_ID FROM Segment";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            ObservableList<String> list = FXCollections.observableArrayList();
            while(rs.next()) {
                segmentMap.put(rs.getString("Segment_Name"), rs.getString("Segment_ID"));
                list.add(rs.getString("Segment_Name"));
            }
            cmb.setItems(list);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadJudgesForSegment(String segmentId, ComboBox<String> cmb) {
        String sql = "SELECT j.Judge_ID, j.Last_Name, aj.AssignedJudge_ID FROM Segment s JOIN Round r ON s.Round_ID = r.Round_ID JOIN AssignedJudge aj ON r.Round_ID = aj.Round_ID JOIN Judge j ON aj.Judge_ID = j.Judge_ID WHERE s.Segment_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, segmentId);
            ResultSet rs = stmt.executeQuery();
            ObservableList<String> list = FXCollections.observableArrayList();
            judgeMap.clear();
            while(rs.next()) {
                String name = rs.getString("Last_Name") + " (" + rs.getString("Judge_ID") + ")";
                judgeMap.put(name, rs.getString("AssignedJudge_ID"));
                list.add(name);
            }
            cmb.setItems(list);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void buildScoreTable(TableView<Map<String, String>> table, String segmentId, String assignedJudgeId) {
        table.getColumns().clear();
        table.getItems().clear();

        TableColumn<Map<String, String>, String> colName = new TableColumn<>("Candidate");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("name")));
        table.getColumns().add(colName);

        List<String> criteriaIds = new ArrayList<>();
        String critSql = "SELECT ac.AssignedCriteria_ID, c.Criteria_Name FROM AssignedCriteria ac JOIN Criteria c ON ac.Criteria_ID = c.Criteria_ID WHERE ac.Segment_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(critSql)) {
            stmt.setString(1, segmentId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                String cName = rs.getString("Criteria_Name");
                String cId = rs.getString("AssignedCriteria_ID");
                criteriaIds.add(cId);
                TableColumn<Map<String, String>, String> colCrit = new TableColumn<>(cName);
                colCrit.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOrDefault(cId, "0")));
                table.getColumns().add(colCrit);
            }
        } catch (SQLException e) { e.printStackTrace(); }

        TableColumn<Map<String, String>, String> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("total")));
        table.getColumns().add(colTotal);

        loadTableData(table, assignedJudgeId, criteriaIds);
    }

    private void loadTableData(TableView<Map<String, String>> table, String assignedJudgeId, List<String> critIds) {
        ObservableList<Map<String, String>> rows = FXCollections.observableArrayList();
        String sql = "SELECT c.Represents, c.Candidate_ID, sc.AssignedCriteria_ID, sc.Raw_Score FROM Candidate c LEFT JOIN Score sc ON c.Candidate_ID = sc.Candidate_ID AND sc.AssignedJudge_ID = ? ORDER BY c.Candidate_No";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, assignedJudgeId);
            ResultSet rs = stmt.executeQuery();
            Map<String, Map<String, String>> dataMap = new LinkedHashMap<>();
            while(rs.next()) {
                String cId = rs.getString("Candidate_ID");
                dataMap.putIfAbsent(cId, new HashMap<>());
                Map<String, String> row = dataMap.get(cId);
                row.put("name", rs.getString("Represents"));
                String critId = rs.getString("AssignedCriteria_ID");
                if(critId != null) row.put(critId, rs.getString("Raw_Score"));
            }
            for(Map<String, String> row : dataMap.values()) {
                double total = 0;
                for(String cid : critIds) try { total += Double.parseDouble(row.getOrDefault(cid, "0")); } catch(Exception e){}
                row.put("total", String.format("%.2f", total));
            }
            rows.addAll(dataMap.values());
            table.setItems(rows);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadPlaceholder(String title) {
        contentArea.getChildren().clear();
        // FIXED: Placeholder Title is now Gold
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 24px; -fx-text-fill: #927400; -fx-font-weight: bold;");
        contentArea.getChildren().add(t);
    }

    @FXML private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }
}