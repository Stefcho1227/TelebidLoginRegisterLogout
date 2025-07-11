// src/test/java/util/SessionManagerTest.java
package util;

import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {
    @Test
    void createSessionStoresAndRetrievesUserId() {
        int userId = 42;
        String sid = SessionManager.createSession(userId);
        assertNotNull(sid, "SID не бива да е null");
        assertEquals(userId, SessionManager.getUserId(sid),
                "getUserId трябва да върне същия uid");
    }
    @Test
    void sessionIdsAreUnique() {
        Set<String> ids = new HashSet<>();

        for (int i = 0; i < 20; i++) {
            ids.add(SessionManager.createSession(i));
        }
        assertEquals(20, ids.size(), "Всички SID трябва да са уникални");
    }
    @Test
    void invalidateRemovesSession() {
        int uid = 7;
        String sid = SessionManager.createSession(uid);
        assertNotNull(SessionManager.getUserId(sid));
        SessionManager.invalidate(sid);
        assertNull(SessionManager.getUserId(sid),
                "След invalidate не бива да остава запис за сесията");
    }
    @Test
    void invalidateNullIsNoOp() {
        assertDoesNotThrow(() -> SessionManager.invalidate(null));
    }
}
