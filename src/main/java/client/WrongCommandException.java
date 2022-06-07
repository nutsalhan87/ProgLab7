package client;

/**
 * An exception class that describes an exception that is called if an invalid command is submitted for processing
 */

public class WrongCommandException extends Exception {
    public WrongCommandException() {
        super("Такой команды нет");
    }

    public WrongCommandException(String message) {
        super(message);
    }
}
