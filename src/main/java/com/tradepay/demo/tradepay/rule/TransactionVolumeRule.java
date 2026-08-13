package com.tradepay.demo.tradepay.rule;

import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.dto.RuleResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class TransactionVolumeRule implements DecisionRule{

    private static final BigDecimal DECLINE_THRESHOLD = new BigDecimal("0.75");

    private static final BigDecimal PARTIAL_APPROVAL_THRESHOLD = new BigDecimal("0.50");

    @Override
    public RuleResult evaluate(DecisionRequest request) {

        BigDecimal ratio = request.transactionAmount()
                        .divide(request.monthlyPurchaseVolume(), 4, RoundingMode.HALF_UP);

        /*
         * Transaction is greater than 75% of the merchant's
         * normal monthly purchase volume.
         */
        if (ratio.compareTo(DECLINE_THRESHOLD) > 0) {

            return RuleResult.decline(
                    "TRANSACTION_TOO_LARGE",
                    "Transaction exceeds 75% of monthly purchase volume"
            );
        }

        /*
         * Transaction is between 50% and 75% of the merchant's
         * monthly purchase volume.
         */
        if (ratio.compareTo(PARTIAL_APPROVAL_THRESHOLD) > 0) {

            return RuleResult.partialApproval(
                    "HIGH_TRANSACTION_VOLUME_RATIO",
                    "Transaction exceeds 50% of monthly purchase volume"
            );
        }

        /*
         * Transaction is 50% or less of monthly purchase volume.
         */
        return RuleResult.approve(
                "ACCEPTABLE_TRANSACTION_VOLUME",
                "Transaction is reasonable compared with monthly purchase volume"
        );
    }

    @Override
    public int priority() {
        return 30;
    }
}
