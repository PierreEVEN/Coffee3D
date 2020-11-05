#include "uniformDeclaration.glsl";

in vec2 texCoord;
in vec3 pos;
in vec3 normal;
in vec3 worldNormal;
in vec4 vertColor;
in vec4 fragPosLightSpace;

out vec4 outputColor;
uniform vec4 color;
uniform float scale;