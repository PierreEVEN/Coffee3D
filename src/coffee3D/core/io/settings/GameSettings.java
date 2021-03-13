package coffee3D.core.io.settings;

import coffee3D.core.io.log.Log;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public abstract class GameSettings implements Serializable {

    private static final long serialVersionUID = 3695991487467546660L;

    public abstract String getFileName();
    public static String GetSavePath() { return "./saved/config/"; }

    private final static HashMap<String, GameSettings> _settings = new HashMap<>();

    public GameSettings() {
        _settings.put(getFileName(), this);
    }

    public void saveSetting() {
        try {
            File path = new File(GetSavePath());
            if (!path.exists()) path.mkdirs();

            FileOutputStream fos = new FileOutputStream(new File(GetSavePath() + getFileName() + ".conf"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
            Log.Display("successfully saved " + getFileName() + " settings");
        } catch (Exception e) {
            Log.Warning("failed to serialise " + getFileName() + " settings : " + e.getMessage());
        }
    }

    public static void SaveSettings() {
        for (GameSettings setting : _settings.values()) {
            setting.saveSetting();
        }
    }

    public static void ScanSettings() { ScanSettings(new File(GetSavePath())); }
    private static void ScanSettings(File path) {
        Log.Display("Read settings");
        if (!path.exists()) return;
        for (File subFile : Objects.requireNonNull(path.listFiles())) {
            if (subFile.isDirectory()) {
               ScanSettings(subFile);
            }
            else {
                Optional<String> extension = Optional.of(subFile.getName())
                        .filter(f -> f.contains("."))
                        .map(f -> f.substring(subFile.getName().lastIndexOf(".") + 1));
                if (!extension.isPresent()) continue;
                if (extension.get().equals("conf")) {
                    try {
                        FileInputStream fis = new FileInputStream(subFile);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        GameSettings item = (GameSettings) ois.readObject();
                        if (item != null) {
                            _settings.put(item.getFileName(), item);
                        }
                        ois.close();
                        fis.close();
                    } catch (Exception e) {
                        Log.Error("failed to deserialize settings " + subFile.getName() + " : " + e.getMessage());
                    }
                }
            }
        }
    }

    public static <T extends GameSettings> T GetSettings(String settingName) { return (T)_settings.get(settingName); }
}
