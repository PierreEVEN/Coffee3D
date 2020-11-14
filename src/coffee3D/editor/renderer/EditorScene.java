package coffee3D.editor.renderer;

import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.scene.RenderSceneSettings;
import coffee3D.editor.components.GizmoComponent;

public class EditorScene extends RenderScene {

    private final GizmoComponent _gizmo = new GizmoComponent();


    public EditorScene(RenderSceneSettings settings) {
        super(settings);
    }

    @Override
    public void postDraw() {
        super.postDraw();

        _gizmo.draw(this);
    }

    public GizmoComponent getGizmo() {
        return _gizmo;
    }
}
