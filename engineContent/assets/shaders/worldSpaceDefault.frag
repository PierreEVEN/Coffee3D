

#version 330

out vec4 outputColor;

in vec2 texCoord;
in vec3 pos;
in vec3 normal;
in vec3 worldNormal;

uniform sampler2D texture0;
uniform vec4 color;

layout (std140) uniform shader_data
{
    mat4 viewMatrix;
    mat4 projMatrix;
    vec3 cameraPos;
    vec3 cameraDir;
    float time;
};



void main()
{
    vec3 lightDir = normalize(vec3(1, 1, 1));

    vec4 finalColor;

    if (abs(worldNormal.z) > .5f) {
        finalColor = texture(texture0, pos.xy) * color;
    }
    else if (abs(worldNormal.x) > .5f) {
        finalColor = texture(texture0, pos.yz) * color;
    }
    else {
        finalColor = texture(texture0, pos.xz) * color;
    }

    float val = pow(min(1, max(0, dot(lightDir, worldNormal))), 1);
    outputColor = finalColor * val + finalColor * 0.4;
}