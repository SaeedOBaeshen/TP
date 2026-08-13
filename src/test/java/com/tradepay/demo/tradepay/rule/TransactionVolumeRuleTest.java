package com.tradepay.demo.tradepay.rule;

import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.dto.RuleResult;
import com.tradepay.demo.tradepay.enums.DecisionStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionVolumeRuleTest {

    private final TransactionVolumeRule rule = new TransactionVolumeRule();

    @Test
    void shouldApproveWhenTransactionIsBelow50PercentOfVolume() {

        RuleResult result = rule.evaluate(request("20000"));

        assertEquals(DecisionStatus.APPROVED, result.status());
    }

    @Test
    void shouldPartiallyApproveBetween50And75Percent() {

        RuleResult result = rule.evaluate(request("40000"));

        assertEquals(DecisionStatus.PARTIALLY_APPROVED, result.status());
    }

    @Test
    void shouldDeclineAbove75Percent() {

        RuleResult result = rule.evaluate(request("50000"));

        assertEquals(DecisionStatus.DECLINED, result.status());
    }

    private DecisionRequest request(String transactionAmount) {

        return new DecisionRequest(
                "M1021",
                "Grocery store",
                "B",
                new BigDecimal("100000"),
                BigDecimal.ZERO,
                new BigDecimal(transactionAmount),
                new BigDecimal("65000"),
                Map.of("sku_A", 100)
        );
    }
}
