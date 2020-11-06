#ifndef TEXTURE_GLSL
#define TEXTURE_GLSL

vec4 blurTexture(sampler2D text, vec2 iResolution, vec2 coordinates, float Quality, float Size, float Directions) {
    float Pi = 6.28318530718; // Pi*2

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