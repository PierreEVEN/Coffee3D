package Core.Renderer.Scene;

import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class RenderScene extends Scene {

    private final Framebuffer _sceneBuffer;
    private Vector4f backgroundColor;

    public RenderScene(int bfrSizeX, int bfrSizeY) {
        super();
        _sceneBuffer = new Framebuffer(bfrSizeX, bfrSizeY);
        backgroundColor = new Vector4f(0.5f, 0.7f, 0.9f, 1.f);
    }

    @Override
    public void renderScene() {
        // Bind and reset scene frame buffer
        glBindFramebuffer(GL_FRAMEBUFFER, _sceneBuffer.getBufferId());
        glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        glFrontFace(GL_CW);

        // render scene content
        super.renderScene();

        // Bind default buffer
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public Framebuffer getFramebuffer() { return _sceneBuffer; }
}
