package org.BioLayoutExpress3D.GPUComputing.OpenCLContext;

import javax.swing.*;
import org.jocl.*;
import static org.jocl.CL.*;
import org.BioLayoutExpress3D.DataStructures.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* OpenCLContext is the main OpenCL context component for GPU Computing.
 *
* @author Marco Hutter, Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public abstract class OpenCLContext
{

    /**
    *  Value needed for the OpenCL GPU Computations.
    */
    private static final int MAXIMUM_SIZE_OF_CL_STRING = 1 << 16; // 65536

    /**
    *  Value needed for the OpenCL GPU Computations.
    */
    protected JFrame jFrame = null;

    /**
    *  Value needed for the OpenCL GPU Computations.
    */
    private boolean CPUErrorOccured = false;

    /**
    *  Value needed for the OpenCL GPU Computations.
    */
    private boolean GPUErrorOccured = false;

    /**
    *  Value needed for error messages displaying.
    */
    private boolean dialogErrorLog = false;

    /**
    *  Value needed for OpenCL support messages displaying.
    */
    private boolean openCLSupportAndExtensionsLogOnly = false;

    /**
    *  Value needed for OpenCL support messages displaying.
    */
    protected boolean profileCommandQueue = false;

    /**
    *  Value needed for OpenCL GPU Computing.
    */
    protected int deviceIndex = 0;

    /**
    *  Value needed for OpenCL GPU Computing.
    */
    protected cl_context context = null;

    /**
    *  Value needed for OpenCL GPU Computing.
    */
    protected cl_command_queue commandQueue = null;

    /**
    *  Value needed for OpenCL GPU Computing.
    */
    protected cl_mem[] memoryObjects = null;

    /**
    *  Value needed for OpenCL GPU Computing.
    */
    protected cl_program[] programs = null;

    /**
    *  Value needed for OpenCL GPU Computing.
    */
    protected cl_kernel[] kernels = null;

    /**
    *  Value needed for OpenCL GPU Computing.
    */
    protected cl_event[][] writeEvents = null;

    /**
    *  Value needed for OpenCL GPU Computing.
    */
    protected cl_event[][] kernelEvents = null;

    /**
    *  Value needed for OpenCL GPU Computing.
    */
    protected cl_event[][] readEvents = null;

    /**
    *  The first constructor of the OpenCLContext class.
    */
    public OpenCLContext(JFrame jFrame)
    {
        this(jFrame, false, false, false);
    }

    /**
    *  The second constructor of the OpenCLContext class.
    */
    public OpenCLContext(JFrame jFrame, boolean dialogErrorLog)
    {
        this(jFrame, dialogErrorLog, false, false);
    }

    /**
    *  The third constructor of the OpenCLContext class.
    */
    public OpenCLContext(JFrame jFrame, boolean dialogErrorLog, boolean profileCommandQueue)
    {
        this(jFrame, dialogErrorLog, profileCommandQueue, false);
    }

    /**
    *  The fourth constructor of the OpenCLContext class.
    */
    public OpenCLContext(JFrame jFrame, boolean dialogErrorLog, boolean profileCommandQueue, boolean openCLSupportAndExtensionsLogOnly)
    {
        this.jFrame = jFrame;
        this.dialogErrorLog = dialogErrorLog;
        this.profileCommandQueue = profileCommandQueue;
        this.openCLSupportAndExtensionsLogOnly = openCLSupportAndExtensionsLogOnly;
    }

    /**
    *  Starts the OpenCL context and the GPU Computing processing.
    */
    public void startGPUComputingProcessing()
    {
        startGPUComputingProcessing(false);
    }

    /**
    *  Starts the OpenCL context and the GPU Computing processing.
       Overloaded version that invokes the GPU Computing Processing within a thread.
    */
    public void startGPUComputingProcessing(boolean invokeInThread)
    {
        if (invokeInThread)
        {
            Thread runLightWeightThread = new Thread( new Runnable()
            {

                @Override
                public void run()
                {
                    initializeAndStartGPUComputingProcessing();
                }


            } );

            runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
            runLightWeightThread.start();
        }
        else
            initializeAndStartGPUComputingProcessing();
    }

    /**
    *  Called by the JOGL immediately after the OpenCL context is initialized.
    */
    private void initializeAndStartGPUComputingProcessing()
    {
        if (openCLSupportAndExtensionsLogOnly)
        {
            checkOpenCLPlatformSupport();
            return;
        }

        try
        {
            Tuple2<Boolean, String> tuple2 = initializeOpenCLContext();
            if (tuple2.first)
            {
                initializeOpenCLContextInfoAndCommandQueue();
                initializeCPUMemory();
                if (!CPUErrorOccured)
                {
                    initializeGPUMemory();
                    performGPUComputingCalculations();
                    retrieveGPUResults();
                }
                deleteOpenCLContext(true);
            }
            else
            {
                if (dialogErrorLog)
                    JOptionPane.showMessageDialog(jFrame, "OpenCL GPU Initialization error:\n" + tuple2.second, "OpenCL GPU Initialization Error", JOptionPane.WARNING_MESSAGE);
                if (DEBUG_BUILD) println("OpenCL GPU Initialization error:\n" + tuple2.second);

                GPUErrorOccured = true;
            }

            if (DEBUG_BUILD) println( "\nOpenCL GPU Computing overal error status: " + ( getErrorOccured() ? "Errors occured." : "No errors occured." ) );
        }
        catch (CLException CLex)
        {
            deleteOpenCLContext(false);

            if (dialogErrorLog)
                JOptionPane.showMessageDialog(jFrame, "OpenCL reported a CLException: " + CLex.getMessage(), "OpenCL CLException: " + CLex.getMessage(), JOptionPane.WARNING_MESSAGE);
            if (DEBUG_BUILD) println("OpenCL reported a CLException: " + CLex.getMessage());

            GPUErrorOccured = true;
        }
    }

    /**
    *  Checks the OpenCL support for all platforms & devices.
    */
    private void checkOpenCLPlatformSupport()
    {
        setExceptionsEnabled(false);

        // Obtain the number of platforms
        int[] numberOfPlatforms = new int[1];
        clGetPlatformIDs(0, null, numberOfPlatforms);
        if (DEBUG_BUILD) println("\nNumber of platforms: " + numberOfPlatforms[0]);

        // Obtain the platform IDs
        cl_platform_id[] platforms = new cl_platform_id[numberOfPlatforms[0]];
        clGetPlatformIDs(platforms.length, platforms, null);

        if (CL_ALL_PLATFORM_NAMES == null)
        {
            CL_ALL_PLATFORM_NAMES = new String[platforms.length];
            CL_IS_PLATFORM_AMD_ATI = new boolean[platforms.length];
            CL_ALL_PLATFORM_DEVICE_IDS = new cl_device_id[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_NAMES = new String[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_VENDORS = new String[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_DRIVER_VERSIONS = new String[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_VERSIONS = new String[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_OPENCL_C_VERSIONS = new String[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_PROFILES = new String[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_TYPES = new String[platforms.length][][];
            CL_ALL_PLATFORM_DEVICES_ENDIAN_LITTLES = new int[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_MAX_COMPUTE_UNITS = new int[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_DIMENSIONS = new long[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_SIZES = new int[platforms.length][][];
            CL_ALL_PLATFORM_DEVICES_MAX_WORK_GROUP_SIZES = new long[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_MAX_CLOCK_FREQUENCIES = new long[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_ADDRESSES_BITS = new int[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_MAX_MEM_ALLOC_SIZES = new long[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_GLOBAL_MEM_SIZES = new long[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_ERROR_CORRECTIONS_SUPPORT = new int[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_LOCAL_MEM_TYPES = new int[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_LOCAL_MEM_SIZES = new long[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_MAX_CONSTANT_BUFFER_SIZES = new long[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_QUEUES_PROPERTIES = new String[platforms.length][][];
            CL_ALL_PLATFORM_DEVICES_IMAGES_SUPPORT = new int[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_CL_DEVICE_MAX_SAMPLERS = new int[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_MAX_READ_IMAGES_ARGS = new int[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_MAX_WRITE_IMAGES_ARGS = new int[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_IMAGE_2D_MAX_WIDTHS = new int[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_IMAGE_2D_MAX_HEIGHTS = new int[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_WIDTHS = new int[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_HEIGHTS = new int[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_DEPTHS = new int[platforms.length][];
            CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS = new int[platforms.length][][];
            CL_ALL_PLATFORM_DEVICES_SINGLE_FP_CONFIGS = new String[platforms.length][][];
            CL_ALL_PLATFORM_DEVICES_EXECUTION_CAPABILITIES = new String[platforms.length][][];
            CL_ALL_PLATFORM_DEVICES_EXTENSIONS = new String[platforms.length][][];
        }

        // Collect all devices of all platforms
        int[] numberOfDevices = new int[1];
        for (int i = 0; i < platforms.length; i++)
        {
            CL_ALL_PLATFORM_NAMES[i] = getString(platforms[0], CL_PLATFORM_NAME);
            CL_IS_PLATFORM_AMD_ATI[i] = ( CL_ALL_PLATFORM_NAMES[i].contains("ATI") || CL_ALL_PLATFORM_NAMES[i].contains("AMD") );

            // Obtain the number of devices for the current platform
            clGetDeviceIDs(platforms[i], CL_DEVICE_TYPE_ALL, 0, null, numberOfDevices);

            if (DEBUG_BUILD) println("Number of devices in platform " + CL_ALL_PLATFORM_NAMES[i] + ": " + numberOfDevices[0]);

            CL_ALL_PLATFORM_DEVICE_IDS[i] = new cl_device_id[numberOfDevices[0]];
            clGetDeviceIDs(platforms[i], CL_DEVICE_TYPE_ALL, numberOfDevices[0], CL_ALL_PLATFORM_DEVICE_IDS[i], null);

            if (CL_ALL_PLATFORM_DEVICES_NAMES[0] == null)
            {
                CL_ALL_PLATFORM_DEVICES_NAMES[i] = getAllDevicesNames(CL_ALL_PLATFORM_DEVICE_IDS[i]);
                CL_ALL_PLATFORM_DEVICES_VENDORS[i] = new String[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_DRIVER_VERSIONS[i] = new String[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_VERSIONS[i] = new String[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_OPENCL_C_VERSIONS[i] = new String[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_PROFILES[i] = new String[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_TYPES[i] = new String[CL_ALL_PLATFORM_DEVICES_NAMES[i].length][4];
                CL_ALL_PLATFORM_DEVICES_ENDIAN_LITTLES[i] = new int[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_MAX_COMPUTE_UNITS[i] = new int[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_DIMENSIONS[i] = new long[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_SIZES[i] = new int[CL_ALL_PLATFORM_DEVICES_NAMES[i].length][];
                CL_ALL_PLATFORM_DEVICES_MAX_WORK_GROUP_SIZES[i] = new long[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_MAX_CLOCK_FREQUENCIES[i] = new long[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_ADDRESSES_BITS[i] = new int[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_MAX_MEM_ALLOC_SIZES[i] = new long[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_GLOBAL_MEM_SIZES[i] = new long[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_ERROR_CORRECTIONS_SUPPORT[i] = new int[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_LOCAL_MEM_TYPES[i] = new int[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_LOCAL_MEM_SIZES[i] = new long[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_MAX_CONSTANT_BUFFER_SIZES[i] = new long[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_QUEUES_PROPERTIES[i] = new String[CL_ALL_PLATFORM_DEVICES_NAMES[i].length][2];
                CL_ALL_PLATFORM_DEVICES_IMAGES_SUPPORT[i] = new int[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_CL_DEVICE_MAX_SAMPLERS[i] = new int[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_MAX_READ_IMAGES_ARGS[i] = new int[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_MAX_WRITE_IMAGES_ARGS[i] = new int[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_IMAGE_2D_MAX_WIDTHS[i] = new int[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_IMAGE_2D_MAX_HEIGHTS[i] = new int[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_WIDTHS[i] = new int[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_HEIGHTS[i] = new int[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_DEPTHS[i] = new int[CL_ALL_PLATFORM_DEVICES_NAMES[i].length];
                CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[i] = new int[CL_ALL_PLATFORM_DEVICES_NAMES[i].length][6];
                CL_ALL_PLATFORM_DEVICES_SINGLE_FP_CONFIGS[i] = new String[CL_ALL_PLATFORM_DEVICES_NAMES[i].length][];
                CL_ALL_PLATFORM_DEVICES_EXECUTION_CAPABILITIES[i] = new String[CL_ALL_PLATFORM_DEVICES_NAMES[i].length][2];
                CL_ALL_PLATFORM_DEVICES_EXTENSIONS[i] = new String[CL_ALL_PLATFORM_DEVICES_NAMES[i].length][];
            }

            for (int j = 0; j < CL_ALL_PLATFORM_DEVICE_IDS[i].length; j++)
                reportOpenCLDeviceCaps(i, j, CL_ALL_PLATFORM_DEVICE_IDS[i][j]);
        }
    }

    /**
    *  Reports the OpenCL support for the given device.
    */
    private void reportOpenCLDeviceCaps(int platformIndex, int deviceIndex, cl_device_id device)
    {
        // CL_DEVICE_NAME
        // String deviceName = getString(device, CL_DEVICE_NAME);
        if (DEBUG_BUILD)
        {
            println("\n--- Information for device " + CL_ALL_PLATFORM_DEVICES_NAMES[platformIndex][deviceIndex] + ": ---");
            printf("CL_DEVICE_NAME:\t\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_NAMES[platformIndex][deviceIndex]);
        }

        // CL_DEVICE_VENDOR
        CL_ALL_PLATFORM_DEVICES_VENDORS[platformIndex][deviceIndex] = getString(device, CL_DEVICE_VENDOR);
        if (DEBUG_BUILD) printf("CL_DEVICE_VENDOR:\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_VENDORS[platformIndex][deviceIndex]);

        // CL_DRIVER_VERSION
        CL_ALL_PLATFORM_DEVICES_DRIVER_VERSIONS[platformIndex][deviceIndex] = getString(device, CL_DRIVER_VERSION);
        if (DEBUG_BUILD) printf("CL_DRIVER_VERSION:\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_DRIVER_VERSIONS[platformIndex][deviceIndex]);

        // CL_DEVICE_VERSION
        CL_ALL_PLATFORM_DEVICES_VERSIONS[platformIndex][deviceIndex] = getString(device, CL_DEVICE_VERSION);
        if (DEBUG_BUILD) printf("CL_DEVICE_VERSION:\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_VERSIONS[platformIndex][deviceIndex]);

        // CL_DEVICE_OPENCL_C_VERSION
        CL_ALL_PLATFORM_DEVICES_OPENCL_C_VERSIONS[platformIndex][deviceIndex] = getString(device, CL_DEVICE_OPENCL_C_VERSION).trim();
        if ( CL_ALL_PLATFORM_DEVICES_OPENCL_C_VERSIONS[platformIndex][deviceIndex].isEmpty() ) CL_ALL_PLATFORM_DEVICES_OPENCL_C_VERSIONS[platformIndex][deviceIndex] = "Device OpenCL C Version Information Not Available";
        if (DEBUG_BUILD) printf("CL_DEVICE_OPENCL_C_VERSION:\t\t%s\n", CL_ALL_PLATFORM_DEVICES_OPENCL_C_VERSIONS[platformIndex][deviceIndex]);

        // CL_DEVICE_PROFILE
        CL_ALL_PLATFORM_DEVICES_PROFILES[platformIndex][deviceIndex] = getString(device, CL_DEVICE_PROFILE);
        if (DEBUG_BUILD) printf("CL_DEVICE_PROFILE:\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_PROFILES[platformIndex][deviceIndex]);

        // CL_DEVICE_TYPE
        long deviceType = getLong(device, CL_DEVICE_TYPE);
        if ( (deviceType & CL_DEVICE_TYPE_CPU) != 0 )
        {
            CL_ALL_PLATFORM_DEVICES_TYPES[platformIndex][deviceIndex][0] = "CL_DEVICE_TYPE_CPU";
            if (DEBUG_BUILD) printf("CL_DEVICE_TYPE:\t\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_TYPES[platformIndex][deviceIndex][0]);
        }
        if ( (deviceType & CL_DEVICE_TYPE_GPU) != 0 )
        {
            CL_ALL_PLATFORM_DEVICES_TYPES[platformIndex][deviceIndex][1] = "CL_DEVICE_TYPE_GPU";
            if (DEBUG_BUILD) printf("CL_DEVICE_TYPE:\t\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_TYPES[platformIndex][deviceIndex][1]);
        }
        if ( (deviceType & CL_DEVICE_TYPE_ACCELERATOR) != 0 )
        {
            CL_ALL_PLATFORM_DEVICES_TYPES[platformIndex][deviceIndex][2] = "CL_DEVICE_TYPE_ACCELERATOR";
            if (DEBUG_BUILD) printf("CL_DEVICE_TYPE:\t\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_TYPES[platformIndex][deviceIndex][2]);
        }
        if( (deviceType & CL_DEVICE_TYPE_DEFAULT) != 0 )
        {
            CL_ALL_PLATFORM_DEVICES_TYPES[platformIndex][deviceIndex][3] = "CL_DEVICE_TYPE_DEFAULT";
            if (DEBUG_BUILD) printf("CL_DEVICE_TYPE:\t\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_TYPES[platformIndex][deviceIndex][3]);
        }

        // CL_DEVICE_ENDIAN_LITTLE
        CL_ALL_PLATFORM_DEVICES_ENDIAN_LITTLES[platformIndex][deviceIndex] = getInt(device, CL_DEVICE_ENDIAN_LITTLE);
        if (DEBUG_BUILD) printf("CL_DEVICE_ENDIAN_LITTLE:\t\t%s\n", (CL_ALL_PLATFORM_DEVICES_ENDIAN_LITTLES[platformIndex][deviceIndex] != 0) ? "YES" : "NO");

        // CL_DEVICE_MAX_COMPUTE_UNITS
        CL_ALL_PLATFORM_DEVICES_MAX_COMPUTE_UNITS[platformIndex][deviceIndex] = getInt(device, CL_DEVICE_MAX_COMPUTE_UNITS);
        if (DEBUG_BUILD) printf("CL_DEVICE_MAX_COMPUTE_UNITS:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_MAX_COMPUTE_UNITS[platformIndex][deviceIndex]);

        // CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS
        CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_DIMENSIONS[platformIndex][deviceIndex] = getLong(device, CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS);
        if (DEBUG_BUILD) printf("CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS:\t%d\n", CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_DIMENSIONS[platformIndex][deviceIndex]);

        // CL_DEVICE_MAX_WORK_ITEM_SIZES
        CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_SIZES[platformIndex][deviceIndex] = (IS_64BIT) ? convertLongToIntArray( getLongs(device, CL_DEVICE_MAX_WORK_ITEM_SIZES, 3) ) : getInts(device, CL_DEVICE_MAX_WORK_ITEM_SIZES, 3);
        if (DEBUG_BUILD) printf("CL_DEVICE_MAX_WORK_ITEM_SIZES:\t\t%d / %d / %d \n", CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_SIZES[platformIndex][deviceIndex][0],
                                                                                     CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_SIZES[platformIndex][deviceIndex][1],
                                                                                     CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_SIZES[platformIndex][deviceIndex][2]);

        // CL_DEVICE_MAX_WORK_GROUP_SIZE
        CL_ALL_PLATFORM_DEVICES_MAX_WORK_GROUP_SIZES[platformIndex][deviceIndex] = getLong(device, CL_DEVICE_MAX_WORK_GROUP_SIZE);
        if (DEBUG_BUILD) printf("CL_DEVICE_MAX_WORK_GROUP_SIZE:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_MAX_WORK_GROUP_SIZES[platformIndex][deviceIndex]);

        // CL_DEVICE_MAX_CLOCK_FREQUENCY
        CL_ALL_PLATFORM_DEVICES_MAX_CLOCK_FREQUENCIES[platformIndex][deviceIndex] = getLong(device, CL_DEVICE_MAX_CLOCK_FREQUENCY);
        if (DEBUG_BUILD) printf("CL_DEVICE_MAX_CLOCK_FREQUENCY:\t\t%d MHz\n", CL_ALL_PLATFORM_DEVICES_MAX_CLOCK_FREQUENCIES[platformIndex][deviceIndex]);

        // CL_DEVICE_ADDRESS_BITS
        CL_ALL_PLATFORM_DEVICES_ADDRESSES_BITS[platformIndex][deviceIndex] = getInt(device, CL_DEVICE_ADDRESS_BITS);
        if (DEBUG_BUILD) printf("CL_DEVICE_ADDRESS_BITS:\t\t\t%d\n", CL_ALL_PLATFORM_DEVICES_ADDRESSES_BITS[platformIndex][deviceIndex]);

        // CL_DEVICE_MAX_MEM_ALLOC_SIZE
        CL_ALL_PLATFORM_DEVICES_MAX_MEM_ALLOC_SIZES[platformIndex][deviceIndex] = getLong(device, CL_DEVICE_MAX_MEM_ALLOC_SIZE);
        if (DEBUG_BUILD) printf("CL_DEVICE_MAX_MEM_ALLOC_SIZE:\t\t%d MByte\n", (int)(CL_ALL_PLATFORM_DEVICES_MAX_MEM_ALLOC_SIZES[platformIndex][deviceIndex] / (1024 * 1024)));

        // CL_DEVICE_GLOBAL_MEM_SIZE
        CL_ALL_PLATFORM_DEVICES_GLOBAL_MEM_SIZES[platformIndex][deviceIndex] = getLong(device, CL_DEVICE_GLOBAL_MEM_SIZE);
        if (DEBUG_BUILD) printf("CL_DEVICE_GLOBAL_MEM_SIZE:\t\t%d MByte\n", (int)(CL_ALL_PLATFORM_DEVICES_GLOBAL_MEM_SIZES[platformIndex][deviceIndex] / (1024 * 1024)));

        // CL_DEVICE_ERROR_CORRECTION_SUPPORT
        CL_ALL_PLATFORM_DEVICES_ERROR_CORRECTIONS_SUPPORT[platformIndex][deviceIndex] = getInt(device, CL_DEVICE_ERROR_CORRECTION_SUPPORT);
        if (DEBUG_BUILD) printf("CL_DEVICE_ERROR_CORRECTION_SUPPORT:\t%s\n", (CL_ALL_PLATFORM_DEVICES_ERROR_CORRECTIONS_SUPPORT[platformIndex][deviceIndex] != 0) ? "YES" : "NO");

        // CL_DEVICE_LOCAL_MEM_TYPE
        CL_ALL_PLATFORM_DEVICES_LOCAL_MEM_TYPES[platformIndex][deviceIndex] = getInt(device, CL_DEVICE_LOCAL_MEM_TYPE);
        if (DEBUG_BUILD) printf("CL_DEVICE_LOCAL_MEM_TYPE:\t\t%s\n", (CL_ALL_PLATFORM_DEVICES_LOCAL_MEM_TYPES[platformIndex][deviceIndex] == 1) ? "LOCAL" : "GLOBAL");

        // CL_DEVICE_LOCAL_MEM_SIZE
        CL_ALL_PLATFORM_DEVICES_LOCAL_MEM_SIZES[platformIndex][deviceIndex] = getLong(device, CL_DEVICE_LOCAL_MEM_SIZE);
        if (DEBUG_BUILD) printf("CL_DEVICE_LOCAL_MEM_SIZE:\t\t%d KByte\n", (int)(CL_ALL_PLATFORM_DEVICES_LOCAL_MEM_SIZES[platformIndex][deviceIndex] / 1024));

        // CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE
        CL_ALL_PLATFORM_DEVICES_MAX_CONSTANT_BUFFER_SIZES[platformIndex][deviceIndex] = getLong(device, CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE);
        if (DEBUG_BUILD) printf("CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE:\t%d KByte\n", (int)(CL_ALL_PLATFORM_DEVICES_MAX_CONSTANT_BUFFER_SIZES[platformIndex][deviceIndex] / 1024));

        // CL_DEVICE_QUEUE_PROPERTIES
        long queueProperties = getLong(device, CL_DEVICE_QUEUE_PROPERTIES);
        if ( ( queueProperties & CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE ) != 0 )
        {
            CL_ALL_PLATFORM_DEVICES_QUEUES_PROPERTIES[platformIndex][deviceIndex][0] = "CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE";
            if (DEBUG_BUILD) printf("CL_DEVICE_QUEUE_PROPERTIES:\t\t%s\n", CL_ALL_PLATFORM_DEVICES_QUEUES_PROPERTIES[platformIndex][deviceIndex][0]);
        }
        if ( (queueProperties & CL_QUEUE_PROFILING_ENABLE) != 0 )
        {
            CL_ALL_PLATFORM_DEVICES_QUEUES_PROPERTIES[platformIndex][deviceIndex][1] = "CL_QUEUE_PROFILING_ENABLE";
            if (DEBUG_BUILD) printf("CL_DEVICE_QUEUE_PROPERTIES:\t\t%s\n", CL_ALL_PLATFORM_DEVICES_QUEUES_PROPERTIES[platformIndex][deviceIndex][1]);
        }

        // CL_DEVICE_IMAGE_SUPPORT
        CL_ALL_PLATFORM_DEVICES_IMAGES_SUPPORT[platformIndex][deviceIndex] = getInt(device, CL_DEVICE_IMAGE_SUPPORT);
        if (DEBUG_BUILD) printf("CL_DEVICE_IMAGE_SUPPORT:\t\t%s\n", (CL_ALL_PLATFORM_DEVICES_IMAGES_SUPPORT[platformIndex][deviceIndex] != 0) ? "YES" : "NO");

        // CL_DEVICE_MAX_SAMPLERS
        CL_ALL_PLATFORM_DEVICES_CL_DEVICE_MAX_SAMPLERS[platformIndex][deviceIndex] = getInt(device, CL_DEVICE_MAX_SAMPLERS);
        if (DEBUG_BUILD) printf("CL_DEVICE_MAX_SAMPLERS:\t\t\t%d\n", CL_ALL_PLATFORM_DEVICES_CL_DEVICE_MAX_SAMPLERS[platformIndex][deviceIndex]);

        // CL_DEVICE_MAX_READ_IMAGE_ARGS
        CL_ALL_PLATFORM_DEVICES_MAX_READ_IMAGES_ARGS[platformIndex][deviceIndex] = getInt(device, CL_DEVICE_MAX_READ_IMAGE_ARGS);
        if (DEBUG_BUILD) printf("CL_DEVICE_MAX_READ_IMAGE_ARGS:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_MAX_READ_IMAGES_ARGS[platformIndex][deviceIndex]);

        // CL_DEVICE_MAX_WRITE_IMAGE_ARGS
        CL_ALL_PLATFORM_DEVICES_MAX_WRITE_IMAGES_ARGS[platformIndex][deviceIndex] = getInt(device, CL_DEVICE_MAX_WRITE_IMAGE_ARGS);
        if (DEBUG_BUILD) printf("CL_DEVICE_MAX_WRITE_IMAGE_ARGS:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_MAX_WRITE_IMAGES_ARGS[platformIndex][deviceIndex]);

        // CL_DEVICE_IMAGE2D_MAX_WIDTH
        CL_ALL_PLATFORM_DEVICES_IMAGE_2D_MAX_WIDTHS[platformIndex][deviceIndex] = (IS_64BIT) ? (int)getLong(device, CL_DEVICE_IMAGE2D_MAX_WIDTH) : getInt(device, CL_DEVICE_IMAGE2D_MAX_WIDTH);
        if (DEBUG_BUILD) printf("CL_DEVICE_IMAGE2D_MAX_WIDTH:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_IMAGE_2D_MAX_WIDTHS[platformIndex][deviceIndex]);

        // CL_DEVICE_IMAGE2D_MAX_HEIGHT
        CL_ALL_PLATFORM_DEVICES_IMAGE_2D_MAX_HEIGHTS[platformIndex][deviceIndex] = (IS_64BIT) ? (int)getLong(device, CL_DEVICE_IMAGE2D_MAX_HEIGHT) : getInt(device, CL_DEVICE_IMAGE2D_MAX_HEIGHT);
        if (DEBUG_BUILD) printf("CL_DEVICE_IMAGE2D_MAX_HEIGHT:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_IMAGE_2D_MAX_HEIGHTS[platformIndex][deviceIndex]);

        // CL_DEVICE_IMAGE3D_MAX_WIDTH
        CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_WIDTHS[platformIndex][deviceIndex] = (IS_64BIT) ? (int)getLong(device, CL_DEVICE_IMAGE3D_MAX_WIDTH) : getInt(device, CL_DEVICE_IMAGE3D_MAX_WIDTH);
        if (DEBUG_BUILD) printf("CL_DEVICE_IMAGE3D_MAX_WIDTH:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_WIDTHS[platformIndex][deviceIndex]);

        // CL_DEVICE_IMAGE3D_MAX_HEIGHT
        CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_HEIGHTS[platformIndex][deviceIndex] = (IS_64BIT) ? (int)getLong(device, CL_DEVICE_IMAGE3D_MAX_HEIGHT) : getInt(device, CL_DEVICE_IMAGE3D_MAX_HEIGHT);
        if (DEBUG_BUILD) printf("CL_DEVICE_IMAGE3D_MAX_HEIGHT:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_HEIGHTS[platformIndex][deviceIndex]);

        // CL_DEVICE_IMAGE3D_MAX_DEPTH
        CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_DEPTHS[platformIndex][deviceIndex] = (IS_64BIT) ? (int)getLong(device, CL_DEVICE_IMAGE3D_MAX_DEPTH) : getInt(device, CL_DEVICE_IMAGE3D_MAX_DEPTH);
        if (DEBUG_BUILD) printf("CL_DEVICE_IMAGE3D_MAX_DEPTH\t\t%d\n", CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_DEPTHS[platformIndex][deviceIndex]);

        // CL_DEVICE_PREFERRED_VECTOR_WIDTH_<type>
        if (DEBUG_BUILD) printf("CL_DEVICE_PREFERRED_VECTOR_WIDTH:\t");
        CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][0] = getInt(device, CL_DEVICE_PREFERRED_VECTOR_WIDTH_CHAR);
        CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][1] = getInt(device, CL_DEVICE_PREFERRED_VECTOR_WIDTH_SHORT);
        CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][2] = getInt(device, CL_DEVICE_PREFERRED_VECTOR_WIDTH_INT);
        CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][3] = getInt(device, CL_DEVICE_PREFERRED_VECTOR_WIDTH_LONG);
        CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][4] = getInt(device, CL_DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT);
        CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][5] = getInt(device, CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE);
        if (DEBUG_BUILD) printf("CHAR \t%d,\n\t\t\t\t\tSHORT \t%d,\n\t\t\t\t\tINT \t%d,\n\t\t\t\t\tLONG \t%d,\n\t\t\t\t\tFLOAT \t%d,\n\t\t\t\t\tDOUBLE \t%d\n",
                                CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][0],
                                CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][1],
                                CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][2],
                                CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][3],
                                CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][4],
                                CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][5]);

        // CL_DEVICE_SINGLE_FP_CONFIG
        CL_ALL_PLATFORM_DEVICES_SINGLE_FP_CONFIGS[platformIndex][deviceIndex] = stringFor_cl_device_fp_config( getLong(device, CL_DEVICE_SINGLE_FP_CONFIG) ).split("\\s+");
        if (DEBUG_BUILD)
            for (int i = 0; i < CL_ALL_PLATFORM_DEVICES_SINGLE_FP_CONFIGS[platformIndex][deviceIndex].length; i++)
                printf( (i == 0) ? "CL_DEVICE_SINGLE_FP_CONFIG:\t\t%s\n" : "\t\t\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_SINGLE_FP_CONFIGS[platformIndex][deviceIndex][i] );

        // CL_DEVICE_EXECUTION_CAPABILITIES
        CL_ALL_PLATFORM_DEVICES_EXECUTION_CAPABILITIES[platformIndex][deviceIndex] = stringFor_cl_device_exec_capabilities( getLong(device, CL_DEVICE_EXECUTION_CAPABILITIES) ).split("\\s+");
        if (DEBUG_BUILD)
            for (int i = 0; i < CL_ALL_PLATFORM_DEVICES_EXECUTION_CAPABILITIES[platformIndex][deviceIndex].length; i++)
                printf( (i == 0) ? "CL_DEVICE_EXECUTION_CAPABILITIES:\t%s\n" : "\t\t\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_EXECUTION_CAPABILITIES[platformIndex][deviceIndex][i] );

        // CL_DEVICE_EXTENSIONS
        CL_ALL_PLATFORM_DEVICES_EXTENSIONS[platformIndex][deviceIndex] = getString(device, CL_DEVICE_EXTENSIONS).toUpperCase().split("\\s+");
        if (DEBUG_BUILD)
            for (int i = 0; i < CL_ALL_PLATFORM_DEVICES_EXTENSIONS[platformIndex][deviceIndex].length; i++)
                printf( (i == 0) ? "CL_DEVICE_EXTENSIONS:\t\t\t%s\n" : "\t\t\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_EXTENSIONS[platformIndex][deviceIndex][i] );
    }

    /**
    *  Returns the value of the device info parameter with the given name
    *
    *  @param device The device
    *  @param parameterName The parameter name
    *  @return The value
    */
    private int getInt(cl_device_id device, int parameterName)
    {
        return getInts(device, parameterName, 1)[0];
    }

    /**
    *  Returns the values of the device info parameter with the given name
    *
    *  @param device The device
    *  @param parameterName The parameter name
    *  @param numberOfValues The number of values
    *  @return The value
    */
    private int[] getInts(cl_device_id device, int parameterName, int numberOfValues)
    {
        int[] values = new int[numberOfValues];
        clGetDeviceInfo(device, parameterName, Sizeof.cl_int * numberOfValues, Pointer.to(values), null);

        return values;
    }

    /**
    *  Returns the value of the device info parameter with the given name
    *
    *  @param device The device
    *  @param parameterName The parameter name
    *  @return The value
    */
    private long getLong(cl_device_id device, int parameterName)
    {
        return getLongs(device, parameterName, 1)[0];
    }

    /**
    *  Returns the values of the device info parameter with the given name
    *
    *  @param device The device
    *  @param parameterName The parameter name
    *  @param numberOfValues The number of values
    *  @return The value
    */
    private long[] getLongs(cl_device_id device, int parameterName, int numberOfValues)
    {
        long[] values = new long[numberOfValues];
        clGetDeviceInfo(device, parameterName, Sizeof.cl_long * numberOfValues, Pointer.to(values), null);

        return values;
    }

    /**
    *  Returns the value of the device info parameter with the given name
    *
    *  @param device The device
    *  @param parameterName The parameter name
    *  @return The value
    */
    private String getString(cl_device_id device, int parameterName)
    {
        // Obtain the length of the string that will be queried
        long[] size = new long[1];
        clGetDeviceInfo(device, parameterName, 0, null, size);
        if (size[0] <= 0)
            return "";
        else if (size[0] > MAXIMUM_SIZE_OF_CL_STRING)
            size[0] = MAXIMUM_SIZE_OF_CL_STRING;

        // Create a buffer of the appropriate size and fill it with the info
        byte[] buffer = new byte[(int)size[0]];
        clGetDeviceInfo(device, parameterName, buffer.length, Pointer.to(buffer), null);

        // Create a string from the buffer (excluding the trailing \0 byte)
        return (buffer.length <= 0) ? "" : new String(buffer, 0, buffer.length - 1);
    }

    /**
    *  Returns the value of the platform info parameter with the given name
    *
    *  @param platform The platform
    *  @param parameterName The parameter name
    *  @return The value
    */
    private String getString(cl_platform_id platform, int parameterName)
    {
        // Obtain the length of the string that will be queried
        long[] size = new long[1];
        clGetPlatformInfo(platform, parameterName, 0, null, size);
        if (size[0] <= 0)
            return "";
        else if (size[0] > MAXIMUM_SIZE_OF_CL_STRING)
            size[0] = MAXIMUM_SIZE_OF_CL_STRING;

        // Create a buffer of the appropriate size and fill it with the info
        byte[] buffer = new byte[(int)size[0]];
        clGetPlatformInfo(platform, parameterName, buffer.length, Pointer.to(buffer), null);

        // Create a string from the buffer (excluding the trailing \0 byte)
        return (buffer.length <= 0) ? "" : new String(buffer, 0, buffer.length - 1);
    }

    /**
    *  Converts a long array to an integer array.
    */
    private int[] convertLongToIntArray(long[] longArray)
    {
        int[] intArray = new int[longArray.length];
        for (int i = 0; i < intArray.length; i++)
            intArray[i] = (int)longArray[i];

        return intArray;
    }

    /**
    *  Returns a String array of names for the given devicesArray.
    */
    private String[] getAllDevicesNames(cl_device_id[] devicesArray)
    {
        // CL_DEVICE_NAME
        String[] devicesNames = new String[devicesArray.length];
        for (int i = 0; i < devicesArray.length; i++)
            devicesNames[i] = getString(devicesArray[i], CL_DEVICE_NAME);

        return devicesNames;
    }

    /**
    *  Initializes the OpenCL context.
    */
    private Tuple2<Boolean, String> initializeOpenCLContext()
    {
        setExceptionsEnabled(true);

        cl_platform_id[] platforms = new cl_platform_id[1];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platforms[0]);

        // Create an OpenCL context on a GPU device
        context = clCreateContextFromType(contextProperties, CL_DEVICE_TYPE_GPU, null, null, null);
        if (context == null)
        {
            if (DEBUG_BUILD) println("Unable to create a GPU context");

            // If no context for a GPU device could be created,
            // try to create one for a CPU device.
            context = clCreateContextFromType(contextProperties, CL_DEVICE_TYPE_CPU, null, null, null);

            if (context == null)
                return Tuples.tuple(false, "Unable to create a GPU or a CPU context.");
            else
                return Tuples.tuple(true, "Created a CPU context.");
        }
        else
            return Tuples.tuple(true, "Created a GPU context.");
    }

    /**
    *  Initializes the OpenCL context.
    */
    private void initializeOpenCLContextInfoAndCommandQueue()
    {
        // Get the list of GPU devices associated with the context
        long[] numberOfBytes = new long[1];
        clGetContextInfo(context, CL_CONTEXT_DEVICES, 0, null, numberOfBytes);

        // Obtain the cl_device_id for the first device
        int numberOfDevices = (int)numberOfBytes[0] / Sizeof.cl_device_id;
        cl_device_id[] devices = new cl_device_id[numberOfDevices];
        clGetContextInfo(context, CL_CONTEXT_DEVICES, numberOfBytes[0], Pointer.to(devices), null);

        for (int i = 0; i < CL_ALL_PLATFORM_DEVICE_IDS[0].length; i++)
        {
            if ( CL_ALL_PLATFORM_DEVICE_IDS[0][i].equals(devices[0]) )
            {
                deviceIndex = i;
                if (DEBUG_BUILD) println("\nDeviceIndex for the chosen CL device: " + deviceIndex);
            }
        }

        // Create a command-queue, with profiling info enabled
        long properties = 0;
        if (profileCommandQueue)
        {
            properties |= CL_QUEUE_PROFILING_ENABLE;
            if ( CL_ALL_PLATFORM_DEVICES_QUEUES_PROPERTIES[0][deviceIndex][0] != null &&
                 CL_ALL_PLATFORM_DEVICES_QUEUES_PROPERTIES[0][deviceIndex][0].equals("CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE") )
                properties |= CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE;
        }
        commandQueue = clCreateCommandQueue(context, devices[0], properties, null);
    }

    /**
    *  Initializes CPU memory.
    */
    private void initializeCPUMemory()
    {
        try
        {
            initializeCPUMemoryImplementation();
        }
        catch (OutOfMemoryError memErr)
        {
            if (dialogErrorLog)
                JOptionPane.showMessageDialog(jFrame, "Java reported an Out Of Memory error: " + memErr.getMessage() + "!\nPoint of error: initializeCPUMemory()", "Java Out Of Memory Error", JOptionPane.WARNING_MESSAGE);
            if (DEBUG_BUILD) println("Java reported an Out Of Memory error: " + memErr.getMessage() + "!\nPoint of error: initializeCPUMemory()");

            CPUErrorOccured = true;
        }
    }

    /**
    *  Retrieves GPU results.
    */
    private void retrieveGPUResults() throws CLException
    {
        try
        {
            retrieveGPUResultsImplementation();
        }
        catch (CLException CLex)
        {
            throw CLex;
        }
        catch (OutOfMemoryError memErr)
        {
            if (dialogErrorLog)
                JOptionPane.showMessageDialog(jFrame, "Java reported an Out Of Memory error: " + memErr.getMessage() + "!\nPoint of error: retrieveGPUResults()", "Java Out Of Memory Error", JOptionPane.WARNING_MESSAGE);
            if (DEBUG_BUILD) println("Java reported an Out Of Memory error: " + memErr.getMessage() + "!\nPoint of error: retrieveGPUResults()");

            CPUErrorOccured = true;
        }
    }

    /**
    *  Deletes the OpenCL context.
    */
    private void deleteOpenCLContext(boolean reThrowCLException) throws CLException
    {
        try
        {
            deleteOpenCLContextForGPUComputing();

            if (commandQueue != null)
                clReleaseCommandQueue(commandQueue);
            if (context != null)
                clReleaseContext(context);
        }
        catch (CLException CLex)
        {
            if (reThrowCLException)
                throw CLex;
            else
                if (DEBUG_BUILD) println("CLException with deleteOpenCLContext(): " + CLex.getMessage());
        }
        catch (Exception ex)
        {
            if (DEBUG_BUILD) println("Exception with deleteOpenCLContext(): " + ex.getMessage());
        }
    }

    /**
    *  Gets any occurences of OpenCL errors.
    */
    public boolean getErrorOccured()
    {
        return (CPUErrorOccured || GPUErrorOccured);
    }


    // Abstract methods from here on

    /**
    *  Called by an implementing subclass for initializing the CPU memory.
    */
    protected abstract void initializeCPUMemoryImplementation() throws OutOfMemoryError;

    /**
    *  Called by an implementing subclass for initializing the GPU memory.
    */
    protected abstract void initializeGPUMemory() throws CLException;

    /**
    *  Called by an implementing subclass for performing the GPU Computing calculations.
    */
    protected abstract void performGPUComputingCalculations() throws CLException;

    /**
    *  Called by an implementing subclass for retrieving GPU results.
    */
    protected abstract void retrieveGPUResultsImplementation() throws CLException, OutOfMemoryError;

    /**
    *  Called by an implementing subclass for deleting the OpenCL context for GPU computing.
    */
    protected abstract void deleteOpenCLContextForGPUComputing() throws CLException;


}