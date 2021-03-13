package coffee3D.core.resources.types;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.resources.GraphicResource;
import coffee3D.core.resources.ResourceManager;
import coffee3D.core.types.Color;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import static org.lwjgl.BufferUtils.createFloatBuffer;
import static org.lwjgl.BufferUtils.createIntBuffer;
import static org.lwjgl.opengl.GL46.*;

/**
 * Basically a shader resource, with vertex and fragment shader
 */
public class MaterialResource extends GraphicResource {

    private static MaterialResource _lastMaterialResource;
    private final int _materialHandle;
    private String _compilationMessage = null;
    private String _vertexData;
    private String _fragmentData;
    private final HashMap<String, Integer> _uniforms = new HashMap<>();

    private static final IntBuffer _bufferSize = createIntBuffer(1); // size of the variable
    private static final IntBuffer _bufferType = createIntBuffer(1); // type of the variable (float, vec3 or mat4, etc)
    private static final IntBuffer _bufferCount = createIntBuffer(1);


    public MaterialResource(String resourceName, String vertexData, String fragmentData) {
        super(resourceName);
        _vertexData = vertexData;
        _fragmentData = fragmentData;
        _materialHandle = glCreateProgram();
    }

    @Override
    public void load() {
        _compilationMessage = null;

        int vertexShaderId, fragmentShaderId;
        vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);

        // LOAD AND COMPILE SOURCES
        glShaderSource(vertexShaderId, _vertexData);
        glShaderSource(fragmentShaderId, _fragmentData);
        _vertexData = null;
        _fragmentData = null;
        glCompileShader(vertexShaderId);
        glCompileShader(fragmentShaderId);
        if (glGetShaderi(vertexShaderId, GL_COMPILE_STATUS) == 0) {
            String error = "Error compiling vertex Shader code " + toString() + " : " + glGetShaderInfoLog(vertexShaderId);
            Log.Warning(error);
            _compilationMessage += error + '\n';
        }
        if (glGetShaderi(fragmentShaderId, GL_COMPILE_STATUS) == 0) {
            String error = "Error compiling fragment Shader code " + toString() + " : " + glGetShaderInfoLog(fragmentShaderId);
            Log.Warning(error);
            _compilationMessage += error + '\n';
        }

        // LINK AND CLEANUP COMPILED SOURCES
        glAttachShader(_materialHandle, vertexShaderId);
        glAttachShader(_materialHandle, fragmentShaderId);
        glLinkProgram(_materialHandle);
        glDetachShader(_materialHandle, vertexShaderId);
        glDetachShader(_materialHandle, fragmentShaderId);
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);

        // CHECK ERRORS
        int uniformBlockIndexRed = glGetUniformBlockIndex(_materialHandle, "shader_data");
        if (uniformBlockIndexRed < 0) {
            String error = "Error linking shader " + toString() + " : cannot find shader_data block index";
            Log.Error(error);
            _compilationMessage += error + '\n';
        }
        glUniformBlockBinding(_materialHandle, uniformBlockIndexRed, 0);

        if (getErrors() != null) ResourceManager.UnRegisterResource(this);
        RenderUtils.CheckGLErrors();

        glGetProgramiv(_materialHandle, GL_ACTIVE_UNIFORMS, _bufferCount);
        for (int i = 0; i < _bufferCount.get(0); i++)
        {
            String name = glGetActiveUniform(_materialHandle, i, 64, _bufferSize, _bufferType);
            _uniforms.put(name, glGetUniformLocation(_materialHandle, name));
        }
    }

    @Override
    public void unload() {
        glDeleteProgram(_materialHandle);
    }

    @Override
    public void use(Scene context) {
        if (_lastMaterialResource != this) {
            _lastMaterialResource = this;
            glUseProgram(_materialHandle);
        }
    }

    public int getProgramHandle() { return _materialHandle; }

    public String getErrors() { return _compilationMessage; }

    public void setIntParameter(String parameterName, int value) {
        use(null);
        Integer location = _uniforms.get(parameterName);
        if (location == null) return;
        glUniform1i(location, value);
    }

    public void setFloatParameter(String parameterName, float value) {
        use(null);
        Integer location = _uniforms.get(parameterName);
        if (location == null) return;
        glUniform1f(location, value);
    }

    public void setModelMatrix(Matrix4f value) {
        use(null);
        Integer location = _uniforms.get("model");
        if (location == null) return;
        final FloatBuffer bfr = createFloatBuffer(16);
        value.get(bfr);
        glUniformMatrix4fv(location, false, bfr);
    }
    private static final FloatBuffer _matrixBuffer = createFloatBuffer(16);
    public void setMatrixParameter(String parameterName, Matrix4f value) {
        use(null);
        Integer location = _uniforms.get(parameterName);
        if (location == null) return;
        value.get(_matrixBuffer);
        glUniformMatrix4fv(location, false, _matrixBuffer);
    }

    public void setColorParameter(String parameterName, Color color) {
        use(null);
        Integer location = _uniforms.get(parameterName);
        if (location == null) return;
        glUniform4f(location,
                color.getVector().x * color.getPower(),
                color.getVector().y * color.getPower(),
                color.getVector().z * color.getPower(),
                color.getVector().w * color.getPower());
    }

    public void setVec4Parameter(String parameterName, Vector4f value) {
        use(null);
        Integer location = _uniforms.get(parameterName);
        if (location == null) return;
        glUniform4f(location, value.x, value.y, value.z, value.w);
    }
    public void setVec4Parameter(String parameterName, Vector3f value, float w) {
        use(null);
        Integer location = _uniforms.get(parameterName);
        if (location == null) return;
        glUniform4f(location, value.x, value.y, value.z, w);
    }
}
