#version 330

#include "materialFunctions/basicFragmentShader.glsl";
#include "materialFunctions/lighting.glsl";

uniform sampler2D texture0;

void main() {
    outputColor = lightColor(texture(texture0, texCoord) * color, worldNormal, sunDirection.xyz, pos.xyz);
}