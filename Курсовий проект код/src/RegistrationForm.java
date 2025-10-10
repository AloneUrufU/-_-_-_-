import java.util.HashMap;
import java.util.Map;

public class RegistrationForm {
    private Map<String, String> formData = new HashMap<>();

    public void input(String fieldName, String value) {
        formData.put(fieldName, value);
    }

    public boolean validateFormData() {
        return formData.containsKey("first_name") &&
            formData.containsKey("last_name") &&
            formData.containsKey("nick") &&
            formData.containsKey("phone") &&
            formData.containsKey("age");
    }

    public Map<String, String> submit() {
        if (validateFormData()) {
            return formData;
        } else {
            throw new IllegalArgumentException("Invalid form data");
        }
    } // input, validateFormData, submit - ревізія, без змін
}