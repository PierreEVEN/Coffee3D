package Core.Factories;

import Core.IO.LogOutput.Log;
import Core.Resources.MaterialResource;
import Core.Resources.TextureResource;

import java.io.File;
import java.io.FileInputStream;

/**
 * Handle material shaders creation
 */
public class MaterialFactory {

    /**
     * Create material resource that contains vertex and fragment data from source file path.
     * @param resourceName
     * @param vertexFile
     * @param fragmentFile
     * @return generated material resource
     */
    public static MaterialResource FromFiles(String resourceName, String vertexFile, String fragmentFile) { return FromFiles(resourceName, vertexFile, fragmentFile, null); }

    /**
     * Create material resource that contains vertex and fragment data from source file path. Also link given textures to theses resources.
     * @param vertexFile
     * @param fragmentFile
     * @param textures
     * @return generated material resource
     */
    public static MaterialResource FromFiles(String resourceName, String vertexFile, String fragmentFile, TextureResource[] textures) {
        // Load shader data
        try {
            File file = new File(vertexFile);
            FileInputStream inputStream = new FileInputStream(file);
            String vertexData = new String(inputStream.readAllBytes());
            inputStream.close();

            file = new File(fragmentFile);
            inputStream = new FileInputStream(file);
            String fragmentData = new String(inputStream.readAllBytes());
            inputStream.close();
            return FromData(resourceName, vertexData, fragmentData, textures);
        }
        catch (Exception e) {
            Log.Warning("failed to load shader file : " + e.getMessage());
        }
        return null;
    }

    /**
     * Create material resource that contains vertex and fragment data from shader string data. Also link given textures to theses resources.
     * @param vertexData
     * @param fragmentData
     * @return generated material resource
     */
    public static MaterialResource FromData(String resourceName, String vertexData, String fragmentData) { return FromData(resourceName, vertexData, fragmentData, null); }

    /**
     * Create material resource that contains vertex and fragment data from shader string data.
     * @param vertexData
     * @param fragmentData
     * @return generated material resource
     */
    public static MaterialResource FromData(String resourceName, String vertexData, String fragmentData, TextureResource[] textures) {
        MaterialResource mat = new MaterialResource(resourceName, vertexData, fragmentData, textures);
        mat.load();
        if (!mat.hasErrors()) {
            return mat;
        }
        else {
            return null;
        }
    }
}
