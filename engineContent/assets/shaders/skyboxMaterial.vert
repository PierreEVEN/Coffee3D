#version 330 core

#include "materialFunctions/basicVertexShader.glsl";

void main()
{
    gl_Position = projMatrix * viewMatrix * model * vec4(aPos, 1.0f);
    texCoord = vec2(aTexCoord.x, aTexCoord.y);
    pos = (model * vec4(aPos, 1.0f)).xyz;
    normal = aNormal;
    worldNormal = normalize(mat3(transpose(inverse(model))) * aNormal);
}