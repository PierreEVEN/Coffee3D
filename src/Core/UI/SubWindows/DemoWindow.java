package Core.UI.SubWindows;

import imgui.ImGui;

public class DemoWindow extends SubWindow {
    public DemoWindow(String windowName) {
        super(windowName);
    }

    @Override
    protected void draw() {
        ImGui.showDemoWindow();
    }
}
