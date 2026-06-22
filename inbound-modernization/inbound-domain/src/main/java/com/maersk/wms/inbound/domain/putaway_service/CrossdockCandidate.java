package com.maersk.wms.inbound.domain.putaway_service;

import com.maersk.wms.inbound.shared.kernel.identifiers.LpnKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.SkuKey;
import com.maersk.wms.inbound.shared.kernel.identifiers.StorerKey;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Candidate item for crossdock evaluation.
 * Represents inbound inventory being checked for crossdock eligibility.
 */
public class CrossdockCandidate {

    private String receiptKey;
    private String receiptDetailKey;
    private StorerKey storerKey;
    private SkuKey skuKey;
    private LpnKey lpnKey;
    private BigDecimal quantity;
    private String lot;
    private LocalDate expiryDate;
    private String skuType;
    private boolean hazmat;
    private boolean temperatureControlled;
    private String orderKey;
    private String waveKey;
    private String carrierCode;
    private String routeKey;

    public CrossdockCandidate() {}

    // Getters and Setters
    public String getReceiptKey() { return receiptKey; }
    public void setReceiptKey(String receiptKey) { this.receiptKey = receiptKey; }

    public String getReceiptDetailKey() { return receiptDetailKey; }
    public void setReceiptDetailKey(String receiptDetailKey) { this.receiptDetailKey = receiptDetailKey; }

    public StorerKey getStorerKey() { return storerKey; }
    public void setStorerKey(StorerKey storerKey) { this.storerKey = storerKey; }

    public SkuKey getSkuKey() { return skuKey; }
    public void setSkuKey(SkuKey skuKey) { this.skuKey = skuKey; }

    public LpnKey getLpnKey() { return lpnKey; }
    public void setLpnKey(LpnKey lpnKey) { this.lpnKey = lpnKey; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public String getLot() { return lot; }
    public void setLot(String lot) { this.lot = lot; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getSkuType() { return skuType; }
    public void setSkuType(String skuType) { this.skuType = skuType; }

    public boolean isHazmat() { return hazmat; }
    public void setHazmat(boolean hazmat) { this.hazmat = hazmat; }

    public boolean isTemperatureControlled() { return temperatureControlled; }
    public void setTemperatureControlled(boolean temperatureControlled) { this.temperatureControlled = temperatureControlled; }

    public String getOrderKey() { return orderKey; }
    public void setOrderKey(String orderKey) { this.orderKey = orderKey; }

    public String getWaveKey() { return waveKey; }
    public void setWaveKey(String waveKey) { this.waveKey = waveKey; }

    public String getCarrierCode() { return carrierCode; }
    public void setCarrierCode(String carrierCode) { this.carrierCode = carrierCode; }

    public String getRouteKey() { return routeKey; }
    public void setRouteKey(String routeKey) { this.routeKey = routeKey; }

    public String getOrderType() {
        // Derived from order if available
        return null;
    }
}
