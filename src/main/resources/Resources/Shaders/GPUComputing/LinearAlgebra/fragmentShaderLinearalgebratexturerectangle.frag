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

uniform sampler2DRect textureX;
uniform sampler2DRect textureY;
uniform float alpha;

void main()
{
    vec4 x = texture2DRect(textureX, gl_TexCoord[0].st);
    vec4 y = texture2DRect(textureY, gl_TexCoord[0].st);
    
    // int index_X = int(gl_TexCoord[0].s - 0.5 + 0.01);
    // int index_Y = int(gl_TexCoord[0].t - 0.5 + 0.01);
    // gl_FragColor = vec4(index_X, index_Y, index_X, index_Y);

    gl_FragColor = y + alpha * x;
}