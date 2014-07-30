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

FS_VARYING vec2 coord;

uniform sampler2D spotCircleRandom2DTexture;

uniform float spotCircleTimer;
uniform float spotCircleTransparency;
uniform bool spotCircleOldLCDStyleTransparency;

uniform float spotCircleCenterX;
uniform float spotCircleCenterY;
uniform float spotCircleRadius;
uniform float spotCirclePreCalcAlphaValues;
uniform bool spotCircleWithNoiseEffect;

const vec4 OUTER_SPOT_CIRCLE_COLOR = vec4(0.0);

void applyOldStyleTransparency();

#if USE_SPOT_CIRCLE_RANDOM_2D_TEXTURE_CONDITION
#else
    float rand(vec2 vector);

    float rand(vec2 vector)
    {
        return fract( 43758.5453 * sin( dot(vector, vec2(12.9898, 78.233) ) ) );
    }
#endif

void main()
{
    if (spotCircleOldLCDStyleTransparency)
        applyOldStyleTransparency();

    float spotCircleDistance = distance(vec2(spotCircleCenterX, spotCircleCenterY), coord.xy);

    // using integer number checks for the anti-alias effect!
    int spotCircleDistanceInteger = int(spotCircleDistance);
    int spotCircleRadiusInteger = int(spotCircleRadius);
    if (spotCircleDistanceInteger < spotCircleRadiusInteger)
    {
        if (spotCircleWithNoiseEffect)
        {            
            #if USE_SPOT_CIRCLE_RANDOM_2D_TEXTURE_CONDITION
                vec4 allRandomColors = texture2D( spotCircleRandom2DTexture, vec2( fract( gl_TexCoord[0].st + fract(spotCircleTimer) ) ) );
            #else                                  
                vec4 allRandomColors = vec4( vec2( rand( vec2( fract( gl_TexCoord[0].st + fract(spotCircleTimer * 0.13 ) ) ) ) ),
                                             vec2( rand( vec2( fract( gl_TexCoord[0].st + fract(spotCircleTimer * 0.83 ) ) ) ) ) );
            #endif    
            vec3 randomColor = vec3( (allRandomColors.r + allRandomColors.g + allRandomColors.b + allRandomColors.a) / 4.0 );
            gl_FragColor = vec4(randomColor, spotCircleTransparency * spotCirclePreCalcAlphaValues);
        }
        else
            gl_FragColor = OUTER_SPOT_CIRCLE_COLOR;
    }
    else if (spotCircleDistanceInteger > spotCircleRadiusInteger)
        gl_FragColor = vec4(OUTER_SPOT_CIRCLE_COLOR.xyz, spotCircleTransparency);
    else // if (distanceInteger == spotCircleRadiusInteger) // for the anti-alias effect!
        gl_FragColor = vec4( OUTER_SPOT_CIRCLE_COLOR.xyz, spotCircleTransparency * fract(spotCircleDistance) );
}
