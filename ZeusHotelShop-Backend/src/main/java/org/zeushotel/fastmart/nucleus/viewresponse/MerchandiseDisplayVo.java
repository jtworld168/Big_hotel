package org.zeushotel.fastmart.nucleus.viewresponse;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MerchandiseDisplayVo {
    private Long itemIdentifier;
    private String itemSku;
    private String itemDisplayName;
    private String itemDescription;
    private String categoryLabel;
    private BigDecimal standardPriceYuan;
    private BigDecimal staffDiscountPriceYuan;
    private BigDecimal finalPriceYuan;
    private String mainImageUrl;
    private String additionalImagesJson;
    private Integer stockQuantity;
    private Integer shelfStatus;
    private Boolean employeeEligible;
}
