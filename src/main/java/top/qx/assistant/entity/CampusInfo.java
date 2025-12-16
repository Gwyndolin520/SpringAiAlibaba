// top.qx.assistant.entity.CampusInfo.java
package top.qx.assistant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("campus_info")
public class CampusInfo {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String category; // library/dining/academic/other
    private String title;
    private String content;
    private String keywords;
    private Integer priority;
    private Integer isActive;
    private String createdBy;
    private String updatedBy;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}