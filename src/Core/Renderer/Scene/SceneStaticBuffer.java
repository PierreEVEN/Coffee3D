package Core.Renderer.Scene;

import Core.Resources.GraphicResource;
import Core.Types.SceneBufferData;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL46.*;

public class SceneStaticBuffer extends GraphicResource {

    private int _uboHandle;

    public SceneBufferData bufferData;

    public SceneStaticBuffer() {
        super("World static buffer");
        bufferData = new SceneBufferData();
    }

    @Override
    public void load() {
        _uboHandle = glGenBuffers();
        glBindBuffer(GL_UNIFORM_BUFFER, _uboHandle);
        float[] test = new float[] {4.f};
        glBufferData(GL_UNIFORM_BUFFER, test, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
        glBindBufferRange(GL_UNIFORM_BUFFER, 0, _uboHandle, 0, 4);
    }

    @Override
    public void unload() {

    }

    @Override
    public void use(Scene context) {
        bufferData.viewMatrix = new Matrix4f().identity();//context.getCamera().getViewMatrix();
        bufferData.worldProjection = new Matrix4f().identity();//context.getProjection();
        bufferData.cameraPosition = context.getCamera().getPosition();

        float[] data = new float[1];
        data[0] = (float)GLFW.glfwGetTime();



        glBindBuffer(GL_UNIFORM_BUFFER, _uboHandle);
        glBufferData(GL_UNIFORM_BUFFER, data, GL_WRITE_ONLY);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }
}
