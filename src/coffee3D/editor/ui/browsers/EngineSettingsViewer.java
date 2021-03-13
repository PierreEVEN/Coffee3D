package coffee3D.editor.ui.browsers;

import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.io.settings.GameSettings;
import coffee3D.core.ui.subWindows.SubWindow;
import coffee3D.editor.ui.propertyHelper.StructureReader;
import imgui.ImGui;

public class EngineSettingsViewer extends SubWindow {
    public EngineSettingsViewer(String windowName) {
        super(windowName);
    }

    @Override
    protected void draw() {
        if (ImGui.button("save")) {
            EngineSettings.Get().saveSetting();
        }
        StructureReader.WriteObj(EngineSettings.Get(), "Engine settings");
    }
}
