#version 330 core

#include "materialFunctions/uniformDeclaration.glsl";

out vec4 color;
in vec2 texCoord;

uniform sampler2D image;
uniform sampler2D shadowMap;

void main() {
    color = texture(image, texCoord);
    if (color.w <= 0.5) discard;
}