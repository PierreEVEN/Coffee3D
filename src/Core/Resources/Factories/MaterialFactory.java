package Core.Resources.Factories;

import Core.IO.Log;
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
     * @param vertexFile
     * @param fragmentFile
     * @return generated material resource
     */
    public static MaterialResource FromFiles(String vertexFile, String fragmentFile) { return FromFiles(vertexFile, fragmentFile, null); }

    /**
     * Create material resource that contains vertex and fragment data from source file path. Also link given textures to theses resources.
     * @param vertexFile
     * @param fragmentFile
     * @param textures
     * @return generated material resource
     */
    public static MaterialResource FromFiles(String vertexFile, String fragmentFile, TextureResource[] textures) {
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
            return FromData(vertexData, fragmentData, textures);
        }
        catch (Exception e) {
            Log.Fail("failed to load shader file : " + e.getMessage());
        }
        return null;
    }

    /**
     * Create material resource that contains vertex and fragment data from shader string data. Also link given textures to theses resources.
     * @param vertexData
     * @param fragmentData
     * @return generated material resource
     */
    public static MaterialResource FromData(String vertexData, String fragmentData) { return FromData(vertexData, fragmentData, null); }

    /**
     * Create material resource that contains vertex and fragment data from shader string data.
     * @param vertexData
     * @param fragmentData
     * @return generated material resource
     */
    public static MaterialResource FromData(String vertexData, String fragmentData, TextureResource[] textures) {
        MaterialResource mat = new MaterialResource(vertexData, fragmentData, textures);
        mat.load();
        return mat;
    }
}
