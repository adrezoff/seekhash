package ru.adrezoff.cli.application.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrey Selin
 */
public class ConsoleUI {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleUI.class);

    public void showWelcomeMessage() {
        System.out.println("\n=== seekhash v0.0.1 ===");
        System.out.println("Type 'help' for available commands");
        System.out.println();
    }

    public void showPrompt() {
        System.out.print("> ");
    }

    public void showGoodbye() {
        System.out.println("\nGoodbye!");
        System.out.println();
    }

    public void showError(String message) {
        System.out.println("ERROR: " + message);
    }

    public void showSuccess(String message) {
        System.out.println("SUCCESS: " + message);
    }

    public void showWarning(String message) {
        System.out.println("WARNING: " + message);
    }

    public void showInfo(String message) {
        System.out.println("INFO: " + message);
    }

    public void showUnknownCommand(String command) {
        System.out.println("Unknown command: " + command);
        System.out.println("Type 'help' for available commands");
    }

    public void showResult(String message) {
        System.out.println("RESULT: " + message);
    }

    public void showProgress(int current, int total, String message) {
        int percent = (int) (current * 100.0 / total);
        System.out.printf("\r[%d/%d] %d%% %s", current, total, percent, message);
        if (current >= total) {
            System.out.println();
        }
    }

    public void clearLine() {
        System.out.print("\r\033[K");
    }
}
