package com.tradepay.demo.tradepay.dto;

import java.math.BigDecimal;

public record PricingResult(
        BigDecimal interestRate,
        String repaymentTermMonths
) {
}
