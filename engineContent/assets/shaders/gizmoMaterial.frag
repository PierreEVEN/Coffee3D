#version 330

#include "../../../engineContent/assets/shaders/materialFunctions/basicFragmentShader.glsl";

uniform int axis;

void main()
{
    vec3 baseColor = vertColor.xyz * .5;
    if (baseColor.x + baseColor.y + baseColor.z > 1) baseColor = vec3(1, 1, 0.05);
    outputColor = vec4(baseColor, 1);
}