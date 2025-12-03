package com.example.expensetracker.service;

import com.example.expensetracker.drools.DynamicRuleLoader;
import com.example.expensetracker.model.Transaction;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DynamicDroolsService {

    private volatile KieContainer kieContainer;
    private final DynamicRuleLoader ruleLoader;

    @Autowired
    public DynamicDroolsService(DynamicRuleLoader ruleLoader) {
        this.ruleLoader = ruleLoader;
        this.kieContainer = ruleLoader.loadKieContainer();
    }

    public synchronized void reloadRules() {
        this.kieContainer = ruleLoader.loadKieContainer();
    }

    public void applyRules(Transaction t) {
        KieSession session = kieContainer.newKieSession();
        try {
            session.insert(t);
            session.fireAllRules();
        } finally {
            session.dispose();
        }
    }
}

