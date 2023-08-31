package com.dhx.bi.utils;

import java.lang.management.ManagementFactory;
import com.dhx.bi.model.DTO.ServerLoadInfo;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;

/**
 * @author adorabled4
 * @className ServerMetricsUtil 服务器压力指标
 * @date : 2023/08/30/ 11:36
 **/
public class ServerMetricsUtil {
    private static OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    private static MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

    private static ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

    /**
     * 获取当前服务器CPU使用占比
     *
     * @return CPU usage percentage.
     */
    public static double getCpuUsagePercentage() {
        return osBean.getProcessCpuLoad() * 100; // Convert to percentage
    }

    /**
     * 获取当前服务器内存使用占比
     *
     * @return Memory usage percentage.
     */
    public static double getMemoryUsagePercentage() {
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();

        return ((double) usedMemory / maxMemory) * 100; // Convert to percentage
    }

    /**
     * 判断当前使用同步还是异步进行服务
     * based on CPU and memory usage.
     *
     * @return true for synchronous, false for asynchronous.
     */
    public static boolean shouldProvideSync() {
        double cpuUsagePercentage = getCpuUsagePercentage();
        double memoryUsagePercentage = getMemoryUsagePercentage();

        // Threshold values for CPU and memory usage
        double cpuThreshold = 70.0; // Example threshold value
        double memoryThreshold = 80.0; // Example threshold value

        if (cpuUsagePercentage < cpuThreshold && memoryUsagePercentage < memoryThreshold) {
            return true; // Provide service synchronously
        } else {
            return false; // Provide service asynchronously
        }
    }

    public static ServerLoadInfo getLoadInfo() {
        double cpuUsagePercentage = getCpuUsagePercentage();
        double memoryUsagePercentage = getMemoryUsagePercentage();
        return new ServerLoadInfo(cpuUsagePercentage,memoryUsagePercentage);
    }
}