package org.zeushotel.fastmart.nucleus.viewresponse;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BasketItemVo {
    private Long basketItemId;
    private Long itemIdentifier;
    private String itemDisplayName;
    private String mainImageUrl;
    private BigDecimal standardPriceYuan;
    private BigDecimal staffDiscountPriceYuan;
    private Integer selectedQuantity;
    private Integer stockQuantity;
}
