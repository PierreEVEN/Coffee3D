#version 330

#include "materialFunctions/uniformDeclaration.glsl";

out vec4 outputColor;
uniform vec4 color;


void main()
{
    outputColor = vec4(1,1,0,0);
}