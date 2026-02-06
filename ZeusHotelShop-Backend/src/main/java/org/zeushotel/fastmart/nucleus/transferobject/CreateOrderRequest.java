package org.zeushotel.fastmart.nucleus.transferobject;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private List<OrderItemDto> orderItems;
    private Long voucherId;
    private Integer paymentMethod;
}
