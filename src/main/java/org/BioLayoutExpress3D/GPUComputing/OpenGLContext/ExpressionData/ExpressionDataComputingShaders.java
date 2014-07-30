package org.BioLayoutExpress3D.GPUComputing.OpenGLContext.ExpressionData;

import javax.media.opengl.*;
import org.BioLayoutExpress3D.Textures.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* ExpressionDataComputingShaders is the shader class for Expression Data GPU Computing.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public class ExpressionDataComputingShaders
{

    /**
    *  Available shader types.
    */
    public static enum ShaderTypes { EXPRESSIONDATARTEXTURERECTANGLE, EXPRESSIONDATARTEXTURE2D, EXPRESSIONDATARGBATEXTURERECTANGLE, EXPRESSIONDATARGBATEXTURE2D }

    /**
    *  Directory of GPU Computing.
    */
    private static final String GPU_COMPUTING_DIRECTORY = "GPUComputing/";

    /**
    *  Directory of shader files.
    */
    private static final String SHADER_FILES_DIRECTORY = "ExpressionData";

    /**
    *  Available number of shaders.
    */
    private static final int NUMBER_OF_AVAILABLE_SHADERS = ShaderTypes.values().length;

    /**
    *  Vertex shader storage.
    */
    private final int[] VERTEX_SHADERS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Fragmant shader storage.
    */
    private final int[] FRAGMENT_SHADERS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program storage.
    */
    private final int[] SHADER_PROGRAMS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program textureX name.
    */
    private static final String SHADER_PROGRAM_TEXTURE_X_NAME = "textureX";

    /**
    *  Shader program textureX storage.
    */
    private final int[] SHADER_PROGRAM_TEXTURE_XS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program textureY name.
    */
    private static final String SHADER_PROGRAM_TEXTURE_Y_NAME = "textureY";

    /**
    *  Shader program textureY storage.
    */
    private final int[] SHADER_PROGRAM_TEXTURE_YS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program textureSumX_cache name.
    */
    private static final String SHADER_PROGRAM_TEXTURE_SUM_X_CACHE_NAME = "textureSumX_cache";

    /**
    *  Shader program textureSumX_cache storage.
    */
    private final int[] SHADER_PROGRAM_TEXTURE_SUM_X_CACHES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program textureSumX_sumX2_cache name.
    */
    private static final String SHADER_PROGRAM_TEXTURE_SUM_X_SUM_X2_CACHE_NAME = "textureSumX_sumX2_cache";

    /**
    *  Shader program textureSumX_sumX2_cache storage.
    */
    private final int[] SHADER_PROGRAM_TEXTURE_SUM_X_SUM_X2_CACHES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program textureSumColumns_X2_cache name.
    */
    private static final String SHADER_PROGRAM_TEXTURE_SUM_COLUMNS_X2_CACHE_NAME = "textureSumColumns_X2_cache";

    /**
    *  Shader program textureSumColumns_X2_cache storage.
    */
    private final int[] SHADER_PROGRAM_TEXTURE_SUM_COLUMNS_X2_CACHES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program textureExpressionMatrix name.
    */
    private static final String SHADER_PROGRAM_TEXTURE_EXPRESSION_MATRIX_NAME = "textureExpressionMatrix";

    /**
    *  Shader program textureExpressionMatrix storage.
    */
    private final int[] SHADER_PROGRAM_TEXTURE_EXPRESSION_MATRICES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program oneDimensionalExpressionDataConvertedTo2DSquareTextureSize name.
    */
    private static final String SHADER_PROGRAM_ONE_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_NAME = "oneDimensionalExpressionDataConvertedTo2DSquareTextureSize";

    /**
    *  Shader program oneDimensionalExpressionDataConvertedTo2DSquareTextureSize storage.
    */
    private final int[] SHADER_PROGRAM_ONE_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision name.
    */
    private static final String SHADER_PROGRAM_ONE_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_MODULO_DIVISION_NAME = "oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision";

    /**
    *  Shader program oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision storage.
    */
    private final int[] SHADER_PROGRAM_ONE_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_MODULO_DIVISIONS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo name.
    */
    private static final String SHADER_PROGRAM_ONE_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_POWER_OF_TWO_NAME = "oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo";

    /**
    *  Shader program oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo storage.
    */
    private final int[] SHADER_PROGRAM_ONE_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_POWER_OF_TWOS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program twoDimensionalExpressionDataConvertedTo2DSquareTextureSize name.
    */
    private static final String SHADER_PROGRAM_TWO_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_NAME = "twoDimensionalExpressionDataConvertedTo2DSquareTextureSize";

    /**
    *  Shader program twoDimensionalExpressionDataConvertedTo2DSquareTextureSize storage.
    */
    private final int[] SHADER_PROGRAM_TWO_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision name.
    */
    private static final String SHADER_PROGRAM_TWO_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_MODULO_DIVISION_NAME = "twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision";

    /**
    *  Shader program twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision storage.
    */
    private final int[] SHADER_PROGRAM_TWO_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_MODULO_DIVISIONS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo name.
    */
    private static final String SHADER_PROGRAM_TWO_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_POWER_OF_TWO_NAME = "twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo";

    /**
    *  Shader program oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo storage.
    */
    private final int[] SHADER_PROGRAM_TWO_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_POWER_OF_TWOS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program texelCenters name.
    */
    private static final String SHADER_PROGRAM_TEXEL_CENTERS_NAME = "texelCenters";

    /**
    *  Shader program texelCenters storage.
    */
    private final int[] SHADER_PROGRAM_TEXEL_CENTERS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program totalColumns name.
    */
    private static final String SHADER_PROGRAM_TOTAL_COLUMNS_NAME = "totalColumns";

    /**
    *  Shader program totalColumns storage.
    */
    private final int[] SHADER_PROGRAM_TOTAL_COLUMNS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Constructor of the ExpressionDataComputingShaders class.
    */
    public ExpressionDataComputingShaders(GL2 gl, boolean isTwoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo)
    {
        loadAndCompileAllShaderPrograms(gl, isTwoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo);
    }

    /**
    *  Loads and compiles all the shader programs.
    */
    private void loadAndCompileAllShaderPrograms(GL2 gl, boolean isTwoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo)
    {
        String versionString = (USE_330_SHADERS_PROCESS) ? MINIMUM_GLSL_VERSION_FOR_330_SHADERS + " " + GLSL_LANGUAGE_MODE : MINIMUM_GLSL_VERSION_FOR_120_SHADERS;
        String GLSLPreprocessorCommands = "#version " + versionString + "\n" +
                                          "#define TWO_DIMENSIONAL_EXPRESSION_DATA_POWER_OF_TWO " + ( (isTwoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo) ? 1 : 0 ) + "\n";
        ShaderTypes[] allShaderTypes = ShaderTypes.values();
        String shaderEffectName = "";
        String shaderEffectFileName = "";
        for (int i = 0; i < NUMBER_OF_AVAILABLE_SHADERS; i++)
        {
            shaderEffectName = allShaderTypes[i].toString().toLowerCase();
            shaderEffectFileName = Character.toUpperCase( shaderEffectName.charAt(0) ) + shaderEffectName.substring(1);
            ShaderUtils.loadShaderFileCompileAndLinkProgram(gl, GPU_COMPUTING_DIRECTORY + SHADER_FILES_DIRECTORY, shaderEffectFileName, new boolean[]{ false, true },
                                                            LOAD_SHADER_PROGRAMS_FROM_EXTERNAL_SOURCE, VERTEX_SHADERS, FRAGMENT_SHADERS, SHADER_PROGRAMS, i, GLSLPreprocessorCommands, DEBUG_BUILD);
            SHADER_PROGRAM_TEXTURE_XS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_TEXTURE_X_NAME);
            SHADER_PROGRAM_TEXTURE_YS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_TEXTURE_Y_NAME);
            SHADER_PROGRAM_TEXTURE_SUM_X_CACHES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_TEXTURE_SUM_X_CACHE_NAME);
            SHADER_PROGRAM_TEXTURE_SUM_X_SUM_X2_CACHES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_TEXTURE_SUM_X_SUM_X2_CACHE_NAME);
            SHADER_PROGRAM_TEXTURE_SUM_COLUMNS_X2_CACHES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_TEXTURE_SUM_COLUMNS_X2_CACHE_NAME);
            SHADER_PROGRAM_TEXTURE_EXPRESSION_MATRICES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_TEXTURE_EXPRESSION_MATRIX_NAME);
            SHADER_PROGRAM_ONE_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ONE_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_NAME);
            SHADER_PROGRAM_ONE_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_MODULO_DIVISIONS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ONE_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_MODULO_DIVISION_NAME);
            SHADER_PROGRAM_ONE_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_POWER_OF_TWOS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ONE_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_POWER_OF_TWO_NAME);
            SHADER_PROGRAM_TWO_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_TWO_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_NAME);
            SHADER_PROGRAM_TWO_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_MODULO_DIVISIONS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_TWO_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_MODULO_DIVISION_NAME);
            SHADER_PROGRAM_TWO_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_POWER_OF_TWOS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_TWO_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_POWER_OF_TWO_NAME);
            SHADER_PROGRAM_TEXEL_CENTERS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_TEXEL_CENTERS_NAME);
            SHADER_PROGRAM_TOTAL_COLUMNS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_TOTAL_COLUMNS_NAME);
        }
    }

    /**
    *  Uses a particular shader texture SFX program.
    */
    private void useProgramAndUniforms(GL2 gl, int effectIndex, int textureX, int textureY, int textureSumX_cache, int textureSumX_sumX2_cache, int textureSumColumns_X2_cache, int textureExpressionMatrix, int oneDimensionalExpressionDataConvertedTo2DSquareTextureSize, int oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, int oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, int twoDimensionalExpressionDataConvertedTo2DSquareTextureSize, int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, float[] texelCenters, int totalColumns)
    {
        gl.glUseProgram(SHADER_PROGRAMS[effectIndex]);
        gl.glUniform1i(SHADER_PROGRAM_TEXTURE_XS[effectIndex], textureX);
        gl.glUniform1i(SHADER_PROGRAM_TEXTURE_YS[effectIndex], textureY);
        gl.glUniform1i(SHADER_PROGRAM_TEXTURE_SUM_X_CACHES[effectIndex], textureSumX_cache);
        gl.glUniform1i(SHADER_PROGRAM_TEXTURE_SUM_X_SUM_X2_CACHES[effectIndex], textureSumX_sumX2_cache);
        gl.glUniform1i(SHADER_PROGRAM_TEXTURE_SUM_COLUMNS_X2_CACHES[effectIndex], textureSumColumns_X2_cache);
        gl.glUniform1i(SHADER_PROGRAM_TEXTURE_EXPRESSION_MATRICES[effectIndex], textureExpressionMatrix);
        gl.glUniform1i(SHADER_PROGRAM_ONE_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZES[effectIndex], oneDimensionalExpressionDataConvertedTo2DSquareTextureSize);
        gl.glUniform1i(SHADER_PROGRAM_ONE_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_MODULO_DIVISIONS[effectIndex], oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision);
        gl.glUniform1i(SHADER_PROGRAM_ONE_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_POWER_OF_TWOS[effectIndex], oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo);
        if (twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision != 0) gl.glUniform1i(SHADER_PROGRAM_TWO_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_MODULO_DIVISIONS[effectIndex], twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision);
        if (twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo != 0) gl.glUniform1i(SHADER_PROGRAM_TWO_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZE_POWER_OF_TWOS[effectIndex], twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo);
        gl.glUniform1i(SHADER_PROGRAM_TWO_DIMENSIONAL_EXPRESSION_DATA_CONVERTED_TO_2D_SQUARE_TEXTURE_SIZES[effectIndex], twoDimensionalExpressionDataConvertedTo2DSquareTextureSize);
        if (texelCenters != null) gl.glUniform2fv(SHADER_PROGRAM_TEXEL_CENTERS[effectIndex], 1, texelCenters, 0);
        gl.glUniform1i(SHADER_PROGRAM_TOTAL_COLUMNS[effectIndex], totalColumns);
    }

    /**
    *  Uses the R Texture Rectangle shader for the Expression Data computations.
    */
    public void useRTextureRectangleShaderForExpressionData(GL2 gl, int textureX, int textureY, int textureSumX_cache, int textureSumX_sumX2_cache, int textureSumColumns_X2_cache, int textureExpressionMatrix, int oneDimensionalExpressionDataConvertedTo2DSquareTextureSize, int oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, int oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, int twoDimensionalExpressionDataConvertedTo2DSquareTextureSize, int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, int totalColumns)
    {
        useProgramAndUniforms(gl, ShaderTypes.EXPRESSIONDATARTEXTURERECTANGLE.ordinal(), textureX, textureY, textureSumX_cache, textureSumX_sumX2_cache, textureSumColumns_X2_cache, textureExpressionMatrix, oneDimensionalExpressionDataConvertedTo2DSquareTextureSize,  oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, twoDimensionalExpressionDataConvertedTo2DSquareTextureSize, twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, null, totalColumns);
    }

    /**
    *  Uses the R Texture 2D shader for the Expression Data computations.
    */
    public void useRTexture2DShaderForExpressionData(GL2 gl, int textureX, int textureY, int textureSumX_cache, int textureSumX_sumX2_cache, int textureSumColumns_X2_cache, int textureExpressionMatrix, int oneDimensionalExpressionDataConvertedTo2DSquareTextureSize, int oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, int oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, int twoDimensionalExpressionDataConvertedTo2DSquareTextureSize, int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, float[] texelCenters, int totalColumns)
    {
        useProgramAndUniforms(gl, ShaderTypes.EXPRESSIONDATARTEXTURE2D.ordinal(), textureX, textureY, textureSumX_cache, textureSumX_sumX2_cache, textureSumColumns_X2_cache, textureExpressionMatrix, oneDimensionalExpressionDataConvertedTo2DSquareTextureSize,  oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, twoDimensionalExpressionDataConvertedTo2DSquareTextureSize, twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, texelCenters, totalColumns);
    }

    /**
    *  Uses the RGBA Texture Rectangle shader for the Expression Data computations.
    */
    public void useRGBATextureRectangleShaderForExpressionData(GL2 gl, int textureX, int textureY, int textureSumX_cache, int textureSumX_sumX2_cache, int textureSumColumns_X2_cache, int textureExpressionMatrix, int oneDimensionalExpressionDataConvertedTo2DSquareTextureSize, int oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, int oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, int twoDimensionalExpressionDataConvertedTo2DSquareTextureSize, int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, int totalColumns)
    {
        useProgramAndUniforms(gl, ShaderTypes.EXPRESSIONDATARGBATEXTURERECTANGLE.ordinal(), textureX, textureY, textureSumX_cache, textureSumX_sumX2_cache, textureSumColumns_X2_cache, textureExpressionMatrix, oneDimensionalExpressionDataConvertedTo2DSquareTextureSize,  oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, twoDimensionalExpressionDataConvertedTo2DSquareTextureSize, twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, null, totalColumns);
    }

    /**
    *  Uses the RGBA Texture 2D shader for the Expression Data computations.
    */
    public void useRGBATexture2DShaderForExpressionData(GL2 gl, int textureX, int textureY, int textureSumX_cache, int textureSumX_sumX2_cache, int textureSumColumns_X2_cache, int textureExpressionMatrix, int oneDimensionalExpressionDataConvertedTo2DSquareTextureSize, int oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, int oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, int twoDimensionalExpressionDataConvertedTo2DSquareTextureSize, int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, float[] texelCenters, int totalColumns)
    {
        useProgramAndUniforms(gl, ShaderTypes.EXPRESSIONDATARGBATEXTURE2D.ordinal(), textureX, textureY, textureSumX_cache, textureSumX_sumX2_cache, textureSumColumns_X2_cache, textureExpressionMatrix, oneDimensionalExpressionDataConvertedTo2DSquareTextureSize, oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, twoDimensionalExpressionDataConvertedTo2DSquareTextureSize, twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo, texelCenters, totalColumns);
    }

    /**
    *  Disables the Expression Data Computing.
    */
    public void disableExpressionDataComputing(GL2 gl)
    {
        gl.glUseProgram(0);
    }

    /**
    *  Destroys (de-initializes) all shader resources.
    */
    public void destructor(GL2 gl)
    {
        for (int i = 0; i < NUMBER_OF_AVAILABLE_SHADERS; i++)
            ShaderUtils.detachAndDeleteShader(gl, new boolean[]{ false, true }, VERTEX_SHADERS, FRAGMENT_SHADERS, SHADER_PROGRAMS, i);
    }


}
