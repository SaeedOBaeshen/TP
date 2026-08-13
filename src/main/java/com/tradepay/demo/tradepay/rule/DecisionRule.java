package com.tradepay.demo.tradepay.rule;

import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.dto.RuleResult;

public interface DecisionRule {

    RuleResult evaluate(DecisionRequest request);
    int priority();
}
