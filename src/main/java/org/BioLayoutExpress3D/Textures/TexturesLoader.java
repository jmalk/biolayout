package org.BioLayoutExpress3D.Textures;

import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import javax.media.opengl.GL2;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.awt.ImageUtil;
import org.BioLayoutExpress3D.CPUParallelism.Executors.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.StaticLibraries.ImageProducer.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The images files and images are stored inside the jar file.
* They are conveniently converted to Textures for use with the OpenGL renderer.
*
* TexturesLoader descriptor format:
*
*  <filename>           single image file
*
*  and blank lines and comment lines.
*
* The textures are stored as Texture objects.
*
* @see org.BioLayoutExpress3D.Textures.DrawTextureSFXs
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public class TexturesLoader
{

    /**
    *  The key is the filename prefix, the object (value) is a Texture.
    */
    private AbstractMap<String, Texture> texturesMap = null;

    /**
    *  The key is the filename prefix, the object (value) is a Texture.
    */
    private AbstractMap<String, BufferedImage> imagesMap = null;

    /**
    *  Variable needed for storing the textures directory name.
    */
    private String directoryFilename = "";

    /**
    *  Variable needed for using auto mipmap texture generation.
    */
    private boolean useAutoMipmapGeneration = false;

    /**
    *  Variable needed for using image flipping.
    */
    private boolean useImageFlipping = false;

    /**
    *  Variable needed for storing the buffered image instead.
    */
    private boolean storeBufferedImageInstead = false;

    /**
    *  Variable needed for resizing the texture/image.
    */
    private float resizeValue = 1.0f;

    /**
    *  Variable needed for putting ransparentBorders on the texture/image.
    */
    private float transparentBordersValue = 1.0f;

    /**
    *  Variable needed for loading from file or jar.
    */
    private boolean loadFromFileOrFromJar = false;

    /**
    *  The first constructor of the TexturesLoader class. Initializes all the variables and starts
    *  the loading procedure.
    */
    public TexturesLoader(String directoryFilename, String filename, boolean loadFromFileOrFromJar)
    {
        this(directoryFilename, filename, false, false, false, 1.0f, 1.0f, loadFromFileOrFromJar);
    }

    /**
    *  The second constructor of the TexturesLoader class. Initializes all the variables and starts
    *  the loading procedure.
    */
    public TexturesLoader(String directoryFilename, String filename, boolean useAutoMipmapGeneration, boolean loadFromFileOrFromJar)
    {
        this(directoryFilename, filename, useAutoMipmapGeneration, false, false, 1.0f, 1.0f, loadFromFileOrFromJar);
    }

    /**
    *  The third constructor of the TexturesLoader class. Initializes all the variables and starts
    *  the loading procedure.
    */
    public TexturesLoader(String directoryFilename, String filename, boolean useAutoMipmapGeneration, boolean useImageFlipping, boolean loadFromFileOrFromJar)
    {
        this(directoryFilename, filename, useAutoMipmapGeneration, useImageFlipping, false, 1.0f, 1.0f, loadFromFileOrFromJar);
    }

    /**
    *  The fourth constructor of the TexturesLoader class. Initializes all the variables and starts
    *  the loading procedure.
    */
    public TexturesLoader(String directoryFilename, String filename, boolean useAutoMipmapGeneration, boolean useImageFlipping, boolean storeBufferedImageInstead, boolean loadFromFileOrFromJar)
    {
        this(directoryFilename, filename, useAutoMipmapGeneration, useImageFlipping, storeBufferedImageInstead, 1.0f, 1.0f, loadFromFileOrFromJar);
    }

    /**
    *  The fifth constructor of the TexturesLoader class. Initializes all the variables and starts
    *  the loading procedure.
    */
    public TexturesLoader(String directoryFilename, String filename, boolean useAutoMipmapGeneration, boolean useImageFlipping, boolean storeBufferedImageInstead, float resizeValue, boolean loadFromFileOrFromJar)
    {
        this(directoryFilename, filename, useAutoMipmapGeneration, useImageFlipping, storeBufferedImageInstead, resizeValue, 1.0f, loadFromFileOrFromJar);
    }

    /**
    *  The sixth constructor of the TexturesLoader class. Initializes all the variables and starts
    *  the loading procedure.
    */
    public TexturesLoader(String directoryFilename, String filename, boolean useAutoMipmapGeneration, boolean useImageFlipping, boolean storeBufferedImageInstead, float resizeValue, float transparentBordersValue, boolean loadFromFileOrFromJar)
    {
        this.directoryFilename = directoryFilename;
        this.useAutoMipmapGeneration = useAutoMipmapGeneration;
        this.useImageFlipping = useImageFlipping;
        this.storeBufferedImageInstead = storeBufferedImageInstead;
        this.resizeValue = resizeValue;
        this.transparentBordersValue = transparentBordersValue;
        this.loadFromFileOrFromJar = loadFromFileOrFromJar;

        if (transparentBordersValue < 1.0f)
        {
            if (DEBUG_BUILD) println("The transparentBordersValue has to be at least a value of 1.0f.");
            transparentBordersValue = 1.0f;
        }

        initLoader();
        loadImagesInfoFile(filename);
    }

    /**
    *  Initializes the data structures needed for the loading procedure.
    */
    private void initLoader()
    {
        if (!storeBufferedImageInstead)
            texturesMap = (USE_MULTICORE_PROCESS && storeBufferedImageInstead) ? new ConcurrentHashMap<String, Texture>() : new HashMap<String, Texture>();
        else
            imagesMap = (USE_MULTICORE_PROCESS && storeBufferedImageInstead) ? new ConcurrentHashMap<String, BufferedImage>() : new HashMap<String, BufferedImage>();
    }

    /**
    *  Loads the images info file and parses the available information inside it.
    *   Format:
    *       <filename>          a single image
    *
    *   and blank lines and comment lines.
    */
    private void loadImagesInfoFile(String filename)
    {
        String textureFilename = directoryFilename + filename;
        BufferedReader textureBufferedReader = null;

        if (DEBUG_BUILD)
        {
            println();
            println("Reading file: " + textureFilename);
            println();
        }

        try
        {
            textureBufferedReader = (loadFromFileOrFromJar)
                                    ? new BufferedReader( new FileReader(textureFilename) )
                                    : new BufferedReader( new InputStreamReader( this.getClass().getResourceAsStream(textureFilename) ) );
            String line = "";
            String token = "";
            Scanner scanner = null;
            int numberOfTokens = 0;

            boolean useMultiCoreForImageLoadingOnly = (USE_MULTICORE_PROCESS && storeBufferedImageInstead);
            ExecutorService executorService = (useMultiCoreForImageLoadingOnly) ? Executors.newFixedThreadPool(NUMBER_OF_AVAILABLE_PROCESSORS, new LoggerThreadFactory("loadImagesInfoFile")) : null;
            Collection<Future<?>> futures = (useMultiCoreForImageLoadingOnly) ? new LinkedList<Future<?>>() : null;

            while ( ( line = textureBufferedReader.readLine() ) != null )
            {
                if ( line.isEmpty() )       // blank line
                    continue;
                if ( line.startsWith("#") ) // comment line
                    continue;

                numberOfTokens = line.split("\\s+").length;
                scanner = new Scanner(line);
                token = scanner.next();

                // a single image
                if (useMultiCoreForImageLoadingOnly)
                    futures.add( executorService.submit( getMultiCoreFileNameImage(token, numberOfTokens, line) ) );
                else
                    getFileNameImage(token, numberOfTokens, line);

                scanner.close();
            }

            if (useMultiCoreForImageLoadingOnly)
            {
                try
                {
                    try
                    {
                        for (Future<?> future : futures)
                            future.get();
                    }
                    finally
                    {
                        executorService.shutdown();
                    }
                }
                catch (ExecutionException ex)
                {
                    if (DEBUG_BUILD) println("Problem with thread execution exception in loadImagesInfoFile()!:\n" + ex.getMessage());
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    if (DEBUG_BUILD) println("Problem with thread interrupted exception in loadImagesInfoFile()!:\n" + ex.getMessage());
                }
            }

            textureBufferedReader.close();
        }
        catch (IOException e)
        {
            if (DEBUG_BUILD) println("Error while reading file " + textureFilename + ":\n" + e.getMessage());
        }
        finally
        {
            try
            {
                if (textureBufferedReader != null) textureBufferedReader.close();
            }
            catch (IOException ioe)
            {
                if (DEBUG_BUILD) println("IOException while closing the stream in TexturesLoader.loadImagesInfoFile():\n" + ioe.getMessage());
            }
        }
    }

    /**
    *  Gets the file name of the image and loads it. Used for the format: o <filename>.
    */
    private void getFileNameImage(String token, int numberOfTokens, String line)
    {
        if (numberOfTokens != 1)
        {
            if (DEBUG_BUILD) println("Wrong no. of arguments for " + line);
        }
        else
        {
            if (!storeBufferedImageInstead)
                loadSingleTexture(token);
            else
                loadSingleImage(token);
        }
    }

    /**
    *  Gets the file name of the image and loads it. Used for the format: o <filename>.
    *  MultiCore version of the method above.
    */
    private Runnable getMultiCoreFileNameImage(final String token, final int numberOfTokens, final String line)
    {
        return new Runnable()
        {

            @Override
            public void run()
            {
                getFileNameImage(token, numberOfTokens, line);
            }


        };
    }

    /**
    *  Loads a single image and converts it to a texture.
    */
    private boolean loadSingleTexture(String filename)
    {
        String name = IOUtils.getPrefix(filename);

        if ( texturesMap.containsKey(name) )
        {
            if (DEBUG_BUILD) println("Error: " + name + " already used");

            return false;
        }

        Texture texture = loadTexture(filename);
        if (texture != null)
        {
            texturesMap.put(name, texture);
            if (DEBUG_BUILD) println("Line: Stored " + name + "/" + filename);

            return true;
        }
        else
            return false;
    }

    /**
    *  Load the texture from <filename>, returning it as a Texture.
    */
    private Texture loadTexture(String filename)
    {
        return TextureProducer.createTextureFromBufferedImageAndDeleteOrigContext(loadImage(filename), useAutoMipmapGeneration);
    }

    /**
    *  Loads a single image.
    */
    private boolean loadSingleImage(String filename)
    {
        String name = IOUtils.getPrefix(filename);

        if ( imagesMap.containsKey(name) )
        {
            if (DEBUG_BUILD) println("Error: " + name + " already used");

            return false;
        }

        BufferedImage image = loadImage(filename);
        if (image != null)
        {
            imagesMap.put(name, image);
            if (DEBUG_BUILD) println("Line: Stored " + name + "/" + filename);

            return true;
        }
        else
            return false;
    }

    /**
    *  Load the image from <filename>.
    */
    private BufferedImage loadImage(String filename)
    {
        BufferedImage image = (loadFromFileOrFromJar) ? loadImageFromFile( new File(directoryFilename + filename) ) : loadImageFromURL( this.getClass().getResource(directoryFilename + filename) );
        if (resizeValue != 1.0f)
            image = resizeImageByGivenRatio(image, resizeValue, true);
        if (transparentBordersValue != 1.0f)
            image = createBufferedImageWithTransparentBorders(image, transparentBordersValue);
        if (useImageFlipping)
            ImageUtil.flipImageVertically(image);

        return image;
    }

    //*****ACCESS METHODS FOR ALL THE ABOVE INTERNAL DATA STRUCTURES*****

    /**
    *  Gets the texture associated with <name>.
    */
    public Texture getTexture(String name)
    {
        Texture texture = texturesMap.get(name);
        if (texture == null)
        {
            if (DEBUG_BUILD) println("No texture(s) stored under " + name);

            return TextureProducer.getNullPointerTexture(useAutoMipmapGeneration);
        }

        return texture;
    }

    /**
    *  Gets the image associated with <name>.
    */
    public BufferedImage getImage(String name)
    {
        BufferedImage image = imagesMap.get(name);
        if (image == null)
        {
            if (DEBUG_BUILD) println("No image(s) stored under " + name);

            return createNullPointerBufferedImage();
        }

        return image;
    }

    /**
    *  Returns if there is a <name> key in the texturesMap or imagesMap hashMap.
    */
    public boolean isLoaded(String name)
    {
        return (storeBufferedImageInstead) ? (imagesMap.get(name) != null) : (texturesMap.get(name) != null);
    }

    /**
    *  Returns the total number of textures or images available.
    */
    public int howManyTextures()
    {
        return (storeBufferedImageInstead) ? imagesMap.size() : texturesMap.size();
    }

    /**
    *  Returns all the available texture names.
    */
    public Collection<String> allTextureNames()
    {
        return (storeBufferedImageInstead) ? imagesMap.keySet() : texturesMap.keySet();
    }

    /**
    *  The manual destructor of this class. Has to vbe used to release internal resources of all textures.
    */
    public void destructor(GL2 gl)
    {
        if (DEBUG_BUILD) println("\nNow disposing all textures:");

        if (!storeBufferedImageInstead)
        {
            Texture texture = null;
            Set<String> allStringKeys = texturesMap.keySet();
            for (String stringKey : allStringKeys)
            {
                texture = texturesMap.get(stringKey);
                texture = null;

                if (DEBUG_BUILD) println("Texture " + stringKey + " disposed");
            }
            texturesMap.clear();
            texturesMap = null;
        }
        else
        {
            BufferedImage image = null;
            Set<String> allStringKeys = imagesMap.keySet();
            for (String stringKey : allStringKeys)
            {
                image = imagesMap.get(stringKey);
                image.flush();
                image = null;

                if (DEBUG_BUILD) println("Texture " + stringKey + " disposed");
            }
            imagesMap.clear();
            imagesMap = null;
        }
    }


}
