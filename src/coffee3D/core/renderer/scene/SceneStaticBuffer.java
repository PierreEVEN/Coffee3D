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

    public void use(RenderScene scene, float width, float height, Camera camera, Matrix4f viewMatrix) {
        bufferData.viewMatrix.set(viewMatrix);
        bufferData.worldProjection.set(Scene.getProjection(width, height, camera));
        bufferData.cameraPosition.set(camera.getRelativePosition());
        bufferData.cameraDirection.set(camera.getUpVector());
        bufferData.time = (float)GLFW.glfwGetTime();
        bufferData.sunVector.set(((RenderSceneProperties)scene._sceneProperties).getSunVector());
        bufferData.lightSpaceMatrix = scene.getLightSpaceMatrix();
        bufferData.shadowIntensity = ((RenderSceneProperties)scene._sceneProperties).shadowIntensity * (scene.getShadowBuffer() == null ? 0 : 1);

        glBindBuffer(GL_UNIFORM_BUFFER, _uboHandle);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, bufferData.serializeData());
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }

    public void use(RenderScene scene, float width, float height, Camera camera) {
        use(scene, width, height, camera, camera.getViewMatrix());
    }
}
