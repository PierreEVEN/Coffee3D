package coffee3D.core.resources.factories;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.resources.types.MaterialResource;
import coffee3D.core.resources.types.TextureResource;

import java.io.*;

/**
 * Handle material shaders creation
 */
public class MaterialFactory {

    /**
     * Create material resource that contains vertex and fragment data from source file path. Also link given textures to theses resources.
     * @param vertexFile
     * @param fragmentFile
     * @return generated material resource
     */
    public static MaterialResource FromFiles(String resourceName, String vertexFile, String fragmentFile) {
        // Load shader data
        try {
            String vertexData = buildShaderContent(vertexFile);
            String fragmentData = buildShaderContent(fragmentFile);
            return FromData(resourceName, vertexData, fragmentData);
        }
        catch (Exception e) {
            Log.Warning("failed to load shader file : " + e.getMessage());
        }
        return null;
    }

    /**
     * Create material resource that contains vertex and fragment data from shader string data.
     * @param vertexData
     * @param fragmentData
     * @return generated material resource
     */
    public static MaterialResource FromData(String resourceName, String vertexData, String fragmentData) {
        MaterialResource mat = new MaterialResource(resourceName, vertexData, fragmentData);
        mat.load();
        if (mat.getErrors() == null) {
            return mat;
        }
        else {
            return null;
        }
    }


    public static String buildShaderContent(String filePath) {
        try {
            BufferedReader inputStream = new BufferedReader(new FileReader(filePath));
            String content = "";
            String line = inputStream.readLine();
            while (line != null) {

                if (line.startsWith("#include")) {
                    String includePath = line.substring(line.indexOf('"') + 1, line.indexOf(';') - 1);
                    File newContent = new File(new File(filePath).getParent() + "/" + includePath);
                    if (newContent.exists()) content += buildShaderContent(newContent.getPath());
                    else Log.Error("Failed to import shader file in " + filePath + " : " + newContent.getPath());
                }
                else {
                    content += line + "\n";
                }
                line = inputStream.readLine();
            }
            inputStream.close();
            return content;
        }
        catch (Exception e) {
            Log.Error("failed to read shader file " + filePath + " : " + e.getMessage());
        }
        return "";

    }

}
