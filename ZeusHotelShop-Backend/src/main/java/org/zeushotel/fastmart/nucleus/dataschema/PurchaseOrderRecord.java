package org.zeushotel.fastmart.nucleus.dataschema;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tbl_purchase_orders")
public class PurchaseOrderRecord {
    
    @TableId(type = IdType.AUTO)
    private Long orderIdentifier;
    
    private String orderNumber;
    
    private Long shopperProfileId;
    
    private BigDecimal totalAmountYuan;
    
    private BigDecimal discountAmountYuan;
    
    private BigDecimal finalPaymentYuan;
    
    private Integer paymentMethod;
    
    private String paymentTransactionId;
    
    private Integer orderStatus;
    
    private String orderItemsJson;
    
    private Long appliedVoucherId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime recordCreatedTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime recordUpdatedTime;
    
    @TableLogic
    private Integer deletionMarker;
}
