package org.zeushotel.fastmart.nucleus.bizcore;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zeushotel.fastmart.nucleus.dataschema.MerchandiseItem;
import org.zeushotel.fastmart.nucleus.dataschema.ShopperProfileRecord;
import org.zeushotel.fastmart.nucleus.dbgateway.MerchandiseItemGateway;
import org.zeushotel.fastmart.nucleus.dbgateway.ShopperProfileGateway;
import org.zeushotel.fastmart.nucleus.viewresponse.UnifiedApiResponse;
import org.zeushotel.fastmart.nucleus.viewresponse.MerchandiseDisplayVo;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchandiseCatalogOrchestrator {
    
    private final MerchandiseItemGateway merchandiseItemGateway;
    private final ShopperProfileGateway shopperProfileGateway;
    
    public UnifiedApiResponse<Page<MerchandiseDisplayVo>> browseMerchandise(Integer pageNum, Integer pageSize, String categoryLabel, Long shopperId) {
        Page<MerchandiseItem> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MerchandiseItem> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(MerchandiseItem::getShelfStatus, 1);
        
        if (categoryLabel != null && !categoryLabel.isEmpty()) {
            queryWrapper.eq(MerchandiseItem::getCategoryLabel, categoryLabel);
        }
        
        queryWrapper.orderByDesc(MerchandiseItem::getRecordCreatedTime);
        
        Page<MerchandiseItem> resultPage = merchandiseItemGateway.selectPage(page, queryWrapper);
        
        boolean isEmployee = false;
        if (shopperId != null) {
            ShopperProfileRecord shopper = shopperProfileGateway.selectById(shopperId);
            if (shopper != null && shopper.getEmployeeStatusFlag() == 1) {
                isEmployee = true;
            }
        }
        
        final boolean employeeFlag = isEmployee;
        
        Page<MerchandiseDisplayVo> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<MerchandiseDisplayVo> voList = resultPage.getRecords().stream()
                .map(item -> convertToDisplayVo(item, employeeFlag))
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return UnifiedApiResponse.success(voPage);
    }
    
    public UnifiedApiResponse<MerchandiseDisplayVo> fetchMerchandiseDetails(Long itemId, Long shopperId) {
        MerchandiseItem item = merchandiseItemGateway.selectById(itemId);
        
        if (item == null) {
            return UnifiedApiResponse.fail("商品不存在");
        }
        
        boolean isEmployee = false;
        if (shopperId != null) {
            ShopperProfileRecord shopper = shopperProfileGateway.selectById(shopperId);
            if (shopper != null && shopper.getEmployeeStatusFlag() == 1) {
                isEmployee = true;
            }
        }
        
        MerchandiseDisplayVo vo = convertToDisplayVo(item, isEmployee);
        
        return UnifiedApiResponse.success(vo);
    }
    
    private MerchandiseDisplayVo convertToDisplayVo(MerchandiseItem item, boolean isEmployee) {
        MerchandiseDisplayVo vo = new MerchandiseDisplayVo();
        vo.setItemIdentifier(item.getItemIdentifier());
        vo.setItemSku(item.getItemSku());
        vo.setItemDisplayName(item.getItemDisplayName());
        vo.setItemDescription(item.getItemDescription());
        vo.setCategoryLabel(item.getCategoryLabel());
        vo.setStandardPriceYuan(item.getStandardPriceYuan());
        vo.setStaffDiscountPriceYuan(item.getStaffDiscountPriceYuan());
        vo.setMainImageUrl(item.getMainImageUrl());
        vo.setAdditionalImagesJson(item.getAdditionalImagesJson());
        vo.setStockQuantity(item.getStockQuantity());
        vo.setShelfStatus(item.getShelfStatus());
        vo.setEmployeeEligible(isEmployee);
        
        if (isEmployee) {
            vo.setFinalPriceYuan(item.getStaffDiscountPriceYuan());
        } else {
            vo.setFinalPriceYuan(item.getStandardPriceYuan());
        }
        
        return vo;
    }
    
    public UnifiedApiResponse<String> addNewMerchandise(MerchandiseItem item) {
        item.setShelfStatus(1);
        item.setDeletionMarker(0);
        merchandiseItemGateway.insert(item);
        return UnifiedApiResponse.success("添加成功");
    }
    
    public UnifiedApiResponse<String> modifyMerchandise(Long itemId, MerchandiseItem updateData) {
        MerchandiseItem item = merchandiseItemGateway.selectById(itemId);
        
        if (item == null) {
            return UnifiedApiResponse.fail("商品不存在");
        }
        
        if (updateData.getItemDisplayName() != null) {
            item.setItemDisplayName(updateData.getItemDisplayName());
        }
        if (updateData.getItemDescription() != null) {
            item.setItemDescription(updateData.getItemDescription());
        }
        if (updateData.getStandardPriceYuan() != null) {
            item.setStandardPriceYuan(updateData.getStandardPriceYuan());
        }
        if (updateData.getStaffDiscountPriceYuan() != null) {
            item.setStaffDiscountPriceYuan(updateData.getStaffDiscountPriceYuan());
        }
        if (updateData.getStockQuantity() != null) {
            item.setStockQuantity(updateData.getStockQuantity());
        }
        
        merchandiseItemGateway.updateById(item);
        return UnifiedApiResponse.success("更新成功");
    }
}
