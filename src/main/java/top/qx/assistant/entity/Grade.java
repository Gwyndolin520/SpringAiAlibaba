// top.qx.assistant.entity.Grade.java
package top.qx.assistant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("grades")
public class Grade {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String studentId;
    private String courseCode;
    private String courseName;
    private String semester;
    private String scoreType;
    private Float score;
    private Float credit;
    private Float gpa;
    private Integer isPassed;
    private LocalDate examDate;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}