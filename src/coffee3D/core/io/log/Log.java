package coffee3D.core.io.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Receive log events
 */
public class Log {

    private static final List<LogMessage> _logHistory = new ArrayList<>();
    private static ArrayList<ILogSent> logInterfaces = new ArrayList<>();
    public static void BindLogSent(ILogSent item) {
        logInterfaces.add(item);
    }

    /** log colors */
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";


    /**
     * Print log
     * @param message   text
     * @param verbosity verbosity
     */
    public static void Print(String message, LogVerbosity verbosity) {
        String color = ANSI_WHITE;
        switch (verbosity) {
            case DISPLAY : color = ANSI_CYAN; break;
            case WARNING : color = ANSI_YELLOW; break;
            case ERROR : color = ANSI_RED; break;
            case FAIL : color = ANSI_PURPLE; break;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy:HH:mm:ss");
        Logger.Get().print("[" + dtf.format(LocalDateTime.now()) + "] " + message, color, verbosity);

        LogMessage logMessage = new LogMessage();
        logMessage.verbosity = verbosity;
        logMessage.message = message;
        _logHistory.add(logMessage);
        for (ILogSent item : logInterfaces) {
            item.OnLogSent(message);
        }
    }

    public static List<LogMessage> GetLogHistory() { return _logHistory; }

    /**
     * print display message
     * @param message text
     */
    public static void Display(String message) { Print(message, LogVerbosity.DISPLAY); }

    /**
     * print warning message
     * @param message text
     */
    public static void Warning(String message) { Print(message, LogVerbosity.WARNING); }

    /**
     * print error message
     * @param message text
     */
    public static void Error(String message) {
        Print(message, LogVerbosity.ERROR);
    }

    /**
     * print fail message
     * @param message text
     */
    public static void Fail(String message) {
        Print(message, LogVerbosity.FAIL);
        printStackTrace();
    }

    private static void printStackTrace() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (int i = 3; i < elements.length; i++) {
            StackTraceElement s = elements[i];
            System.out.println("\tat " + s.getClassName() + "." + s.getMethodName()
                    + "(" + s.getFileName() + ":" + s.getLineNumber() + ")");
        }
        System.exit(1);
    }
}
