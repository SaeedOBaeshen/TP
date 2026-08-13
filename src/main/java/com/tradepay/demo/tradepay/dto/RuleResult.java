package com.tradepay.demo.tradepay.dto;

import com.tradepay.demo.tradepay.enums.DecisionStatus;

public record RuleResult(
        String ruleCode,
        DecisionStatus status,
        String message
) {
    public static RuleResult approve(String code, String message) {
        return new RuleResult(
                code,
                DecisionStatus.APPROVED,
                message
        );
    }

    public static RuleResult partialApproval(String code, String message) {
        return new RuleResult(
                code,
                DecisionStatus.PARTIALLY_APPROVED,
                message
        );
    }

    public static RuleResult decline(String code, String message) {
        return new RuleResult(
                code,
                DecisionStatus.DECLINED,
                message
        );
    }
}
