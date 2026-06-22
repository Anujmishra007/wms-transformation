package com.maersk.wms.task.activity;

import com.maersk.wms.task.domain.entity.Task;
import com.maersk.wms.task.domain.entity.TaskAssignment;
import com.maersk.wms.task.domain.entity.UserWorkload;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.List;

/**
 * Temporal activity interface for task assignment operations.
 */
@ActivityInterface
public interface TaskAssignmentActivities {

    @ActivityMethod
    TaskAssignment createAssignment(Task task, String userId, String assignedBy);

    @ActivityMethod
    void acceptAssignment(Long assignmentKey, String acceptedBy);

    @ActivityMethod
    void releaseAssignment(Long assignmentKey, String reason, String releasedBy);

    @ActivityMethod
    void completeAssignment(Long assignmentKey, String completedBy);

    @ActivityMethod
    void reassignTask(Long assignmentKey, String newUserId, String reason, String reassignedBy);

    @ActivityMethod
    List<UserWorkload> getAvailableUsers(String workGroup, String workZone);

    @ActivityMethod
    UserWorkload getLeastLoadedUser(String workGroup);

    @ActivityMethod
    String selectBestUser(Task task, List<UserWorkload> availableUsers);

    @ActivityMethod
    boolean validateUserCanAcceptTask(String userId, Task task);

    @ActivityMethod
    void updateUserWorkload(String userId, int taskDelta);
}
