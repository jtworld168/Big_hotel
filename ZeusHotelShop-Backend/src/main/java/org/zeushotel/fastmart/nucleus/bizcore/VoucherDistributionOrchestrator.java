package org.zeushotel.fastmart.nucleus.bizcore;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeushotel.fastmart.nucleus.dataschema.DiscountVoucherRecord;
import org.zeushotel.fastmart.nucleus.dataschema.ShopperVoucherClaim;
import org.zeushotel.fastmart.nucleus.dbgateway.DiscountVoucherGateway;
import org.zeushotel.fastmart.nucleus.dbgateway.ShopperVoucherClaimGateway;
import org.zeushotel.fastmart.nucleus.viewresponse.UnifiedApiResponse;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherDistributionOrchestrator {
    
    private final DiscountVoucherGateway discountVoucherGateway;
    private final ShopperVoucherClaimGateway shopperVoucherClaimGateway;
    
    public UnifiedApiResponse<List<DiscountVoucherRecord>> listAvailableVouchers() {
        LambdaQueryWrapper<DiscountVoucherRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DiscountVoucherRecord::getActiveStatus, 1);
        queryWrapper.le(DiscountVoucherRecord::getValidFromTime, LocalDateTime.now());
        queryWrapper.ge(DiscountVoucherRecord::getValidUntilTime, LocalDateTime.now());
        queryWrapper.apply("total_issue_quantity > claimed_quantity");
        
        List<DiscountVoucherRecord> vouchers = discountVoucherGateway.selectList(queryWrapper);
        
        return UnifiedApiResponse.success(vouchers);
    }
    
    @Transactional
    public UnifiedApiResponse<String> claimVoucher(Long shopperId, Long voucherId) {
        DiscountVoucherRecord voucher = discountVoucherGateway.selectById(voucherId);
        
        if (voucher == null) {
            return UnifiedApiResponse.fail("优惠券不存在");
        }
        
        if (voucher.getActiveStatus() != 1) {
            return UnifiedApiResponse.fail("优惠券已失效");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getValidFromTime()) || now.isAfter(voucher.getValidUntilTime())) {
            return UnifiedApiResponse.fail("不在有效期内");
        }
        
        if (voucher.getClaimedQuantity() >= voucher.getTotalIssueQuantity()) {
            return UnifiedApiResponse.fail("优惠券已被领完");
        }
        
        LambdaQueryWrapper<ShopperVoucherClaim> claimQuery = new LambdaQueryWrapper<>();
        claimQuery.eq(ShopperVoucherClaim::getShopperProfileId, shopperId);
        claimQuery.eq(ShopperVoucherClaim::getVoucherId, voucherId);
        
        if (shopperVoucherClaimGateway.selectCount(claimQuery) > 0) {
            return UnifiedApiResponse.fail("您已领取过此优惠券");
        }
        
        ShopperVoucherClaim claim = new ShopperVoucherClaim();
        claim.setShopperProfileId(shopperId);
        claim.setVoucherId(voucherId);
        claim.setUsageStatus(0);
        claim.setClaimedAtTime(LocalDateTime.now());
        claim.setDeletionMarker(0);
        
        shopperVoucherClaimGateway.insert(claim);
        
        voucher.setClaimedQuantity(voucher.getClaimedQuantity() + 1);
        discountVoucherGateway.updateById(voucher);
        
        return UnifiedApiResponse.success("领取成功");
    }
    
    public UnifiedApiResponse<List<ShopperVoucherClaim>> fetchShopperVouchers(Long shopperId) {
        LambdaQueryWrapper<ShopperVoucherClaim> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShopperVoucherClaim::getShopperProfileId, shopperId);
        queryWrapper.orderByDesc(ShopperVoucherClaim::getClaimedAtTime);
        
        List<ShopperVoucherClaim> claims = shopperVoucherClaimGateway.selectList(queryWrapper);
        
        return UnifiedApiResponse.success(claims);
    }
    
    public UnifiedApiResponse<String> createVoucher(DiscountVoucherRecord voucher) {
        voucher.setClaimedQuantity(0);
        voucher.setActiveStatus(1);
        voucher.setDeletionMarker(0);
        
        discountVoucherGateway.insert(voucher);
        
        return UnifiedApiResponse.success("创建成功");
    }
}
