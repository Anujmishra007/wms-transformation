package com.maersk.wms.outbound.domain.picking_service.model;

import com.maersk.wms.outbound.shared.kernel.identifiers.PickDetailKey;
import com.maersk.wms.outbound.shared.kernel.identifiers.PickHeaderKey;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PickList - represents an assigned list of picks for a user/device.
 * Part of Picking Operations Service - List Handling module.
 */
@Data
@Builder
public class PickList {

    private String pickListId;
    private String assignedUser;
    private String assignedEquipment;

    // List attributes
    private PickListStatus status;
    private PickListType type;

    // Pick items
    @Builder.Default
    private List<PickDetailKey> pickDetails = new ArrayList<>();

    // Counts
    private int totalPicks;
    private int completedPicks;
    private int shortedPicks;

    // Route optimization
    private String route;
    private int estimatedMinutes;

    // Timing
    private LocalDateTime assignedTime;
    private LocalDateTime startTime;
    private LocalDateTime completeTime;

    // Audit
    private String addWho;
    private LocalDateTime addDate;

    // Business methods
    public boolean isComplete() {
        return completedPicks + shortedPicks >= totalPicks;
    }

    public int getRemainingPicks() {
        return totalPicks - completedPicks - shortedPicks;
    }

    public double getCompletionPercentage() {
        if (totalPicks == 0) return 0.0;
        return (double) (completedPicks + shortedPicks) / totalPicks * 100.0;
    }

    public void addPick(PickDetailKey pickDetailKey) {
        if (!pickDetails.contains(pickDetailKey)) {
            pickDetails.add(pickDetailKey);
            totalPicks++;
        }
    }

    public void removePick(PickDetailKey pickDetailKey) {
        if (pickDetails.remove(pickDetailKey)) {
            totalPicks--;
        }
    }
}
