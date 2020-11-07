package coffee3D.core.resources.types;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.resources.GraphicResource;
import coffee3D.core.types.SphereBound;
import coffee3D.core.types.TypeHelper;
import coffee3D.core.types.Vertex;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.lang.reflect.Type;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL46.*;

/**
 * Handle index and vertex buffers of a mesh
 */
public class MeshResource extends GraphicResource {

    private static MeshResource _lastDrawnMesh;
    private final int _meshVao, _meshEbo, _meshVbo, _indexCount;
    private final SphereBound _bound;
    private FloatBuffer _vertexBuffer;
    private IntBuffer _indexBuffer;

    public MeshResource(String resourceName, Vertex[] vertices, int[] indices) {
        super(resourceName);
        _meshVbo = glGenBuffers();
        _meshEbo = glGenBuffers();
        _meshVao = glGenVertexArrays();

        // Generate vertex cpu buffer
        final float[] serializedVertices = new float[TypeHelper.GetStructFloatSize(Vertex.class) * vertices.length];
        TypeHelper.SerializeStructure(vertices, serializedVertices);

        _vertexBuffer = BufferUtils.createFloatBuffer(serializedVertices.length);
        _vertexBuffer.put(serializedVertices);
        _vertexBuffer.flip();

        // Generate index cpu buffer
        _indexCount = indices.length;
        _indexBuffer = BufferUtils.createIntBuffer(_indexCount);
        _indexBuffer.put(indices);
        _indexBuffer.flip();

        _bound = new SphereBound();
        buildBounds(vertices);
    }

    public void load() {

        // Generate and load gpu vertex buffer
        int vertexSize = TypeHelper.GetStructByteSize(Vertex.class);
        glBindVertexArray(_meshVao);
        glBindBuffer(GL_ARRAY_BUFFER, _meshVbo);
        glBufferData(GL_ARRAY_BUFFER, _vertexBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, vertexSize, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, vertexSize, 3 * 4);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, vertexSize, 3 * 4 + 2 * 4);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 4, GL_FLOAT, false, vertexSize, 3 * 4 + 2 * 4 + 3 * 4);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        // Generate and load index buffer
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _meshEbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, _indexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        _vertexBuffer = null;
        _indexBuffer = null;
    }

    public void unload() {
        glDeleteVertexArrays(_meshVao);
        glDeleteBuffers(_meshEbo);
        glDeleteBuffers(_meshVbo);
    }

    private void buildBounds(Vertex[] vertices) {
        _bound.position.zero();
        Vector3f temp = TypeHelper.getVector3();
        for (Vertex vert : vertices) {
            _bound.position.add(vert.position);
        }
        _bound.position.div(vertices.length);
        _bound.radius = 0f;
        for (Vertex vert : vertices) {
            temp.x = vert.position.x;
            temp.y = vert.position.y;
            temp.z = vert.position.z;
            float length = temp.sub(_bound.position).length();
            if (length > _bound.radius) {
                _bound.radius = length;
            }
        }
    }

    public SphereBound getStaticBounds() {
        return _bound;
    }

    public void use(Scene context) {
        if (_lastDrawnMesh != this) {
            _lastDrawnMesh = this;
            glBindVertexArray(_meshVao);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _meshEbo);
        }
        glDrawElements(GL_TRIANGLES, _indexCount, GL_UNSIGNED_INT, 0);
    }
}
