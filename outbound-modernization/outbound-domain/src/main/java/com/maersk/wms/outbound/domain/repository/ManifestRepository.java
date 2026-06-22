package com.maersk.wms.outbound.domain.repository;

import com.maersk.wms.outbound.domain.shipping.ManifestStatus;
import com.maersk.wms.outbound.domain.shipping.ShippingManifest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Shipping Manifest operations.
 */
public interface ManifestRepository {

    Optional<ShippingManifest> findByKey(String manifestKey);

    Optional<ShippingManifest> findByNumber(String manifestNumber);

    Optional<ShippingManifest> findOpenManifestForCarrier(String carrierCode, String facility);

    List<ShippingManifest> findByStatus(ManifestStatus status);

    List<ShippingManifest> findByCarrierAndDate(String carrierCode, LocalDate manifestDate);

    List<ShippingManifest> findByFacilityAndDate(String facility, LocalDate manifestDate);

    ShippingManifest save(ShippingManifest manifest);

    void updateStatus(String manifestKey, ManifestStatus status);

    void addMbol(String manifestKey, String mbolKey);

    void removeMbol(String manifestKey, String mbolKey);

    void close(String manifestKey);

    Optional<ShippingManifest> findByPickupConfirmation(String confirmationNumber);
}
