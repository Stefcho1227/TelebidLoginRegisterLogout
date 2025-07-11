// src/test/java/util/CaptchaStoreTest.java
package util;

import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CaptchaStoreTest {
    @Test
    void generateStoresAndPeekReturns() {
        String id  = "id-peek";
        String txt = CaptchaStore.generate(id);

        assertNotNull(txt);
        assertEquals(txt, CaptchaStore.peek(id));
    }
    @Test
    void verifyReturnsTrueAndRemoves() {
        String id  = "id-ok";
        String txt = CaptchaStore.generate(id);

        assertTrue(CaptchaStore.verify(id, txt));
        assertNull(CaptchaStore.peek(id), "Елементът трябва да е изтрит след verify");
    }
    @Test
    void verifyReturnsFalseAndStillRemovesEntry() {
        String id  = "id-bad";
        CaptchaStore.generate(id);

        assertFalse(CaptchaStore.verify(id, "wrongAnswer"));
        assertNull(CaptchaStore.peek(id), "Дори при грешен отговор записът се премахва");
    }
    @Test
    void randomTextIsReasonablyUnique() {
        Set<String> generated = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            generated.add(CaptchaStore.generate("uniq" + i));
        }
        assertTrue(generated.size() > 1, "Текстът трябва да е случаен; очакваме различни стойности");
    }
}
