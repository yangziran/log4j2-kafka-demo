package com.example.demo.util;

import java.time.Instant;

/**
 * Twitter雪花算法（SnowFlake）
 * SnowFlake的结构如下(每部分用-分开):
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * 1) 1位，不用。二进制中最高位为1的都是负数，但是我们生成的id一般都使用整数，所以这个最高位固定是0
 * 2) 41位，用来记录时间戳（毫秒）。
 * 3) 41位可以表示2^41−1个数字，如果只用来表示正整数（计算机中正数包含0），可以表示的数值范围是：0 至 2^41−1，减1是因为可表示的数值范围是从0开始算的，而不是1。
 * 也就是说41位可以表示2^41−1个毫秒的值，转化成单位年则是(2^41−1)/(1000∗60∗60∗24∗365)=69年
 * 4) 10位，用来记录工作机器id。
 * 可以部署在2^10=1024个节点，包括5位datacenterId和5位workerId
 * 5) 5位（bit）可以表示的最大正整数是2^5−1=31，即可以用0、1、2、3、....31这32个数字，来表示不同的datecenterId或workerId
 * 6) 12位，序列号，用来记录同毫秒内产生的不同id。
 * 12位（bit）可以表示的最大正整数是2^12−1=4095，即可以用0、1、2、3、....4094这4095个数字，来表示同一机器同一时间截（毫秒)内产生的4095个ID序号
 * 由于在Java中64bit的整数是long类型，所以在Java中SnowFlake算法生成的id就是long来存储的。
 * @author nature
 * @version 1.0 2020/3/30
 */
public class SnowFlake {

    /** 起始的时间戳：2020-04-01 00:00:00 */
    private final long twepoch = 1585670400000L;

    /** 每部分占用的位数 */
    /** 数据中心占用的位数 */
    private final long dataCenterIdBits = 5L;
    /** 机器标识占用的位数 */
    private final long workerIdBits = 5L;
    /** 序列号占用的位数 */
    private final long sequenceBits = 12L;

    /** 每部分的最大值：先进行左移运算，再同 -1 进行异或运算；异或：相同位置相同结果为 0，不同结果为 1； */
    /** 数据中心最大值：31 */
    private final long maxDatacenterId = -1L ^ (-1L << dataCenterIdBits);
    /** 机器最大值：31 */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    /** 序列号最大值：4095 */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /** 每部分向左的位移 */
    /** 机器标志较序列号的偏移量 */
    private final long workerIdShift = sequenceBits;
    /** 数据中心较机器标志的偏移量 */
    private final long dataCenterIdShift = sequenceBits + workerIdBits;
    /** 时间戳较数据中心的偏移量 */
    private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    /** 数据中心 */
    private long dataCenterId;
    /** 机器标识 */
    private long workerId;
    /** 序列号 */
    private long sequence = 0L;
    /** 上一次时间戳 */
    private long lastTimestamp = -1L;

    public SnowFlake(long dataCenterId, long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker Id can't be greater than %d or less than 0",
                    maxWorkerId));
        }
        if (dataCenterId > maxDatacenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("DataCenter Id can't be greater than %d or less than 0",
                    maxDatacenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * 产生下一个ID
     * @return long ID
     */
    public synchronized long nextId() {
        // 获取当前时间戳
        long timestamp = timeGen();
        // 如果当前时间戳小于上次时间戳则抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d " +
                    "milliseconds", lastTimestamp - timestamp));
        }

        // 相同毫秒内
        if (lastTimestamp == timestamp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & sequenceMask;
            // 同一毫秒的序列数已经达到最大
            if (sequence == 0) {
                // 获取下一时间的时间戳并赋值给当前时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒内，序列号置为0
            sequence = 0L;
        }

        // 当前时间戳存档记录，用于下次产生ID时对比是否为相同时间戳
        lastTimestamp = timestamp;

        // 时间戳部分 | 数据中心部分 | 机器标识部分 | 序列号部分
        return ((timestamp - twepoch) << timestampLeftShift) | (dataCenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return Instant.now().toEpochMilli();
    }

}
