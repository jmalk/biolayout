package org.BioLayoutExpress3D.GPUComputing.OpenGLContext.LinearAlgebra;

import javax.media.opengl.*;
import org.BioLayoutExpress3D.Textures.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* LinearAlgebraComputingShaders is the shader class for Linear Algebra GPU Computing.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public class LinearAlgebraComputingShaders
{

    /**
    *  Available shader types.
    */
    public static enum ShaderTypes { LINEARALGEBRATEXTURERECTANGLE, LINEARALGEBRATEXTURE2D }

    /**
    *  Directory of GPU Computing.
    */
    private static final String GPU_COMPUTING_DIRECTORY = "GPUComputing/";

    /**
    *  Directory of shader files.
    */
    private static final String SHADER_FILES_DIRECTORY = "LinearAlgebra";

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
    *  Shader program alpha name.
    */
    private static final String SHADER_PROGRAM_ALPHA_NAME = "alpha";

    /**
    *  Shader program alpha storage.
    */
    private final int[] SHADER_PROGRAM_ALPHAS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Constructor of the LinearAlgebraComputingShaders class.
    */
    public LinearAlgebraComputingShaders(GL2 gl)
    {
        loadAndCompileAllShaderPrograms(gl);
    }

    /**
    *  Loads and compiles all the shader programs.
    */
    private void loadAndCompileAllShaderPrograms(GL2 gl)
    {
        String versionString = (USE_330_SHADERS_PROCESS) ? MINIMUM_GLSL_VERSION_FOR_330_SHADERS + " " + GLSL_LANGUAGE_MODE : MINIMUM_GLSL_VERSION_FOR_120_SHADERS;
        String GLSLPreprocessorCommands = "#version " + versionString + "\n";
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
            SHADER_PROGRAM_ALPHAS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ALPHA_NAME);
        }
    }

    /**
    *  Uses a particular shader texture SFX program.
    */
    private void useProgramAndUniforms(GL2 gl, int effectIndex, int textureX, int textureY, float alpha)
    {
        gl.glUseProgram(SHADER_PROGRAMS[effectIndex]);
        gl.glUniform1i(SHADER_PROGRAM_TEXTURE_XS[effectIndex], textureX);
        gl.glUniform1i(SHADER_PROGRAM_TEXTURE_YS[effectIndex], textureY);
        gl.glUniform1f(SHADER_PROGRAM_ALPHAS[effectIndex], alpha);
    }

    /**
    *  Uses the Texture Rectangle shader for the Linear Algebra computations.
    */
    public void useTextureRectangleShaderForLinearAlgebra(GL2 gl, int textureX, int textureY, float alpha)
    {
        useProgramAndUniforms(gl, ShaderTypes.LINEARALGEBRATEXTURERECTANGLE.ordinal(), textureX, textureY, alpha);
    }

    /**
    *  Uses the Texture 2D shader for the Linear Algebra computations.
    */
    public void useTexture2DShaderForLinearAlgebra(GL2 gl, int textureX, int textureY, float alpha)
    {
        useProgramAndUniforms(gl, ShaderTypes.LINEARALGEBRATEXTURE2D.ordinal(), textureX, textureY, alpha);
    }

    /**
    *  Uses the Texture Rectangle shader for the Linear Algebra computations.
    */
    public void useTextureRectangleShaderForTextureYUniform(GL2 gl, int textureY)
    {
        gl.glUniform1i(SHADER_PROGRAM_TEXTURE_YS[ShaderTypes.LINEARALGEBRATEXTURERECTANGLE.ordinal()], textureY);
    }

    /**
    *  Uses the Texture 2D shader for the Linear Algebra computations.
    */
    public void useTexture2DShaderForTextureYUniform(GL2 gl, int textureY)
    {
        gl.glUniform1i(SHADER_PROGRAM_TEXTURE_YS[ShaderTypes.LINEARALGEBRATEXTURE2D.ordinal()], textureY);
    }

    /**
    *  Disables the Linear Algebra Computing.
    */
    public void disableLinearAlgebraComputing(GL2 gl)
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