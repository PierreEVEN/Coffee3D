package Core.UI.SubWindows;

import Core.IO.LogOutput.Log;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

import java.util.ArrayList;
import java.util.List;

public abstract class SubWindow {

    private static List<SubWindow> _windows;
    private ImBoolean _bDisplay;
    private String _windowName;
    protected boolean bHasMenuBar = false;

    public SubWindow(String windowName) {
        _bDisplay = new ImBoolean(true);
        if (_windows == null) _windows = new ArrayList<>();
        _windows.add(this);
        _windowName = windowName;
    }

    protected abstract void draw();

    private final void drawInternal() {
        if (!_bDisplay.get()) {
            _windows.remove(this);
            return;
        }

        if (_bDisplay.get()) {
            if (ImGui.begin(_windowName, _bDisplay, bHasMenuBar ? ImGuiWindowFlags.MenuBar : ImGuiWindowFlags.None)) {
                draw();
            }
            ImGui.end();
        }
    }

    public void close() { _bDisplay.set(false); }

    public static void DrawWindows() {
        if (_windows == null) return;
        for (int i = _windows.size() - 1; i >= 0; --i) {
            _windows.get(i).drawInternal();
        }
    }
}
