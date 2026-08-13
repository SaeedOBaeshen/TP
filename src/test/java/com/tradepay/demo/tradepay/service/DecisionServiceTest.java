package com.tradepay.demo.tradepay.service;

import com.tradepay.demo.tradepay.dto.CreditAssessment;
import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.dto.PricingResult;
import com.tradepay.demo.tradepay.dto.RuleResult;
import com.tradepay.demo.tradepay.enums.DecisionStatus;
import com.tradepay.demo.tradepay.rule.DecisionRuleEngine;
import com.tradepay.demo.tradepay.validation.DecisionRequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DecisionServiceTest {

    @Mock
    private DecisionRequestValidator validator;

    @Mock
    private DecisionRuleEngine ruleEngine;

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private DecisionService decisionService;

    @Test
    void shouldApproveWhenAllRulesApprove() {

        DecisionRequest request = request();

        when(ruleEngine.evaluate(request))
                .thenReturn(List.of(
                        RuleResult.approve(
                                "RULE_1",
                                "Approved"
                        ),
                        RuleResult.approve(
                                "RULE_2",
                                "Approved"
                        )
                ));

        when(pricingService.calculate(request, DecisionStatus.APPROVED))
                .thenReturn(
                new PricingResult(new BigDecimal("1.50"), "60 days")
        );

        CreditAssessment result = decisionService.makeDecision(request);

        assertEquals("APPROVED", result.decision());

        assertEquals(new BigDecimal("12000"), result.approvedAmount()
        );

        verify(validator).validate(request);
        verify(ruleEngine).evaluate(request);
    }

    @Test
    void shouldPartiallyApproveIfAnyRuleReturnsPartialApproval() {

        DecisionRequest request = request();

        when(ruleEngine.evaluate(request))
                .thenReturn(List.of(
                        RuleResult.approve("RULE_1", "Approved"),
                        RuleResult.partialApproval("HIGH_UTILIZATION", "High utilization")
                ));

        when(pricingService.calculate(request, DecisionStatus.PARTIALLY_APPROVED))
                .thenReturn(
                new PricingResult(new BigDecimal("1.75"), "30 days")
        );

        CreditAssessment result = decisionService.makeDecision(request);

        assertEquals("PARTIALLY_APPROVED", result.decision());
    }

    @Test
    void shouldDeclineIfAnyRuleDeclines() {

        DecisionRequest request = request();

        when(ruleEngine.evaluate(request))
                .thenReturn(List.of(
                        RuleResult.approve(
                                "RULE_1",
                                "Approved"
                        ),
                        RuleResult.decline(
                                "CREDIT_LIMIT_EXCEEDED",
                                "Credit limit exceeded"
                        )
                ));

        when(pricingService.calculate(request, DecisionStatus.DECLINED))
                .thenReturn(new PricingResult(BigDecimal.ZERO, "N/A"));

        CreditAssessment result = decisionService.makeDecision(request);

        assertEquals("DECLINED", result.decision());

        assertEquals(BigDecimal.ZERO, result.approvedAmount());
    }

    private DecisionRequest request() {

        return new DecisionRequest(
                "M1021",
                "A small grocery store",
                "B",
                new BigDecimal("50000"),
                new BigDecimal("30000"),
                new BigDecimal("12000"),
                new BigDecimal("65000"),
                Map.of("sku_A", 100, "sku_B", 50)
        );
    }
}
