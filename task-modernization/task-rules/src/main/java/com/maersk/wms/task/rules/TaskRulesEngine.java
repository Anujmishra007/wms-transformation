package com.maersk.wms.task.rules;

import com.maersk.wms.task.rules.facts.TaskPriorityFacts;
import com.maersk.wms.task.rules.facts.TaskPriorityResult;
import com.maersk.wms.task.rules.facts.TaskAssignmentFacts;
import com.maersk.wms.task.rules.facts.TaskAssignmentResult;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Component;

/**
 * Drools-based rules engine for task management business rules.
 */
@Component
public class TaskRulesEngine {

    private final KieContainer kieContainer;

    public TaskRulesEngine() {
        KieServices kieServices = KieServices.Factory.get();
        this.kieContainer = kieServices.getKieClasspathContainer();
    }

    /**
     * Evaluates task priority rules.
     */
    public TaskPriorityResult evaluatePriority(TaskPriorityFacts facts) {
        KieSession session = kieContainer.newKieSession("taskPrioritySession");
        try {
            TaskPriorityResult result = new TaskPriorityResult();
            session.insert(facts);
            session.insert(result);
            session.fireAllRules();
            return result;
        } finally {
            session.dispose();
        }
    }

    /**
     * Evaluates task assignment rules.
     */
    public TaskAssignmentResult evaluateAssignment(TaskAssignmentFacts facts) {
        KieSession session = kieContainer.newKieSession("taskAssignmentSession");
        try {
            TaskAssignmentResult result = new TaskAssignmentResult();
            session.insert(facts);
            session.insert(result);
            session.fireAllRules();
            return result;
        } finally {
            session.dispose();
        }
    }
}
