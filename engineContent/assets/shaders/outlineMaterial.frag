

#version 330

out vec4 outputColor;

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
    outputColor = vec4(1,1,0,0);
}