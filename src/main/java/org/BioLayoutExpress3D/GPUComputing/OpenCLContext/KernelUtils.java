package org.BioLayoutExpress3D.GPUComputing.OpenCLContext;

import java.io.*;
import org.jocl.*;
import static org.jocl.CL.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
*  This class includes kernel related static methods.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public final class KernelUtils
{

    /**
    *  The file path of the kernels file to be copied from.
    */
    private static final String KERNELS_FILE_PATH_1 = "/Resources";

    /**
    *  The file path of the kernels file to be copied from.
    */
    private static final String KERNELS_FILE_PATH_2 = "Kernels/";

    /**
    *  The kernels file name.
    */
    private static final String KERNEL_FILE_NAME = "kernel";

    /**
    *  The kernels file name extension.
    */
    private static final String KERNELS_FILE_NAME_EXTENSION = ".cl";

    /**
    *  Loads a kernel source file, creates the program and the kernel.
    */
    public static void loadKernelFileCreateProgramAndKernel(cl_context context, String pathName, String programSourceName, String kernelName, boolean loadFromFileOrFromJar, cl_program[] programs, cl_kernel[] kernels, int index)
    {
        loadKernelFileCreateProgramAndKernel(context, pathName, programSourceName, kernelName, loadFromFileOrFromJar, programs, kernels, index, "", null);
    }

    /**
    *  Loads a kernel source file, creates the program and the kernel.
    *  Overloaded version of the above function that also supports OpenCL preprocessor commands.
    */
    public static void loadKernelFileCreateProgramAndKernel(cl_context context, String pathName, String programSourceName, String kernelName, boolean loadFromFileOrFromJar, cl_program[] programs, cl_kernel[] kernels, int index, String openCLPreprocessorCommands)
    {
        loadKernelFileCreateProgramAndKernel(context, pathName, programSourceName, kernelName, loadFromFileOrFromJar, programs, kernels, index, openCLPreprocessorCommands, null);
    }

    /**
    *  Loads a kernel source file, creates the program and the kernel.
    *  Overloaded version of the above function that also supports OpenCL preprocessor commands & compile options.
    */
    public static void loadKernelFileCreateProgramAndKernel(cl_context context, String pathName, String programSourceName, String kernelName, boolean loadFromFileOrFromJar, cl_program[] programs, cl_kernel[] kernels, int index, String openCLPreprocessorCommands, String compileOptions)
    {
        // Create the program from the source code
        programs[index] = clCreateProgramWithSource(context, 1, new String[]{ openCLPreprocessorCommands + readKernelFile(pathName, KERNEL_FILE_NAME + programSourceName + KERNELS_FILE_NAME_EXTENSION, loadFromFileOrFromJar) }, null, null);

        // Build the program
        clBuildProgram(programs[index], 0, null, compileOptions, null, null);

        // Create the kernel
        kernels[index] = clCreateKernel(programs[index], kernelName, null);
    }

    /**
    *  Loads a multiple kernel source files, creates the program and the kernel.
    *  Overloaded version of the above function that also supports multiple OpenCL preprocessor commands.
    */
    public static void loadKernelFileCreateProgramAndKernel(cl_context context, String pathName, String[] programSourceNames, String kernelName, boolean loadFromFileOrFromJar, cl_program[] programs, cl_kernel[] kernels, int index, String[] openCLPreprocessorCommands)
    {
        loadKernelFileCreateProgramAndKernel(context, pathName, programSourceNames, kernelName, loadFromFileOrFromJar, programs, kernels, index, openCLPreprocessorCommands, null);
    }

    /**
    *  Loads a multiple kernel source files, creates the program and the kernel.
    *  Overloaded version of the above function that also supports multiple OpenCL preprocessor commands & compile options.
    */
    public static void loadKernelFileCreateProgramAndKernel(cl_context context, String pathName, String[] programSourceNames, String kernelName, boolean loadFromFileOrFromJar, cl_program[] programs, cl_kernel[] kernels, int index, String[] openCLPreprocessorCommands, String compileOptions)
    {
        String[] programSources = new String[programSourceNames.length];
        for (int i = 0; i < programSources.length; i++)
            programSources[i] = openCLPreprocessorCommands[i] + readKernelFile(pathName, KERNEL_FILE_NAME + programSourceNames[i] + KERNELS_FILE_NAME_EXTENSION, loadFromFileOrFromJar);

        // Create the program from the source code
        programs[index] = clCreateProgramWithSource(context, programSources.length, programSources, null, null);

        // Build the program
        clBuildProgram(programs[index], 0, null, compileOptions, null, null);

        // Create the kernel
        kernels[index] = clCreateKernel(programs[index], kernelName, null);
    }

    /**
    *  Reads the kernel file.
    */
    private static String readKernelFile(String pathName, String fileName, boolean loadFromFileOrFromJar)
    {
        BufferedReader br = null;
        StringBuilder returnKernelFileString = new StringBuilder();

        try
        {
            br = new BufferedReader( (loadFromFileOrFromJar) ? new FileReader(KERNELS_FILE_PATH_2 + pathName + "/" + fileName) : new InputStreamReader( KernelUtils.class.getResourceAsStream(KERNELS_FILE_PATH_1 + "/" + KERNELS_FILE_PATH_2 + pathName + "/" + fileName) ) );
            String line = "";
            while ( ( line = br.readLine() ) != null )
              returnKernelFileString.append(line).append("\n");
        }
        catch (IOException ioExc)
        {
            if (DEBUG_BUILD) println("IOException caught in readKernelFile():\n" + ioExc.getMessage());
        }
        finally
        {
            try
            {
                if (br != null) br.close();
            }
            catch (IOException ioExc)
            {
                if (DEBUG_BUILD) println("Not managed to close the kernel file with 'finally' clause in readKernelFile() method:\n" + ioExc.getMessage());
            }
        }

        return returnKernelFileString.toString();
    }

    /**
    *  Releases the kernel & the program.
    */
    public static void releaseKernelAndProgram(cl_program[] programs, cl_kernel[] kernels, int index)
    {
        clReleaseKernel(kernels[index]);
        clReleaseProgram(programs[index]);
    }

    /**
    *  Releases the kernel & the program.
    */
    public static void releaseAllKernelsAndPrograms(cl_program[] programs, cl_kernel[] kernels)
    {
        for (int index = 0; index < kernels.length; index++)
        {
            clReleaseKernel(kernels[index]);
            clReleaseProgram(programs[index]);
        }
    }


}