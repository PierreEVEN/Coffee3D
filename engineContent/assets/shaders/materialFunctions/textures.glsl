#ifndef TEXTURE_GLSL
#define TEXTURE_GLSL

int bitMask(vec4 color, int bit) {
    return (1 << bit) & ((int(color.r * 256) & 0xff) + ((int(color.g * 256) & 0xff) << 8) + ((int(color.b * 256) & 0xff) << 16) + ((int(color.a * 256) & 0xff) << 24));
}

vec4 blurTexture(sampler2D text, vec2 coordinates) {
    float Pi = 6.28318530718; // Pi*2
    vec2 iResolution = vec2(1);

    // GAUSSIAN BLUR SETTINGS {{{
    float Directions = 16.0; // BLUR DIRECTIONS (Default 16.0 - More is better but slower)
    float Quality = 10.0; // BLUR QUALITY (Default 4.0 - More is better but slower)
    float Size = 0.02; // BLUR SIZE (Radius)
    // GAUSSIAN BLUR SETTINGS }}}

    vec2 Radius = Size/iResolution.xy;

    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = coordinates/iResolution.xy;
    // Pixel colour
    vec4 Color = texture(text, uv);

    // Blur calculations
    for( float d=0.0; d<Pi; d+=Pi/Directions)
    {
        for(float i=1.0/Quality; i<=1.0; i+=1.0/Quality)
        {
            Color += texture( text, uv+vec2(cos(d),sin(d))*Radius*i);
        }
    }

    // Output to screen
    Color /= Quality * Directions - 15.0;
    return Color;
}

#endif