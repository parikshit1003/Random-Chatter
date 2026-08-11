package org.example.demo.util;

import java.util.Random;

public class GenerateSessionToken {
    public static String generate(){
        String data = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        Random random = new Random();

        StringBuilder token = new StringBuilder();

        for(int i = 0; i < 10; i++){
            token.append(data.charAt(random.nextInt(data.length() - 1)));
        }

        return token.toString();
    }
}
