#version 330 core

#include "materialFunctions/uniformDeclaration.glsl";

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;

out vec2 texCoord;

uniform mat4 model;

void main()
{
    gl_Position = projMatrix * viewMatrix * model * vec4(aPos, 1.0f);
    texCoord = vec2(aTexCoord.x, aTexCoord.y);
}