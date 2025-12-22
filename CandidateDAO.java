package ph.edu.unc.pageantms.dao;

import ph.edu.unc.pageantms.model.CandidateModel;
import ph.edu.unc.pageantms.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CandidateDAO {

    public ObservableList<CandidateModel> getAllCandidates() {
        ObservableList<CandidateModel> list = FXCollections.observableArrayList();

        String sql = "SELECT Candidate_ID, Represents, Candidate_No FROM Candidate";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                CandidateModel candidate = new CandidateModel(
                        rs.getString("Candidate_ID"),
                        rs.getString("Represents"),
                        rs.getInt("Candidate_No")
                );
                list.add(candidate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}