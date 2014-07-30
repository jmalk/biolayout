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

uniform sampler2D blur2DTexture;

uniform float blurTransparency;
uniform bool blurOldLCDStyleTransparency;
uniform int blurState;
uniform bool blurInterpolation;
uniform float blurSize;

const float N = 10.0;
const float N_HALF = N / 2.0;

void applyOldStyleTransparency();

void main()
{
    if (blurOldLCDStyleTransparency)
        applyOldStyleTransparency();

    float localBlurSize = 0.001 * blurSize;

    vec2 tcoordsBlur;
    vec4 blur2DTextureColor, colorSum = vec4(0.0, 0.0, 0.0, 0.0);
    float blurValue = 0.0;
    float blurDivideRation = 0.0;

    if (blurState == 0)
    {
        for (float x = -N_HALF; x <= N_HALF; x++)
        {
            blurValue = (localBlurSize * x);
            tcoordsBlur.s = gl_TexCoord[0].s + blurValue;
            tcoordsBlur.t = gl_TexCoord[0].t;

            blur2DTextureColor = texture2D(blur2DTexture, tcoordsBlur);
            colorSum += (blurInterpolation) ? mix(blur2DTextureColor, colorSum, blurValue) : blur2DTextureColor;
        }

        blurDivideRation = N;
    }
    else if (blurState == 1)
    {
        for (float y = -N_HALF; y <= N_HALF; y++)
        {
            blurValue = (localBlurSize * y);
            tcoordsBlur.s = gl_TexCoord[0].s;
            tcoordsBlur.t = gl_TexCoord[0].t + blurValue;

            blur2DTextureColor = texture2D(blur2DTexture, tcoordsBlur);
            colorSum += (blurInterpolation) ? mix(blur2DTextureColor, colorSum, blurValue) : blur2DTextureColor;
        }

        blurDivideRation = N;
    }
    else // if (blurState == 2)
    {
        for (float y = -N_HALF; y <= N_HALF; y++)
        {
            for (float x = -N_HALF; x <= N_HALF; x++)
            {
                tcoordsBlur.s = gl_TexCoord[0].s + (localBlurSize * x);
                tcoordsBlur.t = gl_TexCoord[0].t + (localBlurSize * y);

                blur2DTextureColor = texture2D(blur2DTexture, tcoordsBlur);
                colorSum += (blurInterpolation) ? mix(blur2DTextureColor, colorSum, blurValue) : blur2DTextureColor;
            }
        }

        blurDivideRation = N * N;
    }

    gl_FragColor = blurTransparency * (colorSum / blurDivideRation);
}
