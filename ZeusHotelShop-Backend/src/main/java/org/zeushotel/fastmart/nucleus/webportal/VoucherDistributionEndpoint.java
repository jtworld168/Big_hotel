package org.zeushotel.fastmart.nucleus.webportal;

import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zeushotel.fastmart.nucleus.bizcore.VoucherDistributionOrchestrator;
import org.zeushotel.fastmart.nucleus.dataschema.DiscountVoucherRecord;
import org.zeushotel.fastmart.nucleus.dataschema.ShopperVoucherClaim;
import org.zeushotel.fastmart.nucleus.viewresponse.UnifiedApiResponse;

import java.util.List;

@Tag(name = "优惠券分发接入点")
@RestController
@RequestMapping("/gateway/vouchers")
@RequiredArgsConstructor
public class VoucherDistributionEndpoint {
    
    private final VoucherDistributionOrchestrator orchestrator;
    
    @Operation(summary = "列出可用优惠券")
    @GetMapping("/available-list")
    public UnifiedApiResponse<List<DiscountVoucherRecord>> listAvailable() {
        return orchestrator.listAvailableVouchers();
    }
    
    @Operation(summary = "领取优惠券")
    @PostMapping("/claim/{voucherId}")
    public UnifiedApiResponse<String> claimVoucher(@PathVariable Long voucherId) {
        Long shopperId = StpUtil.getLoginIdAsLong();
        return orchestrator.claimVoucher(shopperId, voucherId);
    }
    
    @Operation(summary = "查看我的优惠券")
    @GetMapping("/my-collection")
    public UnifiedApiResponse<List<ShopperVoucherClaim>> viewMyVouchers() {
        Long shopperId = StpUtil.getLoginIdAsLong();
        return orchestrator.fetchShopperVouchers(shopperId);
    }
    
    @Operation(summary = "创建优惠券（管理员）")
    @PostMapping("/admin/create-new")
    public UnifiedApiResponse<String> createNewVoucher(@RequestBody DiscountVoucherRecord voucher) {
        return orchestrator.createVoucher(voucher);
    }
}
