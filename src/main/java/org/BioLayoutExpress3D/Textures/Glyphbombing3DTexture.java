package org.BioLayoutExpress3D.Textures;

import java.awt.image.*;
import java.nio.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;
import static javax.media.opengl.GL2.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
*  This class provides 3D texture functionality for the Glyphbombinge GLSL Shader.
*
*
* @see org.BioLayoutExpress3D.Textures.ShaderLightingSFXs
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class Glyphbombing3DTexture
{

    /**
    *  Constant value needed for the glyphbombing texture.
    */
    private static final int NUMBER_OF_BYTES_PER_PIXEL = 4;

    /**
    *  Constant value needed for the glyphbombing texture.
    */
    private static final int MAX_TEXTURE_2D_SLICE_SIZE = 1024;

    /**
    *  Constant value needed for the glyphbombing texture.
    */
    private static final int NUMBER_OF_2D_SLICES = 2;

    /**
    *  Constant value needed for the glyphbombing texture.
    */
    private static final String DIR_NAME = IMAGE_FILES_PATH + "ShaderImages/";

    /**
    *  Constant value needed for the glyphbombing texture.
    */
    private static final String FILE_NAME = "ShaderImagesData.txt";

    /**
    *  Constant value needed for the glyphbombing texture.
    */
    private static final String GLYPH_TEXTURE_NAME = "GlyphMosaic" + MAX_TEXTURE_2D_SLICE_SIZE + "x" + MAX_TEXTURE_2D_SLICE_SIZE;

    /**
    *  TextureLoader reference for the glyphbombing texture.
    */
    private TexturesLoader texturesLoader = null;

    /**
    *  Texture ID reference.
    */
    private final IntBuffer TEXTURE_ID = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();

    /**
    *  Max 3D texture reference.
    */
    private final IntBuffer MAX_3D_TEXTURE_SIZE = Buffers.newDirectIntBuffer(1);

    /**
    *  Curent texture 2D slice size.
    */
    private int currentTexture2DSliceSize = 0;

    /**
    *  The Glyphbombing 3D texture buffer.
    */
    private ByteBuffer glyphbombing3DTextureBuffer = null;

    /**
    *  The Glyphbombing3DTexture constructor.
    */
    public Glyphbombing3DTexture(GL2 gl)
    {
        texturesLoader = new TexturesLoader(DIR_NAME, FILE_NAME, false, false, true, false);

        gl.glGetIntegerv(GL_MAX_3D_TEXTURE_SIZE, MAX_3D_TEXTURE_SIZE);
        if (DEBUG_BUILD) println("For the Glyphbombing3DTexture GL_MAX_3D_TEXTURE_SIZE: " + MAX_3D_TEXTURE_SIZE.get(0));
        currentTexture2DSliceSize = (MAX_3D_TEXTURE_SIZE.get(0) < MAX_TEXTURE_2D_SLICE_SIZE) ? MAX_3D_TEXTURE_SIZE.get(0) : MAX_TEXTURE_2D_SLICE_SIZE;
        glyphbombing3DTextureBuffer = Buffers.newDirectByteBuffer(NUMBER_OF_BYTES_PER_PIXEL * currentTexture2DSliceSize * currentTexture2DSliceSize * NUMBER_OF_2D_SLICES);
    }

    /**
    *  Binds the Glyphbombing 3D texture.
    */
    private void bind(GL2 gl)
    {
        gl.glBindTexture( GL_TEXTURE_3D, TEXTURE_ID.get(0) );
    }

    /**
    *  Binds the Glyphbombing 3D texture with a given active texture unit.
    *  Overloaded version of the method above that selects an active texture unit for the Glyphbombing 3D texture.
    */
    private void bind(GL2 gl, int textureUnit)
    {
        gl.glActiveTexture(GL_TEXTURE0 + textureUnit);
        gl.glBindTexture( GL_TEXTURE_3D, TEXTURE_ID.get(0) );
    }

    /**
    *  Initializes the Glyphbombing 3D texture with a given active texture unit.
    */
    public void initGlyphbombing3DTexture(GL2 gl)
    {
        initGlyphbombing3DTexture(gl, 0);
    }

    /**
    *  Initializes the Glyphbombing 3D texture with a given active texture unit.
    *  Overloaded version of the method above that selects an active texture unit for the Glyphbombing 3D texture.
    */
    public void initGlyphbombing3DTexture(GL2 gl, int textureUnit)
    {
        // allocate the Glyphbombing 3D texture
        gl.glGenTextures(1, TEXTURE_ID);

        if (textureUnit == 0)
            bind(gl);
        else
            bind(gl, textureUnit);

        createRandomPixels();
        // make sure the glyph image is in ARGB format by cloning it in memory
        BufferedImage glyphImage = org.BioLayoutExpress3D.StaticLibraries.ImageProducer.cloneImage( texturesLoader.getImage(GLYPH_TEXTURE_NAME) );
        readPixels( org.BioLayoutExpress3D.StaticLibraries.ImageProducer.resizeImageByGivenRatio(glyphImage, (currentTexture2DSliceSize / (float)MAX_TEXTURE_2D_SLICE_SIZE), true) );
        glyphbombing3DTextureBuffer.flip();

        gl.glTexParameterf(GL_TEXTURE_3D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameterf(GL_TEXTURE_3D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        gl.glTexParameterf(GL_TEXTURE_3D, GL_TEXTURE_WRAP_R, GL_REPEAT);
        gl.glTexParameterf(GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        gl.glTexParameterf(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        gl.glTexImage3D(GL_TEXTURE_3D, 0, GL_RGBA, currentTexture2DSliceSize, currentTexture2DSliceSize, NUMBER_OF_2D_SLICES, 0, GL_RGBA, GL_UNSIGNED_BYTE, glyphbombing3DTextureBuffer);
        if (textureUnit != 0)
            gl.glActiveTexture(GL_TEXTURE0);
    }

    /**
    *  Reads all pixels from given images into the Glyphbombing 3D texture.
    */
    private void createRandomPixels()
    {
        int i = NUMBER_OF_BYTES_PER_PIXEL * currentTexture2DSliceSize * currentTexture2DSliceSize;
        while (--i >= 0)
            glyphbombing3DTextureBuffer.put( (byte)Random.getRandomRange(Byte.MIN_VALUE, Byte.MAX_VALUE) );
    }

    /**
    *  Reads all pixels from given ARGB images into the Glyphbombing 3D texture.
    */
    private void readPixels(BufferedImage image)
    {
        int packedPixel = 0;
        int packedPixels[] = ( (DataBufferInt)image.getRaster().getDataBuffer() ).getData();

        for (int row = currentTexture2DSliceSize - 1; row >= 0; row--)
        {
            for (int col = 0; col < currentTexture2DSliceSize; col++)
            {
                packedPixel = packedPixels[row * currentTexture2DSliceSize + col];
                glyphbombing3DTextureBuffer.put( (byte)( (packedPixel >> 16) & 0xFF) );
                glyphbombing3DTextureBuffer.put( (byte)( (packedPixel >>  8) & 0xFF) );
                glyphbombing3DTextureBuffer.put( (byte)(  packedPixel        & 0xFF) );
                glyphbombing3DTextureBuffer.put( (byte)( (packedPixel >> 24) & 0xFF) );
            }
        }
    }

    /**
    *  Disposes all Glyphbombing 3D texture resources.
    */
    public void disposeAllGlyphbombing3DTextureResources(GL2 gl)
    {
        texturesLoader.destructor(gl);
        //  free the glyphbombing 3D texture
        gl.glDeleteTextures(1, TEXTURE_ID);
        glyphbombing3DTextureBuffer.clear();
    }


}
