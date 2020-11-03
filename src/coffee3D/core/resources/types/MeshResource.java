package coffee3D.core.resources.types;

import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.resources.GraphicResource;
import coffee3D.core.types.SphereBound;
import coffee3D.core.types.TypeHelper;
import coffee3D.core.types.Vertex;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL46.*;

/**
 * Handle index and vertex buffers of a mesh
 */
public class MeshResource extends GraphicResource {

    private final Vertex[] _vertices;
    private final int[] _indices;

    private int _meshVao, _meshEbo;

    private SphereBound _bound;


    public MeshResource(String resourceName, Vertex[] vertices, int[] indices) {
        super(resourceName);
        _vertices = vertices;
        _indices = indices;
    }

    public void load() {

        // Generate vertex buffer
        float[] serializedVertices = SerializeVertexArray(_vertices);
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(serializedVertices.length);
        verticesBuffer.put(serializedVertices);
        verticesBuffer.flip();

        // Generate index buffer
        IntBuffer indexData = BufferUtils.createIntBuffer(_indices.length);
        indexData.put(_indices);
        indexData.flip();


        // Generate and load vertex buffer
        _meshVao = glGenVertexArrays();
        glBindVertexArray(_meshVao);
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.GetByteSize(), 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.GetByteSize(), 3 * 4);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.GetByteSize(), 3 * 4 + 2 * 4);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 4, GL_FLOAT, false, Vertex.GetByteSize(), 3 * 4 + 2 * 4 + 3 * 4);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);

        // Generate and load index buffer
        _meshEbo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _meshEbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public static float[] SerializeVertexArray(Vertex[] vertices) {
        float[] data = new float[vertices.length * Vertex.GetFloatSize()];
        for (int i = 0; i < vertices.length; ++i) {
            // position
            data[i * Vertex.GetFloatSize()] = vertices[i].position.x;
            data[i * Vertex.GetFloatSize() + 1] = vertices[i].position.y;
            data[i * Vertex.GetFloatSize() + 2] = vertices[i].position.z;

            // texCoords
            data[i * Vertex.GetFloatSize() + 3] = vertices[i].texCoords.x;
            data[i * Vertex.GetFloatSize() + 4] = vertices[i].texCoords.y;

            // normal
            data[i * Vertex.GetFloatSize() + 5] = vertices[i].normals.x;
            data[i * Vertex.GetFloatSize() + 6] = vertices[i].normals.y;
            data[i * Vertex.GetFloatSize() + 7] = vertices[i].normals.z;

            // color
            data[i * Vertex.GetFloatSize() + 8] = vertices[i].vertexColor.x;
            data[i * Vertex.GetFloatSize() + 9] = vertices[i].vertexColor.y;
            data[i * Vertex.GetFloatSize() + 10] = vertices[i].vertexColor.z;
            data[i * Vertex.GetFloatSize() + 11] = vertices[i].vertexColor.w;
        }

        return data;
    }

    private void rebuildBound() {
        if (_bound == null) _bound = new SphereBound();
        _bound.position.zero();
        Vector3f temp = TypeHelper.getVector3();
        for (Vertex vert : _vertices) {
            _bound.position.add(vert.position);
        }
        _bound.position.div(_vertices.length);
        _bound.radius = 0f;
        for (Vertex vert : _vertices) {
            temp.x = vert.position.x;
            temp.y = vert.position.y;
            temp.z = vert.position.z;
            float length = temp.sub(_bound.position).length();
            if (length > _bound.radius) {
                _bound.radius = length;
            }
        }
    }

    public SphereBound getBound() {
        if (_bound == null) rebuildBound();
        return _bound;
    }

    public void unload() {}

    public void use(Scene context) {
        glBindVertexArray(_meshVao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _meshEbo);
        glDrawElements(GL_TRIANGLES, _indices.length, GL_UNSIGNED_INT, 0);
    }
}
