package util;

import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern NAME = Pattern.compile("^(?=.*[\\p{L}\\p{M}])[\\p{L}\\p{M}'\\- ]+$");

    public static boolean isValidEmail(String s) {
        return s != null && EMAIL.matcher(s).matches();
    }
    public static boolean isValidName(String s, int min, int max) {
        return s != null && s.length() >= min && s.length() <= max && NAME.matcher(s).matches() && !s.isEmpty();
    }
    public static boolean isStrongPassword(String pw) {
        return pw != null && pw.length() >= 8 && pw.matches(".*[A-Z].*") &&
                pw.matches(".*[a-z].*") &&
                pw.matches(".*\\d.*") &&
                pw.matches(".*[!@#$%^&*].*");
    }
}
