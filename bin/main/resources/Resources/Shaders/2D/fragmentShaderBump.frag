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

uniform sampler2D bump2DTexture;
uniform sampler2D bumpEmboss2DTexture;

uniform float bumpTimer;
uniform float bumpTransparency;
uniform bool bumpOldLCDStyleTransparency;
uniform int bumpState;
uniform float bumpSize;
uniform float bumpPx;
uniform float bumpPy;

const vec4 LIGHT_SPECULAR_COLOR = vec4(1.0, 1.0, 1.0, 1.0); // white specular color
const vec2 CENTER_POINT = vec2(0.5, 0.5);
const vec2 BOTTOM_RIGHT_POINT = vec2(0.0, 0.0);

void applyOldStyleTransparency();

void main()
{
    if (bumpOldLCDStyleTransparency)
        applyOldStyleTransparency();

    vec2 tcoords = gl_TexCoord[0].st;
    vec2 tcoordsLightSpecular = 128.0 * vec2(tcoords - CENTER_POINT);

    vec4 bump2DTextureColor = (bumpState == 0) ? texture2D(bumpEmboss2DTexture, tcoords) : texture2D(bump2DTexture, tcoords);
    if (bump2DTextureColor.a == 0.0) discard;
    bump2DTextureColor.rgb *= ( 0.5 + 0.45 * sin(bumpTimer) );

    float neighborFragmentX = (1.0 / bumpPx);
    float neighborFragmentY = (1.0 / bumpPy);

    tcoords.s -= neighborFragmentX;
    float neighborLeft =  texture2D(bumpEmboss2DTexture, tcoords).a;

    tcoords.s += 2.0 * neighborFragmentX;
    float neighborRight = texture2D(bumpEmboss2DTexture, tcoords).a;

    tcoords.s -= neighborFragmentX;
    tcoords.t += neighborFragmentY;
    float neighborUp = texture2D(bumpEmboss2DTexture, tcoords).a;

    tcoords.t -= 2.0 * neighborFragmentY;
    float neighborDown = texture2D(bumpEmboss2DTexture, tcoords).a;

    float dx = 0.75 * (neighborRight - neighborLeft);
    float dy = 0.75 * (neighborUp    - neighborDown);

    float lightSpecular = bumpSize / distance(vec2( (bumpPx / bumpPy) * ( tcoordsLightSpecular.s + 48.0 * (sin(bumpTimer) + dx) ), tcoordsLightSpecular.t + 48.0 * (cos(bumpTimer) + dy) ), BOTTOM_RIGHT_POINT);

    gl_FragColor = bumpTransparency * mix(bump2DTextureColor, LIGHT_SPECULAR_COLOR, lightSpecular);
}
