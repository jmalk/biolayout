/*

 BioLayoutExpress3D - A tool for visualisation
 and analysis of biological networks

 Copyright (c) 2006-2012 Genome Research Ltd.
 Authors: Thanos Theo, Anton Enright, Leon Goldovsky, Ildefonso Cases, Markus Brosch, Stijn van Dongen, Benjamin Boyer and Tom Freeman
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

FS_VARYING vec3 FS_POSITION;
FS_VARYING vec3 FS_MC_POSITION;
FS_VARYING vec3 FS_NORMAL;
FS_VARYING vec4 FS_SCENE_COLOR;
FS_VARYING vec2 FS_TEX_COORD;
FS_VARYING float FS_V;

uniform sampler3D glyphBombingGlyphbombing3DTexture;

uniform bool glyphBombingFog;
uniform bool glyphBombingTexturing;
uniform float glyphBombingTimer;
uniform bool glyphBombingState;
uniform bool glyphBombingOldLCDStyleTransparency;
uniform bool glyphBombingSolidWireFrame;

const float PI = 3.14159;
const float TWO_PI = 2.0 * PI;
const float intensityLevel = 1.0;
const float intensityTransparencyLevel = 0.8 * intensityLevel;
const float extraLightIntensityFactor = 0.3;

const float ColumnAdjust = 0.0;
const float ScaleFactor = 2.0;
const float Percentage = 0.7;
const float SamplesPerCell = 1.0;
const float RO1 = 0.29;
const float RO2 = 0.79;
const bool RandomScale = false;
const float changeSpeedRatio = 500.0;

const float glyphBombing3DTextureRandomZCoord = 0.25;
const float glyphBombing3DTextureGlyphZCoord = glyphBombing3DTextureRandomZCoord + 0.5;

// animation related GPU Computing variables
uniform bool AnimationGPUComputingMode;
uniform bool ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION;


void applyOldStyleTransparency();
vec4 applyAnimationGPUComputing(in vec4);
vec4 applyADSLightingModel(in bool, in bool, in vec3, in vec3, in vec4);
void applyTexture(inout vec4, in vec2);
vec4 applyFog(in vec4);


void main()
{
    if (glyphBombingOldLCDStyleTransparency)
        applyOldStyleTransparency();

    vec4 glyphColor = (AnimationGPUComputingMode && ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION) ? applyAnimationGPUComputing(FS_SCENE_COLOR) : FS_SCENE_COLOR;
    vec2 scaledUV = ( FS_TEX_COORD * ScaleFactor + fract(glyphBombingTimer / 20.0) );
    vec2 cell = floor(scaledUV);
    vec2 offset = scaledUV - cell;

    for (int i = -1; i <= int(glyphBombingState); i++)
    {
        for (int j = -1; j <= int(glyphBombingState); j++)
        {
            vec2 currentCell = cell + vec2( float(i), float(j) );
            vec2 currentOffset = offset - vec2( float(i) + sin(glyphBombingTimer + PI / 2.0) / 30.0, float(j) + sin(glyphBombingTimer + PI / 4.0) / 30.0 );

            vec2 randomUV = currentCell * vec2(RO1, RO2);

            for (int k = 0; k < int(SamplesPerCell); k++)
            {
                randomUV += vec2(0.79, 0.388) * (sin(glyphBombingTimer + PI / 2.0) / changeSpeedRatio) ;
                vec4 random = texture3D( glyphBombingGlyphbombing3DTexture, vec3(randomUV, glyphBombing3DTextureRandomZCoord) );
                randomUV += random.ba;

                if (random.r < Percentage)
                {
                    vec2 glyphIndex;
                    mat2 rotator;
                    vec2 index;
                    float rotationAgle, cosRotation, sinRotation;

                    index.s = floor(10.0 * random.b);
                    index.t = floor(10.0 * ( ColumnAdjust + fract(glyphBombingTimer / 20.0) ) );

                    if (glyphBombingState)
                    {
                        rotationAgle = TWO_PI * random.g;
                        cosRotation = cos(rotationAgle);
                        sinRotation = sin(rotationAgle);
                        rotator[0] = vec2(cosRotation, sinRotation);
                        rotator[1] = vec2(-sinRotation, cosRotation);
                        glyphIndex = -rotator * (currentOffset - random.rg);
                    }
                    else
                        glyphIndex = currentOffset - random.rg;

                    if (RandomScale)
                        glyphIndex *= vec2(0.5 * random.r + 0.5);

                    glyphIndex = 0.1 * (clamp(glyphIndex, 0.0, 1.0) + index);

                    vec4 image = texture3D( glyphBombingGlyphbombing3DTexture, vec3(glyphIndex, glyphBombing3DTextureGlyphZCoord) );

                    if (image.r < 0.9)
                    // if (image.r != 1.0) // if not white color
                        glyphColor.rgb = mix(0.7 * random.rgb, glyphColor.rgb, image.r);
                }
            }
        }
    }

    vec4 sceneColorLocal = FS_SCENE_COLOR;
    float alpha = sceneColorLocal.a;
    sceneColorLocal *= intensityLevel;
    float lightIntensity = applyADSLightingModel(glyphBombingState, false, FS_NORMAL, FS_POSITION, sceneColorLocal).r;
    vec4 color = mix(glyphColor, sceneColorLocal, 0.5) * (lightIntensity + extraLightIntensityFactor);

    vec4 finalColor = min(vec4(color.rgb, 1.0), 1.0);
    if (alpha < 1.0)
        finalColor.a *= (alpha / intensityTransparencyLevel);

    // apply texturing if appropriate
    if (glyphBombingTexturing)
        applyTexture(finalColor, gl_TexCoord[0].st);

    // apply per-pixel fog if appriopriate
    gl_FragColor = (glyphBombingFog) ? applyFog(finalColor) : finalColor;
}