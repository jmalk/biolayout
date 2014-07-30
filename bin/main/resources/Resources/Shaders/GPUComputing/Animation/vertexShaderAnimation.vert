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

// animation related GPU Computing variables
uniform float NodeValue;
uniform bool ProcessNextNodeValue;
uniform float NextNodeValue;
uniform int AnimationFrameCount;
uniform int FRAMERATE_PER_SECOND_FOR_ANIMATION;
uniform bool ANIMATION_FLUID_LINEAR_TRANSITION;
uniform bool ANIMATION_FLUID_POLYNOMIAL_TRANSITION;
uniform float ANIMATION_TICKS_PER_SECOND;
uniform int ANIMATION_MAX_NODE_SIZE;
uniform float ANIMATION_RESULTS_MAX_VALUE;
uniform float ANIMATION_RESULTS_REAL_MAX_VALUE;
uniform bool ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION;
#if ANIMATION_COMPATIBILITY_CONDITION
    VS_VARYING float nodeValueRatio;
    VS_VARYING float nodeRealValueRatio;
    VS_VARYING float nextNodeValueRatio;
    VS_VARYING float nextNodeRealValueRatio;
    VS_VARYING float percentageBetweenTicks;
#else
    uniform bool ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION;
    uniform vec3 ANIMATION_MAX_SPECTRUM_COLOR;
    uniform sampler2D Animation2DTexture;
    uniform bool ANIMATION_USE_IMAGE_AS_SPECTRUM;
#endif
float animationNodeSizeRatio = 1.0;
vec4 color;

float smootherstep(in float, in float, in float);
vec4 applyAnimationGPUComputing(in vec4);

// Ken Perlin suggests an improved version of the smoothstep function which has zero 1st and 2nd order derivatives at t=0 and t=1
float smootherstep(in float edge0, in float edge1, in float x)
{
    // Scale, and clamp x to 0...1 range
    x = clamp( (x - edge0) / (edge1 - edge0), 0.0, 1.0 );
    // Evaluate polynomial
    return x * x * x * ( x * (x * 6.0 - 15.0) + 10.0 );
}

vec4 applyAnimationGPUComputing(in vec4 sceneColor)
{
    #if ANIMATION_COMPATIBILITY_CONDITION
        // do nothing
    #else
        float nodeRealValueRatio = 0.0;
        float nodeValueRatio = 0.0;
        float nextNodeRealValueRatio = 0.0;
        float nextNodeValueRatio = 0.0;
        float percentageBetweenTicks = 0.0;
    #endif

    if (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) nodeRealValueRatio = clamp(NodeValue / ANIMATION_RESULTS_REAL_MAX_VALUE, 0.0, 1.0);
    float NodeValue = (NodeValue > ANIMATION_RESULTS_MAX_VALUE) ? ANIMATION_RESULTS_MAX_VALUE : NodeValue;
    nodeValueRatio = clamp(NodeValue / ANIMATION_RESULTS_MAX_VALUE, 0.0 , 1.0);
    animationNodeSizeRatio = nodeValueRatio * float(ANIMATION_MAX_NODE_SIZE);
    
    if (ProcessNextNodeValue)
    {
        if (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) nextNodeRealValueRatio = clamp(NextNodeValue / ANIMATION_RESULTS_REAL_MAX_VALUE, 0.0, 1.0);
        float nextSPNNodeValue = (NextNodeValue > ANIMATION_RESULTS_MAX_VALUE) ? ANIMATION_RESULTS_MAX_VALUE : NextNodeValue;
        nextNodeValueRatio = clamp(nextSPNNodeValue / ANIMATION_RESULTS_MAX_VALUE, 0.0, 1.0);

        float currentFrame = ( mod( float(AnimationFrameCount), float(FRAMERATE_PER_SECOND_FOR_ANIMATION) / float(ANIMATION_TICKS_PER_SECOND) ) ) * float(ANIMATION_TICKS_PER_SECOND);
        percentageBetweenTicks = currentFrame / float(FRAMERATE_PER_SECOND_FOR_ANIMATION);

        if (ANIMATION_FLUID_POLYNOMIAL_TRANSITION)
            percentageBetweenTicks = smootherstep(0.0, 1.0, percentageBetweenTicks);
        animationNodeSizeRatio = mix(animationNodeSizeRatio, nextNodeValueRatio * float(ANIMATION_MAX_NODE_SIZE), percentageBetweenTicks);
    }
    #if ANIMATION_COMPATIBILITY_CONDITION
        else
        {
            nextNodeValueRatio = 0.0;
            percentageBetweenTicks = 0.0;
        }
    #endif
    
    color = sceneColor;

    #if ANIMATION_COMPATIBILITY_CONDITION
        // do nothing
    #else
        if (ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION)
        {
            if (!ANIMATION_USE_IMAGE_AS_SPECTRUM)
            {
                if (!ANIMATION_FLUID_LINEAR_TRANSITION || !ProcessNextNodeValue) // no interpolation for in-between color steps, default case
                    color.rgb = mix(sceneColor.rgb, ANIMATION_MAX_SPECTRUM_COLOR, (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) ? nodeRealValueRatio : nodeValueRatio);
                else
                {
                    // interpolated color step
                    // note: no need to re-calc the percentageBetweenTicks from smoothstep, its value is available from above for the ANIMATION_FLUID_POLYNOMIAL_TRANSITION case
                    color.rgb = mix( sceneColor.rgb, ANIMATION_MAX_SPECTRUM_COLOR, mix( (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) ? nodeRealValueRatio : nodeValueRatio, (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) ? nextNodeRealValueRatio : nextNodeValueRatio, percentageBetweenTicks ) );
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
        }
    #endif

    return vec4(gl_Vertex.xyz * animationNodeSizeRatio, gl_Vertex.w);
}