package org.BioLayoutExpress3D.Utils;

import java.io.File;

/**
 * Implement Java 1.7 java.nio.file.Paths style functionality for Java 1.6
 *
 * @author Tim Angus <tim.angus@roslin.ed.ac.uk>
 */
public class Path
{
    public static String combine(String... paths)
    {
        File file = new File(paths[0]);

        for (int i = 1; i < paths.length ; i++)
        {
            file = new File(file, paths[i]);
        }

        return file.getPath();
    }
}