#version 330

#include "materialFunctions/basicFragmentShader.glsl";
#include "materialFunctions/noises.glsl";

vec3 vecLerp(vec3 A, vec3 B, float pow) {
    return B * pow + (1 - pow) * A;
}

void main() {

    float zValue = min(1, max(0, pos.z / 100));

    vec3 colorA = vec3(.5,.6,.6);
    vec3 colorB = vec3(.5, .8, 1);
    vec3 realColor = vecLerp(colorA, colorB, zValue);

    vec3 lightDir = normalize(sunDirection.xyz);
    vec4 finalColor = vec4(realColor, 1) * color;
    float val = pow(min(1, max(0, dot(lightDir, worldNormal))), 200);
    outputColor = (finalColor + val) * length(sunDirection);
}