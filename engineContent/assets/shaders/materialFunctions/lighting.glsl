#ifndef LIGHTING_GLSL
#define LIGHTING_GLSL

#include "noises.glsl";


vec3 vecLerp(vec3 A, vec3 B, float pow) {
    return B * pow + (1 - pow) * A;
}

float cloudTexture(vec3 pos) {
    return (noise((pos + time * 5) / 10) * noise((pos + time * 5) / 20)) / 10;
}

float noiseTexture(vec3 pos, float light) {
    return (noise(pos * 5) / 18) * (light / 2 + .2) - .05;
}

float calcLightIntensity(vec3 normal, vec3 sunDirection) {
    return max(0, dot(normal, sunDirection));
}

vec4 getAmbiant(vec3 sunVector) {

    vec3 sun = normalize(sunVector);

    vec3 color = vecLerp(vec3(.4f, .5f, .5f), vec3(1), max(0, min(1, sun.z)));

    color += vec3(1, 0, -.5f) * pow(max(0, sun.y), 8);


    return vec4(max(0.05f, sunVector.z * 0.4) * color, 1);
}


vec4 lightColor(vec4 sourceColor, vec3 normal, vec3 sunDirection, vec3 pos) {
    float lightIntensity = calcLightIntensity(normal, sunDirection);
    vec4 lightedColor = sourceColor * lightIntensity;
    vec4 ambiant = sourceColor * getAmbiant(sunDirection.xyz) * max(0, dot(sunDirection, vec3(0,0,1)));
    return max(vec4(0), lightedColor + ambiant + noiseTexture(pos, lightIntensity) - cloudTexture(pos));
}


float ShadowCalculation(vec4 fragPosLightSpace, sampler2D shadowTexture, vec3 normal, vec3 lightDir)
{
    float biasValue = 0.01;

    // perform perspective divide
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    // transform to [0,1] range
    projCoords = projCoords * 0.5 + 0.5;
    if (projCoords.z > 1) return 0.f;
    // get closest depth value from light's perspective (using [0,1] range fragPosLight as coords)
    float closestDepth = texture(shadowTexture, projCoords.xy).r;
    // get depth of current fragment from light's perspective
    float currentDepth = projCoords.z;

    // check whether current frag pos is in shadow
    float bias = max(biasValue * (1.0 - dot(normal, lightDir)), biasValue / 10);  ;

    float shadow = 0.0;

    const int texelQuality = 1;

    vec2 texelSize = 1.0 / textureSize(shadowTexture, 0);
    for(int x = -texelQuality; x <= texelQuality; ++x)
    {
        for(int y = -texelQuality; y <= texelQuality; ++y)
        {
            float pcfDepth = texture(shadowTexture, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth - bias > pcfDepth  ? 1.0 : 0.0;
        }
    }
    shadow /= (2 * texelQuality + 1) * (2 * texelQuality + 1);


    return shadow * pow(max(0, normalize(sunDirection).z), 0.1f);
}

vec3 calcLight(vec3 baseColor, vec3 worldPos, vec3 norm, sampler2D shadowTexture, vec4 posLightSpace) {

    // ambient
    vec3 ambient = getAmbiant(sunDirection.xyz).xyz * baseColor;
    // diffuse
    vec3 lightDir = sunDirection.xyz;
    float diff = max(dot(lightDir, norm), 0.0) * (1 - noise(worldPos * 0.02f + vec3(time * 0.5f)) * 0.4f);
    vec3 diffuse = vec3(diff) * pow(max(0, sunDirection.z), 0.2f);
    // calculate shadow
    float shadow = 0;
    if (shadowIntensity != 0) shadow = ShadowCalculation(posLightSpace, shadowTexture, norm, lightDir) * shadowIntensity;
    vec3 result = (ambient + (1.0 - shadow) * diffuse) * baseColor;
    return result;
}

#endif