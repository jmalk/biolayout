/*

 BioLayoutExpress3D - A tool for visualisation
 and analysis of biological networks

 Copyright (c) 2006-2012 Genome Research Ltd.
 Authors: Thanos Theo, Anton Enright, Leon Goldovsky, Ildefonso Cases, Markus Brosch, Stijn van Dongen, Michael Kargas, Benjamin Boyer and Tom Freeman
 Contact: support@biolayout.org

 This program iS free software; you can redistribute iT and/or
 modify iT under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program iS distributed in the hope that iT will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

 @ author, GLSL & OpenGL code author Thanos Theo, 2012

*/

#extension GL_EXT_geometry_shader4 : enable

GS_VARYING in  vec3 VS_GS_POSITION[];
GS_VARYING in  vec3 VS_GS_MC_POSITION[];
GS_VARYING in  vec3 VS_GS_NORMAL[];
GS_VARYING in  vec4 VS_GS_SCENE_COLOR[];
GS_VARYING in  vec2 VS_GS_TEX_COORD[];
GS_VARYING in float VS_GS_V[];

GS_VARYING out  vec3 fsPosition;
GS_VARYING out  vec3 fsMCPosition;
GS_VARYING out  vec3 fsNormal;
GS_VARYING out  vec4 fsSceneColor;
GS_VARYING out  vec2 fsTexCoord;
GS_VARYING out float fsV;
GS_VARYING out  vec3 fsTriangleDistances;

const vec3 ZERO_VECTOR = vec3(0.0);
const vec4 UNIT_NORMAL_COLOR_RED = vec4(1.0, 0.0, 0.0, 1.0);

const int SPHERE_SUBDIVISION_LEVEL = 2; // ranging 0-2; could go more than 2, but the GS vertex output limits this
const float SPHERE_SUBDIVISION_RADIUS = 2.0;

void applyTriangleGeometry(in bool, in float, in bool, in bool);
void applyNormalsGeometry();
void applySphereSubdivision();
int applySphereSubdivisionLevel();
void produceVertex(in float, in float, in vec3, in vec3, in vec3);

// implied from OpenGL 4.0+ specs/ any bigger value slows down badly
// layout(triangles, invocations = 1) in;

void applyTriangleGeometry(in bool useShrinkFactor, in float shrinkFactor, in bool useUserClipping, in bool useTexturing)
{
    vec3 triangleCentroid = (useShrinkFactor) ? (gl_PositionIn[0].xyz + gl_PositionIn[1].xyz + gl_PositionIn[2].xyz) / 3.0 : vec3(0.0);
    for (int i = 0; i < gl_VerticesIn; i++)
    {
        fsPosition   = VS_GS_POSITION[i];
        fsMCPosition = VS_GS_MC_POSITION[i];
        fsNormal     = VS_GS_NORMAL[i];
        fsSceneColor = VS_GS_SCENE_COLOR[i];
        fsTexCoord   = VS_GS_TEX_COORD[i];
        fsV          = VS_GS_V[i];
        fsTriangleDistances    = ZERO_VECTOR;
        fsTriangleDistances[i] = 1.0;

        gl_Position = gl_ModelViewProjectionMatrix * ( (useShrinkFactor) ? vec4(triangleCentroid + shrinkFactor * (gl_PositionIn[i].xyz - triangleCentroid), gl_PositionIn[i].w)
                                                                         : gl_PositionIn[i] );
        if (useUserClipping)
            gl_ClipVertex = gl_ClipVertexIn[i];
        // gl_PointSize = gl_PointSizeIn[i]; // GL_POINTS will never be used with Geometry Shaders, only triangles
        if (useTexturing)
            gl_TexCoord[0] = gl_TexCoordIn[i][0];
        EmitVertex();
    }
    EndPrimitive(); 
}

void applyNormalsGeometry()
{
    float intensity;
    vec4 position;
    for (int i = 0; i < gl_VerticesIn; i++)
    {
        fsSceneColor = UNIT_NORMAL_COLOR_RED;

        intensity = length( normalize(VS_GS_NORMAL[i]) ) / 10.0;
        position = gl_ModelViewProjectionMatrix * vec4( (1.0 + intensity) * gl_PositionIn[i].xyz, gl_PositionIn[i].w );

        for (int j = 0; j < gl_VerticesIn; j++)
        {
            gl_Position = (j == 0) ? gl_ModelViewProjectionMatrix * gl_PositionIn[i] : position;
            EmitVertex();
        }
        EndPrimitive();
    }
}

void applySphereSubdivision()
{
    vec3 v0  =  gl_PositionIn[0].xyz;
    vec3 v01 = (gl_PositionIn[1] - gl_PositionIn[0]).xyz;
    vec3 v02 = (gl_PositionIn[2] - gl_PositionIn[0]).xyz;

    int numLayers = 1 << applySphereSubdivisionLevel(); //SPHERE_SUBDIVISION_LEVEL;
    float dt = 1.0 / float(numLayers);
    float tTop = 1.0;
    float tBottom;
    float sMaxTop;
    float sMaxBottom;
    int numbers;
    float dsTop;
    float dsBottom;
    float sTop;
    float sBottom;
    for(int iT = 0; iT < numLayers; iT++)
    {
        tBottom    = tTop - dt;
        sMaxTop    = 1.0 - tTop;
        sMaxBottom = 1.0 - tBottom;

        numbers  = iT + 1;
        dsTop    = sMaxTop    / float(numbers - 1);
        dsBottom = sMaxBottom / float(numbers);

        sTop = 0.0;
        sBottom = 0.0;

        for(int iS = 0; iS < numbers; iS++)
        {
            produceVertex(sBottom, tBottom, v0, v01, v02);
            produceVertex(sTop, tTop, v0, v01, v02);
            sTop    += dsTop;
            sBottom += dsBottom;
        }

        produceVertex(sBottom, tBottom, v0, v01, v02);
        EndPrimitive();

        tTop = tBottom;
        tBottom -= dt;        
    }
}

int applySphereSubdivisionLevel()
{
    // calculate the centroid of the triangle
    vec3 triangleCentroid = (gl_PositionIn[0].xyz + gl_PositionIn[1].xyz + gl_PositionIn[2].xyz) / 3.0;

    // get the extreme points of the centroid
    vec4 mx = vec4(triangleCentroid - vec3(SPHERE_SUBDIVISION_RADIUS, 0.0, 0.0), 1.0);
    vec4 px = vec4(triangleCentroid + vec3(SPHERE_SUBDIVISION_RADIUS, 0.0, 0.0), 1.0);
    vec4 my = vec4(triangleCentroid - vec3(0.0, SPHERE_SUBDIVISION_RADIUS, 0.0), 1.0);
    vec4 py = vec4(triangleCentroid + vec3(0.0, SPHERE_SUBDIVISION_RADIUS, 0.0), 1.0);
    vec4 mz = vec4(triangleCentroid - vec3(0.0, 0.0, SPHERE_SUBDIVISION_RADIUS), 1.0);
    vec4 pz = vec4(triangleCentroid + vec3(0.0, 0.0, SPHERE_SUBDIVISION_RADIUS), 1.0);

    // get the extreme points in clip space
    mx = gl_ModelViewProjectionMatrix * mx;
    px = gl_ModelViewProjectionMatrix * px;
    my = gl_ModelViewProjectionMatrix * my;
    py = gl_ModelViewProjectionMatrix * py;
    mz = gl_ModelViewProjectionMatrix * mz;
    pz = gl_ModelViewProjectionMatrix * pz;

    // get the extreme points in NDC space
    mx.xy /= mx.w;
    px.xy /= px.w;
    my.xy /= my.w;
    py.xy /= py.w;
    mz.xy /= mz.w;
    pz.xy /= pz.w;

    // how much NDC do the extreme points subtend?
    float dx = distance(mx.xy, px.xy);
    float dy = distance(my.xy, py.xy);
    float dz = distance(mz.xy, pz.xy);
    float dmax = length( vec3(dx, dy, dz) ); // sqrt(dx * dx + dy * dy + dz * dz);
    
    if (dmax <= 0.25)
        return 0;
    else if ( (dmax > 0.25) && (dmax <= 1.25) )
        return 1;
    else // if ( (dmax > 1.25) && (dmax <= 2.25) )
        return 2;
}

void produceVertex(in float s, in float t, in vec3 v0, in vec3 v01, in vec3 v02)
{
    vec3 v = normalize(v0 + s * v01 + t * v02);
    vec4 newVertex = vec4(SPHERE_SUBDIVISION_RADIUS * v, 1.0);

    fsPosition   = vec3(gl_ModelViewMatrix * newVertex);
    fsMCPosition = vec3(newVertex);
    fsNormal     = normalize(gl_NormalMatrix * v);
    fsSceneColor = normalize(VS_GS_SCENE_COLOR[0] + s * VS_GS_SCENE_COLOR[1] + t * VS_GS_SCENE_COLOR[2]);
    fsTexCoord   = normalize(VS_GS_TEX_COORD[0]   + s * VS_GS_TEX_COORD[1]   + t * VS_GS_TEX_COORD[2]);
    fsV          = normalize(VS_GS_V[0]           + s * VS_GS_V[1]           + t * VS_GS_V[2]);
    fsTriangleDistances             = ZERO_VECTOR;
    fsTriangleDistances[int(s + t)] = 1.0;

    gl_Position = gl_ModelViewProjectionMatrix * newVertex;
    EmitVertex();   
}