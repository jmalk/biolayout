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

 @ author, GLSL & OpenGL code author Thanos Theo, Michael Kargas, 2012

*/

uniform sampler2D blobStars3dScroller2DTexture;

uniform float blobStars3dScrollerTimer;
uniform float blobStars3dScrollerTransparency;
uniform bool blobStars3dScrollerOldLCDStyleTransparency;
uniform int blobStars3dScrollerState;
uniform float blobStars3dScrollerPx;
uniform float blobStars3dScrollerPy;
uniform vec2 blobStars3dScrollerMouseMove;

// blob stars 3d scroller uniform variables
uniform float blobStars3dScrollerBlobWidth;
uniform float blobStars3dScrollerBlobHeight;
uniform float blobStars3dScrollerBlobScaleSize;
uniform float blobStars3dScrollerBlobHaloExponent;
uniform float blobStars3dScrollerScSize;
uniform float blobStars3dScrollerStarDistanceZ;
uniform float blobStars3dScrollerNumberOf3DStars;
uniform vec3 blobStars3dScrollerBlobColor;
uniform float blobStars3dScrollerBlobMotionBlur;

const vec2 CENTER_POINT = vec2(0.5, 0.5);

void applyOldStyleTransparency();

void main()
{
    if (blobStars3dScrollerOldLCDStyleTransparency)
        applyOldStyleTransparency();

    float timer = blobStars3dScrollerTimer / 256.0;    
    vec2 mouseMove = blobStars3dScrollerMouseMove / 50000.0f;    
    vec2 centeredCoords = vec2( gl_TexCoord[0].s - CENTER_POINT.x - 2.0 * mouseMove.x, (gl_TexCoord[0].t - CENTER_POINT.y + 2.5 * mouseMove.y) * (blobStars3dScrollerPy / blobStars3dScrollerPx) );
    // centeredCoords.x = -( centeredCoords.x * cos(timer) + centeredCoords.y * sin(timer));
    // centeredCoords.y = -(-centeredCoords.x * sin(timer) + centeredCoords.y * cos(timer));
    
    float gradient = 0.0;
    float fade = 0.0;
    float z = 0.0;
    vec2 starPosition;
    vec2 blobCoord;
    vec2 blobDimension = vec2(blobStars3dScrollerBlobWidth, blobStars3dScrollerBlobHeight) / 64.0;
    for (float i = 1.0; i <= blobStars3dScrollerNumberOf3DStars; i++)
    {
            starPosition = vec2( sin(i) * (blobStars3dScrollerScSize / 8.0), sin(i * i * 0.75) * (blobStars3dScrollerScSize / 8.0) );
            z = mod(i * i * i * 0.83 - 512.0 * timer, blobStars3dScrollerStarDistanceZ);
            fade = (blobStars3dScrollerStarDistanceZ - z) / blobStars3dScrollerStarDistanceZ;
            blobCoord = starPosition / z;
            gradient += ( ( fade * (blobStars3dScrollerBlobScaleSize / 131072.0f) ) / pow(length( (centeredCoords - blobCoord) / blobDimension ), 2.0) ) * (fade * fade);
    }

    if ( (gradient <= 0.0) && (blobStars3dScrollerState != 1) )
        discard;
    else
    {    
        gradient = clamp(gradient, 0.0, 1.0);
        vec4 color = blobStars3dScrollerTransparency * vec4(gradient * blobStars3dScrollerBlobColor, pow(gradient, blobStars3dScrollerBlobHaloExponent));
        gl_FragColor = (blobStars3dScrollerState == 1) ? mix(color, texture2D(blobStars3dScroller2DTexture, gl_TexCoord[0].st), blobStars3dScrollerBlobMotionBlur) // for motion blur of blob 3D stars using ping-pong FBOs
                                                       : color;
    }
}
