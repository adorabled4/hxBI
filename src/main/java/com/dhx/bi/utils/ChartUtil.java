package com.dhx.bi.utils;

import com.dhx.bi.model.DO.ChartEntity;
import org.apache.commons.lang3.StringUtils;

/**
 * @author adorabled4
 * @className ChartUtil
 * @date : 2023/08/30/ 11:53
 **/
public class ChartUtil {

    /**
     * 压缩json
     *
     * @param data 数据
     * @return {@link String}
     */
    public static String compressJson(String data) {
        data = data.replaceAll("\t+", "");
        data = data.replaceAll(" +", "");
        data = data.replaceAll("\n+", "");
        return data;
    }

    /**
     * 建立用户输入 (单条消息)
     *
     * @param chart 图表
     * @return {@link String}
     */
    public static String buildUserInput(ChartEntity chart) {
        // 获取CSV
        // 构造用户输入
        StringBuilder userInput = new StringBuilder("");
        // 拼接图表类型;
        String userGoal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += ", 请使用 " + chartType;
        }
        userInput.append("分析需求: ").append('\n');
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        userInput.append(csvData).append("\n");
        return userInput.toString();
    }
}
