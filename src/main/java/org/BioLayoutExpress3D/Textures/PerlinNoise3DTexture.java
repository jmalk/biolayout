package org.BioLayoutExpress3D.Textures;

import java.nio.*;
import java.util.*;
import java.util.concurrent.*;
import static java.lang.Math.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;
import static javax.media.opengl.GL2.*;
import org.BioLayoutExpress3D.CPUParallelism.*;
import org.BioLayoutExpress3D.CPUParallelism.Executors.*;
import org.BioLayoutExpress3D.DataStructures.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
*  This class provides Perlin Noise functionality for GLSL Shaders usage through a 3D OpenGL texture.
*
*  Coherent Perlin Noise function over 1, 2 or 3 dimensions (copyright Ken Perlin).
*  Modifications by John Kessenich (GLSL Orange Book & setNoiseFrequency() support).
*  Java 1.6 & N-CP conversion by Thanos Theo.
*
*
* @see org.BioLayoutExpress3D.Textures.ShaderLightingSFXs
* @author Ken Perlin, John Kessenich, Thanos Theo, 2009-2010-2011-2012
* @version 3.0.0.0
*
*/

public final class PerlinNoise3DTexture
{

    /**
    *  Noise 3D number of octaves.
    */
    private static final int NOISE_3D_NUMBER_OF_OCTAVES = 4;

    /**
    *  Noise 3D texture size.
    */
    private static final int NOISE_3D_TEXTURE_SIZE = 128;

    /**
    *  RAND_MAX value from C.
    */
    private static final int RAND_MAX = 1 << 15 - 1; // 32767;

    /**
    *  MAXB value.
    */
    private static final int MAXB = 0x100;

    /**
    *  N value.
    */
    private static final int N = 0x1000;

    /**
    *  Texture ID reference.
    */
    private final IntBuffer TEXTURE_ID = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();

    /**
    *  Random reference.
    */
    private Random random = null;

    /**
    *  The Perlin Noise 3D texture buffer.
    */
    private ByteBuffer perlinNoise3DTextureBuffer = null;

    /**
    *  Constructor of the PerlinNoise3DTexture class.
    */
    public PerlinNoise3DTexture()
    {
        random = new Random(30757);
        perlinNoise3DTextureBuffer = Buffers.newDirectByteBuffer(4 * NOISE_3D_TEXTURE_SIZE * NOISE_3D_TEXTURE_SIZE * NOISE_3D_TEXTURE_SIZE);
    }

    /**
    *  Hermite blending function (or a fifth degree polynomial for the improved version of Perlin Noise so as to have a continuous 2nd derivative, C2 polynomial).
    */
    private double sCurve(double t)
    {
        // return ( t * t * (3.0 - 2.0 * t) );
        return ( t * t * t * (t * (t * 6.0 - 15.0) + 10.0) );
    }

    /**
    *  Linear interpolation (mix (1 - t) * a + t * b ) function.
    */
    private double lerp(double t, double a, double b)
    {
        return a + t * (b - a);
    }

    /**
    *  2D dot product function.
    */
    private double at2(double rx, double ry, double q1, double q2)
    {
        return (rx * q1 + ry * q2);
    }

    /**
    *  3D dot product function.
    */
    private double at3(double rx, double ry, double rz, double q1, double q2, double q3)
    {
        return (rx * q1 + ry * q2 + rz * q3);
    }

    /**
    *  2D normalize function.
    */
    private void normalize2(double[] v)
    {
        double s = sqrt(v[0] * v[0] + v[1] * v[1]);
        v[0] = v[0] / s;
        v[1] = v[1] / s;
    }

    /**
    *  3D normalize function.
    */
    private void normalize3(double[] v)
    {
        double s = sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        v[0] = v[0] / s;
        v[1] = v[1] / s;
        v[2] = v[2] / s;
    }

    /**
    *  Initializes all the Perlin Noise arrays.
    */
    public Tuple4<int[], double[], double[][], double[][]> initPerlinNoise(int frequency, boolean calc2Darray, boolean calc3Darray)
    {
        int[] p = new int[MAXB + MAXB + 2];
        double[] g1 = new double[MAXB + MAXB + 2];
        double[][] g2 = null;
        if (calc2Darray)
            g2 = new double[MAXB + MAXB + 2][2];
        double[][] g3 = null;
        if (calc2Darray && calc3Darray)
            g3 = new double[MAXB + MAXB + 2][3];
        int i = 0, j = 0, k = 0;

        for (i = 0; i < frequency; i++)
        {
            p[i] = i;
            g1[i] = RAND_MAX * (double)( (random.nextInt() % (frequency + frequency) ) - frequency) / frequency;

            if (calc2Darray)
            {
                for (j = 0; j < 2; j++)
                    g2[i][j] = RAND_MAX * (double)( (random.nextInt() % (frequency + frequency) ) - frequency) / frequency;
                        normalize2(g2[i]);
            }

            if (calc2Darray && calc3Darray)
            {
                for (j = 0; j < 3; j++)
                    g3[i][j] = RAND_MAX * (double)( (random.nextInt() % (frequency + frequency) ) - frequency) / frequency;
                normalize3(g3[i]);
            }
        }

        while (--i != 0)
        {
            k = p[i];
            p[i] = p[j = ( RAND_MAX * random.nextInt() ) % frequency];
            p[j] = k;
        }

        for (i = 0; i < frequency + 2; i++)
        {
            p[frequency + i] = p[i];
            g1[frequency + i] = g1[i];

            if (calc2Darray)
                for (j = 0; j < 2; j++)
                    g2[frequency + i][j] = g2[i][j];

            if (calc2Darray && calc3Darray)
                for (j = 0; j < 3; j++)
                    g3[frequency + i][j] = g3[i][j];
        }

        return Tuples.tuple(p, g1, g2, g3);
    }

    /**
    *  1D Perlin Noise function.
    */
    public double perlinNoise1(double xIn, int frequency)
    {
        Tuple4<int[], double[], double[][], double[][]> tuple4 = initPerlinNoise(frequency, false, false);
        int[] p = tuple4.first;
        double[] g1 = tuple4.second;

        return perlinNoise1(xIn, frequency, p, g1);
    }

    /**
    *  1D Perlin Noise function.
    *
    *  Overloaded version where the p/g1 arrays are being passed to the function as parameters.
    */
    public double perlinNoise1(double xIn, int frequency, int[] p, double[] g1)
    {
        int bx0 = 0, bx1 = 0;
        double rx0 = 0.0, rx1 = 0.0, sx = 0.0, t = 0.0, u = 0.0, v = 0.0;

        // setup(0, bx0, bx1, rx0, rx1)
        t = xIn + N;
        bx0 = (int)t & (frequency - 1);
        bx1 = (bx0 + 1) & (frequency - 1);
        rx0 = t - (int)t;
        rx1 = rx0 - 1.0;

        sx = sCurve(rx0);
        u = rx0 * g1[p[bx0]];
        v = rx1 * g1[p[bx1]];

        return lerp(sx, u, v);
    }

    /**
    *  2D Perlin Noise function.
    */
    public double perlinNoise2(double xIn, double yIn, int frequency)
    {
        Tuple4<int[], double[], double[][], double[][]> tuple4 = initPerlinNoise(frequency, true, false);
        int[] p = tuple4.first;
        double[] g1 = tuple4.second;
        double[][] g2 = tuple4.third;

        return perlinNoise2(xIn, yIn, frequency, p, g1, g2);
    }

    /**
    *  2D Perlin Noise function.
    *
    *  Overloaded version where the p/g1/g2 arrays are being passed to the function as parameters.
    */
    public double perlinNoise2(double xIn, double yIn, int frequency, int[] p, double[] g1, double[][] g2)
    {
        int bx0 = 0, bx1 = 0, by0 = 0, by1 = 0, b00 = 0, b10 = 0, b01 = 0, b11 = 0;
        double rx0 = 0.0, rx1 = 0.0, ry0 = 0.0, ry1 = 0.0, sx = 0.0, sy = 0.0, a = 0.0, b = 0.0, t = 0.0, u = 0.0, v = 0.0;
        double q1 = 0.0, q2 = 0.0;
        int i = 0, j = 0;

        // setup(0, bx0, bx1, rx0, rx1)
        t = xIn + N;
        bx0 = (int)t & (frequency - 1);
        bx1 = (bx0 + 1) & (frequency - 1);
        rx0 = t - (int)t;
        rx1 = rx0 - 1.0;

        // setup(1, by0, by1, ry0, ry1)
        t = yIn + N;
        by0 = (int)t & (frequency - 1);
        by1 = (by0 + 1) & (frequency - 1);
        ry0 = t - (int)t;
        ry1 = ry0 - 1.0;

        i = p[bx0];
        j = p[bx1];

        b00 = p[i + by0];
        b10 = p[j + by0];
        b01 = p[i + by1];
        b11 = p[j + by1];

        sx = sCurve(rx0);
        sy = sCurve(ry0);

        q1 = g2[b00][0];
        q2 = g2[b00][1];
        u = at2(rx0, ry0, q1, q2);
        q1 = g2[b10][0];
        q2 = g2[b10][1];
        v = at2(rx1, ry0, q1, q2);
        a = lerp(sx, u, v);

        q1 = g2[b01][0];
        q2 = g2[b01][1];
        u = at2(rx0, ry1, q1, q2);
        q1 = g2[b11][0];
        q2 = g2[b11][1];
        v = at2(rx1, ry1, q1, q2);
        b = lerp(sx, u, v);

        return lerp(sy, a, b);
    }

    /**
    *  3D Perlin Noise function.
    */
    public double perlinNoise3(double xIn, double yIn, double zIn, int frequency)
    {
        Tuple4<int[], double[], double[][], double[][]> tuple4 = initPerlinNoise(frequency, true, true);
        int[] p = tuple4.first;
        double[] g1 = tuple4.second;
        double[][] g2 = tuple4.third;
        double[][] g3 = tuple4.fourth;

        return perlinNoise3(xIn, yIn, zIn, frequency, p, g1, g2, g3);
    }

    /**
    *  3D Perlin Noise function.
    *
    *  Overloaded version where the p/g1/g2/g3 arrays are being passed to the function as parameters.
    */
    public double perlinNoise3(double xIn, double yIn, double zIn, int frequency, int[] p, double[] g1, double[][] g2, double[][] g3)
    {
        int bx0 = 0, bx1 = 0, by0 = 0, by1 = 0, bz0 = 0, bz1 = 0, b00 = 0, b10 = 0, b01 = 0, b11 = 0;
        double rx0 = 0.0, rx1 = 0.0, ry0 = 0.0, ry1 = 0.0, rz0 = 0.0, rz1 = 0.0, sy = 0.0, sz = 0.0, a = 0.0, b = 0.0, c = 0.0, d = 0.0, t = 0.0, u = 0.0, v = 0.0;
        double q1 = 0.0, q2 = 0.0, q3 = 0.0;
        int i = 0, j = 0;

        // setup(0, bx0, bx1, rx0, rx1)
        t = xIn + N;
        bx0 = (int)t & (frequency - 1);
        bx1 = (bx0 + 1) & (frequency - 1);
        rx0 = t - (int)t;
        rx1 = rx0 - 1.0;

        // setup(1, by0, by1, ry0, ry1)
        t = yIn + N;
        by0 = (int)t & (frequency - 1);
        by1 = (by0 + 1) & (frequency - 1);
        ry0 = t - (int)t;
        ry1 = ry0 - 1.0;

        // setup(2, bz0, bz1, rz0, rz1)
        t = zIn + N;
        bz0 = (int)t & (frequency - 1);
        bz1 = (bz0 + 1) & (frequency - 1);
        rz0 = t - (int)t;
        rz1 = rz0 - 1.0;

        i = p[bx0];
        j = p[bx1];

        b00 = p[i + by0];
        b10 = p[j + by0];
        b01 = p[i + by1];
        b11 = p[j + by1];

        t  = sCurve(rx0);
        sy = sCurve(ry0);
        sz = sCurve(rz0);

        q1 = g3[b00 + bz0][0];
        q2 = g3[b00 + bz0][1];
        q3 = g3[b00 + bz0][2];
        u = at3(rx0, ry0, rz0, q1, q2, q3);
        q1 = g3[b10 + bz0][0];
        q2 = g3[b10 + bz0][1];
        q3 = g3[b10 + bz0][2];
        v = at3(rx1, ry0, rz0, q1, q2, q3);
        a = lerp(t, u, v);

        q1 = g3[b01 + bz0][0];
        q2 = g3[b01 + bz0][1];
        q3 = g3[b01 + bz0][2];
        u = at3(rx0, ry1, rz0, q1, q2, q3);
        q1 = g3[b11 + bz0][0];
        q2 = g3[b11 + bz0][1];
        q3 = g3[b11 + bz0][2];
        v = at3(rx1, ry1, rz0, q1, q2, q3);
        b = lerp(t, u, v);

        c = lerp(sy, a, b);

        q1 = g3[b00 + bz1][0];
        q2 = g3[b00 + bz1][1];
        q3 = g3[b00 + bz1][2];
        u = at3(rx0, ry0, rz1, q1, q2, q3);
        q1 = g3[b10 + bz1][0];
        q2 = g3[b10 + bz1][1];
        q3 = g3[b10 + bz1][2];
        v = at3(rx1, ry0, rz1, q1, q2 ,q3);
        a = lerp(t, u, v);

        q1 = g3[b01 + bz1][0];
        q2 = g3[b01 + bz1][1];
        q3 = g3[b01 + bz1][2];
        u = at3(rx0, ry1, rz1, q1, q2, q3);
        q1 = g3[b11 + bz1][0];
        q2 = g3[b11 + bz1][1];
        q3 = g3[b11 + bz1][2];
        v = at3(rx1, ry1, rz1, q1, q2, q3);
        b = lerp(t, u, v);

        d = lerp(sy, a, b);

        return lerp(sz, c, d);
    }

    /**
    *  1D Perlin Noise harmonic summing function.
    *  In what follows "alpha" is the weight when the sum is formed.
    *  Typically it is 2, as this approaches 1 the function is noisier.
    *  "beta" is the harmonic scaling/spacing, typically 2.
    */
    public double perlinNoise1D(double xIn, double alpha, double beta, int n, int frequency)
    {
        Tuple4<int[], double[], double[][], double[][]> tuple4 = initPerlinNoise(frequency, false, false);
        int[] p = tuple4.first;
        double[] g1 = tuple4.second;

        return perlinNoise1D(xIn, alpha, beta, n, frequency, p, g1);
    }

    /**
    *  1D Perlin Noise harmonic summing function.
    *  In what follows "alpha" is the weight when the sum is formed.
    *  Typically it is 2, as this approaches 1 the function is noisier.
    *  "beta" is the harmonic scaling/spacing, typically 2.
    *
    *  Overloaded version where the p/g1 arrays are being passed to the function as parameters.
    */
    public double perlinNoise1D(double xIn, double alpha, double beta, int n, int frequency, int[] p, double[] g1)
    {
        int i = 0;
        double val = 0.0, sum = 0.0;
        double pp1 = 0.0, scale = 1.0;

        pp1 = xIn;
        for (i = 0; i < n; i++)
        {
            val = perlinNoise1(pp1, frequency, p, g1);
            sum += val / scale;
            scale *= alpha;
            pp1 *= beta;
        }

        return sum;
    }

    /**
    *  2D Perlin Noise harmonic summing function.
    *  In what follows "alpha" is the weight when the sum is formed.
    *  Typically it is 2, as this approaches 1 the function is noisier.
    *  "beta" is the harmonic scaling/spacing, typically 2.
    */
    public double perlinNoise2D(double xIn, double yIn, double alpha, double beta, int n, int frequency)
    {
        Tuple4<int[], double[], double[][], double[][]> tuple4 = initPerlinNoise(frequency, true, false);
        int[] p = tuple4.first;
        double[] g1 = tuple4.second;
        double[][] g2 = tuple4.third;

        return perlinNoise2D(xIn, yIn, alpha, beta, n, frequency, p, g1, g2);
    }

    /**
    *  2D Perlin Noise harmonic summing function.
    *  In what follows "alpha" is the weight when the sum is formed.
    *  Typically it is 2, as this approaches 1 the function is noisier.
    *  "beta" is the harmonic scaling/spacing, typically 2.
    *
    *  Overloaded version where the p/g1/g2 arrays are being passed to the function as parameters.
    */
    public double perlinNoise2D(double xIn, double yIn, double alpha, double beta, int n, int frequency, int[] p, double[] g1, double[][] g2)
    {
        int i = 0;
        double val = 0.0, sum = 0.0;
        double scale = 1.0;
        double pp1 = 0.0, pp2 = 0.0;

        pp1 = xIn;
        pp2 = yIn;
        for (i = 0; i < n; i++)
        {
            val = perlinNoise2(pp1, pp2, frequency, p, g1, g2);
            sum += val / scale;
            scale *= alpha;
            pp1 *= beta;
            pp2 *= beta;
        }

        return sum;
    }

    /**
    *  3D Perlin Noise harmonic summing function.
    *  In what follows "alpha" is the weight when the sum is formed.
    *  Typically it is 2, as this approaches 1 the function is noisier.
    *  "beta" is the harmonic scaling/spacing, typically 2.
    */
    public double perlinNoise3D(double xIn, double yIn, double zIn, double alpha, double beta, int n, int frequency)
    {
        Tuple4<int[], double[], double[][], double[][]> tuple4 = initPerlinNoise(frequency, true, true);
        int[] p = tuple4.first;
        double[] g1 = tuple4.second;
        double[][] g2 = tuple4.third;
        double[][] g3 = tuple4.fourth;

        return perlinNoise3D(xIn, yIn, zIn, alpha, beta, n, frequency, p, g1, g2, g3);
    }

    /**
    *  3D Perlin Noise harmonic summing function.
    *  In what follows "alpha" is the weight when the sum is formed.
    *  Typically it is 2, as this approaches 1 the function is noisier.
    *  "beta" is the harmonic scaling/spacing, typically 2.
    *
    *  Overloaded version where the p/g1/g2/g3 arrays are being passed to the function as parameters.
    */
    public double perlinNoise3D(double xIn, double yIn, double zIn, double alpha, double beta, int n, int frequency, int[] p, double[] g1, double[][] g2, double[][] g3)
    {
        int i = 0;
        double val = 0.0, sum = 0.0;
        double scale = 1.0;
        double pp1 = 0.0, pp2 = 0.0, pp3 = 0.0;

        pp1 = xIn;
        pp2 = yIn;
        pp3 = zIn;
        for (i = 0; i < n; i++)
        {
            val = perlinNoise3(pp1, pp2, pp3, frequency, p, g1, g2, g3);
            sum += val / scale;
            scale *= alpha;
            pp1 *= beta;
            pp2 *= beta;
            pp3 *= beta;
        }

        return sum;
    }

    /**
    *  Makes the Perlin Noise 3D texture.
    */
    public void makePerlinNoise3DTexture()
    {
        if (!USE_MULTICORE_PROCESS)
        {
            int f = 0, inc = 0;
            int frequency = 4;
            double amp = 0.5;

            for (f = 0, inc = 0; f < NOISE_3D_NUMBER_OF_OCTAVES; ++f, frequency *= 2, ++inc, amp *= 0.5)
                makePerlinNoise3DTextureCalculation(f, inc, frequency, amp);
        }
        else
        {

            // variables needed for N-CP (up to 4 cores in this case, splitting the 4 frequencies to up to 4 cores)
            int numberOfNCPThreadProcesses = (NUMBER_OF_AVAILABLE_PROCESSORS > NOISE_3D_NUMBER_OF_OCTAVES) ? NOISE_3D_NUMBER_OF_OCTAVES : NUMBER_OF_AVAILABLE_PROCESSORS;
            CyclicBarrierTimer cyclicBarrierTimer = new CyclicBarrierTimer();
            CyclicBarrier threadBarrier = new CyclicBarrier(numberOfNCPThreadProcesses + 1, cyclicBarrierTimer);
            boolean isPowerOfTwo = org.BioLayoutExpress3D.StaticLibraries.Math.isPowerOfTwo(numberOfNCPThreadProcesses);
            LoggerThreadPoolExecutor executor = new LoggerThreadPoolExecutor(numberOfNCPThreadProcesses, numberOfNCPThreadProcesses, 0L, TimeUnit.MILLISECONDS,
                                                                             new LinkedBlockingQueue<Runnable>(numberOfNCPThreadProcesses),
                                                                             new LoggerThreadFactory("PerlinNoise3DTexture"),
                                                                             new ThreadPoolExecutor.CallerRunsPolicy() );

            cyclicBarrierTimer.clear();
            for (int threadId = 0; threadId < numberOfNCPThreadProcesses; threadId++)
                executor.execute( makePerlinNoise3DTextureCalculationProcessKernel(numberOfNCPThreadProcesses, threadBarrier, threadId, isPowerOfTwo) );

            try
            {
                threadBarrier.await(); // wait for all threads to be ready
                threadBarrier.await(); // wait for all threads to finish
                executor.shutdown();
            }
            catch (BrokenBarrierException ex)
            {
                if (DEBUG_BUILD) println("Problem with a broken barrier with the main correlation calculation thread in makePerlinNoise3DTexture()!:\n" + ex.getMessage());
            }
            catch (InterruptedException ex)
            {
                // restore the interuption status after catching InterruptedException
                Thread.currentThread().interrupt();
                if (DEBUG_BUILD) println("Problem with pausing the main correlation calculation thread in makePerlinNoise3DTexture()!:\n" + ex.getMessage());
            }

            if (DEBUG_BUILD) println("\nTotal PerlinNoise3DTexture N-CP run time: " + (cyclicBarrierTimer.getTime() / 1e6) + " ms.\n");
        }
    }

    private void makePerlinNoise3DTextureCalculation(int f, int inc, int frequency, double amp)
    {
        if (DEBUG_BUILD) println("Generating Perlin Noise 3D Texture: Octave " + (f + 1) + "/" + NOISE_3D_NUMBER_OF_OCTAVES + "...");

        int i = 0, j = 0, k = 0;
        int offset = 0;
        double ni1 = 0.0, ni2 = 0.0, ni3 = 0.0;
        double inci = 0.0, incj = 0.0, inck = 0.0;
        Tuple4<int[], double[], double[][], double[][]> tuple4 = initPerlinNoise(frequency, true, true);

        inci = 1.0 / (NOISE_3D_TEXTURE_SIZE / frequency);
        for (i = 0; i < NOISE_3D_TEXTURE_SIZE; ++i, ni1 += inci)
        {
            incj = 1.0 / (NOISE_3D_TEXTURE_SIZE / frequency);
            for (j = 0; j < NOISE_3D_TEXTURE_SIZE; ++j, ni2 += incj)
            {
                inck = 1.0 / (NOISE_3D_TEXTURE_SIZE / frequency);
                for (k = 0; k < NOISE_3D_TEXTURE_SIZE; ++k, ni3 += inck, offset += 4)
                {
                    perlinNoise3DTextureBuffer.put( (offset + inc), (byte)( ( ( perlinNoise3(ni1, ni2, ni3, frequency, tuple4.first, tuple4.second, tuple4.third, tuple4.fourth) + 1.0 ) * amp ) * 128.0 ) );
                    // perlinNoise3DTextureBuffer.put( (offset + inc), (byte)( ( ( SimplexNoise.perlinNoise3(ni1, ni2, ni3) + 1.0 ) * amp ) * 128.0 ) );
                }
            }
        }
    }

    /**
    *   Return a light-weight runnable using the Adapter technique for the correlation calculation so as to avoid any load latencies.
    *   The coding style simulates an OpenCL/CUDA kernel.
    */
    private Runnable makePerlinNoise3DTextureCalculationProcessKernel(final int numberOfNCPThreadProcesses, final CyclicBarrier threadBarrier, final int threadId, final boolean isPowerOfTwo)
    {
        return new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                   threadBarrier.await();
                    try
                    {
                        int f = 0, inc = 0;
                        int frequency = 4;
                        double amp = 0.5;

                        if (isPowerOfTwo)
                        {
                            for (f = 0, inc = 0; f < NOISE_3D_NUMBER_OF_OCTAVES; ++f, frequency *= 2, ++inc, amp *= 0.5)
                                if ( ( f & (numberOfNCPThreadProcesses - 1) ) == threadId )
                                    makePerlinNoise3DTextureCalculation(f, inc, frequency, amp);
                        }
                        else
                        {
                            for (f = 0, inc = 0; f < NOISE_3D_NUMBER_OF_OCTAVES; ++f, frequency *= 2, ++inc, amp *= 0.5)
                                if ( (f % numberOfNCPThreadProcesses) == threadId )
                                    makePerlinNoise3DTextureCalculation(f, inc, frequency, amp);
                        }
                    }
                    finally
                    {
                        threadBarrier.await();
                    }
                }
                catch (BrokenBarrierException ex)
                {
                    if (DEBUG_BUILD) println("Problem with a broken barrier with the N-Core thread with threadId " + threadId + " in makePerlinNoise3DTextureCalculationProcessKernel()!:\n" + ex.getMessage());
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    if (DEBUG_BUILD) println("Problem with pausing the N-Core thread with threadId " + threadId + " in makePerlinNoise3DTextureCalculationProcessKernel()!:\n" + ex.getMessage());
                }
            }


        };
    }

    /**
    *  Binds the Perlin Noise 3D texture.
    */
    private void bind(GL2 gl)
    {
        gl.glBindTexture( GL_TEXTURE_3D, TEXTURE_ID.get(0) );
    }

    /**
    *  Binds the Perlin Noise 3D texture with a given active texture unit.
    *  Overloaded version of the method above that selects an active texture unit for the Perlin Noise 3D texture.
    */
    private void bind(GL2 gl, int textureUnit)
    {
        gl.glActiveTexture(GL_TEXTURE0 + textureUnit);
        gl.glBindTexture( GL_TEXTURE_3D, TEXTURE_ID.get(0) );
    }

    /**
    *  Initializes the Perlin Noise 3D texture.
    */
    public void initPerlinNoise3DTexture(GL2 gl)
    {
        initPerlinNoise3DTexture(gl, 0);
    }

    /**
    *  Initializes the Perlin Noise 3D texture with a given active texture unit.
    *  Overloaded version of the method above that selects an active texture unit for the Perlin Noise 3D texture.
    */
    public void initPerlinNoise3DTexture(GL2 gl, int textureUnit)
    {
        // allocate the Perlin Noise 3D texture
        gl.glGenTextures(1, TEXTURE_ID);

        if (textureUnit == 0)
            bind(gl);
        else
            bind(gl, textureUnit);

        gl.glTexParameterf(GL_TEXTURE_3D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameterf(GL_TEXTURE_3D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        gl.glTexParameterf(GL_TEXTURE_3D, GL_TEXTURE_WRAP_R, GL_REPEAT);
        gl.glTexParameterf(GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        gl.glTexParameterf(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        gl.glTexImage3D(GL_TEXTURE_3D, 0, GL_RGBA, NOISE_3D_TEXTURE_SIZE, NOISE_3D_TEXTURE_SIZE, NOISE_3D_TEXTURE_SIZE, 0, GL_RGBA, GL_UNSIGNED_BYTE, perlinNoise3DTextureBuffer);
        if (textureUnit != 0)
            gl.glActiveTexture(GL_TEXTURE0);
    }

    /**
    *  Disposes all Perlin Noise 3D texture resources.
    */
    public void disposeAllPerlinNoise3DTextureResources(GL2 gl)
    {
        //  free the Perlin Noise 3D texture
        gl.glDeleteTextures(1, TEXTURE_ID);

        random = null;
        perlinNoise3DTextureBuffer.clear();
    }


}
