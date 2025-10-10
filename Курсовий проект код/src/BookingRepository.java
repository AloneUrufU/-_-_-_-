import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookingRepository {
    private final String url = "jdbc:sqlite:C:\\Users\\User Andrew\\Desktop\\Курсовий проект код\\src\\my_database.sqlite";

    // saveBooking - ревізія, без змін
    public void saveBooking(int hostId, int roomId, int adminId, String date, String time) throws SQLException {
        String sql = "INSERT INTO reservations (Host_id, room_id, Admin_id, Booking_date, Booking_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hostId);
            stmt.setInt(2, roomId);
            stmt.setInt(3, adminId);
            stmt.setString(4, date);
            stmt.setString(5, time);
            stmt.executeUpdate();
        }
    }

    // isRoomAvailable - ревізія, без змін
    public boolean isRoomAvailable(int roomId, String date, String time) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations WHERE room_id = ? AND Booking_date = ? AND Booking_time = ?";
        try (Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setString(2, date);
            stmt.setString(3, time);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) == 0;
        }
    }
}