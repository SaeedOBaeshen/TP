package com.tradepay.demo.tradepay.rule;

import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.dto.RuleResult;
import org.springframework.stereotype.Component;

@Component
public class RiskTierRule implements DecisionRule {

    @Override
    public RuleResult evaluate(DecisionRequest request) {

        return switch (request.riskTier().toUpperCase()) {

            case "A" -> RuleResult.approve(
                    "LOW_RISK_TIER",
                    "Merchant is in risk tier A"
            );

            case "B" -> RuleResult.approve(
                    "MODERATE_RISK_TIER",
                    "Merchant is in risk tier B"
            );

            case "C" -> RuleResult.partialApproval(
                    "ELEVATED_RISK_TIER",
                    "Merchant is in risk tier C"
            );

            case "D" -> RuleResult.partialApproval(
                    "HIGH_RISK_TIER",
                    "Merchant is in risk tier D"
            );

            default -> RuleResult.decline(
                    "INVALID_RISK_TIER",
                    "Unknown merchant risk tier"
            );
        };
    }

    @Override
    public int priority() {
        return 40;
    }
}
