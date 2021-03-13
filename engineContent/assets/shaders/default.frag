#version 330

#include "materialFunctions/basicFragmentShader.glsl";
#include "materialFunctions/lighting.glsl";
#include "materialFunctions/noises.glsl";

uniform sampler2D texture0;
uniform sampler2D shadowMap;

void main() {
    vec3 baseColor = (texture(texture0, texCoord) * color).xyz;
    outputColor = vec4(calcLight(baseColor, pos.xyz, worldNormal.xyz, shadowMap, fragPosLightSpace), 1);
}