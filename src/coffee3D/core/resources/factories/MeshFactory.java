package coffee3D.core.resources.factories;

import coffee3D.core.io.log.Log;
import coffee3D.core.resources.types.MeshResource;
import coffee3D.core.types.TypeHelper;
import coffee3D.core.types.Vertex;
import org.joml.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.lang.Math;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.assimp.Assimp.*;

public class MeshFactory {

    private static Vector4f vec4Pos = new Vector4f();
    private static int nodeIndex = 0;
    private static Matrix4f fromAiMatrix(AIMatrix4x4 aiMatrix4x4) {
        Matrix4f result = TypeHelper.getMat4();
        result.m00(aiMatrix4x4.a1());
        result.m10(aiMatrix4x4.a2());
        result.m20(aiMatrix4x4.a3());
        result.m30(aiMatrix4x4.a4());
        result.m01(aiMatrix4x4.b1());
        result.m11(aiMatrix4x4.b2());
        result.m21(aiMatrix4x4.b3());
        result.m31(aiMatrix4x4.b4());
        result.m02(aiMatrix4x4.c1());
        result.m12(aiMatrix4x4.c2());
        result.m22(aiMatrix4x4.c3());
        result.m32(aiMatrix4x4.c4());
        result.m03(aiMatrix4x4.d1());
        result.m13(aiMatrix4x4.d2());
        result.m23(aiMatrix4x4.d3());
        result.m33(aiMatrix4x4.d4());
        return result;
    }
    
    private static final Matrix4f zMatrixY = new Matrix4f().identity().rotate(new Quaternionf().identity().rotateXYZ((float) Math.toRadians(90), 0, 0));
    private static final Matrix4f zMatrixMY = new Matrix4f().identity().rotate(new Quaternionf().identity().rotateXYZ((float) Math.toRadians(-90), 0, 0));

    public static MeshResource[] FromFile(String resourceName, File filePath, ImportZAxis zAxis) {
        nodeIndex = 0;
        if (filePath == null) {
            Log.Error("failed to load " + resourceName + " :  invalid file path");
            return null;
        }
        AIScene aiScene = aiImportFile(filePath.getPath(), aiProcess_Triangulate);
        if (aiScene == null) {
            Log.Error("Failed to load model " + resourceName + " : " + filePath);
            return null;
        }

        AINode node = aiScene.mRootNode();

        Matrix4f transform = fromAiMatrix(node.mTransformation());

        switch (zAxis) {
            case ZUp -> {}
            case YUp -> transform.mul(zMatrixY);
            case YDown -> transform.mul(zMatrixMY);
        }

        ArrayList<MeshResource> meshes = processNode(resourceName, node, transform, aiScene.mMeshes());
        MeshResource[] tabMesh = new MeshResource[meshes.size()];
        meshes.toArray(tabMesh);
        return tabMesh;
    }


    private static ArrayList<MeshResource> processNode(String resourceName, AINode node, Matrix4f nodeMatrix, PointerBuffer meshBuffer) {
        ArrayList<MeshResource> resources = new ArrayList<>();

        IntBuffer meshes = node.mMeshes();
        if (meshes != null) {
            for (int i = 0; i < meshes.capacity(); ++i) {
                AIMesh mesh = AIMesh.create(meshBuffer.get(meshes.get(i)));
                resources.add(processMesh(resourceName, mesh, nodeMatrix));
            }
        }

        int childrenCount = node.mNumChildren();
        PointerBuffer children = node.mChildren();
        for (int i = 0; i < childrenCount; ++i) {
            AINode childNode = AINode.create(children.get(i));
            Matrix4f worldTransform = fromAiMatrix(childNode.mTransformation()).mul(nodeMatrix);
            resources.addAll(processNode(resourceName + "_" + childNode.mName().dataString(), childNode, worldTransform, meshBuffer));
        }

        return resources;
    }


    private static MeshResource processMesh(String resourceName, AIMesh aiMesh, Matrix4f transform) {
        Vertex[] vertices = processVertices(aiMesh, transform);
        int[] indices = processIndices(aiMesh);

        for (int i = 0; i < indices.length; ++i) {



        }


        return FromResources(resourceName + "_" + nodeIndex++, vertices, indices);
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

    private static Vertex[] processVertices(AIMesh aiMesh, Matrix4f transformation) {

        Vertex[] vertices = new Vertex[aiMesh.mNumVertices()];

        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        AIVector3D.Buffer texCoords = aiMesh.mTextureCoords(0);
        AIVector3D.Buffer normals = aiMesh.mNormals();
        AIColor4D.Buffer colorBfr = aiMesh.mColors(0);
        int numTextCoords = texCoords != null ? texCoords.remaining() : 0;

        int cnt = 0;
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            AIVector3D texCoord = texCoords.get();
            AIVector3D normal = normals.get();
            transformation.transform(vec4Pos.set(aiVertex.x(), aiVertex.y(), aiVertex.z(), 0));
            Vector3f pos = new Vector3f(vec4Pos.x, vec4Pos.y, vec4Pos.z);
            transformation.transform(vec4Pos.set(normal.x(), normal.y(), normal.z(), 0));
            Vector3f outNormal = new Vector3f(vec4Pos.x(), vec4Pos.y(), vec4Pos.z());

            Vector4f outColor = null;
            if (colorBfr != null) {
                AIColor4D result = colorBfr.get();
                outColor = new Vector4f(result.r(), result.g(), result.b(), result.a());
            }
            else {
                outColor = new Vector4f(1, 1, 1, 1);
            }

            vertices[cnt] = new Vertex(
                    pos,
                    numTextCoords == vertices.length ? new Vector2f(texCoord.x(), texCoord.y()) : new Vector2f(0, 0),
                    outNormal,
                    outColor
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
