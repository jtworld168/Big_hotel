package org.zeushotel.fastmart.nucleus.dataschema;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tbl_merchandise_catalog")
public class MerchandiseItem {
    
    @TableId(type = IdType.AUTO)
    private Long itemIdentifier;
    
    private String itemSku;
    
    private String itemDisplayName;
    
    private String itemDescription;
    
    private String categoryLabel;
    
    private BigDecimal standardPriceYuan;
    
    private BigDecimal staffDiscountPriceYuan;
    
    private String mainImageUrl;
    
    private String additionalImagesJson;
    
    private Integer stockQuantity;
    
    private Integer shelfStatus;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime recordCreatedTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime recordUpdatedTime;
    
    @TableLogic
    private Integer deletionMarker;
}
