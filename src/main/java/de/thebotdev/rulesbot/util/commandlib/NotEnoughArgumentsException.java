package de.thebotdev.rulesbot.util.commandlib;

public class NotEnoughArgumentsException extends CommandException {
    public NotEnoughArgumentsException() {
        super();
    }

    public NotEnoughArgumentsException(String message) {
        super(message);
    }

    public NotEnoughArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughArgumentsException(Throwable cause) {
        super(cause);
    }

    protected NotEnoughArgumentsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
