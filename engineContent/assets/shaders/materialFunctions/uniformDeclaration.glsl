layout (std140) uniform shader_data
{
    mat4 viewMatrix;
    mat4 projMatrix;
    mat4 lightSpaceMatrix;
    vec4 cameraPos;
    vec4 cameraDir;
    vec4 sunDirection;
    float time;
};