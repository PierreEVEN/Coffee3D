package coffee3D.editor.ui.tools;

import coffee3D.core.ui.subWindows.SubWindow;
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
