#include "noises.glsl";

#define MIN_LIGHTING 0
#define AMBIANT_INTENSITY .3

float cloudTexture(vec3 pos) {
    return (noise((pos + time * 5) / 10) * noise((pos + time * 5) / 20)) / 10;
}

float noiseTexture(vec3 pos, float light) {
    return (noise(pos * 5) / 18) * (light / 2 + .2) - .05;
}

float calcLightIntensity(vec3 normal, vec3 sunDirection) {
    return max(0, dot(normal, sunDirection));
}

vec4 lightColor(vec4 sourceColor, vec3 normal, vec3 sunDirection, vec3 pos) {
    float lightIntensity = calcLightIntensity(normal, sunDirection);
    vec4 lightedColor = sourceColor * lightIntensity;
    vec4 ambiant = sourceColor * AMBIANT_INTENSITY * max(0, dot(sunDirection, vec3(0,0,1)));
    return max(vec4(0), lightedColor + ambiant + noiseTexture(pos, lightIntensity) - cloudTexture(pos));
}


float ShadowCalculation(vec4 fragPosLightSpace, sampler2D shadowTexture, vec3 normal, vec3 lightDir)
{
    // perform perspective divide
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    // transform to [0,1] range
    projCoords = projCoords * 0.5 + 0.5;
    // get closest depth value from light's perspective (using [0,1] range fragPosLight as coords)
    float closestDepth = texture(shadowTexture, projCoords.xy).r;
    // get depth of current fragment from light's perspective
    float currentDepth = projCoords.z;

    if (currentDepth > 1) return 0;
    // check whether current frag pos is in shadow
    float bias = max(0.05 * (1.0 - dot(normal, lightDir)), 0.005);  ;

    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowTexture, 0);
    for(int x = -1; x <= 1; ++x)
    {
        for(int y = -1; y <= 1; ++y)
        {
            float pcfDepth = texture(shadowTexture, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth - bias > pcfDepth  ? 1.0 : 0.0;
        }
    }
    shadow /= 9.0;


    return shadow;
}