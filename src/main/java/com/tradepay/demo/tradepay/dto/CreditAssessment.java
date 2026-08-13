package com.tradepay.demo.tradepay.dto;

import com.tradepay.demo.tradepay.enums.DecisionStatus;

import java.math.BigDecimal;
import java.util.List;

public record CreditAssessment(
        String decision,
        BigDecimal approvedAmount,
        BigDecimal interestRate,
        String repaymentTerms,
        String reason
) {

}
