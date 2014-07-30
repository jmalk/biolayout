package org.BioLayoutExpress3D.Models.Lathe3D;

import javax.media.opengl.*;
import org.BioLayoutExpress3D.Models.*;
import org.BioLayoutExpress3D.Models.Lathe3D.Variants.*;
import static org.BioLayoutExpress3D.StaticLibraries.EnumUtils.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.Lathe3DShapes.*;
import static org.BioLayoutExpress3D.Models.Lathe3D.Lathe3DShapeTypes.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* Many examples using Lathe3DShape and subclasses for producing various Lathe3D-based shapes.
*
* @see org.BioLayoutExpress3D.Models.Lathe3D.Lathe3DShape
* @author Jeff Molofee, 2000, Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public final class Lathe3DShapesProducer
{

    /**
    *  Creates the xsIn/ysIn based on the given shape index.
    */
    public static Lathe3DSettings createLathe3DSettings(Lathe3DShapes lathe3DShape, int splineStep)
    {
        Lathe3DSettings lathe3DSettings = new Lathe3DSettings();
        switch (lathe3DShape)
        {
            case CYLINDER:

                // all straight lines: use -0.001 to start a line at position 0
                lathe3DSettings.xsIn = new float[]{ -0.001f, -0.4f,   -0.4f,   -0.001f };
                lathe3DSettings.ysIn = new float[]{  0.001f,  0.001f,    2.0f,  2.0f   };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = CIRCLE;

                break;

            case CONE:

                // all straight lines: use -0.001 to start a line at position 0
                lathe3DSettings.xsIn = new float[]{ -0.001f, -1.5f,   -0.100f, -0.001f };
                lathe3DSettings.ysIn = new float[]{  0.001f,  0.001f,  2.0f,    2.0f   };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = CIRCLE;

                break;

            case TRAPEZOID:

                // all straight lines: use -0.001 to start a line at position 0
                lathe3DSettings.xsIn = new float[]{ -0.001f, -1.5f,   -0.5f,   -0.001f };
                lathe3DSettings.ysIn = new float[]{  0.001f,  0.001f,  1.333f,  1.333f };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = CIRCLE;

                break;

            case EGG:

                // curves
                lathe3DSettings.xsIn = new float[]{ 0.001f, 1.0f, 0.001f };
                lathe3DSettings.ysIn = new float[]{ 0.001f, 1.5f, 2.5f   };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = CIRCLE;

                break;

            case DRIP:

                // curves
                lathe3DSettings.xsIn = new float[]{ 0.001f, 0.1f, 0.7f, 0.001f };
                lathe3DSettings.ysIn = new float[]{ 0.001f, 0.1f, 1.5f, 2.0f   };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = CIRCLE;

                break;

            case CUP:

                // mix straight lines and curves
                lathe3DSettings.xsIn = new float[]{ -0.001f, -0.7f,   -0.25f, 0.25f, 0.7f, -0.6f, -0.5f };
                lathe3DSettings.ysIn = new float[]{  0.001f,  0.001f,  0.5f,  1.0f,  2.5f,  3.0f,  3.0f };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = CIRCLE;

                break;

            case LIMB:

                // curves limb
                lathe3DSettings.xsIn = new float[]{ 0.001f, 0.4f, 0.6f, 0.001f };
                lathe3DSettings.ysIn = new float[]{ 0.001f, 0.4f, 2.2f, 3.0f   };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = CIRCLE;

                break;

            case ROUND_R:

                // various shape rotations using subclasses
                lathe3DSettings.xsIn = new float[]{ -1.0f,   -1.0f };
                lathe3DSettings.ysIn = new float[]{  0.001f,  1.0f };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = CIRCLE;

                break;

            case OVAL_R:

                // various shape rotations using subclasses
                lathe3DSettings.xsIn = new float[]{ -1.0f,   -1.0f };
                lathe3DSettings.ysIn = new float[]{  0.001f,  1.0f };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = ELLIPSE;

                break;

            case FLOWER:

                // various shape rotations using subclasses
                lathe3DSettings.xsIn = new float[]{ -1.0f,   -1.0f };
                lathe3DSettings.ysIn = new float[]{  0.001f,  1.0f };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = RHODONEA_8_PETALS;

                break;

            case CHESS_PIECE:

                // various shape rotations using subclasses
                lathe3DSettings.xsIn = new float[]{ -0.001f, -0.4f,   -0.2f, 0.2f, 0.3f, -0.2f, -0.3f, -0.001f };
                lathe3DSettings.ysIn = new float[]{  0.001f,  0.001f,  1.0f, 1.2f, 1.4f,  1.6f,  1.8f,  1.8f   };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = CIRCLE;

                break;

            case BRANCH:

                // all straight lines: use -0.001 to start a line at position 0
                lathe3DSettings.xsIn = new float[]{ -0.001f, -0.4f,   -0.15f, -0.001f };
                lathe3DSettings.ysIn = new float[]{  0.001f,  0.001f,  3.0f,   3.0f   };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = CIRCLE;

                break;

            case TORUS:

                // rotated circle: a torus
                lathe3DSettings.xsIn = new float[]{ 1.0f,   1.5f, 1.0f, 0.5f, 1.0f   };
                lathe3DSettings.ysIn = new float[]{ 0.001f, 0.5f, 1.0f, 0.5f, 0.001f };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = CIRCLE;

                break;

            case DUMB_BELL:

                lathe3DSettings.xsIn = new float[]{ 0.001f, 0.4f, -0.1f, 0.1f, 0.4f, 0.001f };
                lathe3DSettings.ysIn = new float[]{ 0.001f, 0.4f,  0.8f, 1.2f, 1.6f, 2.0f   };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = CIRCLE;

                break;

            case DOME:

                lathe3DSettings.xsIn = new float[]{ -0.01f,   1.0f,   0.001f };
                lathe3DSettings.ysIn = new float[]{  0.001f,  0.001f, 1.0f   };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = CIRCLE;

                break;

            case ARMOUR:

                lathe3DSettings.xsIn = new float[]{ -0.01f,  0.5f,   -1.0f, -1.2f, 1.4f, -0.5f, -0.5f, 0.001f };
                lathe3DSettings.ysIn = new float[]{  0.001f, 0.001f,  1.5f,  1.5f, 2.0f,  2.5f,  2.7f, 2.7f   };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = ELLIPSE;

                break;

            case SAUCER:

                lathe3DSettings.xsIn = new float[]{ 0.001f, 0.75f, 0.9f,  0.75f, 0.001f };
                lathe3DSettings.ysIn = new float[]{ 0.001f, 0.23f, 0.38f, 0.53f, 0.75f  };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = CIRCLE;

                break;

            default: // default case create the Cylinder

                // all straight lines: use -0.001 to start a line at position 0
                lathe3DSettings.xsIn = new float[]{ -0.001f, -0.4f,   -0.4f, -0.001f };
                lathe3DSettings.ysIn = new float[]{  0.001f,  0.001f,  2.0f,  2.0f   };
                lathe3DSettings.splineStep = splineStep / 3;
                lathe3DSettings.lathe3DShapeType = CIRCLE;

                break;
        }

        return lathe3DSettings;
    }

    /**
    *  Creates the relevant lathe3D-based model shape.
    */
    public static ModelShape createLathe3DShape(GL2 gl, Lathe3DSettings lathe3DSettings, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        if ( lathe3DSettings.lathe3DShapeType.equals(CIRCLE) )
            return new Lathe3DShape(gl, lathe3DShapeAngleIncrement, lathe3DSettings, modelSettings);
        else if ( lathe3DSettings.lathe3DShapeType.equals(ELLIPSE) )
            return new Ellipse3DShape(gl, lathe3DShapeAngleIncrement, lathe3DSettings, modelSettings);
        else if ( lathe3DSettings.lathe3DShapeType.equals(RHODONEA_4_PETALS) )
        {
            lathe3DSettings.k = 2.0f;
            return new Rhodonea3DShape(gl, lathe3DShapeAngleIncrement, lathe3DSettings, modelSettings);
        }
        else if ( lathe3DSettings.lathe3DShapeType.equals(RHODONEA_5_PETALS) )
        {
            lathe3DSettings.k = 5.0f;
            return new Rhodonea3DShape(gl, lathe3DShapeAngleIncrement, lathe3DSettings, modelSettings);
        }
        else if ( lathe3DSettings.lathe3DShapeType.equals(RHODONEA_7_PETALS) )
        {
            lathe3DSettings.k = 7.0f;
            return new Rhodonea3DShape(gl, lathe3DShapeAngleIncrement, lathe3DSettings, modelSettings);
        }
        else if ( lathe3DSettings.lathe3DShapeType.equals(RHODONEA_8_PETALS) )
        {
            lathe3DSettings.k = 4.0f;
            return new Rhodonea3DShape(gl, lathe3DShapeAngleIncrement, lathe3DSettings, modelSettings);
        }
        else if ( lathe3DSettings.lathe3DShapeType.equals(RHODONEA_9_PETALS) )
        {
            lathe3DSettings.k = 9.0f;
            return new Rhodonea3DShape(gl, lathe3DShapeAngleIncrement, lathe3DSettings, modelSettings);
        }
        else // if ( lathe3DSettings.lathe3DShapeType.equals(RHODONEA_12_PETALS) )
        {
            lathe3DSettings.k = 6.0f;
            return new Rhodonea3DShape(gl, lathe3DShapeAngleIncrement, lathe3DSettings, modelSettings);
        }
    }

    /**
    *  Creates a 'Cylinder' model shape.
    */
    public static ModelShape createCylinderShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(CYLINDER);

        return createLathe3DShape(gl, createLathe3DSettings(CYLINDER, splineStep), lathe3DShapeAngleIncrement, modelSettings);
    }

    /**
    *  Creates a 'Cone' model shape.
    */
    public static ModelShape createConeShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(CONE);

        return createLathe3DShape(gl, createLathe3DSettings(CONE, splineStep), lathe3DShapeAngleIncrement, modelSettings);
    }

    /**
    *  Creates a 'Trapezoid' model shape.
    */
    public static ModelShape createTrapezoidShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(TRAPEZOID);

        return createLathe3DShape(gl, createLathe3DSettings(TRAPEZOID, splineStep), lathe3DShapeAngleIncrement, modelSettings);
    }

    /**
    *  Creates an 'Egg' model shape.
    */
    public static ModelShape createEggShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(EGG);

        return createLathe3DShape(gl, createLathe3DSettings(EGG, splineStep), lathe3DShapeAngleIncrement, modelSettings);
    }

    /**
    *  Creates a 'Drip' model shape.
    */
    public static ModelShape createDripShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(DRIP);

        return createLathe3DShape(gl, createLathe3DSettings(DRIP, splineStep), lathe3DShapeAngleIncrement, modelSettings);
    }

    /**
    *  Creates a 'Cup' model shape.
    */
    public static ModelShape createCupShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(CUP);

        return createLathe3DShape(gl, createLathe3DSettings(CUP, splineStep), lathe3DShapeAngleIncrement, modelSettings);
    }

    /**
    *  Creates a 'Limb' model shape.
    */
    public static ModelShape createLimbShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(LIMB);

        return createLathe3DShape(gl, createLathe3DSettings(LIMB, splineStep), lathe3DShapeAngleIncrement, modelSettings);
    }

    /**
    *  Creates a 'Round R' model shape.
    */
    public static ModelShape createRoundRShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(ROUND_R);

        return createLathe3DShape(gl, createLathe3DSettings(ROUND_R, splineStep), lathe3DShapeAngleIncrement, modelSettings); // circular
    }

    /**
    *  Creates an 'Oval R' model shape.
    */
    public static ModelShape createOvalRShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(OVAL_R);

        return createLathe3DShape(gl, createLathe3DSettings(OVAL_R, splineStep), lathe3DShapeAngleIncrement, modelSettings); // elliptic
    }

    /**
    *  Creates a 'Flower' model shape.
    */
    public static ModelShape createFlowerShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(FLOWER);

        return createLathe3DShape(gl, createLathe3DSettings(FLOWER, splineStep), lathe3DShapeAngleIncrement, modelSettings); // rhodonea
    }

    /**
    *  Creates a 'Chess Piece' model shape.
    */
    public static ModelShape createChessPieceShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(CHESS_PIECE);

        return createLathe3DShape(gl, createLathe3DSettings(CHESS_PIECE, splineStep), lathe3DShapeAngleIncrement, modelSettings);
    }

    /**
    *  Creates a 'Branch' model shape.
    */
    public static ModelShape createBranchShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(BRANCH);

        return createLathe3DShape(gl, createLathe3DSettings(BRANCH, splineStep), lathe3DShapeAngleIncrement, modelSettings);
    }

    /**
    *  Creates a 'Torus' model shape.
    */
    public static ModelShape createTorusShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(TORUS);

        return createLathe3DShape(gl, createLathe3DSettings(TORUS, splineStep), lathe3DShapeAngleIncrement, modelSettings);
    }

    /**
    *  Creates a 'Dump Bell' model shape.
    */
    public static ModelShape createDumbBellShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(DUMB_BELL);

        return createLathe3DShape(gl, createLathe3DSettings(DUMB_BELL, splineStep), lathe3DShapeAngleIncrement, modelSettings);
    }

    /**
    *  Creates a 'Dome' model shape.
    */
    public static ModelShape createDomeShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(DOME);

        return createLathe3DShape(gl, createLathe3DSettings(DOME, splineStep), lathe3DShapeAngleIncrement, modelSettings);
    }

    /**
    *  Creates an 'Armour' model shape.
    */
    public static ModelShape createArmourShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(ARMOUR);

        return createLathe3DShape(gl, createLathe3DSettings(ARMOUR, splineStep), lathe3DShapeAngleIncrement, modelSettings); // elliptic
    }

    /**
    *  Creates a 'Saucer' model shape.
    */
    public static ModelShape createSaucerShape(GL2 gl, int splineStep, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(SAUCER);

        return createLathe3DShape(gl, createLathe3DSettings(SAUCER, splineStep), lathe3DShapeAngleIncrement, modelSettings);
    }


}