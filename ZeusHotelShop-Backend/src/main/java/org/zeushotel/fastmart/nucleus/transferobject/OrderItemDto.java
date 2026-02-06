package org.zeushotel.fastmart.nucleus.transferobject;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private Long itemId;
    private String itemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
