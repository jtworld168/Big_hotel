package org.zeushotel.fastmart.nucleus.webportal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zeushotel.fastmart.nucleus.paymentbridge.AlipayPaymentChannel;
import org.zeushotel.fastmart.nucleus.paymentbridge.WeChatPaymentChannel;
import org.zeushotel.fastmart.nucleus.qrcodegen.QrCodeImageGenerator;
import org.zeushotel.fastmart.nucleus.viewresponse.UnifiedApiResponse;

import java.util.Map;

@Tag(name = "支付渠道接入点")
@RestController
@RequestMapping("/gateway/payment-channels")
@RequiredArgsConstructor
public class PaymentChannelEndpoint {
    
    private final WeChatPaymentChannel weChatChannel;
    private final AlipayPaymentChannel alipayChannel;
    private final QrCodeImageGenerator qrGenerator;
    
    @Operation(summary = "发起微信支付")
    @PostMapping("/wechat/initiate")
    public UnifiedApiResponse<Map<String, Object>> initiateWeChatPay(
            @RequestParam String orderNumber,
            @RequestParam Double amount,
            @RequestParam String description) {
        
        Map<String, Object> paymentData = weChatChannel.initiateWeChatPaymentFlow(orderNumber, amount, description);
        String qrCodeContent = (String) paymentData.get("qrCodeData");
        String qrImage = qrGenerator.generateBase64QrImage(qrCodeContent, 300);
        
        paymentData.put("qrCodeImage", qrImage);
        
        return UnifiedApiResponse.success(paymentData);
    }
    
    @Operation(summary = "发起支付宝支付")
    @PostMapping("/alipay/initiate")
    public UnifiedApiResponse<Map<String, Object>> initiateAlipayPay(
            @RequestParam String orderNumber,
            @RequestParam Double amount,
            @RequestParam String description) {
        
        Map<String, Object> paymentData = alipayChannel.initiateAlipayPaymentFlow(orderNumber, amount, description);
        String qrCodeContent = (String) paymentData.get("qrCodeString");
        String qrImage = qrGenerator.generateBase64QrImage(qrCodeContent, 300);
        
        paymentData.put("qrCodeImage", qrImage);
        
        return UnifiedApiResponse.success(paymentData);
    }
    
    @Operation(summary = "微信支付回调")
    @PostMapping("/wechat/callback")
    public UnifiedApiResponse<String> handleWeChatCallback(@RequestBody Map<String, String> callbackData) {
        Boolean verified = weChatChannel.verifyWeChatPaymentCallback(callbackData);
        
        if (verified) {
            return UnifiedApiResponse.success("支付成功");
        }
        
        return UnifiedApiResponse.fail("支付验证失败");
    }
    
    @Operation(summary = "支付宝支付回调")
    @PostMapping("/alipay/callback")
    public UnifiedApiResponse<String> handleAlipayCallback(@RequestBody Map<String, String> callbackData) {
        Boolean verified = alipayChannel.verifyAlipayCallback(callbackData);
        
        if (verified) {
            return UnifiedApiResponse.success("支付成功");
        }
        
        return UnifiedApiResponse.fail("支付验证失败");
    }
    
    @Operation(summary = "生成二维码图片")
    @GetMapping("/generate-qr")
    public UnifiedApiResponse<String> generateQrCode(@RequestParam String content, @RequestParam(required = false) Integer size) {
        String qrImage = qrGenerator.generateBase64QrImage(content, size);
        return UnifiedApiResponse.success(qrImage);
    }
}
