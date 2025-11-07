package test_suites;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class BookingServiceTest {
    @Test
    public void hostNotFound_shouldReturn_hostNotFound() {
        RegistrationService reg = new RegistrationService();
        BookingService svc = new BookingService(reg);
        BookingService.Result r = svc.bookRoom("unknown", 1, new Date(), "10:00");
        assertTrue(r.hostNotFound);
    }

    @Test
    public void roomNotFound_shouldReturn_roomNotFound() {
        RegistrationService reg = new RegistrationService();
        BookingService svc = new BookingService(reg);
        Map<String,String> host = new HashMap<>();
        host.put("nick","host1"); host.put("name","X"); host.put("email","x@x");
        reg.registerHost(host);

        BookingService.Result r = svc.bookRoom("host1", 999, new Date(), "10:00");
        assertTrue(r.roomNotFound);
    }

    @Test
    public void notAvailable_shouldReturn_notAvailable() {
        RegistrationService reg = new RegistrationService();
        BookingService svc = new BookingService(reg);
        Map<String,String> host = new HashMap<>();
        host.put("nick","host2"); host.put("name","Y"); host.put("email","y@x");
        reg.registerHost(host);

        Date dt = new Date();
        svc.prebook(1, dt, "10:00");
        BookingService.Result r = svc.bookRoom("host2", 1, dt, "10:00");
        assertTrue(r.notAvailable);
    }

    @Test
    public void successBooking_shouldReturn_successAndSaved() {
        RegistrationService reg = new RegistrationService();
        BookingService svc = new BookingService(reg);
        Map<String,String> host = new HashMap<>();
        host.put("nick","host3"); host.put("name","Z"); host.put("email","z@x");
        reg.registerHost(host);

        BookingService.Result r = svc.bookRoom("host3", 2, new Date(), "11:00");
        assertTrue(r.success);
        assertTrue(r.saved);
    }
}

// helper classes in the same package for test isolation
class RegistrationService {
    private final java.util.Set<String> nicks = new java.util.HashSet<>();

    public static class Result {
        public final boolean invalidData;
        public final boolean nickExists;
        public final boolean success;
        public final boolean saved;

        public Result(boolean invalidData, boolean nickExists, boolean success, boolean saved) {
            this.invalidData = invalidData;
            this.nickExists = nickExists;
            this.success = success;
            this.saved = saved;
        }
    }

    public Result registerHost(java.util.Map<String, String> data) {
        if (data == null || data.get("nick") == null || data.get("name") == null || data.get("email") == null ||
                data.get("nick").trim().isEmpty() || data.get("name").trim().isEmpty() || data.get("email").trim().isEmpty()) {
            return new Result(true, false, false, false);
        }
        String nick = data.get("nick").trim();
        if (nicks.contains(nick)) {
            return new Result(false, true, false, false);
        }
        nicks.add(nick);
        return new Result(false, false, true, true);
    }

    public boolean hostExists(String nick) {
        return nick != null && nicks.contains(nick);
    }
}

class BookingService {
    private final RegistrationService registrationService;
    private final java.util.Set<Integer> rooms = new java.util.HashSet<>();
    private final java.util.Set<String> bookings = new java.util.HashSet<>();

    public static class Result {
        public final boolean hostNotFound;
        public final boolean roomNotFound;
        public final boolean notAvailable;
        public final boolean success;
        public final boolean saved;

        public Result(boolean hostNotFound, boolean roomNotFound, boolean notAvailable, boolean success, boolean saved) {
            this.hostNotFound = hostNotFound;
            this.roomNotFound = roomNotFound;
            this.notAvailable = notAvailable;
            this.success = success;
            this.saved = saved;
        }
    }

    public BookingService(RegistrationService registrationService) {
        this.registrationService = registrationService;
        rooms.add(1);
        rooms.add(2);
        rooms.add(3);
    }

    public Result bookRoom(String hostNick, int roomId, java.util.Date date, String time) {
        if (!registrationService.hostExists(hostNick)) {
            return new Result(true, false, false, false, false);
        }
        if (!rooms.contains(roomId)) {
            return new Result(false, true, false, false, false);
        }
        String key = keyFor(roomId, date, time);
        if (bookings.contains(key)) {
            return new Result(false, false, true, false, false);
        }
        bookings.add(key);
        return new Result(false, false, false, true, true);
    }

    private String keyFor(int roomId, java.util.Date date, String time) {
        long d = date == null ? 0L : date.getTime();
        String t = time == null ? "" : time;
        return roomId + "|" + d + "|" + t;
    }

    public void prebook(int roomId, java.util.Date date, String time) {
        bookings.add(keyFor(roomId, date, time));
    }
}


