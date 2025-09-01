package com.example.kuge.service;

public interface PayService {
    void createorder(Integer id, String outTradeNo, Integer payaccount);

    void updateOrderStatus(String outTradeNo, String tradeNo, String tradeStatus);
}
