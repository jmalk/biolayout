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

VS_VARYING vec4 sceneColor;

uniform float animationPx;
uniform float animationPy;

// animation related GPU Computing variables
uniform bool AnimationGPUComputingMode;
float animationNodeSizeRatio = 1.0;
vec4 color;

vec4 applyAnimationGPUComputing(in vec4);

void main()
{
    sceneColor = gl_Color;
    if (AnimationGPUComputingMode)
    {
        vec4 vertex = applyAnimationGPUComputing(sceneColor);
        sceneColor.rgb = color.rgb;
        vec4 centerPoint = vec4( (animationNodeSizeRatio * animationPx) / 2.0, (animationNodeSizeRatio * animationPy) / 2.0, 0.0, 0.0 );
        gl_Position = gl_ModelViewProjectionMatrix * (vertex - centerPoint);
    }
    else
        gl_Position = ftransform();
    gl_TexCoord[0] = gl_TextureMatrix[0] * gl_MultiTexCoord0;    
}