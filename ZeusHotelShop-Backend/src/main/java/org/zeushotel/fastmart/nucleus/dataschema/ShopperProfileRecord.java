package org.zeushotel.fastmart.nucleus.dataschema;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tbl_shopper_profiles")
public class ShopperProfileRecord {
    
    @TableId(type = IdType.AUTO)
    private Long profileIdentifier;
    
    private String loginUsername;
    
    private String secretHash;
    
    private String displayNickname;
    
    private String portraitImageLink;
    
    private String contactPhoneNumber;
    
    private Integer employeeStatusFlag;
    
    private Integer userRole;
    
    private Integer accountActiveFlag;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime recordCreatedTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime recordUpdatedTime;
    
    @TableLogic
    private Integer deletionMarker;
}
