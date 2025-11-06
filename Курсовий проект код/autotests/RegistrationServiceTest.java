import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class RegistrationServiceTest {
    @Test
    public void invalidData_shouldReturn_invalidDataFlag() {
        RegistrationService svc = new RegistrationService();
        Map<String, String> data = new HashMap<>(); // пусто -> invalid
        RegistrationService.Result r = svc.registerHost(data);
        assertTrue(r.invalidData);
    }

    @Test
    public void validButNickExists_shouldReturn_nickExistsFlag() {
        RegistrationService svc = new RegistrationService();
        Map<String, String> good = new HashMap<>();
        good.put("nick", "alice");
        good.put("name", "Alice");
        good.put("email", "a@x.com");
        svc.registerHost(good); // first time saved

        Map<String, String> second = new HashMap<>(good);
        RegistrationService.Result r = svc.registerHost(second);
        assertTrue(r.nickExists);
    }

    @Test
    public void validUniqueNick_short_shouldReturn_success() {
        RegistrationService svc = new RegistrationService();
        Map<String, String> good = new HashMap<>();
        good.put("nick", "bob");
        good.put("name", "Bob");
        good.put("email", "b@x.com");
        RegistrationService.Result r = svc.registerHost(good);
        // перевіряємо ключові флаги за скороченою таблицею
        assertTrue(r.success);
        assertTrue(r.saved);
    }
}
