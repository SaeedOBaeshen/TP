package com.tradepay.demo.tradepay.rule;

import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.dto.RuleResult;
import com.tradepay.demo.tradepay.enums.DecisionStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreditCapacityRuleTest {

    private final CreditCapacityRule rule = new CreditCapacityRule();

    @Test
    void shouldApproveWhenTransactionFitsWithinCreditLimit() {

        DecisionRequest request = new DecisionRequest(
                "M1021",
                "Grocery store",
                "B",
                new BigDecimal("50000"),
                new BigDecimal("30000"),
                new BigDecimal("12000"),
                new BigDecimal("65000"),
                Map.of("sku_A", 100)
        );

        RuleResult result = rule.evaluate(request);

        assertEquals(DecisionStatus.APPROVED, result.status());

        assertEquals("WITHIN_CREDIT_LIMIT", result.ruleCode());
    }

    @Test
    void shouldDeclineWhenTransactionExceedsCreditLimit() {

        DecisionRequest request = new DecisionRequest(
                "M1021",
                "Grocery store",
                "B",
                new BigDecimal("50000"),
                new BigDecimal("45000"),
                new BigDecimal("10000"),
                new BigDecimal("65000"),
                Map.of("sku_A", 100)
        );

        RuleResult result = rule.evaluate(request);

        assertEquals(DecisionStatus.DECLINED, result.status());
    }
}
