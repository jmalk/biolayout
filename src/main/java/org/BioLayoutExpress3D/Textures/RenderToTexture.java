package org.BioLayoutExpress3D.Textures;

import java.nio.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;
import static javax.media.opengl.GL2.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
*  This class provides fast render-to-texture support using the GL_EXT_framebuffer_object OpenGL extension.
*
* @author Thanos Theo, 2008-2009-2010-2011-2012
* @version 3.0.0.0
*
*/

public final class RenderToTexture
{

    /**
    *  Frame buffer object reference.
    */
    private final IntBuffer FBO = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();

    /**
    *  Texture ID reference.
    */
    private final IntBuffer TEXTURE_ID = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();

    /**
    *  Depth render buffer reference.
    */
    private final IntBuffer DEPTH_RENDER_BUFFER = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();

    /**
    *  Width of the render-to-texture.
    */
    private int width = 0;

    /**
    *  Height of the render-to-texture.
    */
    private int height = 0;

    /**
    *  Shadowmap value of the render-to-texture.
    */
    private boolean hasShadowMap = false;

    /**
    *  Texture format value 1 of the render-to-texture.
    */
    private int textureFormat1 = 0;

    /**
    *  Texture format value 2 of the render-to-texture.
    */
    private int textureFormat2 = 0;

    /**
    *  Depth render buffer value of the render-to-texture.
    */
    private boolean hasDepthRenderBuffer = false;

    /**
    *  Mipmap generation value of the render-to-texture.
    */
    private boolean generateMipmap = false;

    /**
    *  First constructor of the RenderToTexture class.
    */
    public RenderToTexture(GL2 gl)
    {
        this(gl, false, GL_RGBA8, GL_RGBA, false, false);
    }

    /**
    *  Second constructor of the RenderToTexture class.
    */
    public RenderToTexture(GL2 gl, boolean hasDepthRenderBuffer)
    {
        this(gl, false, GL_RGBA8, GL_RGBA, hasDepthRenderBuffer, false);
    }

    /**
    *  Third constructor of the RenderToTexture class.
    */
    public RenderToTexture(GL2 gl, boolean hasShadowMap, boolean hasDepthRenderBuffer)
    {
        this(gl, hasShadowMap, GL_RGBA8, GL_RGBA, hasDepthRenderBuffer, false);
    }

    /**
    *  Fourth constructor of the RenderToTexture class.
    */
    public RenderToTexture(GL2 gl, boolean hasShadowMap, int textureFormat1, int textureFormat2, boolean hasDepthRenderBuffer, boolean generateMipmap)
    {
        this.hasShadowMap = hasShadowMap;
        this.textureFormat1 = textureFormat1;
        this.textureFormat2 = textureFormat2;
        this.hasDepthRenderBuffer = hasDepthRenderBuffer;
        this.generateMipmap = generateMipmap;
    }

    /**
    *  Initializes all render-to-texture resources.
    */
    public void initAllRenderToTextureResources(GL2 gl, int width, int height)
    {
        initAllRenderToTextureResources(gl, 0, width, height);
    }

    /**
    *  Initializes all render-to-texture resources.
    *  Overloaded version of the method above that selects an active texture unit for the render-to-texture.
    */
    public void initAllRenderToTextureResources(GL2 gl, int textureUnit, int width, int height)
    {
        this.width = width;
        this.height = height;

        //  allocate a framebuffer object
        gl.glGenFramebuffers(1, FBO);
        gl.glBindFramebuffer( GL_FRAMEBUFFER, FBO.get(0) );

        if (hasDepthRenderBuffer)
        {
            //  allocate a depth renderbuffer for our depth buffer the same size as our texture
            gl.glGenRenderbuffers(1, DEPTH_RENDER_BUFFER);
            gl.glBindRenderbuffer( GL_RENDERBUFFER, DEPTH_RENDER_BUFFER.get(0) );
            gl.glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
            //  attach the depth renderbuffer to our framebuffer
            gl.glFramebufferRenderbuffer( GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, DEPTH_RENDER_BUFFER.get(0) );
        }

        //  allocate the texture that we will render into
        gl.glGenTextures(1, TEXTURE_ID);
        if (textureUnit == 0)
            bind(gl);
        else
            bind(gl, textureUnit);

        if (hasShadowMap)
            gl.glTexImage2D( GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        else
            gl.glTexImage2D(GL_TEXTURE_2D, 0, textureFormat1, width, height, 0, textureFormat2, GL_UNSIGNED_BYTE, null);

        gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, (generateMipmap) ? GL_LINEAR_MIPMAP_LINEAR : GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        if (generateMipmap)
            gl.glGenerateMipmap(GL_TEXTURE_2D);

        //  attach the framebuffer to our texture, which may be a depth texture
        if (hasShadowMap)
        {
            gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, TEXTURE_ID.get(0), 0);
            //  disable drawing to any buffers, we only want the depth
            gl.glDrawBuffer(GL_NONE);
            gl.glReadBuffer(GL_NONE);
        }
        else
            gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, TEXTURE_ID.get(0), 0);

        if ( checkFrameBufferStatus(gl) )
        {
            if (DEBUG_BUILD) println("checkFrameBufferStatus() reported all ok.");
        }
        else
        {
            if (DEBUG_BUILD) println("checkFrameBufferStatus() reported a Framebuffer error.");
        }

        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
    *  Checks the status of the Frame Buffer (FBO) initialization.
    */
    private boolean checkFrameBufferStatus(GL2 gl)
    {
        int status = gl.glCheckFramebufferStatus(GL_FRAMEBUFFER);
        switch (status)
        {
            case GL_FRAMEBUFFER_COMPLETE:

                if (DEBUG_BUILD) println("Framebuffer initialization is complete.");

                return true;

            case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:

                if (DEBUG_BUILD) println("Framebuffer incomplete, incomplete attachment.");

                return false;

            case GL_FRAMEBUFFER_UNSUPPORTED:

                if (DEBUG_BUILD) println("Unsupported framebuffer format.");

                return false;

            case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:

                if (DEBUG_BUILD) println("Framebuffer incomplete, missing attachment.");

                return false;

            case GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:

                if (DEBUG_BUILD) println("Framebuffer incomplete, attached images must have same dimensions.");

                return false;

            case GL_FRAMEBUFFER_INCOMPLETE_FORMATS:

                if (DEBUG_BUILD) println("Framebuffer incomplete, attached images must have same format.");

                return false;

            case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:

                if (DEBUG_BUILD) println("Framebuffer incomplete, missing draw buffer.");

                return false;

            case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:

                if (DEBUG_BUILD) println("Framebuffer incomplete, missing read buffer.");

                return false;

            default:

                if (DEBUG_BUILD) println("Framebuffer unknown error: " + status);

                return false;
        }
    }

    /**
    *  Binds the framebuffer & sets the viewport to the given texture dimensions (uses glPushAttrib).
    */
    public void startRender(GL2 gl)
    {
        gl.glBindFramebuffer( GL_FRAMEBUFFER, FBO.get(0) );
        gl.glPushAttrib(GL_VIEWPORT_BIT);
        gl.glViewport(0, 0, width, height);
    }

    /**
    *  Unbinds the framebuffer & returns to default state.
    *  Always restore the viewport when ready to render to the screen (uses glPopAttrib).
    */
    public void finishRender(GL2 gl)
    {
        gl.glPopAttrib();
        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
    *  Enable the render-to-texture.
    */
    public void enable(GL2 gl)
    {
        gl.glEnable(GL_TEXTURE_2D);
    }

    /**
    *  Disable the render-to-texture.
    */
    public void disable(GL2 gl)
    {
        gl.glDisable(GL_TEXTURE_2D);
    }

    /**
    *  Binds the render-to-texture.
    */
    public void bind(GL2 gl)
    {
        gl.glBindTexture( GL_TEXTURE_2D, TEXTURE_ID.get(0) );
    }

    /**
    *  Binds the render-to-texture with a given active texture unit.
    *  Overloaded version of the method above that selects an active texture unit for the render-to-texture.
    */
    public void bind(GL2 gl, int textureUnit)
    {
        gl.glActiveTexture(GL_TEXTURE0 + textureUnit);
        gl.glBindTexture( GL_TEXTURE_2D, TEXTURE_ID.get(0) );
    }

    /**
    *  Prepares the high quality rendering.
    */
    public void prepareHighQualityRendering(GL2 gl)
    {
        gl.glEnable(GL_MULTISAMPLE);

        gl.glEnable(GL_POINT_SMOOTH);
        gl.glEnable(GL_LINE_SMOOTH);
        gl.glEnable(GL_POLYGON_SMOOTH);

        gl.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        gl.glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        gl.glHint(GL_FOG_HINT, GL_NICEST);
        gl.glHint(GL_GENERATE_MIPMAP_HINT, GL_NICEST);
        if (GL_IS_NVIDIA && USE_SHADERS_PROCESS) gl.glHint(GL_FRAGMENT_SHADER_DERIVATIVE_HINT, GL_NICEST); // warning, the AMD/ATI driver does not like this setting, and it's only for OpenGL 2.0 and above!
    }

    /**
    *  Prepares the low quality rendering.
    */
    public void prepareLowQualityRendering(GL2 gl)
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
    *  Returns the render-to-texture width.
    */
    public int getWidth()
    {
        return width;
    }

    /**
    *  Returns the render-to-texture height.
    */
    public int getHeight()
    {
        return height;
    }

    /**
    *  Disposes all render-to-texture resources.
    */
    public void disposeAllRenderToTextureResources(GL2 gl)
    {
        //  free the framebuffer
        gl.glDeleteFramebuffers(1, FBO);

        //  free the render-to-texture texture
        gl.glDeleteTextures(1, TEXTURE_ID);

        //  free the depth renderbuffer
        if (hasDepthRenderBuffer) gl.glDeleteRenderbuffers(1, DEPTH_RENDER_BUFFER);
    }


}
