import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {
    private final String url = "jdbc:sqlite:C:\\Users\\User Andrew\\Desktop\\Курсовий проект код\\src\\my_database.sqlite";

    public DBManager() {
        initDatabase();
    }

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement()) {

            // створюємо таблицю адміністраторів
            String createAdmins = "CREATE TABLE IF NOT EXISTS admins (" +
                    "Admin_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "middle_name VARCHAR(100) NOT NULL," +
                    "first_name VARCHAR(100) NOT NULL," +
                    "last_name VARCHAR(100) NOT NULL," +
                    // Виправлено: BookingAdming → BookingAdmin
                    "role TEXT NOT NULL CHECK(role IN ('RegistrationAdmin', 'BookingAdmin', 'SystemAdmin'))," +
                    "login VARCHAR(100) NOT NULL UNIQUE," +
                    "password VARCHAR(100) NOT NULL" +
                    ");";
            stmt.execute(createAdmins);

            // створюємо таблицю ведучих
            String createHosts = "CREATE TABLE IF NOT EXISTS hosts (" +
                    "Host_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "middle_name VARCHAR(100) NOT NULL," +
                    "first_name VARCHAR(100) NOT NULL," +
                    "last_name VARCHAR(100) NOT NULL," +
                    "nick VARCHAR(100) NOT NULL UNIQUE," +
                    "phone VARCHAR(15) NOT NULL UNIQUE," +
                    "age INTEGER NOT NULL" +
                    ");";
            stmt.execute(createHosts);

            // створюємо таблицю кімнат
            String createRooms = "CREATE TABLE IF NOT EXISTS rooms (" +
                    "room_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name VARCHAR(100) NOT NULL," +
                    "capacity INTEGER NOT NULL" +
                    ");";
            stmt.execute(createRooms);

            // створюємо таблицю бронювань
            String createReservations = "CREATE TABLE IF NOT EXISTS reservations (" +
                    "Booking_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "Host_id INTEGER NOT NULL," +
                    "room_id INTEGER NOT NULL," +
                    "Admin_id INTEGER NOT NULL," +
                    "Booking_date DATE NOT NULL," +
                    "Booking_time TIME NOT NULL," +
                    "FOREIGN KEY (Host_id) REFERENCES hosts(Host_id)," +
                    "FOREIGN KEY (room_id) REFERENCES rooms(room_id)," +
                    "FOREIGN KEY (Admin_id) REFERENCES admins(Admin_id)" +
                    ");";
            stmt.execute(createReservations);

            // перевіряємо чи існує адміністратор з роллю SystemAdmin
            String checkRole = "SELECT COUNT(*) FROM admins WHERE role = 'SystemAdmin';";
            ResultSet rs = stmt.executeQuery(checkRole);
            if (rs.next() && rs.getInt(1) == 0) {
                String insertAdmin = "INSERT INTO admins (middle_name, first_name, last_name, role, login, password) " +
                        "VALUES ('Agapitov', 'Andrey', 'Ivanovich', 'SystemAdmin', 'admin', 'admin123');";
                stmt.executeUpdate(insertAdmin);
            }

            //Додавання таблиці кімнат
            String insertRooms = "INSERT INTO rooms (name, capacity) VALUES ('Room 1', 2), ('Room 2', 4), ('Room 3', 6), ('Room 4', 8), ('Room 5', 10);";
            stmt.executeUpdate(insertRooms);

        } catch (SQLException e) {
            System.out.println("Помилка ініціалізації БД: " + e.getMessage());
        }
    }

    
    //JOIN з сортуванням Список ведучих, які забронювали кімнати, відсортований за датою:

    public void getHostsWithBookingsSortedByDate() {
        String query = "SELECT h.first_name, h.last_name, r.name, r.Booking_date " +
                    "FROM hosts h " +
                    "JOIN reservations r ON h.Host_id = r.Host_id " +
                    "JOIN rooms rm ON r.room_id = rm.room_id " +
                    "ORDER BY r.Booking_date;";
        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String roomName = rs.getString("name");
                String bookingDate = rs.getString("Booking_date");
                System.out.println("Host: " + firstName + " " + lastName + ", Room: " + roomName + ", Date: " + bookingDate);
            }
        } catch (SQLException e) {
            System.out.println("Помилка отримання даних: " + e.getMessage());
        }
    }

    //LIKE Знайти всіх ведучих, чиє прізвище починається на "A":
    public void findHostsByLastNameStartingWithA() {
        String query = "SELECT * FROM hosts WHERE last_name LIKE 'A%';";
        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int hostId = rs.getInt("Host_id");
                String middleName = rs.getString("middle_name");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String nick = rs.getString("nick");
                String phone = rs.getString("phone");
                int age = rs.getInt("age");
                System.out.println("Host ID: " + hostId + ", Name: " + middleName + " " + firstName + " " + lastName + ", Nick: " + nick + ", Phone: " + phone + ", Age: " + age);
            }
        } catch (SQLException e) {
            System.out.println("Помилка отримання даних: " + e.getMessage());
        }
    }

//BETWEEN Список бронювань у заданий період
public void getBookingsInPeriod(String startDate, String endDate) {
    String query = "SELECT * FROM reservations WHERE Booking_date BETWEEN ? AND ?;";
    try (Connection conn = DriverManager.getConnection(url);
        PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, startDate);
        pstmt.setString(2, endDate);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            int bookingId = rs.getInt("Booking_id");
            int hostId = rs.getInt("Host_id");
            int roomId = rs.getInt("room_id");
            int adminId = rs.getInt("Admin_id");
            String bookingDate = rs.getString("Booking_date");
            String bookingTime = rs.getString("Booking_time");
            System.out.println("Booking ID: " + bookingId + ", Host ID: " + hostId + ", Room ID: " + roomId + ", Admin ID: " + adminId + ", Date: " + bookingDate + ", Time: " + bookingTime);
        }
    } catch (SQLException e) {
        System.out.println("Помилка отримання даних: " + e.getMessage());
    }
}

    //Агрегатна функція без угруповання Кількість бронювань за останній тиждень
    public void countBookingsLastWeek() {
        String query = "SELECT COUNT(*) AS booking_count FROM reservations " +
                    "WHERE Booking_date >= date('now', '-7 days');";
        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int bookingCount = rs.getInt("booking_count");
                System.out.println("Кількість бронювань за останній тиждень: " + bookingCount);
            }
        } catch (SQLException e) {
            System.out.println("Помилка отримання даних: " + e.getMessage());
        }
    }

    //Агрегатна функція з угрупованням Кількість бронювань, зроблених кожним ведучим
    public void countBookingsByHost() {
        String query = "SELECT h.first_name, h.last_name, COUNT(*) AS booking_count " +
                    "FROM hosts h " +
                    "JOIN reservations res ON h.Host_id = res.Host_id " +
                    "GROUP BY h.Host_id;";
        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int bookingCount = rs.getInt("booking_count");
                System.out.println("Host: " + firstName + " " + lastName + ", Booking Count: " + bookingCount);
            }
        } catch (SQLException e) {
            System.out.println("Помилка отримання даних: " + e.getMessage());
        }
    }

    //ALL / ANY Хто з ведучих зробив найбільше бронювань
    public void hostWithMostBookings() {
    String query = "SELECT h.first_name, h.last_name, COUNT(res.Booking_id) AS booking_count " +
                "FROM hosts h " +
                "JOIN reservations res ON h.Host_id = res.Host_id " +
                "GROUP BY h.Host_id " +
                "ORDER BY booking_count DESC " +
                "LIMIT 1;";

    try (Connection conn = DriverManager.getConnection(url);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int bookingCount = rs.getInt("booking_count");
                System.out.println("Host with most bookings: " + firstName + " " + lastName + ", Booking Count: " + bookingCount);
            }
        } catch (SQLException e) {
            System.out.println("Помилка виконання запиту: " + e.getMessage());
        }
    }

    //Корельований підзапит Для кожної кімнати — ведучий з найбільшою кількістю бронювань
    public void hostWithMostBookingsPerRoom() { 
    String query = "SELECT r.name, h.first_name, h.last_name " +
                "FROM rooms r " +
                "JOIN reservations res ON r.room_id = res.room_id " +
                "JOIN hosts h ON res.Host_id = h.Host_id " +
                "WHERE res.Host_id = (SELECT Host_id FROM reservations res2 " +
                "WHERE res2.room_id = r.room_id " +
                "GROUP BY res2.Host_id " +
                "ORDER BY COUNT(*) DESC LIMIT 1);";
    try (Connection conn = DriverManager.getConnection(url);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            String roomName = rs.getString("name");
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            System.out.println("Room: " + roomName + ", Host: " + firstName + " " + lastName);
        }
    } catch (SQLException e) {
        System.out.println("Помилка отримання даних: " + e.getMessage());
    }
}

    //Заперечення LEFT JOIN
    public void hostsWithoutBookings() {
    String query = "SELECT h.first_name, h.last_name " +
                "FROM hosts h " +
                "LEFT JOIN reservations res ON h.Host_id = res.Host_id " +
                "WHERE res.Host_id IS NULL;";
    try (Connection conn = DriverManager.getConnection(url);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            System.out.println("Ведучий без бронювань (LEFT JOIN): " + firstName + " " + lastName);
        }
    } catch (SQLException e) {
        System.out.println("Помилка отримання даних: " + e.getMessage());
    }
}

    //Заперечення IN
    public void hostsNotInReservations() {
    String query = "SELECT first_name, last_name FROM hosts " +
                "WHERE Host_id NOT IN (SELECT Host_id FROM reservations);";
    try (Connection conn = DriverManager.getConnection(url);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            System.out.println("Ведучий без бронювань (IN): " + firstName + " " + lastName);
        }
    } catch (SQLException e) {
        System.out.println("Помилка отримання даних: " + e.getMessage());
    }
}

    //Заперечення EXISTS
    public void hostsWithoutBookingsExists() {
        String query = "SELECT first_name, last_name FROM hosts h " +
                    "WHERE NOT EXISTS (SELECT 1 FROM reservations res WHERE res.Host_id = h.Host_id);";
        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                System.out.println("Ведучий без бронювань (EXISTS): " + firstName + " " + lastName);
            }
        } catch (SQLException e) {
            System.out.println("Помилка отримання даних: " + e.getMessage());
        }
    }

    //UNION з коментарем Список ведучих з коментарем
    public void hostsWithComments() {
        String query = "SELECT first_name, last_name, 'Ведучий' AS comment FROM hosts " +
                "UNION " +
                "SELECT first_name, last_name, 'Адміністратор' AS comment FROM admins;";
        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String comment = rs.getString("comment");
                System.out.println("Host: " + firstName + " " + lastName + ", Comment: " + comment);
            }
        } catch (SQLException e) {
            System.out.println("Помилка отримання даних: " + e.getMessage());
        }
    }
}