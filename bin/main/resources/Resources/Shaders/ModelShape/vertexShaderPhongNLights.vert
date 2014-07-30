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

 @ author, GLSL & OpenGL code author Thanos Theo, 2011-2012

*/

VS_VARYING vec3 VS_EYE_VECTOR;
VS_VARYING vec3 VS_NORMAL;

uniform bool texturing;
uniform bool sphericalMapping;

vec2 applySphericalCoordinates(in vec3, in vec3);

void main()
{
    vec3 position = vec3(gl_ModelViewMatrix * gl_Vertex);
    VS_EYE_VECTOR = -position;
    VS_NORMAL = gl_NormalMatrix * gl_Normal;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

    if (texturing)
        gl_TexCoord[0] = (sphericalMapping) ? vec4(applySphericalCoordinates(position, VS_NORMAL), (gl_TextureMatrix[0] * gl_MultiTexCoord0).pq) : gl_TextureMatrix[0] * gl_MultiTexCoord0;
}

vec2 applySphericalCoordinates(in vec3 position, in vec3 VS_NORMAL)
{
    vec3 r = reflect( normalize(position), normalize(VS_NORMAL) );
    float m = 2.0 * sqrt( r.x * r.x + r.y * r.y + (r.z + 1.0) * (r.z + 1.0) );

    return vec2(r.x, r.y) / m + 0.5;
}