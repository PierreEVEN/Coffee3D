package coffee3D.core.types;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;


public class SceneBufferData {
    public final Matrix4f viewMatrix = new Matrix4f();
    public final Matrix4f worldProjection = new Matrix4f();
    public final Matrix4f lightSpaceMatrix = new Matrix4f();
    public final Vector4f cameraPosition = new Vector4f();
    public final Vector4f cameraDirection = new Vector4f();
    public final Vector4f sunVector = new Vector4f();
    public final Vector2f framebufferSize = new Vector2f();
    public float time;
    public float shadowIntensity;
}
