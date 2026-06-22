package com.maersk.wms.infrastructure;

import com.maersk.wms.common.context.TenantContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Routing data source that selects tenant-specific database.
 * Replaces legacy DBTemplateFactory.getWMSJdbcTemplate(countryCode).
 */
public class MultiTenantDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getCountryCode();
    }
}
