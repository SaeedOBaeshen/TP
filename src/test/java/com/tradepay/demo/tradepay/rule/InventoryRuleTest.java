package com.tradepay.demo.tradepay.rule;

import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.dto.RuleResult;
import com.tradepay.demo.tradepay.enums.DecisionStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InventoryRuleTest {

    private final InventoryRule rule = new InventoryRule();

    @Test
    void shouldApproveWhenInventoryExists() {

        DecisionRequest request = request(
                Map.of(
                        "sku_A", 100,
                        "sku_B", 50,
                        "sku_C", 200
                )
        );

        RuleResult result = rule.evaluate(request);

        assertEquals(DecisionStatus.APPROVED, result.status());

        assertEquals("INVENTORY_AVAILABLE", result.ruleCode());
    }

    @Test
    void shouldPartiallyApproveWhenInventoryIsMissing() {

        DecisionRequest request = request(null);

        RuleResult result = rule.evaluate(request);

        assertEquals(DecisionStatus.PARTIALLY_APPROVED, result.status());

        assertEquals("NO_INVENTORY_DATA", result.ruleCode());
    }

    @Test
    void shouldPartiallyApproveWhenInventoryIsEmpty() {

        DecisionRequest request = request(Collections.emptyMap());

        RuleResult result = rule.evaluate(request);

        assertEquals(DecisionStatus.PARTIALLY_APPROVED, result.status());

        assertEquals("NO_INVENTORY_DATA", result.ruleCode());
    }

    @Test
    void shouldDeclineNegativeInventory() {

        DecisionRequest request = request(
                Map.of(
                        "sku_A", 100,
                        "sku_B", -10
                )
        );

        RuleResult result = rule.evaluate(request);

        assertEquals(DecisionStatus.DECLINED, result.status());

        assertEquals("INVALID_INVENTORY", result.ruleCode());
    }

    @Test
    void shouldPartiallyApproveWhenAllInventoryIsZero() {

        DecisionRequest request = request(
                Map.of(
                        "sku_A", 0,
                        "sku_B", 0,
                        "sku_C", 0
                )
        );

        RuleResult result = rule.evaluate(request);

        assertEquals(DecisionStatus.PARTIALLY_APPROVED, result.status());

        assertEquals("ZERO_INVENTORY", result.ruleCode());
    }

    private DecisionRequest request(Map<String, Integer> inventory) {

        return new DecisionRequest(
                "M1021",
                "A small grocery store",
                "B",
                new BigDecimal("50000"),
                new BigDecimal("30000"),
                new BigDecimal("12000"),
                new BigDecimal("65000"),
                inventory
        );
    }
}
