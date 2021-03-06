package coffee3D.core.io.log;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;


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
            File logPath = new File("./saved");
            if (!logPath.exists()) {
                logPath.mkdir();
            }
            logPath = new File("./saved/logs");
            if (!logPath.exists()) {
                logPath.mkdir();
            }

            _logWriter = new FileWriter("./saved/logs/last.log");
        }
        catch (Exception e) {
            printToConsole("failed to write to log file");
        }
    }

    protected void print(String text, String colorString, LogVerbosity verbosity) {
        printToConsole(colorString + text);
        printToLog(text);
    }

    private void printToConsole(String text) {
        System.out.println(text);
    }

    private void printToLog(String text) {
        try {
            _logWriter.write(text + "\n");
            _logWriter.flush();
        }
        catch (Exception e) {
            printToConsole("failed to write to log file");
        }
    }

}
