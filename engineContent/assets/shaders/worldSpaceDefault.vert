#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;
layout (location = 2) in vec3 aNormal;
layout (location = 3) in vec4 aVertexColor;

out vec2 texCoord;
out vec3 pos;
out vec3 normal;
out vec3 worldNormal;

uniform mat4 model;

layout (std140) uniform shader_data
{
    mat4 viewMatrix;
    mat4 projMatrix;
    vec3 cameraPos;
    vec3 cameraDir;
    float time;
};

void main()
{
    vec4 worldPos = model * vec4(aPos, 1.0f);
    gl_Position = projMatrix * viewMatrix * worldPos;
    texCoord = vec2(aPos.x, aPos.y);
    pos = (model * vec4(aPos, 1.0f)).xyz;
    normal = aNormal;
    worldNormal = normalize(mat3(transpose(inverse(model))) * aNormal);
}