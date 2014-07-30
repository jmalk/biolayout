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

// #pragma OPENCL EXTENSION cl_amd_printf : enable /* for printf("%d \n", int i); based commands, AMD/ATI only extension */

#if USE_ATOMICS
    #pragma OPENCL EXTENSION cl_khr_global_int32_base_atomics: enable
#endif

inline void calcBiDirForce2D(const uint, const uint,
                             __global const int*, __global const float*, __global const int*, __global const int *,
                             __global const float *, __global const float*, __global const int*, __global int*,
                             const int, const float, const int, const int
                            );

__kernel void calcBiDirForce2DKernel1D(__global const int *indexXY,
                                       __global const int *vertexIndicesMatrixArray, __global const float *cachedVertexPointCoordsMatrixArray, __global const int *cachedVertexConnectionMatrixArray, __global const int *cachedVertexConnectionRowSkipSizeValuesMatrixArray,
                                       __global const float *displacementMatrixArray, __global const float *cachedVertexNormalizedWeightMatrixArray, __global const int *cachedVertexNormalizedWeightIndicesToSkipArray, __global int *displacementValuesArray,
                                       const int displacementMatrixDimensionality, const float kDoubled, const int BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE, const int BOOLEAN_PACKED_DATA_BIT_SIZE,
                                       const int lastIterationlastIterationFRLayoutCalculationsNeededLayoutCalculationsNeeded
                                      )
{
    const uint globalID = get_global_id(0);
    if ( (lastIterationlastIterationFRLayoutCalculationsNeededLayoutCalculationsNeeded == 0) || (globalID < lastIterationlastIterationFRLayoutCalculationsNeededLayoutCalculationsNeeded) )
    {
        #if USE_PAIR_INDICES
            const int index = globalID + globalID;
            const int firstRow = indexXY[index];
            const int secondRow = indexXY[index + 1];
        #else
            const int index = indexXY[globalID];
            const int firstRow = ( (index >> 16) & 0xFFFF);
            const int secondRow = (index & 0xFFFF);
        #endif

        calcBiDirForce2D(firstRow, secondRow,
                         vertexIndicesMatrixArray, cachedVertexPointCoordsMatrixArray, cachedVertexConnectionMatrixArray, cachedVertexConnectionRowSkipSizeValuesMatrixArray,
                         displacementMatrixArray, cachedVertexNormalizedWeightMatrixArray, cachedVertexNormalizedWeightIndicesToSkipArray, displacementValuesArray,
                         displacementMatrixDimensionality, kDoubled, BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE, BOOLEAN_PACKED_DATA_BIT_SIZE);
    }
}

__kernel void calcBiDirForce2DKernel2D(__global const int *vertexIndicesMatrixArray, __global const float *cachedVertexPointCoordsMatrixArray, __global const int *cachedVertexConnectionMatrixArray, __global const int *cachedVertexConnectionRowSkipSizeValuesMatrixArray,
                                       __global const float *displacementMatrixArray, __global const float *cachedVertexNormalizedWeightMatrixArray, __global const int *cachedVertexNormalizedWeightIndicesToSkipArray, __global int *displacementValuesArray,
                                       const int displacementMatrixDimensionality, const float kDoubled, const int BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE, const int BOOLEAN_PACKED_DATA_BIT_SIZE,
                                       const int numberOfVertices
                                      )
{
    const uint firstRow = get_global_id(0);
    const uint secondRow = get_global_id(1);

    if ( (firstRow < numberOfVertices) && (secondRow < numberOfVertices) && (firstRow < secondRow) )
        calcBiDirForce2D(firstRow, secondRow,
                         vertexIndicesMatrixArray, cachedVertexPointCoordsMatrixArray, cachedVertexConnectionMatrixArray,cachedVertexConnectionRowSkipSizeValuesMatrixArray,
                         displacementMatrixArray, cachedVertexNormalizedWeightMatrixArray, cachedVertexNormalizedWeightIndicesToSkipArray, displacementValuesArray,
                         displacementMatrixDimensionality, kDoubled, BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE, BOOLEAN_PACKED_DATA_BIT_SIZE);
}

inline void calcBiDirForce2D(const uint firstRow, const uint secondRow,
                             __global const int *vertexIndicesMatrixArray, __global const float *cachedVertexPointCoordsMatrixArray, __global const int *cachedVertexConnectionMatrixArray, __global const int *cachedVertexConnectionRowSkipSizeValuesMatrixArray,
                             __global const float *displacementMatrixArray, __global const float *cachedVertexNormalizedWeightMatrixArray, __global const int *cachedVertexNormalizedWeightIndicesToSkipArray, __global int *displacementValuesArray,
                             const int displacementMatrixDimensionality, const float kDoubled, const int BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE, const int BOOLEAN_PACKED_DATA_BIT_SIZE
                            )
{
    int vertexID1 = vertexIndicesMatrixArray[firstRow];
    int vertexID2 = vertexIndicesMatrixArray[secondRow];

    int vertexID1Index0 = vertexID1 << 1;
    int vertexID2Index0 = vertexID2 << 1;
    int vertexID1Index1 = vertexID1Index0 + 1;
    int vertexID2Index1 = vertexID2Index0 + 1;
    int dimensionalityIndex = cachedVertexConnectionRowSkipSizeValuesMatrixArray[vertexID1 - 1] + vertexID2;

    float2 cachedVertexPointCoordsMatrixArrayvertexID1 = vload2(0, cachedVertexPointCoordsMatrixArray + vertexID1Index0);
    float2 cachedVertexPointCoordsMatrixArrayvertexID2 = vload2(0, cachedVertexPointCoordsMatrixArray + vertexID2Index0);
    float2 dist2 = cachedVertexPointCoordsMatrixArrayvertexID1 - cachedVertexPointCoordsMatrixArrayvertexID2;

    if (dist2.x == 0.0f)
        dist2.x = 1.0f;
    if (dist2.y == 0.0f)
        dist2.y = 1.0f;

    uint2 absDist2 = abs( convert_int2(dist2) );
    float2 sign2 = sign(dist2);

    int distanceCache = (6000 * absDist2.x + 6 * absDist2.y);
    if (distanceCache >= displacementMatrixDimensionality)
        distanceCache = displacementMatrixDimensionality;
    int2 dispCalc2;

    if ( ( cachedVertexConnectionMatrixArray[dimensionalityIndex >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE] >> (dimensionalityIndex & BOOLEAN_PACKED_DATA_BIT_SIZE) ) & 1 )
    {
        #if WEIGHTED_EDGES
            // int weightArrayIndex = 0;
            float weight = 1.0f; //cachedVertexNormalizedWeightMatrixArray[weightArrayIndex];
            float2 displacementMatrixArray2 = vload2(0, displacementMatrixArray + distanceCache + 4);
            dispCalc2 = convert_int2( ( ( (vload2(0, displacementMatrixArray + distanceCache) -  displacementMatrixArray2) * weight ) + displacementMatrixArray2 ) * sign2 );
        #else
            dispCalc2 = convert_int2( sign2 * vload2(0, displacementMatrixArray + distanceCache) );
        #endif

        #if USE_ATOMICS
            (void)atom_add(&displacementValuesArray[vertexID1Index0],  dispCalc2.x);
            (void)atom_add(&displacementValuesArray[vertexID1Index1],  dispCalc2.y);

            (void)atom_sub(&displacementValuesArray[vertexID2Index0],  dispCalc2.x);
            (void)atom_sub(&displacementValuesArray[vertexID2Index1],  dispCalc2.y);
        #else
            displacementValuesArray[vertexID1Index0] +=  dispCalc2.x;
            displacementValuesArray[vertexID1Index1] +=  dispCalc2.y;

            displacementValuesArray[vertexID2Index0] -=  dispCalc2.x;
            displacementValuesArray[vertexID2Index1] -=  dispCalc2.y;
        #endif
    }
    else
    {
        if ( !( (absDist2.x > kDoubled) && (absDist2.y > kDoubled) ) )
        {
            dispCalc2 = convert_int2( sign2 * vload2(0, displacementMatrixArray + distanceCache + 2) );

            #if USE_ATOMICS
                (void)atom_add(&displacementValuesArray[vertexID1Index0],  dispCalc2.x);
                (void)atom_add(&displacementValuesArray[vertexID1Index1],  dispCalc2.y);

                (void)atom_sub(&displacementValuesArray[vertexID2Index0],  dispCalc2.x);
                (void)atom_sub(&displacementValuesArray[vertexID2Index1],  dispCalc2.y);
            #else
                displacementValuesArray[vertexID1Index0] +=  dispCalc2.x;
                displacementValuesArray[vertexID1Index1] +=  dispCalc2.y;

                displacementValuesArray[vertexID2Index0] -=  dispCalc2.x;
                displacementValuesArray[vertexID2Index1] -=  dispCalc2.y;
            #endif
        }
    }
}

__kernel void set2DForceToVertex(__global const int *vertexIndicesMatrixArray, __global float *cachedVertexPointCoordsMatrixArray, __global int *displacementValuesArray,
                                 const float temperature, const int numberOfVertices, const int canvasXSize, const int canvasYSize
                                )
{
    const uint globalID = get_global_id(0);
    if (globalID >= numberOfVertices)
        return;

    int vertexID = vertexIndicesMatrixArray[globalID];

    int vertexIDIndex = vertexID << 1;
    float currentDisplacementValue = displacementValuesArray[vertexIDIndex];

    // for the X axis
    float value = (currentDisplacementValue < 0.0f) ? max(-temperature, currentDisplacementValue) : min(temperature, currentDisplacementValue);
    cachedVertexPointCoordsMatrixArray[vertexIDIndex] += value;

    if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] > canvasXSize)
        cachedVertexPointCoordsMatrixArray[vertexIDIndex] = canvasXSize;
    // commented out so as to avoid the relayout being bounded by the layout minimum threshold
    // else if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] < 0)
    //    cachedVertexPointCoordsMatrixArray[vertexIDIndex] = 0;

    displacementValuesArray[vertexIDIndex] = 0;



    // for the Y axis
    vertexIDIndex++;
    currentDisplacementValue = displacementValuesArray[vertexIDIndex];
    value = (currentDisplacementValue < 0.0f) ? max(-temperature, currentDisplacementValue) : min(temperature, currentDisplacementValue);
    cachedVertexPointCoordsMatrixArray[vertexIDIndex] += value;

    if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] > canvasYSize)
        cachedVertexPointCoordsMatrixArray[vertexIDIndex] = canvasYSize;
    // commented out so as to avoid the relayout being bounded by the layout minimum threshold
    // else if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] < 0)
    //    cachedVertexPointCoordsMatrixArray[vertexIDIndex] = 0;

    displacementValuesArray[vertexIDIndex] = 0;
}