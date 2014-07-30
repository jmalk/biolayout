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

in vec4 lineColor;
in vec3 fsTriangleDistances;

vec4 applySolidWireFrame(in vec4, in float);
float amplify(in float, in float, in float);

void main()
{
    gl_FragColor = applySolidWireFrame(lineColor, 1.5);
}

vec4 applySolidWireFrame(in vec4 originalColor, in float lineWidth)
{
    // compute the shortest distance between the fragment and the edges
    float minDistance = min(min(fsTriangleDistances.x, fsTriangleDistances.y), fsTriangleDistances.z);
    // cull fragments after a certain distance
    if (minDistance > 0.05)
        discard;        

    // gradient is computed from the function exp2(-2(x)^2)
    lineWidth = 150.0 / (lineWidth / gl_FragCoord.w);
    if (lineWidth < 10) 
        lineWidth = 10;
    float gradient = 1.0 - amplify(minDistance, lineWidth, -0.5);
    return vec4(gradient * originalColor.rgb, 1.0);
}

float amplify(in float d, in float scale, in float offset)
{
    d = scale * d + offset;
    d = clamp(d, 0.0, 1.0);

    return 1.0 - exp2(-2.0 * d * d);
}