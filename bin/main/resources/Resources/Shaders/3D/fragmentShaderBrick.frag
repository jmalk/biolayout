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

uniform bool brickFog;
uniform bool brickTexturing;
uniform bool brickState;
uniform bool brickAntiAlias;
uniform bool brickOldLCDStyleTransparency;
uniform bool brickSolidWireFrame;

const float intensityLevel = 1.0;
const float intensityTransparencyLevel = 0.8 * intensityLevel;
const float extraLightIntensityFactor = 0.35;

const vec4 BrickColor = vec4(1.0, 0.3, 0.2, 1.0);
const vec4 MortarColor = vec4(0.85, 0.86, 0.84, 1.0);
const vec2 BrickSize = vec2(0.3, 0.15);
const vec2 BrickPct = vec2(0.9, 0.85);
const vec2 MortarPct = vec2(1.0 - BrickPct.x, 1.0 - BrickPct.y);
const vec2 Delta = vec2(0.05, 0.05);
const vec2 ZeroVec2 = vec2(0.0, 0.0);

// animation related GPU Computing variables
uniform bool AnimationGPUComputingMode;
uniform bool ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION;


vec2 smootherstep(in vec2, in vec2, in vec2);

void applyOldStyleTransparency();
vec4 applyAnimationGPUComputing(in vec4);
vec4 applyADSLightingModel(in bool, in bool, in vec3, in vec3, in vec4);
void applyTexture(inout vec4, in vec2);
vec4 applyFog(in vec4);


void main()
{
    if (brickOldLCDStyleTransparency)
        applyOldStyleTransparency();

    vec2 positionBrick = FS_MC_POSITION.xy / BrickSize;

    if (fract(positionBrick.y * 0.5) > 0.5)
        positionBrick.x += 0.5;

    vec2 useBrick;
    if (brickAntiAlias)
    {
        positionBrick = fract(positionBrick);
        useBrick =   smootherstep(positionBrick - Delta, positionBrick, BrickPct)  // antialias left and bottom
                   - smootherstep(positionBrick - Delta, positionBrick, ZeroVec2); // antialias right and top edges
    }
    else
    {
        positionBrick = fract(positionBrick);
        useBrick = step(positionBrick, BrickPct);
    }

    // vec3 colorBrick = mix(MortarColor, BrickColor, useBrick.x * useBrick.y) * lightIntensity;
    vec4 sceneColorLocal = (AnimationGPUComputingMode && ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION) ? applyAnimationGPUComputing(FS_SCENE_COLOR) : FS_SCENE_COLOR;
    float alpha = sceneColorLocal.a;
    sceneColorLocal *= intensityLevel;
    vec4 lightIntensity = applyADSLightingModel(brickState, false, FS_NORMAL, FS_POSITION, sceneColorLocal);
    vec4 color = mix(mix(MortarColor, sceneColorLocal, 0.5), mix(BrickColor, sceneColorLocal, 0.5), useBrick.x * useBrick.y) * (lightIntensity + extraLightIntensityFactor);

    vec4 finalColor = min(vec4(color.rgb, 1.0), 1.0);
    if (alpha < 1.0)
        finalColor.a *= (alpha / intensityTransparencyLevel);

    // apply texturing if appropriate
    if (brickTexturing)
        applyTexture(finalColor, gl_TexCoord[0].st);

    // apply per-pixel fog if appriopriate
    gl_FragColor = (brickFog) ? applyFog(finalColor) : finalColor;
}