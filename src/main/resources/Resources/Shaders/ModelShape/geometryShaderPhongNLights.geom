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

 @ author, GLSL & OpenGL code author Thanos Theo, 2012

*/

#extension GL_EXT_geometry_shader4 : enable

GS_VARYING in vec3 GS_VS_EYE_VECTOR[];
GS_VARYING in vec3 GS_VS_NORMAL[];

GS_VARYING out vec3 GS_FS_EYE_VECTOR;
GS_VARYING out vec3 GS_FS_NORMAL;
GS_VARYING out vec4 GS_FS_COLOR;

uniform bool shrinkTriangles;
uniform bool texturing;
uniform bool normals;

const vec4 UNIT_NORMAL_COLOR_RED  = vec4(1.0, 0.0, 0.0, 0.0);
const vec4 UNIT_NORMAL_COLOR_ZERO = vec4(0.0, 0.0, 0.0, 0.0);

void applyTriangleGeometry(in bool, in float, in bool);
void applyNormalsGeometry();

void main()
{
    applyTriangleGeometry(shrinkTriangles, 0.95, texturing);
    
    if (normals)
        applyNormalsGeometry();    
}

void applyTriangleGeometry(in bool useShrinkFactor, in float shrinkFactor, in bool useTexturing)
{
    vec3 triangleCentroid = (useShrinkFactor) ? (gl_PositionIn[0].xyz + gl_PositionIn[1].xyz + gl_PositionIn[2].xyz) / 3.0 : vec3(0.0);
    for (int i = 0; i < gl_VerticesIn; i++)
    {
        GS_FS_EYE_VECTOR = GS_VS_EYE_VECTOR[i];
        GS_FS_NORMAL     = GS_VS_NORMAL[i];
        GS_FS_COLOR      = UNIT_NORMAL_COLOR_ZERO;

        gl_Position = gl_ModelViewProjectionMatrix * ( (useShrinkFactor) ? vec4(triangleCentroid + shrinkFactor * (gl_PositionIn[i].xyz - triangleCentroid), gl_PositionIn[i].w)
                                                                         : gl_PositionIn[i] );
        // gl_ClipVertex = gl_ClipVertexIn[i]; // user clipping will never be used with Geometry Shaders
        // gl_PointSize = gl_PointSizeIn[i];   // GL_POINTS will never be used with Geometry Shaders, only triangles
        if (useTexturing)
            gl_TexCoord[0] = gl_TexCoordIn[i][0];
        EmitVertex();
    }
    EndPrimitive(); 
}

void applyNormalsGeometry()
{
    // Show unit normals
    float intensity;
    vec4 position;
    for (int i = 0; i < gl_VerticesIn; i++) 
    {
        GS_FS_COLOR = UNIT_NORMAL_COLOR_RED;

        intensity = length( normalize(GS_VS_NORMAL[i]) ) / 10.0;
        position = gl_ModelViewProjectionMatrix * vec4( (1.0 + intensity) * gl_PositionIn[i].xyz, gl_PositionIn[i].w );

        for (int j = 0; j < gl_VerticesIn; j++)
        {
            gl_Position = (j == 0) ? gl_ModelViewProjectionMatrix * gl_PositionIn[i] : position;
            EmitVertex();
        }
        EndPrimitive();
    }
}