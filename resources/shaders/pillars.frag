

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

    vec3 lightDir = normalize((lightPos - pos));//normalize(vec3(1, 1, 0));

    vec4 color = vec4(0.5,0.5, 0.5, 1.f) * (texture(texture0, texCoord) * 0.5 + 0.1);

    float val = pow(min(1, max(0, 1 - length((pos - lightPos)) / 30)), 2);
    outputColor = color * val + color * 0.01;
}