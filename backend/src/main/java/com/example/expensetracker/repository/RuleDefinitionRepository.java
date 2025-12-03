package com.example.expensetracker.repository;

import com.example.expensetracker.model.RuleDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuleDefinitionRepository extends JpaRepository<RuleDefinition, Long> {
    Optional<RuleDefinition> findByRuleName(String ruleName);
}

