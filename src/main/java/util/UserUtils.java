package util;

import pojo.User;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class UserUtils {

    public static User getRandomUser() {
        Random random = new Random();
        Charset charset = StandardCharsets.UTF_8;

        byte[] emailArray = new byte[11];
        random.nextBytes(emailArray);
        String email = new String(emailArray, charset) + "@yandex.ru";

        byte[] passwordArray = new byte[9];
        random.nextBytes(passwordArray);
        String password = new String(passwordArray, charset);

        byte[] nameArray = new byte[5];
        random.nextBytes(nameArray);
        String name = new String(nameArray, charset);

        User user = new User(email, password, name);
        return user;
    }
}
