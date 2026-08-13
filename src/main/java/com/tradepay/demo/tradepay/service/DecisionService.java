package com.tradepay.demo.tradepay.service;

import com.tradepay.demo.tradepay.dto.CreditAssessment;
import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.dto.PricingResult;
import com.tradepay.demo.tradepay.dto.RuleResult;
import com.tradepay.demo.tradepay.enums.DecisionStatus;
import com.tradepay.demo.tradepay.rule.DecisionRuleEngine;
import com.tradepay.demo.tradepay.validation.DecisionRequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DecisionService {

    private static final Logger log = LoggerFactory.getLogger(DecisionService.class);


    private final DecisionRequestValidator validator;
    private final DecisionRuleEngine ruleEngine;
    private final PricingService pricingService;

    public DecisionService(DecisionRequestValidator validator, DecisionRuleEngine ruleEngine, PricingService pricingService) {
        this.validator = validator;
        this.ruleEngine = ruleEngine;
        this.pricingService = pricingService;
    }

    public CreditAssessment makeDecision(DecisionRequest request) {

        log.info(
                "Starting decision for merchantId={}, transactionAmount={}",
                request.merchantId(),
                request.transactionAmount()
        );

        // 1. Validate request
        validator.validate(request);

        // 2. Run all decision rules
        List<RuleResult> ruleResults = ruleEngine.evaluate(request);

        ruleResults.forEach(result ->
                log.info(
                        "Decision rule evaluated: merchantId={}, ruleCode={}, status={}, message={}",
                        request.merchantId(),
                        result.ruleCode(),
                        result.status(),
                        result.message()
                )
        );


        // 3. Determine overall decision
        DecisionStatus decision = determineFinalDecision(ruleResults);

        // 4. Determine how much we can approve
        BigDecimal approvedAmount = calculateApprovedAmount(request, decision);

        // 5. Calculate interest rate and repayment terms
        PricingResult pricing = pricingService.calculate(request, decision);

        // 6. Build explanation from the rules
        String reason = buildReason(ruleResults, decision);

        log.info(
                "Decision completed: merchantId={}, decision={}, requestedAmount={}, approvedAmount={}, interestRate={}, repaymentTerms={}, reason={}",
                request.merchantId(),
                decision,
                request.transactionAmount(),
                approvedAmount,
                pricing.interestRate(),
                pricing.repaymentTermMonths(),
                reason
        );

        // 7. Return API response
        return new CreditAssessment(
                decision.name(),
                approvedAmount,
                pricing.interestRate(),
                pricing.repaymentTermMonths(),
                reason
        );
    }

    private DecisionStatus determineFinalDecision(List<RuleResult> results) {

        boolean declined = results.stream()
                        .anyMatch(result -> result.status() == DecisionStatus.DECLINED);

        if (declined) {
            return DecisionStatus.DECLINED;
        }

        boolean partiallyApproved = results.stream().anyMatch(result -> result.status() == DecisionStatus.PARTIALLY_APPROVED);

        if (partiallyApproved) {
            return DecisionStatus.PARTIALLY_APPROVED;
        }

        return DecisionStatus.APPROVED;
    }

    private BigDecimal calculateApprovedAmount(DecisionRequest request, DecisionStatus decision) {

        // Nothing should be financed if declined.
        if (decision == DecisionStatus.DECLINED) {
            return BigDecimal.ZERO;
        }

        BigDecimal availableCredit = request.creditLimit().subtract(request.currentExposure());

        /*
         * Never approve more than:
         *
         * 1. the requested transaction amount, or
         * 2. the merchant's available credit.
         */
        return request.transactionAmount().min(availableCredit).max(BigDecimal.ZERO);
    }

    private String buildReason(List<RuleResult> results, DecisionStatus decision) {

        /*
         * For APPROVED transactions, we don't necessarily
         * need every positive rule message.
         */
        if (decision == DecisionStatus.APPROVED) {

            return "The transaction satisfies the merchant's "
                    + "credit and risk requirements.";
        }

        /*
         * For partial approvals and declines, return the
         * relevant rule explanations.
         */
        return results.stream()
                .filter(result ->
                        result.status() != DecisionStatus.APPROVED
                )
                .map(RuleResult::message)
                .collect(Collectors.joining(". "));
    }
}
