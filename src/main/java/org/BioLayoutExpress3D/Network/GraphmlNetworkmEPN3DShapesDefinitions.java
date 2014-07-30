package org.BioLayoutExpress3D.Network;

import org.BioLayoutExpress3D.Models.Lathe3D.*;
import org.BioLayoutExpress3D.Models.SuperQuadric.*;
import static java.lang.Math.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.Lathe3DShapes.*;
import static org.BioLayoutExpress3D.Models.Lathe3D.Lathe3DShapeTypes.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.SuperQuadricShapes.*;
import static org.BioLayoutExpress3D.Models.SuperQuadric.SuperQuadricShapeTypes.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
* Graphml network mEPN 3D shapes definitions.
*
*
* @author Thanos Theo, Tom Charles Freeman, 2011
* @version 3.0.0.0
*
*/

public final class GraphmlNetworkmEPN3DShapesDefinitions
{

    public static final String SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_NAME = "Complex";
    private static final SuperQuadricShapes SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_PRESET_SHAPE = ROUND_CUBE;
    public static final SuperQuadricSettings SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SETTINGS = SuperQuadricShapesProducer.createSuperQuadricSettings( SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_PRESET_SHAPE, NODE_TESSELATION.get(), NODE_TESSELATION.get() );
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SCALE_X = 1.4f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SCALE_Y = 0.4f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SCALE_Z = 0.2f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_ROTATE_X = 0.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_ROTATE_Y = 0.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_ROTATE_Z = 0.0f;

    public static final String SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_NAME = "Protein";
    private static final SuperQuadricShapes SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_PRESET_SHAPE = ROUND_CUBE;
    public static final SuperQuadricSettings SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SETTINGS = SuperQuadricShapesProducer.createSuperQuadricSettings( SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_PRESET_SHAPE, NODE_TESSELATION.get(), NODE_TESSELATION.get() );
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SCALE_X = 1.2f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SCALE_Y = 0.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SCALE_Z = 0.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_ROTATE_X = 0.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_ROTATE_Y = 0.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_ROTATE_Z = 0.0f;

    public static final String OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_NAME = "Gene";
    public static final float OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_SCALE_X = 0.0f;
    public static final float OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_SCALE_Y = 0.0f;
    public static final float OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_SCALE_Z = 0.0f;
    public static final float OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_ROTATE_X = 0.0f;
    public static final float OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_ROTATE_Y = 0.0f;
    public static final float OBJ_MODEL_LOADER_MEPN_3D_SHAPE_GENE_ROTATE_Z = 90.0f;

    public static final String SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_NAME = "Simple Biochemical";
    private static final SuperQuadricShapes SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_PRESET_SHAPE = PINEAPPLE_SLICE;
    public static final SuperQuadricSettings SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SETTINGS = SuperQuadricShapesProducer.createSuperQuadricSettings( SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_PRESET_SHAPE, NODE_TESSELATION.get(), NODE_TESSELATION.get() );
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SCALE_X = 1.3f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SCALE_Y = -0.1f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SCALE_Z = -0.5f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_ROTATE_X = 0.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_ROTATE_Y = 0.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_ROTATE_Z = 0.0f;

    public static final String SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_NAME = "Generic Entity";
    private static final SuperQuadricShapes SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_PRESET_SHAPE = PINEAPPLE_SLICE;
    public static final SuperQuadricSettings SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SETTINGS = SuperQuadricShapesProducer.createSuperQuadricSettings( SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_PRESET_SHAPE, NODE_TESSELATION.get(), NODE_TESSELATION.get() );
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SCALE_X = 0.8f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SCALE_Y = -0.2f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SCALE_Z = -0.6f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_ROTATE_X = 0.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_ROTATE_Y = 0.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_ROTATE_Z = 0.0f;

    public static final String SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_NAME = "Drug";
    private static final SuperQuadricShapes SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_PRESET_SHAPE = DOUBLE_PYRAMID;
    public static final SuperQuadricSettings SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SETTINGS = SuperQuadricShapesProducer.createSuperQuadricSettings( SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_PRESET_SHAPE, NODE_TESSELATION.get(), NODE_TESSELATION.get() );
    public static final SuperQuadricSettings SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_COMPATIBLE_WITH_LOD_SETTINGS = SuperQuadricShapesProducer.createSuperQuadricSettings( SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_PRESET_SHAPE, NODE_TESSELATION.get(), NODE_TESSELATION.get() );
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SCALE_X = 2.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SCALE_Y = 2.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SCALE_Z = 2.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_ROTATE_X = 0.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_ROTATE_Y = 0.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_ROTATE_Z = 0.0f;

    public static final String SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_NAME = "Ion/Simple Molecule";
    private static final SuperQuadricShapes SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_PRESET_SHAPE = DOUBLE_PYRAMID;
    public static final SuperQuadricSettings SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SETTINGS = SuperQuadricShapesProducer.createSuperQuadricSettings( SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_PRESET_SHAPE, NODE_TESSELATION.get(), NODE_TESSELATION.get() );
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SCALE_X = 1.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SCALE_Y = 1.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SCALE_Z = 1.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_ROTATE_X = 0.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_ROTATE_Y = 0.0f;
    public static final float SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_ROTATE_Z = 0.0f;

    public static final String LATHE3D_MEPN_3D_SHAPE_AND_NAME = "AND";
    private static final Lathe3DShapes LATHE3D_MEPN_3D_SHAPE_AND_PRESET_SHAPE = Lathe3DShapes.TORUS;
    public static final Lathe3DSettings LATHE3D_MEPN_3D_SHAPE_AND_SETTINGS = Lathe3DShapesProducer.createLathe3DSettings( LATHE3D_MEPN_3D_SHAPE_AND_PRESET_SHAPE, NODE_TESSELATION.get() );
    public static final float LATHE3D_MEPN_3D_SHAPE_AND_SCALE_X = -0.3f;
    public static final float LATHE3D_MEPN_3D_SHAPE_AND_SCALE_Y = -0.3f;
    public static final float LATHE3D_MEPN_3D_SHAPE_AND_SCALE_Z = -0.3f;
    public static final float LATHE3D_MEPN_3D_SHAPE_AND_ROTATE_X = 90.0f;
    public static final float LATHE3D_MEPN_3D_SHAPE_AND_ROTATE_Y = 0.0f;
    public static final float LATHE3D_MEPN_3D_SHAPE_AND_ROTATE_Z = 0.0f;

    public static final String LATHE3D_MEPN_3D_SHAPE_OR_NAME = "OR";
    private static final Lathe3DShapes LATHE3D_MEPN_3D_SHAPE_OR_PRESET_SHAPE = SAUCER;
    public static final Lathe3DSettings LATHE3D_MEPN_3D_SHAPE_OR_SETTINGS = Lathe3DShapesProducer.createLathe3DSettings( LATHE3D_MEPN_3D_SHAPE_OR_PRESET_SHAPE, NODE_TESSELATION.get() );
    public static final float LATHE3D_MEPN_3D_SHAPE_OR_SCALE_X = -0.3f;
    public static final float LATHE3D_MEPN_3D_SHAPE_OR_SCALE_Y = -0.3f;
    public static final float LATHE3D_MEPN_3D_SHAPE_OR_SCALE_Z = -0.3f;
    public static final float LATHE3D_MEPN_3D_SHAPE_OR_ROTATE_X = 90.0f;
    public static final float LATHE3D_MEPN_3D_SHAPE_OR_ROTATE_Y = 0.0f;
    public static final float LATHE3D_MEPN_3D_SHAPE_OR_ROTATE_Z = 0.0f;

    static
    {
        // Complex mEPN 3D shape
        SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SETTINGS.e = 0.5f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SETTINGS.n = 0.6f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SETTINGS.v1 = (float)(-PI);
        SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SETTINGS.alpha = 2.0f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_COMPLEX_SETTINGS.superQuadricShapeType = ELLIPSOID;

        // Protein mEPN 3D shape
        SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SETTINGS.e = 0.5f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SETTINGS.n = 0.6f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SETTINGS.v1 = (float)(-PI);
        SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SETTINGS.alpha = 2.0f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_PROTEIN_SETTINGS.superQuadricShapeType = ELLIPSOID;

        // Simple Biochemical mEPN 3D shape
        SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SETTINGS.e = 1.0f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SETTINGS.n = 1.1f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SETTINGS.v1 = (float)(-PI);
        SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SETTINGS.alpha = 2.0f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_SIMPLE_BIOCHEMICAL_SETTINGS.superQuadricShapeType = ELLIPSOID;

        // Generic Entity mEPN 3D shape
        SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SETTINGS.e = 1.0f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SETTINGS.n = 0.3f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SETTINGS.v1 = (float)(-PI);
        SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SETTINGS.alpha = 1.0f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_GENERIC_ENTITY_SETTINGS.superQuadricShapeType = ELLIPSOID;

        // Drug mEPN 3D shape
        SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SETTINGS.e = 3.3f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SETTINGS.n = 3.8f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SETTINGS.v1 = (float)(-PI);
        SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SETTINGS.alpha = 2.0f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_SETTINGS.superQuadricShapeType = ELLIPSOID;
        SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_COMPATIBLE_WITH_LOD_SETTINGS.e = 2.0f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_COMPATIBLE_WITH_LOD_SETTINGS.n = 2.0f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_COMPATIBLE_WITH_LOD_SETTINGS.v1 = (float)(-PI);
        SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_COMPATIBLE_WITH_LOD_SETTINGS.alpha = 2.0f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_DRUG_COMPATIBLE_WITH_LOD_SETTINGS.superQuadricShapeType = ELLIPSOID;

        // Ion/Simple Molecule mEPN 3D shape
        SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SETTINGS.e = 2.0f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SETTINGS.n = 1.4f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SETTINGS.v1 = (float)(-PI);
        SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SETTINGS.alpha = 2.0f;
        SUPER_QUADRIC_MEPN_3D_SHAPE_ION_SIMPLE_MOLECULE_SETTINGS.superQuadricShapeType = ELLIPSOID;

        // AND mEPN 3D shape
        LATHE3D_MEPN_3D_SHAPE_AND_SETTINGS.k = 4.0f;
        LATHE3D_MEPN_3D_SHAPE_AND_SETTINGS.xsIn = new float[]{ 1.0f, 1.5f, 1.0f, 0.5f, 1.0f };
        LATHE3D_MEPN_3D_SHAPE_AND_SETTINGS.ysIn = new float[]{ 0.0f, 0.5f, 1.0f, 0.5f, 0.001f };
        LATHE3D_MEPN_3D_SHAPE_AND_SETTINGS.splineStep = 10;
        LATHE3D_MEPN_3D_SHAPE_AND_SETTINGS.lathe3DShapeType = RHODONEA_8_PETALS;

        // OR mEPN 3D shape
        LATHE3D_MEPN_3D_SHAPE_OR_SETTINGS.k = 2.0f;
        LATHE3D_MEPN_3D_SHAPE_OR_SETTINGS.xsIn = new float[]{ 0.667f, 2.025f,  0.641f, 0.6667f, 0.667f };
        LATHE3D_MEPN_3D_SHAPE_OR_SETTINGS.ysIn = new float[]{ 0.0f,   0.667f,  1.359f, 0.0256f, 0.0256f };
        LATHE3D_MEPN_3D_SHAPE_OR_SETTINGS.splineStep = 10;
        LATHE3D_MEPN_3D_SHAPE_OR_SETTINGS.lathe3DShapeType = RHODONEA_4_PETALS;
    }

}