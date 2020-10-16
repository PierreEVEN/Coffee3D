package Editor.UI.Browsers;

import java.io.File;

@FunctionalInterface
public interface IFileValidated {
    void applyFile(File file);
}
