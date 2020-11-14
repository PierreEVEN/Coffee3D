#version 330 core

#include "../../../engineContent/assets/shaders/materialFunctions/basicVertexShader.glsl";

void main()
{
    vec4 worldPos = model * vec4(aPos, 1.0f);
    gl_Position = projMatrix * viewMatrix * worldPos;
    texCoord = vec2(aPos.x, aPos.y);
    pos = (model * vec4(aPos, 1.0f)).xyz;
    normal = aNormal;
    worldNormal = normalize(mat3(transpose(inverse(model))) * aNormal);
    vertColor = aVertexColor;
    fragPosLightSpace = lightSpaceMatrix * vec4(pos, 1.0);
}