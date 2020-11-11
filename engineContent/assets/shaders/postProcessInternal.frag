#version 330

#include "materialFunctions/uniformDeclaration.glsl";
#include "materialFunctions/textures.glsl";

#define SELECTION_BIT 0

in vec2 TexCoords;

out vec4 outputColor;
uniform sampler2D colorTexture;
uniform sampler2D depthTexture;
uniform sampler2D stencilTexture;

int getOffset(int dirX, int dirY) {
    vec2 offset = vec2(0.002 * (framebufferSize.y / framebufferSize.x), 0.002);
    vec2 pos = vec2(dirX, dirY) * offset + TexCoords;
    if (pos.x < 0 || pos.y < 0 || pos.x > 1 || pos.y > 1) return 0;
    return bitMask(texture(stencilTexture, pos), SELECTION_BIT);
}

int getSelectionOutline() {

    int summ = getOffset(0,0);

    summ += getOffset(1,1);
    summ += getOffset(-1,1);
    summ += getOffset(-1,-1);
    summ += getOffset(1,-1);
    if (summ == 0 || summ == 5) return 0;
    return 1;
}

void main()
{
    const float exposure = 1;
    const float gamma = 2.2;

    vec3 hdrColor = texture(colorTexture, TexCoords).rgb +  + getSelectionOutline() * vec3(1,.5,0);
    vec3 bloomColor = vec3(0);//max(vec3(0), blurTexture(colorTexture, TexCoords).rgb - 1);
    hdrColor += bloomColor; // additive blending
    // tone mapping
    vec3 result = vec3(1.0) - exp(-hdrColor * exposure);
    // also gamma correct while we're at it
    result = pow(result, vec3(1.0 / gamma));

    outputColor = vec4(result, 1.0);
}