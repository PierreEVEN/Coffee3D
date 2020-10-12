package Core.Factories;

import Core.IO.LogOutput.Log;
import Core.Resources.Texture2DResource;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;

/**
 * Handle texture resource creation
 */
public class TextureFactory {

    /**
     * Create texture2D from file
     * @param filePath
     * @return generated texture resource
     */
    public static Texture2DResource T2dFromFile(String resourceName, String filePath) {
        try {
            BufferedImage bfr = ImageIO.read(new File(filePath));
            int[] pixels = new int[bfr.getWidth() * bfr.getHeight()];
            bfr.getRGB(0, 0, bfr.getWidth(), bfr.getHeight(), pixels, 0, bfr.getWidth());

            int[] result = new int[bfr.getWidth() * bfr.getHeight()];

            for (int x = 0; x < bfr.getWidth(); ++x) {
                for (int y = 0; y < bfr.getHeight(); ++y) {
                    // Flip y axis
                    int pixelIndex = (bfr.getHeight() - y - 1) * bfr.getWidth() + x;
                    int newIndex = y * bfr.getWidth() + x;

                    // Flip argb to rgba
                    int A = (pixels[pixelIndex] >> 24) & 0xff;
                    int R = (pixels[pixelIndex] >> 16) & 0xff;
                    int G = (pixels[pixelIndex] >>  8) & 0xff;
                    int B = (pixels[pixelIndex]      ) & 0xff;
                    result[newIndex] =  (A & 0xff) << 24 | (B & 0xff) << 16 | (G & 0xff) << 8 | (R & 0xff);
                }
            }

            return T2dFromData(resourceName, result, bfr.getWidth(), bfr.getHeight());
        }
        catch(Exception e) {
            Log.Error("failed to open texture : " + e.getMessage());
        }
        return null;
    }

    /**
     * Create texture from data
     * @param data
     * @param width
     * @param height
     * @return generated texture resource
     */
    public static Texture2DResource T2dFromData(String resourceName, int[] data, int width, int height) {
        Texture2DResource resource = new Texture2DResource(resourceName, data, width, height);
        resource.load();
        return resource;
    }

}
