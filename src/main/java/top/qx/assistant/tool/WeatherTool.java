package top.qx.assistant.tool;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

/**
 * @author mqxu
 * @date 2025/11/26
 * @description 天气查询工具
 **/
@Component
public class WeatherTool implements BiFunction<String, ToolContext, String> {

    @Override
    public String apply(
            @ToolParam(description = "城市名称，如：北京、上海、南京") String city,
            ToolContext toolContext) {
        // 实际项目中这里应该调用真实的天气API
        // 这里使用模拟数据
        return String.format("{ \"city\": \"%s\", \"weather\": \"晴天\", \"temperature\": \"22°C\", \"advice\": \"适合出行，建议穿长袖\" }", city);
    }
}