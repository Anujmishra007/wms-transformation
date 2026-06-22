package com.maersk.wms.outbound.domain.repository;

import com.maersk.wms.outbound.domain.shipping.CbolStatus;
import com.maersk.wms.outbound.domain.shipping.CommercialBillOfLading;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Commercial Bill of Lading (CBOL) operations.
 *
 * Legacy Table Reference: CBOL
 */
public interface CbolRepository {

    CommercialBillOfLading save(CommercialBillOfLading cbol);

    Optional<CommercialBillOfLading> findByKey(String cbolKey);

    List<CommercialBillOfLading> findByMbolKey(String mbolKey);

    List<CommercialBillOfLading> findByOrderKey(String orderKey);

    List<CommercialBillOfLading> findByStatus(CbolStatus status);

    List<CommercialBillOfLading> findByTrackingNumber(String trackingNumber);

    List<CommercialBillOfLading> findByCarrierCodeAndStatus(String carrierCode, CbolStatus status);

    void deleteByKey(String cbolKey);

    int countByMbolKey(String mbolKey);
}
