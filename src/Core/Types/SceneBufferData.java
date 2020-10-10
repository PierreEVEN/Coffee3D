package Core.Types;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

public class SceneBufferData {

    public SceneBufferData() {
        viewMatrix = new Matrix4f().identity();
        worldProjection = new Matrix4f().identity();
        cameraPosition = new Vector3f().zero();
    }

    public Matrix4f viewMatrix;
    public Matrix4f worldProjection;
    public Vector3f cameraPosition;

    public static int GetByteSize() { return GetFloatSize() * 4; }

    public static int GetFloatSize() {
        return 16 + //view
        16 + // world
        3; // cam pos
    }

    public ByteBuffer serializeData() {
        float[] matrixData = new float[16];
        viewMatrix.get(matrixData);
        ByteBuffer result = BufferUtils.createByteBuffer(GetByteSize());
        for (int i = 0; i < 16; ++i) {
            result.putFloat(i * 4, matrixData[0]);
        }
        worldProjection.get(matrixData);
        for (int i = 0; i < 16; ++i) {
            result.putFloat((i + 16) * 4, matrixData[0]);
        }
        result.putFloat(32 * 4, cameraPosition.x);
        result.putFloat(33 * 4, cameraPosition.y);
        result.putFloat(34 * 4, cameraPosition.z);

        result.flip();

        return result;
    }
}
