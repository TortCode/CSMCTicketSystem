package edu.utdallas.csmc.web.util;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class TimeUtils {
    // Format sample: +P00Y00M00DT01H15M00S
    public static int[] getHourMinuteFromString(String str) {
        int[] tmp = {0, 0};
        try {
            int h = Integer.parseInt(str.substring(12, 14));
            int m = Integer.parseInt(str.substring(15, 17));
            tmp[0] = h;
            tmp[1] = m;
        } catch (Exception e ) {
            log.info("Can not convert String to Time");
        }
        return tmp;
    }

    public static String convertHourMinuteToStringFormat(int h, int m) {
        String hour = h < 10 ? "0" + Integer.toString(h) : Integer.toString(h);
        String minute = m < 10 ? "0" + Integer.toString(m) : Integer.toString(m);
        return "+P00Y00M00DT" + hour + "H" + minute + "M00S";
    }
}
