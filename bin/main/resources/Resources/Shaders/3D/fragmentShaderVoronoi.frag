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

uniform bool voronoiFog;
uniform bool voronoiTexturing;
uniform float voronoiTimer;
uniform bool voronoiState;
uniform bool voronoiOldLCDStyleTransparency;
uniform bool voronoiSolidWireFrame;

const float intensityLevel = 0.5;
const float intensityTransparencyLevel = 1.8 * intensityLevel;

const vec2 VORONOI_TEXTURE_CENTER_POINT = vec2(0.5, 0.5);
const int VORONOI_N = 32;

// animation related GPU Computing variables
uniform bool AnimationGPUComputingMode;
uniform bool ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION;


void applyVoronoiTexture(inout vec4);

void applyOldStyleTransparency();
vec4 applyAnimationGPUComputing(in vec4);
vec4 applyADSLightingModel(in bool, in bool, in vec3, in vec3, in vec4);
void applyTexture(inout vec4, in vec2);
vec4 applyFog(in vec4);


void main()
{
    if (voronoiOldLCDStyleTransparency)
        applyOldStyleTransparency();

    vec4 sceneColorLocal = (AnimationGPUComputingMode && ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION) ? applyAnimationGPUComputing(FS_SCENE_COLOR) : FS_SCENE_COLOR;
    float alpha = sceneColorLocal.a;
    sceneColorLocal.rgb *= intensityLevel;
    vec4 finalColor = applyADSLightingModel(voronoiState, true, FS_NORMAL, FS_POSITION, sceneColorLocal);
    if (alpha < 1.0)
        finalColor.a *= (alpha / intensityTransparencyLevel);

    // note: always apply the voronoi 2d texture in the fragment shader
    applyVoronoiTexture(finalColor);

    // apply texturing if appropriate
    if (voronoiTexturing)
        applyTexture(finalColor, gl_TexCoord[0].st);

    // apply per-pixel fog if appriopriate
    gl_FragColor = (voronoiFog) ? applyFog(finalColor) : finalColor;
}

void applyVoronoiTexture(inout vec4 finalColor)
{
    vec2 coord = vec2( 2.0 * (gl_TexCoord[0].st - VORONOI_TEXTURE_CENTER_POINT) );
    float ii = 0.0;
    float dist = 0.0;

    if (voronoiState) // process voronoi blob effect
    {
	for (int i = 0; i < VORONOI_N; i++)
	{
            ii = float(i);
            dist += 1.0 / pow( distance( coord, vec2( sin(ii * ii + voronoiTimer), sin(ii * ii * ii + voronoiTimer) ) ), 2.0);
	}

        finalColor *= vec4(vec3(0.01 * dist), 1.0);
    }
    else // process standard voronoi effect
    {
        float minimum = 256.0;
        for (int i = 0; i < VORONOI_N; i++)
        {
            ii = float(i);
            dist = distance( coord, vec2( sin(ii * ii + voronoiTimer), sin(ii * ii * ii + voronoiTimer) ) );
            minimum = min(minimum, dist);
        }

        finalColor *= vec4(vec3(3.0 * minimum), 1.0);
    }
}