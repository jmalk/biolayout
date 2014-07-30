package org.BioLayoutExpress3D.Models.Loaders.OBJModelLoader;

import java.util.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/*
*
* FaceMaterials stores the face indicies where a material
* is first used. At render time, this information is utilized
* to change the rendering material when a given face needs
* to be drawn.
*
* @author Andrew Davison, 2006, rewrite for BioLayout Express3D by Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class FaceMaterials
{

    /**
    *  The face index (integer) where a material is first used.
    */
    private HashMap<Integer, String> faceMaterialsMap = null;

    /**
    *  For reporting how many times a material (string) is used.
    */
    private HashMap<String, Integer> materialCountMap = null;

    /**
    *  The FaceMaterials class constructor.
    */
    public FaceMaterials()
    {
        faceMaterialsMap = new HashMap<Integer, String>();
        if (DEBUG_BUILD) materialCountMap = new HashMap<String, Integer>();
    }

    /**
    *  Stores the face index and the material it uses.
    *  it also stores how many times a materialName has been used by faces.
    */
    public void addUse(String materialName, int faceIndex)
    {
        if (DEBUG_BUILD)
            if ( faceMaterialsMap.containsKey(faceIndex) ) // face index already present
                println("Face index " + faceIndex + " changed to use material " + materialName);

        faceMaterialsMap.put(faceIndex, materialName);
        if (DEBUG_BUILD) materialCountMap.put(materialName, materialCountMap.containsKey(materialName) ? materialCountMap.get(materialName) + 1 : 1);
    }

    /**
    *  Finds the material by giving an index.
    */
    public String findMaterial(int faceIndex)
    {
        String faceMaterial = faceMaterialsMap.get(faceIndex);
        return (faceMaterial != null) ? faceMaterial : "";
    }

    /**
    *  Clears all face materials.
    */
    public void clearAllFaceMaterials()
    {
        faceMaterialsMap.clear();
        if (DEBUG_BUILD) materialCountMap.clear();
    }

    /**
    *  Lists all the materials used by faces, and the number of
    *  faces that have used them.
    */
    public void showUsedMaterials()
    {
        if (DEBUG_BUILD)
        {
            println("\nNo. of materials used: " + materialCountMap.size());
            // cycle through the hashmap showing the count for each material
            for ( String materialName : materialCountMap.keySet() )
                print(materialName + ": " + materialCountMap.get(materialName) + "\n");
        }
    }


}