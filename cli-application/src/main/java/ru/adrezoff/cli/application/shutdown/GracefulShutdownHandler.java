package ru.adrezoff.cli.application.shutdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.adrezoff.cli.application.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Andrey Selin
 */
public class GracefulShutdownHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GracefulShutdownHandler.class);

    private final Application application;
    private final List<ShutdownListener> listeners;
    private final AtomicBoolean isShuttingDown;
    private final CountDownLatch shutdownLatch;
    private final long timeoutSeconds;

    public GracefulShutdownHandler(Application application) {
        this(application, 30);
    }

    public GracefulShutdownHandler(Application application, long timeoutSeconds) {
        this.application = application;
        this.listeners = new ArrayList<>();
        this.isShuttingDown = new AtomicBoolean(false);
        this.shutdownLatch = new CountDownLatch(1);
        this.timeoutSeconds = timeoutSeconds;
    }
    
    public void addListener(ShutdownListener listener) {
        listeners.add(listener);
        LOGGER.debug("Added shutdown listener: {}", listener.getClass().getSimpleName());
    }
    
    public void removeListener(ShutdownListener listener) {
        listeners.remove(listener);
        LOGGER.debug("Removed shutdown listener: {}", listener.getClass().getSimpleName());
    }


    public CompletableFuture<Void> shutdown() {
        if (!isShuttingDown.compareAndSet(false, true)) {
            LOGGER.info("Shutdown already in progress");
            return CompletableFuture.completedFuture(null);
        }

        LOGGER.info("Initiating graceful shutdown...");
        final long startTime = System.currentTimeMillis();

        return CompletableFuture.runAsync(() -> {
            try {
                LOGGER.info("Notifying {} shutdown listeners", listeners.size());
                for (ShutdownListener listener : listeners) {
                    try {
                        listener.onShutdown();
                    } catch (Exception e) {
                        LOGGER.error("Error notifying shutdown listener: {}",
                                listener.getClass().getSimpleName(), e);
                    }
                }

                if (application.isRunning()) {
                    LOGGER.info("Shutting down main application");
                    application.shutdown();
                }

                // Wait for operations to complete
                LOGGER.info("Waiting for operations to complete (timeout: {}s)", timeoutSeconds);
                boolean completed = shutdownLatch.await(timeoutSeconds, TimeUnit.SECONDS);

                if (completed) {
                    LOGGER.info("All operations completed successfully");
                } else {
                    LOGGER.warn("Shutdown timeout reached after {}s", timeoutSeconds);
                }

            } catch (InterruptedException e) {
                LOGGER.warn("Shutdown interrupted", e);
                Thread.currentThread().interrupt();
                throw new RuntimeException("Shutdown interrupted", e);
            } finally {
                long duration = System.currentTimeMillis() - startTime;
                LOGGER.info("Graceful shutdown completed in {}ms", duration);
            }
        });
    }
    
    public void shutdownComplete() {
        shutdownLatch.countDown();
        LOGGER.debug("Shutdown completion signal received");
    }
    
    public void forceShutdown() {
        LOGGER.warn("Forcing immediate shutdown");

        for (ShutdownListener listener : listeners) {
            try {
                listener.onForceShutdown();
            } catch (Exception e) {
                LOGGER.error("Error in force shutdown listener", e);
            }
        }

        System.exit(1);
    }
    
    public boolean isShuttingDown() {
        return isShuttingDown.get();
    }
}