package com.tradepay.demo.tradepay.validation;

import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.exception.InvalidDecisionRequestException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class DecisionRequestValidator {

    private static final Set<String> VALID_RISK_TIERS =  Set.of("A", "B", "C", "D");

    public void validate(DecisionRequest request) {

        if (request == null) {
            throw new InvalidDecisionRequestException(
                    "Request body cannot be null"
            );
        }

        if (isBlank(request.merchantId())) {
            throw new InvalidDecisionRequestException(
                    "merchantId is required"
            );
        }

        if (isBlank(request.riskTier())) {
            throw new InvalidDecisionRequestException(
                    "riskTier is required"
            );
        }

        String riskTier = request.riskTier().toUpperCase();

        if (!VALID_RISK_TIERS.contains(riskTier)) {
            throw new InvalidDecisionRequestException(
                    "riskTier must be one of A, B, C, or D"
            );
        }

        validatePositive(
                request.creditLimit(),
                "creditLimit"
        );

        validateNonNegative(
                request.currentExposure(),
                "currentExposure"
        );

        validatePositive(
                request.transactionAmount(),
                "transactionAmount"
        );

        validatePositive(
                request.monthlyPurchaseVolume(),
                "monthlyPurchaseVolume"
        );

        if (request.currentExposure()
                .compareTo(request.creditLimit()) > 0) {

            throw new InvalidDecisionRequestException(
                    "currentExposure cannot exceed creditLimit"
            );
        }

        if (request.inventoryLevel() != null) {

            boolean containsNullQuantity =
                    request.inventoryLevel()
                            .values()
                            .stream()
                            .anyMatch(value -> value == null);

            if (containsNullQuantity) {
                throw new InvalidDecisionRequestException(
                        "inventory quantities cannot be null"
                );
            }

            boolean containsNegativeQuantity =
                    request.inventoryLevel()
                            .values()
                            .stream()
                            .anyMatch(value -> value < 0);

            if (containsNegativeQuantity) {
                throw new InvalidDecisionRequestException(
                        "inventory quantities cannot be negative"
                );
            }
        }
    }

    private void validatePositive(BigDecimal value, String fieldName) {

        if (value == null) {
            throw new InvalidDecisionRequestException(
                    fieldName + " is required"
            );
        }

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDecisionRequestException(
                    fieldName + " must be greater than zero"
            );
        }
    }

    private void validateNonNegative(BigDecimal value, String fieldName) {

        if (value == null) {
            throw new InvalidDecisionRequestException(
                    fieldName + " is required"
            );
        }

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidDecisionRequestException(
                    fieldName + " cannot be negative"
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
