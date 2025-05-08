package edu.utdallas.csmc.web.util;

import edu.utdallas.csmc.web.model.misc.IpAddress;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

public class IpChecker {
    public static String UTD_MASK = "10.0.0.0/8";
    public static String CS_DEPT_MASK = "10.176.0.0/16";

    public static boolean inRange(String subnet, IpAddress ip){
        IpAddressMatcher ipAddressMatcher = new IpAddressMatcher(subnet);
        return ipAddressMatcher.matches(longToIp(ip.getAddress()));
    }

    public static String longToIp(long ip) {
        return ((ip >> 24) & 0xFF) + "."
                + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 8) & 0xFF) + "."
                + (ip & 0xFF);
    }
    public static long ipToLong(String ipAddress) {
        String[] ipAddressInArray = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < ipAddressInArray.length; i++) {
            int power = 3 - i;
            int ip = Integer.parseInt(ipAddressInArray[i]);
            result += ip * Math.pow(256, power);
        }
        return result;
    }
}
