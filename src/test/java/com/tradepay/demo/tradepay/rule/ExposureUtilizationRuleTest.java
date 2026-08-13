package com.tradepay.demo.tradepay.rule;

import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.dto.RuleResult;
import com.tradepay.demo.tradepay.enums.DecisionStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExposureUtilizationRuleTest {

    private final ExposureUtilizationRule rule = new ExposureUtilizationRule();

    @Test
    void shouldApproveNormalUtilization() {

        DecisionRequest request = request("10000", "10000");

        RuleResult result = rule.evaluate(request);

        assertEquals(DecisionStatus.APPROVED, result.status());

        assertEquals("NORMAL_UTILIZATION", result.ruleCode());
    }

    @Test
    void shouldPartiallyApproveVeryHighUtilization() {

        DecisionRequest request = request("40000", "7000");

        RuleResult result = rule.evaluate(request);

        assertEquals(DecisionStatus.PARTIALLY_APPROVED, result.status());
    }

    @Test
    void shouldDeclineWhenUtilizationExceeds100Percent() {

        DecisionRequest request = request("45000", "10000");

        RuleResult result = rule.evaluate(request);

        assertEquals(DecisionStatus.DECLINED, result.status());
    }

    private DecisionRequest request(String currentExposure, String transactionAmount) {

        return new DecisionRequest(
                "M1021",
                "Grocery store",
                "B",
                new BigDecimal("50000"),
                new BigDecimal(currentExposure),
                new BigDecimal(transactionAmount),
                new BigDecimal("65000"),
                Map.of("sku_A", 100)
        );
    }
}
