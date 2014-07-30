package org.BioLayoutExpress3D.Models.Loaders.OBJModelLoader;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.media.opengl.*;
import javax.swing.*;
import com.jogamp.opengl.util.texture.*;
import static org.BioLayoutExpress3D.Models.Loaders.OBJModelLoader.OBJModelLoader.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
*  This class does two main tasks:
*     * it loads the material details from the MTL file, storing
*       them as Material objects in the materialsMap HashMap.
*
*     * it sets up a specified material's colours or textures
*       to be used when rendering -- see drawWithMaterial()
*
* @author Andrew Davison, 2007, rewrite for BioLayout Express3D by Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class Materials
{

    /**
    *  Stores the Material objects built from the MTL file data.
    */
    private HashMap<String, Material> materialsMap = null;

    /**
    *  Stores the current render material name.
    */
    private String drawnMaterialName = "";

    /**
    *  The usingTexture variable.
    */
    private boolean usingTexture = false;

    private String materialFilename;
    private boolean loadFromFileOrFromJar;

    /**
    *  The Materials class constructor.
    */
    public Materials(String materialFilename, boolean loadFromFileOrFromJar)
    {
        materialsMap = new HashMap<String, Material>();
        this.materialFilename = materialFilename;
        this.loadFromFileOrFromJar = loadFromFileOrFromJar;
    }

    /*
    *  Parses the MTL file line-by-line, building Material
    *  objects which are collected in the materialsMap ArrayList.
    */
    public boolean parse()
    {
        BufferedReader materialBufferedReader = null;
        String line = "";
        Material currentMaterial = null;

        try
        {
            materialBufferedReader = (loadFromFileOrFromJar)
                                    ? new BufferedReader( new FileReader(materialFilename) )
                                    : new BufferedReader( new InputStreamReader( this.getClass().getResourceAsStream(materialFilename) ) );

            while ( ( line = materialBufferedReader.readLine() ) != null)
            {
                line = line.trim();
                if (line.length() == 0)
                    continue;

                if ( line.startsWith("newmtl ") ) // new material
                {
                    if (currentMaterial != null) // save previous material
                        materialsMap.put(currentMaterial.getMaterialName(), currentMaterial);

                    // start collecting info for new material
                    currentMaterial = new Material( line.substring(7) );
                }
                else if ( line.startsWith("map_Kd ") ) // texture filename
                {
                    String textureFilename = MODEL_FILES_PATH + line.substring(7);
                    currentMaterial.loadTexture(textureFilename);
                }
                else if ( line.startsWith("Ka ") ) // ambient colour
                    currentMaterial.setKa( readPoint3D(line) );
                else if ( line.startsWith("Kd ") ) // diffuse colour
                    currentMaterial.setKd( readPoint3D(line) );
                else if ( line.startsWith("Ks ") ) // specular colour
                    currentMaterial.setKs( readPoint3D(line) );
                else if ( line.startsWith("Ns ") ) // shininess
                    currentMaterial.setNs( Float.valueOf( line.substring(3) ).floatValue() );
                else if ( line.charAt(0) == 'd' || line.startsWith("Tr ") ) // alpha
                    currentMaterial.setD( Float.valueOf( line.substring(2) ).floatValue() );
                else if ( line.startsWith("illum ") )  // illumination model
                {
                    // not implemented
                }
                else if (line.charAt(0) == '#')   // comment line
                    continue;
                else
                    if (DEBUG_BUILD) println("Ignoring MTL line: " + line);
          }

            if (currentMaterial != null)
                materialsMap.put(currentMaterial.getMaterialName(), currentMaterial);
        }
        catch (IOException ioExc)
        {
            if (DEBUG_BUILD)
            {
                println("IOException while parsing the material file " + materialFilename + " in Materials.parseMaterialFile(): " + ioExc.getMessage());
            }

            return false;
        }
        finally
        {
            try
            {
                if (materialBufferedReader != null) materialBufferedReader.close();
            }
            catch (IOException ioe)
            {
                if (DEBUG_BUILD) println("IOException while closing the stream in Materials.parseMaterialFile():\n" + ioe.getMessage());
            }
        }

        return true;
    }


    // ----------------- using a material at render time -----------------

    /*
    *  Checks drawing using the texture or colours associated with the
    *  material, faceMaterial. But only change things if faceMaterial is
    *  different from the current rendering material, whose name
    *  is stored in drawnMaterialName.
    *
    *  Returns the texture if needed to texturise the material.
    */
    public Texture checkDrawWithMaterial(String faceMaterial)
    {
        Texture texture = null;
        if ( !faceMaterial.equals(drawnMaterialName) ) // is faceMaterial is a new material?
        {
            // store current faceMaterial
            drawnMaterialName = faceMaterial;

            // set up new rendering material
            texture = getTexture(drawnMaterialName);
            usingTexture = (texture != null);
        }

        return texture;
    }

    /*
    *  Draws using the texture or colours associated with the
    *  material, faceMaterial. But only change things if faceMaterial is
    *  different from the current rendering material, whose name
    *  is stored in drawnMaterialName.
    *
    *  Returns the texture if needed to texturise the material.
    */
    public Texture drawWithMaterial(GL2 gl, String faceMaterial, boolean useMaterialColors)
    {
        Texture texture = null;
        if ( !faceMaterial.equals(drawnMaterialName) ) // is faceMaterial is a new material?
        {
            // store current faceMaterial
            drawnMaterialName = faceMaterial;

            // set up new rendering material
            texture = getTexture(drawnMaterialName);
            if (texture != null) // switch on the material's texture
                switchOnTexture(gl, texture);
            else if (useMaterialColors) // use the material's colours
                setMaterialColors(gl, drawnMaterialName);
        }

        return texture;
    }

    /*
    *  Resets the drawnMaterialName.
    */
    public void resetDrawnMaterialName(boolean resetMaterialValues)
    {
        drawnMaterialName = "";
        if (resetMaterialValues)
            Material.resetMaterialValues();
    }

    /*
    *  Switches the texturing on (binds the texture).
    */
    private void switchOnTexture(GL2 gl, Texture texture)
    {
        usingTexture = true;
        texture.bind(gl);
    }

    /*
    *  Switches the texturing off.
    */
    public void switchOffTexture()
    {
        if (usingTexture)
            usingTexture = false;
    }

    /*
    *  Returns the texture associated with the material name.
    */
    private Texture getTexture(String materialName)
    {
        Material material = materialsMap.get(materialName);
        return (material != null) ? material.getTexture() : null;
    }

    /*
    *  Starts rendering using the colours specified by the named material.
    */
    private void setMaterialColors(GL2 gl, String materialName)
    {
        Material material = materialsMap.get(materialName);
        if (material != null)
            material.setMaterialValues(gl);
    }

    /*
    *  Checks for material textures availability.
    */
    public boolean hasMaterialTextures()
    {
        for ( Material material : materialsMap.values() )
            if (material.getTexture() != null)
                return true;

        return false;
    }

    /**
    *  Clears all materials.
    */
    public void clearAllMaterials(GL2 gl)
    {
        materialsMap.clear();
    }

    /*
    *  Shows all the Material information.
    */
    public void showMaterials()
    {
        if (DEBUG_BUILD)
        {
            println("\nNo. of materialsMap: " + materialsMap.size());
            for ( Material material : materialsMap.values() )
                material.showMaterial();
        }
    }


}
