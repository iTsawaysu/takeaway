package com.sun.takeaway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

/**
 * @author sun
 */
@SpringBootTest
public class TakeawayApplicationTests {

    private static final String SALT = "iTsawaysu";

    @Test
    void testMD5() {
        String s = DigestUtils.md5DigestAsHex((SALT + "sun123").getBytes());
        System.out.println("s = " + s);
    }
}
