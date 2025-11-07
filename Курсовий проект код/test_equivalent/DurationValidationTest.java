package test_equivalent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DurationValidationTest {

    private final DurationValidator validator = new DurationValidator();

    @Test
    public void tcEC1_validDuration_shouldSucceed() {
        DurationValidator.Result r = validator.validate("120");
        assertTrue("Очікується успішна валідація", r.success);
        assertTrue("Дані мають зберігатися", r.saved);
        assertEquals("Бронювання успішне", r.message);
    }

    @Test
    public void tcEC2_durationBelowMinimum_shouldFail() {
        DurationValidator.Result r = validator.validate("30");
        assertFalse(r.success);
        assertFalse(r.saved);
        assertEquals("Тривалість має бути не менше 60 хвилин", r.message);
    }

    @Test
    public void tcEC3_durationAboveMaximum_shouldFail() {
        DurationValidator.Result r = validator.validate("300");
        assertFalse(r.success);
        assertFalse(r.saved);
        assertEquals("Тривалість не може перевищувати 240 хвилин", r.message);
    }

    @Test
    public void tcEC4_lettersInsteadOfNumber_shouldFailFormat() {
        DurationValidator.Result r = validator.validate("півтори");
        assertFalse(r.success);
        assertFalse(r.saved);
        assertEquals("Невірний формат: Потрібно ввести ціле число", r.message);
    }

    @Test
    public void tcEC5_specialCharacters_shouldFailFormat() {
        DurationValidator.Result r = validator.validate("120@");
        assertFalse(r.success);
        assertFalse(r.saved);
        assertEquals("Невірний формат: Потрібно ввести ціле число", r.message);
    }

    @Test
    public void tcEC6_emptyField_shouldFailRequired() {
        DurationValidator.Result r = validator.validate("");
        assertFalse(r.success);
        assertFalse(r.saved);
        assertEquals("Поле \"Тривалість\" є обов'язковим для заповнення", r.message);
    }
}

class DurationValidator {

    public DurationValidator.Result validate(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new Result(false, false, "Поле \"Тривалість\" є обов'язковим для заповнення");
        }
        String normalized = input.trim();
        int duration;
        try {
            if (!normalized.matches("-?\\d+")) {
                throw new NumberFormatException("non-integer");
            }
            duration = Integer.parseInt(normalized);
        } catch (NumberFormatException ex) {
            return new Result(false, false, "Невірний формат: Потрібно ввести ціле число");
        }

        if (duration < 60) {
            return new Result(false, false, "Тривалість має бути не менше 60 хвилин");
        }
        if (duration > 240) {
            return new Result(false, false, "Тривалість не може перевищувати 240 хвилин");
        }
        return new Result(true, true, "Бронювання успішне");
    }

    public static class Result {
        public final boolean success;
        public final boolean saved;
        public final String message;

        public Result(boolean success, boolean saved, String message) {
            this.success = success;
            this.saved = saved;
            this.message = message;
        }
    }
}

