#version 330

#include "materialFunctions/basicFragmentShader.glsl";
#include "materialFunctions/lighting.glsl";

uniform sampler2D texture0;
uniform sampler2D shadowMap;


void main()
{
    vec4 textureColor;
    if (abs(worldNormal.z) > .5f) textureColor = texture(texture0, pos.xy);
    else if (abs(worldNormal.x) > .5f) textureColor = texture(texture0, pos.yz);
    else textureColor = texture(texture0, pos.xz);

    vec3 baseColor = (textureColor * color).xyz;

    outputColor = vec4(calcLight(baseColor, pos.xyz, worldNormal.xyz, shadowMap, fragPosLightSpace), 1);

}