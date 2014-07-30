package org.BioLayoutExpress3D.Textures;

import java.nio.*;
import static java.lang.Math.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.common.nio.Buffers;
import static javax.media.opengl.GL2.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.StaticLibraries.Random.*;

/**
*
*  Various texture operations used as special effects.
*  This class is responsible for producing textures using various effects.
*
*
* @see org.BioLayoutExpress3D.Textures.ImageSFXs
* @author Thanos Theo, Michael Kargas, 2008-2009
* @version 3.0.0.0
*
*/

public class TextureSFXs
{

    /**
    *  Variable used for the texture displacement effect.
    */
    private static final int DISPLACEMENT_GRID_X = 32;

    /**
    *  Variable used for the texture displacement effect.
    */
    private static final int DISPLACEMENT_GRID_Y = 32;

    /**
    *  Variable used for the texture displacement effect.
    */
    private float displacementTime = 0.0f;

    /**
    *  Variable used for the texture displacement effect.
    */
    private float displacementTimeStep = 0.0f;

    /**
    *  Variable to be used for OpenGL Vertex Arrays support for the displacement effect.
    */
    // private final IntBuffer INDICES_DISPLACEMENT_BUFFER = Buffers.newDirectIntBuffer(4 * DISPLACEMENT_GRID_X * DISPLACEMENT_GRID_Y);

    /**
    *  Variable to be used for OpenGL Vertex Arrays support for the displacement effect.
    */
    // private final FloatBuffer ALL_TEXTURE_2D_COORDS_DISPLACEMENT_BUFFER = Buffers.newDirectFloatBuffer(2 * 4 * DISPLACEMENT_GRID_X * DISPLACEMENT_GRID_Y);

    /**
    *  Variable to be used for OpenGL Vertex Arrays support for the displacement effect.
    */
    // private final FloatBuffer ALL_VERTEX_2D_COORDS_DISPLACEMENT_BUFFER = Buffers.newDirectFloatBuffer(2 * 4 * DISPLACEMENT_GRID_X * DISPLACEMENT_GRID_Y);

    /**
    *  Variable to be used for OpenGL Vertex Arrays support for the displacement effect.
    */
    private final FloatBuffer INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER = Buffers.newDirectFloatBuffer((2 * 2 * 4 + 4) * DISPLACEMENT_GRID_X * DISPLACEMENT_GRID_Y); // add 4 dummy values for GL_T2F_V3F V3F part

    /**
    *  Variable used for the texture displacement effect.
    */
    private float[][] dispX = null;

    /**
    *  Variable used for the texture displacement effect.
    */
    private float[][] dispY = null;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private Texture blobStarTexture = null;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private BlobStars3DScrollerEffectInitializer blobStars3DScrollerEffectInitializer = null;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private int blobStarDisplayList = 0;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private float[] star3D_X = null;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private float[] star3D_Y = null;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private float[] star3D_Z = null;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private float[] star3D_V = null;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private float[] screenStarX = null;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private float[] screenStarY = null;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private float[] screenStarZ = null;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private float blobStars3DScrollerMouseMoveX = 0.0f;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private float blobStars3DScrollerMouseMoveY = 0.0f;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private float blobStars3DScrollerMouseMovePreviousX = 0.0f;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private float blobStars3DScrollerMouseMovePreviousY = 0.0f;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private float blobMoveStep = 1.5f;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private static final int BLOB_MOVE_RESET_VALUE = 70;

    /**
    *  Initializes the texture displacement effect.
    */
    public void textureDisplacementEffectInit(float displacementTimeStep)
    {
        this.displacementTimeStep = displacementTimeStep;

        dispX = new float[DISPLACEMENT_GRID_X][DISPLACEMENT_GRID_Y];
        dispY = new float[DISPLACEMENT_GRID_X][DISPLACEMENT_GRID_Y];

        /*
        INDICES_DISPLACEMENT_BUFFER.clear();
        int endIndex = 4 * DISPLACEMENT_GRID_X * DISPLACEMENT_GRID_Y;
        for (int i = 0; i < endIndex; i++)
            INDICES_DISPLACEMENT_BUFFER.put(i);
        INDICES_DISPLACEMENT_BUFFER.rewind();
        */
    }

    /**
    *  Processes the texture displacement effect.
    */
    public void textureDisplacementEffect()
    {
        displacementTime += displacementTimeStep;
    }

    /**
    *  Renders the texture displacement effect.
    *  Overloaded version to include an alpha value.
    */
    public void textureDisplacementRender(GL2 gl, Texture imageTexture, int x1, int y1, int x2, int y2)
    {
        textureDisplacementRender(gl, imageTexture, x1, y1, x2, y2, 1.0f);
    }

    /**
    *  Renders the texture displacement effect.
    */
    public void textureDisplacementRender(GL2 gl, Texture imageTexture, int x1, int y1, int x2, int y2, float alpha)
    {
        TextureCoords tc = imageTexture.getImageTexCoords();
        float tx1 = tc.left();
        float ty1 = tc.top();
        float tx2 = tc.right();
        float ty2 = tc.bottom();

        float vx = 0.0f;
        float vy = 0.0f;
        float dvx = 1.0f / (float)(DISPLACEMENT_GRID_X - 1);
        float dvy = 1.0f / (float)(DISPLACEMENT_GRID_Y - 1);

        float tx = 0.0f;
        float ty = 0.0f;
        float dtx = (tx2 - tx1) / (float)(DISPLACEMENT_GRID_X - 1);
        float dty = (ty2 - ty1) / (float)(DISPLACEMENT_GRID_Y - 1);


        float mulx = (tx2 - tx1) / 16.0f;
        float muly = (ty2 - ty1) / 16.0f;

        float mvx1 = 8.0f;
        float mvx2 = 2.0f;
        float mvy1 = 12.0f;
        float mvy2 = 4.0f;

        // Calculate texture coordinates displacement table
        int y = DISPLACEMENT_GRID_Y;
        int x = 0;
        while (--y >= 0)
        {
            vx = 0.0f;
            x = DISPLACEMENT_GRID_X;
            while (--x >= 0)
            {
                dispX[x][y] = (float)(sin(2.0f * (vx + mvx1 * displacementTime)) * mulx - sin(mvx2 * (vy + 3.0f * displacementTime)) * mulx);
                dispY[x][y] = (float)(sin(1.5f * (vx + mvy1 * displacementTime)) * muly - sin(mvy2 * (vy + 1.25f * displacementTime)) * muly);

                vx += dvx;
                tx += dtx;
            }

            vy += dvy;
        }

        // Render OpenGL displacement texture grid

        // Enable blending, using the SrcOver rule
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // determine which areas of the polygon are to be renderered
        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0

        gl.glShadeModel(GL_SMOOTH);

        imageTexture.bind(gl);
        imageTexture.enable(gl);

        if (!USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
            gl.glBegin(GL_QUADS);

        gl.glColor4f(1.0f, 1.0f, 1.0f, alpha);

        dvx = (float)(x2 - x1) / (float)(DISPLACEMENT_GRID_X - 1);
        dvy = (float)(y2 - y1) / (float)(DISPLACEMENT_GRID_Y - 1);

        vy = (float)y1;
        ty = 0.0f;
        for (y = 0; y < DISPLACEMENT_GRID_Y - 1; y++)
        {
            vx = (float)x1;
            tx = 0.0f;

            for (x = 0; x < DISPLACEMENT_GRID_X - 1; x++)
            {
                if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
                {
                    // ALL_TEXTURE_2D_COORDS_DISPLACEMENT_BUFFER.put(tx + dispX[x][y]);
                    // ALL_TEXTURE_2D_COORDS_DISPLACEMENT_BUFFER.put(ty + dispY[x][y]);
                    // ALL_VERTEX_2D_COORDS_DISPLACEMENT_BUFFER.put(vx);
                    // ALL_VERTEX_2D_COORDS_DISPLACEMENT_BUFFER.put(vy);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(tx + dispX[x][y]);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(ty + dispY[x][y]);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(vx);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(vy);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(0); // dummy 0 for V3F

                    // ALL_TEXTURE_2D_COORDS_DISPLACEMENT_BUFFER.put(tx + dtx + dispX[x + 1][y]);
                    // ALL_TEXTURE_2D_COORDS_DISPLACEMENT_BUFFER.put(ty + dispY[x + 1][y]);
                    // ALL_VERTEX_2D_COORDS_DISPLACEMENT_BUFFER.put(vx + dvx);
                    // ALL_VERTEX_2D_COORDS_DISPLACEMENT_BUFFER.put(vy);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(tx + dtx + dispX[x + 1][y]);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(ty + dispY[x + 1][y]);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(vx + dvx);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(vy);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(0); // dummy 0 for V3F

                    // ALL_TEXTURE_2D_COORDS_DISPLACEMENT_BUFFER.put(tx + dtx + dispX[x + 1][y + 1]);
                    // ALL_TEXTURE_2D_COORDS_DISPLACEMENT_BUFFER.put(ty + dty + dispY[x + 1][y + 1]);
                    // ALL_VERTEX_2D_COORDS_DISPLACEMENT_BUFFER.put(vx + dvx);
                    // ALL_VERTEX_2D_COORDS_DISPLACEMENT_BUFFER.put(vy + dvy);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(tx + dtx + dispX[x + 1][y + 1]);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(ty + dty + dispY[x + 1][y + 1]);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(vx + dvx);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(vy + dvy);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(0); // dummy 0 for V3F

                    // ALL_TEXTURE_2D_COORDS_DISPLACEMENT_BUFFER.put(tx  + dispX[x][y + 1]);
                    // ALL_TEXTURE_2D_COORDS_DISPLACEMENT_BUFFER.put(ty + dty + dispY[x][y + 1]);
                    // ALL_VERTEX_2D_COORDS_DISPLACEMENT_BUFFER.put(vx);
                    // ALL_VERTEX_2D_COORDS_DISPLACEMENT_BUFFER.put(vy + dvy);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(tx  + dispX[x][y + 1]);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(ty + dty + dispY[x][y + 1]);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(vx);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(vy + dvy);
                    INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.put(0); // dummy 0 for V3F
                }
                else
                {
                    gl.glTexCoord2f(tx + dispX[x][y], ty + dispY[x][y]);
                    gl.glVertex2f(vx, vy);             // Bottom Left Of The Texture and Quad

                    gl.glTexCoord2f(tx + dtx + dispX[x + 1][y], ty + dispY[x + 1][y]);
                    gl.glVertex2f(vx + dvx, vy);       // Bottom Right Of The Texture and Quad

                    gl.glTexCoord2f(tx + dtx + dispX[x + 1][y + 1], ty + dty + dispY[x + 1][y + 1]);
                    gl.glVertex2f(vx + dvx, vy + dvy); // Top Right Of The Texture and Quad

                    gl.glTexCoord2f(tx  + dispX[x][y + 1], ty + dty + dispY[x][y + 1]);
                    gl.glVertex2f(vx, vy + dvy);	   // Top Left Of The Texture and Quad
                }

                vx += dvx;
                tx += dtx;
            }

            vy += dvy;
            ty += dty;
        }

        if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
        {
            // ALL_TEXTURE_2D_COORDS_DISPLACEMENT_BUFFER.rewind();
            // ALL_VERTEX_2D_COORDS_DISPLACEMENT_BUFFER.rewind();
            INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER.rewind();

            // gl.glTexCoordPointer(2, GL_FLOAT, 0, ALL_TEXTURE_2D_COORDS_DISPLACEMENT_BUFFER);
            // gl.glVertexPointer(2, GL_FLOAT, 0, ALL_VERTEX_2D_COORDS_DISPLACEMENT_BUFFER);
            gl.glInterleavedArrays(GL_T2F_V3F, 0, INTERLEAVED_ARRAY_COORDS_DISPLACEMENT_BUFFER);
            gl.glDrawArrays(GL_QUADS, 0, 4 * DISPLACEMENT_GRID_X * DISPLACEMENT_GRID_Y);
            // gl.glDrawElements(GL_QUADS, 4 * DISPLACEMENT_GRID_X * DISPLACEMENT_GRID_Y, GL_UNSIGNED_INT, INDICES_DISPLACEMENT_BUFFER);
        }
        else
            gl.glEnd();

        imageTexture.disable(gl);

        gl.glDisable(GL_ALPHA_TEST);
        gl.glDisable(GL_BLEND);
    }

    /**
    *  Moves the blob stars 3D scroller field according to the mouse move values.
    */
    public void blobStars3DScrollerEffectMouseMove(int x, int y)
    {
        if (blobStars3DScrollerMouseMovePreviousX != x)
        {
            if (blobStars3DScrollerMouseMovePreviousX < x)
                blobStars3DScrollerMouseMoveX += blobMoveStep;
            else
                blobStars3DScrollerMouseMoveX -= blobMoveStep;
        }

        if (blobStars3DScrollerMouseMovePreviousY != y)
        {
            if (blobStars3DScrollerMouseMovePreviousY < y)
                blobStars3DScrollerMouseMoveY += blobMoveStep;
            else
                blobStars3DScrollerMouseMoveY -= blobMoveStep;
        }

        if ( blobStars3DScrollerMouseMoveX > BLOB_MOVE_RESET_VALUE || blobStars3DScrollerMouseMoveX < -BLOB_MOVE_RESET_VALUE ||
             blobStars3DScrollerMouseMoveY > BLOB_MOVE_RESET_VALUE || blobStars3DScrollerMouseMoveY < -BLOB_MOVE_RESET_VALUE )
        {
            blobMoveStep = -blobMoveStep;
        }

        blobStars3DScrollerMouseMovePreviousX = x;
        blobStars3DScrollerMouseMovePreviousY = y;
    }

    /**
    *  Initializes the blob stars 3D scroller effect.
    */
    public void blobStars3DScrollerEffectInit(GL2 gl, BlobStars3DScrollerEffectInitializer blobStars3DScrollerEffectInitializer, boolean useAutoMipmapGeneration)
    {
        this.blobStars3DScrollerEffectInitializer = blobStars3DScrollerEffectInitializer;

        blobStars3DScrollerMouseMoveX = 0;
        blobStars3DScrollerMouseMoveY = 0;
        blobMoveStep = abs(blobMoveStep);

        initBlobStars3DScrollerArrays();
        initBlobStars3DScrollerArraysData();
        initBlobStarDisplayList(gl, useAutoMipmapGeneration);
    }

    /**
    *  Initializes all the stars 3D coordinates arrays.
    */
    private void initBlobStars3DScrollerArrays()
    {
        star3D_X = new float[blobStars3DScrollerEffectInitializer.numberOf3DStars];
        star3D_Y = new float[blobStars3DScrollerEffectInitializer.numberOf3DStars];
        star3D_Z = new float[blobStars3DScrollerEffectInitializer.numberOf3DStars];
        star3D_V = new float[blobStars3DScrollerEffectInitializer.numberOf3DStars];

        screenStarX = new float[blobStars3DScrollerEffectInitializer.numberOf3DStars];
        screenStarY = new float[blobStars3DScrollerEffectInitializer.numberOf3DStars];
        screenStarZ = new float[blobStars3DScrollerEffectInitializer.numberOf3DStars];
    }

    /**
    *  Initializes all the stars 3D coordinates arrays with random values for the blob stars 3Dfield.
    */
    private void initBlobStars3DScrollerArraysData()
    {
        int i = blobStars3DScrollerEffectInitializer.numberOf3DStars;
        while (--i >= 0)
        {
            star3D_X[i] = getRandomRange(0, blobStars3DScrollerEffectInitializer.scSize) - (blobStars3DScrollerEffectInitializer.scSize >> 1);
            star3D_Y[i] = getRandomRange(0, blobStars3DScrollerEffectInitializer.scSize) - (blobStars3DScrollerEffectInitializer.scSize >> 1);
            star3D_Z[i] = getRandomRange(0, blobStars3DScrollerEffectInitializer.scSize) - (blobStars3DScrollerEffectInitializer.scSize >> 1);
            star3D_V[i] = getRandomRange(4, 12);
        }
    }

    /**
    *  Initializes the blob star quad display list.
    */
    private void initBlobStarDisplayList(GL2 gl, boolean useAutoMipmapGeneration)
    {
        // dispose previous blob star texture
        if (blobStarTexture != null) blobStarTexture = null;
        blobStarTexture = DrawTextureSFXs.blobStarTextureGenerate(blobStars3DScrollerEffectInitializer.blobWidth, blobStars3DScrollerEffectInitializer.blobHeight, useAutoMipmapGeneration);

        // if ( gl.glIsList(nodeList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
        gl.glDeleteLists(blobStarDisplayList, 1);

        blobStarDisplayList = gl.glGenLists(1);
        gl.glNewList(blobStarDisplayList, GL_COMPILE);

        TextureCoords tc = blobStarTexture.getImageTexCoords();
        float tx1 = tc.left();
        float ty1 = tc.top();
        float tx2 = tc.right();
        float ty2 = tc.bottom();

        if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
        {
            // Buffer indices = Buffers.newDirectByteBuffer(4).put( new byte[] { 0, 1, 2, 3 } ).rewind();
            // Buffer allTex2DCoordsBuffer = Buffers.newDirectFloatBuffer(8).put( new float[] { tx1, ty2, tx2, ty2, tx2, ty1, tx1, ty1 } ).rewind();
            // Buffer allVertex2DCoordsBuffer = Buffers.newDirectFloatBuffer(8).put( new float[] { -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f } ).rewind();
            Buffer interleavedArrayCoordsBuffer = Buffers.newDirectFloatBuffer(2 * 8 + 4).put( new float[] { tx1, ty2, -1.0f, -1.0f, 0,
                                                                                                          tx2, ty2,  1.0f, -1.0f, 0,
                                                                                                          tx2, ty1,  1.0f,  1.0f, 0,
                                                                                                          tx1, ty1, -1.0f,  1.0f, 0 } ).rewind();

            // gl.glTexCoordPointer(2, GL_FLOAT, 0, allTex2DCoordsBuffer);
            // gl.glVertexPointer(2, GL_FLOAT, 0, allVertex2DCoordsBuffer);
            gl.glInterleavedArrays(GL_T2F_V3F, 0, interleavedArrayCoordsBuffer);
            gl.glDrawArrays(GL_QUADS, 0, 4);
            // gl.glDrawElements(GL_QUADS, 4, GL_UNSIGNED_BYTE, indices);
        }
        else
        {
            gl.glBegin(GL_QUADS);
            gl.glTexCoord2f(tx1, ty2); gl.glVertex2f(-1.0f, -1.0f);	// Bottom Left Of The Texture and Quad
            gl.glTexCoord2f(tx2, ty2); gl.glVertex2f( 1.0f, -1.0f);	// Bottom Right Of The Texture and Quad
            gl.glTexCoord2f(tx2, ty1); gl.glVertex2f( 1.0f,  1.0f);	// Top Right Of The Texture and Quad
            gl.glTexCoord2f(tx1, ty1); gl.glVertex2f(-1.0f,  1.0f);	// Top Left Of The Texture and Quad
            gl.glEnd();
        }

        gl.glEndList();
    }

    /**
    *  Processes the blob stars 3D scroller effect.
    */
    public void blobStars3DScrollerEffect()
    {
        // Move the 3D stars field
        int i = blobStars3DScrollerEffectInitializer.numberOf3DStars;
        while (--i >= 0)
        {
            star3D_Z[i] = star3D_Z[i] - star3D_V[i];
            if (star3D_Z[i] < 16)
            {
                star3D_X[i] = getRandomRange(0, blobStars3DScrollerEffectInitializer.scSize) - (blobStars3DScrollerEffectInitializer.scSize >> 1);
                star3D_Y[i] = getRandomRange(0, blobStars3DScrollerEffectInitializer.scSize) - (blobStars3DScrollerEffectInitializer.scSize >> 1);
                star3D_Z[i] = blobStars3DScrollerEffectInitializer.starDistanceZ;
                star3D_V[i] = getRandomRange(4, 12);
            }
        }

        // 3D Star coordinates to screen coordinates
        i = blobStars3DScrollerEffectInitializer.numberOf3DStars;
        while (--i >= 0)
        {
            screenStarX[i] = ((star3D_X[i] * 256.0f) / star3D_Z[i]);
            screenStarY[i] = ((star3D_Y[i] * 256.0f) / star3D_Z[i]);
            screenStarZ[i] = (blobStars3DScrollerEffectInitializer.starDistanceZ - star3D_Z[i]) / blobStars3DScrollerEffectInitializer.starDistanceZ;
        }
    }

    /**
    *  Renders all the blob stars as quad textures.
    */
    public void blobStars3DScrollerRender(GL2 gl, int width, int height)
    {
        blobStars3DScrollerRender(gl, width, height, 1.0f);
    }

    /**
    *  Renders all the blob stars as quad textures.
    *  Overloaded version to include a star field aplha value.
    */
    public void blobStars3DScrollerRender(GL2 gl, int width, int height, float starFieldAlpha)
    {
        float r = blobStars3DScrollerEffectInitializer.blobColor.getRed()   / 255.0f;
        float g = blobStars3DScrollerEffectInitializer.blobColor.getGreen() / 255.0f;
        float b = blobStars3DScrollerEffectInitializer.blobColor.getBlue()  / 255.0f;

        // Enable blending, using the SrcOver rule
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // determine which areas of the polygon are to be renderered
        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0

        // Use the GL_MODULATE texture function to effectively multiply
        // each pixel in the texture by the current alpha value
        gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

        blobStarTexture.bind(gl);
        blobStarTexture.enable(gl);

        float sx = 0.0f, sy = 0.0f, sz = 0.0f;
        float starAlpha = 0.0f;
        int i = blobStars3DScrollerEffectInitializer.numberOf3DStars;
        while (--i >= 0)
        {
            sx = (screenStarX[i] / 1.5f + blobStars3DScrollerMouseMoveX) + width / 2.0f;
            sy = (screenStarY[i] / 1.5f + blobStars3DScrollerMouseMoveY) + height / 2.0f;
            sz = (screenStarZ[i] * blobStars3DScrollerEffectInitializer.blobScaleSize);
            starAlpha = screenStarZ[i] * starFieldAlpha;

            gl.glPushMatrix();
            gl.glTranslatef(sx, sy, 0.0f);
            gl.glScalef(sz, sz, 0.0f);
            gl.glColor4f(r * starAlpha, g * starAlpha, b * starAlpha, starAlpha);
            gl.glCallList(blobStarDisplayList);
            gl.glPopMatrix();
        }

        blobStarTexture.disable(gl);

        gl.glDisable(GL_ALPHA_TEST);
        gl.glDisable(GL_BLEND);
    }

    /**
    *  Destroys (de-initializes) the effects.
    */
    public void destructor(GL2 gl)
    {
        // if ( gl.glIsList(nodeList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
        gl.glDeleteLists(blobStarDisplayList, 1);

        if (blobStarTexture != null) blobStarTexture = null;
    }


}
