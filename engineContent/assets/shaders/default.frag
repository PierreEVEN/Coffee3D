#version 330

#include "materialFunctions/basicFragmentShader.glsl";
#include "materialFunctions/lighting.glsl";

uniform sampler2D texture0;
uniform sampler2D shadowMap;

void main() {
    outputColor = vec4(lightColor(texture(texture0, texCoord) * color, worldNormal, sunDirection.xyz, pos.xyz).xyz, 1);



    vec3 finalColor = (texture(texture0, texCoord) * color).rgb;
    vec3 normal = normalize(worldNormal);
    vec3 lightColor = vec3(1.0);
    // ambient
    vec3 ambient = 0.15 * finalColor;
    // diffuse
    vec3 lightDir = sunDirection.xyz;
    float diff = max(dot(lightDir, normal), 0.0);
    vec3 diffuse = diff * lightColor;
    // specular
    vec3 viewDir = normalize(cameraPos.xyz - pos);
    float spec = 0.0;
    vec3 halfwayDir = normalize(lightDir + cameraDir.xyz);
    spec = pow(max(dot(normal, halfwayDir), 0.0), 64.0);
    vec3 specular = spec * lightColor;
    // calculate shadow
    float shadow = ShadowCalculation(fragPosLightSpace, shadowMap, normal, lightDir);
    vec3 lighting = (ambient + (1.0 - shadow) * (diffuse + specular)) * finalColor;

    outputColor = vec4(lighting, 1.0);

}