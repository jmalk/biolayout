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

uniform bool toonFog;
uniform bool toonTexturing;
uniform bool toonAntiAlias;
uniform bool toonState;
uniform bool toonOldLCDStyleTransparency;
uniform bool toonSolidWireFrame;

const float intensityLevel = 0.95;
const float intensityTransparencyLevel = 0.25 * intensityLevel;

// silhouette Color (default black)
const vec3 SILHOUETTE_COLOR = vec3(0.0, 0.0, 0.0);
const float SILHOUETTE_LOWER_BOUND_THRESHOLD = 0.25;
const float SILHOUETTE_UPPER_BOUND_THRESHOLD = 0.40;
const vec3 ZERO_POINT = vec3(0.0);

// animation related GPU Computing variables
uniform bool AnimationGPUComputingMode;
uniform bool ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION;


float smootherstep(in float, in float, in float);

void applyOldStyleTransparency();
vec4 applyAnimationGPUComputing(in vec4);
vec4 applyADSLightingModel(in bool, in bool, in vec3, in vec3, in vec4);
void applyTexture(inout vec4, in vec2);
float applyFogFactor();


void main()
{
    if (toonOldLCDStyleTransparency)
        applyOldStyleTransparency();

    float fogFactor = (toonFog) ? applyFogFactor() : 0.0;

    vec4 sceneColorLocal = (AnimationGPUComputingMode && ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION) ? applyAnimationGPUComputing(FS_SCENE_COLOR) : FS_SCENE_COLOR;
    float alpha = sceneColorLocal.a;
    sceneColorLocal.rgb *= intensityLevel;

    // Simple Silhouette
    vec3 N = normalize(FS_NORMAL);
    vec3 L = normalize(ZERO_POINT - FS_POSITION.xyz);
    float lambertTerm = max(dot(N, L), 0.0);
    if (lambertTerm < SILHOUETTE_LOWER_BOUND_THRESHOLD)
    {
        vec4 silhouetteColorToUse = vec4( ( (!toonState) ? sceneColorLocal.rgb : SILHOUETTE_COLOR ), alpha );
        gl_FragColor = (toonFog) ? mix(gl_Fog.color, silhouetteColorToUse, fogFactor) : silhouetteColorToUse;
    }
    else if (lambertTerm >= SILHOUETTE_LOWER_BOUND_THRESHOLD && lambertTerm < SILHOUETTE_UPPER_BOUND_THRESHOLD)
    {
        vec3 silhouetteColorToUse = (!toonState) ? sceneColorLocal.rgb : SILHOUETTE_COLOR;
        gl_FragColor = (toonFog) ? mix(gl_Fog.color, vec4(silhouetteColorToUse, alpha), fogFactor) : vec4(silhouetteColorToUse, alpha);

        vec4 finalColor = (toonFog) ? mix(gl_Fog.color, sceneColorLocal, fogFactor) : sceneColorLocal;
        if (alpha < 1.0)
            finalColor.a *= (alpha / intensityTransparencyLevel);

        // apply texturing if appropriate
        if (toonTexturing)
            applyTexture(finalColor, gl_TexCoord[0].st);

        vec3 L = normalize(gl_LightSource[0].position.xyz - FS_POSITION);
        vec3 H = normalize(L - FS_POSITION);

        // Specular part
        float specularPower = pow(max(dot(N, H), 0.0), gl_FrontMaterial.shininess);
        if (specularPower < 0.2)
            finalColor.rgb *= 0.8;

        // Diffuse part
        float diffuse = max(dot(N, L), 0.0);
        if (diffuse < 0.5)
            finalColor.rgb *= 0.8;

        gl_FragColor = mix(gl_FragColor, finalColor, (toonAntiAlias) ? smootherstep(SILHOUETTE_LOWER_BOUND_THRESHOLD, SILHOUETTE_UPPER_BOUND_THRESHOLD, lambertTerm) : lambertTerm);
    }
    else
    {
        vec4 finalColor = (toonFog) ? mix(gl_Fog.color, sceneColorLocal, fogFactor) : sceneColorLocal;
        if (alpha < 1.0)
            finalColor.a *= (alpha / intensityTransparencyLevel);

        // apply texturing if appropriate
        if (toonTexturing)
            applyTexture(finalColor, gl_TexCoord[0].st);

        vec3 L = normalize(gl_LightSource[0].position.xyz - FS_POSITION);
        vec3 H = normalize(L - FS_POSITION);

        // Specular part
        float specularPower = pow(max(dot(N, H), 0.0), gl_FrontMaterial.shininess);
        if (specularPower < 0.2)
            finalColor.rgb *= 0.8;

        // Diffuse part
        float diffuse = max(dot(N, L), 0.0);
        if (diffuse < 0.5)
            finalColor.rgb *= 0.8;

        gl_FragColor = finalColor;
    }
}