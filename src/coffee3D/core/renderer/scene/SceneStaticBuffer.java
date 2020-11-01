package coffee3D.core.renderer.scene;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.Components.Camera;
import coffee3D.core.resources.GraphicResource;
import coffee3D.core.types.SceneBufferData;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL46.*;

public class SceneStaticBuffer extends GraphicResource {

    private static int _uboHandle = -1;

    private SceneBufferData bufferData;
    private static int _bufferId = 0;

    public SceneStaticBuffer() {
        super("World static buffer" + _bufferId++);
        bufferData = new SceneBufferData();
    }


    @Override
    public void load() {
        if (_uboHandle == -1) {
            _uboHandle = glGenBuffers();
            glBindBuffer(GL_UNIFORM_BUFFER, _uboHandle);
            glBufferData(GL_UNIFORM_BUFFER, bufferData.GetByteSize(), GL_STATIC_DRAW);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);
            glBindBufferRange(GL_UNIFORM_BUFFER, 0, _uboHandle, 0, bufferData.GetByteSize());
            glBindBuffer(GL_UNIFORM_BUFFER, 0);
        }
    }

    public int getBufferHandle() {
        return _uboHandle;
    }

    @Override
    public void unload() {

    }

    @Override
    public void use(Scene context) {
        Log.Fail("wrong usage");
    }

    public void use(float width, float height, Camera camera, Matrix4f viewMatrix) {
        bufferData.viewMatrix = viewMatrix;
        bufferData.worldProjection = Scene.getProjection(width, height, camera);
        bufferData.cameraPosition = camera.getRelativePosition();
        bufferData.cameraDirection = camera.getUpVector();
        bufferData.time = (float)GLFW.glfwGetTime();

        glBindBuffer(GL_UNIFORM_BUFFER, _uboHandle);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, bufferData.serializeData());
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }

    public void use(float width, float height, Camera camera) {
        use(width, height, camera, camera.getViewMatrix());
    }
}
