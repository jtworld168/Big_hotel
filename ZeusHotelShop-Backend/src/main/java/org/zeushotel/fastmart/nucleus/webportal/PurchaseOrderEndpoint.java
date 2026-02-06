package org.zeushotel.fastmart.nucleus.webportal;

import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zeushotel.fastmart.nucleus.bizcore.PurchaseOrderOrchestrator;
import org.zeushotel.fastmart.nucleus.dataschema.PurchaseOrderRecord;
import org.zeushotel.fastmart.nucleus.transferobject.CreateOrderRequest;
import org.zeushotel.fastmart.nucleus.viewresponse.UnifiedApiResponse;

import java.util.List;

@Tag(name = "采购订单接入点")
@RestController
@RequestMapping("/gateway/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderEndpoint {
    
    private final PurchaseOrderOrchestrator orchestrator;
    
    @Operation(summary = "创建采购订单")
    @PostMapping("/create-new")
    public UnifiedApiResponse<PurchaseOrderRecord> createOrder(@RequestBody CreateOrderRequest request) {
        Long shopperId = StpUtil.getLoginIdAsLong();
        return orchestrator.createPurchaseOrder(request, shopperId);
    }
    
    @Operation(summary = "查看我的订单列表")
    @GetMapping("/my-orders")
    public UnifiedApiResponse<List<PurchaseOrderRecord>> viewMyOrders() {
        Long shopperId = StpUtil.getLoginIdAsLong();
        return orchestrator.fetchShopperOrders(shopperId);
    }
    
    @Operation(summary = "查看订单详情")
    @GetMapping("/details/{orderId}")
    public UnifiedApiResponse<PurchaseOrderRecord> viewOrderDetails(@PathVariable Long orderId) {
        return orchestrator.fetchOrderDetails(orderId);
    }
    
    @Operation(summary = "更新订单支付状态")
    @PostMapping("/update-payment/{orderId}")
    public UnifiedApiResponse<String> updatePayment(@PathVariable Long orderId, @RequestParam String transactionId) {
        return orchestrator.updateOrderPaymentStatus(orderId, transactionId);
    }
}
