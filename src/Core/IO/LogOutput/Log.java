package Core.IO.LogOutput;

/**
 * Receive log events
 */
public class Log {

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
            case DISPLAY -> color = ANSI_CYAN;
            case WARNING -> color = ANSI_YELLOW;
            case ERROR -> color = ANSI_RED;
            case FAIL -> color = ANSI_PURPLE;
        }
        Logger.Get().print(message, color);
    }

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
        printStackTrace();
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
        for (int i = 1; i < elements.length; i++) {
            StackTraceElement s = elements[i];
            System.out.println("\tat " + s.getClassName() + "." + s.getMethodName()
                    + "(" + s.getFileName() + ":" + s.getLineNumber() + ")");
        }
        System.exit(1);
    }
}
