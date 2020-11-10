package coffee3D.core.renderer.scene;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.RenderMode;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.resources.types.Framebuffer;
import coffee3D.core.types.TypeHelper;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class HitResult {
    public final Vector3f position = new Vector3f();
    public float distance = 0;
    public SceneComponent component = null;

    private final ByteBuffer _hitColor = BufferUtils.createByteBuffer(3);
    private final FloatBuffer _hitDepth = BufferUtils.createFloatBuffer(1);

    protected void update(RenderScene scene, Framebuffer pickBuffer) {
        Vector3f cursorSceneDirection = TypeHelper.getVector3();
        scene.getCursorSceneDirection(cursorSceneDirection);

        Vector3f camWorldPosition = scene.getCamera().getWorldPosition();

        scene.getSceneUbo().use(
                scene,
                pickBuffer.getWidth(),
                pickBuffer.getHeight(),
                scene.getCamera(),
                TypeHelper.getMat4().identity().lookAt(camWorldPosition, TypeHelper.getVector3(camWorldPosition).add(cursorSceneDirection), scene.getCamera().getUpVector()));

        RenderUtils.CheckGLErrors();
        scene.drawFrustumComponents();
        RenderUtils.CheckGLErrors();

        glReadPixels(0, 0, 1, 1,  GL_RGB, GL_UNSIGNED_BYTE, _hitColor);
        glReadPixels(0, 0, 1, 1,  GL_DEPTH_COMPONENT, GL_FLOAT, _hitDepth);

        int hitCompId = (_hitColor.get(0) & 0xff) + ((_hitColor.get(1) & 0xff) << 8) + ((_hitColor.get(2) & 0xff) << 16);
        if (hitCompId > 0 && scene.getComponents().size() >= hitCompId) component = scene.getComponents().get(hitCompId - 1);
        else component = null;
        distance = _hitDepth.get(0) * scene.getCamera().getFarClipPlane() - scene.getCamera().getNearClipPlane();

        float z_near = scene.getCamera().getNearClipPlane();
        float z_far = scene.getCamera().getFarClipPlane();

        float z_b = _hitDepth.get(0);
        float z_n = 2 * z_b - 1;
        float z_e = 2 * z_near * z_far / (z_far + z_near - z_n * (z_far - z_near));

        distance = z_e;
        Log.Display("distance : " + z_e);
        position.set(cursorSceneDirection).mul(distance).add(camWorldPosition);

        RenderUtils.RENDER_MODE = RenderMode.Color;
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }



}
