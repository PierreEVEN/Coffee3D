

#version 330

out vec4 outputColor;

in vec2 texCoord;
in vec3 pos;
in vec3 normal;
in vec3 worldNormal;

uniform sampler2D texture0;

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
    vec3 lightPos = cameraPos;//vec3(0,0,0);

    vec3 lightDir = normalize(vec3(1, 1, 1));

    vec4 color = texture(texture0, texCoord);

    float val = pow(min(1, max(0, dot(lightDir, worldNormal))), 1);
    outputColor = color * val + color * 0.1;
}