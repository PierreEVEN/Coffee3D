#include "uniformDeclaration.glsl";

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;
layout (location = 2) in vec3 aNormal;
layout (location = 3) in vec4 aVertexColor;

out vec2 texCoord;
out vec3 pos;
out vec3 normal;
out vec3 worldNormal;
out vec4 vertColor;

uniform mat4 model;