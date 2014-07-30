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

uniform sampler2D radialBlur2DTexture;

uniform float radialBlurTimer;
uniform float radialBlurTransparency;
uniform bool radialBlurOldLCDStyleTransparency;
uniform float radialBlurSize;
uniform float radialBlurPower;

const float N = 64.0;
const vec2 CENTER_POINT = vec2(0.5, 0.5);

void applyOldStyleTransparency();

void main()
{
    if (radialBlurOldLCDStyleTransparency)
        applyOldStyleTransparency();

    vec2 centeredTexCoord = gl_TexCoord[0].st - CENTER_POINT;
    vec2 lightCoord = radialBlurSize * vec2(0.5 * sin(radialBlurTimer), -0.2);

    vec4 sum = vec4(0.0);
    vec2 sampleTexCoord;
    vec2 displacement;
    for (float i = 0.0; i < N; i++)
    {
        sampleTexCoord = vec2(mix(centeredTexCoord, (centeredTexCoord - lightCoord) * (1.0 - radialBlurSize), i / (N - 1.0)));
        displacement = (sampleTexCoord - centeredTexCoord);
        sum += texture2D(radialBlur2DTexture, gl_TexCoord[0].st + displacement) * radialBlurPower;
    }

    gl_FragColor = radialBlurTransparency * (sum / N);
}
