package com.syhdoctor.common.utils;

import java.util.Random;

public class RandomUtil {
    public static String random(int length) {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < length; i++) {
            result = String.format("%s%s", result, random.nextInt(10));
        }
        return result;

    }
}
