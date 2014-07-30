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

FS_VARYING vec3 FS_POSITION;
FS_VARYING vec3 FS_MC_POSITION;
FS_VARYING vec3 FS_NORMAL;
FS_VARYING vec4 FS_SCENE_COLOR;
FS_VARYING vec2 FS_TEX_COORD;
FS_VARYING float FS_V;

uniform sampler3D granitePerlinNoise3DTexture;

uniform bool graniteFog;
uniform bool graniteTexturing;
uniform bool graniteState;
uniform float graniteTimer;
uniform bool graniteOldLCDStyleTransparency;
uniform bool graniteErosion;
uniform bool graniteSolidWireFrame;

const float PI = 3.14159;
const float intensityLevel = 1.0;
const float intensityTransparencyLevel = 0.8 * intensityLevel;
const float extraLightIntensityFactor = 0.05;

const float NoiseScale = 50000.0;

// animation related GPU Computing variables
uniform bool AnimationGPUComputingMode;
uniform bool ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION;


void applyErosion(in vec4);

void applyOldStyleTransparency();
vec4 applyAnimationGPUComputing(in vec4);
vec4 applyADSLightingModel(in bool, in bool, in vec3, in vec3, in vec4);
void applyTexture(inout vec4, in vec2);
vec4 applyFog(in vec4);


void main()
{
    if (graniteOldLCDStyleTransparency)
        applyOldStyleTransparency();

    vec3 MCpositionDistorted = NoiseScale * FS_MC_POSITION;
    MCpositionDistorted.x += sin(graniteTimer);
    MCpositionDistorted.y += sin(graniteTimer + PI / 2.0);
    MCpositionDistorted.z += sin(graniteTimer + PI / 4.0);

    vec4 noisevec  = texture3D(granitePerlinNoise3DTexture, MCpositionDistorted);
    if (graniteErosion)
        applyErosion(noisevec);
    float intensity = min(1.0, noisevec[3] * 18.0);

    vec4 sceneColorLocal = (AnimationGPUComputingMode && ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION) ? applyAnimationGPUComputing(FS_SCENE_COLOR) : FS_SCENE_COLOR;
    float alpha = sceneColorLocal.a;
    sceneColorLocal *= intensityLevel;
    float lightIntensity = applyADSLightingModel(graniteState, false, FS_NORMAL, FS_POSITION, sceneColorLocal).r;
    vec4 color = vec4( intensity * (lightIntensity + extraLightIntensityFactor) );
    color = clamp(color, 0.0, 1.0);
    vec4 finalColor = min(vec4(mix(color, sceneColorLocal, 0.5).rgb, 1.0), 1.0);
    if (alpha < 1.0)
        finalColor.a *= (alpha / intensityTransparencyLevel);

    // apply texturing if appropriate
    if (graniteTexturing)
        applyTexture(finalColor, gl_TexCoord[0].st);

    // apply per-pixel fog if appriopriate
    gl_FragColor = (graniteFog) ? applyFog(finalColor) : finalColor;
}