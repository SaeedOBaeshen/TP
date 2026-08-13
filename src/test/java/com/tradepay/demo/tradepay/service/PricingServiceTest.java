package com.tradepay.demo.tradepay.service;

import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.dto.PricingResult;
import com.tradepay.demo.tradepay.enums.DecisionStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PricingServiceTest {

    private final PricingService pricingService =
            new PricingService();

    @Test
    void shouldReturnNoPricingWhenDeclined() {

        PricingResult result = pricingService.calculate(standardRequest(), DecisionStatus.DECLINED);

        assertEquals(BigDecimal.ZERO, result.interestRate());

        assertEquals("N/A", result.repaymentTermMonths());
    }

    @Test
    void shouldCalculatePricingForSampleTierBMerchant() {

        PricingResult result = pricingService.calculate(standardRequest(), DecisionStatus.APPROVED);

        /*
         * Tier B base rate = 1.50
         *
         * Utilization:
         * (30000 + 12000) / 50000 = 84%
         * +0.50
         *
         * Transaction / monthly volume:
         * 12000 / 65000 = 18.46%
         * -0.25
         *
         * Final = 1.75
         */
        assertEquals(new BigDecimal("1.75"), result.interestRate());

        assertEquals("60 days", result.repaymentTermMonths());
    }

    @Test
    void shouldReturn30DaysForPartialApproval() {

        PricingResult result = pricingService.calculate(standardRequest(), DecisionStatus.PARTIALLY_APPROVED);

        assertEquals("30 days", result.repaymentTermMonths());
    }

    @Test
    void shouldUseTierABaseRate() {

        DecisionRequest request = request(
                        "A",
                        "10000",
                        "5000",
                        "65000"
                );

        PricingResult result = pricingService.calculate(request, DecisionStatus.APPROVED);

        /*
         * Tier A = 1.00
         * Low utilization = no increase
         * Low transaction ratio = -0.25,
         * but minimum rate = 1.00.
         */
        assertEquals(new BigDecimal("1.00"), result.interestRate());

        assertEquals("90 days", result.repaymentTermMonths());
    }

    @Test
    void shouldIncreaseRateForHighUtilization() {

        DecisionRequest request = request(
                        "B",
                        "35000",
                        "10000",
                        "30000"
                );

        PricingResult result = pricingService.calculate(request, DecisionStatus.APPROVED);

        /*
         * Exposure:
         * 35000 + 10000 = 45000
         *
         * Utilization = 90%
         *
         * Tier B base = 1.50
         * High utilization = +0.50
         *
         * Transaction ratio:
         * 10000 / 30000 = 33.33%
         * No -0.25 discount.
         */
        assertEquals(new BigDecimal("2.00"), result.interestRate());

        assertEquals("60 days", result.repaymentTermMonths());
    }

    private DecisionRequest standardRequest() {

        return new DecisionRequest(
                "M1021",
                "A small grocery store in Riyadh",
                "B",
                new BigDecimal("50000"),
                new BigDecimal("30000"),
                new BigDecimal("12000"),
                new BigDecimal("65000"),
                Map.of(
                        "sku_A", 100,
                        "sku_B", 50,
                        "sku_C", 200
                )
        );
    }

    private DecisionRequest request(
            String riskTier,
            String currentExposure,
            String transactionAmount,
            String monthlyVolume
    ) {

        return new DecisionRequest(
                "M1021",
                "Test merchant",
                riskTier,
                new BigDecimal("50000"),
                new BigDecimal(currentExposure),
                new BigDecimal(transactionAmount),
                new BigDecimal(monthlyVolume),
                Map.of("sku_A", 100)
        );
    }
}
