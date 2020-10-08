package Core.Resources;

import Core.IO.Log;
import Core.Renderer.Scene.Scene;
import org.joml.Matrix4f;

import java.nio.FloatBuffer;

import static org.lwjgl.BufferUtils.createFloatBuffer;
import static org.lwjgl.opengl.GL46.*;

public class MaterialResource extends GraphicResource {

    /**
     * material handle ID
     */
    private int _materialHandle;

    /**
     * vertex shader data
     */
    private String _vertexData;

    /**
     * fragment shader data
     */
    private String _fragmentData;

    /**
     * linked textures
     */
    private TextureResource[] _textures;

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
            Log.Fail("Error compiling vertex Shader code: " + glGetShaderInfoLog(vertexShaderId, 1024));
        }
        if (glGetShaderi(fragmentShaderId, GL_COMPILE_STATUS) == 0) {
            Log.Fail("Error compiling fragment Shader code: " + glGetShaderInfoLog(fragmentShaderId, 1024));
        }

        _materialHandle = glCreateProgram();

        glAttachShader(_materialHandle, vertexShaderId);
        glAttachShader(_materialHandle, fragmentShaderId);

        glLinkProgram(_materialHandle);

        glDetachShader(_materialHandle, vertexShaderId);
        glDetachShader(_materialHandle, fragmentShaderId);
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);
    }

    @Override
    public void unload() {
        glDeleteProgram(_materialHandle);
    }

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
     * @param parameterName
     * @param value
     */
    public void setIntParameter(String parameterName, int value) {
        glUniform1i(glGetUniformLocation(_materialHandle, parameterName), value);
    }


    /**
     * set material int parameter from parameter name
     * @param parameterName
     * @param value
     */
    public void setMatrixParameter(String parameterName, Matrix4f value) {
        FloatBuffer bfr = createFloatBuffer(16);
        value.get(bfr);
        glUniformMatrix4fv(glGetUniformLocation(_materialHandle, parameterName), false, bfr);
    }
}
