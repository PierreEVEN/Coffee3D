package Editor.UI.Tools;

import Core.Renderer.Scene.RenderScene;
import Core.UI.SubWindows.SubWindow;
import Core.UI.Tools.StatHelper;

public class StatWindow extends SubWindow {

    private final RenderScene _drawScene;

    public StatWindow(RenderScene desiredScene, String windowName) {
        super(windowName);
        _drawScene = desiredScene;
    }

    @Override
    protected void draw() {
        StatHelper.DrawStats(_drawScene);
    }
}
