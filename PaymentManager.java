import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class PaymentManager {
    public void addPayment(Payment p) {
        String sql = """
            INSERT INTO payments(payment_id,username,accommodation_id,amount,payment_date,payment_method,is_paid)
            VALUES(?,?,?,?,?,?,?)
        """;
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getPaymentId());
            stmt.setString(2, p.getUsername());
            stmt.setInt   (3, p.getAccommodationId());
            stmt.setDouble(4, p.getAmount());
            stmt.setTimestamp(5, Timestamp.valueOf(p.getPaymentDate()));
            stmt.setString(6, p.getPaymentMethod());
            stmt.setBoolean(7, p.isPaid());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean processPayment(String paymentId) {
        String sql = "UPDATE payments SET is_paid = true, payment_date = ? WHERE payment_id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(2, paymentId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Payment> getPaymentsForUser(String username) {
        String sql = "SELECT * FROM payments WHERE username = ?";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment p = new Payment(
                      rs.getString("payment_id"),
                      rs.getString("username"),
                      rs.getInt   ("accommodation_id"),
                      rs.getDouble("amount"),
                      rs.getString("payment_method")
                    );
                    p.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
                    if (rs.getBoolean("is_paid")) p.processPayment();
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
