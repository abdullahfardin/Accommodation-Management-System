import java.sql.*;
import java.util.*;

public class AccommodationManager {

    // 1) Add a new property, auto‑assigning its ID
    public void addAccommodation(Accommodation acc) {
        String sql = """
            INSERT INTO accommodations(location,price,available,admin_username,admin_phone)
            VALUES(?,?,?,?,?)
        """;
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, acc.getLocation());
            stmt.setDouble(2, acc.getPrice());
            stmt.setBoolean(3, acc.isAvailable());
            stmt.setString(4, acc.getAdminUsername());
            stmt.setString(5, acc.getAdminPhone());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    acc.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2) Fetch every accommodation record
    public List<Accommodation> getAll() {
        String sql = "SELECT * FROM accommodations";
        List<Accommodation> list = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Accommodation a = new Accommodation(
                    rs.getInt("id"),
                    rs.getString("location"),
                    rs.getDouble("price"),
                    rs.getBoolean("available"),
                    rs.getString("admin_username"),
                    rs.getString("admin_phone")
                );
                String bookedBy = rs.getString("booked_by");
                if (bookedBy != null) a.book(bookedBy);
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3) Book if still available
    public boolean bookById(int id, String username) {
        String sql = """
            UPDATE accommodations
            SET available = false, booked_by = ?
            WHERE id = ? AND available = true
        """;
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setInt(2, id);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4) Delete a property owned by this admin
    public boolean deleteProperty(int id, String adminUsername) {
        String sql = "DELETE FROM accommodations WHERE id = ? AND admin_username = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setString(2, adminUsername);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5) Internal helper to get just this admin’s list
    private List<Accommodation> getByAdmin(String adminUsername) {
        String sql = "SELECT * FROM accommodations WHERE admin_username = ?";
        List<Accommodation> list = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, adminUsername);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Accommodation a = new Accommodation(
                        rs.getInt("id"),
                        rs.getString("location"),
                        rs.getDouble("price"),
                        rs.getBoolean("available"),
                        rs.getString("admin_username"),
                        rs.getString("admin_phone")
                    );
                    String bookedBy = rs.getString("booked_by");
                    if (bookedBy != null) a.book(bookedBy);
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // === Methods your main class expects ===

    /** Called in Admin menu and booking report */
    public List<Accommodation> getPropertiesByAdmin(String adminUsername) {
        return getByAdmin(adminUsername);
    }

    /** Called in guest view and elsewhere to print all */
    public void displayAll() {
        List<Accommodation> all = getAll();
        if (all.isEmpty()) {
            System.out.println("No accommodations available.");
        } else {
            System.out.println("\n--- All Properties ---");
            all.forEach(System.out::println);
        }
    }

    /** For constructing a new ID in file‑less world */
    public int getNextId() {
        String sql = "SELECT IFNULL(MAX(id),0) + 1 AS nextId FROM accommodations";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("nextId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
