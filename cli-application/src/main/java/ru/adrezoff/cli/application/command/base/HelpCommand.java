package ru.adrezoff.cli.application.command.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.adrezoff.cli.application.command.Command;
import ru.adrezoff.cli.application.command.CommandRegistry;

/**
 * @author Andrey Selin
 */
public class HelpCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelpCommand.class);
    private final CommandRegistry registry;

    public HelpCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Show available commands";
    }

    @Override
    public String getUsage() {
        return "help [command]";
    }

    @Override
    public void execute(String args) {
        LOGGER.info("Executing help command with args: {}", args);

        if (args.isEmpty()) {
            showAllCommands();
        } else {
            showCommandHelp(args);
        }
    }

    private void showAllCommands() {
        registry.getAllCommands().values().stream()
                .sorted((c1, c2) -> c1.getName().compareTo(c2.getName()))
                .forEach(cmd -> {
                    System.out.printf("  %-12s - %s%n", cmd.getName(), cmd.getDescription());
                });

        System.out.println("\nType 'help <command>' for specific command help");
    }

    private void showCommandHelp(String commandName) {
        Command cmd = registry.getCommand(commandName);

        if (cmd != null) {
            System.out.println("  Description: " + cmd.getDescription());
            System.out.println("  Usage: " + cmd.getUsage());
        } else {
            System.out.println("Unknown command: " + commandName);
        }
    }
}