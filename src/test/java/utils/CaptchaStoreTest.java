package utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class CaptchaStoreTest {
    @Test
    void generateStoresAndPeekReturns() {
        String id  = "id-peek";
        String txt = CaptchaStore.generate(id);

        Assertions.assertNotNull(txt);
        Assertions.assertEquals(txt, CaptchaStore.peek(id));
    }
    @Test
    void verifyReturnsTrueAndRemoves() {
        String id  = "id-ok";
        String txt = CaptchaStore.generate(id);
        Assertions.assertTrue(CaptchaStore.verify(id, txt));
        Assertions.assertNull(CaptchaStore.peek(id), "Елементът трябва да е изтрит след verify");
    }
    @Test
    void verifyReturnsFalseAndStillRemovesEntry() {
        String id  = "id-bad";
        CaptchaStore.generate(id);

        Assertions.assertFalse(CaptchaStore.verify(id, "wrongAnswer"));
        Assertions.assertNull(CaptchaStore.peek(id), "Дори при грешен отговор записът се премахва");
    }
    @Test
    void randomTextIsReasonablyUnique() {
        Set<String> generated = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            generated.add(CaptchaStore.generate("uniq" + i));
        }
        Assertions.assertTrue(generated.size() > 1, "Текстът трябва да е случаен; очакваме различни стойности");
    }
}
