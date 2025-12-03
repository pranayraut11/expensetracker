package com.example.expensetracker.drools;

import com.example.expensetracker.model.RuleDefinition;
import com.example.expensetracker.repository.RuleDefinitionRepository;
import com.example.expensetracker.service.RuleManagementService;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DynamicRuleLoader {

    private final RuleDefinitionRepository repository;
    private final RuleManagementService ruleService;

    @Autowired
    public DynamicRuleLoader(RuleDefinitionRepository repository, RuleManagementService ruleService) {
        this.repository = repository;
        this.ruleService = ruleService;
    }

    public KieContainer loadKieContainer() {
        List<RuleDefinition> rules = repository.findAll();
        String drl = ruleService.buildDRLStringFromRules(rules);

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/resources/rules.drl", drl);
        KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
        if (kb.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
            throw new IllegalStateException("Error building DRL: " + kb.getResults().toString());
        }
        return ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
    }
}

