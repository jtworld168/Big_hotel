package org.zeushotel.fastmart.nucleus.dataschema;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tbl_shopper_voucher_claims")
public class ShopperVoucherClaim {
    
    @TableId(type = IdType.AUTO)
    private Long claimIdentifier;
    
    private Long shopperProfileId;
    
    private Long voucherId;
    
    private Integer usageStatus;
    
    private LocalDateTime claimedAtTime;
    
    private LocalDateTime usedAtTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime recordCreatedTime;
    
    @TableLogic
    private Integer deletionMarker;
}
