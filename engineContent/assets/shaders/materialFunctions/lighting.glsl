#include "noises.glsl";

#define MIN_LIGHTING 0
#define AMBIANT_INTENSITY 0.3

float cloudTexture(vec3 pos) {
    return (noise((pos + time * 5) / 10) * noise((pos + time * 5) / 20)) / 10;
}

float noiseTexture(vec3 pos, float light) {
    return (noise(pos * 5) / 18) * (light / 2 + .2) - .05;
}

float calcLightIntensity(vec3 normal, vec3 sunDirection) {
    return min(1, max(0, dot(normal, sunDirection)));
}

vec4 lightColor(vec4 sourceColor, vec3 normal, vec3 sunDirection, vec3 pos) {
    float lightIntensity = calcLightIntensity(normal, sunDirection);
    vec4 lightedColor = sourceColor * lightIntensity;
    vec4 ambiant = sourceColor * AMBIANT_INTENSITY;
    return lightedColor + ambiant + noiseTexture(pos, lightIntensity) - cloudTexture(pos);
}
