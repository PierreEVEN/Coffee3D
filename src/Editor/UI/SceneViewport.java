package Editor.UI;

import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.RenderScene;
import Core.UI.SubWindows.SubWindow;
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

        if (_sceneContext.getFramebuffer() == null) {
            Log.Warning("cannot render fullscreen scene in ImGui window");
            return;
        }

        int sizeX = (int)ImGui.getContentRegionAvailX();
        int sizeY = (int)ImGui.getContentRegionAvailY();

        if (sizeX != _sceneContext.getFramebuffer().getWidth() || sizeY != _sceneContext.getFramebuffer().getHeight()) {
            _sceneContext.getFramebuffer().resizeFramebuffer(sizeX, sizeY);
        }

        if (ImGui.beginChild("windowContent")) {
            _sceneContext.getFramebuffer().updateDrawPosition((int) ImGui.getWindowPosX(), (int) ImGui.getWindowPosY());
            ImGui.image(
                    _sceneContext.getFramebuffer().getColorBuffer(),
                    _sceneContext.getFramebuffer().getWidth(),
                    _sceneContext.getFramebuffer().getHeight(),
                    0, 1, 1, 0);
        }
        ImGui.endChild();
    }
}
