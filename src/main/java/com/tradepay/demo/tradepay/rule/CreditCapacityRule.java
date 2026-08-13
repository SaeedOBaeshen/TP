package com.tradepay.demo.tradepay.rule;

import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.dto.RuleResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CreditCapacityRule implements DecisionRule{

    @Override
    public RuleResult evaluate(DecisionRequest request) {

        BigDecimal postTransactionExposure = request.currentExposure().add(request.transactionAmount());

        if (postTransactionExposure.compareTo(request.creditLimit()) > 0) {
            return RuleResult.decline(
                    "CREDIT_LIMIT_EXCEEDED",
                    "Transaction would exceed the merchant credit limit"
            );
        }

        return RuleResult.approve(
                "WITHIN_CREDIT_LIMIT",
                "Transaction is within the available credit limit"
        );
    }

    @Override
    public int priority() {
        return 0;
    }
}
