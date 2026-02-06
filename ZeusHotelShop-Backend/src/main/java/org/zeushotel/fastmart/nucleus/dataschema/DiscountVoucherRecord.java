package org.zeushotel.fastmart.nucleus.dataschema;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tbl_discount_vouchers")
public class DiscountVoucherRecord {
    
    @TableId(type = IdType.AUTO)
    private Long voucherIdentifier;
    
    private String voucherCode;
    
    private String voucherTitle;
    
    private Integer voucherType;
    
    private BigDecimal discountAmount;
    
    private BigDecimal minimumPurchase;
    
    private Integer totalIssueQuantity;
    
    private Integer claimedQuantity;
    
    private LocalDateTime validFromTime;
    
    private LocalDateTime validUntilTime;
    
    private Integer activeStatus;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime recordCreatedTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime recordUpdatedTime;
    
    @TableLogic
    private Integer deletionMarker;
}
