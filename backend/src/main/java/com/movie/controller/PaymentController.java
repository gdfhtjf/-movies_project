package com.movie.controller;

import com.movie.common.Result;
import com.movie.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/pay/{orderId}")
    public Result<?> pay(@PathVariable Integer orderId) {
        boolean success = paymentService.processPayment(orderId);
        if (success) {
            return Result.success("支付成功");
        }
        return Result.error(400, "支付失败，订单状态不允许支付");
    }
}
