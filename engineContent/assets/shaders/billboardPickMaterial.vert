#version 330 core

#include "materialFunctions/basicVertexShader.glsl";

uniform vec4 position;

void main()
{
    vec3 CameraRight_worldspace = vec3(viewMatrix[0][0], viewMatrix[1][0], viewMatrix[2][0]);
    vec3 CameraUp_worldspace = vec3(viewMatrix[0][1], viewMatrix[1][1], viewMatrix[2][1]);

    vec3 vertexPosition_worldspace = position.xyz +
    + CameraRight_worldspace * aPos.x * position.w
    + CameraUp_worldspace * aPos.y * position.w;

    gl_Position = projMatrix * viewMatrix * vec4(vertexPosition_worldspace, 1.0f);
}