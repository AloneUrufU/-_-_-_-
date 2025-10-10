import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    private final String url = "jdbc:sqlite:C:\\Users\\User Andrew\\Desktop\\Курсовий проект код\\src\\my_database.sqlite";

    // isNicknameUnique - ревізія, без змін
    public boolean isNicknameUnique(String nick) throws SQLException {
        String sql = "SELECT COUNT(*) FROM hosts WHERE nick = ?";
        try (Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nick);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) == 0;
        }
    }
}