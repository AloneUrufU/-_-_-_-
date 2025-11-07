package test_sequence;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class BookingSequenceTest {
    @Test
    public void successFlow_stepsShouldBe_15_1_2_3_25_24() {
        RegistrationServiceStub reg = new RegistrationServiceStub();
        Map<String,String> host = new HashMap<>();
        host.put("nick","h1"); host.put("name","N"); host.put("email","e@x");
        reg.registerHost(host);

        BookingServiceWithTrace svc = new BookingServiceWithTrace(reg);
        BookingServiceWithTrace.Result r = svc.bookRoom("h1", 1, new Date(), "10:00");
        System.out.println("steps(success): " + r.steps);
        assertEquals("Успішна послідовність має бути [15,1,2,3,25,24]", java.util.Arrays.asList(15,1,2,3,25,24), r.steps);
    }

    @Test
    public void noHost_stepsShouldBe_15_1_21() {
        RegistrationServiceStub reg = new RegistrationServiceStub();
        BookingServiceWithTrace svc = new BookingServiceWithTrace(reg);
        BookingServiceWithTrace.Result r = svc.bookRoom("unknown", 1, new Date(), "10:00");
        System.out.println("steps(noHost): " + r.steps);
        assertEquals("Немає ведучого: послідовність має бути [15,1,21]", java.util.Arrays.asList(15,1,21), r.steps);
    }

    @Test
    public void noRoom_stepsShouldBe_15_1_2_22() {
        RegistrationServiceStub reg = new RegistrationServiceStub();
        Map<String,String> host = new HashMap<>();
        host.put("nick","h2"); host.put("name","N"); host.put("email","e@x");
        reg.registerHost(host);

        BookingServiceWithTrace svc = new BookingServiceWithTrace(reg);
        BookingServiceWithTrace.Result r = svc.bookRoom("h2", 999, new Date(), "10:00");
        System.out.println("steps(noRoom): " + r.steps);
        assertEquals("Немає кімнати: послідовність має бути [15,1,2,22]", java.util.Arrays.asList(15,1,2,22), r.steps);
    }

    @Test
    public void notAvailable_stepsShouldBe_15_1_2_3_23() {
        RegistrationServiceStub reg = new RegistrationServiceStub();
        Map<String,String> host = new HashMap<>();
        host.put("nick","h3"); host.put("name","N"); host.put("email","e@x");
        reg.registerHost(host);

        BookingServiceWithTrace svc = new BookingServiceWithTrace(reg);
        Date dt = new Date();
        svc.prebook(1, dt, "10:00");
        BookingServiceWithTrace.Result r = svc.bookRoom("h3", 1, dt, "10:00");
        System.out.println("steps(notAvailable): " + r.steps);
        assertEquals("Недоступно: послідовність має бути [15,1,2,3,23]", java.util.Arrays.asList(15,1,2,3,23), r.steps);
    }
}

class RegistrationServiceStub {
    private final Set<String> nicks = new HashSet<>();

    public void registerHost(Map<String, String> data) {
        if (data != null && data.get("nick") != null) {
            String nick = data.get("nick").trim();
            if (!nick.isEmpty()) nicks.add(nick);
        }
    }

    public boolean hostExists(String nick) {
        return nick != null && nicks.contains(nick);
    }
}

class BookingServiceWithTrace {
    private final RegistrationServiceStub registrationService;
    private final Set<Integer> rooms = new HashSet<>();
    private final Set<String> bookings = new HashSet<>();

    public static class Result {
        public final boolean hostNotFound;
        public final boolean roomNotFound;
        public final boolean notAvailable;
        public final boolean success;
        public final boolean saved;
        public final List<Integer> steps;

        public Result(boolean hostNotFound, boolean roomNotFound, boolean notAvailable, boolean success, boolean saved, List<Integer> steps) {
            this.hostNotFound = hostNotFound;
            this.roomNotFound = roomNotFound;
            this.notAvailable = notAvailable;
            this.success = success;
            this.saved = saved;
            this.steps = steps;
        }
    }

    public BookingServiceWithTrace(RegistrationServiceStub registrationService) {
        this.registrationService = registrationService;
        rooms.add(1);
        rooms.add(2);
        rooms.add(3);
    }

    public Result bookRoom(String hostNick, int roomId, Date date, String time) {
        List<Integer> steps = new ArrayList<>();
        steps.add(15);
        steps.add(1);
        if (!registrationService.hostExists(hostNick)) {
            steps.add(21);
            return new Result(true, false, false, false, false, steps);
        }
        steps.add(2);
        if (!rooms.contains(roomId)) {
            steps.add(22);
            return new Result(false, true, false, false, false, steps);
        }
        steps.add(3);
        String key = keyFor(roomId, date, time);
        if (bookings.contains(key)) {
            steps.add(23);
            return new Result(false, false, true, false, false, steps);
        }
        steps.add(25);
        bookings.add(key);
        steps.add(24);
        return new Result(false, false, false, true, true, steps);
    }

    private String keyFor(int roomId, Date date, String time) {
        long d = date == null ? 0L : date.getTime();
        String t = time == null ? "" : time;
        return roomId + "|" + d + "|" + t;
    }

    public void prebook(int roomId, Date date, String time) {
        bookings.add(keyFor(roomId, date, time));
    }
}


