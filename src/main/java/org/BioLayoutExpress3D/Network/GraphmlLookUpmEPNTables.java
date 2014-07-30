package org.BioLayoutExpress3D.Network;

import org.BioLayoutExpress3D.DataStructures.*;
import org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static java.awt.Color.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static org.BioLayoutExpress3D.Network.GraphmlLookUpmEPNTables.GraphmlShapesGroup1.*;
import static org.BioLayoutExpress3D.Network.GraphmlLookUpmEPNTables.GraphmlShapesGroup2.*;
import static org.BioLayoutExpress3D.Network.GraphmlLookUpmEPNTables.GraphmlShapesGroup3.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.Shapes2D.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.Shapes3D.*;

/**
* Graphml LookUp mEPN tables.
*
*
* @author Thanos Theo, 2009-2010-2011
* @author Derek Wright
* @version 3.0.0.0
*
*/

public final class GraphmlLookUpmEPNTables
{

    /**
    *  mEPN notation Class Set name.
    */
    public static final String MEPN_NOTATION_CLASS_SET_NAME = "mEPN Scheme Version 2.0";

    /**
    *  Shapes group 1 enumeration, used to do a look-up for name & shape but also returning color.
    */
    public static enum GraphmlShapesGroup1 {
                                             // Boolean Logic Operators
                                             AND,
                                             OR,

                                             // Process Nodes
                                             BINDING,
                                             OLIGOMERIZATION,
                                             CLEAVAGE,
                                             AUTO_CLEAVAGE,
                                             DISSOCIATION,
                                             RATE_LIMITING_CATALYSIS,
                                             CATALYSIS,
                                             AUTO_CATALYSIS,
                                             TRANSLOCATION,
                                             TRANSCRIPTION_TRANSLATION,
                                             ACTIVATION,
                                             INHIBITION,
                                             PHOSPHORYLATION,
                                             DEPHOSPHORYLATION,
                                             AUTO_PHOSPHORYLATION,
                                             PHOSPHO_TRANSFER,
                                             UBIQUITISATION,
                                             SUMOYLATION,
                                             SELENYLATION,
                                             GLYCOSYLATION,
                                             PRENYLATION,
                                             METHYLATION,
                                             ACETYLATION,
                                             PALMITOYLATION,
                                             PROTONATION,
                                             SULPHATION,
                                             PEGYLATION,
                                             MYRISTOYLATION,
                                             HYDROXYLATION,
                                             SECRETION,
                                             SINK_PROTEASOMAL_DEGRADATION,
                                             OXIDATION,
                                             MUTATION,
                                             UNKNOWN_TRANSITION,

                                             // Edge Annotations
                                             EDGE_ACTIVATES,
                                             EDGE_INHIBITS,
                                             EDGE_CATALYSIS,

                                             // Dummy Component
                                             DUMMY_COMPONENT,

                                             // No mEPN Notation
                                             NONE
                                           }

    /**
    *  GraphmlShapesGroup1 graphml shapes which will be functioning as transitions in the mSPN simulation.
    */
    public static final GraphmlShapesGroup1[] GRAPHML_SHAPES_TO_TRANSITIONS = {
                                                                                // Boolean Logic Operators
                                                                                AND,

                                                                                // Process Nodes
                                                                                BINDING,
                                                                                OLIGOMERIZATION,
                                                                                CLEAVAGE,
                                                                                AUTO_CLEAVAGE,
                                                                                DISSOCIATION,
                                                                                RATE_LIMITING_CATALYSIS,
                                                                                CATALYSIS,
                                                                                AUTO_CATALYSIS,
                                                                                TRANSLOCATION,
                                                                                TRANSCRIPTION_TRANSLATION,
                                                                                ACTIVATION,
                                                                                INHIBITION,
                                                                                PHOSPHORYLATION,
                                                                                DEPHOSPHORYLATION,
                                                                                AUTO_PHOSPHORYLATION,
                                                                                PHOSPHO_TRANSFER,
                                                                                UBIQUITISATION,
                                                                                SUMOYLATION,
                                                                                SELENYLATION,
                                                                                GLYCOSYLATION,
                                                                                PRENYLATION,
                                                                                METHYLATION,
                                                                                ACETYLATION,
                                                                                PALMITOYLATION,
                                                                                PROTONATION,
                                                                                SULPHATION,
                                                                                PEGYLATION,
                                                                                MYRISTOYLATION,
                                                                                HYDROXYLATION,
                                                                                SECRETION,
                                                                                SINK_PROTEASOMAL_DEGRADATION,
                                                                                OXIDATION,
                                                                                MUTATION,
                                                                                UNKNOWN_TRANSITION
                                                                              };

    /**
    *  Look-up table 1, used to do a look-up for name & shape but also returning color.
    *  Type Tuple7<String, String, GraphmlShapesGroup1, Color, Float, Shapes2D, Shapes3D>.
    */
    public static final Tuple7[] GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_1 = {
                                                                          // Boolean Logic Operators
                                                                          Tuples.tuple("&",   "ellipse", AND,                          decode("#FF9900"), 5.0f, CIRCLE, TORUS_8_PETALS), //index 0
                                                                          Tuples.tuple("OR",  "ellipse", OR,                           decode("#CC99FF"), 5.0f, CIRCLE, SAUCER_4_PETALS), //index 1

                                                                          // Process Nodes
                                                                          Tuples.tuple("B",   "ellipse", BINDING,                      decode("#FFFF99"), 5.0f, CIRCLE, SPHERE), //index 2
                                                                          Tuples.tuple("O",   "ellipse", OLIGOMERIZATION,              decode("#FFFF99"), 5.0f, CIRCLE, SPHERE), //index 3
                                                                          Tuples.tuple("X",   "ellipse", CLEAVAGE,                     decode("#BFBFBF"), 5.0f, CIRCLE, SPHERE), //index 4
                                                                          Tuples.tuple("AX",  "ellipse", AUTO_CLEAVAGE,                decode("#BFBFBF"), 5.0f, CIRCLE, SPHERE), //index 5
                                                                          Tuples.tuple("D",   "ellipse", DISSOCIATION,                 decode("#FFCC99"), 5.0f, CIRCLE, SPHERE), //index 6
                                                                          Tuples.tuple("RLC", "ellipse", RATE_LIMITING_CATALYSIS,      decode("#9999FF"), 5.0f, CIRCLE, SPHERE), //index 7
                                                                          Tuples.tuple("C",   "ellipse", CATALYSIS,                    decode("#9999FF"), 5.0f, CIRCLE, SPHERE), //index 8
                                                                          Tuples.tuple("AC",  "ellipse", AUTO_CATALYSIS,               decode("#9999FF"), 5.0f, CIRCLE, SPHERE), //index 9
                                                                          Tuples.tuple("T",   "ellipse", TRANSLOCATION,                decode("#00CCFF"), 5.0f, CIRCLE, SPHERE), //index 10
                                                                          Tuples.tuple("TL",  "ellipse", TRANSCRIPTION_TRANSLATION,    decode("#99CCFF"), 5.0f, CIRCLE, SPHERE), //index 11
                                                                          Tuples.tuple("A",   "ellipse", ACTIVATION,                   decode("#00CC33"), 5.0f, CIRCLE, SPHERE), //index 12
                                                                          Tuples.tuple("I",   "ellipse", INHIBITION,                   decode("#FF0000"), 5.0f, CIRCLE, SPHERE), //index 13
                                                                          Tuples.tuple("P",   "ellipse", PHOSPHORYLATION,              decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 14
                                                                          Tuples.tuple("-P",  "ellipse", DEPHOSPHORYLATION,            decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 15
                                                                          Tuples.tuple("AP",  "ellipse", AUTO_PHOSPHORYLATION,         decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 16
                                                                          Tuples.tuple("PT",  "ellipse", PHOSPHO_TRANSFER,             decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 17
                                                                          Tuples.tuple("Ub",  "ellipse", UBIQUITISATION,               decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 18
                                                                          Tuples.tuple("Su",  "ellipse", SUMOYLATION,                  decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 19
                                                                          Tuples.tuple("Se",  "ellipse", SELENYLATION,                 decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 20
                                                                          Tuples.tuple("Gy",  "ellipse", GLYCOSYLATION,                decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 21
                                                                          Tuples.tuple("Pr",  "ellipse", PRENYLATION,                  decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 22
                                                                          Tuples.tuple("Me",  "ellipse", METHYLATION,                  decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 23
                                                                          Tuples.tuple("Ac",  "ellipse", ACETYLATION,                  decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 24
                                                                          Tuples.tuple("Pa",  "ellipse", PALMITOYLATION,               decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 25
                                                                          Tuples.tuple("H+",  "ellipse", PROTONATION,                  decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 26
                                                                          Tuples.tuple("Sp",  "ellipse", SULPHATION,                   decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 27
                                                                          Tuples.tuple("Pe",  "ellipse", PEGYLATION,                   decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 28
                                                                          Tuples.tuple("My",  "ellipse", MYRISTOYLATION,               decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 29
                                                                          Tuples.tuple("OH",  "ellipse", HYDROXYLATION,                decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 30
                                                                          Tuples.tuple("S",   "ellipse", SECRETION,                    decode("#CCFFCC"), 5.0f, CIRCLE, SPHERE), //index 31
                                                                          Tuples.tuple("/",   "ellipse", SINK_PROTEASOMAL_DEGRADATION, decode("#FFFFFF"), 5.0f, CIRCLE, TORUS), //index 32
                                                                          Tuples.tuple("Ox",  "ellipse", OXIDATION,                    decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 33
                                                                          Tuples.tuple("M",   "ellipse", MUTATION,                     decode("#FF99CC"), 5.0f, CIRCLE, SPHERE), //index 34
                                                                          Tuples.tuple("?",   "ellipse", UNKNOWN_TRANSITION,           decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE), //index 35

                                                                          // Edge Annotations
                                                                          Tuples.tuple("A",   "diamond", EDGE_ACTIVATES,               decode("#00CC33"), 5.0f, DIAMOND, OCTAHEDRON), //index 36
                                                                          Tuples.tuple("I",   "diamond", EDGE_INHIBITS,                decode("#FF0000"), 5.0f, DIAMOND, OCTAHEDRON), //index 37
                                                                          Tuples.tuple("C",   "diamond", EDGE_CATALYSIS,               decode("#CC99FF"), 5.0f, DIAMOND, OCTAHEDRON), //index 38

                                                                          // Dummy Component
                                                                          Tuples.tuple("",    "ellipse", DUMMY_COMPONENT,              decode("#FFFFFF"), 3.0f, CIRCLE, SPHERE) //index 39
                                                                        };

    /**
    *  Shapes group 2 enumeration, used to do a look-up for shape & color.
    */
    public static enum GraphmlShapesGroup2 {
                                             // Other
                                             PATHWAY_MODULE,
                                             PATHWAY_OUTPUT,

                                             // Petri Net Transition Nodes
                                             TRANSITION_VERTICAL,
                                             TRANSITION_HORIZONTAL,
                                             TRANSITION_DIAMOND,

                                             // No mEPN Notation
                                             NONE
                                           }

    /**
    *  Look-up table 2, used to do a look-up for shape & color.
    *  Type Tuple6<String, GraphmlShapesGroup2, Color, Float, Shapes2D, Shapes3D>.
    */
    public static final Tuple6[] GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_2 = {
                                                                          // Other
                                                                          Tuples.tuple("octagon",   PATHWAY_MODULE,        decode("#00FF00"), 5.0f, HEXAGON,                       DODECAHEDRON), //index 0
                                                                          Tuples.tuple("octagon",   PATHWAY_OUTPUT,        decode("#F0FFFF"), 5.0f, HEXAGON,                       DODECAHEDRON), //index 1

                                                                          // Petri Net Transition Nodes
                                                                          Tuples.tuple("rectangle", TRANSITION_VERTICAL,   decode("#000000"), 5.0f, Shapes2D.RECTANGLE_VERTICAL,   Shapes3D.RECTANGLE_VERTICAL), //index 2
                                                                          Tuples.tuple("rectangle", TRANSITION_HORIZONTAL, decode("#000000"), 5.0f, Shapes2D.RECTANGLE_HORIZONTAL, Shapes3D.RECTANGLE_HORIZONTAL), // distinguish by width/height sizes //index 3
                                                                          Tuples.tuple("diamond",   TRANSITION_DIAMOND,    decode("#000000"), 4.0f, DIAMOND,                       OCTAHEDRON), //index 4
                                                                       };

    /**
    *  Shapes group 3 enumeration, used to do a look-up for shape only.
    */
    public static enum GraphmlShapesGroup3 {
                                             // Components
                                             PROTEIN_COMPLEX,
                                             PROTEIN_PEPTIDE,
                                             GENE,
                                             DNA_SEQUENCE, //used for BioPAX DnaRegion
                                             SIMPLE_BIOCHEMICAL,
                                             GENERIC_ENTITY,
                                             DRUG,
                                             ION_SIMPLE_MOLECULE,

                                             // Other
                                             ENERGY_MOLECULAR_TRANSFER,
                                             CONDITIONAL_SWITCH,

                                             //BioPAX
                                             DNA,
                                             RNA,
                                             RNA_REGION,

                                             // No mEPN Notation
                                             NONE
                                           }

    /**
    *  Look-up table 3, used to do a look-up for shape only, color used for BL-side rendering colorization so as to avoid random node color assignment.
    *  Type Tuple6<String, GraphmlShapesGroup3, Color, Float, Shapes2D, Shapes3D>.
    */
    public static final Tuple6[] GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3 = {
                                                                          // Components
                                                                          Tuples.tuple("roundrectangle", PROTEIN_COMPLEX,           decode("#FFFF99"), 10.0f, ROUND_RECTANGLE, Shapes3D.ROUND_CUBE_LARGE), //index 0
                                                                          Tuples.tuple("roundrectangle", PROTEIN_PEPTIDE,           decode("#CCFFFF"),  7.0f, ROUND_RECTANGLE, Shapes3D.ROUND_CUBE_THIN), // distinguish by using ':' in name //index 1
                                                                          Tuples.tuple("rectangle",      GENE,                      decode("#CCCC00"), 25.0f, RECTANGLE,       Shapes3D.RECTANGLE_HORIZONTAL), //index 2
                                                                          Tuples.tuple("parallelogram",  DNA_SEQUENCE,              decode("#CCCC00"),  6.0f, PARALLELOGRAM,   Shapes3D.CONE_RIGHT), //index 3
                                                                          Tuples.tuple("hexagon",        SIMPLE_BIOCHEMICAL,        decode("#FFA200"), 10.0f, HEXAGON,         Shapes3D.PINEAPPLE_SLICE_TOROID), //index 4
                                                                          Tuples.tuple("ellipse",        GENERIC_ENTITY,            decode("#CC99FF"), 15.5f, CIRCLE,          Shapes3D.PINEAPPLE_SLICE_ELLIPSOID), //index 5
                                                                          Tuples.tuple("trapezoid",      DRUG,                      decode("#FFFF00"),  5.0f, TRAPEZOID1,      Shapes3D.DOUBLE_PYRAMID_THIN), //index 6
                                                                          Tuples.tuple("diamond",        ION_SIMPLE_MOLECULE,       decode("#C0C0C0"),  5.0f, DIAMOND,         Shapes3D.DOUBLE_PYRAMID_LARGE), //index 7

                                                                          // Other
                                                                          Tuples.tuple("trapezoid2",     ENERGY_MOLECULAR_TRANSFER, decode("#FFFFFF"), 10.0f, TRAPEZOID2,      Shapes3D.TRAPEZOID_DOWN), //index 8
                                                                          Tuples.tuple("octagon",        CONDITIONAL_SWITCH,        decode("#FF0000"), 20.0f, OCTAGON,         Shapes3D.ICOSAHEDRON), //index 9

                                                                          //BioPAX
                                                                          Tuples.tuple("rectangle",      DNA,                      decode("#CCCC00"), 25.0f, RECTANGLE,       Shapes3D.GENE_MODEL), //index 10
                                                                          Tuples.tuple("parallelogram",  RNA,                      decode("#CCCC00"), 25.0f, PARALLELOGRAM,   Shapes3D.GENE_MODEL), //index 11 //TODO SINGLE HELIX
                                                                          Tuples.tuple("parallelogram",  RNA_REGION,               decode("#CCCC00"), 25.0f, PARALLELOGRAM,   Shapes3D.DUMB_BELL) //index 12 //DUMBELL
                                                                       };

    /**
     * Map of BioPAX PhysicalEntity, Gene and Pathway class names to mEPN shapes.
     * Includes new shapes for BioPAX entities that do not map readily to mEPN.
     * @return a Map with key BioPAX type and value a tuple describing node shape, color and size
     */
    public static final Map<String, Tuple6> BIOPAX_MEPN_MAP;


    /**
     * Map of BioPAX Interaction class names to mEPN shapes.
     * Includes new shapes for BioPAX entities that do not map readily to mEPN.
     * @return a Map with key BioPAX type and value a tuple describing node shape, color and size
     */
    public static final Map<String, Tuple7> BIOPAX_MEPN_INTERACTION_MAP;

    //Create BIOPAX_MEPN_MAP
    static
    {
        Map<String, Tuple6> entityNameMap = new HashMap<String, Tuple6>();

        //TODO replace array index with method to search lookup table?
        //physical entities
        entityNameMap.put("Complex", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[0]); //PROTEIN_COMPLEX
        entityNameMap.put("Dna", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[10]); //DNA
        entityNameMap.put("DnaRegion", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[3]); //DNA_SEQUENCE
        entityNameMap.put("Rna", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[11]); //RNA
        entityNameMap.put("RnaRegion", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[12]); //RNA_REGION
        entityNameMap.put("NucleicAcid", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[2]); //GENE
        entityNameMap.put("Protein", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[1]); //PROTEIN_PEPTIDE
        entityNameMap.put("SimplePhysicalEntity", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[5]); //GENERIC_ENTITY
        entityNameMap.put("SmallMolecule", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[7]); //ION_SIMPLE_MOLECULE

        entityNameMap.put("PhysicalEntity", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[5]); //GENERIC_ENTITY (default)

        entityNameMap.put("Pathway", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_2[0]); //PATHWAY_MODULE

        entityNameMap.put("Gene", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[2]); //GENE

        BIOPAX_MEPN_MAP = Collections.unmodifiableMap(entityNameMap);


        //interactions
        //BioPAX: BiochemicalReaction, Catalysis, ComplexAssembly, Control,     Conversion, Degradation, GeneticInteraction, Modulation MOD, MolecularInteraction, TemplateReaction TRE, TemplateReactionRegulation TRR, Transport T, TransportWithBiochemicalReaction  TWB
        //mEPN: catalysis,          catalysis,   binding        ,( new CTL)   new CON     sink        new(GI)
        Map<String, Tuple7> interactionNameMap = new HashMap<String, Tuple7>();

        interactionNameMap.put("BiochemicalReaction", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_1[8]); //CATALYSIS
        interactionNameMap.put("Catalysis", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_1[8]); //CATALYSIS
        interactionNameMap.put("ComplexAssembly", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_1[2]); //BINDING

        interactionNameMap.put("Control",                   Tuples.tuple("CTL",   "ellipse", UNKNOWN_TRANSITION, decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE)); //CTL (new)
        interactionNameMap.put("Conversion",                Tuples.tuple("CON",   "ellipse", UNKNOWN_TRANSITION, decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE));  //CON (new)

        interactionNameMap.put("Degradation", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_1[2]); //SINK_PROTEASOMAL_DEGRADATION

        interactionNameMap.put("GeneticInteraction",         Tuples.tuple("GI",   "ellipse", UNKNOWN_TRANSITION, decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE)); //GI (new)
        interactionNameMap.put("Modulation",                 Tuples.tuple("MOD",   "ellipse", UNKNOWN_TRANSITION, decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE)); //MOD (new)
        interactionNameMap.put("MolecularInteraction",       Tuples.tuple("MI",   "ellipse", UNKNOWN_TRANSITION, decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE)); //MI (new)
        interactionNameMap.put("TemplateReaction",           Tuples.tuple("TRE",   "ellipse", UNKNOWN_TRANSITION, decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE)); //TRE (new)
        interactionNameMap.put("TemplateReactionRegulation", Tuples.tuple("TRR",   "ellipse", UNKNOWN_TRANSITION, decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE));  //TRR (new)

        interactionNameMap.put("Transport", GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_1[10]); //TRANSLOCATION

        interactionNameMap.put("TransportWithBiochemicalReaction", Tuples.tuple("TWB",   "ellipse", UNKNOWN_TRANSITION, decode("#F0FFFF"), 5.0f, CIRCLE, SPHERE)); //TWB (new)

        interactionNameMap.put("Interaction",  GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_1[35]);  //UNKNOWN_TRANSITION (default)

        //        return Tuples.tuple(GraphmlShapesGroup1.NONE, Color.BLACK, 0.0f, CIRCLE, SPHERE, false);


        BIOPAX_MEPN_INTERACTION_MAP = Collections.unmodifiableMap(interactionNameMap);
    }

    /**
    *  Graphml Petri Net inhibitor arrowhead look-up table.
    */
    public static final String[] GRAPHML_PETRI_NET_INHIBITOR_ARROWHEAD_LOOK_UP_TABLE = { "diamond", "t_shape", "white_diamond", "none" };


}
