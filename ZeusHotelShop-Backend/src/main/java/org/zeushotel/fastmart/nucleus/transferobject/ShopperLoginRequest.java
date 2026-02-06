package org.zeushotel.fastmart.nucleus.transferobject;

import lombok.Data;

@Data
public class ShopperLoginRequest {
    private String loginUsername;
    private String password;
}
