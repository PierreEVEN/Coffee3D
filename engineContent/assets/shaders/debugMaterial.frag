#version 330

#include "materialFunctions/uniformDeclaration.glsl";
out vec4 outputColor;
uniform vec4 color;

void main()
{
    outputColor = color;
}