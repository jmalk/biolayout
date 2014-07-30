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

 @ author, GLSL & OpenGL code author Thanos Theo, 2009-2010-2011-2012

*/

uniform sampler2D texture;
uniform sampler3D perlinNoise3DTexture;
uniform bool embossNodeTexture;
uniform float px;
uniform float py;

// Chroma Depth constant values for their transfer functions
const float CHROMA_DEPTH_RED = 10.0;
const float CHROMA_DEPTH_BLUE = -5.0;
const float CHROMA_DEPTH_NEAR = 1.0;
const float CHROMA_DEPTH_FAR = 2.0;

// Erosion constants
const float EROSION_MIN = 0.25;
const float EROSION_MAX = 0.75;

// Specular model constant values
const float SHININESS_DIVIDER_FOR_HALF_VECTOR = 2.0;
const float SHININESS_DIVIDER_FOR_REFLECTION_VECTOR = 7.0;
const float SPECULAR_DIVIDER = 1.8;

// Oren-Nayar diffuse model constant values
const float P = 0.458105;
const float SIGMA = 1.057673;
const float Eo = 0.998868;
const float SIGMA_SQUARED = SIGMA * SIGMA;
const float A = 1.0 - 0.5 * (SIGMA_SQUARED) / (SIGMA_SQUARED + 0.33);
const float B = 0.45 * (SIGMA_SQUARED) / (SIGMA_SQUARED + 0.09);
const float R = 2.0 * P / SIGMA * Eo;

// Solid WireFrame constant values
const float SOLID_WIREFRAME_BRIGHTNESS_FACTOR = 5.0;

// Emboss & Fog constants
const float K = 0.85;
const float LOG2 = 1.442695;

vec3 rainbowTransferFunction(in float);
vec3 heatedObject(in float);
vec3 chromaDepth1TransferFunction(in float);
vec3 chromaDepth2TransferFunction(in float);

float smootherstep(in float, in float, in float);
vec2 smootherstep(in vec2, in vec2, in vec2);

void applyOldStyleTransparency();
void applyErosion(in vec4);
vec4 applyADSLightingModel(in bool, in bool, in vec3, in vec3, in vec4);
vec4 orenNayarDiffuseModel(in vec3, in vec3, in vec3);
void applyTexture(inout vec4, in vec2);
float applyFogFactor();
vec4 applyFog(in vec4);

vec3 rainbowTransferFunction(in float depth)
{
    // the depth value can be the gl_Position.z one or (gl_FragCoord.z / gl_FragCoord.w)
    // usage in BL: vec4(1.0 - chromaDepth2TransferFunction(gl_FragCoord.z / gl_FragCoord.w), 1.0)
    depth = clamp( (depth - CHROMA_DEPTH_RED) / (CHROMA_DEPTH_BLUE - CHROMA_DEPTH_RED), 0.0, 1.0 );

    if (depth < 0.0) // black if below bounds
    {
        return vec3(0.0);
    }
    else if ( (depth >= 0.0) && (depth < 0.2) ) // purple to blue ramp
    {
        vec3 color;
        color.r = 0.5 * (1.0 - depth / 0.2);
        color.g = 0.0;
        color.b = 0.5 + (0.5 * depth / 0.2);

        return clamp(color, 0.0, 1.0);
    }
    else if ( (depth >= 0.2) && (depth < 0.4) ) // blue to cyan ramp
    {
        vec3 color;
        color.r = 0.0;
        color.g = (depth - 0.2) * 5.0;
        color.b = 1.0;

        return clamp(color, 0.0, 1.0);
    }
    else if ( (depth >= 0.4) && (depth < 0.6) ) // cyan to green ramp
    {
        vec3 color;
        color.r = 0.0;
        color.g = 1.0;
        color.b = (0.6 - depth) * 5.0;

        return clamp(color, 0.0, 1.0);
    }
    else if ( (depth >= 0.6) && (depth < 0.8) ) // green to yellow ramp
    {
        vec3 color;
        color.r = (depth - 0.6) * 5.0;
        color.g = 1.0;
        color.b = 0.0;

        return clamp(color, 0.0, 1.0);
    }
    else if ( (depth >= 0.8) && (depth <= 1.0) ) // yellow to red ramp
    {
        vec3 color;
        color.r = 1.0;
        color.g = (1.0 - depth) * 5.0;
        color.b = 0.0;

        return clamp(color, 0.0, 1.0);
    }
    else if (depth > 1.0) // white if above bound
    {
        return vec3(1.0);
    }
}

vec3 heatedObject(in float depth)
{
    // the depth value can be the gl_Position.z one or (gl_FragCoord.z / gl_FragCoord.w)
    // usage in BL: vec4(1.0 - chromaDepth2TransferFunction(gl_FragCoord.z / gl_FragCoord.w), 1.0)
    depth = clamp( (depth - CHROMA_DEPTH_RED) / (CHROMA_DEPTH_BLUE - CHROMA_DEPTH_RED), 0.0, 1.0 );

    vec3 rgb;
    rgb.r = 3. * ( depth - (0.0 / 6.0) );
    rgb.g = 0.;
    rgb.b = 0.;

    if( depth >= (1.0 / 3.0) )
    {
        rgb.r = 1.0;
        rgb.g = 3.0 * ( depth - (1.0 / 3.0) );
    }

    if( depth >= (2.0 / 3.0) )
    {
            rgb.g = 1.0;
            rgb.b = 3.0 * ( depth - (2.0 / 3.0) );
    }

    return clamp(rgb, 0.0, 1.0);
}

vec3 chromaDepth1TransferFunction(in float depth)
{
    // the depth value can be the gl_Position.z one or (gl_FragCoord.z / gl_FragCoord.w)
    // usage in BL: vec4(1.0 - chromaDepth2TransferFunction(gl_FragCoord.z / gl_FragCoord.w), 1.0)
    depth = clamp( (depth - CHROMA_DEPTH_RED) / (CHROMA_DEPTH_BLUE - CHROMA_DEPTH_RED), 0.0, 1.0 );

    float r = 1.0;
    float g = 0.0;
    float b = 1.0 - 6.0 * ( depth - (5.0 / 6.0) );

    if ( depth <= (5.0 / 6.0) )
    {
        r = 6.0 * ( depth - (4.0 / 6.0 ) );
        g = 0.0;
        b = 1.0;
    }

    if ( depth <= (4.0 / 6.0) )
    {
        r = 0.0;
        g = 1.0 - 6.0 * ( depth - (3.0 / 6.0 ) );
        b = 1.0;
    }

    if ( depth <= (3.0 / 6.0) )
    {
        r = 0.0;
        g = 1.0;
        b = 6.0 * ( depth - (2.0 / 6.0 ) );
    }

    if ( depth <= (2.0 / 6.0) )
    {
        r = 1.0 - 6.0 * ( depth - (1.0 / 6.0 ) );
        g = 1.0;
        b = 0.0;
    }

    if ( depth <= (1.0 / 6.0) )
    {
        r = 1.0;
        g = 6.0 * depth;
    }

    return clamp(vec3(r, g, b), 0.0, 1.0);
}

vec3 chromaDepth2TransferFunction(in float depth)
{
    // the depth value can be the gl_Position.z one or (gl_FragCoord.z / gl_FragCoord.w)
    // usage in BL: vec4(1.0 - chromaDepth2TransferFunction(gl_FragCoord.z / gl_FragCoord.w), 1.0)
    depth = clamp( (depth - CHROMA_DEPTH_NEAR) / CHROMA_DEPTH_FAR, 0.0, 1.0 );

    // these formulas are based on code from American Paper Optics at:
    // http://www.chromatek.com/Image_Design/Color_Lookup_Functions/color_lookup_functions.shtml
    float depth2 = depth * depth;
    vec3 rgb;
    if (depth < 0.5)
    {
        rgb.g = 1.6 * depth2 + 1.2 * depth;
    }
    else
    {
        rgb.g = 3.2 * depth2 - 6.8 * depth + 3.6;
        rgb.b = depth2 * -4.8 + 9.2 * depth - 3.4;
    }
    depth  = depth  / 0.9;
    depth2 = depth2 / 0.81;
    rgb.r = -2.14 * depth2 * depth2 -1.07 * depth2 * depth + 0.133 * depth2 + 0.0667 * depth + 1.0;

    return clamp(rgb, 0.0, 1.0);
}

// Prof. Ken Perlin suggests an improved version of the smoothstep function which has zero 1st and 2nd order derivatives at t=0 and t=1.
float smootherstep(in float edge0, in float edge1, in float x)
{
    // Scale, and clamp x to 0...1 range
    x = clamp( (x - edge0) / (edge1 - edge0), 0.0, 1.0 );
    // Evaluate polynomial
    return x * x * x * ( x * (x * 6.0 - 15.0) + 10.0 );
}

// vec2 variaty of the above smootherstep() function
vec2 smootherstep(in vec2 edge0, in vec2 edge1, in vec2 x)
{
    // Scale, and clamp x to 0...1 range
    x = clamp( (x - edge0) / (edge1 - edge0), 0.0, 1.0 );
    // Evaluate polynomial
    return x * x * x * ( x * (x * 6.0 - 15.0) + 10.0 );
}

void applyOldStyleTransparency()
{
    if ( mod(gl_FragCoord.x, 2.0) == mod(gl_FragCoord.y, 2.0) )
        discard; // screen-door transparency effect
}

void applyErosion(in vec4 noisevec)
{
    float intensity = (noisevec[0] + noisevec[1] + noisevec[2] + noisevec[3]) / 2.0;
    if ( (intensity < EROSION_MIN) || (intensity > EROSION_MAX) )
        discard;
}

vec4 applyADSLightingModel(in bool useOrenNayarDiffuseModel, in bool useHalfVector, in vec3 normal, in vec3 position, in vec4 sceneColor)
{
    vec3 N = normalize(normal);
    vec3 L = normalize(gl_LightSource[0].position.xyz - position);
    float distance = length(L);
    float attenuation = 1.0 / (gl_LightSource[0].constantAttenuation +
                   	       gl_LightSource[0].linearAttenuation * distance +
                   	       gl_LightSource[0].quadraticAttenuation * distance * distance);

    float lambertTerm = max( 0.0, dot(N, L) );
    vec4 diffuse = ( (useOrenNayarDiffuseModel) ? orenNayarDiffuseModel(position, N, L) : gl_LightSource[0].diffuse ) * lambertTerm * attenuation;

    vec4 specular = vec4(0.0);
    if (lambertTerm > 0.0)
    {
        float specularPower;
        if (useHalfVector)
        {
            vec3 H = normalize(L - position);
            float NxH = max( 0.0, dot(N, H) );
            specularPower = pow(NxH, gl_FrontMaterial.shininess / SHININESS_DIVIDER_FOR_HALF_VECTOR);
        }
        else
        {   vec3 R = reflect(L, N);
            float RxE = max(dot( R, normalize(position) ), 0.0);
            specularPower = pow(RxE, gl_FrontMaterial.shininess / SHININESS_DIVIDER_FOR_REFLECTION_VECTOR) ;
        }
        specular = (gl_LightSource[0].specular * specularPower / SPECULAR_DIVIDER) * attenuation;
    }

    return vec4(sceneColor + gl_LightSource[0].ambient + diffuse * vec4(sceneColor.rgb, 1.0) + specular * sceneColor.a);
}

vec4 orenNayarDiffuseModel(in vec3 position, in vec3 normal, in vec3 lightVector)
{
    vec3 eyeVector = normalize(-position);

    float nDotL = dot(normal, lightVector);
    float nDotE = dot(normal, eyeVector);

    float sinTr = length( cross(eyeVector, normal) );
    float cosTr = clamp(nDotE, 0.0001, 1.0);
    float sinTi = length( cross(lightVector, normal) );
    float cosTi = clamp(nDotL, 0.0001, 1.0);
    float tanTi = sinTi / cosTi;
    float tanTr = sinTr / cosTr;

    vec3 Ep = normalize(eyeVector - nDotE * normal);
    vec3 Lp = normalize(lightVector - nDotL * normal);

    return 2.0 * R * cosTi * ( A + B * max( 0.0, dot(Ep, Lp) ) * max(sinTr, sinTi) * min(tanTi, tanTr) ) * gl_LightSource[0].diffuse;
}

void applyTexture(inout vec4 finalColor, in vec2 textureCoords)
{
    if (embossNodeTexture)
    {
        // center
        vec4 neighborCenter = texture2D(texture, textureCoords);

        float neighborFragmentX = 2.0 * (1.0 / px); // multiply by 2.0 to make the emboss filter more visible
        float neighborFragmentY = 2.0 * (1.0 / py); // multiply by 2.0 to make the emboss filter more visible

        // one right
        textureCoords.s = textureCoords.s + neighborFragmentX;
        vec4 neighborRight = texture2D(texture, textureCoords);

        // one down
        textureCoords.t = textureCoords.t - neighborFragmentY;
        vec4 neighborRightDown = texture2D(texture, textureCoords);

        // one left
        textureCoords.s = textureCoords.s - neighborFragmentX;
        vec4 neighborDown = texture2D(texture, textureCoords);

        // two up
        textureCoords.t = textureCoords.t + 2.0 * neighborFragmentY;
        vec4 neighborUp = -texture2D(texture, textureCoords);

        // one left
        textureCoords.s = textureCoords.s - neighborFragmentX;
        vec4 neighborUpLeft = -texture2D(texture, textureCoords);

        // one down
        textureCoords.t = textureCoords.t - neighborFragmentY;
        vec4 neighborLeft = -texture2D(texture, textureCoords);

        vec4 color = (neighborRight + neighborRightDown + neighborDown + neighborUp + neighborUpLeft + neighborLeft) / 9.0;
        vec3 gradient = vec3(0.3 + (color.r + color.g + color.b + color.a) / 4.0);
        vec3 gradientRGB = mix(neighborCenter.rgb, gradient, K);

        finalColor.rgb *= gradientRGB;
    }
    else
    {
        vec4 textureColor = texture2D(texture, textureCoords);
        finalColor.rgb = mix(textureColor.rgb, finalColor.rgb, 0.5);
        // finalColor.a = textureColor.a;
    }
}

float applyFogFactor()
{
    // per-pixel exponential fog
    float z = gl_FragCoord.z / gl_FragCoord.w;
    float fogFactor = exp2(-gl_Fog.density * gl_Fog.density * z * z * LOG2);
    fogFactor = clamp(fogFactor, 0.0, 1.0);
    fogFactor = smootherstep(0.0, 1.0, fogFactor);

    return fogFactor;
}

vec4 applyFog(in vec4 color)
{
    // per-pixel exponential fog
    float z = gl_FragCoord.z / gl_FragCoord.w;
    float fogFactor = exp2(-gl_Fog.density * gl_Fog.density * z * z * LOG2);
    fogFactor = clamp(fogFactor, 0.0, 1.0);
    fogFactor = smootherstep(0.0, 1.0, fogFactor);

    return mix(gl_Fog.color, color, fogFactor);
}