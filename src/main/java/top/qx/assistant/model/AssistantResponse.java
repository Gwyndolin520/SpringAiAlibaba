package top.qx.assistant.model;

import lombok.Data;

/**
 * @author mqxu
 * @date 2025/11/26
 * @description 结构化输出类
 **/
@Data
public class AssistantResponse {
    // 用户id
    private String userId;
    // 线程id
    private String threadId;
    // 主要回答内容
    private String answer;
    // 相关建议
    private String suggestion;
    // 回答类型：weather/course/library/general
    private String type;
    // 是否需要进一步帮助
    private boolean needsFurtherHelp;
}