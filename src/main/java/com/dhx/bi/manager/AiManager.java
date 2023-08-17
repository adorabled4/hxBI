package com.dhx.bi.manager;

import com.dhx.bi.common.ErrorCode;
import com.dhx.bi.common.constant.AIConstant;
import com.dhx.bi.common.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author adorabled4
 * @className AiManager
 * @date : 2023/07/07/ 13:30
 **/
@Component
@Slf4j
public class AiManager {

    @Resource
    YuCongMingClient yuClient;


    /**
     * chat
     *
     * @param message 消息
     */
    public String doChat(String message, Long modelId) {
        DevChatRequest chatRequest = new DevChatRequest();
        chatRequest.setMessage(message);
        chatRequest.setModelId(modelId);
        BaseResponse<DevChatResponse> response = yuClient.doChat(chatRequest);
        if (response == null) {
            log.error("AI 响应错误! 发送内容:{}", message);
        }
        if (response.getCode() != 0) {
            log.error("AI 响应错误! 发送内容:{}, 返回内容:{}", message, response.getData());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, response.getMessage() + ": " + message.length());
        }
        return response.getData().getContent();
    }

    /**
     * 聊天和创图
     *
     * @param goal      目标
     * @param chartType 图表类型
     * @param csvData   csv数据
     * @return {@link String}
     */
    public String chatAndGenChart(String goal, String chartType, String csvData) {
        // 1.计算需要发送的总的消息数 : 1(prompt)+n(data)
        int totalLen = 100 + csvData.length();
        int messageNum = totalLen / 960;
        StringBuilder input = new StringBuilder();
        input.append("你是一个数据分析师和前端开发专家，接下来我会提供给你用「@」编号的内容，格式为@{序号}=<内容>，请先记住，但不要进行分析，可以吗？").append("\n")
                .append("分析需求：").append(goal).append("\n")
                .append("请使用 ").append(chartType).append("\n")
                .append("原始数据");
        String chatResult = doChat(input.toString(), AIConstant.BI_MODEL_ID);
        int msgIdx = 0;
        String[] strings = splitStr(csvData, 960);
        while (msgIdx < messageNum) {
            String msg;
            // 构造消息
            if (msgIdx == messageNum - 1) {
                msg = "@" + (msgIdx + 1) + strings[msgIdx] + ">数据提供完成,请根据以上内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
                        "【【【【【\n" +
                        "{前端Echarts V5的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
                        "【【【【【\n" +
                        "{明确的数据分析结论、越详细越好，不要生成多余的注释}\n";
            } else {
                msg = "@" + (msgIdx + 1) + strings[msgIdx] + ">接下来还有数据, 请不要开始分析";
            }

            msgIdx++;
            chatResult = doChat(msg, AIConstant.BI_MODEL_ID);
            log.info("第{}次chat , 发送内容:{} ,\n 返回结果:{}", msgIdx, msg, chatResult);
        }
        return chatResult;
    }

    /**
     * str分割
     *
     * @param str    str
     * @param length 长度
     * @return {@link String[]}
     */
    private static String[] splitStr(String str, int length) {
        String[] res;
        if (str.length() % length == 0) {
            res = new String[str.length() / length];
        } else {
            res = new String[str.length() / length + 1];
        }
        int idx = 0;
        for (int i = 0; i < str.length(); i += length) {
            int endIndex = Math.min(i + length, str.length());
            String subString = str.substring(i, endIndex);
            res[idx++] = subString;
        }
        return res;
    }

}
