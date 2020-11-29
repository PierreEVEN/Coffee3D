#version 330

#include "materialFunctions/basicFragmentShader.glsl";
#include "materialFunctions/noises.glsl";

vec3 vecLerp(vec3 A, vec3 B, float pow) {
    return B * pow + (1 - pow) * A;
}

void main() {

    float zValue = min(1, max(0, pos.z / 100));

    vec3 norm = normalize(pos.xyz - cameraPos.xyz);

    vec3 colorA = vec3(.3,.4,.3);
    vec3 colorB = vec3(.3, .8, 1) / 2;
    vec3 realColor = vecLerp(colorA, colorB, norm.z + sunDirection.z * .5f + norm.y * -0.5f);

    vec3 lightDir = normalize(sunDirection.xyz);
    vec4 finalColor = vec4(realColor, 1) * color;
    vec4 sunPow = pow(pow(min(1, max(0, dot(lightDir, norm))), 300) * 2, 3) * vec4(1, 1, .5, 1);


    outputColor = (finalColor + sunPow) * length(sunDirection);


    outputColor += vec4(pow(noise(norm * 200), 50)) * 0.2f;

}