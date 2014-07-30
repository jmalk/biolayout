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

 @ author, OpenCL code author Thanos Theo, 2008-2009-2010

*/

inline float vectorSum(const int, const int, __global const float*, const int, const int, const int);
#if USE_LOCAL_CACHE_FOR_FIRSTROW
    inline bool wholeWorkGroupHasSameFirstRowsCheck(const int, __local int*, const int);
    inline float vectorLocalSum(const int, const int, __global const float*, const int, const int, const int, __local float*);
#endif

__kernel void calculateCorrelation(__global const int *indexXY,
                                   __global const float *sumX_cache, __global const float *sumX_sumX2_cache, __global const float *sumColumns_X2_cache, __global const float *matrix,
                                   __global float *results, const int totalColumns, const int totalColumnsVector4Index, const int totalColumnsVector4Remainder
#if USE_LOCAL_CACHE_FOR_FIRSTROW
                                 , __local float *localBuffer, const int localIndices, __local int *localFirstRows, const int localWorkSize, __local bool *localWholeWorkGroupHasSameFirstRowsResult)
#else
                                  )
#endif
{
    const uint globalID = get_global_id(0);
    const uint localID = get_local_id(0);

    #if USE_PAIR_INDICES
        const int index = globalID + globalID;
        const int firstRow = indexXY[index];
        const int secondRow = indexXY[index + 1];
    #else
        const int index = indexXY[globalID];
        const int firstRow = ( (index >> 16) & 0xFFFF);
        const int secondRow = (index & 0xFFFF);
    #endif

    #if USE_LOCAL_CACHE_FOR_FIRSTROW
        localFirstRows[localID] = firstRow;
        barrier(CLK_LOCAL_MEM_FENCE);

        if (localID == 0)
            localWholeWorkGroupHasSameFirstRowsResult[0] = wholeWorkGroupHasSameFirstRowsCheck(firstRow, localFirstRows, localWorkSize);
        barrier(CLK_LOCAL_MEM_FENCE);

        if (localWholeWorkGroupHasSameFirstRowsResult[0])
        {
            if (localID < localIndices)
                #ifdef __USE_VECTOR1_COPY__
                    localBuffer[localID] = matrix[firstRow * totalColumns + localID];
                #elif defined(__USE_VECTOR2_COPY__)
                    vstore2(vload2(localID, matrix + firstRow * totalColumns), localID, localBuffer);
                #elif defined(__USE_VECTOR4_COPY__)
                    vstore4(vload4(localID, matrix + firstRow * totalColumns), localID, localBuffer);
                #elif defined(__USE_VECTOR8_COPY__)
                    vstore8(vload8(localID, matrix + firstRow * totalColumns), localID, localBuffer);
                #elif defined(__USE_VECTOR16_COPY__)
                    vstore16(vload16(localID, matrix + firstRow * totalColumns), localID, localBuffer);
                #endif
            barrier(CLK_LOCAL_MEM_FENCE);
        }
    #endif

    float denominator = native_sqrt( (sumColumns_X2_cache[firstRow] - sumX_sumX2_cache[firstRow]) * (sumColumns_X2_cache[secondRow] - sumX_sumX2_cache[secondRow]) );
    if ( (denominator != 0.0f) && !(denominator != denominator) ) // second check is to avoid an NaN problem, see definition of Float.isNaN()
    {
        #if USE_LOCAL_CACHE_FOR_FIRSTROW
            float sumXY = (localWholeWorkGroupHasSameFirstRowsResult[0]) ? vectorLocalSum(firstRow, secondRow, matrix, totalColumns, totalColumnsVector4Index, totalColumnsVector4Remainder, localBuffer) : vectorSum(firstRow, secondRow, matrix, totalColumns, totalColumnsVector4Index, totalColumnsVector4Remainder);
        #else
            float sumXY = vectorSum(firstRow, secondRow, matrix, totalColumns, totalColumnsVector4Index, totalColumnsVector4Remainder);
        #endif
        float result = native_divide( (totalColumns * sumXY) - (sumX_cache[firstRow] * sumX_cache[secondRow]), denominator );
        results[globalID] = clamp(result, -1.0f, 1.0f); // equal in OpenCL with: (result > 1.0f) ? 1.0f : ( (result < -1.0f) ? -1.0f : result );
    }
    else
        results[globalID] = -1.0f;
}

inline float vectorSum(const int firstRow, const int secondRow, __global const float *matrix, const int totalColumns, const int totalColumnsVector4Index, const int totalColumnsVector4Remainder)
{
    __global const float* matrixOffsetFirstRowDimension = matrix + firstRow * totalColumns;
    __global const float* matrixOffsetSecondRowDimension = matrix + secondRow * totalColumns;

    float sumXY = 0.0f;
    float4 sumXY4 = (float4)(0.0f);
    int i = 0;

    #if USE_VECTOR8_TRANSFERS_IN_VECTORSUMS
        int totalColumnsVector8Index = totalColumnsVector4Index >> 1;    // divide by 2
        int totalColumnsVector8Remainder = totalColumnsVector4Index & 1; // modulo by 2
        float8 sumXY8 = (float8)(0.0f);
        for (int j = 0; j < totalColumnsVector8Index; j++)
            sumXY8 = USE_MAD_OR_FMA_FUNCTION(vload8(j, matrixOffsetFirstRowDimension), vload8(j, matrixOffsetSecondRowDimension), sumXY8);
        i = 2 * totalColumnsVector8Index;
        sumXY4 = (sumXY8.s0123 + sumXY8.s4567);

        if (totalColumnsVector8Remainder > 0)
        {
            sumXY4 = USE_MAD_OR_FMA_FUNCTION(vload4(i, matrixOffsetFirstRowDimension), vload4(i, matrixOffsetSecondRowDimension), sumXY4);
            i++;
        }
    #else
        for (i = 0; i < totalColumnsVector4Index; i++)
            sumXY4 = USE_MAD_OR_FMA_FUNCTION(vload4(i, matrixOffsetFirstRowDimension), vload4(i, matrixOffsetSecondRowDimension), sumXY4);
    #endif

    sumXY += (sumXY4.x + sumXY4.y + sumXY4.z + sumXY4.w);

    if (totalColumnsVector4Remainder > 0)
    {
        sumXY4 = vload4(i, matrixOffsetFirstRowDimension) * vload4(i, matrixOffsetSecondRowDimension);
        if (totalColumnsVector4Remainder == 1)
            sumXY += sumXY4.x;
        else if (totalColumnsVector4Remainder == 2)
            sumXY += (sumXY4.x + sumXY4.y);
        else if (totalColumnsVector4Remainder == 3)
            sumXY += (sumXY4.x + sumXY4.y + sumXY4.z);
    }

    return sumXY;
}

#if USE_LOCAL_CACHE_FOR_FIRSTROW
    inline bool wholeWorkGroupHasSameFirstRowsCheck(const int firstRow, __local int* localFirstRows, const int localWorkSize)
    {
        __local const int* localFirstRowsForVector = localFirstRows;
        int maxIndex = localWorkSize >> 4; // divide by 16
        int16 localFirstRowsVector16;
        for (int i = 0; i < maxIndex; i++)
        {
            localFirstRowsVector16 = vload16(i, localFirstRowsForVector);
            if (firstRow != localFirstRowsVector16.s0 || firstRow != localFirstRowsVector16.s1 || firstRow != localFirstRowsVector16.s2 || firstRow != localFirstRowsVector16.s3 ||
                firstRow != localFirstRowsVector16.s4 || firstRow != localFirstRowsVector16.s5 || firstRow != localFirstRowsVector16.s6 || firstRow != localFirstRowsVector16.s7 ||
                firstRow != localFirstRowsVector16.s8 || firstRow != localFirstRowsVector16.s9 || firstRow != localFirstRowsVector16.sa || firstRow != localFirstRowsVector16.sb ||
                firstRow != localFirstRowsVector16.sc || firstRow != localFirstRowsVector16.sd || firstRow != localFirstRowsVector16.se || firstRow != localFirstRowsVector16.sf)
                return false;
        }

        return true;
    }

    inline float vectorLocalSum(const int firstRow, const int secondRow, __global const float *matrix, const int totalColumns, const int totalColumnsVector4Index, const int totalColumnsVector4Remainder, __local float* localBuffer)
    {
        // __local const float* localBufferForVector = (__local const float*)localBuffer;
        __global const float* matrixOffsetSecondRowDimension = matrix + secondRow * totalColumns;

        float sumXY = 0.0f;
        float4 sumXY4 = (float4)(0.0f);
        int i = 0;

        #if USE_VECTOR16_TRANSFERS_IN_VECTORSUMS
            int totalColumnsVector16Index = totalColumnsVector4Index >> 2;    // divide by 4
            int totalColumnsVector16Remainder = totalColumnsVector4Index & 3; // modulo by 4
            float16 sumXY16 = (float16)(0.0f);
            for (int j = 0; j < totalColumnsVector16Index; j++)
                sumXY16 = USE_MAD_OR_FMA_FUNCTION(vload16(j, localBuffer), vload16(j, matrixOffsetSecondRowDimension), sumXY16);
            i = 4 * totalColumnsVector16Index;
            sumXY4 = (sumXY16.s0123 + sumXY16.s4567 + sumXY16.s89ab + sumXY16.scdef);

            if (totalColumnsVector16Remainder > 0)
            {
                for (int k = 0; k < totalColumnsVector16Remainder; k++)
                {
                    sumXY4 = USE_MAD_OR_FMA_FUNCTION(vload4(i, localBuffer), vload4(i, matrixOffsetSecondRowDimension), sumXY4);
                    i++;
                }
            }
        #else
            for (i = 0; i < totalColumnsVector4Index; i++)
                sumXY4 = USE_MAD_OR_FMA_FUNCTION(vload4(i, localBuffer), vload4(i, matrixOffsetSecondRowDimension), sumXY4);
        #endif

        sumXY += (sumXY4.x + sumXY4.y + sumXY4.z + sumXY4.w);

        if (totalColumnsVector4Remainder > 0)
        {
            sumXY4 = vload4(i, localBuffer) * vload4(i, matrixOffsetSecondRowDimension);
            if (totalColumnsVector4Remainder == 1)
                sumXY += sumXY4.x;
            else if (totalColumnsVector4Remainder == 2)
                sumXY += (sumXY4.x + sumXY4.y);
            else if (totalColumnsVector4Remainder == 3)
                sumXY += (sumXY4.x + sumXY4.y + sumXY4.z);
        }

        return sumXY;
    }
#endif