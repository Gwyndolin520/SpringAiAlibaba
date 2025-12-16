// top.qx.assistant.tool.CampusCardTool.java
package top.qx.assistant.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiFunction;

/**
 * @author mqxu
 * @date 2025/11/26
 * @description 校园一卡通查询工具类
 **/
@Component
public class CampusCardTool implements BiFunction<String, ToolContext, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 模拟一卡通数据
    private final Map<String, Map<String, Object>> cardDatabase = new HashMap<>();

    public CampusCardTool() {
        initMockData();
    }

    private void initMockData() {
        // 学生20220001的一卡通信息
        cardDatabase.put("20220001", Map.of(
                "balance", 156.78,
                "cardNumber", "10010020220001",
                "status", "正常",
                "lastRecharge", Map.of(
                        "amount", 100.0,
                        "time", "2025-11-25 14:30:00",
                        "method", "微信支付"
                ),
                "todayConsumption", Arrays.asList(
                        Map.of("time", "07:30", "place", "第一食堂", "amount", 6.5, "type", "早餐"),
                        Map.of("time", "12:15", "place", "第二食堂", "amount", 12.0, "type", "午餐"),
                        Map.of("time", "18:45", "place", "校园超市", "amount", 8.5, "type", "购物")
                ),
                "recentTransactions", Arrays.asList(
                        Map.of("date", "2025-11-25", "description", "食堂消费", "amount", -12.0),
                        Map.of("date", "2025-11-25", "description", "微信充值", "amount", 100.0),
                        Map.of("date", "2025-11-24", "description", "图书馆打印", "amount", -3.5),
                        Map.of("date", "2025-11-24", "description", "浴室消费", "amount", -5.0)
                )
        ));

        // 学生20220002的一卡通信息
        cardDatabase.put("20220002", Map.of(
                "balance", 42.30,
                "cardNumber", "10010020220002",
                "status", "正常",
                "lastRecharge", Map.of(
                        "amount", 50.0,
                        "time", "2025-11-24 09:15:00",
                        "method", "支付宝"
                ),
                "todayConsumption", Arrays.asList(
                        Map.of("time", "07:45", "place", "第三食堂", "amount", 5.0, "type", "早餐"),
                        Map.of("time", "12:30", "place", "第一食堂", "amount", 10.5, "type", "午餐")
                ),
                "recentTransactions", Arrays.asList(
                        Map.of("date", "2025-11-24", "description", "食堂消费", "amount", -10.5),
                        Map.of("date", "2025-11-24", "description", "支付宝充值", "amount", 50.0),
                        Map.of("date", "2025-11-23", "description", "超市购物", "amount", -15.2)
                )
        ));
    }

    @Override
    public String apply(
            @ToolParam(description = "查询一卡通余额、消费记录、充值记录等") String query,
            ToolContext toolContext) {

        try {
            String studentId = extractStudentId(query);
            String queryType = detectQueryType(query);

            Map<String, Object> cardInfo = cardDatabase.get(studentId);
            if (cardInfo == null) {
                return "未找到学号为 " + studentId + " 的一卡通信息";
            }

            switch (queryType) {
                case "balance":
                    return formatBalanceInfo(cardInfo, studentId);
                case "consumption":
                    return formatConsumptionInfo(cardInfo, studentId);
                case "transactions":
                    return formatTransactionsInfo(cardInfo, studentId);
                case "all":
                default:
                    return objectMapper.writeValueAsString(cardInfo);
            }
        } catch (Exception e) {
            return "查询一卡通信息时出现错误：" + e.getMessage();
        }
    }

    private String extractStudentId(String query) {
        // 实际应用中可以从上下文中获取或从query中提取
        if (query.contains("20220001")) return "20220001";
        if (query.contains("20220002")) return "20220002";
        return "20220001"; // 默认
    }

    private String detectQueryType(String query) {
        query = query.toLowerCase();
        if (query.contains("余额")) return "balance";
        if (query.contains("消费") || query.contains("流水")) return "consumption";
        if (query.contains("交易") || query.contains("记录")) return "transactions";
        return "all";
    }

    private String formatBalanceInfo(Map<String, Object> cardInfo, String studentId) {
        double balance = (double) cardInfo.get("balance");
        String cardNumber = (String) cardInfo.get("cardNumber");
        String status = (String) cardInfo.get("status");

        return String.format("一卡通信息：\n" +
                        "• 学号：%s\n" +
                        "• 卡号：%s\n" +
                        "• 当前余额：%.2f元\n" +
                        "• 卡片状态：%s\n" +
                        "• 查询时间：%s",
                studentId, cardNumber, balance, status, new Date());
    }

    private String formatConsumptionInfo(Map<String, Object> cardInfo, String studentId) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> todayConsumption =
                (List<Map<String, Object>>) cardInfo.get("todayConsumption");

        StringBuilder sb = new StringBuilder();
        sb.append("今日消费记录：\n");

        double total = 0;
        for (Map<String, Object> record : todayConsumption) {
            sb.append(String.format("• %s %s：%.2f元（%s）\n",
                    record.get("time"),
                    record.get("place"),
                    record.get("amount"),
                    record.get("type")));
            total += (double) record.get("amount");
        }

        sb.append(String.format("\n今日总消费：%.2f元", total));
        return sb.toString();
    }

    private String formatTransactionsInfo(Map<String, Object> cardInfo, String studentId) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> transactions =
                (List<Map<String, Object>>) cardInfo.get("recentTransactions");

        StringBuilder sb = new StringBuilder();
        sb.append("近期交易记录：\n");

        for (Map<String, Object> transaction : transactions) {
            double amount = (double) transaction.get("amount");
            String sign = amount >= 0 ? "+" : "";
            sb.append(String.format("• %s %s：%s%.2f元\n",
                    transaction.get("date"),
                    transaction.get("description"),
                    sign, amount));
        }

        return sb.toString();
    }
}