import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ScheduleManager {
    private final String url = "jdbc:sqlite:C:\\Users\\User Andrew\\Desktop\\Курсовий проект код\\src\\my_database.sqlite";

    // isRoomAvailable, addReservation - ревізія, без змін
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

    public void addReservation(int hostId, int roomId, int adminId, String date, String time) throws SQLException {
        BookingRepository repo = new BookingRepository();
        if (isRoomAvailable(roomId, date, time)) {
            repo.saveBooking(hostId, roomId, adminId, date, time);
        } else {
            throw new SQLException("Room is not available at the selected time.");
        }
    }
}