package org.zeushotel.fastmart.nucleus.bizcore;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeushotel.fastmart.nucleus.dataschema.MerchandiseItem;
import org.zeushotel.fastmart.nucleus.dataschema.ShoppingBasketItem;
import org.zeushotel.fastmart.nucleus.dbgateway.MerchandiseItemGateway;
import org.zeushotel.fastmart.nucleus.dbgateway.ShoppingBasketGateway;
import org.zeushotel.fastmart.nucleus.viewresponse.UnifiedApiResponse;
import org.zeushotel.fastmart.nucleus.viewresponse.BasketItemVo;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingBasketOrchestrator {
    
    private final ShoppingBasketGateway shoppingBasketGateway;
    private final MerchandiseItemGateway merchandiseItemGateway;
    
    @Transactional
    public UnifiedApiResponse<String> addItemToBasket(Long shopperId, Long itemId, Integer quantity) {
        MerchandiseItem item = merchandiseItemGateway.selectById(itemId);
        
        if (item == null) {
            return UnifiedApiResponse.fail("商品不存在");
        }
        
        if (item.getShelfStatus() != 1) {
            return UnifiedApiResponse.fail("商品已下架");
        }
        
        if (item.getStockQuantity() < quantity) {
            return UnifiedApiResponse.fail("库存不足");
        }
        
        LambdaQueryWrapper<ShoppingBasketItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingBasketItem::getShopperProfileId, shopperId);
        queryWrapper.eq(ShoppingBasketItem::getMerchandiseItemId, itemId);
        
        ShoppingBasketItem basketItem = shoppingBasketGateway.selectOne(queryWrapper);
        
        if (basketItem != null) {
            basketItem.setSelectedQuantity(basketItem.getSelectedQuantity() + quantity);
            shoppingBasketGateway.updateById(basketItem);
        } else {
            basketItem = new ShoppingBasketItem();
            basketItem.setShopperProfileId(shopperId);
            basketItem.setMerchandiseItemId(itemId);
            basketItem.setSelectedQuantity(quantity);
            shoppingBasketGateway.insert(basketItem);
        }
        
        return UnifiedApiResponse.success("添加成功");
    }
    
    public UnifiedApiResponse<List<BasketItemVo>> fetchShopperBasket(Long shopperId) {
        LambdaQueryWrapper<ShoppingBasketItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingBasketItem::getShopperProfileId, shopperId);
        
        List<ShoppingBasketItem> basketItems = shoppingBasketGateway.selectList(queryWrapper);
        List<BasketItemVo> voList = new ArrayList<>();
        
        for (ShoppingBasketItem basketItem : basketItems) {
            MerchandiseItem item = merchandiseItemGateway.selectById(basketItem.getMerchandiseItemId());
            if (item != null) {
                BasketItemVo vo = new BasketItemVo();
                vo.setBasketItemId(basketItem.getBasketItemId());
                vo.setItemIdentifier(item.getItemIdentifier());
                vo.setItemDisplayName(item.getItemDisplayName());
                vo.setMainImageUrl(item.getMainImageUrl());
                vo.setStandardPriceYuan(item.getStandardPriceYuan());
                vo.setStaffDiscountPriceYuan(item.getStaffDiscountPriceYuan());
                vo.setSelectedQuantity(basketItem.getSelectedQuantity());
                vo.setStockQuantity(item.getStockQuantity());
                voList.add(vo);
            }
        }
        
        return UnifiedApiResponse.success(voList);
    }
    
    @Transactional
    public UnifiedApiResponse<String> modifyBasketItemQuantity(Long basketItemId, Integer quantity) {
        if (quantity <= 0) {
            shoppingBasketGateway.deleteById(basketItemId);
            return UnifiedApiResponse.success("已移除");
        }
        
        ShoppingBasketItem basketItem = shoppingBasketGateway.selectById(basketItemId);
        
        if (basketItem == null) {
            return UnifiedApiResponse.fail("购物车项不存在");
        }
        
        MerchandiseItem item = merchandiseItemGateway.selectById(basketItem.getMerchandiseItemId());
        
        if (item == null || item.getStockQuantity() < quantity) {
            return UnifiedApiResponse.fail("库存不足");
        }
        
        basketItem.setSelectedQuantity(quantity);
        shoppingBasketGateway.updateById(basketItem);
        
        return UnifiedApiResponse.success("更新成功");
    }
    
    @Transactional
    public UnifiedApiResponse<String> removeBasketItem(Long basketItemId) {
        shoppingBasketGateway.deleteById(basketItemId);
        return UnifiedApiResponse.success("删除成功");
    }
    
    @Transactional
    public UnifiedApiResponse<String> clearShopperBasket(Long shopperId) {
        LambdaQueryWrapper<ShoppingBasketItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingBasketItem::getShopperProfileId, shopperId);
        
        shoppingBasketGateway.delete(queryWrapper);
        
        return UnifiedApiResponse.success("清空成功");
    }
}
