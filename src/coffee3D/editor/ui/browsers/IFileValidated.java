package coffee3D.editor.ui.browsers;

import java.io.File;

@FunctionalInterface
public interface IFileValidated {
    void applyFile(File file);
}
