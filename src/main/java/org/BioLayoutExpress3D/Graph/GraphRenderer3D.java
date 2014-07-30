package org.BioLayoutExpress3D.Graph;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;
import static java.lang.Math.*;
import javax.swing.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.jogamp.opengl.util.awt.ImageUtil;
import javax.imageio.ImageIO;
import static javax.media.opengl.GL2.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.DataStructures.*;
import org.BioLayoutExpress3D.GPUComputing.GLSL.*;
import org.BioLayoutExpress3D.GPUComputing.GLSL.Animation.*;
import org.BioLayoutExpress3D.Graph.ActiveRendering.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.Models.*;
import org.BioLayoutExpress3D.Models.Lathe3D.*;
import org.BioLayoutExpress3D.Models.SuperQuadric.*;
import org.BioLayoutExpress3D.Models.Loaders.OBJModelLoader.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import org.BioLayoutExpress3D.Textures.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Network.NetworkContainer.*;
import static org.BioLayoutExpress3D.Graph.Graph.*;
import static org.BioLayoutExpress3D.Graph.GraphRendererCommonVariables.*;
import static org.BioLayoutExpress3D.Graph.GraphRendererCommonFinalVariables.*;
import static org.BioLayoutExpress3D.Graph.GraphRenderer3DFinalVariables.*;
import static org.BioLayoutExpress3D.Graph.Camera.GraphCameraFinalVariables.*;
import static org.BioLayoutExpress3D.Models.Lathe3D.Lathe3DShapeAngleIncrements.*;
import static org.BioLayoutExpress3D.Models.ModelRenderingStates.*;
import static org.BioLayoutExpress3D.Network.GraphmlNetworkmEPN3DShapesDefinitions.*;
import static org.BioLayoutExpress3D.StaticLibraries.EnumUtils.*;
import static org.BioLayoutExpress3D.StaticLibraries.ImageProducer.*;
import static org.BioLayoutExpress3D.Environment.AnimationEnvironment.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.Shapes3D.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The GraphRenderer3D class is the main 3D OpenGL renderer class of BioLayoutExpress3D.
*
* @see org.BioLayoutExpress3D.Textures.Glyphbombing3DTexture
* @see org.BioLayoutExpress3D.Textures.PerlinNoise3DTexture
* @see org.BioLayoutExpress3D.Textures.ShaderLightingSFXs
* @see javax.media.opengl.GLCanvas
* @see org.BioLayoutExpress3D.Graph.Graph
* @see org.BioLayoutExpress3D.Graph.GraphRendererCommonVariables
* @see org.BioLayoutExpress3D.Graph.GraphRendererCommonFinalVariables
* @see org.BioLayoutExpress3D.Graph.GraphRenderer3DFinalVariables
* @author Anton Enright, full rewrite/OpenGL 2.1 and above support/GLSL Advanced Shaders support by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

final class GraphRenderer3D implements GraphInterface, TileRendererBase.TileRendererListener // package access
{

    // Node texture related variables
    private Texture nodeTexture = null;
    private RenderToTexture renderToTexture = null;
    private ShaderLightingSFXs shaderSFXsCurrentReference = null;
    private ShaderLightingSFXs shaderLightingSFXsNodes = null;
    private ShaderLightingSFXs shaderLightingSFXsSelectedNodes = null;
    private ShaderLightingSFXs shaderLightingSFXsSelectedNodesNormalsGeometry = null;
    // private ShaderLinesSFXs shaderLinesSFXs = null;
    private boolean enableDisableNodeTexture = false;

    // Rotation / Depth related variables
    private float xzRotate = 0.0f;
    private float yzRotate = 0.0f;
    private float xwRotate = 0.0f;
    private float ywRotate = 0.0f;
    private float scaleValue = DEFAULT_SCALE;
    private float translateDX = 0.0f;
    private float translateDY = 0.0f;

    // Current view matrix transforms all points according to current state of rotation/translation
    private Matrix5D currentViewMatrix = null;

    private boolean autoRotate = false;
    private boolean autoPulsate = false;

    private int pulseSteps = 0;
    private boolean pulsateValue = false;
    private Shapes3D current3DShape = SPHERE;
    private float morphingValue = 0.0f;

    /**
    *  Auxiliary variable to be used with yEd specific change-of rendering.
    */
    private boolean prevYEdStyleRrenderingForGraphmlFiles = false;

    // Picking related variables
    private boolean pickFind = false;
    private boolean pickAdd = false;
    private boolean selectBox = false;

    private boolean isAutoRendering = false;

    // Mouse input related variables
    private int mouseButton = 0;
    private int mouseDragStartX = 0;
    private int mouseDragStartY = 0;
    private int mouseLastX = 0;
    private int mouseLastY = 0;

    private boolean rotating = false;
    private boolean pickingBox = false;

    private ShaderLightingSFXs.ShaderTypes currentShaderType = ShaderLightingSFXs.ShaderTypes.PHONG;
    private ModelShape geneShape = null;
    private ModelShape objModelLoaderShape = null;
    private Graph graph = null;

    /**
    *  The GraphRenderer3D class constructor.
    */
    public GraphRenderer3D(Graph graph)
    {
    	this.graph = graph;

	Matrix5D newMatrix5D = new Matrix5D();
	newMatrix5D.setIdentity();
	this.currentViewMatrix = newMatrix5D;
    }

    /**
    *  Enables the lighting shaders.
    */
    private void enableShaders(GL2 gl, boolean isNodesShading)
    {
        enableShaders(gl, isNodesShading, false, false, 0.0f, false, 0.0f);
    }

    /**
    *  Enables the lighting shaders.
    *  1st overloaded method for enforcing the Voronoi shader.
    */
    private void enableShaders(GL2 gl, boolean isNodesShading, boolean enforceVoronoiBlobsShader)
    {
        enableShaders(gl, isNodesShading, enforceVoronoiBlobsShader, false, 0.0f, false, 0.0f);
    }

    /**
    *  Enables the lighting shaders.
    *  2nd overloaded method for passing the animation parameters.
    */
    private void enableShaders(GL2 gl,  boolean isNodesShading, boolean enforceVoronoiBlobsShader, boolean enableAnimationGPUComputing, float nodeValue, boolean processNextNodeValue, float nextNodeValue)
    {
        if ( USE_SHADERS_PROCESS && MATERIAL_SPECULAR.get() )
        {
            float px = checkForNodeTexturing() ? ( ( !SHOW_3D_ENVIRONMENT_MAPPING.get() ) ? nodeTexture.getImageWidth() : renderToTexture.getWidth() ) : 1.0f;
            float py = checkForNodeTexturing() ? ( ( !SHOW_3D_ENVIRONMENT_MAPPING.get() ) ? nodeTexture.getImageHeight() : renderToTexture.getHeight() ) : 1.0f;

            shaderSFXsCurrentReference = shaderLightingSFXsNodes;
            if (!isAutoRendering)
            {
                if ( MATERIAL_ANIMATED_SHADING.get() )
                    shaderSFXsCurrentReference.timerEffect( ALL_SHADING_SFXS[ShaderLightingSFXs.ShaderTypes.WATER.ordinal()].get() );
                else
                    shaderSFXsCurrentReference.resetTimerEffect();
            }

            if (!enforceVoronoiBlobsShader)
            {
                ShaderLightingSFXs.ShaderTypes[] shaderTypes = ShaderLightingSFXs.ShaderTypes.values();
                for (int i = 0; i < ShaderLightingSFXs.NUMBER_OF_AVAILABLE_SHADERS; i++)
                {
                    if ( ALL_SHADING_SFXS[i].get() )
                    {
                        if (!enableAnimationGPUComputing)
                            shaderSFXsCurrentReference.useShaderLightingSFX(gl, shaderTypes[i], checkForNodeTexturing() && isNodesShading, MATERIAL_SPHERICAL_MAPPING.get() || SHOW_3D_ENVIRONMENT_MAPPING.get(), MATERIAL_EMBOSS_NODE_TEXTURE.get(), DEPTH_FOG.get(), morphingValue, false, MATERIAL_ANTIALIAS_SHADING.get(), MATERIAL_STATE_SHADING.get(), MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get(), MATERIAL_EROSION_SHADING.get() && isNodesShading, !WIREFRAME_SELECTION_MODE.get() && !isNodesShading, WIREFRAME_SELECTION_MODE.get() && !isNodesShading, MATERIAL_NORMALS_SELECTION_MODE.get() && !isNodesShading, px, py);
                        else
                            shaderSFXsCurrentReference.useShaderLightingSFX(gl, shaderTypes[i], checkForNodeTexturing() && isNodesShading, MATERIAL_SPHERICAL_MAPPING.get() || SHOW_3D_ENVIRONMENT_MAPPING.get(), MATERIAL_EMBOSS_NODE_TEXTURE.get(), DEPTH_FOG.get(), morphingValue, false, MATERIAL_ANTIALIAS_SHADING.get(), MATERIAL_STATE_SHADING.get(), MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get(), MATERIAL_EROSION_SHADING.get() && isNodesShading, !WIREFRAME_SELECTION_MODE.get() && !isNodesShading, WIREFRAME_SELECTION_MODE.get() && !isNodesShading, MATERIAL_NORMALS_SELECTION_MODE.get() && !isNodesShading, px, py, nodeValue, processNextNodeValue, nextNodeValue, animationFrameCount);
                    }
                }
            }
            else
                shaderSFXsCurrentReference.useShaderLightingSFX(gl, ShaderLightingSFXs.ShaderTypes.VORONOI, checkForNodeTexturing() && isNodesShading, MATERIAL_SPHERICAL_MAPPING.get() || SHOW_3D_ENVIRONMENT_MAPPING.get(), MATERIAL_EMBOSS_NODE_TEXTURE.get(), DEPTH_FOG.get(), morphingValue, false, MATERIAL_ANTIALIAS_SHADING.get(), true, MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get(), MATERIAL_EROSION_SHADING.get() && isNodesShading, !WIREFRAME_SELECTION_MODE.get() && !isNodesShading, WIREFRAME_SELECTION_MODE.get() && !isNodesShading, MATERIAL_NORMALS_SELECTION_MODE.get() && !isNodesShading, px, py);
        }
    }

    /**
    *  Disables the lighting shaders.
    */
    private void disableShaders(GL2 gl)
    {
        if ( USE_SHADERS_PROCESS && MATERIAL_SPECULAR.get() ) shaderSFXsCurrentReference.disableShaders(gl);
    }

    /**
    *  Renders the 3D OpenGL scene.
    */
    private void renderScene3D(GL2 gl, boolean doRenderEnvironmentMapping)
    {
        if (!TEMPORARILY_DISABLE_ALL_GRAPH_RENDERING)
        {
            if ( SHOW_3D_SHADOWS.get() )
                draw3DShadows(gl);

            if ( doRenderEnvironmentMapping && USE_GL_EXT_FRAMEBUFFER_OBJECT && SHOW_3D_ENVIRONMENT_MAPPING.get() )
                draw3DEnvironmentMapping(gl);

            if ( !DISABLE_EDGES_RENDERING.get() && (allEdgesDisplayLists != null) )
            {
                // shaderLinesSFXs.useShaderLinesSFX(gl, ShaderLinesSFXs.ShaderTypes.LINE_RENDERING);
                gl.glCallLists(allEdgesDisplayLists.capacity(), GL_INT, allEdgesDisplayLists);
                // shaderLinesSFXs.disableShaders(gl);
            }

            if ( !DISABLE_NODES_RENDERING.get() && (SHOW_NODES.get() || !isInMotion) )
            {
                if (!animationRender)
                {
                    enableShaders(gl, true);
                    gl.glCallList(nodesDisplayList);
                    disableShaders(gl);
                }
                else
                    drawAllVisibleNodes(gl);

                if ( !(autoRotate || autoPulsate) )
                {
                    enableShaders(gl, false);
                    gl.glCallList(selectedNodesDisplayList);
                    disableShaders(gl);
                }
            }
        }

        if ( SHOW_3D_FRUSTUM.get() )
            drawFrustum(gl);

        if (selectBox && !(autoRotate || autoPulsate) )
        {
            drawSelectBox(gl, width, height);

            if (doRenderEnvironmentMapping)
                selectBox = false;
        }

        if (autoRotate || autoPulsate)
        {
            // only one out of 2 frames when autoRotate && autoPulsate is on, first frame is to create the nodes maps
            if ( !(autoRotate && autoPulsate) )
            {
                viewOrtho(gl, width, height);

                if (graphRendererThreadUpdater != null)
                {
                    if (IS_WIN && USE_SHADERS_PROCESS)
                        graph.drawProfileMode(gl, width, height, (animationRender) ? "- 3D Animation Mode -" : "- 3D Profile Mode -", animationRender);
                    else
                        graph.drawOSCompatibleProfileMode(gl);
                }

                if (animationRender)
                    graph.drawAnimationCurrentTick(gl, currentTick + 1);

                viewPerspective(gl);
            }
        }

        if (checkForNodeTexturing() && USE_SHADERS_PROCESS) // so as to re-bind the texture object for proper point sprites rendering
        {
            if ( TEXTURE_ENABLED.get() && !SHOW_3D_ENVIRONMENT_MAPPING.get() )
                nodeTexture.bind(gl);
            else if ( USE_GL_EXT_FRAMEBUFFER_OBJECT && SHOW_3D_ENVIRONMENT_MAPPING.get() )
                renderToTexture.bind(gl);
        }

        if (!TEMPORARILY_DISABLE_ALL_GRAPH_RENDERING)
            if ( !DISABLE_NODES_RENDERING.get() && (SHOW_NODES.get() || !isInMotion) )
                if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() && YED_STYLE_COMPONENT_CONTAINERS_RENDERING_FOR_GPAPHML_FILES.get() )
                    gl.glCallList(pathwayComponentContainersDisplayList);
    }

    /**
    *  Selects the 3D OpenGL scene.
    */
    private void selectScene(GL2 gl)
    {
        SELECTION_BUFFER.clear(); // Prepare buffer for reading

        // tell OpenGL to use our array for selection
        gl.glSelectBuffer(SELECTION_BUFFER.capacity(), SELECTION_BUFFER);

        // put openGL in GL_SELECT mode
        gl.glRenderMode(GL_SELECT);

        // initialize the name stack
        gl.glInitNames();

        // Now we're restricting drawing to just under the cursor.  We need the projection matrix
        // for this. Push it to the stack then reset the new matrix with load identity
        gl.glMatrixMode(GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        // set the VIEWPORT to the size and location of the screen
        gl.glGetIntegerv(GL_VIEWPORT, VIEWPORT, 0);

        // Now restrict drawing using gluPickMatrix().  The first parameter is our current
        // mouse position on the x-axis, the second is the current mouse y axis.  Then the width
        // and height of the picking region.  Finally the current VIEWPORT indicates the current boundries.
        // mouse_x and y are the center of the picking region.
        GLU.gluPickMatrix(pickOriginX, VIEWPORT[3] - pickOriginY, pickWidth, pickHeight, VIEWPORT, 0);

        // set projection (perspective or orthogonal) exactly as it is in normal renderering
        setPerspective(gl, FOV_Y, (width <= height) ? ( (double)height / (double)width ) : ( (double)width / (double)height ), NEAR_DISTANCE, FAR_DISTANCE);
        // same with:
        // GLU.gluPerspective(FOV_Y, (width <= height) ? ( (double)height / (double)width ) : ( (double)width / (double)height ), NEAR_DISTANCE, FAR_DISTANCE);

        // set the modelview to render our targets properly
        gl.glMatrixMode(GL_MODELVIEW);

        // push at least one entry on to the stack
        gl.glPushName(MAP_SELECTION_ID);

        // "render" the targets to our modelview
        if ( FAST_SELECTION_MODE.get() )
            gl.glCallList(fastSelectionNodesDisplayList);
        else
            gl.glCallList(nodesDisplayList);

        // pop the entry from the stack
        gl.glPopName();

        // restore original projection matrix
        gl.glMatrixMode(GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glFlush();

        // find out how many objects we hit & back to GL_RENDER mode
        int hits = gl.glRenderMode(GL_RENDER);

        // next display() call will render normally
        selectMode = false;

        // visible result again
        if ( (!pickAdd && !pickFind) || (mouseHasClicked && !pickAdd) )
            selectionManager.clearAllSelection();

        processHits(hits);
    }

    /**
    *  Does the same thing as GLU.gluPerspective() but in one step.
    */
    private void setPerspective(GL2 gl, double fovy, double aspect, double zNear, double zFar)
    {
        double top = zNear * tan(fovy * PI / 360.0);
        double bottom = -top;
        double right = aspect * top;
        double left = -right;

        gl.glFrustum(left, right, bottom, top, zNear, zFar);
    }

    /**
    *  Resets the view.
    */
    private void resetView()
    {
        graph.prepareBackgroundColor();

        scaleValue = DEFAULT_SCALE;
        translateDX = 0.0f;
        translateDY = 0.0f;

        FOCUS_POSITION_3D.setLocation(0.0f, 0.0f, 0.0f);
        xzRotate = 0.0f;
        yzRotate = 0.0f;
	xwRotate = 0.0f;
	ywRotate = 0.0f;
    }

    /**
    *  Prepares the lighting.
    */
    private void prepareLighting(GL2 gl)
    {
        // OpenGL will setup our emissions etc.
        gl.glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
        gl.glEnable(GL_COLOR_MATERIAL);
        gl.glShadeModel(GL_SMOOTH);                                     // Enables Smooth Color Shading (Gouraud Shading)

        gl.glEnable(GL_LIGHT0);
        gl.glLightfv(GL_LIGHT0, GL_AMBIENT, LIGHT_AMBIENT_ARRAY);       // Setup the ambient light
        gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, LIGHT_DIFFUSE_ARRAY);       // Setup the diffuse light
        gl.glLightfv(GL_LIGHT0, GL_SPECULAR, LIGHT_SPECULAR_ARRAY);     // Setup the diffuse light

        gl.glLightModelfv(GL_LIGHT_MODEL_AMBIENT, MODEL_AMBIENT_ARRAY); // Setup a small white ambient light
        gl.glLightModeli(GL_LIGHT_MODEL_LOCAL_VIEWER, LOCAL_VIEWER);
        // gl.glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_TRUE);          // Don't enable two-sided light as it creates problems with Gouraud (fixed pipeline) lighting & SuperQuadric shapes
    }

    /**
    *  Prepares the point sprites.
    */
    private void preparePointSprites(GL2 gl)
    {
        // enable Point Sprites GL 1.5 extension using GL Point Sprites
        // then also enabling combined Shader Usage only to avoid very slow GL_POINTS rendering with shaders on
        gl.glEnable(GL_POINT_SPRITE);
        gl.glEnable(GL_VERTEX_PROGRAM_POINT_SIZE);

        // Set parameters for points
        gl.glPointParameterfv(GL_POINT_DISTANCE_ATTENUATION, POINT_DISTANCE_ATTENUATION_ARRAY);
        gl.glPointParameterf(GL_POINT_FADE_THRESHOLD_SIZE, 60.0f);

        // Make space for particle limits and fill it from OpenGL call
        gl.glGetFloatv(GL_ALIASED_POINT_SIZE_RANGE, ALIASED_POINT_SIZE_RANGE);
        // Tell it the max and min sizes we can use using the pre-filled array
        gl.glPointParameterf( GL_POINT_SIZE_MIN, ALIASED_POINT_SIZE_RANGE.get(0) );
        gl.glPointParameterf( GL_POINT_SIZE_MAX, ALIASED_POINT_SIZE_RANGE.get(1) );

        // Tell OpenGL to replace the coordinates upon drawing
        gl.glTexEnvf(GL_POINT_SPRITE, GL_COORD_REPLACE, GL_TRUE);

        // Tell OpenGL to use the lower left corner for point sprite texture coordinates so as to display a given texture (biolayout logo by default) properly
        gl.glPointParameterf(GL_POINT_SPRITE_COORD_ORIGIN, GL_LOWER_LEFT);
    }

    /**
    *  Prepares the shader lighting.
    */
    private void prepareShaderLighting(GL2 gl)
    {
        // only instantiate them once in first switch
        if (shaderLightingSFXsNodes == null) shaderSFXsCurrentReference = shaderLightingSFXsNodes = new ShaderLightingSFXs(gl);
    }

    /**
    *  Prepares the environment mapping.
    */
    private void prepareEnvironmentMapping(GL2 gl)
    {
        renderToTexture = new RenderToTexture(gl, !(NORMAL_QUALITY_ANTIALIASING.get() || HIGH_QUALITY_ANTIALIASING.get() ), true);
    }

    /**
    *  Clears the screen.
    */
    private void clearScreen3D(GL2 gl)
    {
        // trippy mode disabled for tile based rendering to avoid artifacts
        if (TRIPPY_BACKGROUND.get() && !takeHighResScreenshot)
            graph.colorCycle(BACKGROUND_COLOR_ARRAY);

        gl.glClearColor(BACKGROUND_COLOR_ARRAY[0], BACKGROUND_COLOR_ARRAY[1], BACKGROUND_COLOR_ARRAY[2], 1.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear The Screen And The Depth Buffer

        FOG_COLOR.put(BACKGROUND_COLOR_ARRAY).rewind();
        gl.glFogfv(GL_FOG_COLOR, FOG_COLOR); // set Fog Color
    }

    /**
    *  Performs morphing based pulsation (uses Shaders).
    */
    private void performMorphingBasedPulsation()
    {
        morphingValue = CPUEmulatedGLSLFunctions.clamp( (float)pulseSteps / PULSATION_UPPER_THRESHOLD, 0.001f, 1.0f);
    }

    /**
    *  Performs non-morphing based pulsation (uses standard OpenGL translations).
    */
    private void performNonMorphingBasedPulsation()
    {
        graph.increaseNodeSize(pulsateValue, false);

        updateNodesDisplayList = true;
        updateEdgesDisplayList = true;
    }

    /**
    *  Uses the node material.
    */
    private void useNodeMaterial(GL2 gl)
    {
        if ( MATERIAL_SPECULAR.get() )
        {
            gl.glMaterialfv(GL_FRONT, GL_SPECULAR, LIGHT_SPECULAR_ARRAY);
            gl.glMaterialf(GL_FRONT, GL_SHININESS, MATERIAL_SPECULAR_SHINE.get());
        }
        else
        {
            gl.glMaterialfv(GL_FRONT, GL_SPECULAR, NO_LIGHT_SPECULAR_ARRAY);
            gl.glMaterialf(GL_FRONT, GL_SHININESS, 0.0f);
        }
    }

    /**
    *  Enables the spherical texture coordinates generation.
    */
    private void enableGenerateSphericalTextureCoordinates(GL2 gl)
    {
        gl.glEnable(GL_TEXTURE_GEN_S);
        gl.glEnable(GL_TEXTURE_GEN_T);
        gl.glTexGeni(GL_S, GL_TEXTURE_GEN_MODE, GL_SPHERE_MAP);
        gl.glTexGeni(GL_T, GL_TEXTURE_GEN_MODE, GL_SPHERE_MAP);
    }

    /**
    *  Disables the spherical texture coordinates generation.
    */
    private void disableGenerateSphericalTextureCoordinates(GL2 gl)
    {
        gl.glDisable(GL_TEXTURE_GEN_S);
        gl.glDisable(GL_TEXTURE_GEN_T);
    }

    /**
    *  Builds all 3D shapes display lists.
    */
    private void buildAllShapes3DDisplayLists(GL2 gl)
    {
        buildAllShapes3DDisplayLists(gl, true, true, true);
    }

    /**
    *  Builds all 3D shapes display lists.
    *  Overloaded version of the method above.
    */
    private void buildAllShapes3DDisplayLists(GL2 gl, boolean changeAllShapes, boolean changeTesselationRelatedShapes, boolean changeSphericalCoordsRelatedShapes)
    {
        int tesselation = NODE_TESSELATION.get();
        int shapeIndex = SPHERE.ordinal();
         // don't use a display list here, as it will create problems with the disposeAllModelShapeResources() method where the display list is being disposed and the last diplay list will take its place. Use Immediate Mode, Vertex Arrays or VBO instead
        ModelRenderingStates modelRenderingState = USE_SHADERS_PROCESS ? VBO : (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER ? VERTEX_ARRAY : IMMEDIATE_MODE);
        ModelSettings modelSettings = new ModelSettings(true, true, modelRenderingState);
        for ( Shapes3D shape3D : Shapes3D.values() )
        {
            if ( shape3D.equals(SPHERE) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = SPHERE.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                ModelShape superQuadricShape = SuperQuadricShapesProducer.createSphereShape(gl, tesselation, tesselation, modelSettings);
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                superQuadricShape.drawModelShape(gl);
                gl.glPopMatrix();
                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                superQuadricShape.disposeAllModelShapeResources(gl);
            }
            else if (shape3D.equals(CUBE) && changeAllShapes) // cube
            {
                shapeIndex = CUBE.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                GLUT.glutSolidCube(1.0f);

                gl.glEndList();
            }
            else if (shape3D.equals(TETRAHEDRON) && changeAllShapes)
            {
                shapeIndex = TETRAHEDRON.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                GLUT.glutSolidTetrahedron();

                gl.glEndList();
            }
            else if (shape3D.equals(OCTAHEDRON) && changeAllShapes)
            {
                shapeIndex = OCTAHEDRON.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                GLUT.glutSolidOctahedron();

                gl.glEndList();
            }
            else if (shape3D.equals(DODECAHEDRON) && changeAllShapes)
            {
                shapeIndex = DODECAHEDRON.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                GLUT.glutSolidDodecahedron();

                gl.glEndList();
            }
            else if (shape3D.equals(ICOSAHEDRON) && changeAllShapes)
            {
                shapeIndex = ICOSAHEDRON.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                GLUT.glutSolidIcosahedron();

                gl.glEndList();
            }
            else if ( shape3D.equals(CONE_LEFT) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = CONE_LEFT.ordinal();
                modelSettings.centerModel = true; // Lathe3D Cone will be centered
                ModelShape lathe3DShape = Lathe3DShapesProducer.createConeShape(gl, (tesselation < 3) ? 1 : tesselation / 3, graph.getLathe3DShapeAngleIncrement(tesselation), modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(CONE_RIGHT) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = CONE_RIGHT.ordinal();
                modelSettings.centerModel = true; // Lathe3D Cone will be centered
                ModelShape lathe3DShape = Lathe3DShapesProducer.createConeShape(gl, (tesselation < 3) ? 1 : tesselation / 3, graph.getLathe3DShapeAngleIncrement(tesselation), modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(TRAPEZOID_UP) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = TRAPEZOID_UP.ordinal();
                modelSettings.centerModel = true; // Lathe3D Shape will be centered
                ModelShape lathe3DShape = Lathe3DShapesProducer.createTrapezoidShape(gl, (tesselation < 3) ? 1 : tesselation / 3, graph.getLathe3DShapeAngleIncrement(tesselation), modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(TRAPEZOID_DOWN) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = TRAPEZOID_DOWN.ordinal();
                modelSettings.centerModel = true; // Lathe3D Shape will be centered
                ModelShape lathe3DShape = Lathe3DShapesProducer.createTrapezoidShape(gl, (tesselation < 3) ? 1 : tesselation / 3, graph.getLathe3DShapeAngleIncrement(tesselation), modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(CYLINDER) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = CYLINDER.ordinal();
                modelSettings.centerModel = true; // Lathe3D Cylinder will be centered
                ModelShape lathe3DShape = Lathe3DShapesProducer.createCylinderShape(gl, (tesselation < 3) ? 1 : tesselation / 3, graph.getLathe3DShapeAngleIncrement(tesselation), modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(TORUS) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = TORUS.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                ModelShape superToroidShape = SuperQuadricShapesProducer.createTorusShape(gl, tesselation, tesselation, modelSettings);
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                superToroidShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                superToroidShape.disposeAllModelShapeResources(gl);
            }
            else if (shape3D.equals(RECTANGLE_VERTICAL) && changeAllShapes)
            {
                shapeIndex = RECTANGLE_VERTICAL.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glScalef(0.3f, 1.7f, 1.0f);
                GLUT.glutSolidCube(1.0f);
                gl.glPopMatrix();

                gl.glEndList();
            }
            else if (shape3D.equals(RECTANGLE_HORIZONTAL) && changeAllShapes)
            {
                shapeIndex = RECTANGLE_HORIZONTAL.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glScalef(1.7f, 0.3f, 1.0f);
                GLUT.glutSolidCube(1.0f);
                gl.glPopMatrix();

                gl.glEndList();
            }
            else if ( shape3D.equals(ROUND_CUBE_THIN) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = ROUND_CUBE_THIN.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SETTINGS.uSegments = SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SETTINGS.vSegments = tesselation;
                ModelShape superQuadricShape = new SuperQuadricShape(gl, SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SETTINGS, modelSettings);
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SCALE_X, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SCALE_Y, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SCALE_Z);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                superQuadricShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                superQuadricShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(ROUND_CUBE_LARGE) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = ROUND_CUBE_LARGE.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SETTINGS.uSegments = SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SETTINGS.vSegments = tesselation;
                ModelShape superQuadricShape = new SuperQuadricShape(gl, SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SETTINGS, modelSettings);
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SCALE_X, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SCALE_Y, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SCALE_Z);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                superQuadricShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                superQuadricShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(PINEAPPLE_SLICE_TOROID) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = PINEAPPLE_SLICE_TOROID.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SETTINGS.uSegments = SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SETTINGS.vSegments = tesselation;
                ModelShape superQuadricShape = new SuperQuadricShape(gl, SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SETTINGS, modelSettings);
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SCALE_X, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SCALE_Y, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SCALE_Z);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                superQuadricShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                superQuadricShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(PINEAPPLE_SLICE_ELLIPSOID) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = PINEAPPLE_SLICE_ELLIPSOID.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SETTINGS.uSegments = SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SETTINGS.vSegments = tesselation;
                ModelShape superQuadricShape = new SuperQuadricShape(gl, SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SETTINGS, modelSettings);
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SCALE_X, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SCALE_Y, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SCALE_Z);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                superQuadricShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                superQuadricShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(DOUBLE_PYRAMID_THIN) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = DOUBLE_PYRAMID_THIN.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                ModelShape superQuadricShape = null;
                SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SETTINGS.uSegments = SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SETTINGS.vSegments = tesselation;
                superQuadricShape = new SuperQuadricShape(gl, SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SETTINGS, modelSettings);
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SCALE_X, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SCALE_Y, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SCALE_Z);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                superQuadricShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                superQuadricShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(DOUBLE_PYRAMID_LARGE) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = DOUBLE_PYRAMID_LARGE.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SETTINGS.uSegments = SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SETTINGS.vSegments = tesselation;
                ModelShape superQuadricShape = new SuperQuadricShape(gl, SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SETTINGS, modelSettings);
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SCALE_X, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SCALE_Y, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SCALE_Z);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                superQuadricShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                superQuadricShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(TORUS_8_PETALS) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = TORUS_8_PETALS.ordinal();
                modelSettings.centerModel = true; // Lathe3D Shape will be centered
                LATHE3D_MEPN_3D_SHAPE_AND_SETTINGS.splineStep = (tesselation < 3) ? 1 : tesselation / 3;
                ModelShape lathe3DShape = Lathe3DShapesProducer.createLathe3DShape(gl, LATHE3D_MEPN_3D_SHAPE_AND_SETTINGS, graph.getLathe3DShapeAngleIncrement(tesselation), modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + LATHE3D_MEPN_3D_SHAPE_AND_SCALE_X, 1.0f + LATHE3D_MEPN_3D_SHAPE_AND_SCALE_Y, 1.0f + LATHE3D_MEPN_3D_SHAPE_AND_SCALE_Z);
                gl.glRotatef(LATHE3D_MEPN_3D_SHAPE_AND_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(LATHE3D_MEPN_3D_SHAPE_AND_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(LATHE3D_MEPN_3D_SHAPE_AND_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(SAUCER_4_PETALS) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = SAUCER_4_PETALS.ordinal();
                modelSettings.centerModel = true; // Lathe3D Shape will be centered
                LATHE3D_MEPN_3D_SHAPE_OR_SETTINGS.splineStep = (tesselation < 3) ? 1 : tesselation / 3;
                ModelShape lathe3DShape = Lathe3DShapesProducer.createLathe3DShape(gl, LATHE3D_MEPN_3D_SHAPE_OR_SETTINGS, graph.getLathe3DShapeAngleIncrement(tesselation), modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + LATHE3D_MEPN_3D_SHAPE_OR_SCALE_X, 1.0f + LATHE3D_MEPN_3D_SHAPE_OR_SCALE_Y, 1.0f + LATHE3D_MEPN_3D_SHAPE_OR_SCALE_Z);
                gl.glRotatef(LATHE3D_MEPN_3D_SHAPE_OR_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(LATHE3D_MEPN_3D_SHAPE_OR_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(LATHE3D_MEPN_3D_SHAPE_OR_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(GENE_MODEL) && (changeAllShapes || changeSphericalCoordsRelatedShapes) )
            {
                // no need to reload the model for calculating spherical coords if it does not have textures as materials
                // (update: does indeed need to for spherical mapping in non-shader mode)
                // if ( (geneShape != null) && !changeAllShapes && !geneShape.getHasTexture() ) continue;

                // no need to reload the model for calculating spherical coords, just use current geneShape.drawModelShape() geometry
                if ( !( (geneShape != null) && !changeAllShapes && changeSphericalCoordsRelatedShapes ) )
                {
                    if (geneShape != null)
                    {
                        geneShape.disposeAllModelShapeResources(gl);
                    }
                    geneShape = new OBJModelLoader(gl, graph, MODEL_FILES_PATH,
                            capitalizeFirstCharacter(OBJModelShapes.GENE) + ".obj",
                            OBJ_MODEL_SHAPE_SIZES[OBJModelShapes.GENE.ordinal()], modelRenderingState, false, false);
                }

                shapeIndex = GENE_MODEL.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_SCALE_X, 1.0f + OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_SCALE_Y, 1.0f + OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_SCALE_Z);
                gl.glRotatef(OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                geneShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();
            }
            else if ( shape3D.equals(LATHE_3D) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = LATHE_3D.ordinal();
                modelSettings.centerModel = true; // Lathe3D Shape will be centered
                LATHE3D_SETTINGS.splineStep = (tesselation < 3) ? 1 : tesselation / 3;
                ModelShape lathe3DShape = Lathe3DShapesProducer.createLathe3DShape(gl, LATHE3D_SETTINGS, graph.getLathe3DShapeAngleIncrement(tesselation), modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef( 1.0f + LATHE3D_SCALE_X.get(), 1.0f + LATHE3D_SCALE_Y.get(), 1.0f + LATHE3D_SCALE_Z.get() );
                gl.glRotatef(LATHE3D_ROTATE_X.get(), 1.0f, 0.0f, 0.0f);
                gl.glRotatef(LATHE3D_ROTATE_Y.get(), 0.0f, 1.0f, 0.0f);
                gl.glRotatef(LATHE3D_ROTATE_Z.get(), 0.0f, 0.0f, 1.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(SUPER_QUADRIC) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) )
            {
                shapeIndex = SUPER_QUADRIC.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                SUPER_QUADRIC_SETTINGS.uSegments = SUPER_QUADRIC_SETTINGS.vSegments = tesselation;
                ModelShape superQuadricShape = new SuperQuadricShape(gl, SUPER_QUADRIC_SETTINGS, modelSettings);
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef( 1.0f + SUPER_QUADRIC_SCALE_X.get(), 1.0f + SUPER_QUADRIC_SCALE_Y.get(), 1.0f + SUPER_QUADRIC_SCALE_Z.get() );
                gl.glRotatef(SUPER_QUADRIC_ROTATE_X.get(), 1.0f, 0.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_ROTATE_Y.get(), 0.0f, 1.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_ROTATE_Z.get(), 0.0f, 0.0f, 1.0f);
                superQuadricShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                superQuadricShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(OBJ_MODEL_LOADER) && (changeAllShapes || changeSphericalCoordsRelatedShapes) )
            {
                // no need to reload the model for calculating spherical coords if it does not have textures as materials
                // (update: does indeed need to for spherical mapping in non-shader mode)
                // if ( (objModelLoaderShape != null) && !changeAllShapes && !objModelLoaderShape.getHasTexture() ) continue;

                // no need to reload the model for calculating spherical coords, just use current objModelLoaderShape.drawModelShape() geometry
                if ( !( (objModelLoaderShape != null) && !changeAllShapes && changeSphericalCoordsRelatedShapes ) )
                {
                    if (objModelLoaderShape != null)
                    {
                        objModelLoaderShape.disposeAllModelShapeResources(gl);
                    }
                    objModelLoaderShape = new OBJModelLoader(gl, graph, EXTERNAL_OBJ_MODEL_FILE_PATH,
                            EXTERNAL_OBJ_MODEL_FILE_NAME + ".obj", OBJ_MODEL_LOADER_SHAPE_SIZE.get(),
                            modelRenderingState, USE_EXTERNAL_OBJ_MODEL_FILE, false);
                }

                shapeIndex = OBJ_MODEL_LOADER.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef( 1.0f + OBJ_MODEL_LOADER_SCALE_X.get(), 1.0f + OBJ_MODEL_LOADER_SCALE_Y.get(), 1.0f + OBJ_MODEL_LOADER_SCALE_Z.get() );
                gl.glRotatef(OBJ_MODEL_LOADER_ROTATE_X.get(), 1.0f, 0.0f, 0.0f);
                gl.glRotatef(OBJ_MODEL_LOADER_ROTATE_Y.get(), 0.0f, 1.0f, 0.0f);
                gl.glRotatef(OBJ_MODEL_LOADER_ROTATE_Z.get(), 0.0f, 0.0f, 1.0f);
                objModelLoaderShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();
            }
            else if ( shape3D.equals(DUMB_BELL) && (changeAllShapes || changeTesselationRelatedShapes || changeSphericalCoordsRelatedShapes) ) //dumbbell added for BioPAX RnaRegion
            {
                shapeIndex = DUMB_BELL.ordinal();
                modelSettings.centerModel = true; // Lathe3D Shape will be centered
                ModelShape lathe3DShape = Lathe3DShapesProducer.createDumbBellShape(gl, (tesselation < 3) ? 1 : tesselation / 3, graph.getLathe3DShapeAngleIncrement(tesselation), modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) enableGenerateSphericalTextureCoordinates(gl);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                if ( MATERIAL_SPHERICAL_MAPPING.get() ) disableGenerateSphericalTextureCoordinates(gl);

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
        }
    }

    /**
    *  Builds all 3D shapes fast selection display lists.
    */
    private void buildAllShapes3DFastSelectionDisplayLists(GL2 gl)
    {
        int shapeIndex = SPHERE.ordinal();
        // don't use a display list here, as it will create problems with the disposeAllModelShapeResources() method where the display list is being disposed and the last diplay list will take its place. Use Immediate Mode, Vertex Arrays or VBO instead
        ModelRenderingStates modelRenderingState = USE_SHADERS_PROCESS ? VBO : (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER ? VERTEX_ARRAY : IMMEDIATE_MODE);
        ModelSettings modelSettings = new ModelSettings(false, false, modelRenderingState);
        for ( Shapes3D shape3D : Shapes3D.values() )
        {
            if ( shape3D.equals(SPHERE) )
            {
                shapeIndex = SPHERE.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                ModelShape superQuadricShape = SuperQuadricShapesProducer.createSphereShape(gl, FAST_SELECTION_MODE_NODE_TESSELATION, FAST_SELECTION_MODE_NODE_TESSELATION, modelSettings);
                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                superQuadricShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                superQuadricShape.disposeAllModelShapeResources(gl);
            }
            else if (shape3D.equals(CUBE) ) // cube
            {
                shapeIndex = CUBE.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                GLUT.glutSolidCube(1.0f);

                gl.glEndList();
            }
            else if (shape3D.equals(TETRAHEDRON) )
            {
                shapeIndex = TETRAHEDRON.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                GLUT.glutSolidTetrahedron();

                gl.glEndList();
            }
            else if (shape3D.equals(OCTAHEDRON) )
            {
                shapeIndex = OCTAHEDRON.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                GLUT.glutSolidOctahedron();

                gl.glEndList();
            }
            else if (shape3D.equals(DODECAHEDRON) )
            {
                shapeIndex = DODECAHEDRON.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                GLUT.glutSolidDodecahedron();

                gl.glEndList();
            }
            else if (shape3D.equals(ICOSAHEDRON) )
            {
                shapeIndex = ICOSAHEDRON.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                GLUT.glutSolidIcosahedron();

                gl.glEndList();
            }
            else if ( shape3D.equals(CONE_LEFT) )
            {
                shapeIndex = CONE_LEFT.ordinal();
                modelSettings.centerModel = true; // Lathe3D Cone will be centered
                ModelShape lathe3DShape = Lathe3DShapesProducer.createConeShape(gl, FAST_SELECTION_MODE_NODE_TESSELATION, _90, modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(CONE_RIGHT) )
            {
                shapeIndex = CONE_RIGHT.ordinal();
                modelSettings.centerModel = true; // Lathe3D Cone will be centered
                ModelShape lathe3DShape = Lathe3DShapesProducer.createConeShape(gl, FAST_SELECTION_MODE_NODE_TESSELATION, _90, modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(TRAPEZOID_UP) )
            {
                shapeIndex = TRAPEZOID_UP.ordinal();
                modelSettings.centerModel = true; // Lathe3D Shape will be centered
                LATHE3D_SETTINGS.splineStep = FAST_SELECTION_MODE_NODE_TESSELATION;
                ModelShape lathe3DShape = Lathe3DShapesProducer.createTrapezoidShape(gl, FAST_SELECTION_MODE_NODE_TESSELATION, _90, modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(TRAPEZOID_DOWN) )
            {
                shapeIndex = TRAPEZOID_DOWN.ordinal();
                modelSettings.centerModel = true; // Lathe3D Shape will be centered
                LATHE3D_SETTINGS.splineStep = FAST_SELECTION_MODE_NODE_TESSELATION;
                ModelShape lathe3DShape = Lathe3DShapesProducer.createTrapezoidShape(gl, FAST_SELECTION_MODE_NODE_TESSELATION, _90, modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(CYLINDER) )
            {
                shapeIndex = CYLINDER.ordinal();
                modelSettings.centerModel = true; // Lathe3D Cylinder will be centered
                ModelShape lathe3DShape = Lathe3DShapesProducer.createCylinderShape(gl, FAST_SELECTION_MODE_NODE_TESSELATION, _90, modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(TORUS) )
            {
                shapeIndex = TORUS.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                ModelShape superToroidShape = SuperQuadricShapesProducer.createTorusShape(gl, FAST_SELECTION_MODE_NODE_TESSELATION, FAST_SELECTION_MODE_NODE_TESSELATION , modelSettings);
                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                superToroidShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                superToroidShape.disposeAllModelShapeResources(gl);
            }
            else if (shape3D.equals(RECTANGLE_VERTICAL) )
            {
                shapeIndex = RECTANGLE_VERTICAL.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glScalef(0.3f, 1.7f, 1.0f);
                GLUT.glutSolidCube(1.0f);
                gl.glPopMatrix();

                gl.glEndList();
            }
            else if (shape3D.equals(RECTANGLE_HORIZONTAL) )
            {
                shapeIndex = RECTANGLE_HORIZONTAL.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glScalef(1.7f, 0.3f, 1.0f);
                GLUT.glutSolidCube(1.0f);
                gl.glPopMatrix();

                gl.glEndList();
            }
            else if ( shape3D.equals(ROUND_CUBE_THIN) )
            {
                shapeIndex = ROUND_CUBE_THIN.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SETTINGS.uSegments = SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SETTINGS.vSegments = FAST_SELECTION_MODE_NODE_TESSELATION;
                ModelShape superQuadricShape = new SuperQuadricShape(gl, SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SETTINGS, modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SCALE_X, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SCALE_Y, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SCALE_Z);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                superQuadricShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                superQuadricShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(ROUND_CUBE_LARGE) )
            {
                shapeIndex = ROUND_CUBE_LARGE.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SETTINGS.uSegments = SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SETTINGS.vSegments = FAST_SELECTION_MODE_NODE_TESSELATION;
                ModelShape superQuadricShape = new SuperQuadricShape(gl, SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SETTINGS, modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SCALE_X, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SCALE_Y, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SCALE_Z);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                superQuadricShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                superQuadricShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(PINEAPPLE_SLICE_TOROID) )
            {
                shapeIndex = PINEAPPLE_SLICE_TOROID.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SETTINGS.uSegments = SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SETTINGS.vSegments = FAST_SELECTION_MODE_NODE_TESSELATION;
                ModelShape superQuadricShape = new SuperQuadricShape(gl, SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SETTINGS, modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SCALE_X, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SCALE_Y, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SCALE_Z);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                superQuadricShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                superQuadricShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(PINEAPPLE_SLICE_ELLIPSOID) )
            {
                shapeIndex = PINEAPPLE_SLICE_ELLIPSOID.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SETTINGS.uSegments = SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SETTINGS.vSegments = FAST_SELECTION_MODE_NODE_TESSELATION;
                ModelShape superQuadricShape = new SuperQuadricShape(gl, SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SETTINGS, modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SCALE_X, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SCALE_Y, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SCALE_Z);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                superQuadricShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                superQuadricShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(DOUBLE_PYRAMID_THIN) )
            {
                shapeIndex = DOUBLE_PYRAMID_THIN.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SETTINGS.uSegments = SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SETTINGS.vSegments = FAST_SELECTION_MODE_NODE_TESSELATION;
                ModelShape superQuadricShape = new SuperQuadricShape(gl, SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SETTINGS, modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SCALE_X, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SCALE_Y, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SCALE_Z);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                superQuadricShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                superQuadricShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(DOUBLE_PYRAMID_LARGE) )
            {
                shapeIndex = DOUBLE_PYRAMID_LARGE.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SETTINGS.uSegments = SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SETTINGS.vSegments = FAST_SELECTION_MODE_NODE_TESSELATION;
                ModelShape superQuadricShape = new SuperQuadricShape(gl, SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SETTINGS, modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SCALE_X, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SCALE_Y, 1.0f + SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SCALE_Z);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                superQuadricShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                superQuadricShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(TORUS_8_PETALS) )
            {
                shapeIndex = TORUS_8_PETALS.ordinal();
                modelSettings.centerModel = true; // Lathe3D Shape will be centered
                LATHE3D_MEPN_3D_SHAPE_AND_SETTINGS.splineStep = FAST_SELECTION_MODE_NODE_TESSELATION;
                ModelShape lathe3DShape = Lathe3DShapesProducer.createLathe3DShape(gl, LATHE3D_MEPN_3D_SHAPE_AND_SETTINGS, _90, modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + LATHE3D_MEPN_3D_SHAPE_AND_SCALE_X, 1.0f + LATHE3D_MEPN_3D_SHAPE_AND_SCALE_Y, 1.0f + LATHE3D_MEPN_3D_SHAPE_AND_SCALE_Z);
                gl.glRotatef(LATHE3D_MEPN_3D_SHAPE_AND_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(LATHE3D_MEPN_3D_SHAPE_AND_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(LATHE3D_MEPN_3D_SHAPE_AND_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(SAUCER_4_PETALS) )
            {
                shapeIndex = SAUCER_4_PETALS.ordinal();
                modelSettings.centerModel = true; // Lathe3D Shape will be centered
                LATHE3D_MEPN_3D_SHAPE_OR_SETTINGS.splineStep = FAST_SELECTION_MODE_NODE_TESSELATION;
                ModelShape lathe3DShape = Lathe3DShapesProducer.createLathe3DShape(gl, LATHE3D_MEPN_3D_SHAPE_OR_SETTINGS, _90, modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + LATHE3D_MEPN_3D_SHAPE_OR_SCALE_X, 1.0f + LATHE3D_MEPN_3D_SHAPE_OR_SCALE_Y, 1.0f + LATHE3D_MEPN_3D_SHAPE_OR_SCALE_Z);
                gl.glRotatef(LATHE3D_MEPN_3D_SHAPE_OR_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(LATHE3D_MEPN_3D_SHAPE_OR_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(LATHE3D_MEPN_3D_SHAPE_OR_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(GENE_MODEL) )
            {
                if (geneShape == null)
                {
                    // if (geneShape != null) geneShape.disposeAllModelShapeResources(gl);
                    geneShape = new OBJModelLoader(gl, graph, MODEL_FILES_PATH,
                            capitalizeFirstCharacter(OBJModelShapes.GENE) + ".obj",
                            OBJ_MODEL_SHAPE_SIZES[OBJModelShapes.GENE.ordinal()], modelRenderingState, false, false);
                }

                shapeIndex = GENE_MODEL.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef(1.0f + OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_SCALE_X, 1.0f + OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_SCALE_Y, 1.0f + OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_SCALE_Z);
                gl.glRotatef(OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_ROTATE_X, 1.0f, 0.0f, 0.0f);
                gl.glRotatef(OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_ROTATE_Y, 0.0f, 1.0f, 0.0f);
                gl.glRotatef(OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_ROTATE_Z, 0.0f, 0.0f, 1.0f);
                geneShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();
            }
            else if ( shape3D.equals(LATHE_3D) )
            {
                shapeIndex = LATHE_3D.ordinal();
                modelSettings.centerModel = true; // Lathe3D Shape will be centered
                LATHE3D_SETTINGS.splineStep = FAST_SELECTION_MODE_NODE_TESSELATION;
                ModelShape lathe3DShape = Lathe3DShapesProducer.createLathe3DShape(gl, LATHE3D_SETTINGS, _90, modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef( 1.0f + LATHE3D_SCALE_X.get(), 1.0f + LATHE3D_SCALE_Y.get(), 1.0f + LATHE3D_SCALE_Z.get() );
                gl.glRotatef(LATHE3D_ROTATE_X.get(), 1.0f, 0.0f, 0.0f);
                gl.glRotatef(LATHE3D_ROTATE_Y.get(), 0.0f, 1.0f, 0.0f);
                gl.glRotatef(LATHE3D_ROTATE_Z.get(), 0.0f, 0.0f, 1.0f);
                lathe3DShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                lathe3DShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(SUPER_QUADRIC) )
            {
                shapeIndex = SUPER_QUADRIC.ordinal();
                modelSettings.centerModel = false; // SuperQuadrics are alredy pre-centered
                SUPER_QUADRIC_SETTINGS.uSegments = SUPER_QUADRIC_SETTINGS.vSegments = FAST_SELECTION_MODE_NODE_TESSELATION;
                ModelShape superQuadricShape = new SuperQuadricShape(gl, SUPER_QUADRIC_SETTINGS, modelSettings);

                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef( 1.0f + SUPER_QUADRIC_SCALE_X.get(), 1.0f + SUPER_QUADRIC_SCALE_Y.get(), 1.0f + SUPER_QUADRIC_SCALE_Z.get() );
                gl.glRotatef(SUPER_QUADRIC_ROTATE_X.get(), 1.0f, 0.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_ROTATE_Y.get(), 0.0f, 1.0f, 0.0f);
                gl.glRotatef(SUPER_QUADRIC_ROTATE_Z.get(), 0.0f, 0.0f, 1.0f);
                superQuadricShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();

                superQuadricShape.disposeAllModelShapeResources(gl);
            }
            else if ( shape3D.equals(OBJ_MODEL_LOADER) )
            {
                // no need to reload the model for calculating spherical coords if it does not have textures as materials
                // (update: does indeed need to for spherical mapping in non-shader mode)
                // if ( (objModelLoaderShape != null) && !changeAllShapes && !objModelLoaderShape.getHasTexture() ) continue;

                // no need to reload the model for calculating spherical coords, just use current objModelLoaderShape.drawModelShape() geometry
                if (objModelLoaderShape == null)
                {
                    // if (objModelLoaderShape != null) objModelLoaderShape.disposeAllModelShapeResources(gl);
                    objModelLoaderShape = new OBJModelLoader(gl, graph, EXTERNAL_OBJ_MODEL_FILE_PATH,
                            EXTERNAL_OBJ_MODEL_FILE_NAME + ".obj", OBJ_MODEL_LOADER_SHAPE_SIZE.get(),
                            modelRenderingState, USE_EXTERNAL_OBJ_MODEL_FILE, false);
                }

                shapeIndex = OBJ_MODEL_LOADER.ordinal();
                gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], 1);
                gl.glNewList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

                gl.glPushMatrix();
                gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                gl.glScalef( 1.0f + OBJ_MODEL_LOADER_SCALE_X.get(), 1.0f + OBJ_MODEL_LOADER_SCALE_Y.get(), 1.0f + OBJ_MODEL_LOADER_SCALE_Z.get() );
                gl.glRotatef(OBJ_MODEL_LOADER_ROTATE_X.get(), 1.0f, 0.0f, 0.0f);
                gl.glRotatef(OBJ_MODEL_LOADER_ROTATE_Y.get(), 0.0f, 1.0f, 0.0f);
                gl.glRotatef(OBJ_MODEL_LOADER_ROTATE_Z.get(), 0.0f, 0.0f, 1.0f);
                objModelLoaderShape.drawModelShape(gl);
                gl.glPopMatrix();

                gl.glEndList();
            }
        }
    }

    /**
    *  Builds all display lists.
    */
    private void buildAllDisplayLists(GL2 gl)
    {
	//System.out.println("Building Display lists");
        
	// Rotate current view matrix and reset rotate values
	currentViewMatrix.rotateAboutXW(yzRotate);
	yzRotate = 0.0f;
	currentViewMatrix.rotateAboutYW(xzRotate);
	xzRotate = 0.0f;
	currentViewMatrix.rotateAboutXZ(ywRotate);
	ywRotate = 0.0f;
	currentViewMatrix.rotateAboutYZ(xwRotate);
	xwRotate = 0.0f;

	updateEdgesDisplayList = true;
	updateNodesDisplayList = true;
	
	//System.out.println(currentViewMatrix.toString());

        if (CHANGE_NODE_TESSELATION || CHANGE_SPHERICAL_MAPPING_ENABLED)
        {
            buildAllShapes3DDisplayLists(gl, false, CHANGE_NODE_TESSELATION, CHANGE_SPHERICAL_MAPPING_ENABLED);
            CHANGE_NODE_TESSELATION = false;
            CHANGE_SPHERICAL_MAPPING_ENABLED = false;
        }

        if (CHANGE_ALL_SHAPES)
        {
            buildAllShapes3DDisplayLists(gl);
            CHANGE_ALL_SHAPES = false;
            if (CHANGE_ALL_FAST_SELECTION_SHAPES)
            {
                buildAllShapes3DFastSelectionDisplayLists(gl);
                CHANGE_ALL_FAST_SELECTION_SHAPES = false;
            }
        }

        if (CHANGE_TEXTURE_ENABLED)
        {
            if ( checkForNodeTexturing() )
                nodeTexture = graph.prepareNodeTexture(gl, nodeTexture);
            CHANGE_TEXTURE_ENABLED = false;
        }

        if (ANIMATION_CHANGE_SPECTRUM_TEXTURE_ENABLED)
        {
            graph.prepareAnimationSpectrumTexture(gl);
            ANIMATION_CHANGE_SPECTRUM_TEXTURE_ENABLED = false;
        }

        if (CHANGE_GRAPHML_COMPONENT_CONTAINERS)
        {
            // if ( gl.glIsList(pathwayComponentContainersDisplayList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
            gl.glDeleteLists(pathwayComponentContainersDisplayList, 1);
            gl.glNewList(pathwayComponentContainersDisplayList, GL_COMPILE);
            drawPathwayComponentContainers3DMode(gl);
            gl.glEndList();

            CHANGE_GRAPHML_COMPONENT_CONTAINERS = false;
        }

        if (updateEdgesDisplayList)
        {
            // if allEdgesDisplayLists not empty, delete all its display lists
            if (allEdgesDisplayLists != null)
                for (int i = 0; i < allEdgesDisplayLists.capacity(); i++)
                    gl.glDeleteLists(allEdgesDisplayLists.get(i), 1);

            if ( !DISABLE_EDGES_RENDERING.get() )
                drawAllVisibleEdges(gl);

            updateEdgesDisplayList = false;
        }

        if (updateNodesDisplayList)
        {
            // if ( gl.glIsList(nodesDisplayList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
            gl.glDeleteLists(nodesDisplayList, 1);
            if ( FAST_SELECTION_MODE.get() )
            {
                // if ( gl.glIsList(fastSelectionNodesDisplayList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
                gl.glDeleteLists(fastSelectionNodesDisplayList, 1);
            }

            if ( !DISABLE_NODES_RENDERING.get() )
            {
                gl.glNewList(nodesDisplayList, GL_COMPILE);
                drawAllVisibleNodes(gl);
                gl.glEndList();
                if ( FAST_SELECTION_MODE.get() )
                {
                    gl.glNewList(fastSelectionNodesDisplayList, GL_COMPILE);
                    drawAllVisibleNodesForFastSelection(gl);
                    gl.glEndList();
                }
            }

            updateNodesDisplayList = false;
        }

        if (updateSelectedNodesDisplayList)
        {
            // if ( gl.glIsList(selectedNodesDisplayList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
            gl.glDeleteLists(selectedNodesDisplayList, 1);

            if ( !DISABLE_NODES_RENDERING.get() )
            {
                gl.glNewList(selectedNodesDisplayList, GL_COMPILE);
                drawAllSelectedNodes(gl);
                gl.glEndList();
            }

            updateSelectedNodesDisplayList = false;
        }
    }

    /**
    *  Draws all visible edges.
    */
    private void drawAllVisibleEdges(GL2 gl)
    {
        if (DEBUG_BUILD) println("Building Edge Display List");

        // for line antialiasing and blending options usage only
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        boolean useProportionalEdgesSizeToWeightRendering = WEIGHTED_EDGES && PROPORTIONAL_EDGES_SIZE_TO_WEIGHT.get();
        float lineWidth = DEFAULT_EDGE_SIZE.get();
        GraphNode node1 = null;
        GraphNode node2 = null;
        Point3D point1 = null;
        Point3D point2 = null;
        GraphmlNetworkContainer gnc = nc.getGraphmlNetworkContainer();
        float[] currentNode1GraphmlMapCoord = null;
        float[] currentNode2GraphmlMapCoord = null;
        Tuple6<String, Tuple2<float[], ArrayList<Point2D.Float>>, String[], String[], String[], String[]> edgeTuple6 = null;
        float x1 = 0.0f;
        float x2 = 0.0f;
        float y1 = 0.0f;
        float y2 = 0.0f;
        float z1 = 0.0f;
        float z2 = 0.0f;

        int howManyDisplayListsToCreate = visibleEdges.size() / EDGES_PER_DISPLAY_LIST;
        // add one extra display list if not perfect integer division (most of the cases) and not let it be zero
        if ( ( (visibleEdges.size() % EDGES_PER_DISPLAY_LIST) != 0 ) || (howManyDisplayListsToCreate == 0) )
            howManyDisplayListsToCreate++;

        if ( (allEdgesDisplayLists == null) || (howManyDisplayListsToCreate != prevHowManyDisplayListsToCreate) )
        {
            if (allEdgesDisplayLists != null)
                allEdgesDisplayLists.clear();

            // allocate new edge display lists
            allEdgesDisplayLists = Buffers.newDirectIntBuffer(howManyDisplayListsToCreate);
            for (int i = 0; i < howManyDisplayListsToCreate; i++)
                allEdgesDisplayLists.put( gl.glGenLists(1) );
            allEdgesDisplayLists.rewind();
        }

        prevHowManyDisplayListsToCreate = howManyDisplayListsToCreate;

        boolean glBegin = false;
        boolean glNewList = false;
        int edgeIndex = 0;
        int displayListIndex = 0;

        if (DEBUG_BUILD) println("GraphRenderer3D visibleEdges size: " + visibleEdges.size());
        for (GraphEdge edge : visibleEdges)
        {
            if ( (edgeIndex % EDGES_PER_DISPLAY_LIST) == 0 )
            {
                if (displayListIndex > 0 && glNewList)
                {
                    gl.glEndList();
                }

                gl.glNewList(allEdgesDisplayLists.get(displayListIndex), GL_COMPILE);
                glNewList = true;

                if (displayListIndex == 0 && !useProportionalEdgesSizeToWeightRendering)
                {
                    gl.glLineWidth(lineWidth);
                    gl.glBegin(GL_LINES); // GL_TRIANGLES
                    glBegin = true;
                }

                displayListIndex++;
            }
            edgeIndex++;

            if (useProportionalEdgesSizeToWeightRendering)
            {
                lineWidth = ( (lineWidth = ( DEFAULT_EDGE_SIZE.get() * edge.getScaledWeight() ) ) > 0.0f) ? lineWidth : 0.001f;
                gl.glLineWidth(lineWidth);
                gl.glBegin(GL_LINES); // GL_TRIANGLES
                glBegin = true;
            }

            node1 = edge.getNodeFirst();
            node2 = edge.getNodeSecond();

            if ( !node1.equals(node2) ) // don't need to draw an arrow to itself (for now)
            {
                if ( WEIGHTED_EDGES && COLOR_EDGES_BY_WEIGHT.get() )
                    edge.getColor().getRGBColorComponents(CURRENT_COLOR);
                else
                    DEFAULT_EDGE_COLOR.get().getRGBComponents(CURRENT_COLOR);

                if ( ANAGLYPH_STEREOSCOPIC_3D_VIEW.get() ) graph.createGrayScaleColor(CURRENT_COLOR);
                gl.glColor3fv(CURRENT_COLOR, 0);

                if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                {
                    currentNode1GraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node1.getNodeName() ).first;
                    currentNode2GraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node2.getNodeName() ).first;
                    point1 = new Point3D(currentNode1GraphmlMapCoord[2], currentNode1GraphmlMapCoord[3], currentNode1GraphmlMapCoord[4] + CANVAS_Z_SIZE / 2.0f);
                    point2 = new Point3D(currentNode2GraphmlMapCoord[2], currentNode2GraphmlMapCoord[3], currentNode2GraphmlMapCoord[4] + CANVAS_Z_SIZE / 2.0f);
                }
                else
                {
                    point1 = node1.getPoint();
                    point2 = node2.getPoint();
                }

                // beginning of line
                // gl.glVertex4f(point1.x / 100.0f - 5.0f, point1.y / 100.0f - 5.0f, point1.z / 100.0f - 5.0f, 0.0f);
                gl.glVertex3f(point1.getX(currentViewMatrix) / 100.0f - 5.0f, point1.getY(currentViewMatrix) / 100.0f - 5.0f, point1.getZ(currentViewMatrix) / 100.0f - 5.0f);
                System.out.println("Vertex3f in drawAllVisibleEdges:");
		System.out.println(point1.getX(currentViewMatrix)/100.0f - 5.0f);
		System.out.println(point1.getY(currentViewMatrix)/100.0f - 5.0f);
		System.out.println(point1.getZ(currentViewMatrix)/100.0f - 5.0f);

                if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                {
                    edgeTuple6 = gnc.getAllGraphmlEdgesMap().get( node1.getNodeName() + " " + node2.getNodeName() );
                    if (edgeTuple6 != null)
                    {
                        Point3D polylinePoint = null;
                        for (Point2D.Float polylinePoint2D : edgeTuple6.second.second)
                        {
                            polylinePoint = new Point3D(polylinePoint2D.x, polylinePoint2D.y, (point1.getZ() + point2.getZ()) / 2.0f);
                            // end of line
                            // gl.glVertex4f(polylinePoint.x / 100.0f - 5.0f, polylinePoint.y / 100.0f - 5.0f, polylinePoint.z / 100.0f - 5.0f, 1.0f);
                            // gl.glVertex4f((polylinePoint.x + 0.05f) / 100.0f - 5.0f, (polylinePoint.y + 0.05f) / 100.0f - 5.0f, (polylinePoint.z + 0.05f) / 100.0f - 5.0f, 2.0f);
                            gl.glVertex3f(polylinePoint.x / 100.0f - 5.0f, polylinePoint.y / 100.0f - 5.0f, polylinePoint.z / 100.0f - 5.0f);
                            // beginning of next line
                            // gl.glVertex4f(polylinePoint.x / 100.0f - 5.0f, polylinePoint.y / 100.0f - 5.0f, polylinePoint.z / 100.0f - 5.0f, 0.0f);
                            gl.glVertex3f(polylinePoint.x / 100.0f - 5.0f, polylinePoint.y / 100.0f - 5.0f, polylinePoint.z / 100.0f - 5.0f);
                        }
                    }
                }
                // end of line
                // gl.glVertex4f(point2.x / 100.0f - 5.0f, point2.y / 100.0f - 5.0f, point2.z / 100.0f - 5.0f, 1.0f);
                // gl.glVertex4f((point2.x + 0.05f) / 100.0f - 5.0f, (point2.y + 0.05f) / 100.0f - 5.0f, (point2.z + 0.05f) / 100.0f - 5.0f, 2.0f);
                gl.glVertex3f(point2.getX(currentViewMatrix) / 100.0f - 5.0f, point2.getY(currentViewMatrix) / 100.0f - 5.0f, point2.getZ(currentViewMatrix) / 100.0f - 5.0f);
            System.out.println("Vertex3f in drawAllVisibleEdges:");
		System.out.println(point2.getX(currentViewMatrix)/100.0f - 5.0f);
		System.out.println(point2.getY(currentViewMatrix)/100.0f - 5.0f);
		System.out.println(point2.getZ(currentViewMatrix)/100.0f - 5.0f);

	    }

            if (useProportionalEdgesSizeToWeightRendering && glBegin)
            {
                gl.glEnd();
            }
        }

        if (!useProportionalEdgesSizeToWeightRendering && glBegin)
        {
            gl.glEnd();
        }

        // make sure to disable shaders before the 2D rendering of node labels, but also need to disable shaders out of the display lists to avoid horribly slow FPSs!
        // shaderLinesSFXs.disableShaders(gl);

        for (GraphEdge edge : visibleEdges)
        {
            if ( edge.isShowEdgeName() )
            {
                node1 = edge.getNodeFirst();
                node2 = edge.getNodeSecond();

                if ( !node1.equals(node2) ) // don't need to draw an arrow to itself (for now)
                {
                    if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                    {
                        currentNode1GraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node1.getNodeName() ).first;
                        currentNode2GraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node2.getNodeName() ).first;
                        point1 = new Point3D(currentNode1GraphmlMapCoord[2], currentNode1GraphmlMapCoord[3], currentNode1GraphmlMapCoord[4] + CANVAS_Z_SIZE / 2.0f);
                        point2 = new Point3D(currentNode2GraphmlMapCoord[2], currentNode2GraphmlMapCoord[3], currentNode2GraphmlMapCoord[4] + CANVAS_Z_SIZE / 2.0f);
                    }
                    else
                    {
                        point1 = node1.getPoint();
                        point2 = node2.getPoint();
                    }

                    if ( WEIGHTED_EDGES && COLOR_EDGES_BY_WEIGHT.get() )
                        edge.getColor().getRGBColorComponents(CURRENT_COLOR);
                    else
                        DEFAULT_EDGE_COLOR.get().getRGBComponents(CURRENT_COLOR);

                    if ( ANAGLYPH_STEREOSCOPIC_3D_VIEW.get() ) graph.createGrayScaleColor(CURRENT_COLOR);
                    gl.glColor3fv(CURRENT_COLOR, 0);

                    if ( nc.getIsGraphml() )
                    {
                        if (edge.getEdgeName() != null)
                        {
                            x1 = (point1.getX(currentViewMatrix) / 100.0f - 5.0f);
                            x2 = (point2.getX(currentViewMatrix) / 100.0f - 5.0f);
                            y1 = (point1.getY(currentViewMatrix) / 100.0f - 5.0f);
                            y2 = (point2.getY(currentViewMatrix) / 100.0f - 5.0f);
                            z1 = (point1.getZ(currentViewMatrix) / 100.0f - 5.0f);
                            z2 = (point2.getZ(currentViewMatrix) / 100.0f - 5.0f);

			System.out.println("Coords in drawAllVisibleEdges:");
		System.out.println(point1.getX(currentViewMatrix)/100.0f - 5.0f);
		System.out.println(point1.getY(currentViewMatrix)/100.0f - 5.0f);
		System.out.println(point1.getZ(currentViewMatrix)/100.0f - 5.0f);


                            gl.glRasterPos3f(x1 - (x1 - x2) / 2.0f, y1 - (y1 - y2) / 2.0f - 0.025f, z1 - (z1 - z2) / 2.0f);
                            GLUT.glutBitmapString( EDGE_NAMES_OPENGL_FONT_TYPE, edge.getEdgeName() );
                        }
                    }
                    else
                    {
                        x1 = (point1.getX(currentViewMatrix) / 100.0f - 5.0f);
                        x2 = (point2.getX(currentViewMatrix) / 100.0f - 5.0f);
                        y1 = (point1.getY(currentViewMatrix) / 100.0f - 5.0f);
                        y2 = (point2.getY(currentViewMatrix) / 100.0f - 5.0f);
                        z1 = (point1.getZ(currentViewMatrix) / 100.0f - 5.0f);
                        z2 = (point2.getZ(currentViewMatrix) / 100.0f - 5.0f);

			System.out.println("Coords in drawAllVisibleEdges:");
		System.out.println(point1.getX(currentViewMatrix)/100.0f - 5.0f);
		System.out.println(point1.getY(currentViewMatrix)/100.0f - 5.0f);
		System.out.println(point1.getZ(currentViewMatrix)/100.0f - 5.0f);


                        gl.glRasterPos3f(x1 - (x1 - x2) / 2.0f, y1 - (y1 - y2) / 2.0f - 0.025f, z1 - (z1 - z2) / 2.0f);
                        GLUT.glutBitmapString( EDGE_NAMES_OPENGL_FONT_TYPE, NUMBER_FORMAT.format( edge.getWeight() ) );
                    }
                }
            }
        }

        if (glNewList)
        {
            gl.glEndList();
        }

        if (DEBUG_BUILD) println("Done");
    }

    /**
    *  Draws all visible nodes.
    */
    private void drawAllVisibleNodes(GL2 gl)
    {
        if (DEBUG_BUILD) println("Building Node Display List");

        if ( !MATERIAL_SMOOTH_SHADING.get() )
            gl.glShadeModel(GL_FLAT);
        else
            gl.glShadeModel(GL_SMOOTH);

        if ( !DEPTH_FOG.get() )
            gl.glDisable(GL_FOG);
        else
            gl.glEnable(GL_FOG);

        // Enable blending, using the SrcOver rule
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_NORMALIZE);

        if ( checkForNodeTexturing() )
        {
            // determine which areas of the polygon are to be renderered
            gl.glEnable(GL_ALPHA_TEST);
            gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0

            gl.glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

            enableDisableNodeTexture = false;
        }

        GraphmlNetworkContainer gnc = nc.getGraphmlNetworkContainer();
        float ratioX = ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() ) ? (float)width  / gnc.getRangeX() : 0.0f;
        float ratioY = ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() ) ? (float)height / gnc.getRangeY() : 0.0f;
        float extraSizeAmoutValueForYEdStyleRendering = ( 1.0f / ( (ratioX > ratioY) ? ratioX : ratioY ) );
        float nodeScaleValue = 0.0f;
        Point3D point = null;
        float[] currentNodeGraphmlMapCoord = null;
        Color nodeColor = null;

        if (DEBUG_BUILD) println("GraphRenderer3D visibleNodes size: " + visibleNodes.size());
        for (GraphNode node : visibleNodes)
        {
            nodeScaleValue = node.getNodeSize();
            nodeColor = node.getColor();

            if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
            {
                currentNodeGraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node.getNodeName() ).first;
                point = new Point3D(currentNodeGraphmlMapCoord[2], currentNodeGraphmlMapCoord[3], currentNodeGraphmlMapCoord[4] + CANVAS_Z_SIZE / 2.0f);
                nodeScaleValue *= extraSizeAmoutValueForYEdStyleRendering;
            }
            else
                point = node.getPoint();

            if (animationRender)
            {
                if ( ( ( ANIMATION_SELECTED_NODES_ANIMATION_ONLY.get() ) ? ( ( !selectionManager.getSelectedNodes().isEmpty() ) ? selectionManager.getSelectedNodes().contains(node) : true ) : true ) )
                {
                    if ( !node.ismEPNTransition() )
                    {
                        if ( ( !DATA_TYPE.equals(DataTypes.EXPRESSION) && ANIMATION_MEPN_COMPONENTS_ANIMATION_ONLY.get() ) ? node.ismEPNComponent() : true)
                        {
                            Tuple6<Float, Color, Boolean, Float, Boolean, Float> tuple6 = AnimationVisualization.performAnimationVisualization(true, node.getNodeID(), node.getNodeName(), layoutFrame.isAllShadingSFXSValueEnabled(), nodeColor, currentTick, animationFrameCount, animationSpectrumImage, DATA_TYPE.equals(DataTypes.EXPRESSION));
                            nodeScaleValue = tuple6.first;
                            nodeColor = tuple6.second;

                            if (tuple6.third)
                                enableShaders(gl, true, false, true, tuple6.fourth, tuple6.fifth, tuple6.sixth);
                        }
                        else
                            enableShaders(gl, true);
                    }
                    else
                        enableShaders( gl, true, USE_SPN_ANIMATED_TRANSITIONS_SHADING.get() ); // transition node, enforce the Voronoi shader
                }
                else
                    enableShaders(gl, true);
            }

            drawNode(gl, point, nodeColor, ( (TRANSPARENT.get() ) ? node.getTransparencyAlpha() : 1.0f), node.getNodeID(), node.getNode3DShape(), nodeScaleValue, true);

            if (animationRender)
                disableShaders(gl);
        }

        if ( checkForNodeTexturing() )
        {
            gl.glDisable(GL_ALPHA_TEST);

            if (enableDisableNodeTexture)
            {
                if ( TEXTURE_ENABLED.get() && !SHOW_3D_ENVIRONMENT_MAPPING.get() )
                    nodeTexture.disable(gl);
                else if ( USE_GL_EXT_FRAMEBUFFER_OBJECT && SHOW_3D_ENVIRONMENT_MAPPING.get() )
                    renderToTexture.disable(gl);
            }
        }

        // make sure to disable shaders before the 2D rendering of node labels, but also need to disable shaders out of the display lists to avoid horribly slow FPSs!
        disableShaders(gl);

        gl.glDisable(GL_LIGHTING);
        gl.glDisable(GL_NORMALIZE);

        String nodeName = "";
        boolean isSelectedNodesAnimation = false;
        boolean defineOnce = false;
        for (GraphNode node : visibleNodes)
        {
            if (!animationRender)
            {
                if ( node.isShowNodeName() )
                {
                    if (!defineOnce)
                    {
                        defineOnce = true;
                        gl.glDisable(GL_DEPTH_TEST);
                        if (CUSTOMIZE_NODE_NAMES_NAME_RENDERING_TYPE.get() == 0)
                        {
                            gl.glEnable(GL_COLOR_LOGIC_OP);
                            gl.glLogicOp(GL_EQUIV);
                        }
                    }

                    if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                    {
                        currentNodeGraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node.getNodeName() ).first;
                        point = new Point3D(currentNodeGraphmlMapCoord[2], currentNodeGraphmlMapCoord[3], currentNodeGraphmlMapCoord[4] + CANVAS_Z_SIZE / 2.0f);
                    }
                    else
                        point = node.getPoint();

                    // float offset = UNIT_SPHERE_SIZE * (float)node.getNodeSize();
                    // gl.glRasterPos3f( (point.x / 100.0f - 5.0f) + offset, (point.y / 100.0f - 5.0f) + offset, (point.z / 100.0f - 5.0f) );
                    Color.BLACK.getRGBComponents(CURRENT_COLOR);
                    if ( ANAGLYPH_STEREOSCOPIC_3D_VIEW.get() ) graph.createGrayScaleColor(CURRENT_COLOR);
                    gl.glColor3fv(CURRENT_COLOR, 0);
                    gl.glRasterPos3f( (point.getX(currentViewMatrix) / 100.0f - 5.0f), (point.getY(currentViewMatrix) / 100.0f - 5.0f), (point.getZ(currentViewMatrix) / 100.0f - 5.0f) );

		    System.out.println("RasterPos point coordinates in drawAllVisibleNodes:");
		    System.out.println(point.getX(currentViewMatrix)/100.0f - 5.0f);
		    System.out.println(point.getY(currentViewMatrix)/100.0f - 5.0f);
		    System.out.println(point.getZ(currentViewMatrix)/100.0f - 5.0f);

                    nodeName = Graph.customizeNodeName( nc.getNodeName( node.getNodeName() ) );
                    if (CUSTOMIZE_NODE_NAMES_NAME_RENDERING_TYPE.get() != 0)
                        graph.drawNodeNameBackgroundLegend(gl, node, nodeName);
                    GLUT.glutBitmapString(NODE_NAMES_OPENGL_FONT_TYPE.ordinal() + 2, nodeName); // + 2 for GLUT public static variables ordering for excluding STROKE_ROMAN/STROKE_MONO_ROMAN
                }
            }
            else
            {
                isSelectedNodesAnimation = ( ( ANIMATION_SELECTED_NODES_ANIMATION_ONLY.get() ) ? ( ( !selectionManager.getSelectedNodes().isEmpty() ) ? selectionManager.getSelectedNodes().contains(node) : true ) : true );
                if ( node.isShowNodeName() || (!node.ismEPNTransition() && isSelectedNodesAnimation) )
                {
                    if (!defineOnce)
                    {
                        defineOnce = true;
                        gl.glDisable(GL_DEPTH_TEST);
                        if (CUSTOMIZE_NODE_NAMES_NAME_RENDERING_TYPE.get() == 0)
                        {
                            gl.glEnable(GL_COLOR_LOGIC_OP);
                            gl.glLogicOp(GL_EQUIV);
                        }
                    }

                    if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                    {
                        currentNodeGraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node.getNodeName() ).first;
                        point = new Point3D(currentNodeGraphmlMapCoord[2], currentNodeGraphmlMapCoord[3], currentNodeGraphmlMapCoord[4] + CANVAS_Z_SIZE / 2.0f);
                    }
                    else
                        point = node.getPoint();

                    // float offset = UNIT_SPHERE_SIZE * (float)node.getNodeSize();
                    // gl.glRasterPos3f( (point.x / 100.0f - 5.0f) + offset, (point.y / 100.0f - 5.0f) + offset, (point.z / 100.0f - 5.0f) );
                    Color.BLACK.getRGBComponents(CURRENT_COLOR);
                    if ( ANAGLYPH_STEREOSCOPIC_3D_VIEW.get() ) graph.createGrayScaleColor(CURRENT_COLOR);
                    gl.glColor3fv(CURRENT_COLOR, 0);
                    gl.glRasterPos3f( (point.getX(currentViewMatrix) / 100.0f - 5.0f), (point.getY(currentViewMatrix) / 100.0f - 5.0f), (point.getZ(currentViewMatrix) / 100.0f - 5.0f) );

		    System.out.println("RasterPos point coordinates in drawAllVisibleNodes:");
		    System.out.println(point.getX(currentViewMatrix)/100.0f - 5.0f);
		    System.out.println(point.getY(currentViewMatrix)/100.0f - 5.0f);
		    System.out.println(point.getZ(currentViewMatrix)/100.0f - 5.0f);

		    nodeName = Graph.customizeNodeName( nc.getNodeName( node.getNodeName() ) );
                    // if-else commands below have to be like this to be able to properly selectively render names
                    if ( node.isShowNodeName() && !ANIMATION_SHOW_NODE_ANIMATION_VALUE.get() )
                    {
                        if (CUSTOMIZE_NODE_NAMES_NAME_RENDERING_TYPE.get() != 0)
                            graph.drawNodeNameBackgroundLegend(gl, node, nodeName);
                        GLUT.glutBitmapString(NODE_NAMES_OPENGL_FONT_TYPE.ordinal() + 2, nodeName); // + 2 for GLUT public static variables ordering for excluding STROKE_ROMAN/STROKE_MONO_ROMAN
                    }
                    else if ( !node.ismEPNTransition() && isSelectedNodesAnimation && ANIMATION_SHOW_NODE_ANIMATION_VALUE.get() )
                    {
                        if ( !nodeName.isEmpty() )
                            nodeName += ": ";
                        nodeName += NUMBER_FORMAT.format( AnimationVisualization.getAnimationVisualizationNodeValue(node.getNodeID(), node.getNodeName(), currentTick, animationFrameCount, DATA_TYPE.equals(DataTypes.EXPRESSION)) );
                        if (CUSTOMIZE_NODE_NAMES_NAME_RENDERING_TYPE.get() != 0)
                            graph.drawNodeNameBackgroundLegend(gl, node, nodeName);
                        GLUT.glutBitmapString(NODE_NAMES_OPENGL_FONT_TYPE.ordinal() + 2, nodeName); // + 2 for GLUT public static variables ordering for excluding STROKE_ROMAN/STROKE_MONO_ROMAN
                    }
                }
            }
        }

        if (defineOnce)
        {
            defineOnce = false;
            if (CUSTOMIZE_NODE_NAMES_NAME_RENDERING_TYPE.get() == 0)
                gl.glDisable(GL_COLOR_LOGIC_OP);
            gl.glEnable(GL_DEPTH_TEST);
        }

        if (DEBUG_BUILD) println("Done");
    }

    /**
    *  Draws all visible nodes for fast selection.
    */
    private void drawAllVisibleNodesForFastSelection(GL2 gl)
    {
        if (DEBUG_BUILD) println("Building Node Display List For Fast Selection");

        GraphmlNetworkContainer gnc = nc.getGraphmlNetworkContainer();
        float ratioX = ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() ) ? (float)width  / gnc.getRangeX() : 0.0f;
        float ratioY = ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() ) ? (float)height / gnc.getRangeY() : 0.0f;
        float extraSizeAmoutValueForYEdStyleRendering = ( 1.0f / ( (ratioX > ratioY) ? ratioX : ratioY ) );
        float nodeScaleValue = 0.0f;
        Point3D point = null;
        float[] currentNodeGraphmlMapCoord = null;

        if (DEBUG_BUILD) println("GraphRenderer3D visibleNodes size: " + visibleNodes.size());
        for (GraphNode node : visibleNodes)
        {
            nodeScaleValue = node.getNodeSize();

            if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
            {
                currentNodeGraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node.getNodeName() ).first;
                point = new Point3D(currentNodeGraphmlMapCoord[2], currentNodeGraphmlMapCoord[3], currentNodeGraphmlMapCoord[4] + CANVAS_Z_SIZE / 2.0f);
                nodeScaleValue *= extraSizeAmoutValueForYEdStyleRendering;
            }
            else
                point = node.getPoint();

            gl.glLoadName( node.getNodeID() );
            drawNode3DShape(gl, point.getX(currentViewMatrix) / 100.0f - 5.0f, point.getY(currentViewMatrix) / 100.0f - 5.0f, point.getZ(currentViewMatrix) / 100.0f - 5.0f, choose3DShape( node.getNode3DShape() ), UNIT_SHAPE_SIZE * nodeScaleValue, true);

		System.out.println("Call to drawNode3DShape:");
		    System.out.println(point.getX(currentViewMatrix)/100.0f - 5.0f);
		    System.out.println(point.getY(currentViewMatrix)/100.0f - 5.0f);
		    System.out.println(point.getZ(currentViewMatrix)/100.0f - 5.0f);

        }

        if (DEBUG_BUILD) println("Done");
    }

    /**
    *  Draws all selected nodes.
    */
    private void drawAllSelectedNodes(GL2 gl)
    {
        if (DEBUG_BUILD) println("Building Selected Display List");

	System.out.println("Building Selected Display List");

        HashSet<GraphNode> selectedNodes = selectionManager.getSelectedNodes();
        if ( !selectedNodes.isEmpty() )
        {
            float transparencyValue = 0.0f;
            float selectedNodeOffsetValue = 0.0f;

            if ( !WIREFRAME_SELECTION_MODE.get() )
            {
                if ( !MATERIAL_SMOOTH_SHADING.get() )
                    gl.glShadeModel(GL_FLAT);
                else
                    gl.glShadeModel(GL_SMOOTH);

                if ( !DEPTH_FOG.get() )
                    gl.glDisable(GL_FOG);
                else
                    gl.glEnable(GL_FOG);

                // Enable blending, using the SrcOver rule
                gl.glEnable(GL_BLEND);
                gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

                gl.glEnable(GL_LIGHTING);
                gl.glEnable(GL_NORMALIZE);

                transparencyValue = Float.MIN_VALUE;
                selectedNodeOffsetValue = 0.1f;
            }
            else
            {
                gl.glLineWidth(1.5f);
                // gl.glPolygonOffset(-1.0f, -1.0f);
                // gl.glEnable(GL_POLYGON_OFFSET_LINE);
                gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

                transparencyValue = 1.0f;
                selectedNodeOffsetValue = 0.005f;
            }

            GraphmlNetworkContainer gnc = nc.getGraphmlNetworkContainer();
            float ratioX = ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() ) ? (float)width  / gnc.getRangeX() : 0.0f;
            float ratioY = ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() ) ? (float)height / gnc.getRangeY() : 0.0f;
            float extraSizeAmoutValueForYEdStyleRendering = ( 1.0f / ( (ratioX > ratioY) ? ratioX : ratioY ) );
            float nodeScaleValue = 0.0f;
            Point3D point = null;
            float[] currentNodeGraphmlMapCoord = null;

            if (DEBUG_BUILD) println("GraphRenderer3D selectedNodes size: " + selectedNodes.size());
            for (GraphNode node : selectedNodes)
            {
                nodeScaleValue = node.getNodeSize();
                if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                {
                    currentNodeGraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node.getNodeName() ).first;
                    point = new Point3D(currentNodeGraphmlMapCoord[2], currentNodeGraphmlMapCoord[3], currentNodeGraphmlMapCoord[4] + CANVAS_Z_SIZE / 2.0f);
                    nodeScaleValue *= extraSizeAmoutValueForYEdStyleRendering;
                }
                else
                    point = node.getPoint();

                drawNode(gl, point, SELECTION_COLOR.get(), transparencyValue, node.getNodeID(), node.getNode3DShape(), nodeScaleValue * (1.0f + selectedNodeOffsetValue), false);
        	System.out.println("Sending point3D to drawNode");
	    }

            if ( !WIREFRAME_SELECTION_MODE.get() )
            {
                gl.glDisable(GL_LIGHTING);
                gl.glDisable(GL_NORMALIZE);
            }
            else
            {
                gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                // gl.glDisable(GL_POLYGON_OFFSET_LINE);
            }
        }

        if (DEBUG_BUILD) println("Done");
    }

    /**
    *  Draws the node.
    */
    private void drawNode(GL2 gl, Point3D point, Color color, float alpha, int name, Shapes3D shape, float size, boolean normal)
    {
	System.out.println("drawNode function...");

        useNodeMaterial(gl);
        boolean enableDepthMask = false;

        if (normal)
        {
            if ( !FAST_SELECTION_MODE.get() )
                gl.glLoadName(name);

            color.getRGBComponents(CURRENT_COLOR);
            if ( ANAGLYPH_STEREOSCOPIC_3D_VIEW.get() ) graph.createGrayScaleColor(CURRENT_COLOR);
            CURRENT_COLOR[3] = alpha;

            enableDepthMask = (alpha >= 1.0f);
            // warning the polygon-based shapes do not have texture coords defined (undefined) but they do have normals
            if ( checkForNodeTexturing() && hasShapeTextureCoords(shape) )
            {
                if (!enableDisableNodeTexture)
                {
                    enableDisableNodeTexture = true;

                    if ( TEXTURE_ENABLED.get() && !SHOW_3D_ENVIRONMENT_MAPPING.get() )
                    {
                        nodeTexture.bind(gl);
                        nodeTexture.enable(gl);
                    }
                    else if ( USE_GL_EXT_FRAMEBUFFER_OBJECT && SHOW_3D_ENVIRONMENT_MAPPING.get() )
                    {
                        renderToTexture.bind(gl);
                        renderToTexture.enable(gl);
                    }
                }
            }
            else
            {
                if (enableDisableNodeTexture)
                {
                    enableDisableNodeTexture = false;

                    if ( TEXTURE_ENABLED.get() && !SHOW_3D_ENVIRONMENT_MAPPING.get() )
                        nodeTexture.disable(gl);
                    else if ( USE_GL_EXT_FRAMEBUFFER_OBJECT && SHOW_3D_ENVIRONMENT_MAPPING.get() )
                        renderToTexture.disable(gl);
                }
            }

            gl.glColor4fv(CURRENT_COLOR, 0);

            if (!enableDepthMask)
                gl.glDepthMask(false); // Makes the z-buffer read-only for any translucent polygons (GL_FALSE for alpha < 1.0f)
        }
        else
        {
            if ( !WIREFRAME_SELECTION_MODE.get() )
            {
                CURRENT_COLOR[3] = alpha;
                gl.glDepthMask(false); // Makes the z-buffer read-only for any translucent polygons (GL_FALSE for alpha < 1.0f)
            }

            SELECTION_COLOR.get().getRGBColorComponents(CURRENT_COLOR);
            if ( ANAGLYPH_STEREOSCOPIC_3D_VIEW.get() ) graph.createGrayScaleColor(CURRENT_COLOR);
            gl.glColor4fv(CURRENT_COLOR, 0);
        }
	
	//drawNode3DShape arguments determine where a node gets put.
        drawNode3DShape(gl, point.getX(currentViewMatrix) / 100.0f - 5.0f, point.getY(currentViewMatrix) / 100.0f - 5.0f, point.getZ(currentViewMatrix) / 100.0f - 5.0f, choose3DShape(shape), UNIT_SHAPE_SIZE * size, false);


	System.out.println("Call to drawNode3DShape:");
		    System.out.println(point.getX(currentViewMatrix)/100.0f - 5.0f);
		    System.out.println(point.getY(currentViewMatrix)/100.0f - 5.0f);
		    System.out.println(point.getZ(currentViewMatrix)/100.0f - 5.0f);

        // Re-enable the z-buffer to avoid artifacts
        if (!enableDepthMask)
            gl.glDepthMask(true);
    }

   /**
    *  Draws the node 3D shape.
    */
    private void drawNode3DShape(GL2 gl, float coordX, float coordY, float coordZ, Shapes3D shape3D, float size, boolean isFastSelectionNode)
    {
	System.out.println("drawNode3DShape function...");
        switch (shape3D)
        {
            case SPHERE:

                draw3DShape(gl, coordX, coordY, coordZ, SPHERE, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case POINT:
                gl.glPointSize(200 * size);
                gl.glBegin(GL_POINTS);
                gl.glVertex3f(coordX, coordY, coordZ);
                gl.glEnd();

                break;

            case CUBE:

                draw3DShape(gl, coordX, coordY, coordZ, CUBE, size, 0.6f, isFastSelectionNode);

                break;

            case TETRAHEDRON:

                draw3DShape(gl, coordX, coordY, coordZ, TETRAHEDRON, size, 0.6f, isFastSelectionNode);

                break;

            case OCTAHEDRON:

                draw3DShape(gl, coordX, coordY, coordZ, OCTAHEDRON, size, 0.6f, isFastSelectionNode);

                break;

            case DODECAHEDRON:

                draw3DShape(gl, coordX, coordY, coordZ, DODECAHEDRON, size, 0.3f, isFastSelectionNode);

                break;

            case ICOSAHEDRON:

                draw3DShape(gl, coordX, coordY, coordZ, ICOSAHEDRON, size, 0.6f, isFastSelectionNode);

                break;

            case CONE_LEFT:

                draw3DShape(gl, coordX, coordY, coordZ, CONE_LEFT, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case CONE_RIGHT:

                draw3DShape(gl, coordX, coordY, coordZ, CONE_RIGHT, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case TRAPEZOID_UP:

                draw3DShape(gl, coordX, coordY, coordZ, TRAPEZOID_UP, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case TRAPEZOID_DOWN:

                draw3DShape(gl, coordX, coordY, coordZ, TRAPEZOID_DOWN, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case CYLINDER:

                draw3DShape(gl, coordX, coordY, coordZ, CYLINDER, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case TORUS:

                draw3DShape(gl, coordX, coordY, coordZ, TORUS, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case RECTANGLE_VERTICAL:

                draw3DShape(gl, coordX, coordY, coordZ, RECTANGLE_VERTICAL, size, 0.6f, isFastSelectionNode);

                break;

            case RECTANGLE_HORIZONTAL:

                draw3DShape(gl, coordX, coordY, coordZ, RECTANGLE_HORIZONTAL, size, 0.6f, isFastSelectionNode);

                break;

            case ROUND_CUBE_THIN:

                draw3DShape(gl, coordX, coordY, coordZ, ROUND_CUBE_THIN, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case ROUND_CUBE_LARGE:

                draw3DShape(gl, coordX, coordY, coordZ, ROUND_CUBE_LARGE, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case PINEAPPLE_SLICE_TOROID:

                draw3DShape(gl, coordX, coordY, coordZ, PINEAPPLE_SLICE_TOROID, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case PINEAPPLE_SLICE_ELLIPSOID:

                draw3DShape(gl, coordX, coordY, coordZ, PINEAPPLE_SLICE_ELLIPSOID, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case DOUBLE_PYRAMID_THIN:

                draw3DShape(gl, coordX, coordY, coordZ, DOUBLE_PYRAMID_THIN, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case DOUBLE_PYRAMID_LARGE:

                draw3DShape(gl, coordX, coordY, coordZ, DOUBLE_PYRAMID_LARGE, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case TORUS_8_PETALS:

                draw3DShape(gl, coordX, coordY, coordZ, TORUS_8_PETALS, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case SAUCER_4_PETALS:

                draw3DShape(gl, coordX, coordY, coordZ, SAUCER_4_PETALS, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case GENE_MODEL:

                draw3DShape(gl, coordX, coordY, coordZ, GENE_MODEL, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case LATHE_3D:

                draw3DShape(gl, coordX, coordY, coordZ, LATHE_3D, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case SUPER_QUADRIC:

                draw3DShape(gl, coordX, coordY, coordZ, SUPER_QUADRIC, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case OBJ_MODEL_LOADER:

                draw3DShape(gl, coordX, coordY, coordZ, OBJ_MODEL_LOADER, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            case DUMB_BELL:

                draw3DShape(gl, coordX, coordY, coordZ, DUMB_BELL, size / 1.5f, 1.0f, isFastSelectionNode);

                break;

            default: // default case draw the sphere

                draw3DShape(gl, coordX, coordY, coordZ, SPHERE, size / 1.5f, 1.0f, isFastSelectionNode);

                break;
        }
    }

    /**
    *  Draws the 3D shape.
    */
    private void draw3DShape(GL2 gl, float coordX, float coordY, float coordZ, Shapes3D shape3D, float size, float factor, boolean isFastSelectionNode)
    {
	System.out.println("draw3DShape function...");
    	// Duplicate the matrix at the top of the stack (now the top two matrices are the same.    	
    	gl.glPushMatrix();
    	// Translate the top matrix (makes a 4*4 where right hand column is x,y,z.)
        gl.glTranslatef(coordX, coordY, coordZ);

	System.out.println("Translate matrix for draw3DShape:");
	System.out.println(coordX);
	System.out.println(coordY);
	System.out.println(coordZ);

        gl.glScalef(factor * size, factor * size, factor * size);
        // Execute the commands in the display list.
        if (isFastSelectionNode)
            gl.glCallList(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[shape3D.ordinal()]);
        else
            gl.glCallList(ALL_SHAPES_3D_DISPLAY_LISTS[shape3D.ordinal()]);
        gl.glPopMatrix();
    }

    /**
    *  Draws the 3D shadows.
    */
    private void draw3DShadows(GL2 gl)
    {
        gl.glPushMatrix();
        gl.glDisable(GL_LIGHT0);
        gl.glDisable(GL_LIGHTING);
        gl.glDisable(GL_DEPTH_TEST);
        gl.glTranslatef(0.0f, 9.0f, 5.0f);

        SHADOW_PROJECTION_MATRIX.put( 7, -1.0f / LIGHT_POSITION[1].get() );
        gl.glMultMatrixf(SHADOW_PROJECTION_MATRIX);
        if ( !DISABLE_EDGES_RENDERING.get() && (allEdgesDisplayLists != null) )
            gl.glCallLists(allEdgesDisplayLists.capacity(), GL_INT, allEdgesDisplayLists);
        if ( !DISABLE_NODES_RENDERING.get() && (SHOW_NODES.get() || !isInMotion) )
        {
            enableShaders(gl, true);
            gl.glCallList(nodesDisplayList);
            disableShaders(gl);
            enableShaders(gl, false);
            gl.glCallList(selectedNodesDisplayList);
            disableShaders(gl);
        }

        gl.glEnable(GL_DEPTH_TEST);
        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_LIGHT0);
        gl.glPopMatrix();
    }

    /**
    *  Initializes the 3D environment mapping.
    */
    private void init3DEnviromentMapping(GL2 gl)
    {
        int ratioFactor = HIGH_QUALITY_ANTIALIASING.get() ? 1 : ( NORMAL_QUALITY_ANTIALIASING.get() ? 2 : 4 );
        if (DEBUG_BUILD) println("Render-to-texture ratioFactor: " + ratioFactor);

        renderToTexture.disposeAllRenderToTextureResources(gl);
        renderToTexture.initAllRenderToTextureResources(gl, width / ratioFactor, height / ratioFactor);
    }

    /**
    *  Draws the 3D environment mapping.
    */
    private void draw3DEnvironmentMapping(GL2 gl)
    {
        // enable low quality rendering for polygons (if needed) for the environment mapping to avoid artifacts
        if (qualityRendering)
            gl.glDisable(GL_POLYGON_SMOOTH);

        renderToTexture.startRender(gl);

        clearScreen3D(gl);
        renderScene3D(gl, false);

        renderToTexture.finishRender(gl);

        // re-enable high quality rendering for polygons (if needed) for the environment mapping to avoid artifacts
        if (qualityRendering)
            gl.glEnable(GL_POLYGON_SMOOTH);
    }

    /**
    *  Draws the pathway component containers in 3D mode.
    */
    private void drawPathwayComponentContainers3DMode(GL2 gl)
    {
        // Enable blending, using the SrcOver rule
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // determine which areas of the polygon are to be renderered
        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0

        Rectangle2D.Float rectangle2D = null;
        gl.glBegin(GL_QUADS);
        for ( GraphmlComponentContainer pathwayComponentContainer : nc.getGraphmlNetworkContainer().getAllPathwayComponentContainersFor3D() )
        {
            pathwayComponentContainer.color.getRGBComponents(CURRENT_COLOR);
            if ( ANAGLYPH_STEREOSCOPIC_3D_VIEW.get() ) graph.createGrayScaleColor(CURRENT_COLOR);
            CURRENT_COLOR[3] = pathwayComponentContainer.alpha;
            gl.glColor4fv(CURRENT_COLOR, 0);
            rectangle2D = pathwayComponentContainer.rectangle2D;

            // upper surface quad
            gl.glVertex3f( (rectangle2D.x - rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y - rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f + (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Bottom Left Of The Quad
            gl.glVertex3f( (rectangle2D.x + rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y - rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f + (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Bottom Right Of The Quad
            gl.glVertex3f( (rectangle2D.x + rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y + rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f + (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Top Right Of The Quad
            gl.glVertex3f( (rectangle2D.x - rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y + rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f + (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Top Left Of The Quad

            // lower surface quad
            gl.glVertex3f( (rectangle2D.x - rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y - rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f - (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Bottom Left Of The Quad
            gl.glVertex3f( (rectangle2D.x + rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y - rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f - (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Bottom Right Of The Quad
            gl.glVertex3f( (rectangle2D.x + rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y + rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f - (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Top Right Of The Quad
            gl.glVertex3f( (rectangle2D.x - rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y + rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f - (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Top Left Of The Quad

            // left surfarce quad
            gl.glVertex3f( (rectangle2D.x - rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y - rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f + (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Upper Bottom Left Of The Quad
            gl.glVertex3f( (rectangle2D.x - rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y + rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f + (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Upper Top Left Of The Quad
            gl.glVertex3f( (rectangle2D.x - rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y + rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f - (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Lower Top Left Of The Quad
            gl.glVertex3f( (rectangle2D.x - rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y - rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f - (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Lower Bottom Left Of The Quad

            // right surfarce quad
            gl.glVertex3f( (rectangle2D.x + rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y - rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f + (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Upper Bottom Right Of The Quad
            gl.glVertex3f( (rectangle2D.x + rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y + rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f + (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Upper Top Right Of The Quad
            gl.glVertex3f( (rectangle2D.x + rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y + rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f - (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Lower Top Right Of The Quad
            gl.glVertex3f( (rectangle2D.x + rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y - rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f - (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Lower Bottom Right Of The Quad

            // top surfarce quad
            gl.glVertex3f( (rectangle2D.x + rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y - rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f + (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Upper Bottom Right Of The Quad
            gl.glVertex3f( (rectangle2D.x - rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y - rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f + (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Upper Bottom Left Of The Quad
            gl.glVertex3f( (rectangle2D.x - rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y - rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f - (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Lower Bottom Left Of The Quad
            gl.glVertex3f( (rectangle2D.x + rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y - rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f - (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Lower Bottom Right Of The Quad

            // bottom surfarce quad
            gl.glVertex3f( (rectangle2D.x + rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y + rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f + (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Upper Top Right Of The Quad
            gl.glVertex3f( (rectangle2D.x - rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y + rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f + (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Upper Top Left Of The Quad
            gl.glVertex3f( (rectangle2D.x - rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y + rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f - (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Lower Top Left Of The Quad
            gl.glVertex3f( (rectangle2D.x + rectangle2D.width / 2.0f) / 100.0f - 5.0f, (rectangle2D.y + rectangle2D.height / 2.0f) / 100.0f - 5.0f, ( CANVAS_Z_SIZE / 2.0f - (pathwayComponentContainer.depth / 2.0f) ) / 100.0f - 5.0f); // Lower Top Right Of The Quad
        }
        gl.glEnd();

        gl.glDisable(GL_ALPHA_TEST);
    }

    /**
    *  Draws the frustum.
    */
    private void drawFrustum(GL2 gl)
    {
        FRUSTUM_COLOR.getRGBColorComponents(CURRENT_COLOR);
        if ( ANAGLYPH_STEREOSCOPIC_3D_VIEW.get() ) graph.createGrayScaleColor(CURRENT_COLOR);
        gl.glColor3fv(CURRENT_COLOR, 0);
        gl.glLineWidth(FRUSTUM_LINE_WIDTH);

        if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
        {
            // gl.glVertexPointer(3, GL_FLOAT, 0, ALL_VERTEX_3D_COORDS_LAYOUT_CUBE_BUFFER);
            gl.glInterleavedArrays(GL_V3F, 0, ALL_VERTEX_3D_COORDS_LAYOUT_CUBE_BUFFER);
            gl.glDrawArrays(GL_LINES, 0, 24);
            // gl.glDrawElements(GL_LINES, 24, GL_UNSIGNED_BYTE, indices);
        }
        else
        {
            gl.glBegin(GL_LINES);

            gl.glVertex3f(-5.0f, -5.0f,  -5.0f);
            gl.glVertex3f(-5.0f,  5.0f,  -5.0f);
            gl.glVertex3f(-5.0f,  5.0f,  -5.0f);
            gl.glVertex3f( 5.0f,  5.0f,  -5.0f);
            gl.glVertex3f( 5.0f,  5.0f,  -5.0f);
            gl.glVertex3f( 5.0f, -5.0f,  -5.0f);
            gl.glVertex3f( 5.0f, -5.0f,  -5.0f);
            gl.glVertex3f(-5.0f, -5.0f, -5.0f);

            gl.glVertex3f(-5.0f, -5.0f,  5.0f);
            gl.glVertex3f(-5.0f,  5.0f,  5.0f);
            gl.glVertex3f(-5.0f,  5.0f,  5.0f);
            gl.glVertex3f( 5.0f,  5.0f,  5.0f);
            gl.glVertex3f( 5.0f,  5.0f,  5.0f);
            gl.glVertex3f( 5.0f, -5.0f,  5.0f);
            gl.glVertex3f( 5.0f, -5.0f,  5.0f);
            gl.glVertex3f(-5.0f, -5.0f,  5.0f);

            gl.glVertex3f(-5.0f, -5.0f, -5.0f);
            gl.glVertex3f(-5.0f, -5.0f,  5.0f);
            gl.glVertex3f(-5.0f,  5.0f, -5.0f);
            gl.glVertex3f(-5.0f,  5.0f,  5.0f);
            gl.glVertex3f( 5.0f,  5.0f, -5.0f);
            gl.glVertex3f( 5.0f,  5.0f,  5.0f);
            gl.glVertex3f( 5.0f, -5.0f, -5.0f);
            gl.glVertex3f( 5.0f, -5.0f,  5.0f);

            gl.glEnd();
        }
    }

    /**
    *  Rotates the current view in the conventional x,y,z manner.
    */
    private void zRotate(int startX, int startY, int x, int y)
    {
	System.out.println("zRotate function called.");

        xzRotate += (startX - x);
        yzRotate += (startY - y);

	System.out.println("xzRotate is " + xzRotate);
	System.out.println("yzRotate is " + yzRotate);

        isInMotion = true;
    }

    /**
     * Rotates the current view with x and y moving about w axis.
     */
    private void wRotate(int startX, int startY, int x, int y)
    {
	System.out.println("wRotate function called.");

        xwRotate += (startX - x);
        ywRotate += (startY - y);

	System.out.println("xwRotate is " + xwRotate);
	System.out.println("ywRotate is " + ywRotate);

        isInMotion = true;
    }

    /**
    *  Scales the current view.
    */
    private void scale(int startX, int startY, int x, int y)
    {
        scaleValue += ( (scaleValue > 5.0f) ? ( ( (startX - x) - (startY - y) ) / 400.0f ) * (1.0f + scaleValue)
                                            : ( ( (startX - x) - (startY - y) ) / 40.0f ) );

        isInMotion = true;
    }

    /**
    *  Translates the current view.
    */
    private void translate(int startX, int startY, int x, int y)
    {
        translateDX += ( (startX - x) / FAR_DISTANCE ) * (1.0f + scaleValue);
        translateDY += ( (startY - y) / FAR_DISTANCE ) * (1.0f + scaleValue);

        isInMotion = true;
    }

    /**
    *  Picks a node.
    */
    private void pick(double startX, double startY, double width, double height, boolean multiple)
    {
        if ( (width > 0) && (height > 0) && !(autoRotate || autoPulsate) )
        {
            pickOriginX = startX;
            pickOriginY = startY;
            pickWidth = width;
            pickHeight = height;
            pickAdd = multiple;
            pickOneNode = false;
            pickFind = false;
            selectMode = true;
        }
    }

    /**
    *  Picks a node.
    *  Overloaded version of the method above.
    */
    private void pick(double startX, double startY, boolean multiple)
    {
        if ( !(autoRotate || autoPulsate) )
        {
            pickOriginX = startX;
            pickOriginY = startY;
            pickWidth = 1.0;
            pickHeight = 1.0;
            pickAdd = multiple;
            pickOneNode = true;
            pickFind = false;
            selectMode = true;
        }
    }

    /**
    *  Finds a node.
    */
    private void findNode(int startX, int startY, boolean hasMouseClicked, boolean multiple)
    {
        mouseHasClicked = hasMouseClicked;

        pickOriginX = startX;
        pickOriginY = startY;
        pickWidth = 1.0;
        pickHeight = 1.0;
        pickAdd = multiple;
        pickOneNode = true;
        pickFind = true;
        lastNodeNamePicked = "";
        lastNodeURLStringPicked = "";
        selectMode = true;
    }

    /**
    *  Processes the hits from selectScene().
    */
    private void processHits(int hits)
    {
        int offset = 0;
        int numberNames = 0;
        int nameID = 0;
        float minZ = 0.0f;
        float tempMinZ = 0.0f;
        closestNode = null;

        HashSet<GraphNode> nodesToAdd = new HashSet<GraphNode>();
        for (int i = 0; i < hits; i++)
        {
            numberNames = SELECTION_BUFFER.get(offset++);
            minZ = (float)SELECTION_BUFFER.get(offset++) / 0x7fffffff; // divided by 2^32 - 1 to convert to from C OpenGL unsigned to signed type
            // maxZ = (float)buffer.get(offset++) / 0x7fffffff;
            offset++; // skip maxZ offset, we need just the closest node to the mouse pointer

            for (int j = 0; j < numberNames; j++)
            {
                nameID = SELECTION_BUFFER.get(offset++);
                // for group nodes appearing on the graph, having a nodeID < 0
                GraphNode node = (nameID < 0) ? selectionManager.getGroupManager().getGroupNodebyID(nameID) : graphNodes.get(nameID); // Integer autoboxing

                if (!pickOneNode && !pickFind)
                {
                    if ( selectionManager.getSelectedNodes().contains(node) )
                    {
                        selectionManager.removeNodeFromSelected(node, false, false, false); // do not need to do any viewer updates here
                    }
                    else
                    {
                        nodesToAdd.add(node);
                    }
                }
                else
                {
                    if (minZ < tempMinZ)
                    {
                        closestNode = node;
                        tempMinZ = minZ;
                    }
                }
            }
        }

        if ( pickOneNode && (closestNode != null) )
        {
            if (pickFind)
            {
                if (mouseHasClicked)
                {
                    nodesToAdd.add(closestNode);
                    lastNodeURLStringPicked = closestNode.getURLString();
                }

                lastNodeNamePicked = nc.getNodeName( closestNode.getNodeName() );
            }
            else
            {
                if ( selectionManager.getSelectedNodes().contains(closestNode) )
                {
                    selectionManager.removeNodeFromSelected(closestNode, false, false, false); // do not need to do any viewer updates here
                }
                else
                {
                    nodesToAdd.add(closestNode);

                    Point3D point = null;
                    if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                    {
                        GraphmlNetworkContainer gnc = nc.getGraphmlNetworkContainer();
                        float[] currentNodeGraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( closestNode.getNodeName() ).first;
                        point = new Point3D(currentNodeGraphmlMapCoord[2], currentNodeGraphmlMapCoord[3], currentNodeGraphmlMapCoord[4] + CANVAS_Z_SIZE / 2.0f);
                    }
                    else
                        point = closestNode.getPoint();

                    FOCUS_POSITION_3D.setLocation(point.getX(currentViewMatrix) / 100.0f - 5.0f, point.getY(currentViewMatrix) / 100.0f - 5.0f, point.getZ(currentViewMatrix) / 100.0f - 5.0f);

                    translateDX = 0.0f;
                    translateDY = 0.0f;
                }
            }
        }

        if (!pickFind || mouseHasClicked)
        {
            // check to deselect the same clicked node
            if (mouseHasClicked)
            {
                if ( selectionManager.getSelectedNodes().contains(closestNode) )
                    selectionManager.removeNodeFromSelected(closestNode);
                else
                    selectionManager.addNodesToSelected(nodesToAdd);
            }
            else
                selectionManager.addNodesToSelected(nodesToAdd);

            mouseHasClicked = false;
            updateSelectedNodesDisplayList = true;
        }

        if ( !nodesToAdd.isEmpty() )
            selectionManager.checkSetEnabledNodeNameTextFieldAndSelectNodesTab();
    }

    /**
    *  Initial starting point of the selection box.
    */
    private void selectBox(float startX, float startY, float x, float y)
    {
        if ( !(autoRotate || autoPulsate) )
        {
            selectBox = true;
            selectBoxStartX = startX;
            selectBoxStartY = startY;
            selectBoxEndX = x;
            selectBoxEndY = y;
        }
    }

    /**
    *  Draws the selection box.
    */
    private void drawSelectBox(GL2 gl, int width, int height)
    {
        viewOrtho(gl, width, height);

        gl.glEnable(GL_COLOR_LOGIC_OP);
        gl.glLogicOp(GL_XOR);
        Color.WHITE.getRGBColorComponents(CURRENT_COLOR);
        if ( ANAGLYPH_STEREOSCOPIC_3D_VIEW.get() ) graph.createGrayScaleColor(CURRENT_COLOR);
        gl.glColor3fv(CURRENT_COLOR, 0);
        gl.glLineWidth(SELECTED_BOX_LINE_WIDTH);

        if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
        {
            ALL_VERTEX_3D_COORDS_SELECT_BOX_BUFFER.put( new float[] {
                                                                      selectBoxStartX, selectBoxStartY,
                                                                      selectBoxStartX, selectBoxEndY,
                                                                      selectBoxStartX, selectBoxEndY,
                                                                      selectBoxEndX,   selectBoxEndY,
                                                                      selectBoxEndX,   selectBoxEndY,
                                                                      selectBoxEndX,   selectBoxStartY,
                                                                      selectBoxEndX,   selectBoxStartY,
                                                                      selectBoxStartX, selectBoxStartY
                                                              } ).rewind();

            // gl.glVertexPointer(2, GL_FLOAT, 0, ALL_VERTEX_3D_COORDS_SELECT_BOX_BUFFER);
            gl.glInterleavedArrays(GL_V2F, 0, ALL_VERTEX_3D_COORDS_SELECT_BOX_BUFFER);
            gl.glDrawArrays(GL_LINES, 0, 8);
            // gl.glDrawElements(GL_LINES, 8, GL_UNSIGNED_BYTE, indices);

        }
        else
        {
            gl.glBegin(GL_LINES);

            gl.glVertex2f(selectBoxStartX, selectBoxStartY);
            gl.glVertex2f(selectBoxStartX, selectBoxEndY);
            gl.glVertex2f(selectBoxStartX, selectBoxEndY);
            gl.glVertex2f(selectBoxEndX,   selectBoxEndY);
            gl.glVertex2f(selectBoxEndX,   selectBoxEndY);
            gl.glVertex2f(selectBoxEndX,   selectBoxStartY);
            gl.glVertex2f(selectBoxEndX,   selectBoxStartY);
            gl.glVertex2f(selectBoxStartX, selectBoxStartY);

            gl.glEnd();
        }

        gl.glDisable(GL_COLOR_LOGIC_OP);

        viewPerspective(gl);
    }

    /**
    *  Orthogonal view.
    */
    private void viewOrtho(GL2 gl, int width, int height)    // Set Up An Ortho View
    {
        gl.glMatrixMode(GL_PROJECTION);                     // Select Projection
        gl.glPushMatrix();                                  // Push The Matrix
        gl.glLoadIdentity();                                // Reset The Matrix
        gl.glOrtho(0, width, height, 0, -1.0f, 1.0f);       // Select Ortho Mode
        // same with:
        // GLU.gluOrtho2D(0, width, height, 0);
        gl.glMatrixMode(GL_MODELVIEW);                      // Select Modelview Matrix
        gl.glPushMatrix();                                  // Push The Matrix
        gl.glLoadIdentity();                                // Reset The Matrix
    }

    /**
    *  Perspective view.
    */
    private void viewPerspective(GL2 gl)                     // Set Up A Perspective View
    {
        gl.glMatrixMode(GL_PROJECTION);                     // Select Projection
        gl.glPopMatrix();                                   // Pop The Matrix
        gl.glMatrixMode(GL_MODELVIEW);                      // Select Modelview
        gl.glPopMatrix();                                   // Pop The Matrix
    }

    /**
    *  Deletes all display lists.
    */
    private void deleteAllDisplayLists(GL2 gl)
    {
        for (int i = 0; i < ALL_SHAPES_3D_DISPLAY_LISTS.length; i++)
            gl.glDeleteLists(ALL_SHAPES_3D_DISPLAY_LISTS[i], 1);

        for (int i = 0; i < ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS.length; i++)
            gl.glDeleteLists(ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[i], 1);

        if (allEdgesDisplayLists != null) // if allEdgesDisplayLists not empty, delete all its display lists
        {
            for (int i = 0; i < allEdgesDisplayLists.capacity(); i++)
                gl.glDeleteLists(allEdgesDisplayLists.get(i), 1);

            allEdgesDisplayLists.clear();
            allEdgesDisplayLists = null;
        }

        // if ( gl.glIsList(nodesDisplayList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
        gl.glDeleteLists(nodesDisplayList, 1);

        // if ( gl.glIsList(fastSelectionNodesDisplayList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
        gl.glDeleteLists(fastSelectionNodesDisplayList, 1);

        // if ( gl.glIsList(selectedNodesDisplayList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
        gl.glDeleteLists(selectedNodesDisplayList, 1);

        // if ( gl.glIsList(pathwayComponentContainersDisplayList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
        gl.glDeleteLists(pathwayComponentContainersDisplayList, 1);

        prevHowManyDisplayListsToCreate = 0;
    }

    /**
    *  Increases the node depth (graphml mode only).
    */
    private void increaseNodeDepth(boolean increase)
    {
        HashSet<GraphNode> nodes = selectionManager.getSelectedNodes();
        if ( nodes.isEmpty() )
            nodes = visibleNodes;

        GraphmlNetworkContainer gnc = nc.getGraphmlNetworkContainer();
        for (GraphNode node : nodes)
            // inverse value so as to give the user the impression that a positive depth (Z) value is nearer to the viewer
            gnc.getAllGraphmlNodesMap().get( node.getNodeName() ).first[4] += ( (increase) ? -AMOUNT_OF_DEPTH : AMOUNT_OF_DEPTH);
    }

    /**
    *  Resets the pulsate values.
    */
    private void resetPulsateValues()
    {
        pulseSteps = 0;
        pulsateValue = false;
        morphingValue = 0.0f;
    }

    /**
    *  Increases the node tesselation.
    */
    private void increaseNodeTesselation(boolean increase)
    {
        if ( ( (NODE_TESSELATION.get() > NODE_TESSELATION_MIN_VALUE) || increase ) && ( (NODE_TESSELATION.get() < NODE_TESSELATION_MAX_VALUE + NODE_TESSELATION_EXTRA_KEYS_VALUE_THRESHOLD) || !increase ) )
            NODE_TESSELATION.set( ( (increase) ? NODE_TESSELATION.get() + 1 : NODE_TESSELATION.get() - 1 ) );
    }

    /**
    *  Cycles to the next shape.
    */
    private void nextShape()
    {
        current3DShape = (current3DShape.ordinal() < NUMBER_OF_3D_SHAPES - 1) ? Shapes3D.values()[current3DShape.ordinal() + 1] : SPHERE;
    }

    /**
    *  Chooses a 3D shape.
    */
    private Shapes3D choose3DShape(Shapes3D originalShape)
    {
        return (MANUAL_SHAPE_3D) ? current3DShape : originalShape;
    }

    /**
    *  Checks if the given shape has texture coordinates.
    */
    private boolean hasShapeTextureCoords(Shapes3D shape)
    {
        shape = choose3DShape(shape);
        for (int i = 0; i < SHAPES_WITH_TEXTURE_COORDS_ONLY.length; i++)
            if ( shape.equals(SHAPES_WITH_TEXTURE_COORDS_ONLY[i]) && !objModelLoaderShape.getHasTexture() )
                return true;
        for (int i = 0; i < SHAPES_WITH_TESSELATION_AND_TEXTURE_COORDS.length; i++)
            if ( shape.equals(SHAPES_WITH_TESSELATION_AND_TEXTURE_COORDS[i]) )
                return true;

        return false;
    }

    /**
    *  Checks if node texturing is enabled.
    */
    private boolean checkForNodeTexturing()
    {
        return TEXTURE_ENABLED.get() || ( USE_GL_EXT_FRAMEBUFFER_OBJECT && SHOW_3D_ENVIRONMENT_MAPPING.get() );
    }

    /**
    *  Deletes an OpenGL texture.
    */
    private void disposeAllTextures(GL2 gl)
    {
        if (nodeTexture != null)
        {
            nodeTexture = null;
        }

        if ( USE_GL_EXT_FRAMEBUFFER_OBJECT && (renderToTexture != null) )
        {
            renderToTexture.disposeAllRenderToTextureResources(gl);
            renderToTexture = null;
        }

        if (objModelLoaderShape != null)
        {
            objModelLoaderShape.disposeAllModelShapeResources(gl);
            objModelLoaderShape = null;
        }

        if (screenshot != null)
        {
            screenshot.flush();
            screenshot = null;
        }
    }

    /**
    *  Takes a screenshot.
    */
    private void takeScreenshot(GL2 gl, boolean renderToFile)
    {
        try
        {
            AWTGLReadBufferUtil agrbu = new AWTGLReadBufferUtil(GLProfile.getDefault(), false);
            screenshot = agrbu.readPixelsToBufferedImage(gl, true);

            if (renderToFile)
            {
                if (DEBUG_BUILD) println("Screenshot " + saveScreenshotFile.getAbsolutePath() + " taken");
                ImageIO.write(screenshot, "png", saveScreenshotFile);
                screenshot = null;
                InitDesktop.open(saveScreenshotFile);
            }
        }
        catch (GLException glExc)
        {
            if (DEBUG_BUILD) println("GLException in GraphRenderer3D.takeScreenshot():\n" + glExc.getMessage());

            // do it here before the showMessageDialog() so as to avoid refreshes that will fire up the screenshot rendering!
            takeScreenshot = false;

            JOptionPane.showMessageDialog(graph, "Something went wrong while saving the file:\n" + glExc.getMessage(), "Error with saving the file!", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception exc)
        {
            if (DEBUG_BUILD) println("Exception in GraphRenderer3D.takeScreenshot():\n" + exc.getMessage());

            // do it here before the showMessageDialog() so as to avoid refreshes that will fire up the screenshot rendering!
            takeScreenshot = false;

            JOptionPane.showMessageDialog(graph, "Something went wrong while saving the image to file:\n" + exc.getMessage() + "\nPlease try again with a different file name/path/drive.", "Error with saving the image to file!", JOptionPane.ERROR_MESSAGE);

            graph.initiateTakeScreenShotProcess(false);
        }
        finally
        {
            takeScreenshot = false;
        }
    }

    /**
    *  Takes a high resolution screenshot.
    */
    private void takeHighResScreenshot(GL2 gl)
    {
        float originalEdgeSize = DEFAULT_EDGE_SIZE.get();
        DEFAULT_EDGE_SIZE.set(originalEdgeSize * TILE_SCREEN_FACTOR.get());
        updateEdgesDisplayList = true;
        buildAllDisplayLists(gl);

        try
        {
            // do the trick below so as to avoid tile rendering artifacts for every tile being rendered and then changed
            boolean tempShow3DEnvironmentMapping = SHOW_3D_ENVIRONMENT_MAPPING.get();
            if (tempShow3DEnvironmentMapping)
                SHOW_3D_ENVIRONMENT_MAPPING.set(false);

            // do the trick below so as to avoid tile rendering artifacts for every tile being rendered and then changed
            boolean tempMaterialAntiAliasShading = false;
            if (USE_SHADERS_PROCESS)
            {
                tempMaterialAntiAliasShading = MATERIAL_ANIMATED_SHADING.get();
                if (tempMaterialAntiAliasShading)
                    MATERIAL_ANIMATED_SHADING.set(false);
            }

            int tileWidth = width * TILE_SCREEN_FACTOR.get();
            int tileHeight = height * TILE_SCREEN_FACTOR.get();

            TileRenderer tr = new TileRenderer();

            tr.setTileSize(256, 256, 0);
            tr.setImageSize(tileWidth, tileHeight);

            final GLPixelBuffer.GLPixelBufferProvider pixelBufferProvider = GLPixelBuffer.defaultProviderWithRowStride;
            final boolean[] flipVertically =
            {
                false
            };

            GLPixelBuffer.GLPixelAttributes pixelAttribs = pixelBufferProvider.getAttributes(gl, 3);
            GLPixelBuffer pixelBuffer = pixelBufferProvider.allocate(gl, pixelAttribs, tileWidth, tileHeight, 1, true, 0);

            tr.setImageBuffer(pixelBuffer);

            int tileCount = 0;
            this.addTileRendererNotify(tr);
            while(!tr.eot())
            {
                tr.beginTile(gl);
                this.reshape(gl,
                    tr.getParam(TileRendererBase.TR_CURRENT_TILE_X_POS),
                    tr.getParam(TileRendererBase.TR_CURRENT_TILE_Y_POS),
                    tr.getParam(TileRendererBase.TR_CURRENT_TILE_WIDTH),
                    tr.getParam(TileRendererBase.TR_CURRENT_TILE_HEIGHT),
                    tr.getParam(TileRendererBase.TR_IMAGE_WIDTH),
                    tr.getParam(TileRendererBase.TR_IMAGE_HEIGHT));

                clearScreen3D(gl);
                renderScene3D(gl, true);

                layoutProgressBarDialog.incrementProgress(++tileCount);
                tr.endTile(gl);
            }
            this.removeTileRendererNotify(tr);

            if (DEBUG_BUILD) println(tileCount + " tiles drawn\n");

            layoutProgressBarDialog.incrementProgress(100);
            layoutProgressBarDialog.setText( "Writing image to: " + saveScreenshotFile.getAbsolutePath() );

            if (DEBUG_BUILD) println( "Now Writing High Res BufferedImage to File: " + saveScreenshotFile.getAbsolutePath() );

            final GLPixelBuffer imageBuffer = tr.getImageBuffer();
            final TextureData textureData = new TextureData(
                    GLProfile.getDefault(),
                    0 /* internalFormat */,
                    tileWidth, tileHeight,
                    0,
                    imageBuffer.pixelAttributes,
                    false, false,
                    flipVertically[0],
                    imageBuffer.buffer,
                    null /* Flusher */);

            TextureIO.write(textureData, saveScreenshotFile);

            // do the trick below so as to avoid tile rendering artifacts for every tile being rendered and then changed
            if (tempShow3DEnvironmentMapping)
                SHOW_3D_ENVIRONMENT_MAPPING.set(true);

            // do the trick below so as to avoid tile rendering artifacts for every tile being rendered and then changed
            if (USE_SHADERS_PROCESS)
                if (tempMaterialAntiAliasShading)
                    MATERIAL_ANIMATED_SHADING.set(true);

            System.gc();

            if (DEBUG_BUILD) println("Done Writing High Res BufferedImage to File");

            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
            layoutProgressBarDialog.setIndeterminate(false);

            InitDesktop.open(saveScreenshotFile);
        }
        catch (OutOfMemoryError memErr)
        {
            if (DEBUG_BUILD) println("Out of Memory Error with creating the High Res Image to File in GraphRenderer3D.takeHighResScreenshot():\n" + memErr.getMessage());

            // do it here before the showMessageDialog() so as to avoid refreshes that will fire up the high res screenshot rendering!
            takeHighResScreenshot = false;

            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
            layoutProgressBarDialog.setIndeterminate(false);

            JOptionPane.showMessageDialog(graph, "Out of memory while creating the high resolution image to file:\n" + memErr.getMessage() + "\nPlease try again with a smaller scale value.", "Error with creating the high resolution image to file!", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception exc)
        {
            if (DEBUG_BUILD) println("Exception with writing the Image to File in GraphRenderer3D.takeHighResScreenshot():\n" + exc.getMessage());

            // do it here before the showMessageDialog() so as to avoid refreshes that will fire up the high res screenshot rendering!
            takeHighResScreenshot = false;

            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
            layoutProgressBarDialog.setIndeterminate(false);

            JOptionPane.showMessageDialog(graph, "Something went wrong while saving the high resolution image to file:\n" + exc.getMessage() + "\nPlease try again with a different file name/path/drive.", "Error with saving the high resolution image to file!", JOptionPane.ERROR_MESSAGE);

            graph.initiateTakeScreenShotProcess(true);
        }
        finally
        {
            takeHighResScreenshot = false;

            // Reset canvas size
            this.reshape(graph, 0, 0, width, height);

            CENTER_VIEW_CAMERA.setProjection(gl);

            DEFAULT_EDGE_SIZE.set(originalEdgeSize);
            updateEdgesDisplayList = true;
            buildAllDisplayLists(gl);
        }
    }

    @Override
    public void addTileRendererNotify(TileRendererBase tr)
    {
    }

    @Override
    public void removeTileRendererNotify(TileRendererBase tr)
    {
    }

    @Override
    public void startTileRendering(TileRendererBase tr)
    {
    }

    @Override
    public void endTileRendering(TileRendererBase tr)
    {
    }

    @Override
    public void reshapeTile(TileRendererBase tr,
            int tileX, int tileY, int tileWidth, int tileHeight,
            int imageWidth, int imageHeight)
    {
        final GL2 gl = tr.getAttachedDrawable().getGL().getGL2();
        gl.setSwapInterval(0);
        reshape(gl, tileX, tileY, tileWidth, tileHeight, imageWidth, imageHeight);
    }

    public void reshape(GL2 gl, int tileX, int tileY, int tileWidth, int tileHeight, int imageWidth, int imageHeight)
    {
        // Scene dimensions
        float originalLeft = (float) CENTER_VIEW_CAMERA.getLeft();
        float originalRight = (float) CENTER_VIEW_CAMERA.getRight();
        float originalTop = (float) CENTER_VIEW_CAMERA.getTop();
        float originalBottom = (float) CENTER_VIEW_CAMERA.getBottom();

        final float w = originalRight - originalLeft;
        final float h = originalTop - originalBottom;

        // Tile dimensions
        final float tileLeft = originalLeft + tileX * w / imageWidth;
        final float tileRight = tileLeft + tileWidth * w / imageWidth;
        final float tileBottom = originalBottom + tileY * h / imageHeight;
        final float tileTop = tileBottom + tileHeight * h / imageHeight;

        CENTER_VIEW_CAMERA.setProjection(gl, tileLeft, tileRight, tileBottom, tileTop);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
	
	
	// Replaced xRotate and yRotate with 0.0f
        CENTER_VIEW_CAMERA.setCamera(gl, translateDX, translateDY, scaleValue,
                0.0f, 0.0f, 0.0f, FOCUS_POSITION_3D, true);
	
    }

    /**
    *  Starts the updating/rendering thread(s).
    *  Overrides the parent's abstract method.
    */
    private void startRender()
    {
        isAutoRendering = true;

        if (graphRendererThreadUpdater == null)
        {
            graphRendererThreadUpdater = new GraphRendererThreadUpdater(graph, this, frameskip, fullscreen, targetFPS);
            graphRendererThreadUpdater.startWithPriority(Thread.NORM_PRIORITY);
        }
    }

    /**
    *  Stops updating/rendering thread(s).
    *  Overrides the parent's abstract method.
    */
    private void stopRender()
    {
        if (graphRendererThreadUpdater != null)
        {
            graphRendererThreadUpdater.setRendering(false);
            graphRendererThreadUpdater = null;
        }

        isAutoRendering = false;
    }

    /**
    *  Toggles rotation.
    */
    private void toggleRotation(ActionEvent e)
    {
        // when a non-null object is sent, skip toggle (used from the GUI animation control)
        if ( e.getActionCommand().equals(START_ANIMATION_EVENT_STRING) )
        {
            startRotation();
        }
        else if ( e.getActionCommand().equals(STOP_ANIMATION_EVENT_STRING) )
        {
            stopRotationAndPulsation();
        }
        else
        {
            if (!rotating)
                startRotation();
            else
                stopRotationAndPulsation();
        }
    }

    /**
    *  Toggles pulsation.
    */
    private void togglePulsation()
    {
        if (!rotating)
            startRotationAndPulsation();
        else
            stopRotationAndPulsation();
    }

    /**
    *  Starts rotation.
    */
    private void startRotation()
    {
        if (!rotating)
        {
            rotating = true;
            autoRotate = true;
            startRender();
        }
    }

    /**
    *  Stops rotation.
    */
    private void stopRotation()
    {
        if (rotating)
        {
            rotating = false;
            autoRotate = false;
            stopRender();
        }
        refreshDisplay();
    }

    /**
    *  Starts rotation and pulsation.
    */
    private void startRotationAndPulsation()
    {
        startRotation(); // make sure rotation process is stopped
        autoPulsate = true;
    }

    /**
    *  Stops rotation and pulsation.
    */
    private void stopRotationAndPulsation()
    {
        if (USE_SHADERS_PROCESS) resetPulsateValues();
        autoPulsate = false;
        stopRotation();
    }

    /**
    *  Renders a high resolution image to file.
    */
    private void highResRenderToFile()
    {
        stopRotationAndPulsation();

        layoutProgressBarDialog.setIndeterminate(true);
        layoutProgressBarDialog.prepareProgressBar(100, "Now Performing High Resolution Render Image to file (may take some time)...");
        layoutProgressBarDialog.startProgressBar();

        // so as to show some progress bar animation, as the render thread will not manage update the progress bar for some reason
        for (int i = 0; i < 100; i++)
        {
            LayoutFrame.sleep(10);
            layoutProgressBarDialog.incrementProgress();
        }

        takeHighResScreenshot = true;
    }

    /**
    *  Resets the animation values.
    */
    private void resetAnimationValues()
    {
        autoRotate = false;
        autoPulsate = false;

        stopRotationAndPulsation();
    }

    /**
    *  Enables/disables the given shader SFX lighting program.
    */
    private void enableDisableShaderLightingSFX(ShaderLightingSFXs.ShaderTypes shaderType)
    {
        if (USE_SHADERS_PROCESS)
        {
            currentShaderType = shaderType;
            layoutFrame.setShaderLightingSFXValue(shaderType);
            refreshDisplay();
        }
    }

    /**
    *  Called by the JOGL2 glDrawable immediately after the OpenGL context is initialized.
    */
    @Override
    public void init(GLAutoDrawable glDrawable)
    {
        if (DEBUG_BUILD) println("GraphRenderer3D init()");
	
	System.out.println("GraphRender3D init()");
	System.out.println("Current view matrix:");
	System.out.println(currentViewMatrix.toString());

        GL2 gl = glDrawable.getGL().getGL2();
        clearScreen3D(gl);

        nodesDisplayList = gl.glGenLists(1);
        fastSelectionNodesDisplayList = gl.glGenLists(1);
        selectedNodesDisplayList = gl.glGenLists(1);
        pathwayComponentContainersDisplayList = gl.glGenLists(1);
        for (int i = 0; i < ALL_SHAPES_3D_DISPLAY_LISTS.length; i++)
            ALL_SHAPES_3D_DISPLAY_LISTS[i] = gl.glGenLists(1);
        for (int i = 0; i < ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS.length; i++)
            ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS[i] = gl.glGenLists(1);

        buildAllShapes3DDisplayLists(gl);
        buildAllShapes3DFastSelectionDisplayLists(gl);

        // enable the z-buffer algorithm
        gl.glClearDepth(1.0);                                  // Specifies the clear value for the depth buffer (1.0 is the initial value)
        gl.glEnable(GL_DEPTH_TEST);                            // Enables Depth Testing
        gl.glDepthFunc(GL_LEQUAL);                             // The Type Of Depth Test To Do
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);  // Really Nice Perspective Calculations

        // Disable back-face removal for convex objects, as we may need to render the inside of a shape
        gl.glDisable(GL_CULL_FACE);
        gl.glCullFace(GL_BACK);

        // for line antialiasing and blending options usage only
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // fog values
        gl.glFogi(GL_FOG_MODE, GL_EXP2);                       // Exponential Fog Mode
        gl.glFogfv(GL_FOG_COLOR, FOG_COLOR);                   // Sets Fog Color
        gl.glFogf(GL_FOG_DENSITY, 0.01f);                      // How Dense Will The Fog Be

        ANIMATION_CHANGE_SPECTRUM_TEXTURE_ENABLED = true;

        prepareLighting(gl);
        if (USE_SHADERS_PROCESS)
        {
            preparePointSprites(gl);
            prepareShaderLighting(gl);
        }
        if (USE_GL_EXT_FRAMEBUFFER_OBJECT) prepareEnvironmentMapping(gl);

        nodeTexture = graph.prepareNodeTexture(gl, nodeTexture);
    }

    /**
    *  Called by the JOGL2 glDrawable to initiate OpenGL rendering by the client.
    */
    @Override
    public void display(GLAutoDrawable glDrawable)
    {
        GL2 gl = glDrawable.getGL().getGL2();
        if (deAllocOpenGLMemory)
        {
            if (DEBUG_BUILD) println("GraphRenderer3D draw: delete all display lists & destroy all textures");

            /*
            if (USE_SHADERS_PROCESS)
            {
                // don't destroy them, keep it for faster renderer mode switching
                shaderLightingSFXsNodes.destructor(gl);
                if (USE_GL_ARB_GEOMETRY_SHADER4)
                {
                    shaderLightingSFXsSelectedNodes.destructor(gl);
                    shaderLightingSFXsSelectedNodesNormalsGeometry.destructor(gl);
                }
                if (USE_400_SHADERS_PROCESS)
                {
                    shaderLODSFXsNodes.destructor(gl);
                    if (USE_GL_ARB_GEOMETRY_SHADER4)
                    {
                        shaderLODSFXsSelectedNodes.destructor(gl);
                        shaderLODSFXsSelectedNodesNormalsGeometry.destructor(gl);
                    }
                }
                if (USE_400_SHADERS_PROCESS)
                   shaderLinesSFXs.destructor(gl);
            }
            */
            deleteAllDisplayLists(gl);
            disposeAllTextures(gl);

            /*
            if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
            {
                // de-initialize OpenGL Vertex Arrays support
                gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
                gl.glDisableClientState(GL_NORMAL_ARRAY);
                gl.glDisableClientState(GL_VERTEX_ARRAY);
            }
            */

            pickOneNode = false;
            animationFrameCount = 0;
            stepAnimation = false;
        }
        else
        {
            gl.setSwapInterval(USE_VSYNCH.get() ? 1 : 0); // 0 for no VSynch, 1 for VSynch

            clearScreen3D(gl);

            // switch to model view state
            gl.glMatrixMode(GL_MODELVIEW);
            gl.glLoadIdentity();
            // position the light, just after the glLoadIdentity() call
            gl.glLightfv(GL_LIGHT0, GL_POSITION, new float[] { LIGHT_POSITION[0].get(), LIGHT_POSITION[1].get(), LIGHT_POSITION[2].get(), 0.0f }, 0);

            buildAllDisplayLists(gl);

            if ( !ANAGLYPH_STEREOSCOPIC_3D_VIEW.get() )
            {
               	
		// Replaced xRotate and yRotate with 0.0f
	        CENTER_VIEW_CAMERA.setCamera(gl, translateDX, translateDY, scaleValue, 0.0f, 0.0f, 0.0f, FOCUS_POSITION_3D, true);
	
                renderScene3D(gl, true);
            }
            else
            {
                graph.chooseAnaglyphGlassesColorMask(gl, true);
                LEFT_EYE_CAMERA.setProjectionAndCamera(gl, translateDX, translateDY, scaleValue, xzRotate, yzRotate, 0.0f, FOCUS_POSITION_3D, true);
                renderScene3D(gl, true);

                // reset the left eye transformations to continue with the right eye
                gl.glLoadIdentity();

                graph.chooseAnaglyphGlassesColorMask(gl, false);
                RIGHT_EYE_CAMERA.setProjectionAndCamera(gl, translateDX, translateDY, scaleValue, xzRotate, yzRotate, 0.0f, FOCUS_POSITION_3D, true);
                renderScene3D(gl, true);

                gl.glColorMask(true, true, true, true); // reset color mask so as to clear screen properly
            }

            if ( selectMode && !(autoRotate || autoPulsate) ) selectScene(gl);

            if (takeScreenshot) takeScreenshot(gl, renderToFile);
            if (takeHighResScreenshot) takeHighResScreenshot(gl);
        }

        if (autoPulsate)
        {
            autoPulsate = false;
            display(glDrawable);
            autoPulsate = true;
        }

        if (ANIMATION_INITIATE_END_OF_ANIMATION)
        {
            ANIMATION_INITIATE_END_OF_ANIMATION = false;
            layoutFrame.getLayoutAnimationControlDialog().stopAnimation(false);
            refreshDisplay();
        }
    }

    /**
    *  Called by the JOGL2 glDrawable during the first repaint after the component has been resized.
    */
    @Override
    public void reshape(GLAutoDrawable glDrawable, int x, int y, int widthCanvas, int heightCanvas)
    {
        if (DEBUG_BUILD) println("GraphRenderer3D reshape()");

        GL2 gl = glDrawable.getGL().getGL2();
        double intraOcularDistance = extractDouble(GRAPH_INTRA_OCULAR_DISTANCE_TYPE);
        LEFT_EYE_CAMERA.setIntraOcularDistanceAndFrustumShift(DEFAULT_INTRA_OCULAR_DISTANCE);
        LEFT_EYE_CAMERA.updateFrustumDimensions(width, height);
        LEFT_EYE_CAMERA.setIntraOcularDistanceAndFrustumShift(intraOcularDistance);
        CENTER_VIEW_CAMERA.updateViewPortAndFrustumDimensions(gl, x, y, width, height);
        CENTER_VIEW_CAMERA.setProjection(gl);
        RIGHT_EYE_CAMERA.setIntraOcularDistanceAndFrustumShift(DEFAULT_INTRA_OCULAR_DISTANCE);
        RIGHT_EYE_CAMERA.updateFrustumDimensions(width, height);
        RIGHT_EYE_CAMERA.setIntraOcularDistanceAndFrustumShift(intraOcularDistance);
        if (USE_GL_EXT_FRAMEBUFFER_OBJECT) init3DEnviromentMapping(gl);
    }

    /**
    *  KeyPressed keyEvent.
    */
    @Override
    public void keyPressed(KeyEvent e)
    {
        if (DEBUG_BUILD) println("GraphRenderer3D keyPressed()");

        if ( e.isShiftDown() )
        {
            isShiftDown = true;
            isShiftAltDown = e.isAltDown();
        }
    }

    /**
    *  KeyReleased keyEvent.
    */
    @Override
    public void keyReleased(KeyEvent e)
    {
        if (DEBUG_BUILD) println("GraphRenderer3D keyReleased()");

        isShiftDown = false;
        isShiftAltDown = false;
        pickingBox = false;
    }

    /**
    *  KeyTyped keyEvent.
    */
    @Override
    public void keyTyped(KeyEvent e)
    {
        if (DEBUG_BUILD) println("GraphRenderer3D keyTyped()");

        boolean isCtrlDown = (!IS_MAC) ? e.isControlDown() : e.isMetaDown();
        if (!e.isAltDown() && !isCtrlDown)
        {
            if (e.getKeyChar() == '<')
            {
                if (!animationRender)
                {
                    graph.increaseNodeSize(false, true);
                    updateNodesAndSelectedNodesDisplayList();
                }
            }
            else if (e.getKeyChar() == '>')
            {
                if (!animationRender)
                {
                    graph.increaseNodeSize(true, true);
                    updateNodesAndSelectedNodesDisplayList();
                }
            }
            else if (e.getKeyChar() == '-')
            {
                if ( !animationRender && nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                {
                    increaseNodeDepth(false);
                    updateNodesAndSelectedNodesDisplayList();
                }
            }
            else if (e.getKeyChar() == '+')
            {
                if ( !animationRender && nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                {
                    increaseNodeDepth(true);
                    updateNodesAndSelectedNodesDisplayList();
                }
            }
            else if ( !e.isShiftDown() )
            {
                if (e.getKeyChar() == 'e' || e.getKeyChar() == 'E')
                {
                    if ( !layoutFrame.getFileNameLoaded().isEmpty() ) // can take normal res shots while animating!
                        graph.initiateTakeScreenShotProcess(false);
                }
                else if (e.getKeyChar() == 'w' || e.getKeyChar() == 'W')
                {
                    if (!animationRender)
                        if ( !layoutFrame.getFileNameLoaded().isEmpty() )
                            graph.initiateTakeScreenShotProcess(true);
                }
                else if (e.getKeyChar() == 'x' || e.getKeyChar() == 'X')
                {
                    if (!animationRender)
                    {
                        CHANGE_NODE_TESSELATION = true;
                        increaseNodeTesselation(true);
                        updateNodesAndSelectedNodesDisplayList();
                    }
                }
                else if (e.getKeyChar() == 'z' || e.getKeyChar() == 'Z')
                {
                    if (!animationRender)
                    {
                        CHANGE_NODE_TESSELATION = true;
                        increaseNodeTesselation(false);
                        updateNodesAndSelectedNodesDisplayList();
                    }
                }
                else if (e.getKeyChar() == 's' || e.getKeyChar() == 'S')
                {
                    if (!animationRender)
                    {
                        MANUAL_SHAPE_3D = true;
                        nextShape();
                        updateNodesAndSelectedNodesDisplayList();
                    }
                }
                else if (e.getKeyChar() == 'o' || e.getKeyChar() == 'O')
                {
                    if (!animationRender)
                        resetAllValues();
                }
                else if (e.getKeyChar() == 'p' || e.getKeyChar() == 'P')
                {
                    if (!animationRender)
                        togglePulsation();
                }
                else if (e.getKeyChar() == 'r' || e.getKeyChar() == 'R')
                {
                    if (!animationRender)
                        toggleRotation( new ActionEvent(this, 0, "ManualEvent") );
                }
                else if (e.getKeyChar() == 'v' || e.getKeyChar() == 'V')
                {
                    USE_VSYNCH.set( !USE_VSYNCH.get() );
                    layoutFrame.setUseVSynch( USE_VSYNCH.get() );
                    graph.setGraphRendererThreadUpdaterTargetFPS();
                    refreshDisplay();
                }
                else if (e.getKeyChar() == 'a' || e.getKeyChar() == 'A')
                {
                    MATERIAL_ANTIALIAS_SHADING.set( !MATERIAL_ANTIALIAS_SHADING.get() );
                    layoutFrame.setMaterialAntiAliasShading( MATERIAL_ANTIALIAS_SHADING.get() );
                    refreshDisplay();
                }
                else if (e.getKeyChar() == 'n' || e.getKeyChar() == 'N')
                {
                    MATERIAL_ANIMATED_SHADING.set( !MATERIAL_ANIMATED_SHADING.get() );
                    layoutFrame.setMaterialAnimatedPerlinShading( MATERIAL_ANIMATED_SHADING.get() );
                    refreshDisplay();
                }
                else if (e.getKeyChar() == 'k' || e.getKeyChar() == 'K')
                {
                    MATERIAL_STATE_SHADING.set( !MATERIAL_STATE_SHADING.get() );
                    layoutFrame.setMaterialStateShading( MATERIAL_STATE_SHADING.get() );
                    refreshDisplay();
                }
                else if (e.getKeyChar() == 'l' || e.getKeyChar() == 'L')
                {
                    MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.set( !MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get() );
                    layoutFrame.setMaterialOldLCDStyleTransparencyShading( MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get() );
                    refreshDisplay();
                }
                else if (e.getKeyChar() == 'c' || e.getKeyChar() == 'C')
                {
                    SHOW_3D_FRUSTUM.set( !SHOW_3D_FRUSTUM.get() );
                    layoutFrame.setShow3DFrustum( SHOW_3D_FRUSTUM.get() );
                    refreshDisplay();
                }
                else if (e.getKeyChar() == 'b' || e.getKeyChar() == 'B')
                {
                    SHOW_3D_SHADOWS.set( !SHOW_3D_SHADOWS.get() );
                    layoutFrame.setShow3DShadows( SHOW_3D_SHADOWS.get() );
                    refreshDisplay();
                }
                else if (e.getKeyChar() == 'm' || e.getKeyChar() == 'M')
                {
                    if (!animationRender)
                    {
                        SHOW_3D_ENVIRONMENT_MAPPING.set( !SHOW_3D_ENVIRONMENT_MAPPING.get() );
                        CHANGE_SPHERICAL_MAPPING_ENABLED = true;
                        CHANGE_TEXTURE_ENABLED = TEXTURE_ENABLED.get();
                        ANIMATION_CHANGE_SPECTRUM_TEXTURE_ENABLED = true;
                        layoutFrame.setShow3DEnvironmentMapping( SHOW_3D_ENVIRONMENT_MAPPING.get() );

                        if ( SHOW_3D_ENVIRONMENT_MAPPING.get() && !MATERIAL_SPHERICAL_MAPPING.get() )
                            MATERIAL_SPHERICAL_MAPPING.set(true);

                        updateNodesAndSelectedNodesDisplayList();
                    }
                }
                else if (e.getKeyChar() == 'f' || e.getKeyChar() == 'F')
                {
                    DEPTH_FOG.set( !DEPTH_FOG.get() );
                    layoutFrame.setDepthFog( DEPTH_FOG.get() );
                    refreshDisplay();
                }
                else if (e.getKeyChar() == 't' || e.getKeyChar() == 'T')
                {
                    if (!animationRender)
                    {
                        TEXTURE_ENABLED.set( !TEXTURE_ENABLED.get() );
                        CHANGE_TEXTURE_ENABLED = TEXTURE_ENABLED.get();
                        ANIMATION_CHANGE_SPECTRUM_TEXTURE_ENABLED = true;
                        layoutFrame.setNodeSurfaceImageTexture( TEXTURE_ENABLED.get() );

                        updateNodesAndSelectedNodesDisplayList();
                    }
                }
                else if (e.getKeyChar() == 'g' || e.getKeyChar() == 'G')
                {
                    if (!animationRender)
                    {
                        WIREFRAME_SELECTION_MODE.set( !WIREFRAME_SELECTION_MODE.get() );
                        layoutFrame.setWireframeSelectionMode( WIREFRAME_SELECTION_MODE.get() );

                        updateNodesAndSelectedNodesDisplayList();
                    }
                }
                else if (e.getKeyChar() == 'j' || e.getKeyChar() == 'J')
                {
                    TRIPPY_BACKGROUND.set( !TRIPPY_BACKGROUND.get() );
                    layoutFrame.setTrippyBackground( TRIPPY_BACKGROUND.get() );
                    graph.prepareBackgroundColor();
                    refreshDisplay();
                }
                else if (e.getKeyChar() == 'h' || e.getKeyChar() == 'H')
                {
                    if ( !animationRender && !SHOW_3D_ENVIRONMENT_MAPPING.get() )
                    {
                        MATERIAL_SPHERICAL_MAPPING.set( !MATERIAL_SPHERICAL_MAPPING.get() );
                        CHANGE_SPHERICAL_MAPPING_ENABLED = true;
                        layoutFrame.setSphericalMapping( MATERIAL_SPHERICAL_MAPPING.get() );

                        updateNodesAndSelectedNodesDisplayList();
                    }
                }
                else if (e.getKeyChar() == 'i' || e.getKeyChar() == 'I')
                {
                    MATERIAL_EMBOSS_NODE_TEXTURE.set( !MATERIAL_EMBOSS_NODE_TEXTURE.get() );
                    layoutFrame.setEmbossNodeTexture( MATERIAL_EMBOSS_NODE_TEXTURE.get() );
                    refreshDisplay();
                }
                else if (e.getKeyChar() == '1')
                {
                    enableDisableShaderLightingSFX(ShaderLightingSFXs.ShaderTypes.PHONG);
                }
                else if (e.getKeyChar() == '2')
                {
                    enableDisableShaderLightingSFX(ShaderLightingSFXs.ShaderTypes.BUMP);
                }
                else if (e.getKeyChar() == '3')
                {
                    enableDisableShaderLightingSFX(ShaderLightingSFXs.ShaderTypes.TOON);
                }
                else if (e.getKeyChar() == '4')
                {
                    enableDisableShaderLightingSFX(ShaderLightingSFXs.ShaderTypes.GOOCH);
                }
                else if (e.getKeyChar() == '5')
                {
                    enableDisableShaderLightingSFX(ShaderLightingSFXs.ShaderTypes.CLOUD);
                }
                else if (e.getKeyChar() == '6')
                {
                    enableDisableShaderLightingSFX(ShaderLightingSFXs.ShaderTypes.LAVA);
                }
                else if (e.getKeyChar() == '7')
                {
                    enableDisableShaderLightingSFX(ShaderLightingSFXs.ShaderTypes.MARBLE);
                }
                else if (e.getKeyChar() == '8')
                {
                    enableDisableShaderLightingSFX(ShaderLightingSFXs.ShaderTypes.GRANITE);
                }
                else if (e.getKeyChar() == '9')
                {
                    enableDisableShaderLightingSFX(ShaderLightingSFXs.ShaderTypes.WOOD);
                }
                else if (e.getKeyChar() == '0')
                {
                    ShaderLightingSFXs.ShaderTypes[] shaderTypes = ShaderLightingSFXs.ShaderTypes.values();
                    enableDisableShaderLightingSFX(shaderTypes[(currentShaderType.ordinal() + 1) % ShaderLightingSFXs.NUMBER_OF_AVAILABLE_SHADERS]);
                }
            }
        }
    }

    /**
    *  MouseClicked mouseEvent.
    */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        if ( SwingUtilities.isLeftMouseButton(e) )
        {
            boolean isCtrlDown = (!IS_MAC) ? e.isControlDown() : e.isMetaDown();

            findNode(e.getX(), e.getY(), true, e.isAltDown());
            refreshDisplay();
            refreshDisplay(); // second update to show the selected node

            pickFind = false;

            if (!isShiftDown && isCtrlDown)
            {
                if ( !lastNodeURLStringPicked.isEmpty() )
                    InitDesktop.browse(lastNodeURLStringPicked);
                else if ( !lastNodeNamePicked.isEmpty() )
                    InitDesktop.browse(SEARCH_URL.getUrl() + lastNodeNamePicked.split("\\s+")[0]);
            }
        }
    }

    /**
    *  MouseEntered mouseEvent.
    */
    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    /**
    *  MouseExited mouseEvent.
    */
    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    /**
    *  MousePressed mouseEvent.
    */
    @Override
    public void mousePressed(MouseEvent e)
    {
        mouseButton = e.getButton();
        mouseLastX = mouseDragStartX = e.getX();
        mouseLastY = mouseDragStartY = e.getY();

        if (mouseButton == MouseEvent.BUTTON1)
        {
            if (isShiftDown)
            {
                pick(e.getX(), e.getY(), isShiftAltDown);
                refreshDisplay();
            }
        }
        else if (mouseButton == MouseEvent.BUTTON2)
        {
            refreshDisplay();
        }
        else if (mouseButton == MouseEvent.BUTTON3)
        {
            refreshDisplay();
        }
    }

    /**
    *  MouseReleased mouseEvent.
    */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        isInMotion = false;

        if ( pickingBox || (currentMouseMode.equals(MouseModeTypes.SELECT) && pickingBox) )
        {
            pickOriginX = (int)rint( ( mouseDragStartX + e.getX() ) / 2 );
            pickOriginY = (int)rint( ( mouseDragStartY + e.getY() ) / 2 );
            pickWidth  = abs(e.getX() - mouseDragStartX);
            pickHeight = abs(e.getY() - mouseDragStartY);

            pick(pickOriginX, pickOriginY, pickWidth, pickHeight, isShiftAltDown);
            pickingBox = false;

            refreshDisplay();
        }

        refreshDisplay();
        mouseButton = 0;

        mouseDragStartX = 0;
        mouseDragStartY = 0;
    }

    /**
    *  MouseDragged mouseMotionEvent.
    */
    @Override
    public void mouseDragged(MouseEvent e)
    {
        if ( graphPopupIsTiming || GRAPH_POPUP_COMPONENT.isPopupMenuVisible() )
        {
            graphPopupIsTiming = false;
            GRAPH_POPUP_COMPONENT.setPopupMenuVisible(false);
            graphPopupScheduledFuture.cancel(true);
        }

        if ( (mouseButton == MouseEvent.BUTTON1) && ( currentMouseMode.equals(MouseModeTypes.ROTATE) || currentMouseMode.equals(MouseModeTypes.SELECT) ) )
        {

            if ( isShiftDown || currentMouseMode.equals(MouseModeTypes.SELECT) )
            {
                pickingBox = true;
                selectBox( mouseDragStartX, mouseDragStartY, e.getX(), e.getY() );
            }
	    else if ( e.isControlDown() )
	    {
		wRotate( mouseLastX, mouseLastY, e.getX(), e.getY() );
	    }
	    else
            {
		zRotate( mouseLastX, mouseLastY, e.getX(), e.getY() );
            }

            refreshDisplay();

            mouseLastX = e.getX();
            mouseLastY = e.getY();
        }
        else if ( (mouseButton == MouseEvent.BUTTON2) || currentMouseMode.equals(MouseModeTypes.TRANSLATE) )
        {
            translate( mouseLastX, mouseLastY, e.getX(), e.getY() );
            refreshDisplay();

            mouseLastX = e.getX();
            mouseLastY = e.getY();
        }
        else if ( (mouseButton == MouseEvent.BUTTON3) || currentMouseMode.equals(MouseModeTypes.SCALE) )
        {
            scale( mouseLastX, mouseLastY, e.getX(), e.getY() );
            refreshDisplay();

            mouseLastX = e.getX();
            mouseLastY = e.getY();
        }        
    }

    /**
    *  MouseMoved mouseMotionEvent.
    */
    @Override
    public void mouseMoved(MouseEvent e)
    {
        if ( !(autoRotate || autoPulsate) )
        {
            if ( graphPopupIsTiming || GRAPH_POPUP_COMPONENT.isPopupMenuVisible() )
            {
                graphPopupIsTiming = false;
                GRAPH_POPUP_COMPONENT.setPopupMenuVisible(false);
                graphPopupScheduledFuture.cancel(true);
            }

            findNode(e.getX(), e.getY(), false, false);
            refreshDisplay();

            pickFind = false;
            if ( !lastNodeNamePicked.isEmpty() )
            {
                layoutFrame.setNodeLabel(lastNodeNamePicked);
                if(!selectionManager.getSelectedNodes().isEmpty() && selectionManager.getSelectedNodes().contains(closestNode))
                {
                    GRAPH_POPUP_COMPONENT.setPopupComponent(graph, e.getX(), e.getY(),
                            selectionManager.getSelectedNodes(), nc, layoutFrame);
                }
                else
                {
                    GRAPH_POPUP_COMPONENT.setPopupComponent(graph, e.getX(), e.getY(), closestNode, nc, layoutFrame);
                }
                graphPopupScheduledFuture = GRAPH_POPUP_SCHEDULED_EXECUTOR_SERVICE.schedule(GRAPH_POPUP_COMPONENT, COMPONENT_POPUP_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS);
                graphPopupIsTiming = true;
                graphPopupReset = true;
            }
            else
            {
                if (graphPopupReset)
                {
                    graphPopupReset = false;
                    layoutFrame.setNodeLabel(NO_NODE_FOUND_LABEL);
                }
            }
        }
    }

    /**
    *  MouseWheelMoved mouseWheelEvent.
    */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (DEBUG_BUILD) println("GraphRenderer3D mouseWheelMoved()");
    }

    /**
    *  Switches the renderer mode for GraphRenderer3D.
    */
    @Override
    public void switchRendererMode()
    {
        if (DEBUG_BUILD) println("GraphRenderer3D switchRendererMode()");

        layoutFrame.setEnabledAllToolBars(false);
        listener.switchRendererModeCallBack();
    }

    /**
    *  Adds all events to GraphRenderer3D.
    */
    @Override
    public void addAllEvents()
    {
        if (DEBUG_BUILD) println("GraphRenderer3D addAllEvents()");

        deAllocOpenGLMemory = false;

        refreshDisplay();
    }

    /**
    *  Removes all events from GraphRenderer3D.
    */
    @Override
    public void removeAllEvents()
    {
        if (DEBUG_BUILD) println("GraphRenderer3D removeAllEvents()");

        deAllocOpenGLMemory = true;
        resetAnimationValues();
    }

    /**
    *  Gets a BufferedImage (mainly used for printing functionality).
    */
    @Override
    public BufferedImage getBufferedImage()
    {
        if (DEBUG_BUILD) println("GraphRenderer3D getBufferedImage()");

        renderToFile = false;
        takeScreenshot = true;
        refreshDisplay();
        renderToFile = true;

        return screenshot;
    }

    /**
    *  Fully updates the display lists.
    */
    @Override
    public void updateAllDisplayLists()
    {
        if (DEBUG_BUILD) println("updateAllDisplayLists() for 3D mode");

        updateDisplayLists(true, true, true);
        refreshDisplay();
        refreshDisplay(); // 2nd refresh to make sure new double display lists system refreshes properly
    }

    /**
    *  Updates the nodes display list only.
    */
    @Override
    public void updateNodesDisplayList()
    {
        if (DEBUG_BUILD) println("updateNodesDisplayList() for 3D mode");

        updateDisplayLists(true, false, false);
        refreshDisplay();
        refreshDisplay(); // 2nd refresh to make sure new double display lists system refreshes properly
    }

    /**
    *  Updates the selected nodes display list only.
    */
    @Override
    public void updateSelectedNodesDisplayList()
    {
        if (DEBUG_BUILD) println("updateSelectedNodesDisplayList() for 3D mode");

        updateDisplayLists(false, false, true);
        refreshDisplay();
        refreshDisplay(); // 2nd refresh to make sure new double display lists system refreshes properly
    }

    /**
    *  Updates the nodes & selected nodes display lists only.
    */
    @Override
    public void updateNodesAndSelectedNodesDisplayList()
    {
        if (DEBUG_BUILD) println("updateNodesAndSelectedNodesDisplayList() for 3D mode");

        updateDisplayLists(true, false, true);
        refreshDisplay();
        refreshDisplay(); // 2nd refresh to make sure new double display lists system refreshes properly
    }

    /**
    *  Updates the edges display lists only.
    */
    @Override
    public void updateEdgesDisplayList()
    {
        if (DEBUG_BUILD) println("updateEdgesDisplayList() for 3D mode");

        updateDisplayLists(false, true, false);
        refreshDisplay();
        refreshDisplay(); // 2nd refresh to make sure new double display lists system refreshes properly
    }

    /**
    *  Updates the display lists selectively.
    */
    @Override
    public void updateDisplayLists(boolean nodesDisplayList, boolean edgesDisplayList, boolean selectedNodesDisplayList)
    {
        if (DEBUG_BUILD) println("updateDisplayLists(" + nodesDisplayList + ", " + edgesDisplayList + ", " + selectedNodesDisplayList + ") for 3D mode");

        graph.prepareBackgroundColor();

        if ( prevYEdStyleRrenderingForGraphmlFiles == YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
        {
            updateNodesDisplayList = nodesDisplayList;
            updateEdgesDisplayList = nodesDisplayList ? true : edgesDisplayList; // always update the edges when nodes are being updated, to properly re-calculate the edge-to-node connections
            updateSelectedNodesDisplayList = selectedNodesDisplayList;
        }
        else
        {
            updateNodesDisplayList = true;
            updateEdgesDisplayList = true;
            updateSelectedNodesDisplayList = true;

            resetView();
        }

        prevYEdStyleRrenderingForGraphmlFiles = YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get();

        refreshDisplay();
        refreshDisplay(); // 2nd refresh to make sure new double display lists system refreshes properly
    }

    /**
    *  Updates all animation.
    */
    @Override
    public void updateAnimation()
    {
        if (DEBUG_BUILD) println("Updating 3D Scene");

        if (autoRotate && !animationRender)
        {
            xzRotate -= AMOUNT_OF_ROTATION;
            yzRotate -= AMOUNT_OF_ROTATION;
        }

        if (autoPulsate)
        {
            // frameCount % 2 with bitshift for speed, every even frame do pulsation
            if ( (graphRendererThreadUpdater != null) && (graphRendererThreadUpdater.getFrameCount() & 1) == 0 )
            {
                if (pulsateValue)
                    pulseSteps--;
                else
                    pulseSteps++;

                if ( (pulseSteps >= PULSATION_UPPER_THRESHOLD) || (pulseSteps <= 0) )
                    pulsateValue = !pulsateValue;

                if (USE_SHADERS_PROCESS)
                {
                    if ( layoutFrame.isAllShadingSFXSValueEnabled() )
                        performMorphingBasedPulsation();
                    else
                        performNonMorphingBasedPulsation();
                }
                else
                {
                    performNonMorphingBasedPulsation();
                }
            }
        }

        if ( USE_SHADERS_PROCESS && MATERIAL_SPECULAR.get() )
        {
            if (isAutoRendering)
            {
                if ( MATERIAL_ANIMATED_SHADING.get() )
                {
                    shaderLightingSFXsNodes.timerEffect( ALL_SHADING_SFXS[ShaderLightingSFXs.ShaderTypes.WATER.ordinal()].get() );
                }
            }
        }

        if (animationRender)
        {
            currentTick = (int)( ( ++animationFrameCount / (FRAMERATE_PER_SECOND_FOR_ANIMATION / ANIMATION_TICKS_PER_SECOND) ) );
            if (currentTick >= TOTAL_NUMBER_OF_ANIMATION_TICKS)
            {
                currentTick = TOTAL_NUMBER_OF_ANIMATION_TICKS - 1;
                ANIMATION_INITIATE_END_OF_ANIMATION = true;
            }

            if (stepAnimation)
            {
                stepAnimation = false;
                generalPauseRenderUpdateThread();
            }
        }
    }

    /**
    *  Refreshes the display.
    */
    @Override
    public void refreshDisplay()
    {
        if (graph != null) graph.display();
    }

    /**
    *  Resets all values.
    */
    @Override
    public void resetAllValues()
    {
        if (DEBUG_BUILD) println("GraphRenderer3D resetAllValues()");

        if (!animationRender)
            stopRotationAndPulsation();

        resetView();
        refreshDisplay();
        refreshDisplay(); // 2nd refresh to make sure new double display lists system refreshes properly
    }

    /**
    *  Checks if there are more undo steps to be performed.
    */
    @Override
    public boolean hasMoreUndoSteps() { return false; }

    /**
    *  Checks if there are more redo steps to be performed.
    */
    @Override
    public boolean hasMoreRedoSteps() { return false; }

    /**
    *  The main take a screenshot process.
    */
    @Override
    public void takeScreenShotProcess(boolean doHighResScreenShot)
    {
        //give time for menubar to hide
        refreshDisplay();

        if (doHighResScreenShot)
            highResRenderToFile();
        else
            takeScreenshot = true;

        refreshDisplay();
        // second refresh for cleaning behind InitDesktop.open(savedImage)
        if (doHighResScreenShot)
            refreshDisplay();
    }

    /**
    *  Creates the reTranslate action.
    */
    @Override
    public void createReTranslateAction(TranslateTypes translateType, ActionEvent e)
    {
        if ( translateType.equals(TranslateTypes.TRANSLATE_UP) )
            translateDY += DEFAULT_TRANSLATION;
        else if ( translateType.equals(TranslateTypes.TRANSLATE_DOWN) )
            translateDY -= DEFAULT_TRANSLATION;
        else if ( translateType.equals(TranslateTypes.TRANSLATE_LEFT) )
            translateDX += DEFAULT_TRANSLATION;
        else if ( translateType.equals(TranslateTypes.TRANSLATE_RIGHT) )
            translateDX -= DEFAULT_TRANSLATION;

        refreshDisplay();

        if (DEBUG_BUILD) println("GraphRenderer3D reTranslate()");
    }

    /**
    *  Creates the reRotate action.
    */
    @Override
    public void createReRotateAction(RotateTypes rotateType, ActionEvent e)
    {
        if ( rotateType.equals(RotateTypes.ROTATE_UP) )
            yzRotate -= DEFAULT_ROTATION;
        else if ( rotateType.equals(RotateTypes.ROTATE_DOWN) )
            yzRotate += DEFAULT_ROTATION;
        else if ( rotateType.equals(RotateTypes.ROTATE_LEFT) )
            xzRotate += DEFAULT_ROTATION;
        else if ( rotateType.equals(RotateTypes.ROTATE_RIGHT) )
            xzRotate -= DEFAULT_ROTATION;

        refreshDisplay();

        if (DEBUG_BUILD) println("GraphRenderer3D reRotate()");
    }

    /**
    *  Creates the reScale action.
    */
    @Override
    public void createReScaleAction(ScaleTypes scaleType, ActionEvent e)
    {
        scaleValue /= scaleType.equals(ScaleTypes.SCALE_IN) ? SCALE_FACTOR : 1.0f / SCALE_FACTOR;

        refreshDisplay();

        if (DEBUG_BUILD) println("GraphRenderer3D reScale()");
    }

    /**
    *  Creates the Burst Layout Iterations action.
    */
    @Override
    public void createBurstLayoutIterationsAction(ActionEvent e) {}

    /**
    *  Gets the undo node dragging action.
    */
    @Override
    public void createUndoNodeDraggingAction(ActionEvent e) {}

    /**
    *  Gets the redo node dragging action.
    */
    @Override
    public void createRedoNodeDraggingAction(ActionEvent e) {}

    /**
    *  Gets the autorotate action.
    */
    @Override
    public void createAutoRotateAction(ActionEvent e)
    {
        toggleRotation(e);
    }

    /**
    *  Gets the screensaver 2D mode action.
    */
    @Override
    public void createAutoScreenSaver2DModeAction(ActionEvent e) {}

    /**
    *  Gets the pulsation 3D mode action.
    */
    @Override
    public void createPulsation3DModeAction(ActionEvent e)
    {
        togglePulsation();
    }

    /**
    *  Gets the selection action.
    */
    @Override
    public void createSelectAction(ActionEvent e)
    {
        currentMouseMode = MouseModeTypes.SELECT;
    }

    /**
    *  Gets the translation action.
    */
    @Override
    public void createTranslateAction(ActionEvent e)
    {
        currentMouseMode = MouseModeTypes.TRANSLATE;
    }

    /**
    *  Gets the rotation action.
    */
    @Override
    public void createRotateAction(ActionEvent e)
    {
        currentMouseMode = MouseModeTypes.ROTATE;
    }

    /**
    *  Gets the zoom action.
    */
    @Override
    public void createZoomAction(ActionEvent e)
    {
        currentMouseMode = MouseModeTypes.SCALE;
    }

    /**
    *  Gets the reset view action.
    */
    @Override
    public void createResetViewAction(ActionEvent e)
    {
        resetAllValues();
    }

    /**
    *  Gets the render action.
    */
    @Override
    public void createRenderImageToFileAction(ActionEvent e) {}

    /**
    *  Gets the high resolution render action.
    */
    @Override
    public void createRenderHighResImageToFileAction(ActionEvent e) {}

    /**
    *  Generally pauses the renderer.
    */
    @Override
    public void generalPauseRenderUpdateThread()
    {
        graphRendererThreadUpdater.generalPauseRenderUpdateThread();
    }

    /**
    *  Generally resumes the renderer.
    */
    @Override
    public void generalResumeRenderUpdateThread()
    {
        graphRendererThreadUpdater.generalResumeRenderUpdateThread();
    }

    /**
    *  The name of the GraphRenderer3D object.
    */
    @Override
    public String toString()
    {
        return "3D OpenGL renderer";
    }

    /**
    *  Clean up resources
    */
    @Override
    public void dispose(GLAutoDrawable glAutoDrawable)
    {
      //FIXME this was added during the move to JOGL 2
      //TODO check if resources need to be freed here
    }
}
