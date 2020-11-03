#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;
layout (location = 2) in vec3 aNormal;
layout (location = 3) in vec4 aVertexColor;

uniform mat4 model;

#include "materialFunctions/uniformDeclaration.glsl";

void main()
{
    float distance = length(cameraPos.xyz - aPos);
    gl_Position = projMatrix * viewMatrix * (model * vec4(aPos, 1.0f) + vec4(aNormal * (distance * 0.002), 0));
}