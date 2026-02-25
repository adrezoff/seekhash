package ru.adrezoff.cli.application.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Andrey Selin
 */
public class CommandRegistry {

    private final Map<String, Command> commands = new HashMap<>();

    public void register(Command command) {
        commands.put(command.getName(), command);
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }

    public Set<String> getCommandNames() {
        return commands.keySet();
    }

    public Map<String, Command> getAllCommands() {
        return Map.copyOf(commands);
    }
}