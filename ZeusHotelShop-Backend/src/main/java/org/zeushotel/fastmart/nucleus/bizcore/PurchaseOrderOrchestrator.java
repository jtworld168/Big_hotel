package org.zeushotel.fastmart.nucleus.bizcore;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeushotel.fastmart.nucleus.dataschema.*;
import org.zeushotel.fastmart.nucleus.dbgateway.*;
import org.zeushotel.fastmart.nucleus.transferobject.CreateOrderRequest;
import org.zeushotel.fastmart.nucleus.transferobject.OrderItemDto;
import org.zeushotel.fastmart.nucleus.viewresponse.UnifiedApiResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PurchaseOrderOrchestrator {
    
    private final PurchaseOrderGateway purchaseOrderGateway;
    private final MerchandiseItemGateway merchandiseItemGateway;
    private final ShopperVoucherClaimGateway shopperVoucherClaimGateway;
    private final DiscountVoucherGateway discountVoucherGateway;
    private final ShopperProfileGateway shopperProfileGateway;
    private final ShoppingBasketGateway shoppingBasketGateway;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public UnifiedApiResponse<PurchaseOrderRecord> createPurchaseOrder(CreateOrderRequest request, Long shopperId) {
        ShopperProfileRecord shopper = shopperProfileGateway.selectById(shopperId);
        
        if (shopper == null) {
            return UnifiedApiResponse.fail("用户不存在");
        }
        
        boolean isEmployee = shopper.getEmployeeStatusFlag() == 1;
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItemDto> orderItems = new ArrayList<>();
        
        for (OrderItemDto itemDto : request.getOrderItems()) {
            MerchandiseItem item = merchandiseItemGateway.selectById(itemDto.getItemId());
            
            if (item == null) {
                return UnifiedApiResponse.fail("商品不存在: " + itemDto.getItemId());
            }
            
            if (item.getStockQuantity() < itemDto.getQuantity()) {
                return UnifiedApiResponse.fail("库存不足: " + item.getItemDisplayName());
            }
            
            BigDecimal itemPrice = isEmployee ? item.getStaffDiscountPriceYuan() : item.getStandardPriceYuan();
            BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
            
            OrderItemDto orderItem = new OrderItemDto();
            orderItem.setItemId(item.getItemIdentifier());
            orderItem.setItemName(item.getItemDisplayName());
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(itemPrice);
            orderItem.setSubtotal(itemTotal);
            orderItems.add(orderItem);
            
            item.setStockQuantity(item.getStockQuantity() - itemDto.getQuantity());
            merchandiseItemGateway.updateById(item);
        }
        
        BigDecimal discountAmount = BigDecimal.ZERO;
        Long appliedVoucherId = null;
        
        if (request.getVoucherId() != null) {
            LambdaQueryWrapper<ShopperVoucherClaim> claimQuery = new LambdaQueryWrapper<>();
            claimQuery.eq(ShopperVoucherClaim::getShopperProfileId, shopperId);
            claimQuery.eq(ShopperVoucherClaim::getVoucherId, request.getVoucherId());
            claimQuery.eq(ShopperVoucherClaim::getUsageStatus, 0);
            
            ShopperVoucherClaim claim = shopperVoucherClaimGateway.selectOne(claimQuery);
            
            if (claim != null) {
                DiscountVoucherRecord voucher = discountVoucherGateway.selectById(request.getVoucherId());
                
                if (voucher != null && totalAmount.compareTo(voucher.getMinimumPurchase()) >= 0) {
                    discountAmount = voucher.getDiscountAmount();
                    appliedVoucherId = voucher.getVoucherIdentifier();
                    
                    claim.setUsageStatus(1);
                    claim.setUsedAtTime(LocalDateTime.now());
                    shopperVoucherClaimGateway.updateById(claim);
                }
            }
        }
        
        BigDecimal finalPayment = totalAmount.subtract(discountAmount);
        
        if (finalPayment.compareTo(BigDecimal.ZERO) < 0) {
            finalPayment = BigDecimal.ZERO;
        }
        
        PurchaseOrderRecord order = new PurchaseOrderRecord();
        order.setOrderNumber(generateOrderNumber());
        order.setShopperProfileId(shopperId);
        order.setTotalAmountYuan(totalAmount);
        order.setDiscountAmountYuan(discountAmount);
        order.setFinalPaymentYuan(finalPayment);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setOrderStatus(0);
        order.setAppliedVoucherId(appliedVoucherId);
        order.setDeletionMarker(0);
        
        try {
            order.setOrderItemsJson(objectMapper.writeValueAsString(orderItems));
        } catch (JsonProcessingException e) {
            return UnifiedApiResponse.fail("订单数据处理失败");
        }
        
        purchaseOrderGateway.insert(order);
        
        LambdaQueryWrapper<ShoppingBasketItem> basketQuery = new LambdaQueryWrapper<>();
        basketQuery.eq(ShoppingBasketItem::getShopperProfileId, shopperId);
        shoppingBasketGateway.delete(basketQuery);
        
        return UnifiedApiResponse.success(order);
    }
    
    public UnifiedApiResponse<List<PurchaseOrderRecord>> fetchShopperOrders(Long shopperId) {
        LambdaQueryWrapper<PurchaseOrderRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PurchaseOrderRecord::getShopperProfileId, shopperId);
        queryWrapper.orderByDesc(PurchaseOrderRecord::getRecordCreatedTime);
        
        List<PurchaseOrderRecord> orders = purchaseOrderGateway.selectList(queryWrapper);
        
        return UnifiedApiResponse.success(orders);
    }
    
    public UnifiedApiResponse<PurchaseOrderRecord> fetchOrderDetails(Long orderId) {
        PurchaseOrderRecord order = purchaseOrderGateway.selectById(orderId);
        
        if (order == null) {
            return UnifiedApiResponse.fail("订单不存在");
        }
        
        return UnifiedApiResponse.success(order);
    }
    
    @Transactional
    public UnifiedApiResponse<String> updateOrderPaymentStatus(Long orderId, String transactionId) {
        PurchaseOrderRecord order = purchaseOrderGateway.selectById(orderId);
        
        if (order == null) {
            return UnifiedApiResponse.fail("订单不存在");
        }
        
        order.setPaymentTransactionId(transactionId);
        order.setOrderStatus(1);
        
        purchaseOrderGateway.updateById(order);
        
        return UnifiedApiResponse.success("支付成功");
    }
    
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Random random = new Random();
        int randomNum = 1000 + random.nextInt(9000);
        return "ZH" + timestamp + randomNum;
    }
}
