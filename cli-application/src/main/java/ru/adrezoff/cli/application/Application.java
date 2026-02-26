package ru.adrezoff.cli.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.adrezoff.cli.application.command.base.HelpCommand;
import ru.adrezoff.cli.application.shutdown.GracefulShutdownHandler;
import ru.adrezoff.cli.application.ui.ConsoleUI;
import ru.adrezoff.cli.application.command.Command;
import ru.adrezoff.cli.application.command.CommandRegistry;
import ru.adrezoff.cli.application.command.base.ExitCommand;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Main class in application.
 *
 * @author Andrey Selin
 */
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private final CommandRegistry commandRegistry;
    private final ConsoleUI consoleUI;
    private final ExecutorService cliExecutor;
    private final GracefulShutdownHandler shutdownHandler;

    private volatile boolean running = true;
    private Future<?> cliTask;

    public Application() {
        this.commandRegistry = new CommandRegistry();
        this.consoleUI = new ConsoleUI();
        this.cliExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "cli-input-thread");
            t.setDaemon(false);
            return t;
        });
        this.shutdownHandler = new GracefulShutdownHandler(this);

        registerCommands();
        setupShutdownHook();

        LOGGER.info("Application initialized");
    }

    private void registerCommands() {
        commandRegistry.register(new HelpCommand(commandRegistry));
        commandRegistry.register(new ExitCommand(this));
    }

    private void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutdown hook triggered by JVM");
            shutdownHandler.shutdown();
        }, "jvm-shutdown-hook"));
    }

    public void start() {
        consoleUI.showWelcomeMessage();
        LOGGER.info("Application started");

        cliTask = cliExecutor.submit(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (running) {
                    try {
                        consoleUI.showPrompt();

                        if (scanner.hasNextLine()) {
                            String input = scanner.nextLine().trim();

                            if (input.isEmpty()) {
                                continue;
                            }

                            LOGGER.debug("Received command: {}", input);
                            processCommand(input);
                        }
                    } catch (Exception e) {
                        LOGGER.error("Error processing command", e);
                        consoleUI.showError("Ошибка: " + e.getMessage());
                    }
                }
            }
            LOGGER.info("CLI input thread finished");
        });

        try {
            cliTask.get();
        } catch (Exception e) {
            LOGGER.error("CLI task interrupted", e);
        } finally {
            shutdown();
        }
    }

    private void processCommand(String input) {
        String[] parts = input.split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        Command command = commandRegistry.getCommand(commandName);

        if (command != null) {
            try {
                LOGGER.info("Executing command: {} with args: {}", commandName, args);
                command.execute(args);
                LOGGER.debug("Command executed successfully: {}", commandName);
            } catch (Exception e) {
                LOGGER.error("Command execution failed: {}", commandName, e);
                consoleUI.showError("Ошибка выполнения команды: " + e.getMessage());
            }
        } else {
            LOGGER.warn("Unknown command: {}", commandName);
            consoleUI.showUnknownCommand(commandName);
        }
    }


    public void shutdown() {
        LOGGER.info("Initiating graceful shutdown...");

        if (!running) {
            LOGGER.info("Shutdown already in progress");
            return;
        }

        running = false;

        // Stop ExecutorService
        if (cliExecutor != null && !cliExecutor.isShutdown()) {
            cliExecutor.shutdown();
            try {
                if (!cliExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    LOGGER.warn("Executor did not terminate, forcing shutdown");
                    cliExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                LOGGER.error("Shutdown interrupted", e);
                cliExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        consoleUI.showGoodbye();
        LOGGER.info("Application shutdown complete");

        // Signal that shutdown is complete
        shutdownHandler.shutdownComplete();
    }

    public boolean isRunning() {
        return running;
    }

    public static void main(String[] args) {
        try {
            Application app = new Application();
            app.start();
        } catch (Exception e) {
            LoggerFactory.getLogger(Application.class).error("Fatal error", e);
            System.err.println("Fatal error: " + e.getMessage());
            System.exit(1);
        }
    }
}