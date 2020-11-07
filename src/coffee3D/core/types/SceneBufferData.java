package coffee3D.core.types;

import org.joml.Matrix4f;
import org.joml.Vector4f;


public class SceneBufferData {

    public final Matrix4f viewMatrix;
    public final Matrix4f worldProjection;
    public final Matrix4f lightSpaceMatrix;
    public final Vector4f cameraPosition;
    public final Vector4f cameraDirection;
    public final Vector4f sunVector;
    public float time;
    public float shadowIntensity;


    public SceneBufferData() {
        viewMatrix = new Matrix4f().identity();
        worldProjection = new Matrix4f().identity();
        lightSpaceMatrix = new Matrix4f().identity();
        cameraPosition = new Vector4f().zero();
        cameraDirection = new Vector4f(1,0,0, 0);
        sunVector = new Vector4f(0,0,-1, 0);
    }
}
