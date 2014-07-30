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

#if ANIMATION_COMPATIBILITY_CONDITION
    // animation related GPU Computing variables
    uniform sampler2D Animation2DTexture;
    uniform bool ProcessNextNodeValue;
    uniform bool ANIMATION_FLUID_LINEAR_TRANSITION;
    uniform bool ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION;
    uniform vec3 ANIMATION_MAX_SPECTRUM_COLOR;
    uniform bool ANIMATION_USE_IMAGE_AS_SPECTRUM;
    FS_VARYING float nodeValueRatio;
    FS_VARYING float nodeRealValueRatio;
    FS_VARYING float nextNodeValueRatio;
    FS_VARYING float nextNodeRealValueRatio;
    FS_VARYING float percentageBetweenTicks;
#endif

vec4 applyAnimationGPUComputing(in vec4);

vec4 applyAnimationGPUComputing(in vec4 sceneColor)
{
    #if ANIMATION_COMPATIBILITY_CONDITION
        vec4 color = sceneColor; // sceneColor is a FS_VARYING variable in main(), has to be done this way as in fragment shaders FS_VARYING variables are read-only in GLSL, cannot be used for input here
        if (!ANIMATION_USE_IMAGE_AS_SPECTRUM)
        {
            if (!ANIMATION_FLUID_LINEAR_TRANSITION || !ProcessNextNodeValue) // no interpolation for in-between color steps, default case
                color.rgb = mix(color.rgb, ANIMATION_MAX_SPECTRUM_COLOR, (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) ? nodeRealValueRatio : nodeValueRatio);
            else
            {
                // interpolated color step
                // note: no need to re-calc the percentageBetweenTicks from smoothstep, its value is available from above for the ANIMATION_FLUID_POLYNOMIAL_TRANSITION case
                color.rgb = mix( color.rgb, ANIMATION_MAX_SPECTRUM_COLOR, mix( (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) ? nodeRealValueRatio : nodeValueRatio, (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) ? nextNodeRealValueRatio : nextNodeValueRatio, percentageBetweenTicks ) );
            }
        }
        else
        {
            if (!ANIMATION_FLUID_LINEAR_TRANSITION || !ProcessNextNodeValue) // no interpolation for in-between color steps, default case
                color.rgb = texture2D( Animation2DTexture, vec2( (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) ? nodeRealValueRatio : nodeValueRatio, 0.5 ) ).rgb;
            else
            {
                // interpolated color step
                // note: no need to re-calc the percentageBetweenTicks from smoothstep, its value is available from above for the ANIMATION_FLUID_POLYNOMIAL_TRANSITION case
                color.rgb = texture2D( Animation2DTexture, vec2(mix( (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) ? nodeRealValueRatio : nodeValueRatio, (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) ? nextNodeRealValueRatio : nextNodeValueRatio, percentageBetweenTicks ), 0.5) ).rgb;
            }
        }

        return color;
    #else    
        return sceneColor;
    #endif
}