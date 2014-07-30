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

 @ author, GLSL & OpenGL code author Thanos Theo, Michael Kargas, 2009-2010-2011-2012

*/

FS_VARYING vec2 coord;

uniform float plasmaTimer;
uniform float plasmaTransparency;
uniform bool plasmaOldLCDStyleTransparency;

void applyOldStyleTransparency();

void main()
{
    if (plasmaOldLCDStyleTransparency)
        applyOldStyleTransparency();

    float x = coord.x / 8.0;
    float y = coord.y / 6.0;

    float timer = plasmaTimer / 256.0;

    vec4 color = vec4(0.0, 0.0, 0.0, 1.0);
    color.r = (sin((x + timer) / 2.0) + sin(y / 3.0) + sin((x - timer) / 3.0 + sin((y - 2.0 * timer) / 2.0)) + sin((x + y) / 4.0 + sin((x - y + timer) / 3.0)) + 4.0) / 8.0;
    color.g = (sin(x / 3.0) + sin((y + timer) / 2.0) + sin(x / 4.0 + sin((y + 2.0 * timer) / 5.0)) + sin((x + y + 5.0 * timer) / 3.0 + sin((x - y - 2.0 * timer) / 6.0)) + 4.0) / 8.0;
    color.b = (sin((x + 3.0 * timer) / 3.0) + sin((y + timer) / 3.0) + sin(x / 3.0 + sin((y - 3.0 * timer) / 4.0)) + sin((x + y + 4.0 * timer) / 2.0 + sin((x - y) / 4.0)) + 4.0) / 8.0;

    gl_FragColor = plasmaTransparency * color;
}
