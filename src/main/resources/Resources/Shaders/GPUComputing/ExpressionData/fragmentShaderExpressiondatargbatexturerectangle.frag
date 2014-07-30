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

#extension GL_ARB_texture_rectangle : enable
#extension GL_EXT_gpu_shader4 : enable

uniform sampler2DRect textureX;
uniform sampler2DRect textureY;
uniform sampler2DRect textureSumX_cache;
uniform sampler2DRect textureSumX_sumX2_cache;
uniform sampler2DRect textureSumColumns_X2_cache;
uniform sampler2DRect textureExpressionMatrix;

uniform int oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision;
uniform int oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo;
#if TWO_DIMENSIONAL_EXPRESSION_DATA_POWER_OF_TWO
    uniform int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision;
    uniform int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo;
#else
    uniform int twoDimensionalExpressionDataConvertedTo2DSquareTextureSize;
#endif
uniform int totalColumns;

const int RGBA_VALUES_PER_TEXEL = 4;
const int RGBA_VALUES_PER_TEXEL_FOR_BITSHIFT_MODULO = RGBA_VALUES_PER_TEXEL - 1;
const int RGBA_VALUES_PER_TEXEL_FOR_BITSHIFT_DIVISION = int( log2(RGBA_VALUES_PER_TEXEL) );

float getOneDimensionalMatrix1FloatValueFromIndex(in int);
float getOneDimensionalMatrix2FloatValueFromIndex(in int);
float getOneDimensionalMatrix3FloatValueFromIndex(in int);
vec2 getNonNormalizedTexCoordFrom1DIndex(in int);
float calculateCorrelation(in float, in int, in int);

void main()
{
    ivec4 indicesX = ivec4( texture2DRect(textureX, gl_TexCoord[0].st) );
    ivec4 indicesY = ivec4( texture2DRect(textureY, gl_TexCoord[0].st) );
    vec4 denominators = sqrt( vec4( ( getOneDimensionalMatrix3FloatValueFromIndex(indicesX.x) - getOneDimensionalMatrix2FloatValueFromIndex(indicesX.x) ) * ( getOneDimensionalMatrix3FloatValueFromIndex(indicesY.x) - getOneDimensionalMatrix2FloatValueFromIndex(indicesY.x) ),
                                    ( getOneDimensionalMatrix3FloatValueFromIndex(indicesX.y) - getOneDimensionalMatrix2FloatValueFromIndex(indicesX.y) ) * ( getOneDimensionalMatrix3FloatValueFromIndex(indicesY.y) - getOneDimensionalMatrix2FloatValueFromIndex(indicesY.y) ),
                                    ( getOneDimensionalMatrix3FloatValueFromIndex(indicesX.z) - getOneDimensionalMatrix2FloatValueFromIndex(indicesX.z) ) * ( getOneDimensionalMatrix3FloatValueFromIndex(indicesY.z) - getOneDimensionalMatrix2FloatValueFromIndex(indicesY.z) ),
                                    ( getOneDimensionalMatrix3FloatValueFromIndex(indicesX.w) - getOneDimensionalMatrix2FloatValueFromIndex(indicesX.w) ) * ( getOneDimensionalMatrix3FloatValueFromIndex(indicesY.w) - getOneDimensionalMatrix2FloatValueFromIndex(indicesY.w) ) ) );
    // !(denominator != denominator) second check is to avoid an NaN problem, see definition of Float.isNaN()
    for (int i = 0; i < RGBA_VALUES_PER_TEXEL; i++)
        gl_FragColor[i] = ( (denominators[i] != 0.0) && !(denominators[i] != denominators[i]) ) ? calculateCorrelation(denominators[i], indicesX[i], indicesY[i]) : -1.0;
}

float getOneDimensionalMatrix1FloatValueFromIndex(in int indexX)
{
    return texture2DRect( textureSumX_cache, getNonNormalizedTexCoordFrom1DIndex(indexX) )[indexX & RGBA_VALUES_PER_TEXEL_FOR_BITSHIFT_MODULO];
}

float getOneDimensionalMatrix2FloatValueFromIndex(in int indexX)
{
    return texture2DRect( textureSumX_sumX2_cache, getNonNormalizedTexCoordFrom1DIndex(indexX) )[indexX & RGBA_VALUES_PER_TEXEL_FOR_BITSHIFT_MODULO];
}

float getOneDimensionalMatrix3FloatValueFromIndex(in int indexX)
{
    return texture2DRect( textureSumColumns_X2_cache, getNonNormalizedTexCoordFrom1DIndex(indexX) )[indexX & RGBA_VALUES_PER_TEXEL_FOR_BITSHIFT_MODULO];
}

vec2 getNonNormalizedTexCoordFrom1DIndex(in int indexX)
{
    int texelIndex = indexX >> RGBA_VALUES_PER_TEXEL_FOR_BITSHIFT_DIVISION;
    int rowIndex = texelIndex & oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision;
    int columnIndex = texelIndex >> oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo;

    return vec2( float(rowIndex), float(columnIndex) ) ;
}

float calculateCorrelation(in float denominator, in int indexX, in int indexY)
{
    ivec2 texelXIndices = ivec2(indexX, indexY) * totalColumns;
    ivec2 texelIndices;
    ivec2 texelRowColumnIndices;
    vec4 nonNormalizedTexCoordFrom2DIndices;
    float sumXY = 0.0;
    for (int i = 0; i < totalColumns; i++)
    {
        texelIndices = texelXIndices + i;
        texelRowColumnIndices = texelIndices >> RGBA_VALUES_PER_TEXEL_FOR_BITSHIFT_DIVISION;
        #if TWO_DIMENSIONAL_EXPRESSION_DATA_POWER_OF_TWO
            nonNormalizedTexCoordFrom2DIndices = vec4(texelRowColumnIndices & twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, texelRowColumnIndices >> twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo);
        #else
            nonNormalizedTexCoordFrom2DIndices = vec4(texelRowColumnIndices % twoDimensionalExpressionDataConvertedTo2DSquareTextureSize, texelRowColumnIndices / twoDimensionalExpressionDataConvertedTo2DSquareTextureSize);
        #endif
        sumXY += ( texture2DRect(textureExpressionMatrix, nonNormalizedTexCoordFrom2DIndices.xz)[texelIndices.x & RGBA_VALUES_PER_TEXEL_FOR_BITSHIFT_MODULO] * texture2DRect(textureExpressionMatrix, nonNormalizedTexCoordFrom2DIndices.yw)[texelIndices.y & RGBA_VALUES_PER_TEXEL_FOR_BITSHIFT_MODULO] );
    }

    float result = ( (float(totalColumns) * sumXY) - ( getOneDimensionalMatrix1FloatValueFromIndex(indexX) * getOneDimensionalMatrix1FloatValueFromIndex(indexY) ) ) / denominator;
    return clamp(result, -1.0, 1.0); // equal in GLSL with: (result > 1.0) ? 1.0 : ( (result < -1.0) ? -1.0 : result );
}