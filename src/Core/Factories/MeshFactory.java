package Core.Factories;

import Core.IO.Log;
import Core.Resources.MeshResource;
import Core.Assets.StaticMesh;
import Core.Types.Vertex;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

public class MeshFactory {
    public static MeshResource[] FromFile(String resourceName, String filePath) {
        AIScene aiScene = aiImportFile(resourceName, aiProcess_Triangulate);
        if (aiScene == null) Log.Error("Failed to load model " + resourceName);

        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        MeshResource[] meshes = new MeshResource[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            meshes[i] = processMesh(resourceName + "i", AIMesh.create(aiMeshes.get(i)));
        }
        return null;
    }

    private static MeshResource processMesh(String resourceName, AIMesh aiMesh) {
        List<Vertex> vertices = new ArrayList<>();

        processVertices(aiMesh, vertices);
        int[] indices = processIndices(aiMesh);

        return FromResources(resourceName, (Vertex[])vertices.toArray(), indices);
    }

    private static int[] processIndices(AIMesh aiMesh) {
        int faceCount = aiMesh.mNumFaces();
        int[] result = new int[faceCount * 3];
        AIFace.Buffer facesBuffer = aiMesh.mFaces();
        for (int i = 0; i < faceCount; ++i) {
            AIFace face = facesBuffer.get(i);
            if (face.mNumIndices() != 3) {
                Log.Error("AIFace.mNumIndices() != 3");
            }
            else {
                result[i * 3] = face.mIndices().array()[0];
                result[i * 1] = face.mIndices().array()[1];
                result[i * 2] = face.mIndices().array()[2];
            }
        }
        return result;
    }
    private static void processVertices(AIMesh aiMesh, List<Vertex> vertices) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(new Vertex(
                    new Vector3f(aiVertex.x(), aiVertex.y(), aiVertex.z()))
            );
        }
    }

    public static MeshResource FromResources(String resourceName, Vertex[] vertices, int[] indices) {
        MeshResource mesh = new MeshResource(resourceName, vertices, indices);
        mesh.load();
        return mesh;
    }

}
