package com.maersk.wms.inbound.domain.putaway_service;

/**
 * Rule mapping return dispositions to putaway zones.
 */
public class DispositionZoneRule {

    private String disposition;
    private String zone;
    private String locationType;
    private int priority;
    private boolean requiresInspection;
    private boolean requiresQualityHold;

    public DispositionZoneRule() {}

    public DispositionZoneRule(String disposition, String zone) {
        this.disposition = disposition;
        this.zone = zone;
    }

    public String getDisposition() { return disposition; }
    public void setDisposition(String disposition) { this.disposition = disposition; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public String getLocationType() { return locationType; }
    public void setLocationType(String locationType) { this.locationType = locationType; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public boolean isRequiresInspection() { return requiresInspection; }
    public void setRequiresInspection(boolean requiresInspection) { this.requiresInspection = requiresInspection; }

    public boolean isRequiresQualityHold() { return requiresQualityHold; }
    public void setRequiresQualityHold(boolean requiresQualityHold) { this.requiresQualityHold = requiresQualityHold; }
}
