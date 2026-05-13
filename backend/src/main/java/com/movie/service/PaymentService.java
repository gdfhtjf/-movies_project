package com.movie.service;

public interface PaymentService {
    /** 模拟支付：将订单从 pending 改为 paid，返回支付是否成功 */
    boolean processPayment(Integer orderId);
}
