package top.qx.assistant.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.qx.assistant.tool.CampusInfoTool;
import top.qx.assistant.tool.CourseQueryTool;
import top.qx.assistant.tool.LibrarySeatTool;
import top.qx.assistant.tool.WeatherTool;

/**
 * @author mqxu
 * @date 2025/11/26
 * @description Agent 配置类
 **/
@Configuration
public class AgentConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    private static final String SYSTEM_PROMPT = """
            你是一个贴心的校园智能助手"小椰"，专门服务大学生群体。
            
            你的职责包括：
            1. 查询天气信息，帮助学生决定穿衣和出行
            2. 查询课程表，提醒学生上课时间地点
            3. 查询图书馆座位情况，帮助学生找到自习位置
            4. 回答各类校园生活问题
            
            行为准则：
            - 使用轻松友好的语气，像学长学姐一样亲切
            - 回答要具体实用，不要空泛
            - 主动提供相关建议
            - 不确定的信息要说明清楚
            - 适当使用 emoji 让对话更生动
            
            工具使用规则：
            - 查询天气：当学生提到天气、温度、穿衣建议时使用 getWeather 工具
            - 查询课程：当学生问课程、课表、上课地点时使用 getCourseInfo 工具（需要学号）
            - 查询图书馆：当学生问图书馆、自习、座位时使用 getLibrarySeat 工具
            - 校园信息：其他校园相关问题使用 getCampusInfo 工具
            """;

    @Bean
    public DashScopeApi dashScopeApi() {
        return DashScopeApi.builder()
                .apiKey(apiKey)
                .build();
    }


    @Bean
    public DashScopeChatModel chatModel(DashScopeApi dashScopeApi) {
        return DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .build();
    }


    @Bean
    public ToolCallback campusInfoToolCallback(CampusInfoTool campusInfoTool) {
        return FunctionToolCallback
                .builder("getCampusInfo", campusInfoTool)
                .description("查询校园相关信息，如图书馆、食堂、教务处等")
                .inputType(String.class)
                .build();
    }

    @Bean
    public ToolCallback weatherToolToolCallback(WeatherTool weatherTool) {
        return FunctionToolCallback
                .builder("getWeather", weatherTool)
                .description("查询天气信息，如温度、天气情况、穿衣建议")
                .inputType(String.class)
                .build();
    }

    @Bean
    public ToolCallback courseQueryToolCallback(CourseQueryTool courseQueryTool) {
        return FunctionToolCallback
                .builder("getCourseInfo", courseQueryTool)
                .description("查询课程信息，如课程名称、时间、地点")
                .inputType(String.class)
                .build();
    }

    @Bean
    public ToolCallback librarySeatToolCallback(LibrarySeatTool librarySeatTool) {
        return FunctionToolCallback
                .builder("getLibrarySeat", librarySeatTool)
                .description("查询图书馆座位情况，如空闲座位数、位置等")
                .inputType(String.class)
                .build();
    }

    @Bean
    public ReactAgent reactAgent(DashScopeChatModel chatModel,
                                 ToolCallback campusInfoToolCallback,
                                 ToolCallback weatherToolToolCallback,
                                 ToolCallback courseQueryToolCallback,
                                 ToolCallback librarySeatToolCallback) {
        return ReactAgent.builder()
                .name("campus_assistant")
                .model(chatModel)
                .systemPrompt(SYSTEM_PROMPT)
                .tools(campusInfoToolCallback, weatherToolToolCallback, courseQueryToolCallback, librarySeatToolCallback)
                .saver(new MemorySaver())
                .build();
    }
}