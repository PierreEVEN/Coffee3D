package coffee3D.core.types;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class SceneBufferData {

    private FloatBuffer _bufferByteData;

    public SceneBufferData() {
        viewMatrix = new Matrix4f().identity();
        worldProjection = new Matrix4f().identity();
        lightSpaceMatrix = new Matrix4f().identity();
        cameraPosition = new Vector3f().zero();
        cameraDirection = new Vector3f(1, 0,0);
        sunVector = new Vector3f(0, 0, -1);
        _bufferByteData = BufferUtils.createFloatBuffer(GetFloatSize());
    }

    public Matrix4f viewMatrix;
    public Matrix4f worldProjection;
    public Matrix4f lightSpaceMatrix;
    public Vector3f cameraPosition;
    public Vector3f cameraDirection;
    public Vector3f sunVector;
    public float time;
    public float shadowIntensity;

    public static int GetByteSize() { return GetFloatSize() * 4; }

    public static int GetFloatSize() {
        return 16 + //view
        16 + // world
        16 + // lights
        4 + // cam pos
        4 + // cam dir
        4 + //sun
        1 + //time
        1; // shadow intensity
    }

    public FloatBuffer serializeData() {
        float[] matrixData = new float[16];
        _bufferByteData.clear();

        // View patrix
        viewMatrix.get(matrixData);
        for (int i = 0; i < 16; ++i) {
            _bufferByteData.put(matrixData[i]);
        }

        // Proj matrix
        worldProjection.get(matrixData);
        for (int i = 0; i < 16; ++i) {
            _bufferByteData.put(matrixData[i]);
        }

        // Proj matrix
        lightSpaceMatrix.get(matrixData);
        for (int i = 0; i < 16; ++i) {
            _bufferByteData.put(matrixData[i]);
        }

        // Cam pos
        _bufferByteData.put(cameraPosition.x);
        _bufferByteData.put(cameraPosition.y);
        _bufferByteData.put(cameraPosition.z);
        _bufferByteData.put(0);

        // Cam dir
        _bufferByteData.put(cameraDirection.x);
        _bufferByteData.put(cameraDirection.y);
        _bufferByteData.put(cameraDirection.z);
        _bufferByteData.put(0);

        // Sun
        _bufferByteData.put(sunVector.x);
        _bufferByteData.put(sunVector.y);
        _bufferByteData.put(sunVector.z);
        _bufferByteData.put(0);

        // time
        _bufferByteData.put(time);

        // shadow intensity
        _bufferByteData.put(shadowIntensity);

        _bufferByteData.flip();
        return _bufferByteData;
    }
}
