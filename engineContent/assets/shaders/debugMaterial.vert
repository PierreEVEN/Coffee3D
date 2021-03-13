#version 330 core

#include "materialFunctions/basicVertexShader.glsl";

void main()
{
    gl_Position = projMatrix * viewMatrix * model * vec4(aPos, 1.0f);
}