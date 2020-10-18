package Core.Renderer.Scene;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class RenderScene extends Scene {

    private final Framebuffer _sceneBuffer;
    private Vector4f _backgroundColor;

    public RenderScene(int bfrSizeX, int bfrSizeY) {
        super();
        _sceneBuffer = new Framebuffer(bfrSizeX, bfrSizeY);
        _backgroundColor = new Vector4f().zero();
    }

    public void setBackgroundColor(Vector4f color) {
        _backgroundColor = color;
    }

    @Override
    public void renderScene() {
        // Bind and reset scene frame buffer
        glBindFramebuffer(GL_FRAMEBUFFER, _sceneBuffer.getBufferId());
        glClearColor(_backgroundColor.x, _backgroundColor.y, _backgroundColor.z, _backgroundColor.w);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
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
