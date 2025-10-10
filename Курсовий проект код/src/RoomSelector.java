import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomSelector {
    private final String url = "jdbc:sqlite:C:\\Users\\User Andrew\\Desktop\\Курсовий проект код\\src\\my_database.sqlite";

    // getAvailableRooms, getRoomIdByName - ревізія, без змін
    public List<String> getAvailableRooms(String date, String time) throws SQLException {
        List<String> rooms = new ArrayList<>();
        String sql = "SELECT name FROM rooms WHERE room_id NOT IN (" +
                    "SELECT room_id FROM reservations WHERE Booking_date = ? AND Booking_time = ?)";
        try (Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, date);
            stmt.setString(2, time);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rooms.add(rs.getString("name"));
            }
        }
        return rooms;
    }

    public int getRoomIdByName(String name) throws SQLException {
        String sql = "SELECT room_id FROM rooms WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("room_id");
            } else {
                throw new SQLException("Room not found");
            }
        }
    }
}