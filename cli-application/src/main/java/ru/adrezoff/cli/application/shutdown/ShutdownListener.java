package ru.adrezoff.cli.application.shutdown;

/**
 * @author Andrey Selin
 */
public interface ShutdownListener {
    default void onShutdown() {
        // Default empty implementation
    }

    default void onForceShutdown() {
        // Default empty implementation
    }
}