package coffee3D.core.resources.types;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.resources.GraphicResource;
import coffee3D.core.resources.ResourceManager;
import coffee3D.core.types.Color;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;

import static org.lwjgl.BufferUtils.createFloatBuffer;
import static org.lwjgl.opengl.GL46.*;

/**
 * Basically a shader resource, with vertex and fragment shader
 */
public class MaterialResource extends GraphicResource {

    /**
     * material handle ID
     */
    private int _materialHandle;

    /**
     * vertex shader data
     */
    private final String _vertexData;

    /**
     * fragment shader data
     */
    private final String _fragmentData;

    private boolean _bSuccessfullyCompiled = true;

    public boolean hasErrors() { return !_bSuccessfullyCompiled; }

    /**
     * linked textures
     */
    private final TextureResource[] _textures;

    public MaterialResource(String resourceName, String vertexData, String fragmentData, TextureResource[] textures) {
        super(resourceName);
        _vertexData = vertexData;
        _fragmentData = fragmentData;
        _textures = textures;
    }

    @Override
    public void load() {
        int vertexShaderId, fragmentShaderId;

        vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(vertexShaderId, _vertexData);
        glShaderSource(fragmentShaderId, _fragmentData);

        glCompileShader(vertexShaderId);
        glCompileShader(fragmentShaderId);

        if (glGetShaderi(vertexShaderId, GL_COMPILE_STATUS) == 0) {
            Log.Warning("Error compiling vertex Shader code " + toString() + " : " + glGetShaderInfoLog(vertexShaderId, 1024));
            _bSuccessfullyCompiled = false;
        }
        if (glGetShaderi(fragmentShaderId, GL_COMPILE_STATUS) == 0) {
            Log.Warning("Error compiling fragment Shader code " + toString() + " : " + glGetShaderInfoLog(fragmentShaderId, 1024));
            _bSuccessfullyCompiled = false;
        }

        _materialHandle = glCreateProgram();

        glAttachShader(_materialHandle, vertexShaderId);
        glAttachShader(_materialHandle, fragmentShaderId);

        glLinkProgram(_materialHandle);

        glDetachShader(_materialHandle, vertexShaderId);
        glDetachShader(_materialHandle, fragmentShaderId);
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);

        int uniformBlockIndexRed = glGetUniformBlockIndex(_materialHandle, "shader_data");
        if (uniformBlockIndexRed < 0) {
            _bSuccessfullyCompiled = false;
            Log.Error("Error linking shader " + toString() + " : cannot find shader_data block index");
        }
        glUniformBlockBinding(_materialHandle, uniformBlockIndexRed, 0);

        if (hasErrors()) {
            ResourceManager.UnRegisterResource(this);
        }
        RenderUtils.CheckGLErrors();
    }

    public int getProgramHandle() { return _materialHandle; }

    @Override
    public void unload() { glDeleteProgram(_materialHandle); }

    private static MaterialResource _lastMaterial;
    @Override
    public void use(Scene context) {
        if (_lastMaterial != this) {
            _lastMaterial = this;
            glUseProgram(_materialHandle);
        }
    }

    /**
     * set material int parameter from parameter name
     * @param parameterName name
     * @param value         value
     */
    public void setIntParameter(String parameterName, int value) {
        int matHandle = glGetUniformLocation(_materialHandle, parameterName);
        if (matHandle < 0) return;
        glUniform1i(matHandle, value);
    }

    /**
     * set material float parameter from parameter name
     * @param parameterName name
     * @param value         value
     */
    public void setFloatParameter(String parameterName, float value) {
        int matHandle = glGetUniformLocation(_materialHandle, parameterName);
        if (matHandle < 0) return;
        glUniform1f(matHandle, value);
    }

    /**
     * set material int parameter from parameter name
     * @param parameterName name
     * @param value         value
     */
    public void setMatrixParameter(String parameterName, Matrix4f value) {
        int matHandle = glGetUniformLocation(_materialHandle, parameterName);
        if (matHandle < 0) return;
        final FloatBuffer bfr = createFloatBuffer(16);
        value.get(bfr);
        glUniformMatrix4fv(matHandle, false, bfr);
    }

    public void setColorParameter(String parameterName, Color color) {
        int matHandle = glGetUniformLocation(_materialHandle, parameterName);
        if (matHandle < 0) return;
        glUniform4f(matHandle,
                color.getVector().x * color.getPower(),
                color.getVector().y * color.getPower(),
                color.getVector().z * color.getPower(),
                color.getVector().w * color.getPower());
        RenderUtils.CheckGLErrors();
    }

    public void setVec4Parameter(String parameterName, Vector4f value) {
        int matHandle = glGetUniformLocation(_materialHandle, parameterName);
        if (matHandle < 0) return;
        glUniform4f(matHandle, value.x, value.y, value.z, value.w);
    }
}
