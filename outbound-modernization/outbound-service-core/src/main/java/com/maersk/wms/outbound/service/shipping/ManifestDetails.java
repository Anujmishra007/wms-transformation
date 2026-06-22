package com.maersk.wms.outbound.service.shipping;

import com.maersk.wms.outbound.domain.shipping.MasterBillOfLading;
import com.maersk.wms.outbound.domain.shipping.ShippingManifest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Manifest details with related MBOLs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManifestDetails {

    private ShippingManifest manifest;

    @Builder.Default
    private List<MasterBillOfLading> mbols = new ArrayList<>();

    private int totalMbols;
    private int totalPackages;
    private BigDecimal totalWeight;
    private BigDecimal totalVolume;
}
