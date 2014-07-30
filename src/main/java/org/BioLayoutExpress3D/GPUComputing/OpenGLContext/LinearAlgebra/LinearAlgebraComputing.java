package org.BioLayoutExpress3D.GPUComputing.OpenGLContext.LinearAlgebra;

import java.nio.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;
import org.BioLayoutExpress3D.GPUComputing.OpenGLContext.*;
import static javax.media.opengl.GL2.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* LinearAlgebraComputing is the main OpenGL context component for Linear Algebra GPU Computing.
*
* @author Dominik Göddeke, Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public class LinearAlgebraComputing extends OpenGLContext
{

    /**
    *  Serial version UID variable for the OpenGLContext class.
    */
    public static final long serialVersionUID = 117222333444555669L;

    private final IntBuffer TEXTURE_ID_X  = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();
    private final IntBuffer TEXTURE_ID_YS = (IntBuffer)Buffers.newDirectIntBuffer(2).put( new int[] { 0 } ).rewind();

    private int writeTexture = 0;
    private int readTexture = 1;

    private LinearAlgebraComputingShaders linearAlgebraComputingShaders = null;

    private boolean benchmarkMode = true;
    private boolean compareResults = true;
    private boolean showDifferentResultsOnly = false;
    private boolean showResults = false;
    private double CPUmflops = 0.0;
    private double GPUmflops = 0.0;

    private int numberOfIterations = 64;
    private int N = 0;

    private FloatBuffer dataX = null;
    private FloatBuffer dataY = null;
    private float alpha = 0.0f;

    /**
    *  The first constructor of the LinearAlgebraComputing class.
    */
    public LinearAlgebraComputing(AllTextureParameters.TextureParameters textureParameters, int textureSize, int problemDomainX, int problemDomainY)
    {
        super(textureParameters, textureSize, problemDomainX, problemDomainY);

        textureParametersReport();
    }

    /**
    *  The second constructor of the LinearAlgebraComputing class.
    */
    public LinearAlgebraComputing(AllTextureParameters.TextureParameters textureParameters, int textureSize, int problemDomainX, int problemDomainY, boolean dialogErrorLog)
    {
        super(textureParameters, textureSize, problemDomainX, problemDomainY, dialogErrorLog);

        textureParametersReport();
    }

    /**
    *  The third constructor of the LinearAlgebraComputing class.
    */
    public LinearAlgebraComputing(AllTextureParameters.TextureParameters textureParameters, int textureSize, int problemDomainX, int problemDomainY, boolean dialogErrorLog, boolean openGLSupportAndExtensionsLog)
    {
        super(textureParameters, textureSize, problemDomainX, problemDomainY, dialogErrorLog, openGLSupportAndExtensionsLog);

        textureParametersReport();
    }

    /**
    *  Texture parameter report.
    */
    private void textureParametersReport()
    {
        // 4 floats per texel for RGBA mode, 1 float  per texel for R LUMINANCE mode
        N = (textureParameters.textureFormat == GL_RGBA) ? 4 * (textureSize * textureSize) : (textureSize * textureSize);

        if (DEBUG_BUILD) println("\nFloat arrays size N: " + N +
                                 "\nNumber Of Iterations: " + numberOfIterations);
        if (DEBUG_BUILD) reportGPUComputingVariables();
    }

    /**
    *  Creates all data arrays.
    */
    private void createDataArrays()
    {
        // create data vectors
        dataX = FloatBuffer.allocate(N);
        dataY = FloatBuffer.allocate(N);
        // and fill with some arbitrary values
        for (int i = 0; i < N; i++)
        {
            dataX.put(2.0f);
            dataY.put(i + 1.0f);
        }
        dataX.rewind();
        dataY.rewind();
        alpha = 1.0f / 9.0f;
    }

    /**
    *  Creates all textures.
    */
    private void createTextures(GL2 gl)
    {
        // create textures
        // y gets two textures, alternatingly read-only and write-only,
        // x is just read-only
        gl.glGenTextures(1, TEXTURE_ID_X);
        gl.glGenTextures(2, TEXTURE_ID_YS);

        // set up textures
        setupTexture( gl, TEXTURE_ID_X.get(0) );
        transferToTexture( gl, dataX, TEXTURE_ID_X.get(0) );

        setupTexture( gl, TEXTURE_ID_YS.get(writeTexture) );
        transferToTexture( gl, dataY, TEXTURE_ID_YS.get(writeTexture) );

        setupTexture( gl, TEXTURE_ID_YS.get(readTexture) );
        transferToTexture( gl, dataY, TEXTURE_ID_YS.get(readTexture) );

        // set texenv mode from modulate (the default) to replace
        gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
    }

    /**
    *  Prints out given results array for debugging purposes.
    *  Float array version.
    */
    private void printArrayResults(float[] data)
    {
        for (int i = 0; i < N; i++)
            println(i + ":\t" + data[i]);
    }

    /**
    *  Prints out given results array for debugging purposes.
    *  FloatBuffer version.
    */
    private void printArrayResults(FloatBuffer data)
    {
        for (int i = 0; i < N; i++)
            println( i + ":\t" + data.get(i) );
    }

    /**
    *  Swaps the role of the two y-textures (read-only and write-only).
    */
    private void swap()
    {
        if (writeTexture == 0)
        {
            writeTexture = 1;
            readTexture = 0;
        }
        else
        {
            writeTexture = 0;
            readTexture = 1;
        }
    }

    /**
    *  Performs the actual calculation.
    */
    private void performComputation(GL2 gl)
    {
        // attach two textures to FBO
        gl.glFramebufferTexture2D(GL_FRAMEBUFFER, ATTACHMENT_POINTS[writeTexture], textureParameters.textureTarget, TEXTURE_ID_YS.get(writeTexture), 0);
        gl.glFramebufferTexture2D(GL_FRAMEBUFFER, ATTACHMENT_POINTS[readTexture], textureParameters.textureTarget, TEXTURE_ID_YS.get(readTexture), 0);

        // check if that worked
        if ( !checkFrameBufferStatus(gl) )
        {
            if (DEBUG_BUILD) println("FBO glFramebufferTexture2DEXT() Status: Failed!");
        }
        else
        {
            if (DEBUG_BUILD) println("FBO glFramebufferTexture2DEXT() Status: Passed!");
        }

        // enable texture x (read-only, not changed in the computation loop) at texture unit 1
        gl.glActiveTexture(GL_TEXTURE1);
        gl.glBindTexture( textureParameters.textureTarget, TEXTURE_ID_X.get(0) );

        if (textureParameters.textureTarget == GL_TEXTURE_2D)
            linearAlgebraComputingShaders.useTexture2DShaderForLinearAlgebra(gl, 1, 0, alpha);
        else
            linearAlgebraComputingShaders.useTextureRectangleShaderForLinearAlgebra(gl, 1, 0, alpha);

        // Calling glFinish() is only neccessary to get accurate timings,
        // and we need a high number of iterations to avoid timing noise.
        gl.glFinish();

        long startTime = System.nanoTime();
        for (int i = 0; i < numberOfIterations; i++)
        {
            // set render destination
            gl.glDrawBuffer(ATTACHMENT_POINTS[writeTexture]);

            // enable texture y_old (read-only)
            gl.glActiveTexture(GL_TEXTURE0);
            gl.glBindTexture( textureParameters.textureTarget, TEXTURE_ID_YS.get(readTexture) );

            // swapped color attachment always binds at texture unit 0
            if (textureParameters.textureTarget == GL_TEXTURE_2D)
                linearAlgebraComputingShaders.useTexture2DShaderForTextureYUniform(gl, 0);
            else
                linearAlgebraComputingShaders.useTextureRectangleShaderForTextureYUniform(gl, 0);

            renderQuad(gl);

            // swap role of the two textures (read-only source becomes
            // write-only target and the other way round):
            swap();
        }

        linearAlgebraComputingShaders.disableLinearAlgebraComputing(gl);

        // done, stop timer, calc MFLOP/s if neccessary
        if (benchmarkMode)
        {
            gl.glFinish();
            long endTime = System.nanoTime();
            double total = (endTime - startTime) / 1000000000.0; // for secs
            if (DEBUG_BUILD) println("\nTotal time taken for GPU calcs: " + total + " secs");
            // calc mflops
            GPUmflops = (2.0 * N * numberOfIterations) / (total * 1000000.0);
            if (DEBUG_BUILD) printf("GPU MFLOP/s:\t\t\t%d\n", (int)GPUmflops);
        }
    }

    /**
    *  Retrieves GPU results, performs and times saxpy on the CPU & compares results
    */
    private void retrieveGPUResultsAndCompareWithCPU(GL2 gl)
    {
        // get GPU results
        FloatBuffer data = FloatBuffer.allocate(N);
        transferFromTexture(gl, ATTACHMENT_POINTS[readTexture], data);
        if (compareResults)
        {
            float[] dataXArray = dataX.array();
            float[] dataYArray = dataY.array();
            // calc on CPU
            long startTime = System.nanoTime();
            for (int n = 0; n < numberOfIterations; n++)
                for (int i = 0; i < N; i++)
                    dataYArray[i] += (alpha * dataXArray[i]);

            long endTime = System.nanoTime();
            double total = (endTime - startTime) / 1000000000.0; // for secs
            if (DEBUG_BUILD) println("\nTotal time taken for CPU calcs: " + total + " secs");
            CPUmflops = (2.0 * N * numberOfIterations) / (total * 1000000.0);
            if (DEBUG_BUILD) printf("CPU MFLOP/s:\t\t\t%d\n", (int)CPUmflops);

            // and compare results
            double maxError = Double.NEGATIVE_INFINITY;
            double avgError = 0.0;
            double difference = 0.0;
            for (int i = 0; i < N; i++)
            {
                difference = Math.abs(data.get(i) - dataYArray[i]);
                if (difference > maxError)
                {
                    maxError = difference;
                    if (DEBUG_BUILD && showDifferentResultsOnly)
                        println( "Difference of " + difference + " between CPU & GPU data:\t" + dataYArray[i] + " & " + data.get(i) );
                }
                avgError += difference;
            }

            avgError /= (double)N;
            if (DEBUG_BUILD)
            {
                printf("\nMax Error: \t\t\t%e\n", maxError);
                printf("Avg Error: \t\t\t%e\n", avgError);
            }

            if (DEBUG_BUILD) println("GPU is " + GPUmflops / CPUmflops + " times faster than the CPU for this calculation.");

            if (showResults)
            {
                if (DEBUG_BUILD)
                {
                    println("\nCPU RESULTS:");
                    printArrayResults(dataYArray);
                }
            }
        }

        if (showResults)
        {
            // print out results
            if (DEBUG_BUILD)
            {
                println("\nGPU RESULTS:");
                printArrayResults(data);
            }
        }

        data.clear();
        data = null;
        System.gc();
    }

    /**
    *  Deletes all data arrays.
    */
    private void deleteDataArrays()
    {
        dataX.clear();
        dataX = null;
        dataY.clear();
        dataY = null;
    }

    /**
    *  Deletes all textures.
    */
    private void deleteTextures(GL2 gl)
    {
        gl.glDeleteTextures(1, TEXTURE_ID_X);
        gl.glDeleteTextures(2, TEXTURE_ID_YS);
    }

    /**
    *  Initializes CPU memory.
    */
    @Override
    protected void initializeCPUMemoryImplementation(GL2 gl) throws OutOfMemoryError
    {
        createDataArrays();
    }

    /**
    *  Initializes GPU memory.
    */
    @Override
    protected void initializeGPUMemoryImplementation(GL2 gl)
    {
        // init shaders runtime
        linearAlgebraComputingShaders = new LinearAlgebraComputingShaders(gl);
        createTextures(gl);
    }

    /**
    *  Performs the GPU Computing calculations.
    */
    @Override
    protected void performGPUComputingCalculationsImplementation(GL2 gl)
    {
        performComputation(gl);
    }

    /**
    *  Retrieves GPU results.
    */
    @Override
    protected void retrieveGPUResultsImplementation(GL2 gl) throws OutOfMemoryError
    {
        retrieveGPUResultsAndCompareWithCPU(gl);
    }

    /**
    *  Deletes the OpenGL context for GPU computing.
    */
    @Override
    protected void deleteOpenGLContextForGPUComputing(GL2 gl)
    {
        deleteDataArrays();
        linearAlgebraComputingShaders.destructor(gl);
        deleteTextures(gl);
    }


}
