package Core.Factories;

import Core.IO.Log;
import Core.Resources.MeshResource;
import Core.Assets.StaticMesh;
import Core.Types.Vertex;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

public class MeshFactory {
    public static MeshResource[] FromFile(String resourceName, String filePath) {
        AIScene aiScene = aiImportFile(filePath, aiProcess_Triangulate);
        if (aiScene == null) Log.Error("Failed to load model " + resourceName + " : " + filePath);

        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        MeshResource[] meshes = new MeshResource[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            meshes[i] = processMesh(resourceName + i, AIMesh.create(aiMeshes.get(i)));
        }
        return meshes;
    }

    private static MeshResource processMesh(String resourceName, AIMesh aiMesh) {

        Vertex[] vertices = processVertices(aiMesh);
        int[] indices = processIndices(aiMesh);

        return FromResources(resourceName, vertices, indices);
    }

    private static int[] processIndices(AIMesh aiMesh) {
        int faceCount = aiMesh.mNumFaces();
        int[] result = new int[faceCount * 3];
        AIFace.Buffer facesBuffer = aiMesh.mFaces();
        for (int i = 0; i < faceCount; ++i) {
            AIFace face = facesBuffer.get(i);
            if (face.mNumIndices() != 3) {
                Log.Error("AIFace.mNumIndices() != 3 : " + face.mNumIndices());
            }
            else {
                result[i * 3] = face.mIndices().get(0);
                result[i * 3 + 1] = face.mIndices().get(1);
                result[i * 3 + 2] = face.mIndices().get(2);;
            }
        }
        return result;
    }
    private static Vertex[] processVertices(AIMesh aiMesh) {

        Vertex[] vertices = new Vertex[aiMesh.mNumVertices()];

        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        AIVector3D.Buffer texCoords = aiMesh.mTextureCoords(0);

        int numTextCoords = texCoords != null ? texCoords.remaining() : 0;

        int cnt = 0;
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            AIVector3D texCoord = texCoords.get();
            vertices[cnt] = new Vertex(
                    new Vector3f(aiVertex.x(), aiVertex.y(), aiVertex.z()),
                    numTextCoords == vertices.length ? new Vector2f(texCoord.x(), texCoord.y()) : new Vector2f()
            );
            cnt++;
        }
        return vertices;
    }

    public static MeshResource FromResources(String resourceName, Vertex[] vertices, int[] indices) {
        MeshResource mesh = new MeshResource(resourceName, vertices, indices);
        mesh.load();
        return mesh;
    }

}
