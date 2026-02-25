package ru.adrezoff.cli.application.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.adrezoff.cli.application.Application;

/**
 * @author Andrey Selin
 */
public class ExitCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExitCommand.class);
    private final Application application;

    public ExitCommand(Application application) {
        this.application = application;
    }


    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "Exit the application gracefully";
    }

    @Override
    public String getUsage() {
        return "exit";
    }

    @Override
    public void execute(String args) throws Exception {
        LOGGER.info("Exit command received");
        application.shutdown();
    }
}