package com.maersk.wms.task.e2e;

import com.intuit.karate.junit5.Karate;

/**
 * Karate test runner for task management E2E tests.
 */
class TaskKarateRunner {

    @Karate.Test
    Karate testTaskLifecycle() {
        return Karate.run("tasks/task-create", "tasks/task-assign", "tasks/task-complete")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testTaskAssignment() {
        return Karate.run("tasks/task-auto-assign", "tasks/task-reassign")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testTaskGroups() {
        return Karate.run("groups/task-group-create", "groups/task-group-process")
                .relativeTo(getClass());
    }

    @Karate.Test
    Karate testTaskQueues() {
        return Karate.run("queues/task-queue-operations")
                .relativeTo(getClass());
    }
}
