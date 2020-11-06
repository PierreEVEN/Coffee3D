#version 330

#include "materialFunctions/basicFragmentShader.glsl";
#include "materialFunctions/lighting.glsl";
#include "materialFunctions/noises.glsl";

uniform sampler2D texture0;
uniform sampler2D shadowMap;

void main() {
    //if (length(noise(vec3(time * 300 + pos.x * 20000, time * 200 + pos.y * 10000, time * 500 + pos.z * 15046))) < .8) discard;
    vec3 baseColor = (texture(texture0, texCoord) * color).xyz;
    outputColor = vec4(calcLight(baseColor, pos.xyz, worldNormal.xyz, shadowMap, fragPosLightSpace), 1);
}