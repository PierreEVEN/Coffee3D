package Core.Renderer.Scene;

import Core.IO.LogOutput.Log;
import Core.Renderer.RenderUtils;
import Core.Resources.GraphicResource;
import Core.Types.SceneBufferData;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL46.*;

public class SceneStaticBuffer extends GraphicResource {

    private int _uboHandle;

    private SceneBufferData bufferData;

    public SceneStaticBuffer() {
        super("World static buffer");
        bufferData = new SceneBufferData();
    }


    @Override
    public void load() {
        _uboHandle = glGenBuffers();
        glBindBuffer(GL_UNIFORM_BUFFER, _uboHandle);
        glBufferData(GL_UNIFORM_BUFFER, bufferData.GetByteSize(), GL_STATIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
        glBindBufferRange(GL_UNIFORM_BUFFER, 0, _uboHandle, 0, bufferData.GetByteSize());
    }

    public int getBufferHandle() {
        return _uboHandle;
    }

    @Override
    public void unload() {

    }

    @Override
    public void use(Scene context) {
        bufferData.viewMatrix = context.getCamera().getViewMatrix();
        bufferData.worldProjection = context.getProjection();
        bufferData.cameraPosition = context.getCamera().getRelativePosition();
        bufferData.cameraDirection = context.getCamera().getUpVector();
        bufferData.time = (float)GLFW.glfwGetTime();

        glBindBuffer(GL_UNIFORM_BUFFER, _uboHandle);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, bufferData.serializeData());
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }
}
