package com.example.demo.util;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ID生成工具
 * @author nature
 * @version 1.0 2020/3/30
 */
public class IdGenUtils {

    private static final SnowFlake snowFlake = new SnowFlake(getDateCenterId(), getWorkerId());

    /**
     * 工具类，私有构造方法
     */
    private IdGenUtils() {
    }

    public static long nextId() {
        return snowFlake.nextId();
    }

    private static long getDateCenterId() {

        return 0;
    }

    private static long getWorkerId() {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for (int i : ints) {
                sums += i;
            }
            return sums % 32;
        } catch (UnknownHostException e) {
            return 31;
        }
    }
}
