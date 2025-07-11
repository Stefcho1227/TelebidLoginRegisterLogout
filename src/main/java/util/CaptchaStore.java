package util;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class CaptchaStore {
    private static final ConcurrentHashMap<String,String> STORE = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();
    static {
        cleaner.scheduleAtFixedRate(() -> STORE.entrySet().removeIf(e -> e.getValue().endsWith("|expired")),
                1, 1, TimeUnit.MINUTES);
    }
    public static String generate(String id) {
        String txt = randomText(6);
        STORE.put(id, txt);
        return txt;
    }
    public static boolean verify(String id, String answer) {
        String real = STORE.remove(id);
        return real != null && real.equalsIgnoreCase(answer);
    }
    public static String peek(String id) {
        return STORE.get(id);
    }
    private static String randomText(int len) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random r = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
