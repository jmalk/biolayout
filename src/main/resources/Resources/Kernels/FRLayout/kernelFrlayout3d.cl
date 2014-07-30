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

inline void calcBiDirForce3D(const uint, const uint,
                             __global const int*, __global const float*, __global const int*, __global const int*,
                             __global const int*, __global const float*, __global const int*, __global int*,
                             const float, const float, const float, const int, const int
                            );

__kernel void calcBiDirForce3DKernel1D(__global const int *indexXY,
                                       __global const int *vertexIndicesMatrixArray, __global const float *cachedVertexPointCoordsMatrixArray, __global const int *cachedVertexConnectionMatrixArray, __global const int *cachedVertexConnectionRowSkipSizeValuesMatrixArray,
                                       __global const int *cachedPseudoVertexMatrixArray, __global const float *cachedVertexNormalizedWeightMatrixArray, __global const int *cachedVertexNormalizedWeightIndicesToSkipArray, __global int *displacementValuesArray,
                                       const float kDoubled, const float kValue, const float kSquareValue, const int BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE, const int BOOLEAN_PACKED_DATA_BIT_SIZE,
                                       const int lastIterationFRLayoutCalculationsNeeded
                                      )
{
    const uint globalID = get_global_id(0);

    if ( (lastIterationFRLayoutCalculationsNeeded == 0) || (globalID < lastIterationFRLayoutCalculationsNeeded) )
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

        calcBiDirForce3D(firstRow, secondRow, vertexIndicesMatrixArray, cachedVertexPointCoordsMatrixArray, cachedVertexConnectionMatrixArray, cachedVertexConnectionRowSkipSizeValuesMatrixArray,
                         cachedPseudoVertexMatrixArray, cachedVertexNormalizedWeightMatrixArray, cachedVertexNormalizedWeightIndicesToSkipArray, displacementValuesArray,
                         kDoubled, kValue, kSquareValue, BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE, BOOLEAN_PACKED_DATA_BIT_SIZE);
    }
}

__kernel void calcBiDirForce3DKernel2D(__global const int *vertexIndicesMatrixArray, __global const float *cachedVertexPointCoordsMatrixArray, __global const int *cachedVertexConnectionMatrixArray, __global const int *cachedVertexConnectionRowSkipSizeValuesMatrixArray,
                                       __global const int *cachedPseudoVertexMatrixArray, __global const float *cachedVertexNormalizedWeightMatrixArray, __global const int *cachedVertexNormalizedWeightIndicesToSkipArray, __global int *displacementValuesArray,
                                       const float kDoubled, const float kValue, const float kSquareValue, const int BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE, const int BOOLEAN_PACKED_DATA_BIT_SIZE,
                                       const int numberOfVertices
                                       )
{
    const uint firstRow = get_global_id(0);
    const uint secondRow = get_global_id(1);

    if ( (firstRow < numberOfVertices) && (secondRow < numberOfVertices) && (firstRow < secondRow) )
        calcBiDirForce3D(firstRow, secondRow, vertexIndicesMatrixArray, cachedVertexPointCoordsMatrixArray, cachedVertexConnectionMatrixArray, cachedVertexConnectionRowSkipSizeValuesMatrixArray,
                         cachedPseudoVertexMatrixArray, cachedVertexNormalizedWeightMatrixArray, cachedVertexNormalizedWeightIndicesToSkipArray, displacementValuesArray,
                         kDoubled, kValue, kSquareValue, BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE, BOOLEAN_PACKED_DATA_BIT_SIZE);
}

inline void calcBiDirForce3D(const uint firstRow, const uint secondRow,
                             __global const int *vertexIndicesMatrixArray, __global const float *cachedVertexPointCoordsMatrixArray, __global const int *cachedVertexConnectionMatrixArray, __global const int *cachedVertexConnectionRowSkipSizeValuesMatrixArray,
                             __global const int *cachedPseudoVertexMatrixArray, __global const float *cachedVertexNormalizedWeightMatrixArray, __global const int *cachedVertexNormalizedWeightIndicesToSkipArray, __global int *displacementValuesArray,
                             const float kDoubled, const float kValue, const float kSquareValue, const int BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE, const int BOOLEAN_PACKED_DATA_BIT_SIZE
                            )
{
    int vertexID1 = vertexIndicesMatrixArray[firstRow];
    int vertexID2 = vertexIndicesMatrixArray[secondRow];

    int vertexID1Index0 = 3 * vertexID1;
    int vertexID2Index0 = 3 * vertexID2;
    int vertexID1Index1 = vertexID1Index0 + 1;
    int vertexID2Index1 = vertexID2Index0 + 1;
    int vertexID1Index2 = vertexID1Index0 + 2;
    int vertexID2Index2 = vertexID2Index0 + 2;
    int dimensionalityIndex = cachedVertexConnectionRowSkipSizeValuesMatrixArray[vertexID1 - 1] + vertexID2;

    float4 cachedVertexPointCoordsMatrixArrayvertexID1 = vload4(0, cachedVertexPointCoordsMatrixArray + vertexID1Index0);
    float4 cachedVertexPointCoordsMatrixArrayvertexID2 = vload4(0, cachedVertexPointCoordsMatrixArray + vertexID2Index0);
    float4 dist4 = cachedVertexPointCoordsMatrixArrayvertexID1 - cachedVertexPointCoordsMatrixArrayvertexID2;

    if (dist4.x == 0.0f)
        dist4.x = 1.0f;
    if (dist4.y == 0.0f)
        dist4.y = 1.0f;
    if (dist4.z == 0.0f)
        dist4.z = 1.0f;

    bool connected = ( ( cachedVertexConnectionMatrixArray[dimensionalityIndex >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE] >> (dimensionalityIndex & BOOLEAN_PACKED_DATA_BIT_SIZE) ) & 1 );
    if ( !connected && ( abs( (int)dist4.x ) > kDoubled ) && ( abs( (int)dist4.y ) > kDoubled ) )
        return;

    dist4.w = 0.0f;
    float distance = length(dist4);
    int dispCalcX = 0, dispCalcY = 0, dispCalcZ = 0;

    if (distance <= kDoubled)
    {        
        if ( !( ( ( cachedPseudoVertexMatrixArray[vertexID1 >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE] >> (vertexID1 & BOOLEAN_PACKED_DATA_BIT_SIZE) ) & 1 ) && ( ( cachedPseudoVertexMatrixArray[vertexID2 >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE] >> (vertexID2 & BOOLEAN_PACKED_DATA_BIT_SIZE) ) & 1 ) ) )
        {
            float kDist = native_divide(kSquareValue, distance);
	    float4 distance4 = (float4)(distance, distance, distance, 0.0f);
            int4 dispCalc4 = convert_int4( (dist4 / distance4) * kDist );

            #if USE_ATOMICS
                (void)atom_add(&displacementValuesArray[vertexID1Index0], dispCalc4.x);
                (void)atom_add(&displacementValuesArray[vertexID1Index1], dispCalc4.y);
                (void)atom_add(&displacementValuesArray[vertexID1Index2], dispCalc4.z);

                (void)atom_sub(&displacementValuesArray[vertexID2Index0], dispCalc4.x);
                (void)atom_sub(&displacementValuesArray[vertexID2Index1], dispCalc4.y);
                (void)atom_sub(&displacementValuesArray[vertexID2Index2], dispCalc4.z);
            #else
                displacementValuesArray[vertexID1Index0] += dispCalc4.x;
                displacementValuesArray[vertexID1Index1] += dispCalc4.y;
                displacementValuesArray[vertexID1Index2] += dispCalc4.z;

                displacementValuesArray[vertexID2Index0] -= dispCalc4.x;
                displacementValuesArray[vertexID2Index1] -= dispCalc4.y;
                displacementValuesArray[vertexID2Index2] -= dispCalc4.z;
            #endif
        }
    }

    if (connected)
    {
        float kDist = native_divide(distance * distance, kValue);
        float4 distance4 = (float4)(distance, distance, distance, 0.0f);
        int4 dispCalc4;

        #if WEIGHTED_EDGES
            // int weightArrayIndex = 0;
            float kDistWeight = kDist * 1.0f; //cachedVertexNormalizedWeightMatrixArray[weightArrayIndex];
            dispCalc4 = convert_int4( (dist4 / distance4) * kDistWeight );
        #else
            dispCalc4 = convert_int4( (dist4 / distance4) * kDist );
        #endif

        #if USE_ATOMICS
            (void)atom_sub(&displacementValuesArray[vertexID1Index0], dispCalc4.x);
            (void)atom_sub(&displacementValuesArray[vertexID1Index1], dispCalc4.y);
            (void)atom_sub(&displacementValuesArray[vertexID1Index2], dispCalc4.z);

            (void)atom_add(&displacementValuesArray[vertexID2Index0], dispCalc4.x);
            (void)atom_add(&displacementValuesArray[vertexID2Index1], dispCalc4.y);
            (void)atom_add(&displacementValuesArray[vertexID2Index2], dispCalc4.z);
        #else
            displacementValuesArray[vertexID1Index0] -= dispCalc4.x;
            displacementValuesArray[vertexID1Index1] -= dispCalc4.y;
            displacementValuesArray[vertexID1Index2] -= dispCalc4.z;

            displacementValuesArray[vertexID2Index0] += dispCalc4.x;
            displacementValuesArray[vertexID2Index1] += dispCalc4.y;
            displacementValuesArray[vertexID2Index2] += dispCalc4.z;
        #endif
    }
}

__kernel void set3DForceToVertex(__global const int *vertexIndicesMatrixArray, __global float *cachedVertexPointCoordsMatrixArray, __global int *displacementValuesArray,
                                 const float temperature, const int numberOfVertices, const int canvasXSize, const int canvasYSize, const int canvasZSize
                                )
{
    const uint globalID = get_global_id(0);
    if (globalID >= numberOfVertices)
        return;

    int vertexID = vertexIndicesMatrixArray[globalID];

    int vertexIDIndex = 3 * vertexID;
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



    // for the Z axis
    vertexIDIndex++;
    currentDisplacementValue = displacementValuesArray[vertexIDIndex];
    value = (currentDisplacementValue < 0.0f) ? max(-temperature, currentDisplacementValue) : min(temperature, currentDisplacementValue);
    cachedVertexPointCoordsMatrixArray[vertexIDIndex] += value;

    if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] > canvasZSize)
        cachedVertexPointCoordsMatrixArray[vertexIDIndex] = canvasZSize;
    // commented out so as to avoid the relayout being bounded by the layout minimum threshold
    // else if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] < 0)
    //    cachedVertexPointCoordsMatrixArray[vertexIDIndex] = 0;

    displacementValuesArray[vertexIDIndex] = 0;
}