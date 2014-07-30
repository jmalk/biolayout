/*

 BioLayoutExpress3D - A tool for visualisation
 and analysis of biological networks

 Copyright (c) 2006-2012 Genome Research Ltd.
 Authors: Thanos Theo, Anton Enright, Leon Goldovsky, Ildefonso Cases, Markus Brosch, Stijn van Dongen, Michael Kargas, Benjamin Boyer and Tom Freeman
 Contact: support@biolayout.org

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

 @ author, GLSL & OpenGL code author Thanos Theo, 2011-2012

*/

FS_VARYING vec3 FS_EYE_VECTOR;
FS_VARYING vec3 FS_NORMAL;

uniform bool useOrenNayarDiffuseModel;
uniform bool texturing;
uniform sampler2D texture;

const float COS_OUTER_CONE_ANGLE = 0.8; // 36 degrees

// Oren-Nayar diffure model constant values
const float P = 0.458105;
const float SIGMA = 1.057673;
const float Eo = 0.998868;
const float SIGMA_SQUARED = SIGMA * SIGMA;
const float A = 1.0 - 0.5 * (SIGMA_SQUARED) / (SIGMA_SQUARED + 0.33);
const float B = 0.45 * (SIGMA_SQUARED) / (SIGMA_SQUARED + 0.09);
const float R = 2.0 * P / SIGMA * Eo;

float smootherstep(in float, in float, in float);
vec4 orenNayarDiffuseModel(in int, in vec3, in vec3, in vec3);
void applyTexture(inout vec4);

// Ken Perlin suggests an improved version of the smoothstep function which has zero 1st and 2nd order derivatives at t=0 and t=1
float smootherstep(in float edge0, in float edge1, in float x)
{
    // Scale, and clamp x to 0...1 range
    x = clamp( (x - edge0) / (edge1 - edge0), 0.0, 1.0 );
    // Evaluate polynomial
    return x * x * x * ( x * (x * 6.0 - 15.0) + 10.0 );
}

void main()
{
    vec4 finalColor = gl_FrontLightModelProduct.sceneColor * gl_FrontMaterial.ambient;
    vec3 lightVector;
    vec3 N = normalize(FS_NORMAL);
    vec3 L;
    vec3 R;
    vec3 E;

    float lambertTerm;
    float specularPower;
    float distance;
    float attenuation;

    #if USE_SPOT_LIGHTS
        vec3 D;
        float cosCurrentAngle;
        float cosInnerConeAngle;
        float spot;
        float spotWidth;
    #endif

    for (int index = 0; index < MAX_LIGHTS; index++)
    {
        finalColor += gl_LightSource[index].ambient * gl_FrontMaterial.ambient; // ambient light computation
        lightVector = vec3(gl_LightSource[index].position.xyz + FS_EYE_VECTOR); // -position.xyz
        L = normalize(lightVector);
        distance = length(L);
        attenuation = 1.0 / (gl_LightSource[index].constantAttenuation +
                             gl_LightSource[index].linearAttenuation * distance +
                             gl_LightSource[index].quadraticAttenuation * distance * distance);

        #if USE_SPOT_LIGHTS
            // used for spotlight lighting model
            D = normalize(gl_LightSource[index].spotDirection);
            cosCurrentAngle = dot(-L, D);
            cosInnerConeAngle = gl_LightSource[index].spotCosCutoff;
            spotWidth = (cosInnerConeAngle - COS_OUTER_CONE_ANGLE);
            spot = (useOrenNayarDiffuseModel) ? smootherstep(cosInnerConeAngle - spotWidth, cosInnerConeAngle + spotWidth, cosCurrentAngle)
                                              : clamp( (cosCurrentAngle - COS_OUTER_CONE_ANGLE) / spotWidth, 0.0, 1.0 );
        #endif

        lambertTerm = dot(N, L);
        if (lambertTerm > 0.0)
        {
            R = reflect(-L, N);
            E = normalize(FS_EYE_VECTOR);

            lambertTerm = max(0.0, lambertTerm); // diffuse light computation
            specularPower = pow(max(dot(R, E), 0.0001), gl_FrontMaterial.shininess); // failsafe GLSL Shader value for specular light computation

            #if USE_SPOT_LIGHTS
                finalColor += (useOrenNayarDiffuseModel ? orenNayarDiffuseModel(index, -FS_EYE_VECTOR, N, L) : gl_LightSource[index].diffuse) * gl_FrontMaterial.diffuse * lambertTerm * attenuation * spot; // diffuse light computation
                finalColor += gl_LightSource[index].specular * gl_FrontMaterial.specular * specularPower * attenuation * spot; // specular light computation
            #else
                finalColor += (useOrenNayarDiffuseModel ? orenNayarDiffuseModel(index, -FS_EYE_VECTOR, N, L) : gl_LightSource[index].diffuse) * gl_FrontMaterial.diffuse * lambertTerm * attenuation; // diffuse light computation
                finalColor += gl_LightSource[index].specular * gl_FrontMaterial.specular * specularPower * attenuation; // specular light computation
            #endif
        }

        finalColor = clamp(finalColor, 0.0, 1.0);
    }

    if (texturing)
        applyTexture(finalColor);

    gl_FragColor = finalColor;
}

vec4 orenNayarDiffuseModel(in int index, in vec3 position, in vec3 FS_NORMAL, in vec3 lightVector)
{
    vec3 FS_EYE_VECTOR = normalize(-position);

    float nDotL = dot(FS_NORMAL, lightVector);
    float nDotE = dot(FS_NORMAL, FS_EYE_VECTOR);

    float sinTr = length( cross(FS_EYE_VECTOR, FS_NORMAL) );
    float cosTr = clamp(nDotE, 0.0001, 1.0);
    float sinTi = length( cross(lightVector, FS_NORMAL) );
    float cosTi = clamp(nDotL, 0.0001, 1.0);
    float tanTi = sinTi / cosTi;
    float tanTr = sinTr / cosTr;

    vec3 Ep = normalize(FS_EYE_VECTOR - nDotE * FS_NORMAL);
    vec3 Lp = normalize(lightVector - nDotL * FS_NORMAL);

    return 2.0 * R * cosTi * ( A + B * max( 0.0, dot(Ep, Lp) ) * max(sinTr, sinTi) * min(tanTi, tanTr) ) * gl_LightSource[index].diffuse;
}

void applyTexture(inout vec4 finalColor)
{
    vec4 textureColor = texture2D(texture, gl_TexCoord[0].st);
    finalColor.rgb = mix(textureColor.rgb, finalColor.rgb, 0.5);
    // finalColor.a = textureColor.a;
}