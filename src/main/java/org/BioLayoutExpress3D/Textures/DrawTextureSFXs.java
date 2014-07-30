package org.BioLayoutExpress3D.Textures;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.nio.*;
import java.util.*;
import static java.lang.Math.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import com.jogamp.common.nio.Buffers;
import static javax.media.opengl.GL2.*;
import com.jogamp.opengl.util.gl2.GLUT;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;
import org.BioLayoutExpress3D.GPUComputing.OpenGLContext.*;

/**
*
*  This class includes drawString2DTexture(), drawTexture() & drawRotoZoomTexture() wrapper static methods.
*
* @see org.BioLayoutExpress3D.StaticLibraries.ImageProducer
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class DrawTextureSFXs
{

    /**
    *  Variable to be used for OpenGL Vertex Arrays support.
    */
    // private static final Buffer INDICES_BUFFER = Buffers.newDirectByteBuffer(4).put( new byte[] { 0, 1, 2, 3 } ).rewind();

    /**
    *  Variable to be used for OpenGL Vertex Arrays support.
    */
    // private static final DoubleBuffer ALL_TEXTURE_2D_COORDS_BUFFER = Buffers.newDirectDoubleBuffer(8);

    /**
    *  Variable to be used for OpenGL Vertex Arrays support.
    */
    // private static final DoubleBuffer ALL_VERTEX_2D_COORDS_BUFFER = Buffers.newDirectDoubleBuffer(8);

    /**
    *  Variable to be used for OpenGL Vertex Arrays support.
    */
    private static final FloatBuffer INTERLEAVED_ARRAY_COORDS_BUFFER = Buffers.newDirectFloatBuffer(2 * 8 + 4); // add 4 dummy values for GL_T2F_V3F V3F part

    /**
    *  Draws a string 2D texture from a textRenderer. In case of a null supplied textRenderer, the method draws
    *  a white box with red text (?!?) in it.
    */
    public static void drawString2DTexture(GL2 gl, int height, TextRenderer textRenderer, String str, int x, int y)
    {
        if (textRenderer == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawString2DTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            textRenderer.draw(str, x, height - y);
    }

    /**
    *  Draws a string 2D texture from a textRenderer. In case of a null supplied textRenderer, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to choose a color.
    */
    public static void drawString2DTexture(GL2 gl, int height, TextRenderer textRenderer, String str, int x, int y, Color color)
    {
        if (textRenderer == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawString2DTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
        {
            textRenderer.setColor(color);
            textRenderer.draw(str, x, height - y);
        }
    }

    /**
    *  Draws a string 2D texture from a textRenderer. In case of a null supplied textRenderer, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to choose a color & an alpha value.
    */
    public static void drawString2DTexture(GL2 gl, int height, TextRenderer textRenderer, String str, int x, int y, Color color, float alpha)
    {
        if (textRenderer == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawString2DTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
        {
            float r = alpha;
            float g = alpha;
            float b = alpha;

            if (color != null)
            {
                r = color.getRed()   / 255.0f;
                g = color.getGreen() / 255.0f;
                b = color.getBlue()  / 255.0f;
            }

            textRenderer.setColor(r, g, b, alpha);
            textRenderer.draw(str, x, height - y);
        }
    }

    /**
    *  Draws an OpenGL string as bitmap to a specified (x, y) coord.
    */
    public static void drawOpenGLBitmapString(GL2 gl, int GLFont, String label, double x, double y)
    {
        drawOpenGLBitmapString(gl, GLFont, label, x, y, 1.0f, null);
    }

    /**
    *  Draws an OpenGL string as bitmap to a specified (x, y) coord. Overloaded version to choose an alpha value.
    */
    public static void drawOpenGLBitmapString(GL2 gl, int GLFont, String label, double x, double y, float alpha)
    {
        drawOpenGLBitmapString(gl, GLFont, label, x, y, alpha, null);
    }

    /**
    *  Draws an OpenGL string as bitmap to a specified (x, y) coord. Overloaded version to choose a color.
    */
    public static void drawOpenGLBitmapString(GL2 gl, int GLFont, String label, double x, double y, Color color)
    {
        drawOpenGLBitmapString(gl, GLFont, label, x, y, 1.0f, color);
    }

    /**
    *  Draws an OpenGL string as bitmap to a specified (x, y) coord. Overloaded version to choose a color & an alpha value.
    */
    public static void drawOpenGLBitmapString(GL2 gl, int GLFont, String label, double x, double y, float alpha, Color color)
    {
        if (label == null)
        {
            if (DEBUG_BUILD) println("drawOpenGLString(): Label is null; setting to '' (empty String) to DrawTextureSFXs!");
            label = "";
        }

        if (alpha < 0.0f)
        {
            if (DEBUG_BUILD) println("drawOpenGLString(): Alpha must be >= 0.0f; setting to 0.0f to DrawTextureSFXs!");
            alpha = 0.0f;
        }
        else if (alpha > 1.0f)
        {
            if (DEBUG_BUILD) println("drawOpenGLString(): Alpha must be <= 1.0f; setting to 1.0f to DrawTextureSFXs!");
            alpha = 1.0f;
        }

        float r = alpha;
        float g = alpha;
        float b = alpha;

        if (color != null)
        {
            r = color.getRed()   / 255.0f;
            g = color.getGreen() / 255.0f;
            b = color.getBlue()  / 255.0f;
        }

        GLUT glut = new GLUT();

        // Enable blending, using the SrcOver rule
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // determine which areas of the polygon are to be renderered
        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0

        gl.glColor4f(r, g, b, alpha);
        gl.glRasterPos2d(x, y);
        glut.glutBitmapString(GLFont, label);

        gl.glDisable(GL_ALPHA_TEST);
        gl.glDisable(GL_BLEND);
    }

    /**
    *  Draws a line from a specified (x1, y1) coord to a specified (x2, y2) coord.
    *  Also optionally accepts to use individual OpenGL commands for line drawing.
    */
    public static void drawLine(GL2 gl, double x1, double y1, double x2, double y2)
    {
        drawLine(gl, x1, y1, x2, y2, 1.0f, null, true);
    }

    /**
    *  Draws a line from a specified (x1, y1) coord to a specified (x2, y2) coord.
    *  Overloaded version to also choose a line width to mix the line.
    */
    public static void drawLine(GL2 gl, double x1, double y1, double x2, double y2, float lineWidth)
    {
        drawLine(gl, x1, y1, x2, y2, lineWidth, null, true);
    }

    /**
    *  Draws a line from a specified (x1, y1) coord to a specified (x2, y2) coord.
    *  Overloaded version to also choose a line width & a color to mix the line.
    */
    public static void drawLine(GL2 gl, double x1, double y1, double x2, double y2, float lineWidth, Color color)
    {
        drawLine(gl, x1, y1, x2, y2, lineWidth, color, true);
    }

    /**
    *  Draws a line from a specified (x1, y1) coord to a specified (x2, y2) coord.
    *  Overloaded version to also choose a line width & an alpha value to mix the line.
    */
    public static void drawLine(GL2 gl, double x1, double y1, double x2, double y2, float lineWidth, float alpha)
    {
        drawLine(gl, x1, y1, x2, y2, lineWidth, alpha, null, true);
    }

    /**
    *  Draws a line from a specified (x1, y1) coord to a specified (x2, y2) coord.
    *  Overloaded version to also choose a line width, an alpha value & a color to mix the line.
    */
    public static void drawLine(GL2 gl, double x1, double y1, double x2, double y2, float lineWidth, float alpha, Color color)
    {
        drawLine(gl, x1, y1, x2, y2, lineWidth, alpha, color, true);
    }

    /**
    *  Draws a line from a specified (x1, y1) coord to a specified (x2, y2) coord.
    *  Also optionally accepts to use individual OpenGL commands for line drawing.
    */
    public static void drawLine(GL2 gl, double x1, double y1, double x2, double y2, boolean useIndividualGLCommands)
    {
        drawLine(gl, x1, y1, x2, y2, 1.0f, null, useIndividualGLCommands);
    }

    /**
    *  Draws a line from a specified (x1, y1) coord to a specified (x2, y2) coord.
    *  Overloaded version to also choose a line width to mix the line.
    *  Also optionally accepts to use individual OpenGL commands for line drawing.
    */
    public static void drawLine(GL2 gl, double x1, double y1, double x2, double y2, float lineWidth, boolean useIndividualGLCommands)
    {
        drawLine(gl, x1, y1, x2, y2, lineWidth, null, useIndividualGLCommands);
    }

    /**
    *  Draws a line from a specified (x1, y1) coord to a specified (x2, y2) coord.
    *  Overloaded version to also choose a line width & a color to mix the line.
    *  Also optionally accepts to use individual OpenGL commands for line drawing.
    */
    public static void drawLine(GL2 gl, double x1, double y1, double x2, double y2, float lineWidth, Color color, boolean useIndividualGLCommands)
    {
        if (lineWidth <= 0.0f)
        {
            if (DEBUG_BUILD) println("drawLine(): LineWidth must be >= 0.0f; setting to 0.0f to DrawTextureSFXs!");
            lineWidth = 0.001f;
        }

        float r = 1.0f;
        float g = 1.0f;
        float b = 1.0f;

        if (color != null)
        {
            r = color.getRed()   / 255.0f;
            g = color.getGreen() / 255.0f;
            b = color.getBlue()  / 255.0f;
        }

        if (useIndividualGLCommands)
        {
            gl.glLineWidth(lineWidth);
            gl.glBegin(GL_LINES);
        }
        gl.glColor4f(r, g, b, 1.0f);
        gl.glVertex2d(x1, y1);
        gl.glVertex2d(x2, y2);
        if (useIndividualGLCommands)
            gl.glEnd();
    }

    /**
    *  Draws all lines from a specified ArrayList<Point2D.Double> collection.
    */
    public static void drawLines(GL2 gl, ArrayList<Point2D.Double> allPoints, float lineWidth, Color color, boolean useIndividualGLCommands)
    {
        if (lineWidth <= 0.0f)
        {
            if (DEBUG_BUILD) println("drawLine(): LineWidth must be >= 0.0f; setting to 0.0f to DrawTextureSFXs!");
            lineWidth = 0.001f;
        }

        float r = 1.0f;
        float g = 1.0f;
        float b = 1.0f;

        if (color != null)
        {
            r = color.getRed()   / 255.0f;
            g = color.getGreen() / 255.0f;
            b = color.getBlue()  / 255.0f;
        }

        if (useIndividualGLCommands)
        {
            gl.glLineWidth(lineWidth);
            gl.glBegin(GL_LINES);
        }
        gl.glColor4f(r, g, b, 1.0f);
        Point.Double point = null;
        for (int i = 0; i < allPoints.size(); i++)
        {
            point = allPoints.get(i);
            gl.glVertex2d(point.x, point.y);
        }
        if (useIndividualGLCommands)
            gl.glEnd();
    }

    /**
    *  Draws a line from a specified (x1, y1) coord to a specified (x2, y2) coord.
    *  Overloaded version to also choose a line width & an alpha value to mix the texture.
    *  Also optionally accepts to use individual OpenGL commands for line drawing.
    */
    public static void drawLine(GL2 gl, double x1, double y1, double x2, double y2, float lineWidth, float alpha, boolean useIndividualGLCommands)
    {
        drawLine(gl, x1, y1, x2, y2, lineWidth, alpha, null, useIndividualGLCommands);
    }

    /**
    *  Draws a line from a specified (x1, y1) coord to a specified (x2, y2) coord.
    *  Overloaded version to also choose a line width, an alpha value & a color to mix the texture.
    *  Also optionally accepts to use individual OpenGL commands for line drawing.
    */
    public static void drawLine(GL2 gl, double x1, double y1, double x2, double y2, float lineWidth, float alpha, Color color, boolean useIndividualGLCommands)
    {
        if (lineWidth <= 0.0f)
        {
            if (DEBUG_BUILD) println("drawLine(): LineWidth must be >= 0.0f; setting to 0.0f to DrawTextureSFXs!");
            lineWidth = 0.001f;
        }

        if (alpha < 0.0f)
        {
            if (DEBUG_BUILD) println("drawLine(): Alpha must be >= 0.0f; setting to 0.0f to DrawTextureSFXs!");
            alpha = 0.0f;
        }
        else if (alpha > 1.0f)
        {
            if (DEBUG_BUILD) println("drawLine(): Alpha must be <= 1.0f; setting to 1.0f to DrawTextureSFXs!");
            alpha = 1.0f;
        }

        float r = alpha;
        float g = alpha;
        float b = alpha;

        if (color != null)
        {
            r = color.getRed()   / 255.0f;
            g = color.getGreen() / 255.0f;
            b = color.getBlue()  / 255.0f;
        }

        // Enable blending, using the SrcOver rule
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // determine which areas of the polygon are to be renderered
        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0

        if (useIndividualGLCommands)
        {
            gl.glLineWidth(lineWidth);
            gl.glBegin(GL_LINES);
        }
        gl.glColor4f(r, g, b, alpha);
        gl.glVertex2d(x1, y1);
        gl.glVertex2d(x2, y2);
        if (useIndividualGLCommands)
            gl.glEnd();

        gl.glDisable(GL_ALPHA_TEST);
        gl.glDisable(GL_BLEND);
    }

    /**
    *  Draws a quad in a specified point with given width/height.
    */
    public static void drawQuad(GL2 gl, double x, double y, double width, double height)
    {
        // Enable blending, using the SrcOver rule
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // determine which areas of the polygon are to be renderered
        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0

        if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
        {
            // Using Vertex Arrays for passing the data to OpenGL
            // ALL_VERTEX_2D_COORDS_BUFFER.put( new double[] { x, y + height, x + width, y + height, x + width, y, x, y } ).rewind();
            INTERLEAVED_ARRAY_COORDS_BUFFER.put( new float[] { (float)(x),         (float)(y + height),
                                                               (float)(x + width), (float)(y + height),
                                                               (float)(x + width), (float)(y),
                                                               (float)(x),         (float)(y),          } ).rewind();

            // gl.glVertexPointer(2, GL_DOUBLE, 0, ALL_VERTEX_2D_COORDS_BUFFER);
            gl.glInterleavedArrays(GL_V2F, 0, INTERLEAVED_ARRAY_COORDS_BUFFER);
            gl.glDrawArrays(GL_QUADS, 0, 4);
            // gl.glDrawElements(GL_QUADS, 4, GL_UNSIGNED_BYTE, INDICES_BUFFER);
        }
        else
        {
            gl.glBegin(GL_QUADS);
            gl.glVertex2d(x,         y + height); // Bottom Left Of The Quad
            gl.glVertex2d(x + width, y + height); // Bottom Right Of The Quad
            gl.glVertex2d(x + width, y);          // Top Right Of The Quad
            gl.glVertex2d(x,         y);          // Top Left Of The Quad
            gl.glEnd();
         }

        gl.glDisable(GL_ALPHA_TEST);
        gl.glDisable(GL_BLEND);
    }

    /**
    *  Draws a quad in a specified point with given width/height with texture coordinates.
    */
    public static void drawQuadWithTextureCoords(GL2 gl, double x, double y, double width, double height)
    {
        // Enable blending, using the SrcOver rule
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // determine which areas of the polygon are to be renderered
        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0

        if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
        {
            // Using Vertex Arrays for passing the data to OpenGL
            // ALL_TEXTURE_2D_COORDS_BUFFER.put( new double[] { tx1, ty2, tx2, ty2, tx2, ty1, tx1, ty1 } ).rewind();
            // ALL_VERTEX_2D_COORDS_BUFFER.put( new double[] { x, y + height, x + width, y + height, x + width, y, x, y } ).rewind();
            INTERLEAVED_ARRAY_COORDS_BUFFER.put( new float[] { 0.0f, 0.0f, (float)(x),         (float)(y + height), 0,
                                                               1.0f, 0.0f, (float)(x + width), (float)(y + height), 0,
                                                               1.0f, 1.0f, (float)(x + width), (float)(y),          0,
                                                               0.0f, 1.0f, (float)(x),         (float)(y),          0 } ).rewind();

            // gl.glTexCoordPointer(2, GL_DOUBLE, 0, ALL_TEXTURE_2D_COORDS_BUFFER);
            // gl.glVertexPointer(2, GL_DOUBLE, 0, ALL_VERTEX_2D_COORDS_BUFFER);
            gl.glInterleavedArrays(GL_T2F_V3F, 0, INTERLEAVED_ARRAY_COORDS_BUFFER);
            gl.glDrawArrays(GL_QUADS, 0, 4);
            // gl.glDrawElements(GL_QUADS, 4, GL_UNSIGNED_BYTE, INDICES_BUFFER);
        }
        else
        {
            gl.glBegin(GL_QUADS);
            gl.glTexCoord2d(0.0, 0.0); gl.glVertex2d(x,         y + height); // Bottom Left Of The Quad
            gl.glTexCoord2d(1.0, 0.0); gl.glVertex2d(x + width, y + height); // Bottom Right Of The Quad
            gl.glTexCoord2d(1.0, 1.0); gl.glVertex2d(x + width, y);          // Top Right Of The Quad
            gl.glTexCoord2d(0.0, 1.0); gl.glVertex2d(x,         y);          // Top Left Of The Quad
            gl.glEnd();
        }

        gl.glDisable(GL_ALPHA_TEST);
        gl.glDisable(GL_BLEND);
    }

    /**
    *  Draws a buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it.
    */
    public static void drawImageTexture(GL2 gl, BufferedImage image, double x, double y)
    {
        if (image == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawImageTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            drawImageTexture(gl, image, x, y, image.getWidth(), image.getHeight(), 1.0f, null, true);
    }

    /**
    *  Draws a buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value.
    */
    public static void drawImageTexture(GL2 gl, BufferedImage image, double x, double y, float alpha)
    {
        if (image == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawImageTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            drawImageTexture(gl, image, x, y, image.getWidth(), image.getHeight(), alpha, null, true);
    }

    /**
    *  Draws a buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose a color to mix the texture.
    */
    public static void drawImageTexture(GL2 gl, BufferedImage image, double x, double y, Color color)
    {
        if (image == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawImageTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            drawImageTexture(gl, image, x, y, image.getWidth(), image.getHeight(), 1.0f, color, true);
    }

    /**
    *  Draws a buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value & a color to mix the texture.
    */
    public static void drawImageTexture(GL2 gl, BufferedImage image, double x, double y, float alpha, Color color)
    {
        if (image == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawImageTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            drawImageTexture(gl, image, x, y, image.getWidth(), image.getHeight(), alpha, color, true);
    }

    /**
    *  Draws a buffered image converted to texture in a specified rectangle width/height. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it.
    */
    public static void drawImageTexture(GL2 gl, BufferedImage image, double x, double y, double width, double height)
    {
        drawImageTexture(gl, image, x, y, width, height, 1.0f, null, true);
    }

    /**
    *  Draws a buffered image converted to texture in a specified rectangle width/height. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value.
    */
    public static void drawImageTexture(GL2 gl, BufferedImage image, double x, double y, double width, double height, float alpha)
    {
        drawImageTexture(gl, image, x, y, width, height, alpha, null, true);
    }

    /**
    *  Draws a buffered image converted to texture in a specified rectangle width/height. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose a color to mix the texture.
    */
    public static void drawImageTexture(GL2 gl, BufferedImage image, double x, double y, double width, double height, Color color)
    {
        drawImageTexture(gl, image, x, y, width, height, 1.0f, color, true);
    }

    /**
    *  Draws a buffered image converted to texture in a specified rectangle width/height. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value & a color to mix the texture.
    */
    public static void drawImageTexture(GL2 gl, BufferedImage image, double x, double y, double width, double height, float alpha, Color color)
    {
        if (image == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawImageTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
        {
            Texture imageTexture = AWTTextureIO.newTexture(GLProfile.getDefault(), image, false);
            drawTexture(gl, imageTexture, x, y, width, height, alpha, color, true);
        }
    }

    /**
    *  Draws a buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawImageTexture(GL2 gl, BufferedImage image, double x, double y, boolean bindEnableAndDisableTexture)
    {
        if (image == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawImageTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            drawImageTexture(gl, image, x, y, image.getWidth(), image.getHeight(), 1.0f, null, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawImageTexture(GL2 gl, BufferedImage image, double x, double y, float alpha, boolean bindEnableAndDisableTexture)
    {
        if (image == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawImageTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            drawImageTexture(gl, image, x, y, image.getWidth(), image.getHeight(), alpha, null, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawImageTexture(GL2 gl, BufferedImage image, double x, double y, Color color, boolean bindEnableAndDisableTexture)
    {
        if (image == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawImageTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            drawImageTexture(gl, image, x, y, image.getWidth(), image.getHeight(), 1.0f, color, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value & a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawImageTexture(GL2 gl, BufferedImage image, double x, double y, float alpha, Color color, boolean bindEnableAndDisableTexture)
    {
        if (image == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawImageTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            drawImageTexture(gl, image, x, y, image.getWidth(), image.getHeight(), alpha, color, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a buffered image converted to texture in a specified rectangle width/height. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawImageTexture(GL2 gl, BufferedImage image, double x, double y, double width, double height, boolean bindEnableAndDisableTexture)
    {
        drawImageTexture(gl, image, x, y, width, height, 1.0f, null, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a buffered image converted to texture in a specified rectangle width/height. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawImageTexture(GL2 gl, BufferedImage image, double x, double y, double width, double height, float alpha, boolean bindEnableAndDisableTexture)
    {
        drawImageTexture(gl, image, x, y, width, height, alpha, null, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a buffered image converted to texture in a specified rectangle width/height. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawImageTexture(GL2 gl, BufferedImage image, double x, double y, double width, double height, Color color, boolean bindEnableAndDisableTexture)
    {
        drawImageTexture(gl, image, x, y, width, height, 1.0f, color, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a buffered image converted to texture in a specified rectangle width/height. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value & a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawImageTexture(GL2 gl, BufferedImage image, double x, double y, double width, double height, float alpha, Color color, boolean bindEnableAndDisableTexture)
    {
        if (image == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawImageTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
        {
            Texture imageTexture = AWTTextureIO.newTexture(GLProfile.getDefault(), image, false);
            drawTexture(gl, imageTexture, x, y, width, height, alpha, color, bindEnableAndDisableTexture);
        }
    }

    /**
    *  Draws an image texture. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it.
    */
    public static void drawTexture(GL2 gl, Texture imageTexture, double x, double y)
    {
        if (imageTexture == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            drawTexture(gl, imageTexture, x, y, imageTexture.getImageWidth(), imageTexture.getImageHeight(), 1.0f, null, true);
    }

    /**
    *  Draws an image texture. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value.
    */
    public static void drawTexture(GL2 gl, Texture imageTexture, double x, double y, float alpha)
    {
        if (imageTexture == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            drawTexture(gl, imageTexture, x, y, imageTexture.getImageWidth(), imageTexture.getImageHeight(), alpha, null, true);
    }

    /**
    *  Draws an image texture. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose a color to mix the texture.
    */
    public static void drawTexture(GL2 gl, Texture imageTexture, double x, double y, Color color)
    {
        if (imageTexture == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            drawTexture(gl, imageTexture, x, y, imageTexture.getImageWidth(), imageTexture.getImageHeight(), 1.0f, color, true);
    }

    /**
    *  Draws an image texture. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value & a color to mix the texture.
    */
    public static void drawTexture(GL2 gl, Texture imageTexture, double x, double y, float alpha, Color color)
    {
        if (imageTexture == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            drawTexture(gl, imageTexture, x, y, imageTexture.getImageWidth(), imageTexture.getImageHeight(), alpha, color, true);
    }

    /**
    *  Draws an image texture in a specified rectangle width/height. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it.
    */
    public static void drawTexture(GL2 gl, Texture imageTexture, double x, double y, double width, double height)
    {
        drawTexture(gl, imageTexture, x, y, width, height, 1.0f, null, true);
    }

    /**
    *  Draws an image texture in a specified rectangle width/height. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value.
    */
    public static void drawTexture(GL2 gl, Texture imageTexture, double x, double y, double width, double height, float alpha)
    {
        drawTexture(gl, imageTexture, x, y, width, height, alpha, null, true);
    }

    /**
    *  Draws an image texture in a specified rectangle width/height. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose a color to mix the texture.
    */
    public static void drawTexture(GL2 gl, Texture imageTexture, double x, double y, double width, double height, Color color)
    {
        drawTexture(gl, imageTexture, x, y, width, height, 1.0f, color, true);
    }

    /**
    *  Draws an image texture in a specified rectangle width/height. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value & a color to mix the texture.
    */
    public static void drawTexture(GL2 gl, Texture imageTexture, double x, double y, double width, double height, float alpha, Color color)
    {
        drawTexture(gl, imageTexture, x, y, width, height, alpha, color, true);
    }

    /**
    *  Draws an image texture. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawTexture(GL2 gl, Texture imageTexture, double x, double y, boolean bindEnableAndDisableTexture)
    {
        if (imageTexture == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            drawTexture(gl, imageTexture, x, y, imageTexture.getImageWidth(), imageTexture.getImageHeight(), 1.0f, null, bindEnableAndDisableTexture);
    }

    /**
    *  Draws an image texture. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawTexture(GL2 gl, Texture imageTexture, double x, double y, float alpha, boolean bindEnableAndDisableTexture)
    {
        if (imageTexture == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            drawTexture(gl, imageTexture, x, y, imageTexture.getImageWidth(), imageTexture.getImageHeight(), alpha, null, bindEnableAndDisableTexture);
    }

    /**
    *  Draws an image texture. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawTexture(GL2 gl, Texture imageTexture, double x, double y, Color color, boolean bindEnableAndDisableTexture)
    {
        if (imageTexture == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            drawTexture(gl, imageTexture, x, y, imageTexture.getImageWidth(), imageTexture.getImageHeight(), 1.0f, color, bindEnableAndDisableTexture);
    }

    /**
    *  Draws an image texture. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value & a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawTexture(GL2 gl, Texture imageTexture, double x, double y, float alpha, Color color, boolean bindEnableAndDisableTexture)
    {
        if (imageTexture == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
            drawTexture(gl, imageTexture, x, y, imageTexture.getImageWidth(), imageTexture.getImageHeight(), alpha, color, bindEnableAndDisableTexture);
    }

    /**
    *  Draws an image texture in a specified rectangle width/height. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawTexture(GL2 gl, Texture imageTexture, double x, double y, double width, double height, boolean bindEnableAndDisableTexture)
    {
        drawTexture(gl, imageTexture, x, y, width, height, 1.0f, null, bindEnableAndDisableTexture);
    }

    /**
    *  Draws an image texture in a specified rectangle width/height. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawTexture(GL2 gl, Texture imageTexture, double x, double y, double width, double height, float alpha, boolean bindEnableAndDisableTexture)
    {
        drawTexture(gl, imageTexture, x, y, width, height, alpha, null, bindEnableAndDisableTexture);
    }

    /**
    *  Draws an image texture in a specified rectangle width/height. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawTexture(GL2 gl, Texture imageTexture, double x, double y, double width, double height, Color color, boolean bindEnableAndDisableTexture)
    {
        drawTexture(gl, imageTexture, x, y, width, height, 1.0f, color, bindEnableAndDisableTexture);
    }

    /**
    *  Draws an image texture in a specified rectangle width/height. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value & a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawTexture(GL2 gl, Texture imageTexture, double x, double y, double width, double height, float alpha, Color color, boolean bindEnableAndDisableTexture)
    {
        if (imageTexture == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
        {
            if (alpha < 0.0f)
            {
                if (DEBUG_BUILD) println("drawTexture(): Alpha must be >= 0.0f; setting to 0.0f to DrawTextureSFXs!");
                alpha = 0.0f;
            }
            else if (alpha > 1.0f)
            {
                if (DEBUG_BUILD) println("drawTexture(): Alpha must be <= 1.0f; setting to 1.0f to DrawTextureSFXs!");
                alpha = 1.0f;
            }

            float r = alpha;
            float g = alpha;
            float b = alpha;

            if (color != null)
            {
                r = color.getRed()   / 255.0f;
                g = color.getGreen() / 255.0f;
                b = color.getBlue()  / 255.0f;
            }

            TextureCoords tc = imageTexture.getImageTexCoords();
            float tx1 = tc.left();
            float ty1 = tc.top();
            float tx2 = tc.right();
            float ty2 = tc.bottom();

            // Enable blending, using the SrcOver rule
            gl.glEnable(GL_BLEND);
            gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

            // determine which areas of the polygon are to be renderered
            gl.glEnable(GL_ALPHA_TEST);
            gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0

            // Use the GL_MODULATE texture function to effectively multiply
            // each pixel in the texture by the current alpha value
            gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

            if (bindEnableAndDisableTexture)
            {
                imageTexture.bind(gl);
                imageTexture.enable(gl);
            }

            if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
            {
                // Using Vertex Arrays for passing the data to OpenGL
                // ALL_TEXTURE_2D_COORDS_BUFFER.put( new double[] { tx1, ty2, tx2, ty2, tx2, ty1, tx1, ty1 } ).rewind();
                // ALL_VERTEX_2D_COORDS_BUFFER.put( new double[] { x, y + height, x + width, y + height, x + width, y, x, y } ).rewind();
                INTERLEAVED_ARRAY_COORDS_BUFFER.put( new float[] { tx1, ty2, (float)(x),         (float)(y + height), 0,
                                                                   tx2, ty2, (float)(x + width), (float)(y + height), 0,
                                                                   tx2, ty1, (float)(x + width), (float)(y),          0,
                                                                   tx1, ty1, (float)(x),         (float)(y),          0 } ).rewind();

                gl.glColor4f(r, g, b, alpha);
                // gl.glTexCoordPointer(2, GL_DOUBLE, 0, ALL_TEXTURE_2D_COORDS_BUFFER);
                // gl.glVertexPointer(2, GL_DOUBLE, 0, ALL_VERTEX_2D_COORDS_BUFFER);
                gl.glInterleavedArrays(GL_T2F_V3F, 0, INTERLEAVED_ARRAY_COORDS_BUFFER);
                gl.glDrawArrays(GL_QUADS, 0, 4);
                // gl.glDrawElements(GL_QUADS, 4, GL_UNSIGNED_BYTE, INDICES_BUFFER);
            }
            else
            {
                gl.glBegin(GL_QUADS);
                gl.glColor4f(r, g, b, alpha);
                gl.glTexCoord2d(tx1, ty2); gl.glVertex2d(x,         y + height); // Bottom Left Of The Texture and Quad
                gl.glTexCoord2d(tx2, ty2); gl.glVertex2d(x + width, y + height); // Bottom Right Of The Texture and Quad
                gl.glTexCoord2d(tx2, ty1); gl.glVertex2d(x + width, y);          // Top Right Of The Texture and Quad
                gl.glTexCoord2d(tx1, ty1); gl.glVertex2d(x,         y);          // Top Left Of The Texture and Quad
                gl.glEnd();
             }

            if (bindEnableAndDisableTexture)
                imageTexture.disable(gl);

            gl.glDisable(GL_ALPHA_TEST);
            gl.glDisable(GL_BLEND);
        }
    }

    /**
    *  Draws a rotated & zoomed quad in a specified point with given width/height.
    */
    public static void drawRotoZoomQuad(GL2 gl, double x, double y, double width, double height, double theta, double zoomFactorX, double zoomFactorY)
    {
        // Enable blending, using the SrcOver rule
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // determine which areas of the polygon are to be renderered
        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0

        gl.glPushMatrix();
        // center all the transformations to image's center
        gl.glTranslated(x + width / 2.0, y + height / 2.0, 0.0);
        gl.glScaled(zoomFactorX, zoomFactorY, 0.0);
        gl.glRotated(theta, 0.0, 0.0, 1.0);
        // reset centered transformation
        gl.glTranslated(-(x + width / 2.0), -(y + height / 2.0), 0.0);

        if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
        {
            // Using Vertex Arrays for passing the data to OpenGL
            // ALL_VERTEX_2D_COORDS_BUFFER.put( new double[] { x, y + height, x + width, y + height, x + width, y, x, y } ).rewind();
            INTERLEAVED_ARRAY_COORDS_BUFFER.put( new float[] { (float)(x),         (float)(y + height),
                                                               (float)(x + width), (float)(y + height),
                                                               (float)(x + width), (float)(y),
                                                               (float)(x),         (float)(y),         } ).rewind();

            // gl.glVertexPointer(2, GL_DOUBLE, 0, ALL_VERTEX_2D_COORDS_BUFFER);
            gl.glInterleavedArrays(GL_V2F, 0, INTERLEAVED_ARRAY_COORDS_BUFFER);
            gl.glDrawArrays(GL_QUADS, 0, 4);
            // gl.glDrawElements(GL_QUADS, 4, GL_UNSIGNED_BYTE, INDICES_BUFFER);
        }
        else
        {
            gl.glBegin(GL_QUADS);
            gl.glVertex2d(x,         y + height); // Bottom Left Of The Quad
            gl.glVertex2d(x + width, y + height); // Bottom Right Of The Quad
            gl.glVertex2d(x + width, y);          // Top Right Of The Quad
            gl.glVertex2d(x,         y);          // Top Left Of The Quad
            gl.glEnd();
        }

        gl.glPopMatrix();

        gl.glDisable(GL_ALPHA_TEST);
        gl.glDisable(GL_BLEND);
    }

    /**
    *  Draws a rotated & zoomed quad in a specified point with given width/height.
    */
    public static void drawRotoZoomQuadWithTextureCoords(GL2 gl, double x, double y, double width, double height, double theta, double zoomFactorX, double zoomFactorY)
    {
        // Enable blending, using the SrcOver rule
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // determine which areas of the polygon are to be renderered
        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0

        gl.glPushMatrix();
        // center all the transformations to image's center
        gl.glTranslated(x + width / 2.0, y + height / 2.0, 0.0);
        gl.glScaled(zoomFactorX, zoomFactorY, 0.0);
        gl.glRotated(theta, 0.0, 0.0, 1.0);
        // reset centered transformation
        gl.glTranslated(-(x + width / 2.0), -(y + height / 2.0), 0.0);

        if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
        {
            // Using Vertex Arrays for passing the data to OpenGL
            // ALL_TEXTURE_2D_COORDS_BUFFER.put( new double[] { tx1, ty2, tx2, ty2, tx2, ty1, tx1, ty1 } ).rewind();
            // ALL_VERTEX_2D_COORDS_BUFFER.put( new double[] { x, y + height, x + width, y + height, x + width, y, x, y } ).rewind();
            INTERLEAVED_ARRAY_COORDS_BUFFER.put( new float[] { 0.0f, 0.0f, (float)(x),         (float)(y + height), 0,
                                                               1.0f, 0.0f, (float)(x + width), (float)(y + height), 0,
                                                               1.0f, 1.0f, (float)(x + width), (float)(y),          0,
                                                               0.0f, 1.0f, (float)(x),         (float)(y),          0 } ).rewind();

            // gl.glTexCoordPointer(2, GL_DOUBLE, 0, ALL_TEXTURE_2D_COORDS_BUFFER);
            // gl.glVertexPointer(2, GL_DOUBLE, 0, ALL_VERTEX_2D_COORDS_BUFFER);
            gl.glInterleavedArrays(GL_T2F_V3F, 0, INTERLEAVED_ARRAY_COORDS_BUFFER);
            gl.glDrawArrays(GL_QUADS, 0, 4);
            // gl.glDrawElements(GL_QUADS, 4, GL_UNSIGNED_BYTE, INDICES_BUFFER);
        }
        else
        {
            gl.glBegin(GL_QUADS);
            gl.glTexCoord2d(0.0, 0.0); gl.glVertex2d(x,         y + height); // Bottom Left Of The Quad
            gl.glTexCoord2d(1.0, 0.0); gl.glVertex2d(x + width, y + height); // Bottom Right Of The Quad
            gl.glTexCoord2d(1.0, 1.0); gl.glVertex2d(x + width, y);          // Top Right Of The Quad
            gl.glTexCoord2d(0.0, 1.0); gl.glVertex2d(x,         y);          // Top Left Of The Quad
            gl.glEnd();
        }

        gl.glPopMatrix();

        gl.glDisable(GL_ALPHA_TEST);
        gl.glDisable(GL_BLEND);
    }

    /**
    *  Draws a rotated buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it.
    */
    public static void drawRotoZoomImageTexture(GL2 gl, BufferedImage image, double x, double y, double theta)
    {
        drawRotoZoomImageTexture(gl, image, x, y, theta, 1.0, 1.0, 1.0f, null, true);
    }

    /**
    *  Draws a rotated buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose alpha.
    */
    public static void drawRotoZoomImageTexture(GL2 gl, BufferedImage image, double x, double y, double theta, float alpha)
    {
        drawRotoZoomImageTexture(gl, image, x, y, theta, 1.0, 1.0, alpha, null, true);
    }

    /**
    *  Draws a rotated buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose a color to mix the texture.
    */
    public static void drawRotoZoomImageTexture(GL2 gl, BufferedImage image, double x, double y, double theta, Color color)
    {
        drawRotoZoomImageTexture(gl, image, x, y, theta, 1.0, 1.0, 1.0f, color, true);
    }

    /**
    *  Draws a rotated buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value & a color to mix the texture.
    */
    public static void drawRotoZoomImageTexture(GL2 gl, BufferedImage image, double x, double y, double theta, float alpha, Color color)
    {
        drawRotoZoomImageTexture(gl, image, x, y, theta, 1.0, 1.0, alpha, color, true);
    }

    /**
    *  Draws a rotated & zoomed buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose horizontal & vertical zoom factors.
    */
    public static void drawRotoZoomImageTexture(GL2 gl, BufferedImage image, double x, double y, double theta, double zoomFactorX, double zoomFactorY)
    {
        drawRotoZoomImageTexture(gl, image, x, y, theta, zoomFactorX, zoomFactorY, 1.0f, null, true);
    }

    /**
    *  Draws a rotated & zoomed buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose horizontal & vertical zoom factors & an alpha value.
    */
    public static void drawRotoZoomImageTexture(GL2 gl, BufferedImage image, double x, double y, double theta, double zoomFactorX, double zoomFactorY, float alpha)
    {
        drawRotoZoomImageTexture(gl, image, x, y, theta, zoomFactorX, zoomFactorY, alpha, null, true);
    }

    /**
    *  Draws a rotated & zoomed buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose horizontal & vertical zoom factors & a color to mix the texture.
    */
    public static void drawRotoZoomImageTexture(GL2 gl, BufferedImage image, double x, double y, double theta, double zoomFactorX, double zoomFactorY, Color color)
    {
        drawRotoZoomImageTexture(gl, image, x, y, theta, zoomFactorX, zoomFactorY, 1.0f, color, true);
    }

    /**
    *  Draws a rotated & zoomed buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose horizontal & vertical zoom factors & an alpha value & a color to mix the texture.
    */
    public static void drawRotoZoomImageTexture(GL2 gl, BufferedImage image, double x, double y, double theta, double zoomFactorX, double zoomFactorY, float alpha, Color color)
    {
        drawRotoZoomImageTexture(gl, image, x, y, theta, zoomFactorX, zoomFactorY, alpha, color, true);
    }

    /**
    *  Draws a rotated buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomImageTexture(GL2 gl, BufferedImage image, double x, double y, double theta, boolean bindEnableAndDisableTexture)
    {
        drawRotoZoomImageTexture(gl, image, x, y, theta, 1.0, 1.0, 1.0f, null, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a rotated buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose alpha.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomImageTexture(GL2 gl, BufferedImage image, double x, double y, double theta, float alpha, boolean bindEnableAndDisableTexture)
    {
        drawRotoZoomImageTexture(gl, image, x, y, theta, 1.0, 1.0, alpha, null, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a rotated buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomImageTexture(GL2 gl, BufferedImage image, double x, double y, double theta, Color color, boolean bindEnableAndDisableTexture)
    {
        drawRotoZoomImageTexture(gl, image, x, y, theta, 1.0, 1.0, 1.0f, color, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a rotated buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value & a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomImageTexture(GL2 gl, BufferedImage image, double x, double y, double theta, float alpha, Color color, boolean bindEnableAndDisableTexture)
    {
        drawRotoZoomImageTexture(gl, image, x, y, theta, 1.0, 1.0, alpha, color, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a rotated & zoomed buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose horizontal & vertical zoom factors.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomImageTexture(GL2 gl, BufferedImage image, double x, double y, double theta, double zoomFactorX, double zoomFactorY, boolean bindEnableAndDisableTexture)
    {
        drawRotoZoomImageTexture(gl, image, x, y, theta, zoomFactorX, zoomFactorY, 1.0f, null, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a rotated & zoomed buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose horizontal & vertical zoom factors & an alpha value.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomImageTexture(GL2 gl, BufferedImage image, double x, double y, double theta, double zoomFactorX, double zoomFactorY, float alpha, boolean bindEnableAndDisableTexture)
    {
        drawRotoZoomImageTexture(gl, image, x, y, theta, zoomFactorX, zoomFactorY, alpha, null, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a rotated & zoomed buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose horizontal & vertical zoom factors & a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomImageTexture(GL2 gl, BufferedImage image, double x, double y, double theta, double zoomFactorX, double zoomFactorY, Color color, boolean bindEnableAndDisableTexture)
    {
        drawRotoZoomImageTexture(gl, image, x, y, theta, zoomFactorX, zoomFactorY, 1.0f, color, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a rotated & zoomed buffered image converted to texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose horizontal & vertical zoom factors & an alpha value & a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomImageTexture(GL2 gl, BufferedImage image, double x, double y, double theta, double zoomFactorX, double zoomFactorY, float alpha, Color color, boolean bindEnableAndDisableTexture)
    {
        if (image == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawRotoZoomImageTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
        {
            Texture imageTexture = AWTTextureIO.newTexture(GLProfile.getDefault(), image, false);
            drawRotoZoomTexture(gl, imageTexture, x, y, theta, zoomFactorX, zoomFactorY, alpha, color, bindEnableAndDisableTexture);
        }
    }

    /**
    *  Draws a rotated image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it.
    */
    public static void drawRotoZoomTexture(GL2 gl, Texture imageTexture, double x, double y, double theta)
    {
        drawRotoZoomTexture(gl, imageTexture, x, y, theta, 1.0, 1.0, 1.0f, null, true);
    }

    /**
    *  Draws a rotated image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose alpha.
    */
    public static void drawRotoZoomTexture(GL2 gl, Texture imageTexture, double x, double y, double theta, float alpha)
    {
        drawRotoZoomTexture(gl, imageTexture, x, y, theta, 1.0, 1.0, alpha, null, true);
    }

    /**
    *  Draws a rotated image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose a color to mix the texture.
    */
    public static void drawRotoZoomTexture(GL2 gl, Texture imageTexture, double x, double y, double theta, Color color)
    {
        drawRotoZoomTexture(gl, imageTexture, x, y, theta, 1.0, 1.0, 1.0f, color, true);
    }

    /**
    *  Draws a rotated image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value & a color to mix the texture.
    */
    public static void drawRotoZoomTexture(GL2 gl, Texture imageTexture, double x, double y, double theta, float alpha, Color color)
    {
        drawRotoZoomTexture(gl, imageTexture, x, y, theta, 1.0, 1.0, alpha, color, true);
    }

    /**
    *  Draws a rotated & zoomed image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose horizontal & vertical zoom factors.
    */
    public static void drawRotoZoomTexture(GL2 gl, Texture imageTexture, double x, double y, double theta, double zoomFactorX, double zoomFactorY)
    {
        drawRotoZoomTexture(gl, imageTexture, x, y, theta, zoomFactorX, zoomFactorY, 1.0f, null, true);
    }

    /**
    *  Draws a rotated & zoomed image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose horizontal & vertical zoom factors & an alpha value.
    */
    public static void drawRotoZoomTexture(GL2 gl, Texture imageTexture, double x, double y, double theta, double zoomFactorX, double zoomFactorY, float alpha)
    {
        drawRotoZoomTexture(gl, imageTexture, x, y, theta, zoomFactorX, zoomFactorY, alpha, null, true);
    }

    /**
    *  Draws a rotated & zoomed image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose horizontal & vertical zoom factors & a color to mix the texture.
    */
    public static void drawRotoZoomTexture(GL2 gl, Texture imageTexture, double x, double y, double theta, double zoomFactorX, double zoomFactorY, Color color)
    {
        drawRotoZoomTexture(gl, imageTexture, x, y, theta, zoomFactorX, zoomFactorY, 1.0f, color, true);
    }

    /**
    *  Draws a rotated & zoomed image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose horizontal & vertical zoom factors & an alpha value & a color to mix the texture.
    */
    public static void drawRotoZoomTexture(GL2 gl, Texture imageTexture, double x, double y, double theta, double zoomFactorX, double zoomFactorY, float alpha, Color color)
    {
        drawRotoZoomTexture(gl, imageTexture, x, y, theta, zoomFactorX, zoomFactorY, alpha, color, true);
    }

    /**
    *  Draws a rotated image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomTexture(GL2 gl, Texture imageTexture, double x, double y, double theta, boolean bindEnableAndDisableTexture)
    {
        drawRotoZoomTexture(gl, imageTexture, x, y, theta, 1.0, 1.0, 1.0f, null, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a rotated image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose alpha.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomTexture(GL2 gl, Texture imageTexture, double x, double y, double theta, float alpha, boolean bindEnableAndDisableTexture)
    {
        drawRotoZoomTexture(gl, imageTexture, x, y, theta, 1.0, 1.0, alpha, null, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a rotated image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomTexture(GL2 gl, Texture imageTexture, double x, double y, double theta, Color color, boolean bindEnableAndDisableTexture)
    {
        drawRotoZoomTexture(gl, imageTexture, x, y, theta, 1.0, 1.0, 1.0f, color, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a rotated image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose an alpha value & a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomTexture(GL2 gl, Texture imageTexture, double x, double y, double theta, float alpha, Color color, boolean bindEnableAndDisableTexture)
    {
        drawRotoZoomTexture(gl, imageTexture, x, y, theta, 1.0, 1.0, alpha, color, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a rotated & zoomed image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose horizontal & vertical zoom factors.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomTexture(GL2 gl, Texture imageTexture, double x, double y, double theta, double zoomFactorX, double zoomFactorY, boolean bindEnableAndDisableTexture)
    {
        drawRotoZoomTexture(gl, imageTexture, x, y, theta, zoomFactorX, zoomFactorY, 1.0f, null, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a rotated & zoomed image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose horizontal & vertical zoom factors & an alpha value.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomTexture(GL2 gl, Texture imageTexture, double x, double y, double theta, double zoomFactorX, double zoomFactorY, float alpha, boolean bindEnableAndDisableTexture)
    {
        drawRotoZoomTexture(gl, imageTexture, x, y, theta, zoomFactorX, zoomFactorY, alpha, null, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a rotated & zoomed image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose horizontal & vertical zoom factors & a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomTexture(GL2 gl, Texture imageTexture, double x, double y, double theta, double zoomFactorX, double zoomFactorY, Color color, boolean bindEnableAndDisableTexture)
    {
        drawRotoZoomTexture(gl, imageTexture, x, y, theta, zoomFactorX, zoomFactorY, 1.0f, color, bindEnableAndDisableTexture);
    }

    /**
    *  Draws a rotated & zoomed image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Overloaded version to also choose horizontal & vertical zoom factors & an alpha value & a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomTexture(GL2 gl, Texture imageTexture, double x, double y, double theta, double zoomFactorX, double zoomFactorY, float alpha, Color color, boolean bindEnableAndDisableTexture)
    {
        if (imageTexture == null)
        {
            if (DEBUG_BUILD) println("Null image texture supplied to DrawTextureSFXs in method drawRotoZoomTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
        {
            if (alpha < 0.0f)
            {
                if (DEBUG_BUILD) println("drawRotoZoomTexture(): Alpha must be >= 0.0f; setting to 0.0f to DrawTextureSFXs!");
                alpha = 0.0f;
            }
            else if (alpha > 1.0f)
            {
                if (DEBUG_BUILD) println("drawRotoZoomTexture(): Alpha must be <= 1.0f; setting to 1.0f to DrawTextureSFXs!");
                alpha = 1.0f;
            }

            float r = alpha;
            float g = alpha;
            float b = alpha;

            if (color != null)
            {
                r = color.getRed()   / 255.0f;
                g = color.getGreen() / 255.0f;
                b = color.getBlue()  / 255.0f;
            }

            TextureCoords tc = imageTexture.getImageTexCoords();
            float tx1 = tc.left();
            float ty1 = tc.top();
            float tx2 = tc.right();
            float ty2 = tc.bottom();

            // Enable blending, using the SrcOver rule
            gl.glEnable(GL_BLEND);
            gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

            // determine which areas of the polygon are to be renderered
            gl.glEnable(GL_ALPHA_TEST);
            gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0

            // Use the GL_MODULATE texture function to effectively multiply
            // each pixel in the texture by the current alpha value
            gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

            if (bindEnableAndDisableTexture)
            {
                imageTexture.bind(gl);
                imageTexture.enable(gl);
            }

            int width = imageTexture.getImageWidth();
            int height = imageTexture.getImageHeight();

            gl.glPushMatrix();
            // center all the transformations to image's center
            gl.glTranslated(x + width / 2.0, y + height / 2.0, 0.0);
            gl.glScaled(zoomFactorX, zoomFactorY, 0.0);
            gl.glRotated(theta, 0.0, 0.0, 1.0);
            // reset centered transformation
            gl.glTranslated(-(x + width / 2.0), -(y + height / 2.0), 0.0);

            if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
            {
                // Using Vertex Arrays for passing the data to OpenGL
                // ALL_TEXTURE_2D_COORDS_BUFFER.put( new double[] { tx1, ty2, tx2, ty2, tx2, ty1, tx1, ty1 } ).rewind();
                // ALL_VERTEX_2D_COORDS_BUFFER.put( new double[] { x, y + height, x + width, y + height, x + width, y, x, y } ).rewind();
                INTERLEAVED_ARRAY_COORDS_BUFFER.put( new float[] { tx1, ty2, (float)(x),         (float)(y + height), 0,
                                                                   tx2, ty2, (float)(x + width), (float)(y + height), 0,
                                                                   tx2, ty1, (float)(x + width), (float)(y),          0,
                                                                   tx1, ty1, (float)(x),         (float)(y),          0 } ).rewind();

                gl.glColor4f(r, g, b, alpha);
                // gl.glTexCoordPointer(2, GL_DOUBLE, 0, ALL_TEXTURE_2D_COORDS_BUFFER);
                // gl.glVertexPointer(2, GL_DOUBLE, 0, ALL_VERTEX_2D_COORDS_BUFFER);
                gl.glInterleavedArrays(GL_T2F_V3F, 0, INTERLEAVED_ARRAY_COORDS_BUFFER);
                gl.glDrawArrays(GL_QUADS, 0, 4);
                // gl.glDrawElements(GL_QUADS, 4, GL_UNSIGNED_BYTE, INDICES_BUFFER);
            }
            else
            {
                gl.glBegin(GL_QUADS);
                gl.glColor4f(r, g, b, alpha);
                gl.glTexCoord2d(tx1, ty2); gl.glVertex2d(0,         0 + height); // Bottom Left Of The Texture and Quad
                gl.glTexCoord2d(tx2, ty2); gl.glVertex2d(0 + width, 0 + height); // Bottom Right Of The Texture and Quad
                gl.glTexCoord2d(tx2, ty1); gl.glVertex2d(0 + width, 0);          // Top Right Of The Texture and Quad
                gl.glTexCoord2d(tx1, ty1); gl.glVertex2d(0,         0);          // Top Left Of The Texture and Quad
                gl.glEnd();
            }

            gl.glPopMatrix();

            if (bindEnableAndDisableTexture)
                imageTexture.disable(gl);

            gl.glDisable(GL_ALPHA_TEST);
            gl.glDisable(GL_BLEND);
        }
    }

    /**
    *  Draws a render-to-texture from its associated image texture in a specified rectangle width/height. In case of a null supplied image texture, the method draws
    *  a white box with red text (?!?) in it. Version to also choose an alpha value & a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRenderToTexture(GL2 gl, RenderToTexture renderToTexture, double x, double y, double width, double height, float alpha, Color color, boolean bindEnableAndDisableRenderToTexture, int textureUnit)
    {
        if (renderToTexture == null)
        {
            if (DEBUG_BUILD) println("Null renderToTexture supplied to DrawTextureSFXs in method drawRenderToTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
        {
            if (alpha < 0.0f)
            {
                if (DEBUG_BUILD) println("drawRenderToTexture(): Alpha must be >= 0.0f; setting to 0.0f to DrawTextureSFXs!");
                alpha = 0.0f;
            }
            else if (alpha > 1.0f)
            {
                if (DEBUG_BUILD) println("drawRenderToTexture(): Alpha must be <= 1.0f; setting to 1.0f to DrawTextureSFXs!");
                alpha = 1.0f;
            }

            float r = alpha;
            float g = alpha;
            float b = alpha;

            if (color != null)
            {
                r = color.getRed()   / 255.0f;
                g = color.getGreen() / 255.0f;
                b = color.getBlue()  / 255.0f;
            }

            // Enable blending, using the SrcOver rule
            gl.glEnable(GL_BLEND);
            gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

            // determine which areas of the polygon are to be renderered
            gl.glEnable(GL_ALPHA_TEST);
            gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0

            // Use the GL_MODULATE texture function to effectively multiply
            // each pixel in the texture by the current alpha value
            gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

            if (bindEnableAndDisableRenderToTexture)
            {
                if (textureUnit == 0)
                    renderToTexture.bind(gl);
                else
                    renderToTexture.bind(gl, textureUnit);
                renderToTexture.enable(gl);
            }

            if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
            {
                // Using Vertex Arrays for passing the data to OpenGL
                // ALL_TEXTURE_2D_COORDS_BUFFER.put( new double[] { tx1, ty2, tx2, ty2, tx2, ty1, tx1, ty1 } ).rewind();
                // ALL_VERTEX_2D_COORDS_BUFFER.put( new double[] { x, y + height, x + width, y + height, x + width, y, x, y } ).rewind();
                INTERLEAVED_ARRAY_COORDS_BUFFER.put( new float[] { 0.0f, 0.0f, (float)(x),         (float)(y + height), 0,
                                                                   1.0f, 0.0f, (float)(x + width), (float)(y + height), 0,
                                                                   1.0f, 1.0f, (float)(x + width), (float)(y),          0,
                                                                   0.0f, 1.0f, (float)(x),         (float)(y),          0 } ).rewind();

                gl.glColor4f(r, g, b, alpha);
                // gl.glTexCoordPointer(2, GL_DOUBLE, 0, ALL_TEXTURE_2D_COORDS_BUFFER);
                // gl.glVertexPointer(2, GL_DOUBLE, 0, ALL_VERTEX_2D_COORDS_BUFFER);
                gl.glInterleavedArrays(GL_T2F_V3F, 0, INTERLEAVED_ARRAY_COORDS_BUFFER);
                gl.glDrawArrays(GL_QUADS, 0, 4);
                // gl.glDrawElements(GL_QUADS, 4, GL_UNSIGNED_BYTE, INDICES_BUFFER);
            }
            else
            {
                gl.glBegin(GL_QUADS);
                gl.glColor4f(r, g, b, alpha);
                gl.glTexCoord2d(0.0, 0.0); gl.glVertex2d(x,         y + height); // Top Left Of The Texture and Quad
                gl.glTexCoord2d(1.0, 0.0); gl.glVertex2d(x + width, y + height); // Top Right Of The Texture and Quad
                gl.glTexCoord2d(1.0, 1.0); gl.glVertex2d(x + width, y);          // Bottom Right Of The Texture and Quad
                gl.glTexCoord2d(0.0, 1.0); gl.glVertex2d(x,         y);          // Bottom Left Of The Texture and Quad
                gl.glEnd();
             }

            if (bindEnableAndDisableRenderToTexture)
                renderToTexture.disable(gl);

            gl.glDisable(GL_ALPHA_TEST);
            gl.glDisable(GL_BLEND);
        }
    }

    /**
    *  Draws a rotated & zoomed render-to-texture from its associated image texture. In case of a null supplied image, the method draws
    *  a white box with red text (?!?) in it. Version to also choose horizontal & vertical zoom factors & an alpha value & a color to mix the texture.
    *  Also optionally accepts to bind/enable the texture.
    */
    public static void drawRotoZoomRenderToTexture(GL2 gl, RenderToTexture renderToTexture, double x, double y, double theta, double zoomFactorX, double zoomFactorY, float alpha, Color color, boolean bindEnableAndDisableRenderToTexture, int textureUnit)
    {
        if (renderToTexture == null)
        {
            if (DEBUG_BUILD) println("Null renderToTexture supplied to DrawTextureSFXs in method drawRotoZoomRenderToTexture()!");
            drawNullPointerTexture(gl, x, y);
        }
        else
        {
            if (alpha < 0.0f)
            {
                if (DEBUG_BUILD) println("drawRotoZoomRenderToTexture(): Alpha must be >= 0.0f; setting to 0.0f to DrawTextureSFXs!");
                alpha = 0.0f;
            }
            else if (alpha > 1.0f)
            {
                if (DEBUG_BUILD) println("drawRotoZoomRenderToTexture(): Alpha must be <= 1.0f; setting to 1.0f to DrawTextureSFXs!");
                alpha = 1.0f;
            }

            float r = alpha;
            float g = alpha;
            float b = alpha;

            if (color != null)
            {
                r = color.getRed()   / 255.0f;
                g = color.getGreen() / 255.0f;
                b = color.getBlue()  / 255.0f;
            }

            // Enable blending, using the SrcOver rule
            gl.glEnable(GL_BLEND);
            gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

            // determine which areas of the polygon are to be renderered
            gl.glEnable(GL_ALPHA_TEST);
            gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0

            // Use the GL_MODULATE texture function to effectively multiply
            // each pixel in the texture by the current alpha value
            gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

            if (bindEnableAndDisableRenderToTexture)
            {
                if (textureUnit == 0)
                    renderToTexture.bind(gl);
                else
                    renderToTexture.bind(gl, textureUnit);
                renderToTexture.enable(gl);
            }

            int width = renderToTexture.getWidth();
            int height = renderToTexture.getHeight();

            gl.glPushMatrix();
            // center all the transformations to image's center
            gl.glTranslated(x + width / 2.0, y + height / 2.0, 0.0);
            gl.glScaled(zoomFactorX, zoomFactorY, 0.0);
            gl.glRotated(theta, 0.0, 0.0, 1.0);
            // reset centered transformation
            gl.glTranslated(-(x + width / 2.0), -(y + height / 2.0), 0.0);

            if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
            {
                // Using Vertex Arrays for passing the data to OpenGL
                // ALL_TEXTURE_2D_COORDS_BUFFER.put( new double[] { tx1, ty2, tx2, ty2, tx2, ty1, tx1, ty1 } ).rewind();
                // ALL_VERTEX_2D_COORDS_BUFFER.put( new double[] { x, y + height, x + width, y + height, x + width, y, x, y } ).rewind();
                INTERLEAVED_ARRAY_COORDS_BUFFER.put( new float[] { 0.0f, 0.0f, (float)(x),         (float)(y + height), 0,
                                                                   1.0f, 0.0f, (float)(x + width), (float)(y + height), 0,
                                                                   1.0f, 1.0f, (float)(x + width), (float)(y),          0,
                                                                   0.0f, 1.0f, (float)(x),         (float)(y),          0 } ).rewind();

                gl.glColor4f(r, g, b, alpha);
                // gl.glTexCoordPointer(2, GL_DOUBLE, 0, ALL_TEXTURE_2D_COORDS_BUFFER);
                // gl.glVertexPointer(2, GL_DOUBLE, 0, ALL_VERTEX_2D_COORDS_BUFFER);
                gl.glInterleavedArrays(GL_T2F_V3F, 0, INTERLEAVED_ARRAY_COORDS_BUFFER);
                gl.glDrawArrays(GL_QUADS, 0, 4);
                // gl.glDrawElements(GL_QUADS, 4, GL_UNSIGNED_BYTE, INDICES_BUFFER);
            }
            else
            {
                gl.glBegin(GL_QUADS);
                gl.glColor4f(r, g, b, alpha);
                gl.glTexCoord2d(0.0, 0.0); gl.glVertex2d(x,         y + height); // Top Left Of The Texture and Quad
                gl.glTexCoord2d(1.0, 0.0); gl.glVertex2d(x + width, y + height); // Top Right Of The Texture and Quad
                gl.glTexCoord2d(1.0, 1.0); gl.glVertex2d(x + width, y);          // Bottom Right Of The Texture and Quad
                gl.glTexCoord2d(0.0, 1.0); gl.glVertex2d(x,         y);          // Bottom Right Of The Texture and Quad
                gl.glEnd();
            }

            gl.glPopMatrix();

            if (bindEnableAndDisableRenderToTexture)
                renderToTexture.disable(gl);
            if (textureUnit != 0)
                gl.glActiveTexture(GL_TEXTURE0);

            gl.glDisable(GL_ALPHA_TEST);
            gl.glDisable(GL_BLEND);
        }
    }

    /**
    *  Generates the blob star shade pixel.
    */
    private static double getBlobShade(double dist, double maxDist, double dp0, double dp1, double dp2, double sp0, double sp1, double sp2)
    {
    	double d0 = maxDist * dp0;
    	double d1 = maxDist * dp1;
    	double d2 = maxDist * dp2;

    	if (dist < d0)
            return sp0;
    	else if (dist < d1)
            return ( sp0 * (d1 - dist) + sp1 * (dist - d0) ) / (d1 - d0);
    	else if (dist < d2)
            return ( sp1 * (d2 - dist) + sp2 * (dist - d1) ) / (d2 - d1);
    	else
            return sp2;
    }

    /**
    *  Generates an algorithmic based blob star OpenGL texture.
    */
    public static Texture blobStarTextureGenerate(int blobStarTextureWidth, int blobStarTextureHeight, boolean useAutoMipmapGeneration)
    {
        int[] generatedTexture = new int[blobStarTextureWidth * blobStarTextureHeight];
        int generatedTextureI = blobStarTextureWidth * blobStarTextureHeight;
        int generatedTextureC = 0;

        double xc = 0.0, yc = 0.0;
        double dist = 0.0;

        int genTextureY = blobStarTextureHeight;
        int genTextureX = blobStarTextureWidth;
        while (--genTextureY >= 0)
        {
            genTextureX = blobStarTextureWidth;
            while (--genTextureX >= 0)
            {
                xc = genTextureX - (blobStarTextureWidth / 2);
                yc = genTextureY - (blobStarTextureHeight / 2);
                dist = sqrt(xc * xc + yc * yc);
                generatedTextureC = (int)getBlobShade(dist, blobStarTextureWidth / 2, 0.6, 0.85, 1.0, 255, 192, 0);
                generatedTexture[--generatedTextureI] = (generatedTextureC << 24) | (generatedTextureC << 16) | (generatedTextureC << 8) | generatedTextureC;
            }
        }

        BufferedImage blobImage = new BufferedImage(blobStarTextureWidth, blobStarTextureHeight, BufferedImage.TYPE_INT_ARGB);
        blobImage.setRGB(0, 0, blobStarTextureWidth, blobStarTextureHeight, generatedTexture, 0, blobStarTextureWidth);

        return TextureProducer.createTextureFromBufferedImageAndDeleteOrigContext(blobImage, useAutoMipmapGeneration);
    }

    /**
    *  This method draws a white box with red text (?!?) in it (used for when supplying null image textures).
    */
    public static void drawNullPointerTexture(GL2 gl, double x, double y)
    {
        drawTexture(gl, TextureProducer.getNullPointerTexture(), x, y, 40, 40);
    }


}
