import java.sql.*;
import java.util.*;

public class RestaurantManager {

    // 1) Add a restaurant
    public void addRestaurant(Restaurant r) {
        String sql = "INSERT INTO restaurants(name,location,cuisine) VALUES(?,?,?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, r.getName());
            stmt.setString(2, r.getLocation());
            stmt.setString(3, r.getCuisine());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2) Delete by name
    public boolean deleteRestaurant(String name) {
        String sql = "DELETE FROM restaurants WHERE name = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3) Internal fetch by cuisine
    private List<Restaurant> getByCuisine(String cuisine) {
        String sql = "SELECT * FROM restaurants WHERE cuisine = ?";
        List<Restaurant> list = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cuisine);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Restaurant(
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getString("cuisine")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // === Method your main calls ===

    /** Print the filtered list */
    public void displayByCuisine(String cuisine) {
        List<Restaurant> filtered = getByCuisine(cuisine);
        if (filtered.isEmpty()) {
            System.out.println("No restaurants found with " + cuisine + " cuisine.");
        } else {
            System.out.println("\n--- " + cuisine + " Restaurants ---");
            filtered.forEach(System.out::println);
        }
    }
}
