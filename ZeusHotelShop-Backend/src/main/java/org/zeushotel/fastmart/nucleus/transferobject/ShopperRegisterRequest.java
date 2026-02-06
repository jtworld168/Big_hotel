package org.zeushotel.fastmart.nucleus.transferobject;

import lombok.Data;

@Data
public class ShopperRegisterRequest {
    private String loginUsername;
    private String password;
    private String nickname;
    private String phoneNumber;
}
