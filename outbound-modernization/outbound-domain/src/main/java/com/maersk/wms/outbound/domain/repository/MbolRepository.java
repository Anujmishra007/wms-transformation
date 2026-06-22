package com.maersk.wms.outbound.domain.repository;

import com.maersk.wms.outbound.domain.shipping.MasterBillOfLading;
import com.maersk.wms.outbound.domain.shipping.MbolStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Master Bill of Lading operations.
 */
public interface MbolRepository {

    Optional<MasterBillOfLading> findByKey(String mbolKey);

    Optional<MasterBillOfLading> findByExternalKey(String externalMbolKey);

    Optional<MasterBillOfLading> findByTrackingNumber(String trackingNumber);

    List<MasterBillOfLading> findByWaveKey(String waveKey);

    List<MasterBillOfLading> findByLoadKey(String loadKey);

    List<MasterBillOfLading> findByStatus(MbolStatus status);

    List<MasterBillOfLading> findByCarrierAndDateRange(String carrierCode, LocalDateTime from, LocalDateTime to);

    List<MasterBillOfLading> findByStorerAndDateRange(String storerKey, LocalDateTime from, LocalDateTime to);

    MasterBillOfLading save(MasterBillOfLading mbol);

    void updateStatus(String mbolKey, MbolStatus status);

    void delete(String mbolKey);
}
