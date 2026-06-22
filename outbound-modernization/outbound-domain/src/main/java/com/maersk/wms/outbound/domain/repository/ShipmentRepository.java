package com.maersk.wms.outbound.domain.repository;

import com.maersk.wms.outbound.domain.Shipment;
import com.maersk.wms.outbound.domain.ShipmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Shipment entity.
 */
public interface ShipmentRepository {

    Optional<Shipment> findByKey(String shipmentKey);

    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    List<Shipment> findByStatus(ShipmentStatus status);

    List<Shipment> findByStorerKey(String storerKey);

    List<Shipment> findByOrderKey(String orderKey);

    List<Shipment> findByLoadKey(String loadKey);

    List<Shipment> findByShipDateRange(LocalDateTime fromDate, LocalDateTime toDate);

    List<Shipment> findReadyToShip(String storerKey);

    Shipment save(Shipment shipment);

    void delete(String shipmentKey);

    String generateShipmentKey();
}
