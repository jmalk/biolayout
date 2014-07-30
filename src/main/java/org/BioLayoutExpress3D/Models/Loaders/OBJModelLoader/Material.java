package org.BioLayoutExpress3D.Models.Loaders.OBJModelLoader;

import javax.media.opengl.*;
import com.jogamp.opengl.util.texture.*;
import static javax.media.opengl.GL2.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* A Material object holds colour and texture information
* for a named material.
*
* The Material object also manages the rendering using its
* colours (see the setMaterialColors() method). The rendering using the
* texture is done by the Materials object.
*
* @author Andrew Davison, 2006, rewrite for BioLayout Express3D by Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class Material
{

    /**
    *  Stores the material name.
    */
    private String materialName = "";

    /**
    *  Colour info: ambient, diffuse & specular colours
    */
    private Point3D ka = null, kd = null, ks = null;

    /**
    *  Colour info: shininess & alpha
    */
    private float ns = 0.0f, d = 1.0f;

    /**
    *  Texture file name.
    */
    private String textureFileName = "";

    /**
    *  Texture reference.
    */
    private Texture texture = null;

    /**
    *  The Material class constructor.
    */
    public Material(String materialName)
    {
        this.materialName = materialName;
    }

    /**
    *  Gets the material name.
    */
    public String getMaterialName()
    {
        return materialName;
    }


    // --------- set/get methods for colour info --------------

    /**
    *  Sets the alpha colour info.
    */
    public void setD(float d)
    {
        this.d = d;
    }

    /**
    *  Gets the alpha colour info.
    */
    public float getD()
    {
        return d;
    }

    /**
    *  Sets the shininess colour info.
    */
    public void setNs(float ns)
    {
        this.ns = ns;
    }

    /**
    *  Gets the shininess colour info.
    */
    public float getNs()
    {
        return ns;
    }

    /**
    *  Sets the ambient colour info.
    */
    public void setKa(Point3D ka)
    {
        this.ka = ka;
    }

    /**
    *  Gets the ambient colour info.
    */
    public Point3D getKa()
    {
        return ka;
    }

    /**
    *  Sets the diffuse colour info.
    */
    public void setKd(Point3D kd)
    {
        this.kd = kd;
    }

    /**
    *  Gets the diffuse colour info.
    */
    public Point3D getKd()
    {
        return kd;
    }

    /**
    *  Sets the specular colour info.
    */
    public void setKs(Point3D ks)
    {
        this.ks = ks;
    }

    /**
    *  Gets the specular colour info.
    */
    public Point3D getKs()
    {
        return ks;
    }

    /**
    *  Resets all the materials' values.
    */
    public static void resetMaterialValues()
    {
        GL2 gl = GLContext.getCurrent().getGL().getGL2();
        float[] colorAmbient = { 0.2f, 0.2f, 0.2f, 1.0f };
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, colorAmbient, 0);
        float[] colorDiffuse = { 0.8f, 0.8f, 0.8f, 1.0f };
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, colorDiffuse, 0);
        float[] colorSpecular = { 0.0f, 0.0f, 0.0f, 1.0f };
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, colorSpecular, 0);
        float[] colorEmission = { 0.0f, 0.0f, 0.0f, 1.0f };
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_EMISSION, colorEmission, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 0.0f);
    }

    /**
    *  Starts rendering using this material's values.
    */
    public void setMaterialValues(GL2 gl)
    {
        if (ka != null) // ambient color
        {
            float[] colorKa = { ka.getX(), ka.getY(), ka.getZ(), d };
            gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, colorKa, 0);
        }

        if (kd != null) // diffuse color
        {
            float[] colorKd = { kd.getX(), kd.getY(), kd.getZ(), d };
            gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, colorKd, 0);
        }

        if (ks != null) // specular color
        {
            float[] colorKs = { ks.getX(), ks.getY(), ks.getZ(), d };
            gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, colorKs, 0);
        }

        if (ns != 0.0f) // shininess
        {
            gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, ns);
        }

        if (d != 1.0f) // alpha
        {
            // implemented in the 3 glMaterialfv calls above
        }
    }


    // --------- set/get methods for texture info --------------

    /**
    *  Loads the texture.
    */
    public void loadTexture(String textureFileName)
    {
        texture = TextureProducer.createTextureFromBufferedImageAndDeleteOrigContext(org.BioLayoutExpress3D.StaticLibraries.ImageProducer.loadImageFromURL( getClass().getResource(textureFileName) ), true);
    }

    /**
    *  Sets the texture.
    */
    public void setTexture(Texture texture)
    {
        this.texture = texture;
    }

    /**
    *  Gets the texture.
    */
    public Texture getTexture()
    {
        return texture;
    }

    /**
    *  Shows material details.
    */
    public void showMaterial()
    {
        if (DEBUG_BUILD)
        {
            println(materialName);
            if (ka != null)
                println("  Ka: " + ka.toString());
            if (kd != null)
                println("  Kd: " + kd.toString());
            if (ks != null)
                println("  Ks: " + ks.toString());
            if (ns != 0.0f)
                println("  Ns: " + ns);
            if (d != 1.0f)
                println("  d: " + d);
            if ( !textureFileName.isEmpty() )
                println("  Texture file: " + textureFileName);
        }
    }


}
