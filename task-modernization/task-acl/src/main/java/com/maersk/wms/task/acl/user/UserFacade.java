package com.maersk.wms.task.acl.user;

import com.maersk.wms.task.shared.kernel.identifiers.*;

import java.util.List;
import java.util.Optional;

/**
 * Facade interface for User Service integration.
 * Used by Task Management for user-related task operations.
 */
public interface UserFacade {

    /**
     * Get user details.
     */
    Optional<UserDetails> getUserDetails(UserKey userId);

    /**
     * Get users by role.
     */
    List<UserDetails> getUsersByRole(String role);

    /**
     * Get users in work group.
     */
    List<UserDetails> getUsersInWorkGroup(String workGroupId);

    /**
     * Check if user is authorized for task type.
     */
    boolean isUserAuthorized(UserKey userId, String taskType);

    /**
     * Get user's authorized zones.
     */
    List<ZoneKey> getUserAuthorizedZones(UserKey userId);

    /**
     * Get user's skill level for task type.
     */
    int getUserSkillLevel(UserKey userId, String taskType);

    /**
     * Record for user details.
     */
    record UserDetails(
            UserKey userId,
            String userName,
            String role,
            String workGroupId,
            List<String> authorizedTaskTypes,
            List<ZoneKey> authorizedZones,
            boolean active
    ) {}
}
