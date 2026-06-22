package com.maersk.wms.outbound.domain.picking_service.service;

import com.maersk.wms.outbound.domain.picking_service.model.PickDetailInfo;
import com.maersk.wms.outbound.domain.picking_service.model.PickList;
import com.maersk.wms.outbound.domain.picking_service.model.PickListType;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickDetailKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickHeaderKey;

import java.util.List;

/**
 * Service interface for Pick List Management.
 * Manages assignment and grouping of picks.
 * Part of Picking Operations Service bounded context.
 */
public interface PickListService {

    /**
     * Creates a pick list from pick headers.
     */
    PickList createPickList(CreatePickListCommand command);

    /**
     * Gets a pick list by ID.
     */
    PickList getPickList(String pickListId);

    /**
     * Gets pick lists assigned to a user.
     */
    List<PickList> getPickListsForUser(String userId);

    /**
     * Gets the next pick for a user.
     */
    PickDetailInfo getNextPick(String userId);

    /**
     * Assigns a pick list to a user.
     */
    PickList assignPickList(String pickListId, String userId);

    /**
     * Unassigns a pick list from a user.
     */
    PickList unassignPickList(String pickListId, String reason);

    /**
     * Adds a pick detail to a pick list.
     */
    PickList addToPickList(String pickListId, PickDetailKey pickDetailKey);

    /**
     * Removes a pick detail from a pick list.
     */
    PickList removeFromPickList(String pickListId, PickDetailKey pickDetailKey);

    /**
     * Optimizes the pick sequence in a list.
     */
    PickList optimizePickList(String pickListId);

    /**
     * Suspends a pick list.
     */
    PickList suspendPickList(String pickListId, String reason);

    /**
     * Resumes a suspended pick list.
     */
    PickList resumePickList(String pickListId);

    /**
     * Completes a pick list.
     */
    PickList completePickList(String pickListId, String userId);

    /**
     * Gets available pick lists (unassigned).
     */
    List<PickList> getAvailablePickLists(String zone, String equipmentType);

    /**
     * Command to create a pick list.
     */
    record CreatePickListCommand(
            List<PickHeaderKey> pickHeaders,
            PickListType type,
            String assignedUser,
            String zone,
            boolean optimizeRoute
    ) {}
}
