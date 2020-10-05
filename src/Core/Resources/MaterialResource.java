package Core.Resources;

import Core.IO.Log;
import Core.Renderer.Scene.Scene;

import java.io.File;
import java.io.FileInputStream;

import static org.lwjgl.opengl.GL46.*;

public class MaterialResource extends GraphicResource {

    private int _materialHandle;
    private String _vertexPath;
    private String _fragmentPath;

    MaterialResource(String vertexPath, String fragmentPath) {
        _vertexPath = vertexPath;
        _fragmentPath = fragmentPath;
        Load();
    }

    @Override
    public void Load() {

        String vertexData = null;
        String fragmentData = null;

        int vertexShaderId, fragmentShaderId;

        // Load fragment data
        try {
            File file = new File(_vertexPath);
            FileInputStream inputStream = new FileInputStream(file);
            vertexData = new String(inputStream.readAllBytes());
            inputStream.close();
        }
        catch (Exception e) {
            Log.Fail(e.getMessage());
        }

        // Load vertex data
        try {
            File file = new File(_fragmentPath);
            FileInputStream inputStream = new FileInputStream(file);
            fragmentData = new String(inputStream.readAllBytes());
            inputStream.close();
        }
        catch (Exception e) {
            Log.Fail(e.getMessage());
        }

        vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(vertexShaderId, vertexData);
        glShaderSource(fragmentShaderId, vertexData);

        glCompileShader(vertexShaderId);
        glCompileShader(fragmentShaderId);

        if (glGetShaderi(vertexShaderId, GL_COMPILE_STATUS) == 0) {
            Log.Fail("Error compiling Shader code: " + glGetShaderInfoLog(vertexShaderId, 1024));
        }
        if (glGetShaderi(fragmentShaderId, GL_COMPILE_STATUS) == 0) {
            Log.Fail("Error compiling Shader code: " + glGetShaderInfoLog(fragmentShaderId, 1024));
        }

        _materialHandle = glCreateProgram();

        glAttachShader(_materialHandle, vertexShaderId);
        glAttachShader(_materialHandle, fragmentShaderId);

    }

    @Override
    public void Unload() {
        glDeleteProgram(_materialHandle);
    }

    @Override
    public void use(Scene context) {
        glUseProgram(_materialHandle);
    }
}
