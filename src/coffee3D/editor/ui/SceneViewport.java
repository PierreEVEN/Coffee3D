package coffee3D.editor.ui;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.ui.subWindows.SubWindow;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class SceneViewport extends SubWindow {

    public RenderScene _sceneContext;

    public SceneViewport(RenderScene scene, String windowName) {
        super(windowName);
        _sceneContext = scene;
    }

    public RenderScene getScene() { return _sceneContext; }

    @Override
    protected void draw() {

        if (_sceneContext.getSettings().isFullScreen()) {
            Log.Warning("cannot render fullscreen scene in ImGui window");
            return;
        }

        _sceneContext.setResolution((int)ImGui.getContentRegionAvailX(), (int)ImGui.getContentRegionAvailY());

        if (ImGui.beginChild("windowContent", ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY(), false, ImGuiWindowFlags.NoInputs)) {
            _sceneContext.setPosition((int) ImGui.getWindowPosX(), (int) ImGui.getWindowPosY());
            ImGui.image(
                    _sceneContext.getSettings().enablePostProcess() ? _sceneContext.getPostProcessBuffer().getColorTexture() : _sceneContext.getColorFrameBuffer().getColorTexture(),
                    _sceneContext.getFbWidth(),
                    _sceneContext.getFbHeight(),
                    0, 1, 1, 0);
        }
        ImGui.endChild();
    }
}
