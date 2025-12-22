package ph.edu.unc.pageantms.dao;

import ph.edu.unc.pageantms.model.User;
import ph.edu.unc.pageantms.util.DatabaseConnection;
import java.sql.*;

public class UserDAO {

    public User validateLogin(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {

            if (conn == null) return null; // Stop if DB connection failed

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}