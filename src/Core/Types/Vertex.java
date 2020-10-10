package Core.Types;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.Serializable;

public class Vertex implements Serializable {
    public Vector3f position;
    public Vector2f texCoords;
    public Vector3f normals;
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

    public static int GetByteSize() { return GetFloatSize() * 4; }

    public static int GetFloatSize() {
        return 3 +//pos
                2 +//coords
                3 +//normal
                4;//color
    }
}
