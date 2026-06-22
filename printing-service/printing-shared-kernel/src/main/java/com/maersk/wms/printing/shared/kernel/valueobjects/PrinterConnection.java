package com.maersk.wms.printing.shared.kernel.valueobjects;

/**
 * Value object representing printer connection details.
 */
public record PrinterConnection(
        ConnectionType connectionType,
        String address,
        int port,
        String protocol,
        int timeoutMs,
        String credentials
) {
    public PrinterConnection {
        if (timeoutMs <= 0) {
            timeoutMs = 5000;
        }
        if (port <= 0) {
            port = connectionType == ConnectionType.NETWORK ? 9100 : 0;
        }
    }

    public enum ConnectionType {
        NETWORK,      // TCP/IP network printer
        USB,          // USB connected
        SERIAL,       // Serial/COM port
        PARALLEL,     // Parallel/LPT port
        BLUETOOTH,    // Bluetooth
        CLOUD         // Cloud print service
    }

    public static PrinterConnection network(String ipAddress, int port) {
        return new PrinterConnection(
                ConnectionType.NETWORK,
                ipAddress,
                port,
                "RAW",
                5000,
                null
        );
    }

    public static PrinterConnection usb(String devicePath) {
        return new PrinterConnection(
                ConnectionType.USB,
                devicePath,
                0,
                "USB",
                3000,
                null
        );
    }

    public String getConnectionString() {
        return switch (connectionType) {
            case NETWORK -> String.format("tcp://%s:%d", address, port);
            case USB -> String.format("usb://%s", address);
            case SERIAL -> String.format("serial://%s", address);
            case CLOUD -> String.format("cloud://%s", address);
            default -> address;
        };
    }

    public boolean isNetworkPrinter() {
        return connectionType == ConnectionType.NETWORK;
    }
}
