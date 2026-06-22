package com.maersk.wms.outbound.domain.repository;

import com.maersk.wms.outbound.domain.shipping.Carrier;
import com.maersk.wms.outbound.domain.shipping.CarrierStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Carrier operations.
 */
public interface CarrierRepository {

    Optional<Carrier> findByKey(String carrierKey);

    Optional<Carrier> findByCode(String carrierCode);

    List<Carrier> findByStatus(CarrierStatus status);

    List<Carrier> findActive();

    List<Carrier> findAll();

    Carrier save(Carrier carrier);

    void delete(String carrierKey);
}
