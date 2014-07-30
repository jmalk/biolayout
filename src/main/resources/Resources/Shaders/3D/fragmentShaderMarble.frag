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

uniform sampler3D marblePerlinNoise3DTexture;

uniform bool marbleFog;
uniform bool marbleTexturing;
uniform bool marbleState;
uniform float marbleTimer;
uniform bool marbleOldLCDStyleTransparency;
uniform bool marbleErosion;
uniform bool marbleSolidWireFrame;

const float PI = 3.14159;
const float intensityLevel = 1.0;
const float intensityTransparencyLevel = 0.8 * intensityLevel;
const float extraLightIntensityFactor = 0.35;

const vec4 MarbleColor = vec4(0.8, 0.9, 0.85, 1.0);
const vec4 VeinColor = vec4(0.1, 0.1, 0.2, 1.0);

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
    if (marbleOldLCDStyleTransparency)
        applyOldStyleTransparency();

    vec3 MCpositionDistorted = FS_MC_POSITION;
    MCpositionDistorted.x += sin(marbleTimer);
    MCpositionDistorted.y += sin(marbleTimer + PI / 2.0);
    MCpositionDistorted.z += sin(marbleTimer + PI / 4.0);

    vec4 noisevec = texture3D(marblePerlinNoise3DTexture, MCpositionDistorted);
    if (marbleErosion)
        applyErosion(noisevec);
    float intensity = abs(noisevec[0] - 0.25) +
                      abs(noisevec[1] - 0.125) +
                      abs(noisevec[2] - 0.0625) +
                      abs(noisevec[3] - 0.03125);
    float sineval = sin(FS_MC_POSITION.y * 6.0 + intensity * 12.0) * 0.5 + 0.5;

    // vec4 color = mix(VeinColor, MarbleColor, sineval) * lightIntensity;
    vec4 sceneColorLocal = (AnimationGPUComputingMode && ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION) ? applyAnimationGPUComputing(FS_SCENE_COLOR) : FS_SCENE_COLOR;
    float alpha = sceneColorLocal.a;
    sceneColorLocal *= intensityLevel;
    float lightIntensity = applyADSLightingModel(marbleState, false, FS_NORMAL, FS_POSITION, sceneColorLocal).r;
    vec4 color = mix(mix(VeinColor, sceneColorLocal, 0.75), mix(MarbleColor, sceneColorLocal, 0.75), sineval) * (lightIntensity + extraLightIntensityFactor);

    vec4 finalColor = min(vec4(color.rgb, 1.0), 1.0);
    if (alpha < 1.0)
        finalColor.a *= (alpha / intensityTransparencyLevel);

    // apply texturing if appropriate
    if (marbleTexturing)
        applyTexture(finalColor, gl_TexCoord[0].st);

    // apply per-pixel fog if appriopriate
    gl_FragColor = (marbleFog) ? applyFog(finalColor) : finalColor;
}