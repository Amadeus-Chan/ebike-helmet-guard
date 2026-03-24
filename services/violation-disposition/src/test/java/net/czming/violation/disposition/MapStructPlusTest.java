package net.czming.violation.disposition;

import io.github.linpeilie.Converter;
import net.czming.model.violation.disposition.context.UserContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class MapStructPlusTest {

    @Autowired
    Converter converter;

    @Test
    public void test() {
        Map<String, Object> map = new HashMap<>();
        map.put("username", "czming");
        map.put("role", "ADMIN");
        map.put("status", "ENABLED");

        UserContext convert = converter.convert(map, UserContext.class);
        System.out.println(convert);
    }
}
