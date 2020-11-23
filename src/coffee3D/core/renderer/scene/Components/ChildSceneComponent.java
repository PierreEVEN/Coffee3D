package coffee3D.core.renderer.scene.Components;

import coffee3D.core.renderer.scene.IScene;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.renderer.scene.SceneComponent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ChildSceneComponent extends SceneComponent {
    private static final long serialVersionUID = 9095446481677700608L;

    private final IScene _child;

    /**
     * constructor
     *
     * @param position relative position
     * @param rotation relative rotation
     * @param scale    relative scale
     */
    public ChildSceneComponent(IScene inScene, Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(position, rotation, scale);
        _child = inScene;
    }


    @Override
    protected void preDraw(Scene context) {
        super.preDraw(context);
    }

    @Override
    protected void draw(Scene context) {
        super.draw(context);
    }

    @Override
    protected void postDraw(Scene context) {
        super.postDraw(context);
    }
}
