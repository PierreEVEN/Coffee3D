#version 330

#include "../../../engineContent/assets/shaders/materialFunctions/basicFragmentShader.glsl";
#include "../../../engineContent/assets/shaders/materialFunctions/lighting.glsl";

void main()
{
    outputColor = lightColor(vertColor * color, worldNormal, sunDirection.xyz, pos.xyz);
}