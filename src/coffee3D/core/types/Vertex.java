package coffee3D.core.types;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.Serializable;

/**
 * Basically a vertex data wrapper
 */
public final class Vertex implements Serializable {
    /**
     * vertex position
     */
    public Vector3f position;
    /**
     * vertex texture coordinate
     */
    public Vector2f texCoords;
    /**
     * vertex normal
     */
    public Vector3f normals;
    /**
     * vertex color
     */
    public Vector4f vertexColor;

    public Vertex(Vector3f pos) {
        position = pos;
        texCoords = new Vector2f(0,0);
        normals = new Vector3f(0,0,1);
        vertexColor = new Vector4f(0,0,0,0);
    }

    public Vertex(Vector3f pos, Vector2f coords) {
        position = pos;
        texCoords = coords;
        normals = new Vector3f(0,0,1);
        vertexColor = new Vector4f(0,0,0,0);
    }

    public Vertex(Vector3f pos, Vector2f coords, Vector3f normal) {
        position = pos;
        texCoords = coords;
        normals = normal;
        vertexColor = new Vector4f(0,0,0,0);
    }

    public Vertex(Vector3f pos, Vector2f coords, Vector3f normal, Vector4f colors) {
        position = pos;
        texCoords = coords;
        normals = normal;
        vertexColor = colors;
    }

    /**
     * get structure byte size
     * @return byte count
     */
    public static int GetByteSize() { return GetFloatSize() * 4; }

    /**
     * get structure float size
     * @return float count
     */
    public static int GetFloatSize() {
        return 3 +//pos
                2 +//coords
                3 +//normal
                4;//color
    }
}
