#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;
layout (location = 2) in vec3 aNormal;
layout (location = 3) in vec4 aVertexColor;

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
    gl_Position = projMatrix * viewMatrix * model * vec4(aPos, 1.0f);
}