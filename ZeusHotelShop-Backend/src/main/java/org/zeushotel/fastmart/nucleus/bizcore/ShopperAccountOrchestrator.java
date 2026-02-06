package org.zeushotel.fastmart.nucleus.bizcore;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zeushotel.fastmart.nucleus.dataschema.ShopperProfileRecord;
import org.zeushotel.fastmart.nucleus.dbgateway.ShopperProfileGateway;
import org.zeushotel.fastmart.nucleus.transferobject.ShopperLoginRequest;
import org.zeushotel.fastmart.nucleus.transferobject.ShopperRegisterRequest;
import org.zeushotel.fastmart.nucleus.viewresponse.UnifiedApiResponse;

@Service
@RequiredArgsConstructor
public class ShopperAccountOrchestrator {
    
    private final ShopperProfileGateway shopperProfileGateway;
    
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
        
        return UnifiedApiResponse.success("注册成功");
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
        
        // Remove sensitive information
        shoppers.forEach(shopper -> shopper.setSecretHash(null));
        
        return UnifiedApiResponse.success(shoppers);
    }
}
