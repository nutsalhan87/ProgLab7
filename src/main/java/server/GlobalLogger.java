package server;

public class GlobalLogger {
    public static final org.apache.logging.log4j.Logger logger;
    static {
        logger = org.apache.logging.log4j.LogManager.getLogger();
    }
}
