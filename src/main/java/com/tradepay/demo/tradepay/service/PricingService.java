package com.tradepay.demo.tradepay.service;

import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.dto.PricingResult;
import com.tradepay.demo.tradepay.enums.DecisionStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PricingService {

    private static final BigDecimal MINIMUM_RATE = new BigDecimal("1.00");

    public PricingResult calculate(DecisionRequest request, DecisionStatus decision) {

        /*
         * If declined, there are no financing terms.
         */
        if (decision == DecisionStatus.DECLINED) {
            return new PricingResult(BigDecimal.ZERO, "N/A");
        }

        /*
         * 1. Start with a base interest rate
         * based on the merchant risk tier.
         */
        BigDecimal interestRate = getBaseRate(request.riskTier());

        /*
         * 2. Calculate post-transaction exposure.
         */
        BigDecimal postTransactionExposure = request.currentExposure().add(request.transactionAmount());

        /*
         * 3. Calculate utilization.
         *
         * Example:
         * 42,000 / 50,000 = 0.84
         */
        BigDecimal utilization = postTransactionExposure.divide(request.creditLimit(), 4, RoundingMode.HALF_UP);

        /*
         * High utilization increases pricing.
         */
        if (utilization.compareTo(new BigDecimal("0.80")) > 0) {

            interestRate = interestRate.add(new BigDecimal("0.50"));
        }

        /*
         * 4. Calculate transaction-to-volume ratio.
         */
        BigDecimal transactionRatio = request.transactionAmount().divide(request.monthlyPurchaseVolume(), 4, RoundingMode.HALF_UP);

        /*
         * A relatively small transaction is
         * considered less risky.
         */
        if (transactionRatio.compareTo(new BigDecimal("0.25")) <= 0) {

            interestRate = interestRate.subtract(new BigDecimal("0.25"));
        }

        /*
         * Never go below the minimum interest rate.
         */
        interestRate = interestRate.max(MINIMUM_RATE);

        /*
         * 5. Determine repayment terms.
         */
        String repaymentTerms = calculateRepaymentTerms(request.riskTier(), utilization, decision);

        return new PricingResult(interestRate, repaymentTerms);
    }

    private BigDecimal getBaseRate(String riskTier) {

        return switch (riskTier.toUpperCase()) {

            case "A" -> new BigDecimal("1.00");
            case "B" -> new BigDecimal("1.50");
            case "C" -> new BigDecimal("2.00");
            case "D" -> new BigDecimal("2.50");
            default -> throw new IllegalArgumentException("Unsupported risk tier: " + riskTier);
        };
    }

    private String calculateRepaymentTerms(String riskTier, BigDecimal utilization, DecisionStatus decision) {

        /*
         * Partial approvals get shorter repayment terms.
         */
        if (decision == DecisionStatus.PARTIALLY_APPROVED) {
            return "30 days";
        }

        /*
         * Very high utilization.
         */
        if (utilization.compareTo(new BigDecimal("0.90")) > 0) {
            return "30 days";
        }

        /*
         * High utilization.
         */
        if (utilization.compareTo(new BigDecimal("0.80")) > 0) {
            return "60 days";
        }

        /*
         * Otherwise base repayment terms
         * on risk tier.
         */
        return switch (riskTier.toUpperCase()) {

            case "A" -> "90 days";
            case "B" -> "60 days";
            case "C" -> "30 days";
            case "D" -> "30 days";
            default -> "30 days";
        };
    }
}
