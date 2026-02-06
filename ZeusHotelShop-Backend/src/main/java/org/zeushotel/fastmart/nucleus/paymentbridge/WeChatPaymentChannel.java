package org.zeushotel.fastmart.nucleus.paymentbridge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
public class WeChatPaymentChannel {
    
    private final Random randomizer = new Random();
    
    public Map<String, Object> initiateWeChatPaymentFlow(String orderNumber, Double amountInYuan, String description) {
        Map<String, Object> paymentPackage = new HashMap<>();
        
        String prepayIdentifier = generatePrepayIdentifier("WX");
        String qrPayloadData = constructWeChatQrPayload(orderNumber, amountInYuan, prepayIdentifier);
        
        paymentPackage.put("channelType", "wechat");
        paymentPackage.put("prepayId", prepayIdentifier);
        paymentPackage.put("qrCodeData", qrPayloadData);
        paymentPackage.put("expirySeconds", 600);
        paymentPackage.put("orderRef", orderNumber);
        paymentPackage.put("timestamp", System.currentTimeMillis());
        
        log.info("微信支付流程已启动 - 订单: {}, 金额: {}", orderNumber, amountInYuan);
        
        return paymentPackage;
    }
    
    public Boolean verifyWeChatPaymentCallback(Map<String, String> callbackData) {
        String transactionId = callbackData.get("transaction_id");
        String tradeStatus = callbackData.get("trade_state");
        
        if ("SUCCESS".equals(tradeStatus) && transactionId != null) {
            log.info("微信支付验证成功 - 交易ID: {}", transactionId);
            return true;
        }
        
        log.warn("微信支付验证失败 - 状态: {}", tradeStatus);
        return false;
    }
    
    private String generatePrepayIdentifier(String prefix) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        int randomSuffix = 100000 + randomizer.nextInt(900000);
        return prefix + timestamp + randomSuffix;
    }
    
    private String constructWeChatQrPayload(String orderNum, Double amount, String prepayId) {
        return String.format("weixin://wxpay/bizpayurl?pr=%s&order=%s&amt=%.2f", 
            prepayId, orderNum, amount);
    }
}
