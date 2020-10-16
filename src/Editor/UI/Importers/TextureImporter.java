package Editor.UI.Importers;

import Core.UI.SubWindows.SubWindow;
import Editor.UI.Browsers.FileBrowser;
import imgui.ImGui;

public class TextureImporter extends SubWindow {


    public TextureImporter(String windowName) {
        super(windowName);
    }

    @Override
    protected void draw() {
        if (ImGui.button("pick file")) {

        }
    }
}
