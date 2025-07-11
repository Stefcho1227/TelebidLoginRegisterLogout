package web;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FormParserTest {
    @Test
    void parseSimplePairs() throws Exception {
        String body = "a=1&b=two";
        Map<String, String> res = FormParser.parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
        assertEquals(2, res.size());
        assertEquals("1",  res.get("a"));
        assertEquals("two", res.get("b"));
    }
    @Test
    void parsesAndDecodesPlusAndPercent() throws Exception {
        String body = "name=John+Doe&lang=C%2B%2B";
        Map<String, String> res = FormParser.parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
        assertEquals("John Doe", res.get("name"));
        assertEquals("C++",      res.get("lang"));
    }
    @Test
    void ignoresPairsWithoutEquals() throws Exception {
        String body = "flag&x=1";
        Map<String, String> res = FormParser.parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
        assertEquals(1, res.size());
        assertEquals("1", res.get("x"));
        assertFalse(res.containsKey("flag"));
    }
    @Test
    void handlesEmptyValues() throws Exception {
        String body = "key=&empty=&k2=val";
        Map<String, String> res = FormParser.parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
        assertEquals("", res.get("key"));
        assertEquals("", res.get("empty"));
        assertEquals("val", res.get("k2"));
    }
}
