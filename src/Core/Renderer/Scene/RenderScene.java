package Core.Renderer.Scene;

import Core.Types.Color;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

class RenderSceneProperties extends SceneProperty {
    private static final long serialVersionUID = -7591302437531604737L;


    protected Color _backgroundColor = new Color(0,0,0,1);
}


public class RenderScene extends Scene {

    private final Framebuffer _sceneBuffer;

    public RenderScene(int bfrSizeX, int bfrSizeY) {
        super();
        _sceneBuffer = new Framebuffer(bfrSizeX, bfrSizeY);
        _sceneProperties = new RenderSceneProperties();
    }

    @Override
    public void renderScene() {
        // Bind and reset scene frame buffer
        glBindFramebuffer(GL_FRAMEBUFFER, _sceneBuffer.getBufferId());
        Vector4f bgColor = ((RenderSceneProperties)_sceneProperties)._backgroundColor.getVector();
        float power = ((RenderSceneProperties)_sceneProperties)._backgroundColor.getPower();
        glClearColor(bgColor.x * power, bgColor.y * power, bgColor.z * power, bgColor.w * power);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        glFrontFace(GL_CW);
        glViewport(0, 0, getFramebuffer().getWidth(), getFramebuffer().getHeight());

        // render scene content
        super.renderScene();

        // Bind default buffer
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public Framebuffer getFramebuffer() { return _sceneBuffer; }

    @Override
    public Matrix4f getProjection() {
        return new Matrix4f().perspective(
                (float) Math.toRadians(getCamera().getFieldOfView()),
                _sceneBuffer.getWidth() / (float) _sceneBuffer.getHeight(),
                getCamera().getNearClipPlane(),
                getCamera().getFarClipPlane()
        );
    }
}
