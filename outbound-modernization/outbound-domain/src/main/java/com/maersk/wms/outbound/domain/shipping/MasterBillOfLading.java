package com.maersk.wms.outbound.domain.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Master Bill of Lading (MBOL) entity.
 * Maps to MBOL table in the legacy system.
 *
 * Legacy SP References:
 * - WM.lsp_MBOLPPLLoadPlan_Wrapper
 * - WM.lsp_MBOLPPLOrderType2_Wrapper
 * - WM.lsp_MBOLReleaseMoveTask_Wrapper
 * - WM.lsp_WaveGenMBOL
 * - WM.lsp_WaveMoveOrderToNewMBOL
 * - nsp_BackEndValidateMBOL
 * - nsp_BackEndShipped
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterBillOfLading {

    private String mbolKey;
    private String externalMbolKey;
    private String storerKey;
    private String waveKey;
    private String loadKey;

    private MbolStatus status;

    private String carrierKey;
    private String carrierCode;
    private String carrierName;
    private String carrierServiceCode;
    private String proNumber;
    private String trackingNumber;

    private String shipToName;
    private String shipToAddress1;
    private String shipToAddress2;
    private String shipToCity;
    private String shipToState;
    private String shipToZip;
    private String shipToCountry;

    private String shipFromName;
    private String shipFromAddress1;
    private String shipFromAddress2;
    private String shipFromCity;
    private String shipFromState;
    private String shipFromZip;
    private String shipFromCountry;

    private String door;
    private String route;
    private String stop;
    private String trailerNumber;
    private String sealNumber;

    private LocalDateTime expectedShipDate;
    private LocalDateTime actualShipDate;
    private LocalDateTime deliveryDate;

    private int totalOrders;
    private int totalCartons;
    private int totalPallets;
    private BigDecimal totalWeight;
    private String weightUom;
    private BigDecimal totalVolume;
    private String volumeUom;
    private BigDecimal freightCharge;
    private String currency;

    private String bolNumber;
    private String manifestKey;

    // User-defined fields matching legacy
    private String userDefine01;
    private String userDefine02;
    private String userDefine03;
    private String userDefine04;
    private String userDefine05;

    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    @Builder.Default
    private List<CommercialBillOfLading> cbols = new ArrayList<>();

    @Builder.Default
    private List<String> orderKeys = new ArrayList<>();

    /**
     * Check if MBOL can be shipped.
     */
    public boolean canShip() {
        return status == MbolStatus.MANIFESTED || status == MbolStatus.LOADED;
    }

    /**
     * Check if MBOL can be manifested.
     */
    public boolean canManifest() {
        return status == MbolStatus.NEW || status == MbolStatus.PACKED;
    }
}
