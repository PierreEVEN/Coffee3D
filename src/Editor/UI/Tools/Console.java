package Editor.UI.Tools;

import Core.IO.LogOutput.Log;
import Core.IO.LogOutput.LogMessage;
import Core.UI.SubWindows.SubWindow;
import imgui.ImGui;
import imgui.flag.ImGuiCol;


public class Console extends SubWindow {

    public Console(String windowName) {
        super(windowName);
    }

    @Override
    protected void draw() {
        for (LogMessage message : Log.GetLogHistory()) {

            switch (message.verbosity) {
                case DISPLAY -> ImGui.pushStyleColor(ImGuiCol.Text, .5f, .5f, 1f, 1f);
                case WARNING -> ImGui.pushStyleColor(ImGuiCol.Text, 1f, 1f, .5f, 1f);
                case ERROR -> ImGui.pushStyleColor(ImGuiCol.Text, 1f, .5f, .5f, 1f);
                case FAIL -> ImGui.pushStyleColor(ImGuiCol.Text, 1f, 1f, 1f, 1f);
            }

            ImGui.text(message.message);
            ImGui.popStyleColor();
        }
    }
}
