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

uniform bool fractalFog;
uniform bool fractalTexturing;
uniform float fractalTimer;
uniform bool fractalState;
uniform bool fractalOldLCDStyleTransparency;
uniform bool fractalSolidWireFrame;

const float intensityLevel = 0.5;
const float intensityTransparencyLevel = 1.8 * intensityLevel;

#if GPU_SHADER_FP64_COMPATIBILITY_CONDITION

    #extension GL_ARB_gpu_shader_fp64: enable

    const double MAX_ITERATIONS = double(64.0);
    const double Xcenter = double(-1.36);
    const double Ycenter = double(0.005);
    const double XJulia = double(-0.765);
    const double YJulia = double(0.11);
#else
    const float MAX_ITERATIONS = 64.0;
    const float Xcenter = -1.36;
    const float Ycenter = 0.005;
    const float XJulia = -0.765;
    const float YJulia = 0.11;
#endif
const vec3 InnerColor = vec3(0.0, 0.0, 0.0);
const vec3 OuterColor = vec3(0.0, 1.0, 0.0); // green color

// animation related GPU Computing variables
uniform bool AnimationGPUComputingMode;
uniform bool ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION;


float smootherstep(in float, in float, in float);

void applyFractalTexture(inout vec4);
void applyOldStyleTransparency();
vec4 applyAnimationGPUComputing(in vec4);
vec4 applyADSLightingModel(in bool, in bool, in vec3, in vec3, in vec4);
void applyTexture(inout vec4, in vec2);
vec4 applyFog(in vec4);


void main()
{
    if (fractalOldLCDStyleTransparency)
        applyOldStyleTransparency();

    vec4 sceneColorLocal = (AnimationGPUComputingMode && ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION) ? applyAnimationGPUComputing(FS_SCENE_COLOR) : FS_SCENE_COLOR;
    float alpha = sceneColorLocal.a;
    sceneColorLocal.rgb *= intensityLevel;
    vec4 finalColor = applyADSLightingModel(fractalState, true, FS_NORMAL, FS_POSITION, sceneColorLocal);
    if (alpha < 1.0)
        finalColor.a *= (alpha / intensityTransparencyLevel);

    // note: always apply the fractal 2d texture in the fragment shader
    applyFractalTexture(finalColor);

    // apply texturing if appropriate
    if (fractalTexturing)
        applyTexture(finalColor, gl_TexCoord[0].st);

    // apply per-pixel fog if appriopriate
    gl_FragColor = (fractalFog) ? applyFog(finalColor) : finalColor;
}

void applyFractalTexture(inout vec4 finalColor)
{
    #if GPU_SHADER_FP64_COMPATIBILITY_CONDITION
        dvec2 Position = double(5.0) * dvec2( dvec2(gl_TexCoord[0]) - double(0.5) );
        Position = Position * ( smootherstep(0.0, 1.0, sin(fractalTimer + 1.0) ) + ( (!fractalState) ? double(0.015) : double(0.003) ) ) + dvec2(Xcenter, Ycenter);
        double real = Position.x;
        double imag = Position.y;
        double Creal = (!fractalState) ? real : XJulia; // for a Julia fractal set
        double Cimag = (!fractalState) ? imag : YJulia; // for a Julia fractal set

        double rSquared = double(0.0);
        double iterations, tempReal;
        for (iterations = double(0.0); iterations < MAX_ITERATIONS && rSquared < double(4.0); iterations++)
        {
           tempReal = real;
           real = (tempReal * tempReal) - (imag * imag) + Creal;
           imag = double(2.0) * tempReal * imag + Cimag;
           rSquared = (real * real) + (imag * imag);
        }

        finalColor *= ( ( rSquared < double(4.0) ) ? vec4(InnerColor, 1.0) : vec4(mix( finalColor.rgb, OuterColor, fract(0.05 * iterations) ), 1.0) );
    #else
        vec2 Position = 5.0 * vec2(gl_TexCoord[0] - 0.5);
        Position = Position * ( smootherstep(0.0, 1.0, sin(fractalTimer + 1.0) ) + ( (!fractalState) ? 0.015 : 0.003 ) ) + vec2(Xcenter, Ycenter);
        float real = Position.x;
        float imag = Position.y;
        float Creal = (!fractalState) ? real : XJulia; // for a Julia fractal set
        float Cimag = (!fractalState) ? imag : YJulia; // for a Julia fractal set

        float rSquared = 0.0;
        float iterations, tempReal;
        for (iterations = 0.0; iterations < MAX_ITERATIONS && rSquared < 4.0; iterations++)
        {
           tempReal = real;
           real = (tempReal * tempReal) - (imag * imag) + Creal;
           imag = 2.0 * tempReal * imag + Cimag;
           rSquared = (real * real) + (imag * imag);
        }

        finalColor *= ( (rSquared < 4.0) ? vec4(InnerColor, 1.0) : vec4(mix( finalColor.rgb, OuterColor, fract(0.05 * iterations) ), 1.0) );
    #endif
}