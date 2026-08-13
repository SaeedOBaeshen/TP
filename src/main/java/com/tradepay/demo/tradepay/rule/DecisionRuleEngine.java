package com.tradepay.demo.tradepay.rule;

import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.dto.RuleResult;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class DecisionRuleEngine {

    private final List<DecisionRule> rules;

    public DecisionRuleEngine(List<DecisionRule> rules) {
        this.rules = rules.stream()
                .sorted(Comparator.comparingInt(DecisionRule::priority))
                .toList();
    }

    public List<RuleResult> evaluate(DecisionRequest request) {
        return rules.stream()
                .map(rule -> rule.evaluate(request))
                .toList();
    }
}
