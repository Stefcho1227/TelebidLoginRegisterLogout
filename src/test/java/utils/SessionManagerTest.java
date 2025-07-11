package utils;

import util.SessionManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class SessionManagerTest {
    @Test
    void createSessionStoresAndRetrievesUserId() {
        int userId = 42;
        String sid = SessionManager.createSession(userId);
        Assertions.assertNotNull(sid, "SID не бива да е null");
        Assertions.assertEquals(userId, SessionManager.getUserId(sid),
                "getUserId трябва да върне същия uid");
    }
    @Test
    void sessionIdsAreUnique() {
        Set<String> ids = new HashSet<>();

        for (int i = 0; i < 20; i++) {
            ids.add(SessionManager.createSession(i));
        }
        Assertions.assertEquals(20, ids.size(), "Всички SID трябва да са уникални");
    }
    @Test
    void invalidateRemovesSession() {
        int uid = 7;
        String sid = SessionManager.createSession(uid);
        Assertions.assertNotNull(SessionManager.getUserId(sid));
        SessionManager.invalidate(sid);
        Assertions.assertNull(SessionManager.getUserId(sid),
                "След invalidate не бива да остава запис за сесията");
    }
    @Test
    void invalidateNullIsNoOp() {
        Assertions.assertDoesNotThrow(() -> SessionManager.invalidate(null));
    }
}
