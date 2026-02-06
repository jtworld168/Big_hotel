package org.zeushotel.fastmart.nucleus.dataschema;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tbl_shopping_baskets")
public class ShoppingBasketItem {
    
    @TableId(type = IdType.AUTO)
    private Long basketItemId;
    
    private Long shopperProfileId;
    
    private Long merchandiseItemId;
    
    private Integer selectedQuantity;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime recordCreatedTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime recordUpdatedTime;
}
