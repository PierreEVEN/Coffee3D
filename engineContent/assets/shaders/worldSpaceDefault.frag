#version 330

#include "materialFunctions/basicFragmentShader.glsl";
#include "materialFunctions/lighting.glsl";

uniform sampler2D texture0;

void main()
{
    vec4 textureColor;
    if (abs(worldNormal.z) > .5f) textureColor = texture(texture0, pos.xy);
    else if (abs(worldNormal.x) > .5f) textureColor = texture(texture0, pos.yz);
    else textureColor = texture(texture0, pos.xz);
    outputColor = lightColor(textureColor * color, worldNormal, sunDirection.xyz, pos.xyz);
}