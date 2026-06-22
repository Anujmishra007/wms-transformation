package com.maersk.wms.masterdata.domain.repository;

import com.maersk.wms.masterdata.domain.Carrier;
import com.maersk.wms.masterdata.domain.CarrierStatus;
import com.maersk.wms.masterdata.domain.CarrierType;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Carrier entities.
 */
public interface CarrierRepository {

    Carrier save(Carrier carrier);

    Optional<Carrier> findById(Long id);

    Optional<Carrier> findByCarrierCode(String carrierCode);

    Optional<Carrier> findByScacCode(String scacCode);

    List<Carrier> findByStatus(CarrierStatus status);

    List<Carrier> findByCarrierType(CarrierType carrierType);

    List<Carrier> findActiveCarriers();

    List<Carrier> findAll();

    void delete(Carrier carrier);

    boolean existsByCarrierCode(String carrierCode);
}
