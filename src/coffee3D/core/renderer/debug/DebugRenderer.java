package coffee3D.core.renderer.debug;

import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.types.Color;
import coffee3D.core.types.TypeHelper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Type;

import static org.lwjgl.opengl.GL11.*;

public class DebugRenderer {

    public static void DrawDebugLine(Scene context, Vector3f p1, Vector3f p2, Color color) {
        if (RenderUtils.RENDER_MODE == GL_SELECT) return;
        RenderUtils.CheckGLErrors();
        RenderUtils.getDebugMaterial().setColor(color);
        RenderUtils.CheckGLErrors();
        RenderUtils.getDebugMaterial().use(context);

        RenderUtils.getDebugMaterial().getResource().setMatrixParameter("model", TypeHelper.getMat4().identity());
        glMatrixMode(GL_MODELVIEW);
        glBegin(GL_LINES);
        {
            glVertex3f(p1.x, p1.y, p1.z); glVertex3f(p2.x, p2.y, p2.z);
        }
        glEnd();
    }

    public static void DrawDebugBox(Scene context, Vector3f p1, Vector3f p2, Color color) {
        if (RenderUtils.RENDER_MODE == GL_SELECT) return;
        RenderUtils.getDebugMaterial().use(context);
        RenderUtils.getDebugMaterial().getResource().setMatrixParameter("model", TypeHelper.getMat4().identity());
        RenderUtils.getDebugMaterial().getResource().setColorParameter("color", color);
        glMatrixMode(GL_MODELVIEW);
        glBegin(GL_LINES);
        {
            glVertex3f(p1.x, p1.y, p1.z); glVertex3f(p1.x, p1.y, p2.z);
            glVertex3f(p1.x, p1.y, p2.z); glVertex3f(p1.x, p2.y, p2.z);
            glVertex3f(p1.x, p2.y, p2.z); glVertex3f(p1.x, p2.y, p1.z);
            glVertex3f(p1.x, p2.y, p1.z); glVertex3f(p1.x, p1.y, p1.z);

            glVertex3f(p2.x, p1.y, p1.z); glVertex3f(p2.x, p1.y, p2.z);
            glVertex3f(p2.x, p1.y, p2.z); glVertex3f(p2.x, p2.y, p2.z);
            glVertex3f(p2.x, p2.y, p2.z); glVertex3f(p2.x, p2.y, p1.z);
            glVertex3f(p2.x, p2.y, p1.z); glVertex3f(p2.x, p1.y, p1.z);

            glVertex3f(p1.x, p1.y, p1.z); glVertex3f(p2.x, p1.y, p1.z);
            glVertex3f(p1.x, p2.y, p1.z); glVertex3f(p2.x, p2.y, p1.z);
            glVertex3f(p1.x, p2.y, p2.z); glVertex3f(p2.x, p2.y, p2.z);
            glVertex3f(p1.x, p1.y, p2.z); glVertex3f(p2.x, p1.y, p2.z);
        }
        glEnd();
    }

    /*
    public static void DrawDebugCircle(Scene context, Vector3f center, Vector3f direction, float radius, int segments, Color color) {
        if (RenderUtils.RENDER_MODE == GL_SELECT) return;
        RenderUtils.getDebugMaterial().use(context);

        rotation.identity().fromAxisAngleDeg(direction, 90);
        renderMatrix.identity()
                .translate(center)
                .rotate(rotation);

        RenderUtils.getDebugMaterial().getShader().setMatrixParameter("model", renderMatrix);
        RenderUtils.getDebugMaterial().getShader().setColorParameter("color", color);
        glMatrixMode(GL_MODELVIEW);
        glBegin(GL_LINES);
        {
            for (int i = 0; i < segments; ++i) {
                float p1x = (float)Math.cos((i / (float)segments) * Math.PI * 2) * radius;
                float p1y = (float)Math.sin((i / (float)segments) * Math.PI * 2) * radius;
                float p2x = (float)Math.cos(((i + 1) / (float)segments) * Math.PI * 2) * radius;
                float p2y = (float)Math.sin(((i + 1) / (float)segments) * Math.PI * 2) * radius;
                glVertex3f(p1x, 0, p1y); glVertex3f(p2x, 0, p2y);
            }
        }
        glEnd();
    }
     */

    public static void DrawDebugCylinder(Scene context, Vector3f p1, Vector3f p2, float radius, int segments, Color color) {

        RenderUtils.getDebugMaterial().use(context);

        Vector3f zAxis = TypeHelper.getVector3(0,0,1);

        if (TypeHelper.getVector3(p2).sub(p1).normalize().equals(zAxis) || TypeHelper.getVector3(p1).sub(p2).normalize().equals(zAxis)) {
            zAxis.set(0,1,0);
        }

        Matrix4f transformation = TypeHelper.getMat4().identity().lookAt(p1, p2, zAxis);
        RenderUtils.getDebugMaterial().getResource().setMatrixParameter("model", transformation);
        RenderUtils.getDebugMaterial().getResource().setColorParameter("color", color);
        glMatrixMode(GL_MODELVIEW);
        glBegin(GL_QUADS);
        float length = p1.distance(p2);


        for (int i = 0; i < segments; ++i) {
            float y = (float) Math.cos(i / (float)segments * Math.PI * 2) * radius;
            float z = (float) Math.sin(i / (float)segments * Math.PI * 2) * radius;
            float y2 = (float) Math.cos((i + 1) / (float)segments * Math.PI * 2) * radius;
            float z2 = (float) Math.sin((i + 1) / (float)segments * Math.PI * 2) * radius;

            Vector3f forward = TypeHelper.getVector3(p2).sub(p1);
            Vector3f rightA = TypeHelper.getVector3(forward).cross(zAxis);
            Vector3f upA;

            Vector3f rightB;
            Vector3f upB;


            glVertex3f(length, y, z);
            glVertex3f(0, y, z);
            glVertex3f(0, y2, z2);
            glVertex3f(length, y2, z2);
        }
        glEnd();
    }

    public static void DrawDebugSphere(Scene context, Vector3f center, float radius, int segments, Color color) {
        if (RenderUtils.RENDER_MODE == GL_SELECT) return;
        RenderUtils.getDebugMaterial().use(context);
        RenderUtils.getDebugMaterial().getResource().setMatrixParameter("model", TypeHelper.getMat4().identity().translate(center));
        RenderUtils.getDebugMaterial().getResource().setColorParameter("color", color);
        glMatrixMode(GL_MODELVIEW);
        glBegin(GL_LINES);
        {
            for (int j = 0; j < segments; ++j) {
                for (int i = 0; i < segments; ++i) {
                    float rad = radius * ((float)Math.sin((j / ((float)segments)) * Math.PI));
                    float p1x = (float) Math.cos((i / (float) segments) * Math.PI * 2) * rad;
                    float p1y = (float) Math.sin((i / (float) segments) * Math.PI * 2) * rad;
                    float p2x = (float) Math.cos(((i + 1) / (float) segments) * Math.PI * 2) * rad;
                    float p2y = (float) Math.sin(((i + 1) / (float) segments) * Math.PI * 2) * rad;

                    float rad2 = radius * ((float)Math.sin(((j + 1) / ((float)segments)) * Math.PI));
                    float p3x = (float) Math.cos(((i) / (float) segments) * Math.PI * 2) * rad2;
                    float p3y = (float) Math.sin(((i) / (float) segments) * Math.PI * 2) * rad2;

                    float y = (float)Math.cos((j / ((float)segments)) * Math.PI) * radius;
                    float y2 = (float)Math.cos(((j + 1) / ((float)segments)) * Math.PI) * radius;

                    glVertex3f(p1x, y, p1y);
                    glVertex3f(p2x, y, p2y);

                    glVertex3f(p1x, y, p1y);
                    glVertex3f(p3x, y2, p3y);
                }
            }
        }
        glEnd();
    }
}
