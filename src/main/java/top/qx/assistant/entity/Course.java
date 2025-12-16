// top.qx.assistant.entity.Course.java
package top.qx.assistant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("courses")
public class Course {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String studentId;
    private String courseCode;
    private String courseName;
    private String teacher;
    private Float credit;
    private Integer weekDay;
    private Integer startWeek;
    private Integer endWeek;
    private Integer startSection;
    private Integer endSection;
    private String location;
    private String semester;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}