#version 330

#include "../../../engineContent/assets/shaders/materialFunctions/basicFragmentShader.glsl";

uniform int axis;

void main()
{
    vec3 baseColor = vertColor.xyz;

    switch(axis) {
        case 0:
            baseColor = baseColor - .4;
            break;
        case 1:
            baseColor = vec3(.5, .5, 0);
            break;
        case 2:
            if (length(baseColor) <= 1) baseColor *= vec3(2, 0, 0);
            break;
        case 3:
            if (length(baseColor) <= 1) baseColor *= vec3(0, 2, 0);
            break;
        case 4:
            if (length(baseColor) <= 1) baseColor *= vec3(0, 0, 2);
            break;
    }

    outputColor = vec4(baseColor, 1);
}