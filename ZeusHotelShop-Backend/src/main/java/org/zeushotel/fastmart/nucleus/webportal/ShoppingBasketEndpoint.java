package org.zeushotel.fastmart.nucleus.webportal;

import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zeushotel.fastmart.nucleus.bizcore.ShoppingBasketOrchestrator;
import org.zeushotel.fastmart.nucleus.viewresponse.UnifiedApiResponse;
import org.zeushotel.fastmart.nucleus.viewresponse.BasketItemVo;

import java.util.List;

@Tag(name = "购物篮接入点")
@RestController
@RequestMapping("/gateway/shopping-basket")
@RequiredArgsConstructor
public class ShoppingBasketEndpoint {
    
    private final ShoppingBasketOrchestrator orchestrator;
    
    @Operation(summary = "添加商品到购物篮")
    @PostMapping("/add-item")
    public UnifiedApiResponse<String> addItem(@RequestParam Long itemId, @RequestParam Integer quantity) {
        Long shopperId = StpUtil.getLoginIdAsLong();
        return orchestrator.addItemToBasket(shopperId, itemId, quantity);
    }
    
    @Operation(summary = "查看我的购物篮")
    @GetMapping("/my-basket")
    public UnifiedApiResponse<List<BasketItemVo>> viewMyBasket() {
        Long shopperId = StpUtil.getLoginIdAsLong();
        return orchestrator.fetchShopperBasket(shopperId);
    }
    
    @Operation(summary = "修改购物篮商品数量")
    @PutMapping("/modify-quantity/{basketItemId}")
    public UnifiedApiResponse<String> modifyQuantity(@PathVariable Long basketItemId, @RequestParam Integer quantity) {
        return orchestrator.modifyBasketItemQuantity(basketItemId, quantity);
    }
    
    @Operation(summary = "移除购物篮商品")
    @DeleteMapping("/remove-item/{basketItemId}")
    public UnifiedApiResponse<String> removeItem(@PathVariable Long basketItemId) {
        return orchestrator.removeBasketItem(basketItemId);
    }
    
    @Operation(summary = "清空购物篮")
    @DeleteMapping("/clear-all")
    public UnifiedApiResponse<String> clearBasket() {
        Long shopperId = StpUtil.getLoginIdAsLong();
        return orchestrator.clearShopperBasket(shopperId);
    }
}
