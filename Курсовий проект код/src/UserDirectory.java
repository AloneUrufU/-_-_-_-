import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserDirectory {
    private final String url = "jdbc:sqlite:C:\\Users\\User Andrew\\Desktop\\Курсовий проект код\\src\\my_database.sqlite";

    // saveUser, getAllUsers - ревізія, без змін
    public void saveUser(Map<String, String> userData) throws SQLException {
        String sql = "INSERT INTO hosts (first_name, last_name, middle_name, nick, phone, age) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userData.get("first_name"));
            stmt.setString(2, userData.get("last_name"));
            stmt.setString(3, userData.getOrDefault("middle_name", ""));
            stmt.setString(4, userData.get("nick"));
            stmt.setString(5, userData.get("phone"));
            stmt.setInt(6, Integer.parseInt(userData.get("age")));
            stmt.executeUpdate();
        }
    }

    public List<String> getAllUsers() throws SQLException {
        List<String> users = new ArrayList<>();
        String sql = "SELECT first_name, last_name FROM hosts";
        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(rs.getString("first_name") + " " + rs.getString("last_name"));
            }
        }
        return users;
    }
}