package com.tradepay.demo.tradepay.rule;

import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.dto.RuleResult;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InventoryRule implements DecisionRule {

    @Override
    public RuleResult evaluate(DecisionRequest request) {

        Map<String, Integer> inventory = request.inventoryLevel();

        /*
         * Missing inventory information means we do not have
         * enough supporting information to fully approve.
         */
        if (inventory == null || inventory.isEmpty()) {
            return RuleResult.partialApproval(
                    "NO_INVENTORY_DATA",
                    "Inventory information is missing"
            );
        }

        /*
         * Negative inventory is invalid data.
         * Normally this should already be caught by
         * DecisionRequestValidator.
         */
        boolean hasNegativeQuantity =
                inventory.values()
                        .stream()
                        .anyMatch(quantity -> quantity != null && quantity < 0);

        if (hasNegativeQuantity) {
            return RuleResult.decline(
                    "INVALID_INVENTORY",
                    "Inventory quantities cannot be negative"
            );
        }

        /*
         * If every SKU has zero inventory, the merchant
         * has no current stock. We partially approve rather
         * than automatically approving the full transaction.
         */
        boolean allZero = inventory.values()
                        .stream()
                        .allMatch(quantity ->
                                quantity != null && quantity == 0
                        );

        if (allZero) {
            return RuleResult.partialApproval(
                    "ZERO_INVENTORY",
                    "Merchant currently has no inventory"
            );
        }

        return RuleResult.approve(
                "INVENTORY_AVAILABLE",
                "Merchant has available inventory"
        );
    }

    @Override
    public int priority() {
        return 50;
    }
}
