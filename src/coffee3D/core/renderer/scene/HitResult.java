package coffee3D.core.renderer.scene;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.RenderMode;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.resources.types.Framebuffer;
import coffee3D.core.types.TypeHelper;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

public class HitResult {
    public final Vector3f position = new Vector3f();
    public float distance = 0;
    public SceneComponent component = null;
    public final DrawList drawList = new DrawList();
    public final Matrix4f viewMatrix = new Matrix4f();

    private final ByteBuffer _hitColor = BufferUtils.createByteBuffer(3);
    private final FloatBuffer _hitDepth = BufferUtils.createFloatBuffer(1);

    protected void update(RenderScene scene, Framebuffer pickBuffer) {

        Vector3f cursorSceneDirection = TypeHelper.getVector3();
        scene.getCursorSceneDirection(cursorSceneDirection);

        Vector3f camWorldPosition = scene.getCamera().getWorldPosition();
        viewMatrix.identity().lookAt(camWorldPosition, TypeHelper.getVector3(camWorldPosition).add(cursorSceneDirection), scene.getCamera().getUpVector());

        scene.getSceneUbo().use(
                scene,
                pickBuffer.getWidth(),
                pickBuffer.getHeight(),
                scene.getCamera(),
                viewMatrix);



        drawList.build(scene.getComponents(), viewMatrix, Scene.getProjection(pickBuffer.getWidth(), pickBuffer.getHeight(), scene.getCamera()));


        drawList.preRender(scene);
        glClear(GL_DEPTH_BUFFER_BIT);
        drawList.render(scene);
        glReadPixels(0, 0, 1, 1,  GL_DEPTH_COMPONENT, GL_FLOAT, _hitDepth);
        glClear(GL_DEPTH_BUFFER_BIT);
        drawList.postRender(scene);

        glReadPixels(0, 0, 1, 1,  GL_RGB, GL_UNSIGNED_BYTE, _hitColor);

        int hitCompId = (_hitColor.get(0) & 0xff) + ((_hitColor.get(1) & 0xff) << 8) + ((_hitColor.get(2) & 0xff) << 16);
        if (hitCompId > 0 && scene.getComponents().size() >= hitCompId) component = scene.getComponents().get(hitCompId - 1);
        else component = null;
        float zNear = scene.getCamera().getNearClipPlane();
        float zFar = scene.getCamera().getFarClipPlane();
        float z_n = 2 * _hitDepth.get(0) - 1;
        distance = 2 * zNear * zFar / (zFar + zNear - z_n * (zFar - zNear));

        position.set(cursorSceneDirection).mul(distance).add(camWorldPosition);

        RenderUtils.RENDER_MODE = RenderMode.Color;
    }



}
