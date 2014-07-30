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

uniform sampler2D water2DTexture;
uniform sampler2D waterBuffer2DTexture;

uniform float waterTimer;
uniform float waterTransparency;
uniform bool waterOldLCDStyleTransparency;
uniform float waterSize;
uniform float waterPx;
uniform float waterPy;

const float DAMPING = 0.975;
const vec2 CENTER_POINT = vec2(0.5, 0.5);
const vec2 BOTTOM_RIGHT_POINT = vec2(0.0, 0.0);

void applyOldStyleTransparency();

void main()
{
    if (waterOldLCDStyleTransparency)
        applyOldStyleTransparency();

    vec2 tcoords = gl_TexCoord[0].st;
    vec2 tcoordsLightSpecular = 128.0 * (tcoords - CENTER_POINT);

    float dx = (1.0 / waterPx);
    float dy = (1.0 / waterPy);

    tcoords.s = tcoords.s - dx;
    vec4 heightLeft = texture2D(waterBuffer2DTexture, tcoords);

    tcoords.s = tcoords.s + 2.0 * dx;
    vec4 heightRight = texture2D(waterBuffer2DTexture, tcoords);

    tcoords.s = tcoords.s - dx;
    tcoords.t = tcoords.t + dy;
    vec4 heightUp = texture2D(waterBuffer2DTexture, tcoords);

    tcoords.t = tcoords.t - 2.0 * dy;
    vec4 heightDown = texture2D(waterBuffer2DTexture, tcoords);

    tcoords.t = tcoords.t + dy;

    float dispx = 0.125 * (heightLeft.a - heightRight.a);
    float dispy = 0.125 * (heightUp.a   - heightDown.a);

    tcoords.s = tcoords.s + dispx;
    tcoords.t = tcoords.t + dispy;    

    float lightSpecular = waterSize / distance(vec2( (waterPx / waterPy) * ( tcoordsLightSpecular.s + 48.0 * (sin(waterTimer) + 6.0 * dispx) ), tcoordsLightSpecular.t + 48.0 * (cos(waterTimer) + 6.0 * dispy) ), BOTTOM_RIGHT_POINT);

    vec4 color = texture2D(water2DTexture, tcoords);
    color.rgb *= lightSpecular;

    gl_FragColor = waterTransparency * clamp(color, 0.0, 1.0);
}
