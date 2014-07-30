package org.BioLayoutExpress3D.GPUComputing.OpenGLContext;

import java.awt.*;
import java.nio.*;
import java.util.concurrent.*;
import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.nativewindow.awt.*;
import org.BioLayoutExpress3D.DataStructures.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import javax.media.opengl.GLCapabilities;
import javax.media.nativewindow.*;
import static javax.media.opengl.GL2.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* OpenGLContext is the main OpenGL context component for GPU Computing.
* It is a heavyweight AWT component, Canvas, that utilizes an Active Rendering framework for OpenGL, only available in JOGL JSR-231 and above.
 *
* @author Dominik Göddeke, Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public abstract class OpenGLContext extends Canvas
{
    /**
    *  Serial version UID variable for the OpenGLContext class.
    */
    public static final long serialVersionUID = 119222333444555669L;

    /**
    *  Frame buffer object attachment points.
    */
    protected static final int[] ATTACHMENT_POINTS = { GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2, GL_COLOR_ATTACHMENT3 };

    /**
    *  Frame buffer object reference.
    */
    private final IntBuffer FBO = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();

    /**
    *  Reference for the GLU class.
    */
    protected final static GLU glu = new GLU();

    /**
    *  Reference for the GLDrawable class.
    */
    private GLDrawable drawable = null;   // the rendering 'surface'

    /**
    *  Reference for the GLContext class.
    */
    private GLContext context = null;     // the rendering context (holds rendering state info)

    /**
    *  Reference for the AWTGraphicsConfiguration class.
    */
    private static AWTGraphicsConfiguration awtConfig = null;

    /**
    *  Value needed for the OpenGL GPU Computations.
    */
    protected int textureSize = 0;

    /**
    *  Reference for the AllTextureParameters.TextureParameters class.
    */
    protected AllTextureParameters.TextureParameters textureParameters = null;

    /**
    *  Reference for the GPGPUCompatibilityCheck class.
    */
    private GPGPUCompatibilityCheck thisGPGPUCompatibilityCheck = null;

    /**
    *  Value needed for the OpenGL GPU Computations.
    */
    private boolean CPUErrorOccured = false;

    /**
    *  Value needed for the OpenGL GPU Computations.
    */
    private boolean GPUErrorOccured = false;

    /**
    *  Value needed for error messages displaying.
    */
    private boolean dialogErrorLog = false;

    /**
    *  Value needed for OpenGL support and extensions messages displaying.
    */
    private boolean openGLSupportAndExtensionsLogOnly = false;

    /**
    *  The first constructor of the OpenGLContext class.
    */
    public OpenGLContext(AllTextureParameters.TextureParameters textureParameters, int textureSize, int problemDomainX, int problemDomainY)
    {
        this(textureParameters, textureSize, problemDomainX, problemDomainY, false, false);
    }

    /**
    *  The second constructor of the OpenGLContext class.
    */
    public OpenGLContext(AllTextureParameters.TextureParameters textureParameters, int textureSize, int problemDomainX, int problemDomainY, boolean dialogErrorLog)
    {
        this(textureParameters, textureSize, problemDomainX, problemDomainY, dialogErrorLog, false);
    }

    /**
    *  The third constructor of the OpenGLContext class.
    */
    public OpenGLContext(AllTextureParameters.TextureParameters textureParameters, int textureSize, int problemDomainX, int problemDomainY, boolean dialogErrorLog, boolean openGLSupportAndExtensionsLogOnly)
    {
        super( initGraphicsConfiguration() );

        this.textureParameters = textureParameters;
        this.textureSize = textureSize;
        this.dialogErrorLog = dialogErrorLog;
        this.openGLSupportAndExtensionsLogOnly = openGLSupportAndExtensionsLogOnly;

        thisGPGPUCompatibilityCheck = new GPGPUCompatibilityCheck(textureParameters, textureSize, problemDomainX, problemDomainY);

        initRenderingSurfaceAndContext();
        setBackground(Color.BLACK);
    }

    /**
    *  Initializes a configuration suitable for an AWT Canvas.
    *  Has to be static in order to be used from the constructor super() call.
    */
    private static GraphicsConfiguration initGraphicsConfiguration()
    {
        GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
        caps.setDoubleBuffered(false);
        caps.setHardwareAccelerated(true);
        caps.setSampleBuffers(false);

        AWTGraphicsScreen screen = (AWTGraphicsScreen)AWTGraphicsScreen.createDefault();
        awtConfig = (AWTGraphicsConfiguration)GraphicsConfigurationFactory.getFactory(AWTGraphicsDevice.class,
              caps.getClass()).chooseGraphicsConfiguration(caps, caps, null, screen, VisualIDHolder.VID_UNDEFINED);

        GraphicsConfiguration config = null;
        if (awtConfig != null)
          config = awtConfig.getAWTGraphicsConfiguration();

        return config;
    }

    /**
    *  Initializes a rendering surface and a context for this canvas.
    */
    private void initRenderingSurfaceAndContext()
    {
        drawable = GLDrawableFactory.getFactory(GLProfile.getDefault()).createGLDrawable(
            NativeWindowFactory.getNativeWindow(this, awtConfig));

        context = drawable.createContext(null);
    }

    /**
    *  Wait for the Canvas to be added to a Swing component.
    */
    @Override
    public void addNotify()
    {
        super.addNotify();          // make the component displayable
        drawable.setRealized(true); // the canvas can now be rendered into
    }

    /**
    *  Reports the GPU Computing variables.
    */
    protected void reportGPUComputingVariables()
    {
        println("\nTexture Parameters: " + textureParameters.textureName +
                "\nTexture Size: " + textureSize +
                "\nTexture Dimension: " + textureSize + " * " + textureSize + " = " + textureSize * textureSize + " pixels" +
                "\n" + ( (textureParameters.textureFormat == GL_RGBA) ? "4 floats per texel = 16 bytes per texel = 128 bits per texel" : "1 float per texel = 4 bytes per texel = 32 bits per texel" ) +
                "\nVRAM usage for render buffer: " + ( (textureParameters.textureFormat == GL_RGBA) ? 16 * (textureSize * textureSize) : (textureSize * textureSize) ) + " bytes\n");
    }

    /**
    *  Starts the OpenGL context and the GPU Computing processing.
    */
    public void doGPUComputingProcessing()
    {
        startGPUComputingProcessing(false);
    }

    /**
    *  Starts the OpenGL context and the GPU Computing processing.
       Overloaded version that invokes the GPU Computing Processing within a thread.
    */
    public void startGPUComputingProcessing(boolean invokeInThread)
    {
        if (invokeInThread)
        {
            // Submit the task to be executed by the AWT/Swing event dispatch thread.
            // It is put on the event queue to be (eventually) executed by the event dispatch thread.
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    Thread runLightWeightThread = new Thread( new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            initializeAndStartGPUComputingProcessing();
                        }


                    } );

                    runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
                    runLightWeightThread.start();
                }
            } );
        }
        else
            initializeAndStartGPUComputingProcessing();
    }

    /**
    *  Called by the JOGL immediately after the OpenGL context is initialized.
    */
    private void initializeAndStartGPUComputingProcessing()
    {
        OpenGLContextDialog openGLContextDialog = new OpenGLContextDialog(this);

        if ( makeContextCurrent() )
        {
            /*
            * This demonstrates the use of the DebugGL composible pipeline.  In the
            * OpenGL C API, errors are indicated by setting an error code.  When the
            * DebugGL pipline is enabled, the error code is checked after every call
            * to a GL method.  A GLException will be thrown if an error occurs, and
            * the GLException object will contain the error code.
            */
            // gl = new DebugGL( context.getGL() );

            /*
            * This example demonstrates the use of the TraceGL composable pipeline.
            * When the TraceGL pipeline is in place, all OpenGL calls will cause
            * debug output to be sent to the given stream.
            * This case sends all debug output to System.out
            */
            // gl = new TraceGL(context.getGL(), System.out);

            GL2 gl = context.getGL().getGL2();

            requestFocusInCanvas();
            checkOpenGLSupportAndExtensions(gl);

            if (!openGLSupportAndExtensionsLogOnly)
            {
                if (USE_SHADERS_PROCESS)
                {
                    Tuple2<Boolean, String> tuple2 = thisGPGPUCompatibilityCheck.isGPUCompatible(gl);
                    if (tuple2.first)
                    {
                        prepareLowQualityRendering(gl);
                        initializeClamping(gl);
                        initializeFBO(gl);
                        initializeOrthogonalProjection(gl);
                        initializeCPUMemory(gl);
                        if (!CPUErrorOccured)
                        {
                            initializeGPUMemory(gl);
                            if (!GPUErrorOccured)
                                performGPUComputingCalculations(gl);
                            if (!GPUErrorOccured)
                                retrieveGPUResults(gl);
                        }
                        deleteOpenGLContext(gl);
                    }
                    else
                    {
                        if (dialogErrorLog)
                            JOptionPane.showMessageDialog(this, "OpenGL GPU Compatibility error:\n" + tuple2.second, "OpenGL GPU Compatibility Error", JOptionPane.WARNING_MESSAGE);
                        if (DEBUG_BUILD) println("OpenGL GPU Compatibility error:\n" + tuple2.second);

                        GPUErrorOccured = true;
                    }
                }
                else
                {
                    if (dialogErrorLog)
                        JOptionPane.showMessageDialog(this, "This GPU does not support OpenGL version " + MINIMUM_OPENGL_VERSION_FOR_QUALITY_RENDERING_AND_SHADERS + " or above!", "OpenGL GPU Compatibility Error", JOptionPane.WARNING_MESSAGE);
                    if (DEBUG_BUILD) println("This GPU does not support OpenGL version " + MINIMUM_OPENGL_VERSION_FOR_QUALITY_RENDERING_AND_SHADERS + " or above!");

                    GPUErrorOccured = true;
                }
            }

            // release the context, otherwise the AWT lock on X11 will not be released
            context.release();

            // avoid the glError check, as one will be always generated from the context.release() call above
            // checkGLErrors("initializeAndStartGPUComputingProcessing()");
        }
        else
        {
            if (dialogErrorLog)
                JOptionPane.showMessageDialog(this, "No Current OpenGL Context!", "OpenGL GPU Compatibility Error", JOptionPane.WARNING_MESSAGE);
            if (DEBUG_BUILD) println("No Current OpenGL Context!");

            GPUErrorOccured = true;
        }

        if (DEBUG_BUILD) println( "\nOpenGL GPU Computing overal error status: " + ( getErrorOccured() ? "Errors occured." : "No errors occured." ) );

        openGLContextDialog.dispose();
    }

    /**
    *  Makes the OpenGL context current for this thread.
    */
    private boolean makeContextCurrent()
    {
        try
        {
            int tries = 0;
            int status = -1;
            do
            {
                if (tries > 0)
                {
                    if (DEBUG_BUILD)
                        println("Context not current. Retrying... " + tries);

                    TimeUnit.MILLISECONDS.sleep(10);
                }

                status = context.makeCurrent();
                tries++;
            } while (status == GLContext.CONTEXT_NOT_CURRENT && tries < 50);

            return status == GLContext.CONTEXT_CURRENT || status == GLContext.CONTEXT_CURRENT_NEW;
        }
        catch (InterruptedException ex)
        {
            // restore the interuption status after catching InterruptedException
            Thread.currentThread().interrupt();
            if (DEBUG_BUILD) println("InterruptedException in makeContentCurrent():\n" + ex.getMessage());
            return false;
        }
    }

    /**
    *  Requests focus for this Canvas.
    */
    private void requestFocusInCanvas()
    {
        setFocusable(true);
        if ( requestFocusInWindow() )
        {
            if (DEBUG_BUILD) println("requestFocusInWindow for the Canvas was successful!\n");
        }
        else
        {
            if (DEBUG_BUILD) println("requestFocusInWindow for the Canvas has failed!\n");
        }
    }

    /**
    *  Checks OpenGL support & extensions mechanisms.
    */
    private void checkOpenGLSupportAndExtensions(GL2 gl)
    {
        GL_VENDOR_STRING = gl.glGetString(GL_VENDOR);
        GL_RENDERER_STRING = gl.glGetString(GL_RENDERER);
        GL_VERSION_STRING = gl.glGetString(GL_VERSION);
        GL_EXTENSIONS_STRINGS = gl.glGetString(GL_EXTENSIONS).split("\\s+");
        GL_IS_AMD_ATI = ( GL_VENDOR_STRING.contains("ATI") || GL_VENDOR_STRING.contains("AMD") );
        GL_IS_NVIDIA = GL_VENDOR_STRING.contains("NVIDIA");

        if (DEBUG_BUILD)
        {
            StringBuilder output = new StringBuilder();
            output.append("\n\nOpenGL Driver Capabilities:\n\n");
            output.append("GL_VENDOR:\t").append(GL_VENDOR_STRING).append("\n");
            output.append("GL_RENDERER:\t").append(GL_RENDERER_STRING).append("\n");
            output.append("GL_VERSION:\t").append(GL_VERSION_STRING).append("\n\n");
            output.append("\n").append(GL_EXTENSIONS_STRINGS.length).append(" available GL Extensions: " + "\n\n");
            for (String glExtension : GL_EXTENSIONS_STRINGS)
                output.append(glExtension).append("\n");
            println( output.toString() );
        }

        if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
        {
            int firstIndexOfDot = GL_VERSION_STRING.indexOf(".");
            float openGLVersion = Float.parseFloat( GL_VERSION_STRING.substring(0, firstIndexOfDot + 2) );
            if ( USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER = (openGLVersion >= MINIMUM_OPENGL_VERSION_FOR_VERTEX_ARRAYS) )
            {
                // initialize OpenGL Vertex Arrays support
                gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
                gl.glEnableClientState(GL_NORMAL_ARRAY);
                gl.glEnableClientState(GL_VERTEX_ARRAY);
            }
        }

        if (USE_SHADERS_PROCESS)
        {
            int firstIndexOfDot = GL_VERSION_STRING.indexOf(".");
            float openGLVersion = Float.parseFloat( GL_VERSION_STRING.substring(0, firstIndexOfDot + 2) );
            if ( USE_SHADERS_PROCESS = ( (openGLVersion >= MINIMUM_OPENGL_VERSION_FOR_QUALITY_RENDERING_AND_SHADERS) || LoadNativeLibrary.isMacLionAndAbove() ) )
            {
                USE_330_SHADERS_PROCESS = (openGLVersion >= MINIMUM_OPENGL_VERSION_FOR_330_SHADERS);

                IntBuffer OPENGL_INT_VALUE = Buffers.newDirectIntBuffer(1);
                GL_SHADING_LANGUAGE_VERSION_STRING = gl.glGetString(GL_SHADING_LANGUAGE_VERSION);
                gl.glGetIntegerv(GL_MAX_DRAW_BUFFERS, OPENGL_INT_VALUE);
                GL_MAX_DRAW_BUFFERS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_COLOR_ATTACHMENTS, OPENGL_INT_VALUE);
                GL_MAX_COLOR_ATTACHMENTS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_AUX_BUFFERS, OPENGL_INT_VALUE);
                GL_AUX_BUFFERS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_TEXTURE_UNITS, OPENGL_INT_VALUE);
                GL_MAX_TEXTURE_UNITS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_VERTEX_ATTRIBS, OPENGL_INT_VALUE);
                GL_MAX_VERTEX_ATTRIBS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_VERTEX_UNIFORM_COMPONENTS, OPENGL_INT_VALUE);
                GL_MAX_VERTEX_UNIFORM_COMPONENTS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, OPENGL_INT_VALUE);
                GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_VARYING_FLOATS, OPENGL_INT_VALUE);
                GL_MAX_VARYING_FLOATS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, OPENGL_INT_VALUE);
                GL_MAX_TEXTURE_IMAGE_UNITS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_TEXTURE_COORDS, OPENGL_INT_VALUE);
                GL_MAX_TEXTURE_COORDS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, OPENGL_INT_VALUE);
                GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_FRAGMENT_UNIFORM_COMPONENTS, OPENGL_INT_VALUE);
                GL_MAX_FRAGMENT_UNIFORM_COMPONENTS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_3D_TEXTURE_SIZE, OPENGL_INT_VALUE);
                GL_MAX_3D_TEXTURE_SIZE_INTEGER = OPENGL_INT_VALUE.get(0);
                if ( USE_GL_ARB_TEXTURE_RECTANGLE = gl.isExtensionAvailable("GL_ARB_texture_rectangle") )
                {
                    gl.glGetIntegerv(GL_MAX_RECTANGLE_TEXTURE_SIZE_ARB, OPENGL_INT_VALUE);
                    GL_MAX_RECTANGLE_TEXTURE_SIZE_ARB_INTEGER = OPENGL_INT_VALUE.get(0);
                }
                gl.glGetIntegerv(GL_MAX_TEXTURE_SIZE, OPENGL_INT_VALUE);
                GL_MAX_TEXTURE_SIZE_INTEGER = OPENGL_INT_VALUE.get(0);
                if ( USE_GL_EXT_FRAMEBUFFER_OBJECT = gl.isExtensionAvailable("GL_EXT_framebuffer_object") )
                {
                    gl.glGetIntegerv(GL_MAX_RENDERBUFFER_SIZE, OPENGL_INT_VALUE);
                    GL_MAX_RENDERBUFFER_SIZE_EXT_INTEGER = OPENGL_INT_VALUE.get(0);
                }
                USE_GL_EXT_GPU_SHADER4 = gl.isExtensionAvailable("GL_EXT_gpu_shader4");
                USE_GL_ARB_GPU_SHADER5 = gl.isExtensionAvailable("GL_ARB_gpu_shader5");
                USE_GL_ARB_GPU_SHADER_FP64 = gl.isExtensionAvailable("GL_ARB_gpu_shader_fp64");

                if (DEBUG_BUILD)
                {
                    StringBuilder output = new StringBuilder();
                    output.append("\nGLSL Driver Capabilities:\n\n");
                    output.append("GL_SHADING_LANGUAGE_VERSION:\t\t").append(GL_SHADING_LANGUAGE_VERSION_STRING).append("\n");
                    output.append("GL_MAX_DRAW_BUFFERS:\t\t\t").append(GL_MAX_DRAW_BUFFERS_INTEGER).append("\n");
                    output.append("GL_MAX_COLOR_ATTACHMENTS:\t\t").append(GL_MAX_COLOR_ATTACHMENTS_INTEGER).append("\n");
                    output.append("GL_AUX_BUFFERS:\t\t\t\t").append(GL_AUX_BUFFERS_INTEGER).append("\n");
                    output.append("GL_MAX_TEXTURE_UNITS:\t\t\t").append(GL_MAX_TEXTURE_UNITS_INTEGER).append("\n");
                    output.append("GL_MAX_VERTEX_ATTRIBS:\t\t\t").append(GL_MAX_VERTEX_ATTRIBS_INTEGER).append("\n");
                    output.append("GL_MAX_VERTEX_UNIFORM_COMPONENTS:\t").append(GL_MAX_VERTEX_UNIFORM_COMPONENTS_INTEGER).append("\n");
                    output.append("GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS:\t").append(GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_INTEGER).append("\n");
                    output.append("GL_MAX_VARYING_FLOATS:\t\t\t").append(GL_MAX_VARYING_FLOATS_INTEGER).append("\n");
                    output.append("GL_MAX_TEXTURE_IMAGE_UNITS:\t\t").append(GL_MAX_TEXTURE_IMAGE_UNITS_INTEGER).append("\n");
                    output.append("GL_MAX_TEXTURE_COORDS:\t\t\t").append(GL_MAX_TEXTURE_COORDS_INTEGER).append("\n");
                    output.append("GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS:\t").append(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_INTEGER).append("\n");
                    output.append("GL_MAX_FRAGMENT_UNIFORM_COMPONENTS:\t").append(GL_MAX_FRAGMENT_UNIFORM_COMPONENTS_INTEGER).append("\n");
                    output.append("GL_MAX_3D_TEXTURE_SIZE:\t\t\t").append(GL_MAX_3D_TEXTURE_SIZE_INTEGER).append("\n");
                    if (USE_GL_ARB_TEXTURE_RECTANGLE)
                        output.append("GL_MAX_RECTANGLE_TEXTURE_SIZE_ARB:\t").append(GL_MAX_RECTANGLE_TEXTURE_SIZE_ARB_INTEGER).append("\n");
                    output.append("GL_MAX_TEXTURE_SIZE:\t\t\t").append(GL_MAX_TEXTURE_SIZE_INTEGER).append("\n");
                    if (USE_GL_EXT_FRAMEBUFFER_OBJECT)
                        output.append("GL_MAX_RENDERBUFFER_SIZE_EXT:\t\t").append(GL_MAX_RENDERBUFFER_SIZE_EXT_INTEGER).append("\n");
                    output.append("GL GPU SHADER MODEL 4 SUPPORT:\t\t").append(USE_GL_EXT_GPU_SHADER4 ? "YES" : "NO").append("\n");
                    output.append("GL GPU SHADER MODEL 5 SUPPORT:\t\t").append(USE_GL_ARB_GPU_SHADER5 ? "YES" : "NO").append("\n");
                    output.append("GL GPU SHADER FP64 SUPPORT:\t\t").append(USE_GL_ARB_GPU_SHADER_FP64 ? "YES" : "NO").append("\n\n");
                    println( output.toString() );
                }
            }

            gl.glEnable(GL_VERTEX_PROGRAM_TWO_SIDE);
        }
    }

    /**
    *  Prepares the low quality rendering.
    */
    private void prepareLowQualityRendering(GL2 gl)
    {
        gl.glDisable(GL_MULTISAMPLE);

        gl.glDisable(GL_POINT_SMOOTH);
        gl.glDisable(GL_LINE_SMOOTH);
        gl.glDisable(GL_POLYGON_SMOOTH);

        gl.glHint(GL_POINT_SMOOTH_HINT, GL_FASTEST);
        gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
        gl.glHint(GL_POLYGON_SMOOTH_HINT, GL_FASTEST);
        gl.glHint(GL_FOG_HINT, GL_FASTEST);
        gl.glHint(GL_GENERATE_MIPMAP_HINT, GL_FASTEST);
        if (GL_IS_NVIDIA && USE_SHADERS_PROCESS) gl.glHint(GL_FRAGMENT_SHADER_DERIVATIVE_HINT, GL_FASTEST); // warning, the AMD/ATI driver does not like this setting, and it's only for OpenGL 2.0 and above!
    }

    /**
    *  Initializes the OpenGL clamping status.
    */
    private void initializeClamping(GL2 gl)
    {
        gl.glClampColor(GL_CLAMP_VERTEX_COLOR, GL_FALSE);
        gl.glClampColor(GL_CLAMP_FRAGMENT_COLOR, GL_FALSE);
    }

    /**
    *  Initializes the Frame buffer object.
    */
    private void initializeFBO(GL2 gl)
    {
        //  allocate a framebuffer object
        gl.glGenFramebuffers(1, FBO);
        gl.glBindFramebuffer( GL_FRAMEBUFFER, FBO.get(0) );
    }

    /**
    *  Initializes an orthogonal project.
    */
    private void initializeOrthogonalProjection(GL2 gl)
    {
        // viewport for 1:1 pixel = texture mapping
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0.0f, textureSize, 0.0f, textureSize, -1.0f, 1.0f);
        // same with:
        // glu.gluOrtho2D(0, textureSize, 0, textureSize);
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glViewport(0, 0, textureSize, textureSize); // update the viewport
    }

    /**
    *  Initializes CPU memory.
    */
    private void initializeCPUMemory(GL2 gl)
    {
        try
        {
            initializeCPUMemoryImplementation(gl);
        }
        catch (OutOfMemoryError memErr)
        {
            if (dialogErrorLog)
                JOptionPane.showMessageDialog(this, "Java reported an Out Of Memory error: " + memErr.getMessage() + "!\nPoint of error: initializeCPUMemory()", "Java Out Of Memory Error", JOptionPane.WARNING_MESSAGE);
            if (DEBUG_BUILD) println("Java reported an Out Of Memory error: " + memErr.getMessage() + "!\nPoint of error: initializeCPUMemory()");

            CPUErrorOccured = true;
        }
    }

    /**
    *  Initializes GPU memory.
    */
    private void initializeGPUMemory(GL2 gl)
    {
        initializeGPUMemoryImplementation(gl);
        if (checkGLErrors(gl, dialogErrorLog) != null)
        {
            GPUErrorOccured = true;
        }
    }

    /**
     * Performs the GPU Computing calculations.
     */
    private void performGPUComputingCalculations(GL2 gl)
    {
        performGPUComputingCalculationsImplementation(gl);
        if (checkGLErrors(gl, dialogErrorLog) != null)
        {
            GPUErrorOccured = true;
        }
    }

    /**
    *  Retrieves GPU results.
    */
    private void retrieveGPUResults(GL2 gl)
    {
        try
        {
            retrieveGPUResultsImplementation(gl);
            if (checkGLErrors(gl, dialogErrorLog) != null)
            {
                GPUErrorOccured = true;
            }
        }
        catch (OutOfMemoryError memErr)
        {
            if (dialogErrorLog)
                JOptionPane.showMessageDialog(this, "Java reported an Out Of Memory error: " + memErr.getMessage() + "!\nPoint of error: retrieveGPUResults()", "Java Out Of Memory Error", JOptionPane.WARNING_MESSAGE);
            if (DEBUG_BUILD) println("Java reported an Out Of Memory error: " + memErr.getMessage() + "!\nPoint of error: retrieveGPUResults()");

            CPUErrorOccured = true;
        }
    }

    /**
    *  Deletes the OpenGL context.
    */
    private void deleteOpenGLContext(GL2 gl)
    {
        try
        {
            deleteOpenGLContextForGPUComputing(gl);

            gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);

            // free the framebuffer
            gl.glDeleteFramebuffers(1, FBO);

            if (checkGLErrors(gl, dialogErrorLog) != null)
            {
                GPUErrorOccured = true;
            }
        }
        catch (Exception ex)
        {
            if (DEBUG_BUILD) println("Exception with deleteOpenGLContext(): " + ex.getMessage());
        }
    }

    static String previousErrorReport = "";

    /**
    *  Checks for OpenGL errors.
    *  Extremely useful debugging function: When developing,
    *  make sure to call this after almost every GL call.
    */
    public static String checkGLErrors(GL2 gl, boolean dialog)
    {
        int error = gl.glGetError();
        if (error != GL_NO_ERROR)
        {
            String stringError = glu.gluErrorString(error);

            StackTraceElement[] stes = Thread.currentThread().getStackTrace();
            StackTraceElement ste = stes[2]; // The index here is probably java implementation dependendent

            String fullClassName = ste.getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = ste.getMethodName();
            int lineNumber = ste.getLineNumber();
            final String errorReport = className + "." + methodName + "():" + lineNumber +
                    " OpenGL error '" + stringError + "'";

            if (DEBUG_BUILD)
            {
                println(errorReport);
            }

            // Don't show a dialog if we've just shown the same error
            if (dialog && (previousErrorReport.compareTo(errorReport) != 0))
            {
                EventQueue.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        JOptionPane.showMessageDialog(null, errorReport, "OpenGL Error", JOptionPane.WARNING_MESSAGE);
                    }
                });
            }

            previousErrorReport = errorReport;

            return errorReport;
        }

        return null;
    }

    /**
    *  Sets up a floating point texture with NEAREST filtering.
    *  (mipmaps etc. are unsupported for floating point textures)
    */
    protected void setupTexture(GL2 gl, int textureID)
    {
        setupTexture(gl, textureID, textureSize, 0);
    }

    /**
    *  Sets up a floating point texture with NEAREST filtering.
    *  (mipmaps etc. are unsupported for floating point textures)
    *  Overloaded version of the method above that selects an active texture unit for the render-to-texture.
    */
    protected void setupTexture(GL2 gl, int textureID, int textureUnit)
    {
        setupTexture(gl, textureID, textureSize, textureUnit);
    }

    /**
    *  Sets up a floating point texture with NEAREST filtering.
    *  (mipmaps etc. are unsupported for floating point textures)
    *  Overloaded version of the method above that selects a given texture size & an active texture unit for the render-to-texture.
    */
    protected void setupTexture(GL2 gl, int textureID, int textureSize, int textureUnit)
    {
        // make active and bind
        gl.glActiveTexture(GL_TEXTURE0 + textureUnit);
        gl.glBindTexture(textureParameters.textureTarget, textureID);

        // turn off filtering and wrap modes
        gl.glTexParameteri(textureParameters.textureTarget, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        gl.glTexParameteri(textureParameters.textureTarget, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        gl.glTexParameteri(textureParameters.textureTarget, GL_TEXTURE_WRAP_S, GL_CLAMP);
        gl.glTexParameteri(textureParameters.textureTarget, GL_TEXTURE_WRAP_T, GL_CLAMP);

        // define texture with floating point format
        gl.glTexImage2D(textureParameters.textureTarget, 0, textureParameters.textureInternalFormat, textureSize, textureSize, 0, textureParameters.textureFormat, GL_FLOAT, null);
    }

    /**
    *  Transfers data to texture.
    *  Check web page for detailed explanation on the difference between ATI and NVIDIA.
    */
    protected void transferToTexture(GL2 gl, FloatBuffer data, int textureID)
    {
        transferToTexture(gl, data, textureID, textureSize);
    }

    /**
    *  Transfers data to texture.
    *  Check web page for detailed explanation on the difference between ATI and NVIDIA.
    *  Overloaded version of the method above that selects a given texture size.
    */
    protected void transferToTexture(GL2 gl, FloatBuffer data, int textureID, int textureSize)
    {
        // version (a): HW-accelerated on NVIDIA
        gl.glBindTexture(textureParameters.textureTarget, textureID);
        gl.glTexSubImage2D(textureParameters.textureTarget, 0, 0, 0, textureSize, textureSize, textureParameters.textureFormat, GL_FLOAT, data);

        /*
        // version (b): HW-accelerated on ATI
        gl.glFramebufferTexture2DEXT(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, textureParameters.textureTarget, textureID, 0);
        gl.glDrawBuffer(GL_COLOR_ATTACHMENT0);
        gl.glRasterPos2i(0, 0);
        gl.glDrawPixels(textureSize, textureSize, textureParameters.textureFormat, GL_FLOAT, data);
        gl.glFramebufferTexture2DEXT(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, textureParameters.textureTarget, 0, 0);
        */
    }

    /**
    *  Transfers data from currently texture, and stores it in given array.
    */
    protected void transferFromTexture(GL2 gl, int attachmentPoint, FloatBuffer data)
    {
        // version (a): texture is attached
        // recommended on both NVIDIA and ATI
        gl.glReadBuffer(attachmentPoint);
        gl.glReadPixels(0, 0, textureSize, textureSize, textureParameters.textureFormat, GL_FLOAT, data);

        // version b: texture is not neccessarily attached
        // gl.glBindTexture(textureParameters.textureTarget, attachmentPoint);
        // gl.glGetTexImage(textureParameters.textureTarget, 0, textureParameters.textureFormat, GL_FLOAT, data);
    }

    /**
    *  Renders the OpenGL quad.
    */
    protected void renderQuad(GL2 gl)
    {
        // render multitextured viewport-sized quad
        // depending on the texture target, switch between
        // normalised ([0,1]^2) and unnormalised ([0,w]x[0,h])
        // texture coordinates

	// make quad filled to hit every pixel/texel
	// (should be default but we never know)
	gl.glPolygonMode(GL_FRONT, GL_FILL);

        // and render the quad
        gl.glBegin(GL_QUADS);
	if (textureParameters.textureTarget == GL_TEXTURE_2D)
        {
	    // render with normalized texcoords
	    gl.glTexCoord2f(0.0f, 1.0f);
	    gl.glVertex2f(0.0f, textureSize);
	    gl.glTexCoord2f(1.0f, 1.0f);
	    gl.glVertex2f(textureSize, textureSize);
	    gl.glTexCoord2f(1.0f, 0.0f);
	    gl.glVertex2f(textureSize, 0.0f);
	    gl.glTexCoord2f(0.0f, 0.0f);
	    gl.glVertex2f(0.0f, 0.0f);
	}
        else
        {
	    // render with unnormalized texcoords
	    gl.glTexCoord2f(0.0f, textureSize);
	    gl.glVertex2f(0.0f, textureSize);
	    gl.glTexCoord2f(textureSize, textureSize);
	    gl.glVertex2f(textureSize, textureSize);
	    gl.glTexCoord2f(textureSize, 0.0f);
	    gl.glVertex2f(textureSize, 0.0f);
	    gl.glTexCoord2f(0.0f, 0.0f);
	    gl.glVertex2f(0.0f, 0.0f);
	}
        gl.glEnd();
    }

    /**
    *  Checks the status of the Frame Buffer (FBO) initialization.
    */
    protected boolean checkFrameBufferStatus(GL2 gl)
    {
        int status = gl.glCheckFramebufferStatus(GL_FRAMEBUFFER);
        switch (status)
        {
            case GL_FRAMEBUFFER_COMPLETE:

                if (DEBUG_BUILD) println("Framebuffer initialization is complete.");

                return true;

            case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:

                if (DEBUG_BUILD) println("Framebuffer incomplete, incomplete attachment.");

                GPUErrorOccured = true;

                return false;

            case GL_FRAMEBUFFER_UNSUPPORTED:

                if (DEBUG_BUILD) println("Unsupported framebuffer format.");

                GPUErrorOccured = true;

                return false;

            case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:

                if (DEBUG_BUILD) println("Framebuffer incomplete, missing attachment.");

                GPUErrorOccured = true;

                return false;

            case GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:

                if (DEBUG_BUILD) println("Framebuffer incomplete, attached images must have same dimensions.");

                GPUErrorOccured = true;

                return false;

            case GL_FRAMEBUFFER_INCOMPLETE_FORMATS:

                if (DEBUG_BUILD) println("Framebuffer incomplete, attached images must have same format.");

                GPUErrorOccured = true;

                return false;

            case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:

                if (DEBUG_BUILD) println("Framebuffer incomplete, missing draw buffer.");

                GPUErrorOccured = true;

                return false;

            case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:

                if (DEBUG_BUILD) println("Framebuffer incomplete, missing read buffer.");

                GPUErrorOccured = true;

                return false;

            default:

                if (DEBUG_BUILD) println("Framebuffer unknown error: " + status);

                GPUErrorOccured = true;

                return false;
        }
    }

    /**
    *  Gets any occurences of OpenGL errors.
    */
    public boolean getErrorOccured()
    {
        return (CPUErrorOccured || GPUErrorOccured);
    }


    // Abstract methods from here on

    /**
    *  Called by an implementing subclass for initializing the CPU memory.
    */
    protected abstract void initializeCPUMemoryImplementation(GL2 gl) throws OutOfMemoryError;

    /**
    *  Called by an implementing subclass for initializing the GPU memory.
    */
    protected abstract void initializeGPUMemoryImplementation(GL2 gl);

    /**
    *  Called by an implementing subclass for performing the GPU Computing calculations.
    */
    protected abstract void performGPUComputingCalculationsImplementation(GL2 gl);

    /**
    *  Called by an implementing subclass for retrieving GPU results.
    */
    protected abstract void retrieveGPUResultsImplementation(GL2 gl) throws OutOfMemoryError;

    /**
    *  Called by an implementing subclass for deleting the OpenGL context for GPU computing.
    */
    protected abstract void deleteOpenGLContextForGPUComputing(GL2 gl);
}
