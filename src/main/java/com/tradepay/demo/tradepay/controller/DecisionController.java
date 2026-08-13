package com.tradepay.demo.tradepay.controller;

import com.tradepay.demo.tradepay.dto.CreditAssessment;
import com.tradepay.demo.tradepay.dto.DecisionRequest;
import com.tradepay.demo.tradepay.service.DecisionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/decision")
public class DecisionController {

    private final DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping
    public ResponseEntity<CreditAssessment> generateDecision(@RequestBody DecisionRequest request) {

        CreditAssessment assessment = this.decisionService.makeDecision(request);

        return ResponseEntity.ok(assessment);
    }
}
