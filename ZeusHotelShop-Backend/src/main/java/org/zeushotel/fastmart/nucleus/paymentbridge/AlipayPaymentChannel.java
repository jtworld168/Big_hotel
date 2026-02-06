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
public class AlipayPaymentChannel {
    
    private final Random randomizer = new Random();
    
    public Map<String, Object> initiateAlipayPaymentFlow(String orderNumber, Double amountInYuan, String itemDescription) {
        Map<String, Object> paymentBundle = new HashMap<>();
        
        String tradeIdentifier = generateTradeIdentifier("AP");
        String qrStringData = constructAlipayQrString(orderNumber, amountInYuan, tradeIdentifier);
        
        paymentBundle.put("channelType", "alipay");
        paymentBundle.put("tradeNo", tradeIdentifier);
        paymentBundle.put("qrCodeString", qrStringData);
        paymentBundle.put("validityDuration", 900);
        paymentBundle.put("orderReference", orderNumber);
        paymentBundle.put("creationTime", LocalDateTime.now().toString());
        
        log.info("支付宝支付流程已启动 - 订单: {}, 金额: {}", orderNumber, amountInYuan);
        
        return paymentBundle;
    }
    
    public Boolean verifyAlipayCallback(Map<String, String> callbackParams) {
        String tradeNo = callbackParams.get("trade_no");
        String tradeStatus = callbackParams.get("trade_status");
        
        if ("TRADE_SUCCESS".equals(tradeStatus) && tradeNo != null) {
            log.info("支付宝支付验证成功 - 交易号: {}", tradeNo);
            return true;
        }
        
        log.warn("支付宝支付验证失败 - 状态: {}", tradeStatus);
        return false;
    }
    
    private String generateTradeIdentifier(String prefix) {
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        int randomValue = 100000 + randomizer.nextInt(900000);
        return prefix + timeStamp + randomValue;
    }
    
    private String constructAlipayQrString(String orderRef, Double yuan, String tradeId) {
        return String.format("https://qr.alipay.com/bax%s?amount=%.2f&order=%s", 
            tradeId.substring(2), yuan, orderRef);
    }
}
