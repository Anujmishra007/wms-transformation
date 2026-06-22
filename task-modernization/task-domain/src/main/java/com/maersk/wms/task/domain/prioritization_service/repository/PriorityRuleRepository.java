package com.maersk.wms.task.domain.prioritization_service.repository;

import com.maersk.wms.task.domain.prioritization_service.model.PriorityRule;
import com.maersk.wms.task.domain.lifecycle_service.model.TaskType;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PriorityRule entity.
 */
public interface PriorityRuleRepository {

    PriorityRule save(PriorityRule rule);
    Optional<PriorityRule> findById(String ruleKey);
    void delete(String ruleKey);

    List<PriorityRule> findAll();
    List<PriorityRule> findActive();
    List<PriorityRule> findByType(PriorityRule.RuleType type);
    List<PriorityRule> findByTaskType(TaskType taskType);
    List<PriorityRule> findByCustomer(String customerId);
    List<PriorityRule> findEffective();

    List<PriorityRule> findApplicableRules(TaskType taskType, String customerId, String zone);
}
