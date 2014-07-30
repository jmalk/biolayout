package org.BioLayoutExpress3D.Models.SuperQuadric;

import javax.media.opengl.*;
import static java.lang.Math.*;
import org.BioLayoutExpress3D.Models.*;
import static org.BioLayoutExpress3D.StaticLibraries.EnumUtils.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.SuperQuadricShapes.*;
import static org.BioLayoutExpress3D.Models.SuperQuadric.SuperQuadricShapeTypes.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* Many examples using the SuperQuadricShape for producing various SuperQuadric-based shapes.
*
* @see org.BioLayoutExpress3D.Models.SuperQuadric.SuperQuadricSettings
* @see org.BioLayoutExpress3D.Models.SuperQuadric.SuperQuadricShape
* @author Andrew Davison, 2005, Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public final class SuperQuadricShapesProducer
{

    /**
    *  Creates the SuperQuadricSettings based on the given shape index.
    */
    public static SuperQuadricSettings createSuperQuadricSettings(SuperQuadricShapes superQuadricShape, int slices, int segments)
    {
        SuperQuadricSettings superQuadricSettings = new SuperQuadricSettings();
        switch (superQuadricShape)
        {
            case SPHERE:

                superQuadricSettings.a1 = superQuadricSettings.a2 = superQuadricSettings.a3 = 1.2f;
                superQuadricSettings.n = 1.0f;
                superQuadricSettings.e = 1.0f;
                superQuadricSettings.u1 = (float)(-PI / 2.0f);
                superQuadricSettings.u2 = (float)( PI / 2.0f);
                superQuadricSettings.v1 = (float)(-PI);
                superQuadricSettings.v2 = (float)( PI);
                superQuadricSettings.uSegments = slices;
                superQuadricSettings.vSegments = segments;
                superQuadricSettings.s1 = 0.0f;
                superQuadricSettings.t1 = 0.0f;
                superQuadricSettings.s2 = 1.0f;
                superQuadricSettings.t2 = 1.0f;
                superQuadricSettings.superQuadricShapeType = ELLIPSOID;

                break;

            case CYLINDER:

                superQuadricSettings.a1 = superQuadricSettings.a2 = superQuadricSettings.a3 = 1.0f;
                superQuadricSettings.n = 0.0f;
                superQuadricSettings.e = 1.0f;
                superQuadricSettings.u1 = (float)(-PI / 2.0f);
                superQuadricSettings.u2 = (float)( PI / 2.0f);
                superQuadricSettings.v1 = (float)(-PI);
                superQuadricSettings.v2 = (float)( PI);
                superQuadricSettings.uSegments = slices;
                superQuadricSettings.vSegments = segments;
                superQuadricSettings.s1 = 0.0f;
                superQuadricSettings.t1 = 0.0f;
                superQuadricSettings.s2 = 1.0f;
                superQuadricSettings.t2 = 1.0f;
                superQuadricSettings.superQuadricShapeType = ELLIPSOID;

                break;

            case STAR:

                superQuadricSettings.a1 = superQuadricSettings.a2 = superQuadricSettings.a3 = 1.0f;
                superQuadricSettings.n = 4.0f;
                superQuadricSettings.e = 4.0f;
                superQuadricSettings.u1 = (float)(-PI / 2.0f);
                superQuadricSettings.u2 = (float)( PI / 2.0f);
                superQuadricSettings.v1 = (float)(-PI);
                superQuadricSettings.v2 = (float)( PI);
                superQuadricSettings.uSegments = slices;
                superQuadricSettings.vSegments = segments;
                superQuadricSettings.s1 = 0.0f;
                superQuadricSettings.t1 = 0.0f;
                superQuadricSettings.s2 = 1.0f;
                superQuadricSettings.t2 = 1.0f;
                superQuadricSettings.superQuadricShapeType = ELLIPSOID;

                break;

            case DOUBLE_PYRAMID:

                superQuadricSettings.a1 = superQuadricSettings.a2 = superQuadricSettings.a3 = 1.0f;
                superQuadricSettings.n = 2.0f;
                superQuadricSettings.e = 2.0f;
                superQuadricSettings.u1 = (float)(-PI / 2.0f);
                superQuadricSettings.u2 = (float)( PI / 2.0f);
                superQuadricSettings.v1 = (float)(-PI);
                superQuadricSettings.v2 = (float)( PI);
                superQuadricSettings.uSegments = slices;
                superQuadricSettings.vSegments = segments;
                superQuadricSettings.s1 = 0.0f;
                superQuadricSettings.t1 = 0.0f;
                superQuadricSettings.s2 = 1.0f;
                superQuadricSettings.t2 = 1.0f;
                superQuadricSettings.superQuadricShapeType = ELLIPSOID;

                break;

            case TORUS:

                superQuadricSettings.a1 = superQuadricSettings.a2 = superQuadricSettings.a3 = (0.25f + 0.25f) / 2.0f; // radius1 & radius2 equals to 1.0f
                superQuadricSettings.alpha = 1.5f;
                superQuadricSettings.n = 1.0f;
                superQuadricSettings.e = 1.0f;
                superQuadricSettings.u1 = (float)(-PI);
                superQuadricSettings.u2 = (float)( PI);
                superQuadricSettings.v1 = (float)(-PI);
                superQuadricSettings.v2 = (float)( PI);
                superQuadricSettings.uSegments = slices;
                superQuadricSettings.vSegments = segments;
                superQuadricSettings.s1 = 0.0f;
                superQuadricSettings.t1 = 0.0f;
                superQuadricSettings.s2 = 1.0f;
                superQuadricSettings.t2 = 1.0f;
                superQuadricSettings.superQuadricShapeType = TOROID;

                break;

            case PINEAPPLE_SLICE:

                superQuadricSettings.a1 = superQuadricSettings.a2 = superQuadricSettings.a3 = (1.0f + 1.0f) / 2.0f; // radius1 & radius2 equals to 1.0f
                superQuadricSettings.alpha = 1.0f;
                superQuadricSettings.n = 0.0f;
                superQuadricSettings.e = 1.0f;
                superQuadricSettings.u1 = (float)(-PI);
                superQuadricSettings.u2 = (float)( PI);
                superQuadricSettings.v1 = (float)(-PI);
                superQuadricSettings.v2 = (float)( PI);
                superQuadricSettings.uSegments = slices;
                superQuadricSettings.vSegments = segments;
                superQuadricSettings.s1 = 0.0f;
                superQuadricSettings.t1 = 0.0f;
                superQuadricSettings.s2 = 1.0f;
                superQuadricSettings.t2 = 1.0f;
                superQuadricSettings.superQuadricShapeType = TOROID;

                break;

            case PILLOW:

                superQuadricSettings.a1 = superQuadricSettings.a2 = superQuadricSettings.a3 = 1.0f;
                superQuadricSettings.n = 1.0f;
                superQuadricSettings.e = 0.0f;
                superQuadricSettings.u1 = (float)(-PI / 2.0f);
                superQuadricSettings.u2 = (float)( PI / 2.0f);
                superQuadricSettings.v1 = (float)(-PI);
                superQuadricSettings.v2 = (float)( PI);
                superQuadricSettings.uSegments = slices;
                superQuadricSettings.vSegments = segments;
                superQuadricSettings.s1 = 0.0f;
                superQuadricSettings.t1 = 0.0f;
                superQuadricSettings.s2 = 1.0f;
                superQuadricSettings.t2 = 1.0f;
                superQuadricSettings.superQuadricShapeType = ELLIPSOID;

                break;

            case SQUARE_TORUS:

                superQuadricSettings.a1 = superQuadricSettings.a2 = superQuadricSettings.a3 = (1.0f + 1.0f) / 2.0f; // radius1 & radius2 equals to 1.0f
                superQuadricSettings.alpha = 1.0f;
                superQuadricSettings.n = 0.2f;
                superQuadricSettings.e = 0.2f;
                superQuadricSettings.u1 = (float)(-PI);
                superQuadricSettings.u2 = (float)( PI);
                superQuadricSettings.v1 = (float)(-PI);
                superQuadricSettings.v2 = (float)( PI);
                superQuadricSettings.uSegments = slices;
                superQuadricSettings.vSegments = segments;
                superQuadricSettings.s1 = 0.0f;
                superQuadricSettings.t1 = 0.0f;
                superQuadricSettings.s2 = 1.0f;
                superQuadricSettings.t2 = 1.0f;
                superQuadricSettings.superQuadricShapeType = TOROID;

                break;

            case PINCHED_TORUS:

                superQuadricSettings.a1 = superQuadricSettings.a2 = superQuadricSettings.a3 = (1.0f + 1.0f) / 2.0f; // radius1 & radius2 equals to 1.0f
                superQuadricSettings.alpha = 1.0f;
                superQuadricSettings.n = 1.0f;
                superQuadricSettings.e = 4.0f;
                superQuadricSettings.u1 = (float)(-PI);
                superQuadricSettings.u2 = (float)( PI);
                superQuadricSettings.v1 = (float)(-PI);
                superQuadricSettings.v2 = (float)( PI);
                superQuadricSettings.uSegments = slices;
                superQuadricSettings.vSegments = segments;
                superQuadricSettings.s1 = 0.0f;
                superQuadricSettings.t1 = 0.0f;
                superQuadricSettings.s2 = 1.0f;
                superQuadricSettings.t2 = 1.0f;
                superQuadricSettings.superQuadricShapeType = TOROID;

                break;

            case ROUND_CUBE:

                superQuadricSettings.a1 = superQuadricSettings.a2 = superQuadricSettings.a3 = 1.0f;
                superQuadricSettings.n = 0.2f;
                superQuadricSettings.e = 0.2f;
                superQuadricSettings.u1 = (float)(-PI / 2.0f);
                superQuadricSettings.u2 = (float)( PI / 2.0f);
                superQuadricSettings.v1 = (float)(-PI);
                superQuadricSettings.v2 = (float)( PI);
                superQuadricSettings.uSegments = slices;
                superQuadricSettings.vSegments = segments;
                superQuadricSettings.s1 = 0.0f;
                superQuadricSettings.t1 = 0.0f;
                superQuadricSettings.s2 = 1.0f;
                superQuadricSettings.t2 = 1.0f;
                superQuadricSettings.superQuadricShapeType = ELLIPSOID;

                break;

            case HYPERBOLOID_ONE_SHEET:

                superQuadricSettings.a1 = superQuadricSettings.a2 = superQuadricSettings.a3 = 0.45f;
                superQuadricSettings.n = 1.0f;
                superQuadricSettings.e = 1.0f;
                superQuadricSettings.u1 = (float)(-PI / 2.0f);
                superQuadricSettings.u2 = (float)( PI / 2.0f);
                superQuadricSettings.v1 = (float)(-PI);
                superQuadricSettings.v2 = (float)( PI);
                superQuadricSettings.uSegments = slices;
                superQuadricSettings.vSegments = segments;
                superQuadricSettings.s1 = 0.0f;
                superQuadricSettings.t1 = 0.0f;
                superQuadricSettings.s2 = 1.0f;
                superQuadricSettings.t2 = 1.0f;
                superQuadricSettings.superQuadricShapeType = SuperQuadricShapeTypes.HYPERBOLOID_ONE_SHEET;

                break;

            case HYPERBOLOID_TWO_SHEETS:

                superQuadricSettings.a1 = superQuadricSettings.a2 = superQuadricSettings.a3 = 0.25f;
                superQuadricSettings.n = 1.0f;
                superQuadricSettings.e = 1.0f;
                superQuadricSettings.u1 = (float)(-PI / 2.0f);
                superQuadricSettings.u2 = (float)( PI / 2.0f);
                superQuadricSettings.v1 = (float)(-PI / 2.0f);
                superQuadricSettings.v2 = (float)( PI / 2.0f);
                superQuadricSettings.uSegments = slices;
                superQuadricSettings.vSegments = segments;
                superQuadricSettings.s1 = 0.0f;
                superQuadricSettings.t1 = 0.0f;
                superQuadricSettings.s2 = 1.0f;
                superQuadricSettings.t2 = 1.0f;
                superQuadricSettings.superQuadricShapeType = SuperQuadricShapeTypes.HYPERBOLOID_TWO_SHEETS;

                break;

            default: // default case create the Sphere

                superQuadricSettings.a1 = superQuadricSettings.a2 = superQuadricSettings.a3 = 1.2f;
                superQuadricSettings.n = 1.0f;
                superQuadricSettings.e = 1.0f;
                superQuadricSettings.u1 = (float)(-PI / 2.0f);
                superQuadricSettings.u2 = (float)( PI / 2.0f);
                superQuadricSettings.v1 = (float)(-PI);
                superQuadricSettings.v2 = (float)( PI);
                superQuadricSettings.uSegments = slices;
                superQuadricSettings.vSegments = segments;
                superQuadricSettings.s1 = 0.0f;
                superQuadricSettings.t1 = 0.0f;
                superQuadricSettings.s2 = 1.0f;
                superQuadricSettings.t2 = 1.0f;
                superQuadricSettings.superQuadricShapeType = ELLIPSOID;

                break;
        }

        return superQuadricSettings;
    }

    /** v
    *  Creates a 'Sphere' model shape.
    */
    public static ModelShape createSphereShape(GL2 gl, int slices, int segments, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(SPHERE);

        return new SuperQuadricShape(gl, createSuperQuadricSettings(SPHERE, slices, segments), modelSettings);
    }

    /**
    *  Creates a 'Cylinder' model shape.
    */
    public static ModelShape createCylinderShape(GL2 gl, int slices, int segments, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(CYLINDER);

        return new SuperQuadricShape(gl, createSuperQuadricSettings(CYLINDER, slices, segments), modelSettings);
    }

    /**
    *  Creates a 'Star' model shape.
    */
    public static ModelShape createStarShape(GL2 gl, int slices, int segments, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(STAR);

        return new SuperQuadricShape(gl, createSuperQuadricSettings(STAR, slices, segments), modelSettings);
    }

    /**
    *  Creates a 'DoublePyramid' model shape.
    */
    public static ModelShape createDoublePyramidShape(GL2 gl, int slices, int segments, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(DOUBLE_PYRAMID);

        return new SuperQuadricShape(gl, createSuperQuadricSettings(DOUBLE_PYRAMID, slices, segments), modelSettings);
    }

    /**
    *  Creates a 'Torus' model shape.
    */
    public static ModelShape createTorusShape(GL2 gl, int slices, int segments, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(TORUS);

        return new SuperQuadricShape(gl, createSuperQuadricSettings(TORUS, slices, segments), modelSettings);
    }

    /**
    *  Creates a 'PineappleSlice' model shape.
    */
    public static ModelShape createPineappleSliceShape(GL2 gl, int slices, int segments, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(PINEAPPLE_SLICE);

        return new SuperQuadricShape(gl, createSuperQuadricSettings(PINEAPPLE_SLICE, slices, segments), modelSettings);
    }

    /**
    *  Creates a 'Pillow' model shape.
    */
    public static ModelShape createPillowShape(GL2 gl, int slices, int segments, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(PILLOW);

        return new SuperQuadricShape(gl, createSuperQuadricSettings(PILLOW, slices, segments), modelSettings);
    }

    /**
    *  Creates a 'SquareTorus' model shape.
    */
    public static ModelShape createSquareTorusShape(GL2 gl, int slices, int segments, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(SQUARE_TORUS);

        return new SuperQuadricShape(gl, createSuperQuadricSettings(SQUARE_TORUS, slices, segments), modelSettings);
    }

    /**
    *  Creates a 'PinchedTorus' model shape.
    */
    public static ModelShape createPinchedTorusShape(GL2 gl, int slices, int segments, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(PINCHED_TORUS);

        return new SuperQuadricShape(gl, createSuperQuadricSettings(PINCHED_TORUS, slices, segments), modelSettings);
    }

    /**
    *  Creates a 'RoundCube' model shape.
    */
    public static ModelShape createRoundCubeShape(GL2 gl, int slices, int segments, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(ROUND_CUBE);

        return new SuperQuadricShape(gl, createSuperQuadricSettings(ROUND_CUBE, slices, segments), modelSettings);
    }

    /**
    *  Creates a 'HyperboloidOneSheetExample' model shape.
    */
    public static ModelShape createHyperboloidOneSheetExampleShape(GL2 gl, int slices, int segments, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(SuperQuadricShapes.HYPERBOLOID_ONE_SHEET);

        return new SuperQuadricShape(gl, createSuperQuadricSettings(SuperQuadricShapes.HYPERBOLOID_ONE_SHEET, slices, segments), modelSettings);
    }

    /**
    *  Creates a 'HyperboloidTwoSheetsExample' model shape.
    */
    public static ModelShape createHyperboloidTwoSheetsExampleShape(GL2 gl, int slices, int segments, ModelSettings modelSettings)
    {
        modelSettings.shapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(SuperQuadricShapes.HYPERBOLOID_TWO_SHEETS);

        return new SuperQuadricShape(gl, createSuperQuadricSettings(SuperQuadricShapes.HYPERBOLOID_TWO_SHEETS, slices, segments), modelSettings);
    }


}