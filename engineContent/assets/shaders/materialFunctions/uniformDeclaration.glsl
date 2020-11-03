layout (std140) uniform shader_data
{
    mat4 viewMatrix;
    mat4 projMatrix;
    vec4 cameraPos;
    vec4 cameraDir;
    vec4 sunDirection;
    float time;
};