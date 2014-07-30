package org.BioLayoutExpress3D.Textures;

import javax.media.opengl.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
*  Various GLSL shader line rendering operations used as special effects.
*  This class is responsible for producing line rendering using the GLSL 330 specification (OpenGL 3.30 only).
*
*
* @author Thanos Theo, 2012
* @version 3.0.0.0
*
*/

public class ShaderLinesSFXs extends ShaderLightingSFXs
{

    /**
    *  Available shader types.
    */
    public static enum ShaderTypes { LINE_RENDERING }

    /**
    *  Available number of lighting shaders.
    */
    public static final int NUMBER_OF_AVAILABLE_LINE_SHADERS = ShaderTypes.values().length;

    /**
    *  The constructor of the ShaderLinesSFXs class.
    */
    public ShaderLinesSFXs(GL2 gl)
    {
        super(gl, 0.01f, false, false);

        initAllVariables();
        loadAndCompileAllShaderPrograms(gl);
    }

    /**
    *  Initializes all relevant variables.
    */
    private void initAllVariables()
    {
        SHADER_FILES_DIRECTORY_1 = "Lines";
        VERTEX_SHADERS = new int[NUMBER_OF_AVAILABLE_LINE_SHADERS][3];
        FRAGMENT_SHADERS = new int[NUMBER_OF_AVAILABLE_LINE_SHADERS][3];
        SHADER_PROGRAMS = new int[NUMBER_OF_AVAILABLE_LINE_SHADERS];
    }

    /**
    *  Loads and compiles all the shader programs.
    */
    private void loadAndCompileAllShaderPrograms(GL2 gl)
    {
        String versionString =  MINIMUM_GLSL_VERSION_FOR_330_SHADERS + " " + GLSL_LANGUAGE_MODE;
        String GLSLPreprocessorCommands = "#version " + versionString + "\n";
        ShaderTypes[] allShaderTypes = ShaderTypes.values();
        String shaderEffectName = "";
        String shaderEffectFileName = "";
        for (int i = 0; i < NUMBER_OF_AVAILABLE_LINE_SHADERS; i++)
        {
            shaderEffectFileName = EnumUtils.splitAndCapitalizeFirstCharacters(allShaderTypes[i]);
            shaderEffectName = Character.toLowerCase( shaderEffectFileName.charAt(0) ) + shaderEffectFileName.substring(1);
            ShaderUtils.loadShaderFileCompileAndLinkProgram(gl, new String[] { SHADER_FILES_DIRECTORY_1 }, new String[]{ shaderEffectFileName },
                                                            LOAD_SHADER_PROGRAMS_FROM_EXTERNAL_SOURCE, VERTEX_SHADERS, FRAGMENT_SHADERS, SHADER_PROGRAMS, i, GLSLPreprocessorCommands, DEBUG_BUILD);
        }
    }

    /**
    *  Uses a particular shader program with given texturing & fog variables.
    */
    private void useProgramAndUniforms(GL2 gl, int effectIndex)
    {
        gl.glUseProgram(SHADER_PROGRAMS[effectIndex]);
    }

    /**
    *  Uses the given shader SFX lighting program.
    */
    public void useShaderLinesSFX(GL2 gl, ShaderTypes shaderType)
    {
        useProgramAndUniforms(gl, shaderType.ordinal());
    }

    /**
    *  Destroys (de-initializes) all the effect resources.
    */
    @Override
    public void destructor(GL2 gl)
    {
        for (int i = 0; i < NUMBER_OF_AVAILABLE_LINE_SHADERS; i++)
            ShaderUtils.detachAndDeleteShader(gl, loadShadersPairs, VERTEX_SHADERS, FRAGMENT_SHADERS, SHADER_PROGRAMS, i);
    }


}