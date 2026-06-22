package com.maersk.wms.outbound.domain.repository;

import com.maersk.wms.outbound.domain.shipping.LabelStatus;
import com.maersk.wms.outbound.domain.shipping.ShippingLabel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Shipping Label operations.
 *
 * Legacy Table Reference: SHIPPINGLABEL (or embedded in CBOL)
 */
public interface LabelRepository {

    ShippingLabel save(ShippingLabel label);

    Optional<ShippingLabel> findByKey(String labelKey);

    Optional<ShippingLabel> findByTrackingNumber(String trackingNumber);

    List<ShippingLabel> findByCbolKey(String cbolKey);

    List<ShippingLabel> findByMbolKey(String mbolKey);

    List<ShippingLabel> findByStatus(LabelStatus status);

    List<ShippingLabel> findByCarrierCode(String carrierCode);

    List<ShippingLabel> findPendingPrintSince(LocalDateTime since);

    List<ShippingLabel> findVoidedBetween(LocalDateTime start, LocalDateTime end);

    void deleteByKey(String labelKey);

    int countByMbolKey(String mbolKey);
}
