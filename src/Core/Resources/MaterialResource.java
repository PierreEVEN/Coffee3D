package Core.Resources;

import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.Scene;
import Core.Types.Color;
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
            Log.Warning("Error compiling vertex Shader code: " + glGetShaderInfoLog(vertexShaderId, 1024));
            _bSuccessfullyCompiled = false;
        }
        if (glGetShaderi(fragmentShaderId, GL_COMPILE_STATUS) == 0) {
            Log.Warning("Error compiling fragment Shader code: " + glGetShaderInfoLog(fragmentShaderId, 1024));
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
        glUniformBlockBinding(_materialHandle, uniformBlockIndexRed, 0);

        if (hasErrors()) {
            ResourceManager.UnRegisterResource(this);
        }
    }

    public int getProgramHandle() { return _materialHandle; }

    @Override
    public void unload() { glDeleteProgram(_materialHandle); }

    @Override
    public void use(Scene context) {
        // Bind shader program
        glUseProgram(_materialHandle);

        // Bind textures
        if (_textures != null) {
            for (int i = 0; i < _textures.length; ++i) {
                //Handle Texture2DResources
                if (_textures[i] instanceof Texture2DResource) {
                    setIntParameter("texture" + i, i);
                    glActiveTexture(GL_TEXTURE0 + i);
                    glBindTexture(GL_TEXTURE_2D, _textures[i].getTextureHandle());
                    Log.Display("draw text " + i);
                }
            }
        }

    }

    /**
     * set material int parameter from parameter name
     * @param parameterName name
     * @param value         value
     */
    public void setIntParameter(String parameterName, int value) {
        glUniform1i(glGetUniformLocation(_materialHandle, parameterName), value);
    }

    /**
     * set material float parameter from parameter name
     * @param parameterName name
     * @param value         value
     */
    public void setFloatParameter(String parameterName, float value) {
        glUniform1f(glGetUniformLocation(_materialHandle, parameterName), value);
    }

    /**
     * set material int parameter from parameter name
     * @param parameterName name
     * @param value         value
     */
    public void setMatrixParameter(String parameterName, Matrix4f value) {
        final FloatBuffer bfr = createFloatBuffer(16);
        value.get(bfr);
        glUniformMatrix4fv(glGetUniformLocation(_materialHandle, parameterName), false, bfr);
    }

    public void setColorParameter(String parameterName, Color color) {
        glUniform4f(glGetUniformLocation(_materialHandle, parameterName),
                color.getVector().x * color.getPower(),
                color.getVector().y * color.getPower(),
                color.getVector().z * color.getPower(),
                color.getVector().w * color.getPower());
    }

    public void setVec4Parameter(String parameterName, Vector4f value) {
        glUniform4f(glGetUniformLocation(_materialHandle, parameterName), value.x, value.y, value.z, value.w);
    }
}
