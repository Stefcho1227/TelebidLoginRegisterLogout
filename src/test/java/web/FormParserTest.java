package web;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

class FormParserTest {
    @Test
    void parseSimplePairs() throws Exception {
        String body = "a=1&b=two";
        Map<String, String> res = FormParser.parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
        Assertions.assertEquals(2, res.size());
        Assertions.assertEquals("1",  res.get("a"));
        Assertions.assertEquals("two", res.get("b"));
    }
    @Test
    void parsesAndDecodesPlusAndPercent() throws Exception {
        String body = "name=John+Doe&lang=C%2B%2B";
        Map<String, String> res = FormParser.parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
        Assertions.assertEquals("John Doe", res.get("name"));
        Assertions.assertEquals("C++",      res.get("lang"));
    }
    @Test
    void ignoresPairsWithoutEquals() throws Exception {
        String body = "flag&x=1";
        Map<String, String> res = FormParser.parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
        Assertions.assertEquals(1, res.size());
        Assertions.assertEquals("1", res.get("x"));
        Assertions.assertFalse(res.containsKey("flag"));
    }
    @Test
    void handlesEmptyValues() throws Exception {
        String body = "key=&empty=&k2=val";
        Map<String, String> res = FormParser.parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
        Assertions.assertEquals("", res.get("key"));
        Assertions.assertEquals("", res.get("empty"));
        Assertions.assertEquals("val", res.get("k2"));
    }
}
