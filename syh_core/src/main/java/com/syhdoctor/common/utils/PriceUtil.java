package com.syhdoctor.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtil {
    public static void main(String[] args) {
        BigDecimal divide = new BigDecimal(994.9).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        System.out.println(divide);
    }

    public static BigDecimal findPrice(Long value) {
        if (value == null) {
            return new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            return new BigDecimal(value).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        }
    }

    public static BigDecimal findPrice(BigDecimal value) {
        if (value == null) {
            return new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            return value.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        }
    }

    public static BigDecimal addPrice(BigDecimal value) {
        if (value == null) {
            return new BigDecimal(0);
        } else {
            return value.multiply(new BigDecimal(100));
        }
    }
}
