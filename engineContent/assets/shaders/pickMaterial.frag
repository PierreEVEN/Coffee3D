

#version 330

out vec4 outputColor;

uniform int pickId;

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
    int red = (pickId & 0x000000FF);
    int green = (pickId & 0x0000FF00) >> 8;
    int blue = (pickId & 0x00FF0000) >> 16;
    int alpha = (pickId & 0xFF000000) >> 24;
    outputColor = vec4(red / 255f, green / 255f, blue / 255f, alpha / 255f);
}