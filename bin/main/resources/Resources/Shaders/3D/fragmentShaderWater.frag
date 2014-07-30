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

uniform sampler2D texture;
uniform sampler2D waterBuffer2DTexture;

uniform bool waterFog;
uniform bool waterTexturing;
uniform float waterTimer;
uniform bool waterState;
uniform bool waterOldLCDStyleTransparency;
uniform float waterPreCalcEffects2DTexturePx;
uniform float waterPreCalcEffects2DTexturePy;
uniform bool waterSolidWireFrame;

const float intensityLevel = 0.5;
const float intensityTransparencyLevel = 1.8 * intensityLevel;

const float DAMPING = 0.975;
const float WATER_TEXTURE_SIZE = 48.0;
const vec2 WATER_TEXTURE_CENTER_POINT = vec2(0.5, 0.5);
const vec2 WATER_TEXTURE_BOTTOM_RIGHT_POINT = vec2(0.0, 0.0);

// animation related GPU Computing variables
uniform bool AnimationGPUComputingMode;
uniform bool ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION;


void applyWaterTexture(inout vec4);

void applyOldStyleTransparency();
vec4 applyAnimationGPUComputing(in vec4);
vec4 applyADSLightingModel(in bool, in bool, in vec3, in vec3, in vec4);
void applyTexture(inout vec4, in vec2);
vec4 applyFog(in vec4);


void main()
{
    if (waterOldLCDStyleTransparency)
        applyOldStyleTransparency();

    vec4 sceneColorLocal = (AnimationGPUComputingMode && ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION) ? applyAnimationGPUComputing(FS_SCENE_COLOR) : FS_SCENE_COLOR;
    float alpha = sceneColorLocal.a;
    sceneColorLocal.rgb *= intensityLevel;
    vec4 finalColor = applyADSLightingModel(waterState, true, FS_NORMAL, FS_POSITION, sceneColorLocal);
    if (alpha < 1.0)
        finalColor.a *= (alpha / intensityTransparencyLevel);

    // note: always apply the water 2d texture in the fragment shader
    applyWaterTexture(finalColor);

    // apply texturing if appropriate
    if (waterTexturing)
        applyTexture(finalColor, gl_TexCoord[0].st);

    // apply per-pixel fog if appriopriate
    gl_FragColor = (waterFog) ? applyFog(finalColor) : finalColor;
}

void applyWaterTexture(inout vec4 finalColor)
{
    vec2 tcoords = gl_TexCoord[0].st;
    vec2 tcoordsLightSpecular = 128.0 * vec2(tcoords - WATER_TEXTURE_CENTER_POINT);

    float dx = (1.0 / waterPreCalcEffects2DTexturePx);
    float dy = (1.0 / waterPreCalcEffects2DTexturePy);

    tcoords.s = tcoords.s - dx;
    vec4 heightLeft = texture2D(waterBuffer2DTexture, tcoords);

    tcoords.s = tcoords.s + 2.0 * dx;
    vec4 heightRight = texture2D(waterBuffer2DTexture, tcoords);

    tcoords.s = tcoords.s - dx;
    tcoords.t = tcoords.t + dy;
    vec4 heightUp = texture2D(waterBuffer2DTexture, tcoords);

    tcoords.t = tcoords.t - 2.0 * dy;
    vec4 heightDown = texture2D(waterBuffer2DTexture, tcoords);

    tcoords.t = tcoords.t + dy;

    float dispx = 0.125 * (heightLeft.a - heightRight.a);
    float dispy = 0.125 * (heightUp.a   - heightDown.a);

    tcoords.s = tcoords.s + dispx;
    tcoords.t = tcoords.t + dispy;

    float lightSpecular = WATER_TEXTURE_SIZE / ( (waterState) ? distance(vec2( (waterPreCalcEffects2DTexturePx / waterPreCalcEffects2DTexturePy) * ( tcoordsLightSpecular.s + 48.0 * (sin(waterTimer) + 6.0 * dispx) ), tcoordsLightSpecular.t + 48.0 * (cos(waterTimer) + 6.0 * dispy) ), WATER_TEXTURE_BOTTOM_RIGHT_POINT)
                                                              : distance(vec2( (waterPreCalcEffects2DTexturePx / waterPreCalcEffects2DTexturePy) * ( gl_LightSource[0].position.x + 48.0 * (sin(waterTimer) + 6.0 * dispx) ), gl_LightSource[0].position.y + 48.0 * (cos(waterTimer) + 6.0 * dispy) ), WATER_TEXTURE_BOTTOM_RIGHT_POINT) );

    vec4 color = (waterTexturing) ? texture2D(texture, tcoords) : vec4(1.0, 1.0, 1.0, 1.0);
    color.rgb *= lightSpecular;

    finalColor *= color;
}