package Editor.UI.Tools;

import Core.UI.SubWindows.SubWindow;
import imgui.ImGui;

public class StyleEditor extends SubWindow {

    public StyleEditor(String windowName) {
        super(windowName);
    }

    @Override
    protected void draw() {
        ImGui.showStyleEditor();
    }
}
