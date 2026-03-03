package com.example.expensetracker.service;

import com.example.expensetracker.drools.DynamicRuleLoader;
import com.example.expensetracker.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
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

    @Transactional
    public int applyRules(Transaction t) {
        KieSession session = kieContainer.newKieSession();
        try {
            log.info("═══════════════════════════════════════════════════════");
            log.info("🔥 APPLYING DROOLS RULES");
            log.info("Description: [{}]", t.getDescription());
            log.info("Current Category: [{}]", t.getCategory());
            log.info("═══════════════════════════════════════════════════════");

            session.insert(t);
            int rulesFired = session.fireAllRules();

            log.info("✅ Rules fired: {}", rulesFired);
            log.info("📋 New Category: [{}]", t.getCategory());
            log.info("═══════════════════════════════════════════════════════\n");

            return rulesFired;
        } finally {
            session.dispose();
        }
    }
}

