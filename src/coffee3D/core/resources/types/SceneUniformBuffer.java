package coffee3D.core.resources.types;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.scene.Components.Camera;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.scene.RenderSceneProperties;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.resources.GraphicResource;
import coffee3D.core.types.SceneBufferData;
import coffee3D.core.types.TypeHelper;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL46.*;

public class SceneUniformBuffer extends GraphicResource {

    private static int _uboHandle = -1;
    private final SceneBufferData _bufferData;
    private final float[] _rawBufferData;

    public SceneUniformBuffer(String resourceName) {
        super(resourceName);
        _bufferData = new SceneBufferData();
        _rawBufferData = new float[TypeHelper.GetStructFloatSize(SceneBufferData.class)];
    }

    @Override
    public void load() {
        if (_uboHandle == -1) {
            _uboHandle = glGenBuffers();
            int structByteSize = TypeHelper.GetStructByteSize(_bufferData.getClass());
            RenderUtils.BindUniformBuffer(_uboHandle);
            glBufferData(GL_UNIFORM_BUFFER, structByteSize, GL_STATIC_DRAW);
            glBindBufferRange(GL_UNIFORM_BUFFER, 0, _uboHandle, 0, structByteSize);
            RenderUtils.BindUniformBuffer(_uboHandle);
        }
    }

    @Override
    public void unload() {}

    @Override
    public void use(Scene context) {
        Log.Fail("wrong usage");
    }

    public void use(RenderScene scene, float width, float height, Camera camera) {
        use(scene, width, height, camera, camera.getViewMatrix());
    }

    public void use(RenderScene scene, float width, float height, Camera camera, Matrix4f viewMatrix) {
        updateData(scene, width, height, camera, viewMatrix);
        RenderUtils.BindUniformBuffer(_uboHandle);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, TypeHelper.SerializeStructure(_bufferData, _rawBufferData));
    }

    private void updateData(RenderScene scene, float width, float height, Camera camera, Matrix4f viewMatrix) {
        _bufferData.viewMatrix.set(viewMatrix);
        _bufferData.worldProjection.set(Scene.getProjection(width, height, camera));
        _bufferData.cameraPosition.set(camera.getRelativePosition(), 0);
        _bufferData.cameraDirection.set(camera.getUpVector(), 0);
        _bufferData.sunVector.set(((RenderSceneProperties)scene.getProperties()).getSunVector(), 0);
        _bufferData.lightSpaceMatrix.set(scene.getShadowMatrix());
        _bufferData.time = (float)GLFW.glfwGetTime();
        _bufferData.shadowIntensity = ((RenderSceneProperties)scene.getProperties()).shadowIntensity * (scene.getSettings().enableShadows() ? 1 : 0);
        _bufferData.framebufferSize.set(scene.getFbWidth(), scene.getFbHeight());
    }
}
