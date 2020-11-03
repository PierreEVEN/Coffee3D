package coffee3D.core.renderer.scene.Components;

import coffee3D.core.renderer.debug.DebugRenderer;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.types.Color;
import coffee3D.core.types.TypeHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class GizmoComponent extends SceneComponent {
    private static final long serialVersionUID = -5687518908809847596L;

    /**
     * constructor
     *
     * @param position relative position
     * @param rotation relative rotation
     * @param scale    relative scale
     */
    public GizmoComponent(Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(position, rotation, scale);
    }

    @Override
    public void draw(Scene context) {
        super.draw(context);

        DebugRenderer.DrawDebugCylinder(context, getRelativePosition(), TypeHelper.getVector3(getForwardVector()).mul(1).add(getRelativePosition()), .05f, 20, Color.RED);
        DebugRenderer.DrawDebugCylinder(context, getRelativePosition(), TypeHelper.getVector3(getRightVector()).mul(1).add(getRelativePosition()), .05f, 20, Color.GREEN);
        DebugRenderer.DrawDebugCylinder(context, getRelativePosition(), TypeHelper.getVector3(getUpVector()).mul(1).add(getRelativePosition()), .05f, 20, Color.BLUE);

    }
}
