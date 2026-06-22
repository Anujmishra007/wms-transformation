package com.maersk.wms.task.acl.user;

import com.maersk.wms.task.shared.kernel.identifiers.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserFacade.
 * Placeholder for actual service integration.
 */
@Component
public class UserFacadeImpl implements UserFacade {

    @Override
    public Optional<UserDetails> getUserDetails(UserKey userId) {
        // TODO: Connect to user service
        return Optional.empty();
    }

    @Override
    public List<UserDetails> getUsersByRole(String role) {
        // TODO: Connect to user service
        return Collections.emptyList();
    }

    @Override
    public List<UserDetails> getUsersInWorkGroup(String workGroupId) {
        // TODO: Connect to user service
        return Collections.emptyList();
    }

    @Override
    public boolean isUserAuthorized(UserKey userId, String taskType) {
        // TODO: Connect to user service
        return true;
    }

    @Override
    public List<ZoneKey> getUserAuthorizedZones(UserKey userId) {
        // TODO: Connect to user service
        return Collections.emptyList();
    }

    @Override
    public int getUserSkillLevel(UserKey userId, String taskType) {
        // TODO: Connect to user service
        return 50; // Default skill level
    }
}
