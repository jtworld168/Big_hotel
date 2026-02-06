package org.zeushotel.fastmart.nucleus.webportal;

import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zeushotel.fastmart.nucleus.bizcore.ShopperAccountOrchestrator;
import org.zeushotel.fastmart.nucleus.dataschema.ShopperProfileRecord;
import org.zeushotel.fastmart.nucleus.transferobject.ShopperLoginRequest;
import org.zeushotel.fastmart.nucleus.transferobject.ShopperRegisterRequest;
import org.zeushotel.fastmart.nucleus.viewresponse.UnifiedApiResponse;

@Tag(name = "购物者账户接入点")
@RestController
@RequestMapping("/gateway/shopper-accounts")
@RequiredArgsConstructor
public class ShopperAccountEndpoint {
    
    private final ShopperAccountOrchestrator orchestrator;
    
    @Operation(summary = "新购物者注册")
    @PostMapping("/register-new")
    public UnifiedApiResponse<String> registerNewAccount(@RequestBody ShopperRegisterRequest request) {
        return orchestrator.registerNewShopper(request);
    }
    
    @Operation(summary = "购物者身份验证")
    @PostMapping("/authenticate")
    public UnifiedApiResponse<String> performAuthentication(@RequestBody ShopperLoginRequest request) {
        return orchestrator.authenticateShopper(request);
    }
    
    @Operation(summary = "获取当前购物者资料")
    @GetMapping("/my-profile")
    public UnifiedApiResponse<ShopperProfileRecord> retrieveCurrentProfile() {
        Long profileId = StpUtil.getLoginIdAsLong();
        return orchestrator.fetchShopperProfile(profileId);
    }
    
    @Operation(summary = "更新购物者资料")
    @PutMapping("/update-profile")
    public UnifiedApiResponse<String> updateCurrentProfile(@RequestBody ShopperProfileRecord updateData) {
        Long profileId = StpUtil.getLoginIdAsLong();
        return orchestrator.modifyShopperProfile(profileId, updateData);
    }
    
    @Operation(summary = "退出登录")
    @PostMapping("/sign-out")
    public UnifiedApiResponse<String> performSignOut() {
        StpUtil.logout();
        return UnifiedApiResponse.success("已退出");
    }
}
