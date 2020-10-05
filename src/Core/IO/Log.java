package Core.IO;

public class Log {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";


    public static void Print(String message, LogVerbosity verbosity) {
        String color = ANSI_WHITE;
        switch (verbosity) {
            case DISPLAY -> color = ANSI_CYAN;
            case WARNING -> color = ANSI_YELLOW;
            case ERROR -> color = ANSI_RED;
            case FAIL -> color = ANSI_PURPLE;
        }

        System.out.println(color + message + ANSI_RESET);
    }

    public static void Display(String message) { Print(message, LogVerbosity.DISPLAY); }
    public static void Warning(String message) { Print(message, LogVerbosity.WARNING); }
    public static void Error(String message) { Print(message, LogVerbosity.ERROR); }
    public static void Fail(String message) { Print(message, LogVerbosity.FAIL); }

}
