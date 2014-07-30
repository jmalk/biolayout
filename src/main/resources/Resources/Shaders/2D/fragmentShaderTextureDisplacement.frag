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

uniform sampler2D textureDisplacement2DTexture;

uniform float textureDisplacementTimer;
uniform float textureDisplacementTransparency;
uniform bool textureDisplacementOldLCDStyleTransparency;

const float DIVIDE_RATIO = 512.0;

void applyOldStyleTransparency();

void main()
{
    if (textureDisplacementOldLCDStyleTransparency)
        applyOldStyleTransparency();

    vec2 textureCoords = gl_TexCoord[0].st;
    float timer = textureDisplacementTimer / 4096.0;
    float sinTimer = sin(timer);

    textureCoords.s = textureCoords.s + 0.075 * sinTimer * sin( (coord.y / DIVIDE_RATIO + timer) * 16.0 );
    textureCoords.t = textureCoords.t + 0.125 * sinTimer * sin( (coord.x / DIVIDE_RATIO + 1.5 * timer) * 12.0 );

    vec4 color = textureDisplacementTransparency * texture2D(textureDisplacement2DTexture, textureCoords);

    gl_FragColor = color;
}
