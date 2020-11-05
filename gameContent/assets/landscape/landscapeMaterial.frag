#version 330

#include "../../../engineContent/assets/shaders/materialFunctions/basicFragmentShader.glsl";
#include "../../../engineContent/assets/shaders/materialFunctions/lighting.glsl";

uniform sampler2D shadowMap;

void main()
{


    vec3 baseColor = (vertColor * color).xyz;

    outputColor = vec4(calcLight(baseColor, pos.xyz, worldNormal.xyz, shadowMap, fragPosLightSpace), 1);
}