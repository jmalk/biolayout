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

uniform sampler3D hatchingPerlinNoise3DTexture;

uniform bool hatchingFog;
uniform bool hatchingTexturing;
uniform bool hatchingState;
uniform bool hatchingSphericalMapping;
uniform bool hatchingAntiAlias;
uniform bool hatchingOldLCDStyleTransparency;
uniform bool hatchingSolidWireFrame;

const float intensityLevel = 1.0;
const float intensityTransparencyLevel = 0.8 * intensityLevel;
const float extraLightIntensityFactor = 0.3;

const float ScaleStandard = 50.0;
const float ScaleSpherical = 1.0;
const float frequencyStandard = 5.0;
const float frequencySpherical = 2.5;
const float edgew = 0.5; // width of smoothstep

// animation related GPU Computing variables
uniform bool AnimationGPUComputingMode;
uniform bool ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION;


float smootherstep(in float, in float, in float);

void applyOldStyleTransparency();
vec4 applyAnimationGPUComputing(in vec4);
vec4 applyADSLightingModel(in bool, in bool, in vec3, in vec3, in vec4);
void applyTexture(inout vec4, in vec2);
vec4 applyFog(in vec4);


void main()
{
    if (hatchingOldLCDStyleTransparency)
        applyOldStyleTransparency();

    float dp = length( vec2( dFdx(FS_V), dFdy(FS_V) ) );
    float logdp = -log(8.0 * dp);
    float ilogdp = floor(logdp);
    float stripes = exp2(ilogdp);

    float noise = ( (hatchingSphericalMapping) ? ScaleSpherical : ScaleStandard ) * texture3D(hatchingPerlinNoise3DTexture, FS_MC_POSITION).x;
    float sawtooth = fract( (FS_V + 0.1 * noise) * ( (hatchingSphericalMapping) ? frequencySpherical : frequencyStandard ) * stripes );
    float triangle = abs(2.0 * sawtooth - 1.0);

    // adjust line width
    float transition = logdp - ilogdp;

    // taper ends
    triangle = abs( (1.0 + transition) * triangle - transition );

    // vec4 color = mix(LightHatchingColor, DarkHatchingColor, r);
    vec4 sceneColorLocal = (AnimationGPUComputingMode && ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION) ? applyAnimationGPUComputing(FS_SCENE_COLOR) : FS_SCENE_COLOR;
    float alpha = sceneColorLocal.a;
    sceneColorLocal *= intensityLevel;
    float lightIntensity = applyADSLightingModel(hatchingState, false, FS_NORMAL, FS_POSITION, sceneColorLocal).r;

    float edge1 = clamp(lightIntensity + extraLightIntensityFactor, 0.0, 1.0);
    float square = 0.0;
    if (hatchingAntiAlias)
    {
        float edge0 = clamp(lightIntensity + extraLightIntensityFactor - edgew, 0.0, 1.0);
        square = 1.0 - smootherstep(edge0, edge1, triangle);
    }
    else
    {
        square = 1.0 - step(edge1, triangle);
    }
    vec4 color = mix(vec4(vec3(square), 1.0), sceneColorLocal, 0.5);

    vec4 finalColor = min(vec4(color.rgb, 1.0), 1.0);
    if (alpha < 1.0)
        finalColor.a *= (alpha / intensityTransparencyLevel);

    // apply texturing if appropriate
    if (hatchingTexturing)
        applyTexture(finalColor, gl_TexCoord[0].st);

    // apply per-pixel fog if appriopriate
    gl_FragColor = (hatchingFog) ? applyFog(finalColor) : finalColor;
}