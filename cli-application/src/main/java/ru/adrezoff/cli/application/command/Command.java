package ru.adrezoff.cli.application.command;

/**
 * @author Andrey Selin
 */
public interface Command {
    String getName();

    String getDescription();

    String getUsage();

    void execute(String args) throws Exception;
}