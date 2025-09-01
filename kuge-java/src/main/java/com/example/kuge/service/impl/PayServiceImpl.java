package com.example.kuge.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.kuge.config.RabbitMqConfig;
import com.example.kuge.mapper.PayMapper;
import com.example.kuge.pojo.Orders;
import com.example.kuge.service.PayService;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PayServiceImpl implements PayService {
    @Autowired
    private PayMapper payMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private static final long DELAY_MILLIS = 30 * 1000;

    @Transactional
    @Override
    public void createorder(Integer id, String outTradeNo, Integer payaccount) {
        int totalTimes = 5;
        Orders orders=new Orders();
        orders.setPayaccount(payaccount);
        orders.setUser_id(id);
        orders.setOutTradeNo(outTradeNo);
        orders.setCreate_time(LocalDateTime.now());
        orders.setUpdate_time(LocalDateTime.now());
        payMapper.insert(orders);
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.DELAY_EXCHANGE_NAME,
                RabbitMqConfig.DELAY_ROUTING_KEY,
                outTradeNo,
                message -> {
                    message.getMessageProperties().setHeader("x-delay", DELAY_MILLIS);
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    message.getMessageProperties().setHeader("checkTimes", totalTimes);
                    return message;
                }
        );
        System.out.println("发送延迟消息");
    }

    @Override
    public void updateOrderStatus(String outTradeNo, String tradeNo, String tradeStatus) {
        System.out.println(tradeNo.length());
        UpdateWrapper<Orders> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("outTradeNo",outTradeNo);
        updateWrapper.set("status",1);
        updateWrapper.set("update_time",LocalDateTime.now());
        payMapper.update(updateWrapper);
    }
}
