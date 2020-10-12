package Core.IO.LogOutput;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Logger {

    FileWriter _logWriter;

    private static Logger _logger;
    protected static Logger Get() {
        if (_logger == null) {
            _logger = new Logger();
        }
        return _logger;
    }

    private Logger() {
        try {

            if (!Files.exists(Path.of("./saved/logs"), LinkOption.NOFOLLOW_LINKS)) {
                Files.createDirectories(Path.of("./saved/logs"));
            }

            _logWriter = new FileWriter("./saved/logs/last.log");
        }
        catch (Exception e) {
            printToConsole("failed to write to log file");
        }
    }

    protected void print(String text, String colorString) {
        printToConsole(colorString + text);
        printToLog(text);
    }

    private void printToConsole(String text) {
        System.out.println(text);
    }

    private void printToLog(String text) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd HH:mm:ss");
            _logWriter.write(dtf.format(LocalDateTime.now()) + " - " + text + "\n");
            _logWriter.flush();
        }
        catch (Exception e) {
            printToConsole("failed to write to log file");
        }
    }

}
