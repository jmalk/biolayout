package org.BioLayoutExpress3D.CoreUI;

import java.util.*;
import org.BioLayoutExpress3D.Network.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Anton Enright, full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class LayoutClassSetsManager
{
    private ArrayList<LayoutClasses> classSetNames = null;
    private HashMap<LayoutClasses, String> classSetNamesMap = null;
    private HashMap<String, LayoutClasses> classSetNameIDsMap = null;

    private String currentClassSetName = "";
    private int currentClassSetID = 0;
    private int totalclassSetNames = 0;

    public LayoutClassSetsManager()
    {
        classSetNames = new ArrayList<LayoutClasses>();
        classSetNamesMap = new HashMap<LayoutClasses, String>();
        classSetNameIDsMap = new HashMap<String, LayoutClasses>();
    }

    public LayoutClasses createNewClassSet(String newName)
    {
        if ( classSetExists(newName) )
        {
            return getClassSet(newName);
        }
        else
        {
            LayoutClasses classes = new LayoutClasses(newName, totalclassSetNames);

            classSetNames.add(classes);
            classSetNamesMap.put(classes, newName);
            classSetNameIDsMap.put(newName, classes);

            if (DEBUG_BUILD) println("Creating New Class Set: " + newName);

            LayoutClasses rootclasses = classSetNames.get(0);
            for ( Vertex vertex : rootclasses.getClassesMembershipMap().keySet() )
                classes.setClass(vertex, 0);

            totalclassSetNames++;

            return classes;
        }
    }

    public boolean classSetExists(String name)
    {
        return classSetNameIDsMap.containsKey(name);
    }

    public LayoutClasses getClassSet(String name)
    {
        return ( classSetExists(name) ) ? getClassSetByName(name) : createNewClassSet(name);
    }

    public LayoutClasses getClassSet(int classSetID)
    {
        return classSetNames.get(classSetID);
    }

    public void switchClassSet(String classSetName)
    {
        if ( classSetExists(classSetName) )
        {
            currentClassSetName = classSetName;
            currentClassSetID = getClassSetByName(classSetName).getClassSetID();
        }
    }

    public LayoutClasses getCurrentClassSetAllClasses()
    {
        return classSetNames.get(currentClassSetID);
    }

    public String getCurrentClassSetName()
    {
        return currentClassSetName;
    }

    public int getCurrentClassSetID()
    {
        return currentClassSetID;
    }

    public ArrayList<LayoutClasses> getClassSetNames()
    {
        return classSetNames;
    }

    public int getTotalClassSets()
    {
        return totalclassSetNames;
    }

    public LayoutClasses getClassSetByName(String name)
    {
        return classSetNameIDsMap.get(name);
    }

    public void clearClassSets()
    {
        for (LayoutClasses layoutClasses : classSetNames)
            layoutClasses.clearClasses();

        currentClassSetName = "";
        currentClassSetID = 0;
        totalclassSetNames = 0;

        classSetNames.clear();
        classSetNamesMap.clear();
        classSetNameIDsMap.clear();

        createNewClassSet("Default Classes");
    }


}