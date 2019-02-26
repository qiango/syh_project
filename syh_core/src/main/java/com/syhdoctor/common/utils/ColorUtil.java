package com.syhdoctor.common.utils;

import java.awt.*;

public class ColorUtil {
    // 把字符串表达的颜色值转换成java.awt.Color
    public static Color parseToColor(String c) {
        Color convertedColor = Color.ORANGE;
        try {
            convertedColor = new Color(Integer.parseInt(c, 16));
        } catch (NumberFormatException e) {
            e.getStackTrace();
        }
        return convertedColor;
    }

    // Color转换为16进制显示
    public static String toHexEncoding(Color color) {
        String R, G, B;
        StringBuffer sb = new StringBuffer();
        R = Integer.toHexString(color.getRed());
        G = Integer.toHexString(color.getGreen());
        B = Integer.toHexString(color.getBlue());
        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;
        sb.append("0x");
        sb.append(R);
        sb.append(G);
        sb.append(B);
        return sb.toString();
    }


}
