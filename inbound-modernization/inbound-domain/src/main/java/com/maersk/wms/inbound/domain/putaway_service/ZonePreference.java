package com.maersk.wms.inbound.domain.putaway_service;

/**
 * Zone preference configuration for putaway algorithms.
 */
public class ZonePreference {

    private String zone;
    private int sequence;
    private int priority;
    private String locationType;
    private boolean allowMixedSku;
    private boolean allowMixedLot;

    public ZonePreference() {}

    public ZonePreference(String zone, int sequence) {
        this.zone = zone;
        this.sequence = sequence;
    }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public int getSequence() { return sequence; }
    public void setSequence(int sequence) { this.sequence = sequence; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public String getLocationType() { return locationType; }
    public void setLocationType(String locationType) { this.locationType = locationType; }

    public boolean isAllowMixedSku() { return allowMixedSku; }
    public void setAllowMixedSku(boolean allowMixedSku) { this.allowMixedSku = allowMixedSku; }

    public boolean isAllowMixedLot() { return allowMixedLot; }
    public void setAllowMixedLot(boolean allowMixedLot) { this.allowMixedLot = allowMixedLot; }
}
