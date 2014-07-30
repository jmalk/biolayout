package org.BioLayoutExpress3D.GPUComputing.GLSL.Animation;


import java.awt.*;
import java.awt.image.*;
import org.BioLayoutExpress3D.DataStructures.*;
import static org.BioLayoutExpress3D.Graph.Graph.*;
import static org.BioLayoutExpress3D.GPUComputing.GLSL.CPUEmulatedGLSLFunctions.*;
import static org.BioLayoutExpress3D.Environment.AnimationEnvironment.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/*
*
* AnimationVisualization is a final class containing only static based methods, used for calculating the animation visualization.
* The CPU (emulated GLSL) side is residing here, the GPU side is done with GLSL shaders.
*
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class AnimationVisualization
{

    /**
    *  Minimum node ratio value for GPU Computing.
    *  It was empirically defined with a value of 17.5f and a given animation max value of 350.
    */
    private static final float MINIMUM_NODE_RATIO_VALUE_FOR_GPU_COMPUTING = 17.5f / 350.f;

    /**
    *  Calculates all the necessary values for the animation visualization.
    */
    public static Tuple6<Float, Color, Boolean, Float, Boolean, Float> performAnimationVisualization(boolean is3DMode, int nodeID, String nodeName, boolean isAllShadingSFXSValueEnabled, Color nodeColor, int currentTick, int animationFrameCount, BufferedImage animationSpectrumImage, boolean isExpressionProfileAnimationMode)
    {
        float nodeScaleValue = 0.0f;
        float nodeValue = 0.0f;
        float nodeValueRatio = 0.0f;
        float nodeRealValueRatio = 0.0f;
        float nextNodeValue = 0.0f;
        float nextNodeValueRatio = 0.0f;
        float nextNodeRealValueRatio = 0.0f;
        float nextNodeScaleValue = 0.0f;
        float currentFrame = 0.0f;
        float percentageBetweenTicks = 0.0f;
        boolean processNextNodeValue = false;
        int horizontalValue = 0;
        boolean useShaderAnimationGPUComputing = false;

        if ( isExpressionProfileAnimationMode && ANIMATION_PER_NODE_MAX_VALUE.get() )
            ANIMATION_RESULTS_REAL_MAX_VALUE = ANIMATION_RESULTS_MAX_VALUE = ANIMATION_EXPRESSION_DATA_LOCAL_MAX_VALUES[nodeID];
        int index = (isExpressionProfileAnimationMode) ? ANIMATION_EXPRESSION_DATA.getIdentityMap(nodeName) : 0;
        nodeValue = (isExpressionProfileAnimationMode) ? ANIMATION_EXPRESSION_DATA.getExpressionDataValue(index, currentTick) : ANIMATION_SIMULATION_RESULTS.getValue(nodeID, currentTick);
        processNextNodeValue = ( ANIMATION_FLUID_LINEAR_TRANSITION && ( (currentTick + 1) < TOTAL_NUMBER_OF_ANIMATION_TICKS ) );
        if (processNextNodeValue)
            nextNodeValue = (isExpressionProfileAnimationMode) ? ANIMATION_EXPRESSION_DATA.getExpressionDataValue(index, currentTick + 1) : ANIMATION_SIMULATION_RESULTS.getValue(nodeID, currentTick + 1);
        nodeScaleValue = (is3DMode)
                         ? (1.0f)
                         : (1.0f / NODE_SIZE_DIVIDE_RATIO);

        if (USE_SHADERS_PROCESS && MATERIAL_SPECULAR.get() && isAllShadingSFXSValueEnabled)
        {
            if (ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION && !ANIMATION_USE_IMAGE_AS_SPECTRUM)
                nodeColor = ANIMATION_MIN_SPECTRUM_COLOR;

            // for some reason in GPU Computing mode with GLSL Shaders the node can be very small compared to standard CPU mode (almost invisible)
            // this check offsets it to a given minimum value by changing the minimum node value
            float minimumValue = MINIMUM_NODE_RATIO_VALUE_FOR_GPU_COMPUTING * ANIMATION_RESULTS_MAX_VALUE;
            if (nodeValue < minimumValue) nodeValue = minimumValue;
            if ( processNextNodeValue && (nextNodeValue < minimumValue) ) nextNodeValue = minimumValue;

            useShaderAnimationGPUComputing = true;
        }
        else // CPU GLSL emulation code for non-shader capable gfx cards
        {
            if (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) nodeRealValueRatio = clamp(nodeValue / ANIMATION_RESULTS_REAL_MAX_VALUE, 0.0f, 1.0f);
            if (nodeValue > ANIMATION_RESULTS_MAX_VALUE) nodeValue = ANIMATION_RESULTS_MAX_VALUE;
            nodeValueRatio = clamp(nodeValue / ANIMATION_RESULTS_MAX_VALUE, 0.0f, 1.0f);
            nodeScaleValue += (is3DMode)
                              ? (nodeValueRatio * ANIMATION_MAX_NODE_SIZE)
                              : (nodeValueRatio * ANIMATION_MAX_NODE_SIZE / NODE_SIZE_DIVIDE_RATIO);

            if (processNextNodeValue)
            {
                if (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) nextNodeRealValueRatio = clamp(nextNodeValue / ANIMATION_RESULTS_REAL_MAX_VALUE, 0.0f, 1.0f);
                if (nextNodeValue > ANIMATION_RESULTS_MAX_VALUE) nextNodeValue = ANIMATION_RESULTS_MAX_VALUE;
                nextNodeValueRatio = clamp(nextNodeValue / ANIMATION_RESULTS_MAX_VALUE, 0.0f, 1.0f);
                nextNodeScaleValue = (is3DMode)
                                     ?   (1.0f + nextNodeValueRatio * ANIMATION_MAX_NODE_SIZE)
                                     : ( (1.0f + nextNodeValueRatio * ANIMATION_MAX_NODE_SIZE) / NODE_SIZE_DIVIDE_RATIO );

                currentFrame = ( animationFrameCount % (FRAMERATE_PER_SECOND_FOR_ANIMATION / ANIMATION_TICKS_PER_SECOND) ) * ANIMATION_TICKS_PER_SECOND;
                percentageBetweenTicks = currentFrame / FRAMERATE_PER_SECOND_FOR_ANIMATION;

                if (ANIMATION_FLUID_POLYNOMIAL_TRANSITION)
                    percentageBetweenTicks = smootherstep(0.0f, 1.0f, percentageBetweenTicks);
                nodeScaleValue = mix(nodeScaleValue, nextNodeScaleValue, percentageBetweenTicks);
            }

            if (ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION)
            {
                if (!ANIMATION_USE_IMAGE_AS_SPECTRUM)
                {
                    if (!ANIMATION_FLUID_LINEAR_TRANSITION || !processNextNodeValue) // no interpolation for in-between color steps, default case
                        nodeColor = mix(ANIMATION_MIN_SPECTRUM_COLOR, ANIMATION_MAX_SPECTRUM_COLOR, (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) ? nodeRealValueRatio : nodeValueRatio);
                    else
                    {
                        // interpolated color step
                        // note: no need to re-calc the percentageBetweenTicks from CPUEmulatedGLSLFunction_smoothstep, its value is available from above for the ANIMATION_FLUID_POLYNOMIAL_TRANSITION case
                        nodeColor = mix( ANIMATION_MIN_SPECTRUM_COLOR, ANIMATION_MAX_SPECTRUM_COLOR, mix( (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) ? nodeRealValueRatio : nodeValueRatio, (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) ? nextNodeRealValueRatio : nextNodeValueRatio, percentageBetweenTicks ) );
                    }
                }
                else
                {
                    if (!ANIMATION_FLUID_LINEAR_TRANSITION || !processNextNodeValue) // no interpolation for in-between color steps, default case
                        horizontalValue = (int)( ( (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) ? nodeRealValueRatio : nodeValueRatio ) * animationSpectrumImage.getWidth() );
                    else
                    {
                        // interpolated color step
                        // note: no need to re-calc the percentageBetweenTicks from CPUEmulatedGLSLFunction_smoothstep, its value is available from above for the ANIMATION_FLUID_POLYNOMIAL_TRANSITION case
                        horizontalValue = (int)( mix( (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) ? nodeRealValueRatio : nodeValueRatio, (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) ? nextNodeRealValueRatio : nextNodeValueRatio, percentageBetweenTicks ) * animationSpectrumImage.getWidth() );
                    }

                    if ( horizontalValue >= animationSpectrumImage.getWidth() )
                        horizontalValue = animationSpectrumImage.getWidth() - 1;
                    nodeColor = new Color( animationSpectrumImage.getRGB(horizontalValue, animationSpectrumImage.getHeight() / 2) );
                }
            }
        }

        return Tuples.tuple(nodeScaleValue, nodeColor, useShaderAnimationGPUComputing, nodeValue, processNextNodeValue, nextNodeValue);
    }

    /**
    *  Calculates all the necessary values for the node animation value.
    */
    public static float getAnimationVisualizationNodeValue(int nodeID, String nodeName, int currentTick, int animationFrameCount, boolean isExpressionProfileAnimationMode)
    {
        float nodeValue = 0.0f;
        float nextNodeValue = 0.0f;
        float currentFrame = 0.0f;
        float percentageBetweenTicks = 0.0f;
        boolean processNextNodeValue = false;

        int index = (isExpressionProfileAnimationMode) ? ANIMATION_EXPRESSION_DATA.getIdentityMap(nodeName) : 0;
        nodeValue = (isExpressionProfileAnimationMode) ? ANIMATION_EXPRESSION_DATA.getExpressionDataValue(index, currentTick) : ANIMATION_SIMULATION_RESULTS.getValue(nodeID, currentTick);
        processNextNodeValue = ( ANIMATION_FLUID_LINEAR_TRANSITION && ( (currentTick + 1) < TOTAL_NUMBER_OF_ANIMATION_TICKS ) );
        if (processNextNodeValue)
            nextNodeValue = (isExpressionProfileAnimationMode) ? ANIMATION_EXPRESSION_DATA.getExpressionDataValue(index, currentTick + 1) : ANIMATION_SIMULATION_RESULTS.getValue(nodeID, currentTick + 1);

        if (processNextNodeValue)
        {
            currentFrame = ( animationFrameCount % (FRAMERATE_PER_SECOND_FOR_ANIMATION / ANIMATION_TICKS_PER_SECOND) ) * ANIMATION_TICKS_PER_SECOND;
            percentageBetweenTicks = currentFrame / FRAMERATE_PER_SECOND_FOR_ANIMATION;

            if (ANIMATION_FLUID_POLYNOMIAL_TRANSITION)
                percentageBetweenTicks = smootherstep(0.0f, 1.0f, percentageBetweenTicks);
            nodeValue = mix(nodeValue, nextNodeValue, percentageBetweenTicks);
        }

        return nodeValue;
    }


}