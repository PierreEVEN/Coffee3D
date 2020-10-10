package Core.Resources;

import Core.Renderer.Scene.Scene;
import Core.Types.Vertex;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL46.*;

public class MeshResource extends GraphicResource {

    private final Vertex[] _vertices;
    private final int[] _indices;

    private int _meshVao;
    private int _meshEbo;

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

    public void unload() {

    }

    public void use(Scene context) {
        glBindVertexArray(_meshVao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _meshEbo);
        glDrawElements(GL_TRIANGLES, _indices.length, GL_UNSIGNED_INT, 0);
    }
}
