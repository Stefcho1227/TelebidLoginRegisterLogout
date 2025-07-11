package util;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager {
    private static final ConcurrentHashMap<String,Integer> SESSIONS = new ConcurrentHashMap<>();
    public static void init() {}
    public static String createSession(int uid) {
        String sid = UUID.randomUUID().toString();
        SESSIONS.put(sid, uid);
        return sid;
    }
    public static Integer getUserId(String sid) {
        return SESSIONS.get(sid);
    }
    public static void invalidate(String sid) {
        if (sid != null) {
            SESSIONS.remove(sid);
        }
    }
}
