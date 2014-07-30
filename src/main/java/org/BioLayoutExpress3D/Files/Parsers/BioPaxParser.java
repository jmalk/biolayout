package org.BioLayoutExpress3D.Files.Parsers;

import java.awt.Color;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.DataStructures.Tuple6;
import org.BioLayoutExpress3D.DataStructures.Tuple7;
import org.BioLayoutExpress3D.Environment.GlobalEnvironment.Shapes2D;
import org.BioLayoutExpress3D.Environment.GlobalEnvironment.Shapes3D;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.Network.GraphmlLookUpmEPNTables.GraphmlShapesGroup1;
import org.BioLayoutExpress3D.Network.GraphmlLookUpmEPNTables.GraphmlShapesGroup2;
import org.BioLayoutExpress3D.Network.GraphmlLookUpmEPNTables.GraphmlShapesGroup3;
import org.biopax.paxtools.converter.LevelUpgrader;
import org.biopax.paxtools.io.BioPAXIOHandler;
import org.biopax.paxtools.io.SimpleIOHandler;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.BioPAXLevel;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level3.Complex;
import org.biopax.paxtools.model.level3.Control;
import org.biopax.paxtools.model.level3.Conversion;
import org.biopax.paxtools.model.level3.Dna;
import org.biopax.paxtools.model.level3.DnaRegion;
import org.biopax.paxtools.model.level3.Entity;
import org.biopax.paxtools.model.level3.Gene;
import org.biopax.paxtools.model.level3.GeneticInteraction;
import org.biopax.paxtools.model.level3.Interaction;
import org.biopax.paxtools.model.level3.MolecularInteraction;
import org.biopax.paxtools.model.level3.NucleicAcid;
import org.biopax.paxtools.model.level3.Pathway;
import org.biopax.paxtools.model.level3.Process;
import org.biopax.paxtools.model.level3.PhysicalEntity;
import org.biopax.paxtools.model.level3.Protein;
import org.biopax.paxtools.model.level3.Rna;
import org.biopax.paxtools.model.level3.RnaRegion;
import org.biopax.paxtools.model.level3.SimplePhysicalEntity;
import org.biopax.paxtools.model.level3.SmallMolecule;
import org.biopax.paxtools.model.level3.TemplateReaction;

/**
* Parser for BioPAX Level 3 OWL encoded as RDF/XML. 
* Uses PaxTools library.
* @author Derek Wright
*/

public final class BioPaxParser extends CoreParser
{
    private static final Logger logger = Logger.getLogger(BioPaxParser.class.getName());
    
    /**
     * Multiplier to resize nodes to accommodate mEPN glyphs in a network graph.
     */
    public static final double NODE_RESIZE_FACTOR = 0.5d;
    
    /**
     * Name of Class Set
     */
    public static final String CLASS_SET = "BioPAX"; 
    
    private BioPAXIOHandler handler;
    private HashMap<Entity, Vertex> entityVertexMap = null; //created during parsing

    public BioPaxParser(NetworkContainer nc, LayoutFrame layoutFrame)
    {
        super(nc, layoutFrame);
        layoutFrame.setNodeResizeFactor(NODE_RESIZE_FACTOR); //mEPN glyphs are too big for network graph, resize proportionally
    }

    /**
     * Initialize the parser.
     * @param file - the file to be parsed
     * @param fileExtension - the extension of the file ("owl")
     * @return true if parser initialized successfully, false if parser not initialized successfully
     */
    @Override
    public boolean init(File file, String fileExtension)
    {
        this.file = file;
        
        //use JAXP reader rather than Jena reader as only XML files expected + handles large files with high performance        
        handler = new SimpleIOHandler(); //auto-detects BioPAX level, default level 3
        return true;
    }
    
    /**
     * Parse the OWL file and create a network.
     * @return true if parsing successful, otherwise false
     */
    @Override
    public boolean parse()
    {
        isSuccessful = false;
        nc.setOptimized(false);

        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();

        try
        {
            int progressCounter = 0;
            layoutProgressBarDialog.prepareProgressBar(2, "Parsing " + file.getName());
            layoutProgressBarDialog.startProgressBar();
            layoutProgressBarDialog.incrementProgress(++progressCounter);
            
            //int edgeCounter = 1;
            Model model = handler.convertFromOWL(new FileInputStream(file)); //construct object model from OWL file
            
            //Query BioPAX level and upgrade if below level 3
            BioPAXLevel level = model.getLevel();
            switch(level)
            {
                case L2:
                case L1:
                    LevelUpgrader upgrader = new LevelUpgrader();
                    model = upgrader.filter(model); //replace Level 1 or 2 model with Level 3 model
                    break;
            }
            
            Set<Entity> modelEntitySet = model.getObjects(Entity.class); //get all Entities in the model
           
            //create a graph node for each entity            
            logger.fine(modelEntitySet.size() + " Entities parsed from " + file.getName());

            entityVertexMap = new HashMap<Entity, Vertex>(modelEntitySet.size());
            int setIndex = 0; //loop counter
            for(Entity entity: modelEntitySet)
            {
                logger.finer("Entity RDFId: " + entity.getRDFId());
                logger.finer("Entity displayName: " + entity.getDisplayName());
                
                String xrefs = Arrays.toString(entity.getXref().toArray());
                logger.finer("Entity Xrefs: " + xrefs);
                
                Vertex vertex;
                String vertexName = "" + entity.getDisplayName(); //"null" if no displayName //TODO use XRefs?
                vertex = new Vertex(vertexName, nc);
                if(entity instanceof Interaction)
                {
                    Interaction interaction = (Interaction)entity;
                    Tuple7 interactionShape;
                    interactionShape = lookupInteractionShape(interaction);
                    
                    setVertexPropertiesInteraction(vertex, interactionShape);
               }
                else
                {
                    Tuple6 entityShape = lookupEntityShape(entity);
                    if(entityShape.second instanceof GraphmlShapesGroup2) //Pathway
                    {
                        setVertexPropertiesPathway(vertex, entityShape);
                    }
                    else //PhysicalEntity, Gene
                    {
                        setVertexPropertiesEntity(vertex, entityShape);
                    }
               }
                nc.getVerticesMap().put(vertex.getVertexName() + "#" + setIndex, vertex); //create a unique name by numbering the vertices
                
                entityVertexMap.put(entity, vertex);
                
                //create class set
                layoutFrame.getNetworkRootContainer().getLayoutClassSetsManager().createNewClassSet(CLASS_SET);
                layoutFrame.getNetworkRootContainer().getLayoutClassSetsManager().switchClassSet(CLASS_SET);
            
                //convert PaxTools Java class name to something human readable and use as class viewer class name
                String className = entity.getClass().getSimpleName();
                className = className.replace("Impl", ""); //trim Impl from the concrete class name
                className = splitCamelCase(className); //split class name into words

                //assign vertices to classes
                LayoutClasses layoutClasses = layoutFrame.getNetworkRootContainer().getLayoutClassSetsManager().getCurrentClassSetAllClasses();
                VertexClass vertexClass = layoutClasses.createClass(className);
                layoutClasses.setClass(vertex, vertexClass);
                
                setIndex++;
            }
            
            /*
             * Connect all entity and participant vertices
             * 
             * Algorithm:
             * For each Entity
             *  get Vertex
             *  get participant Interactions
             *  for each participant Interaction
             *      get participant's Vertex
             *      connect to entity Vertex
             */
                        
            Set<Entity> graphEntitySet = entityVertexMap.keySet();
            logger.fine("Entity Set has " + graphEntitySet.size() + " Entities");
            
            int hasInteractionCount = 0; //TEST
            
            for(Entity entity: graphEntitySet)
            {
                //if physical entity is component of complex, create edge
                if(entity instanceof Complex)
                {
                    Complex complex = (Complex) entity;
                    Set<PhysicalEntity> components = complex.getComponent();
                    logger.fine("Complex contains " + components.size() + " components");
                    for(PhysicalEntity component: components)
                    {
                        this.connectEntityVertices(component, complex, 0.0f); //TODO stoichiometry as edge weight?
                    }
                }
                
                if(entity instanceof Pathway)
                {
                    Pathway pathway = (Pathway) entity;
                    Set<Process> processes = pathway.getPathwayComponent();
                    logger.fine("Pathway contains " + processes.size() + " processes");
                    for(Process process: processes)
                    {
                        this.connectEntityVertices(process, pathway, 0.0f);
                    }
                    //TODO PathwayStep - label edge with order?
                }
                
                //memberPhysicalEntity - defines generic groups of PhysicalEntity - legacy but used by Reactome
                if(entity instanceof PhysicalEntity)
                {
                    PhysicalEntity physicalEntity = (PhysicalEntity)entity;
                    Set<PhysicalEntity> members = physicalEntity.getMemberPhysicalEntity();
                    logger.fine("Physical entity has " + members.size() + " members");
                    for(PhysicalEntity member: members)
                    {
                        this.connectEntityVertices(member, physicalEntity, 0.0f);
                    }
                }
                
                //TODO EntityReference?
                
                Set<Interaction> interactionSet = entity.getParticipantOf();
                
                //TEST
                logger.fine("Entity " + entity.getRDFId() + " participates in " + interactionSet.size() + " Interactions");
                if(interactionSet.size() > 0)
                {
                    hasInteractionCount++;
                }
                
                for(Interaction interaction: interactionSet)
                {
                    this.connectEntityVertices(entity, interaction, 0.0f);
                }
            }
            
            //TODO check for entities with no edges - add self edge?
            
            logger.fine(hasInteractionCount + " Entities have Interactions");
           
            layoutProgressBarDialog.incrementProgress(++progressCounter);
           
            isSuccessful = true;
        }
        catch(FileNotFoundException e)
        {
            logger.warning(e.getMessage());
            isSuccessful = false;
        }
        finally
        {
            layoutProgressBarDialog.endProgressBar();
        }

        return isSuccessful;
    }

    /**
     * Create a graph Edge between the Nodes mapped to a pair of Entities.
     * @param entityFrom
     * @param entityTo
     * @param edgeWeight
     * @return the created Edge
     */
    private Edge connectEntityVertices(Entity entityFrom, Entity entityTo, float edgeWeight)
    {
        Vertex vertexFrom = entityVertexMap.get(entityFrom);
        Vertex vertexTo = entityVertexMap.get(entityTo);
        Edge edge = new Edge(vertexFrom, vertexTo, edgeWeight);

        vertexFrom.addConnection(vertexTo, edge);
        vertexTo.addConnection(vertexFrom, edge);
        nc.getEdges().add(edge);
        return edge;
    }
    
    /**
     * Looks up graph node shape according to Interaction type.
     * @param interaction
     * @return a graph node shape. If no mapping found, returns shape for Interaction.
     */
    private static Tuple7 lookupInteractionShape(Interaction interaction)
    {
        Tuple7 shape;
        if(interaction instanceof TemplateReaction)
         {
             shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_INTERACTION_MAP.get("TemplateReaction");                                        
         }
         else if(interaction instanceof Control)
         {
             shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_INTERACTION_MAP.get("Control");                                        

         }
         else if(interaction instanceof Conversion)
         {
             shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_INTERACTION_MAP.get("Conversion");                                        

         }
         else if(interaction instanceof MolecularInteraction)
         {
             shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_INTERACTION_MAP.get("MolecularInteraction");                                        

         }
         else if(interaction instanceof GeneticInteraction)
         {
             shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_INTERACTION_MAP.get("GeneticInteraction");                                        

         }
         else
         {
             shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_INTERACTION_MAP.get("Interaction");                                        

         }
            
        return shape;
    }

    /**
     * Looks up graph node shape according to Entity type. 
     * To be used for PhysicalEntity, Pathway and Gene.
     * @param entity
     * @return a graph node shape. If no mapping found, returns shape for SimplePhysicalEntity.
     */
    private static Tuple6 lookupEntityShape(Entity entity)
    {
        Tuple6 shape;
        if(entity instanceof PhysicalEntity)
        {
            if(entity instanceof Complex){
                shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_MAP.get("Complex");
            }
            else if(entity instanceof Dna)
            {
                shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_MAP.get("Dna");
            }
            else if(entity instanceof DnaRegion)
            {
                shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_MAP.get("DnaRegion");                        
            }
            else if(entity instanceof Rna)
            {
                shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_MAP.get("Rna");                        
            }
            else if(entity instanceof RnaRegion)
            {
                shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_MAP.get("RnaRegion");                        
            }
            else if(entity instanceof NucleicAcid)
            {
                shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_MAP.get("NucleicAcid");                                                
            }
            else if(entity instanceof Protein)
            {
                shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_MAP.get("Protein");                        
            }
            else if(entity instanceof SmallMolecule)
            {
                shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_MAP.get("SmallMolecule");                        
            }
            else
            {
                shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_MAP.get("SimplePhysicalEntity"); //default generic entity                      
            }
        }
        else if(entity instanceof Pathway || entity instanceof Process) //superinterface of Pathway
        {
            shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_MAP.get("Pathway");                                 
        }
        else if (entity instanceof Gene)
        {
            shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_MAP.get("Gene");
        }
        else
        {
            shape = GraphmlLookUpmEPNTables.BIOPAX_MEPN_INTERACTION_MAP.get("PhysicalEntity");                                                    
        }        
        return shape;
   }
    
    /**
     * Set display properties of a Vertex (graph node) from mEPN-style properties.
     * Expects a tuple in the format found in GraphmlLookUpmEPNTables.GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3
     * @param shapeLookup - a tuple of size, shape and color
     */
    private static void setVertexPropertiesEntity(Vertex vertex, Tuple6 shapeLookup)
    {
        vertex.setVertex2DShape((Shapes2D)shapeLookup.fifth);
        vertex.setVertex3DShape((Shapes3D)shapeLookup.sixth);
        vertex.setVertexSize((Float)shapeLookup.fourth);
        vertex.setVertexColor((Color)shapeLookup.third);
    }
    
    //special case for pathway - uses different enumeration
    private static void setVertexPropertiesPathway(Vertex vertex, Tuple6 shapeLookup)
    {
        vertex.setVertex2DShape((Shapes2D)shapeLookup.fifth);
        vertex.setVertex3DShape((Shapes3D)shapeLookup.sixth);
        vertex.setVertexSize((Float)shapeLookup.fourth);
        vertex.setVertexColor((Color)shapeLookup.third);
    }

    //interaction
   private static void setVertexPropertiesInteraction(Vertex vertex, Tuple7 shapeLookup)
   {
        vertex.setVertex2DShape((Shapes2D)shapeLookup.sixth);
        vertex.setVertex3DShape((Shapes3D)shapeLookup.seventh);
        vertex.setVertexSize((Float)shapeLookup.fifth);
        vertex.setVertexColor((Color)shapeLookup.fourth);
       
   }

    public HashMap<Entity, Vertex> getEntityVertexMap() {
        return entityVertexMap;
    }
    
    /**
     * Utility to split a camel case String into human readable form.
     * @param s - String to be split
     * @return - human readable string
     */
    public static String splitCamelCase(String s) {
       return s.replaceAll(
          String.format("%s|%s|%s",
             "(?<=[A-Z])(?=[A-Z][a-z])",
             "(?<=[^A-Z])(?=[A-Z])",
             "(?<=[A-Za-z])(?=[^A-Za-z])"
          ),
          " "
       );
    }    
}