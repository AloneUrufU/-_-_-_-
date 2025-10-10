import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HostRegistry {
    private final String url = "jdbc:sqlite:C:\\Users\\User Andrew\\Desktop\\Курсовий проект код\\src\\my_database.sqlite";

    // getAllHosts - ревізія, без змін
    public List<String> getAllHosts() throws SQLException {
        List<String> hosts = new ArrayList<>();
        String sql = "SELECT nick FROM hosts";
        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                hosts.add(rs.getString("nick"));
            }
        }
        return hosts;
    }

    // getHostIdByNick - ревізія, без змін
    public int getHostIdByNick(String nick) throws SQLException {
        String sql = "SELECT Host_id FROM hosts WHERE nick = ?";
        try (Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nick);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Host_id");
            } else {
                throw new SQLException("Host not found");
            }
        }
    }
}