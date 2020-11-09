#version 330 core

#include "materialFunctions/uniformDeclaration.glsl";

out vec4 color;

in vec2 texCoord;

uniform sampler2D texture0;
uniform sampler2D shadowMap;

void main() {
    color = texture(texture0, texCoord);
}