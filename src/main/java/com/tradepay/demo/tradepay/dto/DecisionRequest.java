package com.tradepay.demo.tradepay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

public record DecisionRequest(
        @JsonProperty("merchant_id")
        String merchantId,

        @JsonProperty("merchant_business_description")
        String merchantBusinessDescription,

        @JsonProperty("risk_tier")
        String riskTier,

        @JsonProperty("credit_limit")
        BigDecimal creditLimit,

        @JsonProperty("current_exposure")
        BigDecimal currentExposure,

        @JsonProperty("transaction_amount")
        BigDecimal transactionAmount,

        @JsonProperty("monthly_purchase_volume")
        BigDecimal monthlyPurchaseVolume,

        @JsonProperty("inventory_level")
        Map<String, Integer> inventoryLevel
) {
}
