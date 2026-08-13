package com.tradepay.demo.tradepay.rule;

import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.dto.RuleResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ExposureUtilizationRule implements DecisionRule {

    private static final BigDecimal PARTIAL_APPROVAL_THRESHOLD = new BigDecimal("0.90");

    private static final BigDecimal HIGH_THRESHOLD = new BigDecimal("0.80");

    @Override
    public RuleResult evaluate(DecisionRequest request) {

        BigDecimal postExposure = request.currentExposure().add(request.transactionAmount());

        BigDecimal utilization = postExposure.divide(request.creditLimit(), 4, RoundingMode.HALF_UP);

        // More than 100% of the credit limit
        // means the requested transaction cannot be fully approved.
        if (utilization.compareTo(BigDecimal.ONE) > 0) {
            return RuleResult.decline(
                    "UTILIZATION_OVER_100",
                    "Post-transaction utilization exceeds 100%"
            );
        }

        // Between 90% and 100% utilization.
        if (utilization.compareTo(PARTIAL_APPROVAL_THRESHOLD) > 0) {

            return RuleResult.partialApproval(
                    "VERY_HIGH_UTILIZATION",
                    "Post-transaction utilization exceeds 90%"
            );
        }

        // Between 80% and 90%.
        if (utilization.compareTo(HIGH_THRESHOLD) > 0) {

            return RuleResult.approve(
                    "HIGH_UTILIZATION",
                    "Transaction is within the credit limit but utilization exceeds 80%"
            );
        }

        // 80% or below.
        return RuleResult.approve(
                "NORMAL_UTILIZATION",
                "Post-transaction utilization is within the normal range"
        );
    }

    @Override
    public int priority() {
        return 20;
    }
}
