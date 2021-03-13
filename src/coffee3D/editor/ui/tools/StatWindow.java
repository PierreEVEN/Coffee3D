package coffee3D.editor.ui.tools;

import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.ui.subWindows.SubWindow;
import coffee3D.core.ui.tools.StatHelper;

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
