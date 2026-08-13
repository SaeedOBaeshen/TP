package com.tradepay.demo.tradepay.rule;

import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.dto.RuleResult;
import com.tradepay.demo.tradepay.enums.DecisionStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RiskTierRuleTest {

    private final RiskTierRule rule = new RiskTierRule();

    @Test
    void shouldApproveRiskTierA() {

        RuleResult result = rule.evaluate(request("A"));

        assertEquals(DecisionStatus.APPROVED, result.status());
        assertEquals("LOW_RISK_TIER", result.ruleCode());
    }

    @Test
    void shouldApproveRiskTierB() {

        RuleResult result = rule.evaluate(request("B"));

        assertEquals(DecisionStatus.APPROVED, result.status());
        assertEquals("MODERATE_RISK_TIER", result.ruleCode());
    }

    @Test
    void shouldPartiallyApproveRiskTierC() {

        RuleResult result = rule.evaluate(request("C"));

        assertEquals(DecisionStatus.PARTIALLY_APPROVED, result.status());

        assertEquals("ELEVATED_RISK_TIER", result.ruleCode());
    }

    @Test
    void shouldPartiallyApproveRiskTierD() {

        RuleResult result = rule.evaluate(request("D"));

        assertEquals(DecisionStatus.PARTIALLY_APPROVED, result.status());

        assertEquals("HIGH_RISK_TIER", result.ruleCode());
    }

    @Test
    void shouldDeclineInvalidRiskTier() {

        RuleResult result = rule.evaluate(request("X"));

        assertEquals(DecisionStatus.DECLINED, result.status());

        assertEquals("INVALID_RISK_TIER", result.ruleCode());
    }

    private DecisionRequest request(String riskTier) {

        return new DecisionRequest(
                "M1021",
                "A small grocery store",
                riskTier,
                new BigDecimal("50000"),
                new BigDecimal("30000"),
                new BigDecimal("12000"),
                new BigDecimal("65000"),
                Map.of("sku_A", 100)
        );
    }
}
