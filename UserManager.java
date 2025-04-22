
import java.sql.*;

public class UserManager {
    public boolean authenticate(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return false;
                String storedHash = rs.getString("password");
                return PasswordUtils.verifyPassword(password, storedHash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addUser(User newUser) {
        String sql = "INSERT INTO users(username,password,email,role) VALUES(?,?,?,?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newUser.getUsername());
            stmt.setString(2, newUser.getPassword()); // already hashed
            stmt.setString(3, newUser.getEmail());
            stmt.setString(4, newUser.getRole());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean resetPassword(String username, String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ? AND email = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, PasswordUtils.hashPassword(newPassword));
            stmt.setString(2, username);
            stmt.setString(3, email);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserRole(String username) {
        String sql = "SELECT role FROM users WHERE username = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("role") : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
