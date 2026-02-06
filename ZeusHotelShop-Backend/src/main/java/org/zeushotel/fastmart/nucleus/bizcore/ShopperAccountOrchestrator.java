package org.zeushotel.fastmart.nucleus.bizcore;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeushotel.fastmart.nucleus.dataschema.DiscountVoucherRecord;
import org.zeushotel.fastmart.nucleus.dataschema.ShopperProfileRecord;
import org.zeushotel.fastmart.nucleus.dataschema.ShopperVoucherClaim;
import org.zeushotel.fastmart.nucleus.dbgateway.DiscountVoucherGateway;
import org.zeushotel.fastmart.nucleus.dbgateway.ShopperProfileGateway;
import org.zeushotel.fastmart.nucleus.dbgateway.ShopperVoucherClaimGateway;
import org.zeushotel.fastmart.nucleus.transferobject.ShopperLoginRequest;
import org.zeushotel.fastmart.nucleus.transferobject.ShopperRegisterRequest;
import org.zeushotel.fastmart.nucleus.viewresponse.UnifiedApiResponse;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ShopperAccountOrchestrator {
    
    private final ShopperProfileGateway shopperProfileGateway;
    private final DiscountVoucherGateway discountVoucherGateway;
    private final ShopperVoucherClaimGateway shopperVoucherClaimGateway;
    
    private static final String WELCOME_VOUCHER_CODE = "WELCOME2024";
    
    @Transactional
    public UnifiedApiResponse<String> registerNewShopper(ShopperRegisterRequest request) {
        LambdaQueryWrapper<ShopperProfileRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShopperProfileRecord::getLoginUsername, request.getLoginUsername());
        
        if (shopperProfileGateway.selectCount(queryWrapper) > 0) {
            return UnifiedApiResponse.fail("用户名已被占用");
        }
        
        ShopperProfileRecord profile = new ShopperProfileRecord();
        profile.setLoginUsername(request.getLoginUsername());
        profile.setSecretHash(BCrypt.hashpw(request.getPassword()));
        profile.setDisplayNickname(request.getNickname());
        profile.setContactPhoneNumber(request.getPhoneNumber());
        profile.setEmployeeStatusFlag(0);
        profile.setUserRole(0); // Default to ordinary user
        profile.setAccountActiveFlag(1);
        profile.setDeletionMarker(0);
        
        shopperProfileGateway.insert(profile);
        
        // Auto-claim welcome voucher for new users
        try {
            claimWelcomeVoucher(profile.getProfileIdentifier());
        } catch (Exception e) {
            // Log error but don't fail registration
            System.err.println("Failed to claim welcome voucher for new user: " + e.getMessage());
        }
        
        return UnifiedApiResponse.success("注册成功");
    }
    
    private void claimWelcomeVoucher(Long shopperId) {
        // Find welcome voucher by code
        LambdaQueryWrapper<DiscountVoucherRecord> voucherQuery = new LambdaQueryWrapper<>();
        voucherQuery.eq(DiscountVoucherRecord::getVoucherCode, WELCOME_VOUCHER_CODE);
        voucherQuery.eq(DiscountVoucherRecord::getActiveStatus, 1);
        
        DiscountVoucherRecord welcomeVoucher = discountVoucherGateway.selectOne(voucherQuery);
        
        if (welcomeVoucher == null) {
            return; // Welcome voucher doesn't exist, skip
        }
        
        // Check if user already has this voucher (shouldn't happen for new users but be safe)
        LambdaQueryWrapper<ShopperVoucherClaim> claimQuery = new LambdaQueryWrapper<>();
        claimQuery.eq(ShopperVoucherClaim::getShopperProfileId, shopperId);
        claimQuery.eq(ShopperVoucherClaim::getVoucherId, welcomeVoucher.getVoucherIdentifier());
        
        if (shopperVoucherClaimGateway.selectCount(claimQuery) > 0) {
            return; // User already has this voucher
        }
        
        // Create claim record
        ShopperVoucherClaim claim = new ShopperVoucherClaim();
        claim.setShopperProfileId(shopperId);
        claim.setVoucherId(welcomeVoucher.getVoucherIdentifier());
        claim.setUsageStatus(0);
        claim.setClaimedAtTime(LocalDateTime.now());
        claim.setDeletionMarker(0);
        
        shopperVoucherClaimGateway.insert(claim);
        
        // Update claimed quantity
        welcomeVoucher.setClaimedQuantity(welcomeVoucher.getClaimedQuantity() + 1);
        discountVoucherGateway.updateById(welcomeVoucher);
    }
    
    public UnifiedApiResponse<String> authenticateShopper(ShopperLoginRequest request) {
        LambdaQueryWrapper<ShopperProfileRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShopperProfileRecord::getLoginUsername, request.getLoginUsername());
        
        ShopperProfileRecord profile = shopperProfileGateway.selectOne(queryWrapper);
        
        if (profile == null) {
            return UnifiedApiResponse.fail("用户不存在");
        }
        
        if (!BCrypt.checkpw(request.getPassword(), profile.getSecretHash())) {
            return UnifiedApiResponse.fail("密码错误");
        }
        
        if (profile.getAccountActiveFlag() != 1) {
            return UnifiedApiResponse.fail("账户已被禁用");
        }
        
        StpUtil.login(profile.getProfileIdentifier());
        String authToken = StpUtil.getTokenValue();
        
        return UnifiedApiResponse.success(authToken);
    }
    
    public UnifiedApiResponse<ShopperProfileRecord> fetchShopperProfile(Long profileId) {
        ShopperProfileRecord profile = shopperProfileGateway.selectById(profileId);
        
        if (profile == null) {
            return UnifiedApiResponse.fail("用户不存在");
        }
        
        profile.setSecretHash(null);
        
        return UnifiedApiResponse.success(profile);
    }
    
    public UnifiedApiResponse<String> modifyShopperProfile(Long profileId, ShopperProfileRecord updateData) {
        ShopperProfileRecord profile = shopperProfileGateway.selectById(profileId);
        
        if (profile == null) {
            return UnifiedApiResponse.fail("用户不存在");
        }
        
        if (updateData.getDisplayNickname() != null) {
            profile.setDisplayNickname(updateData.getDisplayNickname());
        }
        
        if (updateData.getPortraitImageLink() != null) {
            profile.setPortraitImageLink(updateData.getPortraitImageLink());
        }
        
        if (updateData.getContactPhoneNumber() != null) {
            profile.setContactPhoneNumber(updateData.getContactPhoneNumber());
        }
        
        shopperProfileGateway.updateById(profile);
        
        return UnifiedApiResponse.success("更新成功");
    }
    
    public UnifiedApiResponse<java.util.List<ShopperProfileRecord>> listAllShoppers() {
        LambdaQueryWrapper<ShopperProfileRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShopperProfileRecord::getAccountActiveFlag, 1);
        queryWrapper.orderByDesc(ShopperProfileRecord::getRecordCreatedTime);
        
        java.util.List<ShopperProfileRecord> shoppers = shopperProfileGateway.selectList(queryWrapper);
        
        // Create a new list with cloned objects to avoid modifying cached entities
        return UnifiedApiResponse.success(
            shoppers.stream().map(shopper -> {
                ShopperProfileRecord safeProfile = new ShopperProfileRecord();
                safeProfile.setProfileIdentifier(shopper.getProfileIdentifier());
                safeProfile.setLoginUsername(shopper.getLoginUsername());
                safeProfile.setDisplayNickname(shopper.getDisplayNickname());
                safeProfile.setPortraitImageLink(shopper.getPortraitImageLink());
                safeProfile.setContactPhoneNumber(shopper.getContactPhoneNumber());
                safeProfile.setEmployeeStatusFlag(shopper.getEmployeeStatusFlag());
                safeProfile.setUserRole(shopper.getUserRole());
                safeProfile.setAccountActiveFlag(shopper.getAccountActiveFlag());
                safeProfile.setRecordCreatedTime(shopper.getRecordCreatedTime());
                // secretHash intentionally omitted for security
                return safeProfile;
            }).collect(java.util.stream.Collectors.toList())
        );
    }
}
