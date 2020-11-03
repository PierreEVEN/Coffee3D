package coffee3D.editor.ui.tools;

import coffee3D.core.io.log.ILogSent;
import coffee3D.core.io.log.Log;
import coffee3D.core.io.log.LogMessage;
import coffee3D.core.ui.subWindows.SubWindow;
import imgui.ImGui;
import imgui.flag.ImGuiCol;


public class Console extends SubWindow {

    boolean bSentLog = true;

    public Console(String windowName) {
        super(windowName);
        Log.BindLogSent(message -> bSentLog = true);
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
        if (bSentLog) {
            bSentLog = false;
            ImGui.setScrollY(1000000);
        }
    }
}
