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

#extension GL_EXT_gpu_shader4 : enable

uniform sampler2D textureX;
uniform sampler2D textureY;
uniform sampler2D textureSumX_cache;
uniform sampler2D textureSumX_sumX2_cache;
uniform sampler2D textureSumColumns_X2_cache;
uniform sampler2D textureExpressionMatrix;

uniform int oneDimensionalExpressionDataConvertedTo2DSquareTextureSize;
uniform int oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision;
uniform int oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo;
uniform int twoDimensionalExpressionDataConvertedTo2DSquareTextureSize;
#if TWO_DIMENSIONAL_EXPRESSION_DATA_POWER_OF_TWO
    uniform int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision;
    uniform int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo;
#endif
uniform vec2 texelCenters;
uniform int totalColumns;

float calculateCorrelation(in float, in ivec2, in vec4);

void main()
{
    ivec2 indices = ivec2( texture2D(textureX, gl_TexCoord[0].st).x, texture2D(textureY, gl_TexCoord[0].st).x );
    vec4 normalizedTexCoordFrom1DIndices = vec4(indices & oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, indices >> oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo) / float(oneDimensionalExpressionDataConvertedTo2DSquareTextureSize) + texelCenters[0];
    float denominator = sqrt( (texture2D(textureSumColumns_X2_cache, normalizedTexCoordFrom1DIndices.xz).x - texture2D(textureSumX_sumX2_cache, normalizedTexCoordFrom1DIndices.xz).x) *
                              (texture2D(textureSumColumns_X2_cache, normalizedTexCoordFrom1DIndices.yw).x - texture2D(textureSumX_sumX2_cache, normalizedTexCoordFrom1DIndices.yw).x) );
    gl_FragColor.x = ( (denominator != 0.0) && !(denominator != denominator) ) ? calculateCorrelation(denominator, indices, normalizedTexCoordFrom1DIndices) : -1.0; // !(denominator != denominator) second check is to avoid an NaN problem, see definition of Float.isNaN()
}

float calculateCorrelation(in float denominator, in ivec2 indices, in vec4 normalizedTexCoordFrom1DIndices)
{
    ivec2 texelXIndices = indices * totalColumns;
    ivec2 texelIndices;
    vec4 normalizedTexCoordFrom2DIndices;
    float sumXY = 0.0;
    for (int i = 0; i < totalColumns; i++)
    {
        texelIndices = texelXIndices + i;
        #if TWO_DIMENSIONAL_EXPRESSION_DATA_POWER_OF_TWO
            normalizedTexCoordFrom2DIndices = vec4(texelIndices & twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, texelIndices >> twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo) / float(twoDimensionalExpressionDataConvertedTo2DSquareTextureSize) + texelCenters[1];
        #else
            normalizedTexCoordFrom2DIndices = vec4(texelIndices % twoDimensionalExpressionDataConvertedTo2DSquareTextureSize, texelIndices / twoDimensionalExpressionDataConvertedTo2DSquareTextureSize) / float(twoDimensionalExpressionDataConvertedTo2DSquareTextureSize) + texelCenters[1];
        #endif
        sumXY += ( texture2D(textureExpressionMatrix, normalizedTexCoordFrom2DIndices.xz).x * texture2D(textureExpressionMatrix, normalizedTexCoordFrom2DIndices.yw).x );
    }

    float result = ( (float(totalColumns) * sumXY) - (texture2D(textureSumX_cache, normalizedTexCoordFrom1DIndices.xz).x * texture2D(textureSumX_cache, normalizedTexCoordFrom1DIndices.yw).x) ) / denominator;
    return clamp(result, -1.0, 1.0); // equal in GLSL with: (result > 1.0) ? 1.0 : ( (result < -1.0) ? -1.0 : result );
}