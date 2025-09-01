package com.example.kuge.controller;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.alibaba.fastjson.JSONObject;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.example.kuge.service.PayService;
import com.example.kuge.util.JwtUtil;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.example.kuge.pojo.Result;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Autowired
    private PayService payService;
    @Autowired
    private Config config;
    @Value("${QR.code.save}")
    private String QRsave;

    @GetMapping("/getCode")
    public Result getCode( @RequestHeader(value = "Authorization") String token,Integer payaccount) throws Exception {
        //TODO 向数据库中插入订单表并向rabbitmq发送延迟消息
        Map<String, Object> map = JwtUtil.parseToken(token);
        Integer id = (Integer) map.get("id");
        Factory.setOptions(config);
        String outTradeNo = UUID.randomUUID().toString().replace("-", "");
        AlipayTradePrecreateResponse response = Factory.Payment.FaceToFace()
                .preCreate("购买vip:", outTradeNo, String.valueOf(payaccount));
            String qrURL = response.getQrCode();
            QrConfig config = new QrConfig(500, 500);
            QrCodeUtil.generate(qrURL, config, new File(QRsave));
        payService.createorder(id,outTradeNo,payaccount);
        return Result.success("/api/qrcode/image.png");
        }
    @PostMapping("/notify")
    public String getNotify(HttpServletRequest request){
        try {
            Map<String, String> params = new HashMap<>();
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String name = parameterNames.nextElement();
                String value = request.getParameter(name);
                params.put(name, value);
            }
            boolean signVerified = Factory.Payment.Common().verifyNotify(params);
            if (signVerified) {
                String outTradeNo = params.get("out_trade_no");
                String tradeNo = params.get("trade_no");
                String tradeStatus = params.get("trade_status");
                if ("TRADE_SUCCESS".equals(tradeStatus)) {
                    payService.updateOrderStatus(outTradeNo, tradeNo, tradeStatus);
                    System.out.println("支付宝异步回调处理成功，订单号：" + outTradeNo);
                }
                return "success";
            } else {
                System.out.println("支付宝异步回调签名验证失败");
                return "fail";
            }
        } catch (Exception e) {
            System.out.println("支付宝异步回调处理异常：" + e.getMessage());
            return "fail";
        }
    }
}
