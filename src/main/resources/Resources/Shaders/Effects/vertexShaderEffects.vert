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

const float MORPH_DESTINATION_FACTOR = 2.0;
const float MORPH_CUBE_SIZE = 2.0;

#if GPU_SHADER4_COMPATIBILITY_CONDITION

    #extension GL_EXT_gpu_shader4: enable

    int LFSR_Rand_Gen(in int);
    float noise3f(in vec3);
#endif
float rand(in vec2);
vec4 applyMorphing(in float, in float);
vec2 applySphericalCoordinates(in vec3, in vec3);

#if GPU_SHADER4_COMPATIBILITY_CONDITION
    int LFSR_Rand_Gen(in int n)
    {
        // <<, ^ and & require GL_EXT_gpu_shader4.
        n = (n << 13) ^ n;
        return (n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff;
    }

    float noise3f(in vec3 vector)
    {
        ivec3 iVector = ivec3( floor(vector) );
        int n = iVector.x + iVector.y * 57 + iVector.z * 113;
        vec3 u = fract(vector);
        u = u * u * (3.0 - 2.0 * u); 

        float gradient = mix(mix(mix(float( LFSR_Rand_Gen( n + (0 + 57 * 0 + 113 * 0) ) ),
                                     float( LFSR_Rand_Gen( n + (1 + 57 * 0 + 113 * 0) ) ), u.x),
                                 mix(float( LFSR_Rand_Gen( n + (0 + 57 * 1 + 113 * 0) ) ),
                                     float( LFSR_Rand_Gen( n + (1 + 57 * 1 + 113 * 0) ) ), u.x), u.y),
                             mix(mix(float( LFSR_Rand_Gen( n + (0 + 57 * 0 + 113 * 1) ) ),
                                     float( LFSR_Rand_Gen( n + (1 + 57 * 0 + 113 * 1) ) ), u.x),
                                 mix(float( LFSR_Rand_Gen( n + (0 + 57 * 1 + 113 * 1) ) ),
                                     float( LFSR_Rand_Gen( n + (1 + 57 * 1 + 113 * 1) ) ), u.x), u.y), u.z);

        return (1.0 - gradient / 1073741824.0);
    }
#endif


float rand(in vec2 vector)
{
    #if GPU_SHADER4_COMPATIBILITY_CONDITION
        // <<, ^ and & require GL_EXT_gpu_shader4
        int n = int(vector.x * 40.0 + vector.y * 6400.0);
        n = (n << 13) ^ n;
        return 1.0 - float( (n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0;
    #else 
        return fract( 43758.5453 * sin( dot(vector, vec2(12.9898, 78.233) ) ) );
    #endif
}

vec4 applyMorphing(in float morphFactor, in float timer)
{
    #if GPU_SHADER4_COMPATIBILITY_CONDITION
        vec4 vertexMorph = vec4(mix(gl_Vertex.xyz, ( MORPH_DESTINATION_FACTOR + 0.25 * noise3f(timer * gl_Vertex.xyz / 8.0) ) * gl_Vertex.xyz, morphFactor), gl_Vertex.w);
    #else    
        vec4 vertexMorph = vec4(mix(gl_Vertex.xyz, ( MORPH_DESTINATION_FACTOR + 0.25 * rand( sin(timer * gl_Vertex.xy) ) )    * gl_Vertex.xyz, morphFactor), gl_Vertex.w);
    #endif 
    vec4 vertexCubeMorph = vertexMorph;
    vertexCubeMorph.xyz *= 4.0 / length(vertexCubeMorph.xyz);
    vertexCubeMorph.xyz = clamp(vertexCubeMorph.xyz, -MORPH_CUBE_SIZE, MORPH_CUBE_SIZE);

    return mix( vertexMorph, vertexCubeMorph, 0.9 * abs( sin(timer) ) );
}

vec2 applySphericalCoordinates(in vec3 position, in vec3 normal)
{
    vec3 r = reflect( normalize(position), normalize(normal) );
    // float m = 2.0 * sqrt( r.x * r.x + r.y * r.y + (r.z + 1.0) * (r.z + 1.0) );
    float m = 2.0 * length( vec3(r.xy, r.z + 1.0) );

    return vec2(r.x, r.y) / m + 0.5;
}