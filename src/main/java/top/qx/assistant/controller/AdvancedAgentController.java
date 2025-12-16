// top.qx.assistant.controller.AdvancedAgentController.java (更新部分)
package top.qx.assistant.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.web.bind.annotation.*;
import top.qx.assistant.model.AssistantResponse;
import top.qx.assistant.model.RequestDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author mqxu
 * @date 2025/11/26
 * @description 带记忆功能的 Controller
 **/
@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdvancedAgentController {

    private final ReactAgent reactAgent;
    private final DashScopeChatModel chatModel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 存储用户的 threadId 映射
    private final Map<String, String> userThreadMap = new HashMap<>();

    @PostMapping("/chat")
    public AssistantResponse chat(@RequestBody RequestDTO request) {
        String userId = request.getUserId();
        String message = request.getMessage();

        // 为每个用户生成或获取 threadId
        String threadId = userThreadMap.computeIfAbsent(userId, k -> UUID.randomUUID().toString());

        // 创建配置
        RunnableConfig config = RunnableConfig.builder()
                .threadId(threadId)
                .addMetadata("user_id", userId)
                .build();

        // 调用 Agent，得到 AssistantMessage
        AssistantMessage response;
        try {
            response = reactAgent.call(message, config);
        } catch (GraphRunnerException e) {
            throw new RuntimeException(e);
        }

        // 解析响应，确定类型
        String responseText = response.getText();
        String type = detectResponseType(responseText, message);
        String suggestion = generateSuggestion(type, responseText);

        // 构建返回结果
        AssistantResponse assistantResponse = new AssistantResponse();
        assistantResponse.setUserId(userId);
        assistantResponse.setThreadId(threadId);
        assistantResponse.setAnswer(responseText);
        assistantResponse.setType(type);
        assistantResponse.setSuggestion(suggestion);
        assistantResponse.setNeedsFurtherHelp(shouldNeedFurtherHelp(type, responseText));

        return assistantResponse;
    }

    private String detectResponseType(String response, String originalQuery) {
        originalQuery = originalQuery.toLowerCase();
        response = response.toLowerCase();

        if (originalQuery.contains("成绩") || response.contains("成绩") || response.contains("绩点")) {
            return "grade";
        } else if (originalQuery.contains("一卡通") || originalQuery.contains("校园卡") ||
                response.contains("一卡通") || response.contains("校园卡")) {
            return "card";
        } else if (originalQuery.contains("课表") || originalQuery.contains("课程") ||
                response.contains("课表") || response.contains("课程")) {
            return "course";
        } else if (originalQuery.contains("天气") || response.contains("天气")) {
            return "weather";
        } else if (originalQuery.contains("图书馆") || originalQuery.contains("食堂") ||
                originalQuery.contains("教务处") || originalQuery.contains("校园")) {
            return "campus";
        } else {
            return "general";
        }
    }

    private String generateSuggestion(String type, String response) {
        switch (type) {
            case "grade":
                return "你可以继续查询：详细成绩单、绩点计算、不及格科目等";
            case "card":
                return "你可以：查看消费详情、查询充值记录、挂失校园卡等";
            case "course":
                return "你可以：查看周课表、查询教室位置、调课通知等";
            case "weather":
                return "你可以：查询未来三天天气、穿衣建议、空气质量等";
            case "campus":
                return "你可以：查询校园地图、各部门联系方式、校园活动等";
            default:
                return "请根据你的需求，选择相关功能或继续提问";
        }
    }

    private boolean shouldNeedFurtherHelp(String type, String response) {
        // 根据响应内容决定是否需要进一步帮助
        return response.contains("无法") || response.contains("错误") ||
                response.contains("抱歉") || response.contains("暂时");
    }

    @GetMapping("/history/{userId}")
    public Map<String, Object> getHistory(@PathVariable String userId) {
        String threadId = userThreadMap.get(userId);
        if (threadId == null) {
            return Map.of("error", "未找到该用户的历史记录");
        }

        // 这里可以扩展为从持久化存储中获取历史记录
        return Map.of(
                "userId", userId,
                "threadId", threadId,
                "history", "历史记录功能需要进一步实现"
        );
    }
}