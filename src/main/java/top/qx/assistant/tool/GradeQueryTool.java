// top.qx.assistant.tool.GradeQueryTool.java
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
 * @description 成绩查询工具类
 **/
@Component
public class GradeQueryTool implements BiFunction<String, ToolContext, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 模拟学生成绩数据
    private final Map<String, List<Map<String, Object>>> gradeDatabase = new HashMap<>();

    public GradeQueryTool() {
        // 初始化模拟数据
        initMockData();
    }

    private void initMockData() {
        // 学生20220001的成绩
        gradeDatabase.put("20220001", Arrays.asList(
                Map.of(
                        "courseName", "高等数学",
                        "credit", 4.0,
                        "score", 88,
                        "gradePoint", 3.7,
                        "semester", "2024-2025-1",
                        "status", "已通过"
                ),
                Map.of(
                        "courseName", "大学英语",
                        "credit", 3.0,
                        "score", 92,
                        "gradePoint", 4.0,
                        "semester", "2024-2025-1",
                        "status", "已通过"
                ),
                Map.of(
                        "courseName", "计算机基础",
                        "credit", 2.5,
                        "score", 85,
                        "gradePoint", 3.5,
                        "semester", "2024-2025-1",
                        "status", "已通过"
                )
        ));

        // 学生20220002的成绩
        gradeDatabase.put("20220002", Arrays.asList(
                Map.of(
                        "courseName", "高等数学",
                        "credit", 4.0,
                        "score", 78,
                        "gradePoint", 2.8,
                        "semester", "2024-2025-1",
                        "status", "已通过"
                ),
                Map.of(
                        "courseName", "大学英语",
                        "credit", 3.0,
                        "score", 82,
                        "gradePoint", 3.2,
                        "semester", "2024-2025-1",
                        "status", "已通过"
                )
        ));
    }

    @Override
    public String apply(
            @ToolParam(description = "学生学号或查询参数，如：查询成绩、查看绩点等") String query,
            ToolContext toolContext) {

        try {
            // 尝试解析是否为JSON格式的查询
            if (query.contains("studentId") || query.contains("学号")) {
                String studentId;
                if (query.contains("{")) {
                    // JSON格式：{"studentId":"20220001"}
                    Map<String, String> queryMap = objectMapper.readValue(query, Map.class);
                    studentId = queryMap.get("studentId");
                } else {
                    // 简单文本格式
                    studentId = extractStudentId(query);
                }

                return queryGrades(studentId);
            } else {
                return getGradeSummary(query);
            }
        } catch (Exception e) {
            return "查询成绩时出现错误：" + e.getMessage();
        }
    }

    private String extractStudentId(String query) {
        // 简单提取学号，实际应用中可以更复杂
        if (query.contains("20220001")) return "20220001";
        if (query.contains("20220002")) return "20220002";
        return "20220001"; // 默认返回第一个学生
    }

    private String queryGrades(String studentId) {
        List<Map<String, Object>> grades = gradeDatabase.get(studentId);

        if (grades == null || grades.isEmpty()) {
            return "未找到学号为 " + studentId + " 的成绩信息";
        }

        try {
            Map<String, Object> result = new HashMap<>();
            result.put("studentId", studentId);
            result.put("grades", grades);
            result.put("summary", calculateSummary(grades));
            result.put("timestamp", new Date().toString());

            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            return "生成成绩单时出错：" + e.getMessage();
        }
    }

    private String getGradeSummary(String query) {
        // 查询绩点、平均分等概要信息
        String studentId = "20220001"; // 默认学生
        List<Map<String, Object>> grades = gradeDatabase.get(studentId);

        if (grades == null || grades.isEmpty()) {
            return "暂无成绩信息";
        }

        Map<String, Object> summary = calculateSummary(grades);

        return String.format("学号 %s 的成绩概要：\n" +
                        "• 已修课程：%d门\n" +
                        "• 平均成绩：%.1f分\n" +
                        "• 平均绩点：%.2f\n" +
                        "• 总学分：%.1f\n" +
                        "• 最高分：%d分（%s）",
                studentId,
                summary.get("courseCount"),
                summary.get("averageScore"),
                summary.get("averageGradePoint"),
                summary.get("totalCredit"),
                summary.get("highestScore"),
                summary.get("highestCourse"));
    }

    private Map<String, Object> calculateSummary(List<Map<String, Object>> grades) {
        double totalScore = 0;
        double totalGradePoint = 0;
        double totalCredit = 0;
        int highestScore = 0;
        String highestCourse = "";

        for (Map<String, Object> grade : grades) {
            int score = (int) grade.get("score");
            double gradePoint = (double) grade.get("gradePoint");
            double credit = (double) grade.get("credit");

            totalScore += score;
            totalGradePoint += gradePoint * credit;
            totalCredit += credit;

            if (score > highestScore) {
                highestScore = score;
                highestCourse = (String) grade.get("courseName");
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("courseCount", grades.size());
        summary.put("averageScore", totalScore / grades.size());
        summary.put("averageGradePoint", totalGradePoint / totalCredit);
        summary.put("totalCredit", totalCredit);
        summary.put("highestScore", highestScore);
        summary.put("highestCourse", highestCourse);

        return summary;
    }
}