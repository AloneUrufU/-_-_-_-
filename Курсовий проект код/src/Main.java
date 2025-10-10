import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final String url = "jdbc:sqlite:D:\\ІКС\\Курсовий проект код\\src\\my_database.sqlite";

    public static void main(String[] args) {
        DBManager dbManager = new DBManager();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Вітаємо в ПП 'All For D&D'");
        while (true) {
            loginProcess(scanner);
        }
    }

    // Метод для входу в акаунт (Рефакторинг: видалено дублювання, приведено ролі до єдиного стилю)
    private static void loginProcess(Scanner scanner) {
        int attempts = 0;
        long lockoutEndTime = 0;

        while (true) {
            long currentTime = System.currentTimeMillis();
            if (currentTime < lockoutEndTime) {
                long secondsLeft = (lockoutEndTime - currentTime) / 1000;
                System.out.println("Акаунт заблоковано. Зачекайте " + secondsLeft + " секунд.");
                try {
                    Thread.sleep(Math.min(5000, lockoutEndTime - currentTime));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }

            System.out.println("Введіть логін для подальшої роботи");
            System.out.print("Логін: ");
            String login = scanner.nextLine();

            System.out.println("Введіть пароль для подальшої роботи");
            System.out.print("Пароль: ");
            String password = scanner.nextLine();

            try {
                AuthResult authResult = authenticate(login, password);
                if (authResult != null) {
                    attempts = 0; // reset attempts on success
                    System.out.println("Увійшли як: " + authResult.lastName + " " + authResult.role);
                    switch (authResult.role) {
                        case "RegistrationAdmin":
                            registrationMenu(scanner, authResult.role, authResult.lastName, login);
                            break;
                        case "BookingAdmin":
                            bookingMenu(scanner, authResult.role, authResult.lastName, login);
                            break;
                        case "SystemAdmin":
                            fullAccessMenu(scanner, authResult.role, authResult.lastName, login);
                            break;
                        default:
                            System.out.println("Невідома роль: " + authResult.role);
                            break;
                    }
                    break;
                } else {
                    attempts++;
                    if (attempts >= 3) {
                        lockoutEndTime = System.currentTimeMillis() + 5 * 60 * 1000; // 5 minutes
                        System.out.println("Три невдалі спроби. Акаунт заблоковано на 5 хвилин.");
                    } else {
                        System.out.println("Невірні логін або пароль, повторіть спробу");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    // Клас для результату аутентифікації
    private static class AuthResult {
        String role;
        String lastName;
        AuthResult(String role, String lastName) {
            this.role = role;
            this.lastName = lastName;
        }
    }

    // Аутентифікація користувача
    private static AuthResult authenticate(String login, String password) throws SQLException {
        String sql = "SELECT role, last_name FROM admins WHERE login = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new AuthResult(rs.getString("role"), rs.getString("last_name"));
            } else {
                return null;
            }
        }
    }

    // Меню для реєстрації Ведучих
    private static void registrationMenu(Scanner scanner, String role, String lastName, String firstName) throws SQLException {
        // Виведення привітання з ім'ям і прізвищем адміністратора
        System.out.println("Вітаємо в вашому записі роль " + role + " " + lastName + " " + firstName);

        while (true) {
            System.out.println("1. Register Host");
            System.out.println("2. Список ведучих, які забронювали кімнати");
            System.out.println("3. Exit");
            System.out.print("Choose: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    registerHost(scanner);
                    break;
                case 2:
                    new DBManager().getHostsWithBookingsSortedByDate();
                    break;
                case 3:
                    loginProcess(scanner);
                default:
                    System.out.println("Невірний вибір, спробуйте ще раз.");
            }
        }
    }

    // Меню для бронювання кімнат
    private static void bookingMenu(Scanner scanner, String role, String lastName, String firstName) throws SQLException {
        System.out.println("Вітаємо в меню бронювання " + role + " " + lastName + " " + firstName);
        while (true) {
            System.out.println("Меню бронювання:");
            System.out.println("1. Забронювати кімнату");
            System.out.println("2. Вийти");
            System.out.print("Choose: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    bookRoom(scanner);
                    break;
                case 2:
                    loginProcess(scanner);
                default:
                    System.out.println("Невірний вибір, спробуйте ще раз.");
            }
        }
    }

    // Меню для повного доступу
    private static void fullAccessMenu(Scanner scanner, String role, String lastName, String firstName) throws SQLException {
        System.out.println("Вітаємо в меню повного доступу " + role + " " + lastName + " " + firstName);
        while (true) {
            System.out.println("Меню повного доступу:");
            System.out.println("1. Зареєструвати ведучого");
            System.out.println("2. Забронювати кімнату");
            System.out.println("3. Переглянути список ведучих");
            System.out.println("4. Переглянути список ведучих, чиї прізвища починаються на літеру 'А'");
            System.out.println("5. Переглянути список бронювань у заданий період");
            System.out.println("6. Переглянути кількість бронювань за останній тиждень");
            System.out.println("7. Вивести кількість бронювань зроблену кожним ведучим");
            System.out.println("8. Вивести ведучого з найбільшою кількістю бронювань");
            System.out.println("9. Вивести ведучого з найбільшою кількістю бронювань по кожній кімнаті");
            System.out.println("10. Вивести ведучих без бронювань (Left Join)");
            System.out.println("11. Вивести ведучих без бронювань (In)");
            System.out.println("12. Вивести ведучих без бронювань (Exists)");
            System.out.println("13. Вивести ведучих з коментарем");
            System.out.println("14. Вийти");
            System.out.print("Choose: ");
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    registerHost(scanner);
                    break;
                case 2:
                    bookRoom(scanner);
                    break;
                case 3:
                    new DBManager().getHostsWithBookingsSortedByDate();
                    break;
                case 4:
                    new DBManager().findHostsByLastNameStartingWithA();
                    break;
                case 5:
                    System.out.print("Введіть початкову дату (YYYY-MM-DD): ");
                    String startDate = scanner.nextLine();
                    System.out.print("Введіть кінцеву дату (YYYY-MM-DD): ");
                    String endDate = scanner.nextLine();
                    new DBManager().getBookingsInPeriod(startDate, endDate);
                    break;
                case 6:
                    new DBManager().countBookingsLastWeek();
                    break;
                case 7:
                    new DBManager().countBookingsByHost();
                    break;
                case 8:
                    new DBManager().hostWithMostBookings();
                    break;
                case 9:
                    new DBManager().hostWithMostBookingsPerRoom();
                    break;
                case 10:
                    new DBManager().hostsWithoutBookings();
                    break;
                case 11:
                    new DBManager().hostsNotInReservations();
                    break;
                case 12:
                    new DBManager().hostsWithoutBookingsExists();
                    break;
                case 13:
                    new DBManager().hostsWithComments();
                    break;
                case 14:
                    loginProcess(scanner);
                    break;
            }
        }
    }
    
    // Реєстрація ведучого
    private static void registerHost(Scanner scanner) throws SQLException {
        RegistrationForm form = new RegistrationForm();
        UserRepository repo = new UserRepository();
        UserDirectory dir = new UserDirectory();

        System.out.print("Ім'я: ");
        form.input("first_name", scanner.nextLine());

        System.out.print("Побатькові: ");
        form.input("last_name", scanner.nextLine());

        System.out.print("Прізвище: ");
        form.input("middle_name", scanner.nextLine());

        System.out.print("Псевдонім: ");
        String nick = scanner.nextLine();
        form.input("nick", nick);

        System.out.print("Телефон: ");
        form.input("phone", scanner.nextLine());

        System.out.print("Вік: ");
        form.input("age", scanner.nextLine());

        if (form.validateFormData()) {
            if (repo.isNicknameUnique(nick)) {
                dir.saveUser(form.submit());
                System.out.println("Ведучий зареєстрований успішно.");
            } else {
                System.out.println("Nickname вже існує.");
            }
        } else {
            System.out.println("Невірні дані.");
        }
    }

    private static void bookRoom(Scanner scanner) throws SQLException {
        BookingForm form = new BookingForm();
        BookingRepository repo = new BookingRepository();
        HostRegistry hostReg = new HostRegistry();
        RoomSelector roomSel = new RoomSelector();

        System.out.print("Nickname ведучого: ");
        int hostId = hostReg.getHostIdByNick(scanner.nextLine());

        System.out.print("Назва кімнати: ");
        int roomId = roomSel.getRoomIdByName(scanner.nextLine());

        System.out.print("Дата (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        System.out.print("Час (HH:MM): ");
        String time = scanner.nextLine();

        System.out.print("Admin ID: ");
        int adminId = Integer.parseInt(scanner.nextLine());

        if (repo.isRoomAvailable(roomId, date, time)) {
            repo.saveBooking(hostId, roomId, adminId, date, time);
            System.out.println("Кімната заброньована.");
        } else {
            System.out.println("Кімната не доступна.");
        }
    }
}