package com.sun.takeaway;

import cn.hutool.core.lang.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.io.File;

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

    @Value("${upload.basePath}")
    private String basePath;
    @Test
    void testUpload() {
        String originalFilename = "abc.jpg";
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.fastUUID().toString(true) + suffix;
        System.out.println("fileName = " + fileName);

        File file = new File(basePath + fileName);
        System.out.println("file = " + file);
    }
}
