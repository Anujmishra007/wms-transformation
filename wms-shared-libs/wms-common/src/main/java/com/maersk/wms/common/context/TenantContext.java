package com.maersk.wms.common.context;

/**
 * Thread-local context for multi-tenant operations.
 */
public final class TenantContext {

    private static final ThreadLocal<TenantInfo> CONTEXT = new ThreadLocal<>();

    private TenantContext() {}

    public static void set(TenantInfo tenantInfo) {
        CONTEXT.set(tenantInfo);
    }

    public static TenantInfo get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }

    public static String getCountryCode() {
        TenantInfo info = get();
        return info != null ? info.getCountryCode() : null;
    }

    public static String getClientCode() {
        TenantInfo info = get();
        return info != null ? info.getClientCode() : null;
    }

    public static String getWarehouseCode() {
        TenantInfo info = get();
        return info != null ? info.getWarehouseCode() : null;
    }

    public static String getUserId() {
        TenantInfo info = get();
        return info != null ? info.getUserId() : null;
    }
}
