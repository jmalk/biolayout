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

uniform sampler2D bumpEmboss2DTexture;

FS_VARYING vec3 FS_POSITION;
FS_VARYING vec3 FS_MC_POSITION;
FS_VARYING vec3 FS_NORMAL;
FS_VARYING vec4 FS_SCENE_COLOR;
FS_VARYING vec2 FS_TEX_COORD;
FS_VARYING float FS_V;

uniform bool bumpFog;
uniform bool bumpTexturing;
uniform float bumpTimer;
uniform bool bumpState;
uniform bool bumpOldLCDStyleTransparency;
uniform float bumpPreCalcEffects2DTexturePx;
uniform float bumpPreCalcEffects2DTexturePy;
uniform bool bumpSolidWireFrame;

const float intensityLevel = 0.5;
const float intensityTransparencyLevel = 0.5 * intensityLevel;

const float EMBOSS_TEXTURE_SIZE = 48.0;
const vec4 EMBOSS_TEXTURE_LIGHT_SPECULAR_COLOR = vec4(1.0, 1.0, 1.0, 1.0); // white specular color
const vec2 EMBOSS_TEXTURE_CENTER_POINT = vec2(0.5, 0.5);
const vec2 EMBOSS_TEXTURE_BOTTOM_RIGHT_POINT = vec2(0.0, 0.0);

const float RADIUS_DIVISOR_FACTOR = 100000.0;
const float TWO_PI = 2.0 * 3.14159265;
const vec3 RIPPLE_C0 = vec3(-2.5, 0.0, 0.0);
const vec3 RIPPLE_C1 = vec3( 2.5, 0.0, 0.0);
const float Amp0 = 1.0;
const float Amp1 = 1.0;
const float PhaseShift = TWO_PI / 2.0;
const float Pd = 2.0;

// animation related GPU Computing variables
uniform bool AnimationGPUComputingMode;
uniform bool ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION;


void applyEmbossTexture(inout vec4, in vec4);
vec3 applyRipples();

void applyOldStyleTransparency();
vec4 applyAnimationGPUComputing(in vec4);
vec4 applyADSLightingModel(in bool, in bool, in vec3, in vec3, in vec4);
void applyTexture(inout vec4, in vec2);
vec4 applyFog(in vec4);


void main()
{
    if (bumpOldLCDStyleTransparency)
        applyOldStyleTransparency();

    vec4 sceneColorLocal = (AnimationGPUComputingMode && ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION) ? applyAnimationGPUComputing(FS_SCENE_COLOR) : FS_SCENE_COLOR;
    float alpha = sceneColorLocal.a;
    sceneColorLocal.rgb *= intensityLevel;
    vec4 finalColor = applyADSLightingModel(bumpState, true, (bumpState) ? applyRipples() : FS_NORMAL, FS_POSITION, sceneColorLocal);
    if (alpha < 1.0)
        finalColor.a *= (alpha / intensityTransparencyLevel);

    // note: always apply the emboss 2d texture in the fragment shader
    applyEmbossTexture(finalColor, sceneColorLocal);

    // apply texturing if appropriate
    if (bumpTexturing)
        applyTexture(finalColor, gl_TexCoord[0].st);

    // apply per-pixel fog if appriopriate
    gl_FragColor = (bumpFog) ? applyFog(finalColor) : finalColor;
}

void applyEmbossTexture(inout vec4 finalColor, in vec4 sceneColorLocal)
{
    vec2 tcoords = gl_TexCoord[0].st;
    vec2 tcoordsLightSpecular = 128.0 * vec2(tcoords - EMBOSS_TEXTURE_CENTER_POINT);

    vec4 bumpEmboss2DTextureColor = vec4(sceneColorLocal.rgb, texture2D(bumpEmboss2DTexture, tcoords).a);
    bumpEmboss2DTextureColor.rgb *= ( 0.5 + 0.45 * sin(bumpTimer) );

    float neighborFragmentX = (1.0 / bumpPreCalcEffects2DTexturePx);
    float neighborFragmentY = (1.0 / bumpPreCalcEffects2DTexturePy);

    tcoords.s -= neighborFragmentX;
    float neighborLeft =  texture2D(bumpEmboss2DTexture, tcoords).a;

    tcoords.s += 2.0 * neighborFragmentX;
    float neighborRight = texture2D(bumpEmboss2DTexture, tcoords).a;

    tcoords.s -= neighborFragmentX;
    tcoords.t += neighborFragmentY;
    float neighborUp = texture2D(bumpEmboss2DTexture, tcoords).a;

    tcoords.t -= 2.0 * neighborFragmentY;
    float neighborDown = texture2D(bumpEmboss2DTexture, tcoords).a;

    float dx = 0.75 * (neighborRight - neighborLeft);
    float dy = 0.75 * (neighborUp    - neighborDown);

    float lightSpecular = EMBOSS_TEXTURE_SIZE / ( (bumpState) ? distance(vec2( (bumpPreCalcEffects2DTexturePx / bumpPreCalcEffects2DTexturePy) * ( tcoordsLightSpecular.s + 48.0 * (sin(bumpTimer) + dx) ), tcoordsLightSpecular.t + 48.0 * (cos(bumpTimer) + dy) ), EMBOSS_TEXTURE_BOTTOM_RIGHT_POINT)
                                                              : distance(vec2( (bumpPreCalcEffects2DTexturePx / bumpPreCalcEffects2DTexturePy) * ( gl_LightSource[0].position.x + 48.0 * (sin(bumpTimer) + dx) ), gl_LightSource[0].position.y + 48.0 * (cos(bumpTimer) + dy) ), EMBOSS_TEXTURE_BOTTOM_RIGHT_POINT) );

    finalColor.rgb *= mix(bumpEmboss2DTextureColor, EMBOSS_TEXTURE_LIGHT_SPECULAR_COLOR, lightSpecular).rgb;
}

vec3 applyRipples()
{
    // first set of ripples
    float radius1 = length(FS_MC_POSITION - RIPPLE_C0) / RADIUS_DIVISOR_FACTOR; // ripple center 0
    // float heightRipple1 = -Amp0 * cos(TWO_PI * radius1 / Pd - TWO_PI * bumpTimer);

    vec3 normalRipple1 = vec3(-Amp0 * (TWO_PI / Pd) * sin(TWO_PI * radius1 / Pd - TWO_PI * bumpTimer / 10.0),
                              0.0,
                              1.0);
    // float angle1 = atan(FS_MC_POSITION.y - RIPPLE_C0.y, FS_MC_POSITION.x - RIPPLE_C0.x);
    // vec3 rotatedNormalRipple1 = vec3( dot( normalRipple1.xy, vec2( cos(angle1), -sin(angle1) ) ),
    //                                   dot( normalRipple1.xy, vec2( sin(angle1),  cos(angle1) ) ),
    //                                   1.0);
    vec2 cossin1 = normalize(FS_MC_POSITION.xy - RIPPLE_C0.xy);
    vec3 rotatedNormalRipple1 = vec3( dot( normalRipple1.xy, vec2(cossin1.x, -cossin1.y) ),
                                      dot( normalRipple1.xy, cossin1.yx ),
                                      1.0);

    // second set of ripples
    float radius2 = length(FS_MC_POSITION - RIPPLE_C1) / RADIUS_DIVISOR_FACTOR; // ripple center 1
    // float heightRipple2 = -Amp1 * cos(TWO_PI * radius2 / Pd - TWO_PI * bumpTimer / 5.0);

    vec3 normalRipple2 = vec3(-Amp1 * (TWO_PI / Pd) * sin(TWO_PI * radius2 / Pd - TWO_PI * bumpTimer - PhaseShift),
                              0.0,
                              1.0);
    // float angle2 = atan(FS_MC_POSITION.y - RIPPLE_C1.y, FS_MC_POSITION.x - RIPPLE_C1.x);
    // vec3 rotatedNormalRipple2 = vec3( dot( normalRipple2.xy, vec2( cos(angle2), -sin(angle2) ) ),
    //                                   dot( normalRipple2.xy, vec2( sin(angle2),  cos(angle2) ) ),
    //                                   1.0);
    vec2 cossin2 = normalize(FS_MC_POSITION.xy - RIPPLE_C1.xy);
    vec3 rotatedNormalRipple2 = vec3( dot( normalRipple2.xy, vec2(cossin2.x, -cossin2.y) ),
                                      dot( normalRipple2.xy, cossin2.yx ),
                                      1.0);

    // the sum is the FS_NORMAL
    return mix(normalize(rotatedNormalRipple1 + rotatedNormalRipple2), FS_NORMAL, 0.5);
}