package com.sparta.blackwhitedeliverydriver.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @GetMapping
    public String getPaymentPage() {
        return "payment";
    }

    @GetMapping("/success")
    public String getPaymentSuccessPage() {
        return "payment-success";
    }

    @GetMapping("/fail")
    public String getPaymentFailPage() {
        return "payment-fail";
    }

    @GetMapping("/cancel")
    public String getPaymentCancelPage() {
        return "payment-cancel";
    }
}
