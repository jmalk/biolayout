package org.BioLayoutExpress3D.CoreUI;

import java.awt.*;
import java.util.*;
import org.BioLayoutExpress3D.Network.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* @author Anton Enright, full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class LayoutClasses
{
    public static final String NO_CLASS = "No Class";

    private HashMap<Integer, VertexClass> classesIDMap = null;
    private HashMap<String, VertexClass> classesNamesMap = null;
    private HashMap<Vertex, VertexClass> classesMembershipMap = null;
    private HashSet<Color> classesColors = null;

    private String classSetName = "";
    private int totalClasses = 0;
    private int classSetID = 0;

    public LayoutClasses(String classSetName, int classSetID)
    {
        this.classSetName = classSetName;
        this.classSetID = classSetID;

        classesIDMap = new HashMap<Integer, VertexClass>();
        classesNamesMap = new HashMap<String, VertexClass>();
        classesMembershipMap = new HashMap<Vertex, VertexClass>();
        classesColors = new HashSet<Color>();

        createClass(0, NO_CLASS);
        getClassByID(0).setColor(DEFAULT_NODE_COLOR);

        classesColors.add(DEFAULT_NODE_COLOR);
        totalClasses = 0;
    }

    public int getClassSetID()
    {
        return classSetID;
    }

    public VertexClass createClass(int number, String className)
    {
        String name = (className == null) ? "" : className;
        VertexClass newClass = new VertexClass(number, name, this);
        classesIDMap.put(number, newClass);
        classesNamesMap.put(name, newClass); // Integer autoboxing
        totalClasses++;

        return newClass;
    }

    public VertexClass createClass(int number, Color classColor, String className)
    {
        String name = (className == null) ? "" : className;
        VertexClass newClass = new VertexClass(number, name, this);
        if (classColor != null) newClass.setColor(classColor);
        classesIDMap.put(number, newClass);
        classesNamesMap.put(name, newClass); // Integer autoboxing
        totalClasses++;

        return newClass;
    }

    public VertexClass createClass(String className)
    {
        if ( !classExists(className) )
        {
            VertexClass newClass = new VertexClass(++totalClasses, className, this);
            classesIDMap.put(totalClasses, newClass); // integer autoboxing
            classesNamesMap.put(className, newClass);

            return newClass;
        }
        else
        {
            return classesNamesMap.get(className);
        }
    }

    public VertexClass getClassByID(int vertexClassID)
    {
        return classesIDMap.get(vertexClassID); // Integer autoboxing
    }

    public VertexClass getClassByName(String className)
    {
        return classesNamesMap.get(className);
    }

    public Set<Integer> getAllClassesKeySet()
    {
        return classesIDMap.keySet();
    }

    public Collection<VertexClass> getAllVertexClasses()
    {
        return classesIDMap.values();
    }

    public void clearClasses()
    {
        classesIDMap.clear();
        createClass(0, NO_CLASS);
        getClassByID(0).setColor( new Color(0, 0, 144) );
        totalClasses = 0;
    }

    public int getTotalClasses()
    {
        return totalClasses;
    }

    public void setClass(Vertex vertex, int vertexClassID)
    {
        if ( classExists(vertexClassID) )
            classesMembershipMap.put( vertex, getClassByID(vertexClassID) );
    }

    public void setClass(Vertex vertex, VertexClass vertexClass)
    {
        classesMembershipMap.put(vertex, vertexClass);
    }

    public void setClassColor(int vertexClassID, Color color)
    {
        if ( classExists(vertexClassID) )
            getClassByID(vertexClassID).setColor(color);
    }

    public void setClassName(int vertexClassID, String className)
    {
        if ( classExists(vertexClassID) )
            getClassByID(vertexClassID).setName(className);
    }

    public boolean classExists(int vertexClassID)
    {
        return classesIDMap.containsKey(vertexClassID); // Integer autoboxing
    }

    public boolean classExists(String className)
    {
        return classesNamesMap.containsKey(className); // Integer autoboxing
    }

    public void updateClass(Vertex vertex, int vertexClassID, String className)
    {
        if ( classExists(vertexClassID) )
        {
            classesMembershipMap.put( vertex, getClassByID(vertexClassID) );
        }
        else
        {
            createClass(vertexClassID, className);
            classesMembershipMap.put( vertex, getClassByID(vertexClassID) );
        }
    }

    public void setDefaultClass(Vertex vertex)
    {
        setClass(vertex, 0);
    }

    public VertexClass getVertexClass(Vertex vertex)
    {
        return classesMembershipMap.get(vertex);
    }

    public HashMap<Integer, VertexClass> getClassesMap()
    {
        return classesIDMap;
    }

    public HashSet<Color> getClassesColors()
    {
        return classesColors;
    }

    public HashMap<Vertex, VertexClass> getClassesMembershipMap()
    {
        return classesMembershipMap;
    }

    public HashMap<String, VertexClass> getClassesNamesMap()
    {
        return classesNamesMap;
    }

    public String getClassSetName()
    {
        return classSetName;
    }
}