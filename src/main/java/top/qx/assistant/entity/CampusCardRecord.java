// top.qx.assistant.entity.CampusCardRecord.java
package top.qx.assistant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("campus_card_records")
public class CampusCardRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String studentId;
    private String cardNo;
    private Integer transactionType; // 1-消费 2-充值 3-补助
    private BigDecimal amount;
    private BigDecimal balance;
    private String location;
    private String deviceNo;
    private LocalDateTime transactionTime;
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}