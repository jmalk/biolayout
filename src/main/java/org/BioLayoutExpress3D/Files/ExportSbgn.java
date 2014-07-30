package org.BioLayoutExpress3D.Files;

import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.util.HashMap;
import java.util.List;
import org.sbgn.*;
import org.sbgn.bindings.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.DataStructures.Tuple2;
import org.BioLayoutExpress3D.DataStructures.Tuple6;
import org.BioLayoutExpress3D.StaticLibraries.*;
import org.BioLayoutExpress3D.Graph.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;
import org.BioLayoutExpress3D.Network.GraphmlComponentContainer;
import org.BioLayoutExpress3D.Network.GraphmlNetworkContainer;
import org.BioLayoutExpress3D.Network.NetworkRootContainer;
import org.BioLayoutExpress3D.Utils.Point3D;

/**
 * The ExportSbgn class is used to export to SBGN-ML files
 *
 * @author Tim Angus <tim.angus@roslin.ed.ac.uk>
 *
*/
public final class ExportSbgn
{
    // Constant to adjust to an appropriate scale for SBGN files
    final static float SCALE = 40.0f;
    final static String PROCESS_EDGE_GLYPH_INDICATOR = "pegi";
    final static String ENERGY_TRANSFER_GLYPH_INDICATOR = "em/t";

    private LayoutFrame layoutFrame = null;
    private JFileChooser fileChooser = null;
    private AbstractAction exportSbgnAction = null;
    private FileNameExtensionFilter fileNameExtensionFilterSbgn = null;

    private NetworkRootContainer nc;
    private GraphmlNetworkContainer gnc;

    public ExportSbgn(LayoutFrame layoutFrame)
    {
        this.layoutFrame = layoutFrame;

        initComponents();
    }

    private void initComponents()
    {
        fileNameExtensionFilterSbgn = new FileNameExtensionFilter("Save as an SBGN File", "sbgn");

        String saveFilePath = FILE_CHOOSER_PATH.get().substring(
                0, FILE_CHOOSER_PATH.get().lastIndexOf(System.getProperty("file.separator")) + 1);
        fileChooser = new JFileChooser(saveFilePath);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(fileNameExtensionFilterSbgn);

        exportSbgnAction = new AbstractAction("SBGN File...")
        {
            @Override
            public void actionPerformed(ActionEvent action)
            {
                setFileChooser("Save SBGN File");
                save();
            }
        };
        exportSbgnAction.setEnabled(false);
    }

    private void setFileChooser(String fileChooserTitle)
    {
        fileChooser.setDialogTitle(fileChooserTitle);
        fileChooser.setSelectedFile(new File(IOUtils.getPrefix(layoutFrame.getFileNameLoaded())));
    }

    public AbstractAction getExportSbgnAction()
    {
        return exportSbgnAction;
    }

    private void save()
    {
        int dialogReturnValue;
        boolean doSaveFile = false;
        File saveFile = null;

        if (fileChooser.showSaveDialog(layoutFrame) == JFileChooser.APPROVE_OPTION)
        {
            String fileExtension = fileNameExtensionFilterSbgn.getExtensions()[0];

            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            fileName = IOUtils.removeMultipleExtensions(fileName, fileExtension);
            saveFile = new File(fileName + "." + fileExtension);

            if (saveFile.exists())
            {
                // Do you want to overwrite
                dialogReturnValue = JOptionPane.showConfirmDialog(layoutFrame,
                        "This File Already Exists.\nDo you want to Overwrite it?",
                        "This File Already Exists. Overwrite?", JOptionPane.YES_NO_CANCEL_OPTION);

                if (dialogReturnValue == JOptionPane.YES_OPTION)
                {
                    doSaveFile = true;
                }
            }
            else
            {
                doSaveFile = true;
            }
        }

        if (doSaveFile)
        {
            // saving process on its own thread, to effectively decouple it from the main GUI thread
            Thread runLightWeightThread = new Thread(new ExportSbgnProcess(saveFile));
            runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
            runLightWeightThread.start();
        }
    }

    private static final java.util.Map<String, String> LABEL_TO_GLYPH_CLASS;
    static
    {
        java.util.Map<String, String> map = new HashMap<String, String>();
        map.put("&",  "and");
        map.put("OR", "or");
        map.put("B",  "association");
        map.put("D",  "dissociation");
        map.put("O",  "process");
        map.put("X",  "process");
        map.put("AX", "process");
        map.put("AC", "process");
        map.put("C",  "process");
        map.put("TL", "process");
        map.put("T",  "process");
        map.put("I",  "process");
        map.put("A",  "process");
        map.put("-P", "process");
        map.put("P",  "process");
        map.put("PT", "process");
        map.put("SU", "process");
        map.put("AP", "process");
        map.put("GY", "process");
        map.put("UB", "process");
        map.put("ME", "process");
        map.put("SE", "process");
        map.put("PA", "process");
        map.put("AC", "process");
        map.put("PR", "process");
        map.put("S",  "process");
        map.put("AT", "process");
        map.put("MY", "process");
        map.put("H+", "process");
        map.put("OH", "process");
        map.put("PE", "process");
        map.put("OX", "process");
        map.put("SEC","process");
        map.put("M",  "process");
        map.put("/",  "source and sink");
        map.put("?",  "uncertain process");
        LABEL_TO_GLYPH_CLASS = Collections.unmodifiableMap(map);
    }

    private class Component
    {
        private int n;
        private String name;
        private List<String> infoList;
        private List<String> modList;

        public Component(int n, String name, List<String> infoList, List<String> modList)
        {
            this.n = n;
            this.name = name;
            this.infoList = infoList;
            this.modList = modList;
        }

        public int getNumber() { return n; }
        public String getName() { return name; }
        public List<String> getInfoList() { return infoList; }
        public List<String> getModList() { return modList; }
    }

    private class ComponentList
    {
        private String alias;
        private List<Component> components;

        public ComponentList(String alias, List<Component> components)
        {
            this.alias = alias;
            this.components = components;
        }

        public String getAlias() { return alias; }
        public List<Component> getComponents() { return components; }
    }

    private static boolean stringIsNotWhitespace(String s)
    {
        return s.trim().length() > 0;
    }

    private ComponentList parseMepnLabel(String mepnLabel)
    {
        String[] components = mepnLabel.split("\\s*:\\s*");

        String complexAlias = null;
        if (components.length > 1)
        {
            // More than one component indicates a complex
            // In this case the last component may include an alias for the complex
            // as a whole which we must save and strip off
            // Note that a complex alias MUST be on its own line to avoid being
            // ambiguous with respect to a protein alias
            int lastComponentIndex = components.length - 1;
            Pattern aliasRegex = Pattern.compile("[^\\n]+(\\n\\s*\\(([^\\)]*)\\))");
            Matcher aliasMatcher = aliasRegex.matcher(components[lastComponentIndex]);
            if (aliasMatcher.find())
            {
                complexAlias = aliasMatcher.group(2);
                components[lastComponentIndex] = components[lastComponentIndex].replace(aliasMatcher.group(1), "");
            }
        }

        List<Component> list = new ArrayList<Component>();

        // Match this pattern: <n>PROT1[A]
        Pattern regex = Pattern.compile("(?:<([^>]+)>)?([^\\[]*)(?:\\[([^\\]]+)\\])?");
        for (String component : components)
        {
            Matcher m = regex.matcher(component);
            int n = 1; // Assume 1 until told otherwise
            String name = null;
            List<String> modList = new ArrayList<String>();

            String s;
            while (m.find())
            {
                s = m.group(1);
                if (s != null && stringIsNotWhitespace(s))
                {
                    try { n = Integer.parseInt(s); }
                    catch(NumberFormatException e)
                    {
                        n = -1;
                    }
                }

                s = m.group(2);
                if (s != null && stringIsNotWhitespace(s))
                {
                    name = s.trim();
                }

                s = m.group(3);
                if (s != null && stringIsNotWhitespace(s))
                {
                    modList.add(s.trim());
                }
            }

            Component c = new Component(n, name, new ArrayList<String>(), modList);
            list.add(c);
        }

        return new ComponentList(complexAlias, list);
    }

    private void configureComponentGlyph(String type, Component c, Glyph glyph)
    {
        Bbox glyphBbox = glyph.getBbox();

        // SBGN-ED seems to only allow three info glyphs per parent glyph edge.
        // It decides which "index" the subglyph sits in based on which third its
        // bbox sits in. So the 0.33f here is to make sure we get the subglyph
        // into a place where SBGN-ED won't just arbitrarily throw it away.
        // Craptastic.
        final float SBGN_ED_X_OFFSET = glyphBbox.getW() * 0.1f;
        final float SBGN_ED_STRIDE = 0.33f * glyphBbox.getW();
        int multimer = c.getNumber();

        if (multimer != 1)
        {
            if (multimer < 0)
            {
                c.getInfoList().add("N:?");
            }
            else
            {
                c.getInfoList().add("N:" + multimer);
            }

            glyph.setClazz(type + " multimer");
        }
        else
        {
            glyph.setClazz(type);
        }

        int infoIndex = 0;
        for (String info : c.getInfoList())
        {
            // Infos
            Glyph infoGlyph = new Glyph();
            infoGlyph.setId(glyph.getId() + ".info" + infoIndex);
            infoGlyph.setClazz("unit of information");

            Bbox bbox = new Bbox();
            bbox.setX(glyphBbox.getX() + SBGN_ED_X_OFFSET + (SBGN_ED_STRIDE * infoIndex));
            bbox.setY(glyphBbox.getY());
            infoGlyph.setBbox(bbox);

            Label label = new Label();
            label.setText(info);
            infoGlyph.setLabel(label);

            glyph.getGlyph().add(infoGlyph);

            infoIndex++;
        }

        int modIndex = 0;
        for (String mod : c.getModList())
        {
            // Mods
            Glyph multimerGlyph = new Glyph();
            multimerGlyph.setId(glyph.getId() + ".mod" + modIndex);
            multimerGlyph.setClazz("state variable");

            Bbox bbox = new Bbox();
            bbox.setX(glyphBbox.getX() + SBGN_ED_X_OFFSET + (SBGN_ED_STRIDE * modIndex));
            bbox.setY(glyphBbox.getY() + glyphBbox.getH());
            multimerGlyph.setBbox(bbox);

            Label label = new Label();
            label.setText(mod);
            multimerGlyph.setLabel(label);

            glyph.getGlyph().add(multimerGlyph);

            modIndex++;
        }

        String name = c.getName();
        if (name != null && name.length() > 0)
        {
            Label label = new Label();
            label.setText(name);
            glyph.setLabel(label);
        }
    }

    private List<Bbox> subDivideBbox(Bbox parent, int subdivisions, float targetAspect)
    {
        List<Bbox> list = new ArrayList<Bbox>();
        float parentAspect = parent.getW() / parent.getH();

        if (subdivisions > 1)
        {
            Bbox first = new Bbox();
            Bbox second = new Bbox();
            if (parentAspect < targetAspect)
            {
                // Top
                first.setW(parent.getW());
                first.setH(parent.getH() * 0.5f);
                first.setX(parent.getX());
                first.setY(parent.getY());

                // Bottom
                second.setW(parent.getW());
                second.setH(parent.getH() * 0.5f);
                second.setX(parent.getX());
                second.setY(parent.getY() + second.getH());
            }
            else
            {
                // Left
                first.setW(parent.getW() * 0.5f);
                first.setH(parent.getH());
                first.setX(parent.getX());
                first.setY(parent.getY());

                // Right
                second.setW(parent.getW() * 0.5f);
                second.setH(parent.getH());
                second.setX(parent.getX() + second.getW());
                second.setY(parent.getY());
            }

            list.addAll(subDivideBbox(first, subdivisions / 2, targetAspect));
            list.addAll(subDivideBbox(second, subdivisions / 2, targetAspect));
        }
        else
        {
            list.add(parent);
        }

        return list;
    }

    private static int compareFloats(float a, float b)
    {
        final float EPSILON = 0.01f;

        if (java.lang.Math.abs(a - b) < EPSILON)
        {
            return 0;
        }
        else
        {
            if (a < b)
            {
                return -1;
            }
            else
            {
                return 1;
            }
        }
    }

    private List<Bbox> subDivideGlyph(Glyph glyph, ComponentList cl)
    {
        final float ALIAS_VERTICAL_SPACE = 1.0f * SCALE;
        final float TARGET_ASPECT = 2.0f;
        int subdivisions = cl.getComponents().size();

        int pow2 = 1;
        while (pow2 < subdivisions)
        {
            pow2 <<= 1;
        }

        Bbox parentBbox = glyph.getBbox();
        Bbox componentsBbox = new Bbox();
        String alias = cl.getAlias();
        if (alias != null)
        {
            componentsBbox.setX(parentBbox.getX());
            componentsBbox.setY(parentBbox.getY());
            componentsBbox.setW(parentBbox.getW());
            componentsBbox.setH(parentBbox.getH() - ALIAS_VERTICAL_SPACE);

            Bbox labelBbox = new Bbox();
            labelBbox.setX(componentsBbox.getX());
            labelBbox.setY(componentsBbox.getY() + componentsBbox.getH());
            labelBbox.setW(componentsBbox.getW());
            labelBbox.setH(ALIAS_VERTICAL_SPACE);

            Label label = new Label();
            label.setText(alias);
            label.setBbox(labelBbox);
            glyph.setLabel(label);
        }
        else
        {
            componentsBbox.setX(parentBbox.getX());
            componentsBbox.setY(parentBbox.getY());
            componentsBbox.setW(parentBbox.getW());
            componentsBbox.setH(parentBbox.getH());
        }

        List<Bbox> list = subDivideBbox(componentsBbox, pow2, TARGET_ASPECT);
        List<Bbox> scaledList = new ArrayList<Bbox>(list.size());

        for (Bbox bbox : list)
        {
            final float SUB_SCALE = 0.7f;

            boolean left = compareFloats(bbox.getX(), componentsBbox.getX()) == 0;
            boolean right = compareFloats(bbox.getX() + bbox.getW(),
                    componentsBbox.getX() + componentsBbox.getW()) == 0;
            boolean top = compareFloats(bbox.getY(), componentsBbox.getY()) == 0;
            boolean bottom = compareFloats(bbox.getY() + bbox.getH(),
                    componentsBbox.getY() + componentsBbox.getH()) == 0;

            float borderWidth = (bbox.getW() - (bbox.getW() * SUB_SCALE));
            float borderHeight = (bbox.getH() - (bbox.getH() * SUB_SCALE));

            float leftBorder = left ? borderWidth * 0.5f : borderWidth * 0.25f;
            float rightBorder = right ? borderWidth * 0.5f : borderWidth * 0.25f;
            float topBorder = top ? borderHeight * 0.5f : borderHeight * 0.25f;
            float bottomBorder = bottom ? borderHeight * 0.5f : borderHeight * 0.25f;

            Bbox scaledBbox = new Bbox();
            scaledBbox.setX(bbox.getX() + leftBorder);
            scaledBbox.setW(bbox.getW() - leftBorder - rightBorder);
            scaledBbox.setY(bbox.getY() + topBorder);
            scaledBbox.setH(bbox.getH() - topBorder - bottomBorder);

            scaledList.add(scaledBbox);
        }

        Collections.sort(scaledList, new java.util.Comparator<Bbox>()
        {
            @Override
            public int compare(Bbox a, Bbox b)
            {
                if (compareFloats(a.getY(), b.getY()) == 0)
                {
                    return (a.getX() < b.getX()) ? -1 : 1;
                }
                else
                {
                    return (a.getY() < b.getY()) ? -1 : 1;
                }
            }
        });

        return scaledList;
    }

    private void configureComponentGlyph(String type, ComponentList cl, Glyph glyph)
    {
        if (cl.getComponents().size() > 1)
        {
            glyph.setClazz("complex");
            List<Glyph> subGlyphs = glyph.getGlyph();
            List<Bbox> subBboxes = subDivideGlyph(glyph, cl);

            int index = 0;
            for (Component c : cl.getComponents())
            {
                Glyph subGlyph = new Glyph();
                subGlyph.setId(glyph.getId() + "." + (index + 1));
                subGlyph.setBbox(subBboxes.get(index));
                configureComponentGlyph(type, c, subGlyph);

                subGlyphs.add(subGlyph);

                index++;
            }
        }
        else
        {
            configureComponentGlyph(type, cl.getComponents().get(0), glyph);
        }
    }

    List<String> spnGlyphIds = new ArrayList<String>();
    List<String> spnArcIds = new ArrayList<String>();

    private boolean specialiseSpnDistSpacerOrOutput(String mepnShape, String mepnLabel, Color mepnBackColor, Glyph glyph)
    {
        if (mepnLabel.isEmpty())
        {
            if (mepnShape.equals("diamond") && mepnBackColor.equals(Color.BLACK))
            {
                // Distribution
                glyph.setClazz("process");
                return true;
            }
            else if (mepnShape.equals("ellipse") && mepnBackColor.equals(Color.WHITE))
            {
                // Spacer
                glyph.setClazz(PROCESS_EDGE_GLYPH_INDICATOR);
                return true;
            }
        }

        return false;
    }

    private boolean specialiseSpnTokenInput(String mepnShape, String mepnLabel, Color mepnBackColor, Glyph glyph)
    {
        if (mepnLabel.isEmpty() && mepnBackColor.equals(Color.BLACK) &&
            (mepnShape.equals("rectangle") || mepnShape.equals("roundedrectangle")))
        {
            glyph.setClazz("source and sink");
            makeSquarePreserveArea(glyph.getBbox());
            return true;
        }

        return false;
    }

    private void specialiseSimpleBiochemical(String mepnLabel, Glyph glyph)
    {
        makeSquarePreserveArea(glyph.getBbox());

        if (mepnLabel.length() > 0)
        {
            ComponentList cl = parseMepnLabel(mepnLabel);

            if (cl.getComponents().size() == 1)
            {
                configureComponentGlyph("simple chemical", cl.getComponents().get(0), glyph);
            }
            else
            {
                // Multiple components? Just use the label directly
                Label label = new Label();
                label.setText(mepnLabel);
                glyph.setLabel(label);

                glyph.setClazz("simple chemical");
            }
        }
    }

    private boolean specialiseSbgnGlyph(String mepnShape, String mepnLabel, Color mepnBackColor, Glyph glyph)
    {
        if (specialiseSpnTokenInput(mepnShape, mepnLabel, mepnBackColor, glyph) ||
                specialiseSpnDistSpacerOrOutput(mepnShape, mepnLabel, mepnBackColor, glyph))
        {
            spnGlyphIds.add(glyph.getId());
            return true;
        }
        else if (mepnShape.equals("ellipse"))
        {
            String glyphClass = LABEL_TO_GLYPH_CLASS.get(mepnLabel.toUpperCase());
            if (glyphClass != null)
            {
                glyph.setClazz(glyphClass);
                return true;
            }

            // Assume every other ellipse is an...
            glyph.setClazz("unspecified entity");

            if (mepnLabel.length() > 0)
            {
                Label label = new Label();
                label.setText(mepnLabel);
                glyph.setLabel(label);
            }

            return true;
        }
        else if (mepnShape.equals("diamond"))
        {
            if (mepnLabel.equals("A") || mepnLabel.equals("C") || mepnLabel.equals("I"))
            {
                // In theory this only occurs in mEPN 2010 diagrams
                // This isn't a real class, but a marker for later when the edges are being processed
                glyph.setClazz(PROCESS_EDGE_GLYPH_INDICATOR + mepnLabel);

                return true;
            }
            else
            {
                // Ion/simple molecule
                specialiseSimpleBiochemical(mepnLabel, glyph);

                return true;
            }
        }
        else if (mepnShape.equals("hexagon"))
        {
            // Simple biochemical
            specialiseSimpleBiochemical(mepnLabel, glyph);

            return true;
        }
        else if (mepnShape.equals("octagon"))
        {
            // Pathway output/module
            glyph.setClazz("submap");

            if (mepnLabel.length() > 0)
            {
                Label label = new Label();
                label.setText(mepnLabel);
                glyph.setLabel(label);
            }

            return true;
        }
        else if (mepnShape.equals("parallelogram") || mepnShape.equals("rectangle"))
        {
            // Gene/DNA sequence
            ComponentList cl = parseMepnLabel(mepnLabel);

            if (cl.getComponents().size() > 0)
            {
                if (cl.getComponents().size() == 1)
                {
                    Component c = cl.getComponents().get(0);

                    if (mepnShape.equals("parallelogram"))
                    {
                        c.getInfoList().add("ct:grr");
                    }
                    else if (mepnShape.equals("rectangle"))
                    {
                        c.getInfoList().add("ct:gene");
                    }
                }

                configureComponentGlyph("nucleic acid feature", cl, glyph);
                return true;
            }
        }
        else if (mepnShape.equals("roundrectangle"))
        {
            // Peptide/protein/protein complex
            ComponentList cl = parseMepnLabel(mepnLabel);

            if (cl.getComponents().size() > 0)
            {
                configureComponentGlyph("macromolecule", cl, glyph);
                return true;
            }
        }
        else if (mepnShape.equals("trapezoid"))
        {
            // Drug
            glyph.setClazz("perturbing agent");

            if (mepnLabel.length() > 0)
            {
                Label label = new Label();
                label.setText(mepnLabel);
                glyph.setLabel(label);
            }

            return true;
        }
        else if (mepnShape.equals("trapezoid2"))
        {
            // Energy/molecular transfer
            // This isn't a real class, but a marker for later
            glyph.setClazz(ENERGY_TRANSFER_GLYPH_INDICATOR + mepnLabel);

            return true;
        }

        return false;
    }

    private Glyph translateNodeToSbgnGlyph(GraphNode graphNode)
    {
        float x, y;
        String id = graphNode.getNodeName();

        if (nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get())
        {
            float[] graphmlCoord = gnc.getAllGraphmlNodesMap().get(id).first;
            x = graphmlCoord[2] * SCALE;
            y = graphmlCoord[3] * SCALE;
        }
        else
        {
            x = graphNode.getX() * SCALE;
            y = graphNode.getY() * SCALE;
        }

        Glyph glyph = new Glyph();
        glyph.setId(id);

        Tuple6<float[], String[], String[], String[], String[], String> nodeData =
                gnc.getAllGraphmlNodesMap().get(graphNode.getNodeName());
        String mepnShape = nodeData.sixth;
        float mepnWidth = nodeData.first[1];
        float mepnHeight = nodeData.first[0];
        float mepnAspect = mepnWidth / mepnHeight;
        String mepnLabel = Graph.customizeNodeName(nc.getNodeName(graphNode.getNodeName()));
        Color mepnBackColor;
        try { mepnBackColor = Color.decode(nodeData.second[1]); }
        catch (Exception e)
        {
            mepnBackColor = Color.WHITE;
        }

        float width = mepnWidth * SCALE;
        float height = mepnHeight * SCALE;

        Bbox bbox = new Bbox();
        bbox.setX(x - (width * 0.5f));
        bbox.setW(width);
        bbox.setY(y - (height * 0.5f));
        bbox.setH(height);
        glyph.setBbox(bbox);

        if (!specialiseSbgnGlyph(mepnShape, mepnLabel, mepnBackColor, glyph))
        {
            // Fallback when we don't know what it is
            glyph.setClazz("unspecified entity");

            Label label = new Label();
            label.setText("Unknown entity: " + mepnLabel);
            glyph.setLabel(label);
        }

        return glyph;
    }

    private Glyph findGlyphWithId(Map map, String id)
    {
        for (Glyph glyph : map.getGlyph())
        {
            if (glyph.getId().equals(id))
            {
                return glyph;
            }
        }

        return null;
    }

    // We need a way to classify glyphs into groups for the purpose of adding clone markers; this is it
    private String metaClassStringForGlyph(Glyph glyph)
    {
        String s = "";

        if (glyph.getCompartmentRef() != null)
        {
            s += "<" + ((Glyph)glyph.getCompartmentRef()).getId() + ">";
        }

        s += glyph.getClazz();
        if (glyph.getLabel() != null)
        {
            s += "(" + glyph.getLabel().getText() + ")";
        }

        if (glyph.getGlyph() != null && glyph.getGlyph().size() > 0)
        {
            List<Glyph> subGlyphs = new ArrayList<Glyph>(glyph.getGlyph());

            // Sort the subglyph list so if they appear in a different order
            // they're still counted the same in terms of cloning
            Collections.sort(subGlyphs, new java.util.Comparator<Glyph>()
            {
                @Override
                public int compare(Glyph a, Glyph b)
                {
                    if (a.getClazz().equals(b.getClazz()))
                    {
                        String aLabel = a.getLabel() != null ? a.getLabel().getText() : "";
                        String bLabel = b.getLabel() != null ? b.getLabel().getText() : "";

                        return aLabel.compareTo(bLabel);
                    }
                    else
                    {
                        return a.getClazz().compareTo(b.getClazz());
                    }
                }
            });

            s += "[";
            boolean first = true;
            for (Glyph subGlyph : subGlyphs)
            {
                if (!first)
                {
                    s += ":";
                }
                s += metaClassStringForGlyph(subGlyph);
                first = false;
            }
            s += "]";
        }

        return s;
    }

    private List<Glyph> glyphsToNotClone = new ArrayList<Glyph>();

    private boolean canBeCloned(Glyph glyph)
    {
        List<String> cloneableClazzes = new ArrayList<String>(
                Arrays.asList(
                "unspecified entity",
                "simple chemical",
                "perturbing agent",
                "phenotype",
                "macromolecule",
                "nucleic acid feature",
                "simple chemical multimer",
                "macromolecule multimer",
                "nucleic acid feature multimer",
                "complex",
                "complex multimer"));
        String clazz = glyph.getClazz();

        return cloneableClazzes.contains(clazz) && !glyphsToNotClone.contains(glyph);
    }

    private void addCloneMarkers(Map map)
    {
        java.util.Map<String, List<String>> clonedGlyphs = new HashMap<String, List<String>>();

        for (Glyph glyph : map.getGlyph())
        {
            if (!canBeCloned(glyph))
            {
                continue;
            }

            String metaClass = metaClassStringForGlyph(glyph);

            if (!clonedGlyphs.containsKey(metaClass))
            {
                clonedGlyphs.put(metaClass, new ArrayList<String>());
            }

            clonedGlyphs.get(metaClass).add(glyph.getId());
        }

        for (java.util.Map.Entry<String, List<String>> clonedGlyph : clonedGlyphs.entrySet())
        {
            List<String> clones = clonedGlyph.getValue();

            if (clones.size() > 1)
            {
                if (DEBUG_BUILD)
                {
                    println("Clone meta class: " + clonedGlyph.getKey());
                }

                // FIXME: for particular classes we need a label as well?

                for (String id : clones)
                {
                    Glyph glyph = findGlyphWithId(map, id);
                    if (glyph != null)
                    {
                        glyph.setClone(new Glyph.Clone());
                    }
                }
            }
        }
    }

    private boolean glyphBoundedByCompartment(Glyph glyph, Glyph compartment)
    {
        Bbox compartmentBbox = compartment.getBbox();
        Bbox glyphBbox = glyph.getBbox();
        boolean left = glyphBbox.getX() >= compartmentBbox.getX();
        boolean right = glyphBbox.getX() + glyphBbox.getW() < compartmentBbox.getX() + compartmentBbox.getW();
        boolean top = glyphBbox.getY() >= compartmentBbox.getY();
        boolean bottom = glyphBbox.getY() + glyphBbox.getH() < compartmentBbox.getY() + compartmentBbox.getH();

        return left && right && top && bottom;
    }

    private void addCompartmentRefs(Map map)
    {
        List<Glyph> compartments = new ArrayList<Glyph>();
        for (Glyph glyph : map.getGlyph())
        {
            if (glyph.getClazz().equals("compartment"))
            {
                compartments.add(glyph);
            }
        }

        for (Glyph glyph : map.getGlyph())
        {
            if (glyph.getClazz().equals("compartment"))
            {
                continue;
            }

            for (Glyph compartment : compartments)
            {
                if (glyph.getCompartmentRef() != null)
                {
                    // The glyph already has a compartmentRef, check if this compartment might be better
                    Glyph existingCompartment = (Glyph)glyph.getCompartmentRef();

                    if (existingCompartment.getCompartmentOrder() > compartment.getCompartmentOrder())
                    {
                        continue;
                    }
                }

                if (glyphBoundedByCompartment(glyph, compartment))
                {
                    glyph.setCompartmentRef(compartment);
                }
            }
        }
    }

    private Point2D.Float getGlyphArcOutlyingPoint(Glyph glyph, Arc arc)
    {
        List<Arc.Next> nextList = arc.getNext();

        if (arc.getTarget() == glyph)
        {
            // Going in
            if (nextList.size() > 0)
            {
                Arc.Next next = nextList.get(nextList.size() - 1);
                return new Point2D.Float(next.getX(), next.getY());
            }
            else
            {
                Arc.Start arcStart = arc.getStart();
                return new Point2D.Float(arcStart.getX(), arcStart.getY());
            }
        }
        else if (arc.getSource() == glyph)
        {
            if (nextList.size() > 0)
            {
                Arc.Next next = nextList.get(0);
                return new Point2D.Float(next.getX(), next.getY());
            }
            else
            {
                Arc.End arcEnd = arc.getEnd();
                return new Point2D.Float(arcEnd.getX(), arcEnd.getY());
            }
        }

        return null;
    }

    private enum GlyphArcDirection { UNKNOWN, LEFT, RIGHT, TOP, BOTTOM }
    private GlyphArcDirection getGlyphArcDirection(Glyph glyph, Arc arc)
    {
        Point2D.Float start = null, end = null;
        List<Arc.Next> nextList = arc.getNext();

        if (arc.getTarget() == glyph)
        {
            // Going in
            start = getGlyphArcOutlyingPoint(glyph, arc);

            Arc.End arcEnd = arc.getEnd();
            end = new Point2D.Float(arcEnd.getX(), arcEnd.getY());
        }
        else if (arc.getSource() == glyph)
        {
            // Coming out
            Arc.Start arcStart = arc.getStart();
            end = new Point2D.Float(arcStart.getX(), arcStart.getY());

            start = getGlyphArcOutlyingPoint(glyph, arc);
        }

        if (start != null && end != null)
        {
            float m = (end.y - start.y) / (end.x - start.x);

            if (java.lang.Math.abs(m) < 1.0f)
            {
                // Left/Right
                if (start.x < end.x)
                {
                    return GlyphArcDirection.LEFT;
                }
                else
                {
                    return GlyphArcDirection.RIGHT;
                }
            }
            else
            {
                // Top/Bottom
                if (start.y < end.y)
                {
                    return GlyphArcDirection.TOP;
                }
                else
                {
                    return GlyphArcDirection.BOTTOM;
                }
            }
        }

        return GlyphArcDirection.UNKNOWN;
    }

    private Point2D.Float getSubmapTagConnectionPoint(Glyph submap, Glyph tag, String orientation)
    {
        Point2D.Float out = new Point2D.Float();
        Point2D.Float centre = getCentreOf(tag.getBbox());
        Bbox submapBbox = submap.getBbox();

        if (orientation.equals("right"))
        {
            out.x = submapBbox.getX();
            out.y = centre.y;
        }
        else if (orientation.equals("left"))
        {
            out.x = submapBbox.getX() + submapBbox.getW();
            out.y = centre.y;
        }
        else if (orientation.equals("down"))
        {
            out.x = centre.x;
            out.y = submapBbox.getY();
        }
        else if (orientation.equals("up"))
        {
            out.x = centre.x;
            out.y = submapBbox.getY() + submapBbox.getH();
        }
        else
        {
            out = centre;
        }

        return out;
    }

    private void addSubmapTag(Map map, Glyph submap, String label, Bbox bbox, String orientation, Arc arc)
    {
        Glyph tag = new Glyph();
        tag.setId(submap.getId() + ".tag" + label);
        tag.setClazz("tag");
        tag.setBbox(bbox);
        tag.setOrientation(orientation);

        Label tagLabel = new Label();
        tagLabel.setText(label);
        tag.setLabel(tagLabel);

        Point2D.Float connectionPoint = getSubmapTagConnectionPoint(submap, tag, orientation);

        if (arc.getSource() == submap)
        {
            arc.setSource(tag);
            Arc.Start start = new Arc.Start();
            start.setX(connectionPoint.x);
            start.setY(connectionPoint.y);
            arc.setStart(start);
        }
        else if (arc.getTarget() == submap)
        {
            arc.setTarget(tag);
            Arc.End end = new Arc.End();
            end.setX(connectionPoint.x);
            end.setY(connectionPoint.y);
            arc.setEnd(end);
        }
        arc.setClazz("equivalence arc");

        map.getGlyph().add(tag);
    }

    private void sortArcListByY(List<Arc> arcList, final Glyph submap, final boolean reverse)
    {
        Collections.sort(arcList, new java.util.Comparator<Arc>()
        {
            @Override
            public int compare(Arc a, Arc b)
            {
                Point2D.Float aPoint = getGlyphArcOutlyingPoint(submap, a);
                Point2D.Float bPoint = getGlyphArcOutlyingPoint(submap, b);

                if (compareFloats(aPoint.y, bPoint.y) == 0)
                {
                    if (!reverse)
                    {
                        return (bPoint.x - aPoint.x) < 0.0f ? -1 : 1;
                    }
                    else
                    {
                        return (aPoint.x - bPoint.x) < 0.0f ? -1 : 1;
                    }
                }
                else
                {
                    if (reverse)
                    {
                        return (bPoint.y - aPoint.y) < 0.0f ? -1 : 1;
                    }
                    else
                    {
                        return (aPoint.y - bPoint.y) < 0.0f ? -1 : 1;
                    }
                }
            }
        });
    }

    private void sortArcListByX(List<Arc> arcList, final Glyph submap, final boolean reverse)
    {
        Collections.sort(arcList, new java.util.Comparator<Arc>()
        {
            @Override
            public int compare(Arc a, Arc b)
            {
                Point2D.Float aPoint = getGlyphArcOutlyingPoint(submap, a);
                Point2D.Float bPoint = getGlyphArcOutlyingPoint(submap, b);

                if (compareFloats(aPoint.x, bPoint.x) == 0)
                {
                    if (!reverse)
                    {
                        return (bPoint.y - aPoint.y) < 0.0f ? -1 : 1;
                    }
                    else
                    {
                        return (aPoint.y - bPoint.y) < 0.0f ? -1 : 1;
                    }
                }
                else
                {
                    if (reverse)
                    {
                        return (bPoint.x - aPoint.x) < 0.0f ? -1 : 1;
                    }
                    else
                    {
                        return (aPoint.x - bPoint.x) < 0.0f ? -1 : 1;
                    }
                }
            }
        });
    }

    private void addSubmapTags(Map map)
    {
        List<Glyph> submaps = new ArrayList<Glyph>();
        for (Glyph submap : map.getGlyph())
        {
            if (submap.getClazz().equals("submap"))
            {
                submaps.add(submap);
            }
        }

        for (Glyph submap : submaps)
        {
            if (!submap.getClazz().equals("submap"))
            {
                continue;
            }

            List<Arc> incidentArcs = new ArrayList<Arc>();

            incidentArcs.addAll(arcsGoingTo(submap, map.getArc()));
            incidentArcs.addAll(arcsComingFrom(submap, map.getArc()));

            List<Arc> leftArcs = new ArrayList<Arc>();
            List<Arc> rightArcs = new ArrayList<Arc>();
            List<Arc> topArcs = new ArrayList<Arc>();
            List<Arc> bottomArcs = new ArrayList<Arc>();

            for (Arc arc : incidentArcs)
            {
                GlyphArcDirection d = getGlyphArcDirection(submap, arc);

                switch (d)
                {
                    case LEFT:
                        leftArcs.add(arc);
                        break;
                    case RIGHT:
                        rightArcs.add(arc);
                        break;
                    case TOP:
                        topArcs.add(arc);
                        break;
                    case BOTTOM:
                        bottomArcs.add(arc);
                        break;
                }
            }

            Bbox submapBbox = submap.getBbox();
            int labelIndex = 1;
            float leftDivision = submapBbox.getH() / (leftArcs.size() + 1);
            float rightDivision = submapBbox.getH() / (rightArcs.size() + 1);
            float topDivision = submapBbox.getW() / (topArcs.size() + 1);
            float bottomDivision = submapBbox.getW() / (bottomArcs.size() + 1);
            float tagWidth = SCALE * 1.2f;
            float tagHeight = SCALE * 0.6f;

            // Clockwise: left, top, right, bottom
            sortArcListByY(leftArcs, submap, true);
            for (int i = 0; i < leftArcs.size(); i++)
            {
                Bbox tagBbox = new Bbox();
                tagBbox.setW(tagWidth);
                tagBbox.setH(tagHeight);
                tagBbox.setX(submapBbox.getX());

                float y = submapBbox.getY() + submapBbox.getH() - (tagBbox.getH() * 0.5f);
                tagBbox.setY(y - ((i + 1) * leftDivision));

                addSubmapTag(map, submap, Integer.toString(labelIndex), tagBbox, "right", leftArcs.get(i));
                labelIndex++;
            }

            sortArcListByX(topArcs, submap, false);
            for (int i = 0; i < topArcs.size(); i++)
            {
                Bbox tagBbox = new Bbox();
                tagBbox.setW(tagHeight);
                tagBbox.setH(tagWidth);
                tagBbox.setY(submapBbox.getY());

                float x = submapBbox.getX() - (tagBbox.getW() * 0.5f);
                tagBbox.setX(x + ((i + 1) * topDivision));

                addSubmapTag(map, submap, Integer.toString(labelIndex), tagBbox, "down", topArcs.get(i));
                labelIndex++;
            }

            sortArcListByY(rightArcs, submap, false);
            for (int i = 0; i < rightArcs.size(); i++)
            {
                Bbox tagBbox = new Bbox();
                tagBbox.setW(tagWidth);
                tagBbox.setH(tagHeight);
                tagBbox.setX(submapBbox.getX() + submapBbox.getW() - tagBbox.getW());

                float y = submapBbox.getY() - (tagBbox.getH() * 0.5f);
                tagBbox.setY(y + ((i + 1) * rightDivision));

                addSubmapTag(map, submap, Integer.toString(labelIndex), tagBbox, "left", rightArcs.get(i));
                labelIndex++;
            }

            sortArcListByX(bottomArcs, submap, true);
            for (int i = 0; i < bottomArcs.size(); i++)
            {
                Bbox tagBbox = new Bbox();
                tagBbox.setW(tagHeight);
                tagBbox.setH(tagWidth);
                tagBbox.setY(submapBbox.getY() + submapBbox.getH() - tagBbox.getH());

                float x = submapBbox.getX() + submapBbox.getW() - (tagBbox.getW() * 0.5f);
                tagBbox.setX(x - ((i + 1) * bottomDivision));

                addSubmapTag(map, submap, Integer.toString(labelIndex), tagBbox, "up", bottomArcs.get(i));
                labelIndex++;
            }
        }
    }

    private static Point2D.Float getCentreOf(Bbox bbox)
    {
        return new Point2D.Float(bbox.getX() + (bbox.getW() * 0.5f), bbox.getY() + (bbox.getH() * 0.5f));
    }

    private static Point2D.Float getCentreOf(Glyph glyph)
    {
        return getCentreOf(glyph.getBbox());
    }

    private static void makeSquarePreserveArea(Bbox bbox)
    {
        float diameter = (float) java.lang.Math.sqrt(bbox.getW() * bbox.getH());
        float halfDiameter = diameter * 0.5f;
        Point2D.Float centre = getCentreOf(bbox);

        bbox.setX(centre.x - halfDiameter);
        bbox.setY(centre.y - halfDiameter);
        bbox.setW(diameter);
        bbox.setH(diameter);
    }

    private static void setArcStartToGlyph(Arc arc, Glyph glyph)
    {
        arc.setSource(glyph);
        Point2D.Float point = getCentreOf(glyph);
        Arc.Start start = new Arc.Start();
        start.setX(point.x);
        start.setY(point.y);
        arc.setStart(start);
    }

    private static void setArcEndToGlyph(Arc arc, Glyph glyph)
    {
        arc.setTarget(glyph);
        Point2D.Float point = getCentreOf(glyph);
        Arc.End end = new Arc.End();
        end.setX(point.x);
        end.setY(point.y);
        arc.setEnd(end);
    }

    private Arc mergeArcs(Arc source, Glyph intermediate, Arc target)
    {
        Arc newArc = new Arc();
        newArc.setId(source.getId() + "." + target.getId());
        newArc.setSource(source.getSource());
        newArc.setStart(source.getStart());
        newArc.setTarget(target.getTarget());
        newArc.setEnd(target.getEnd());

        List<Arc.Next> newArcNextList = newArc.getNext();
        newArcNextList.addAll(source.getNext());

        Arc.Next intermediateNext = new Arc.Next();
        Point2D.Float point = getCentreOf(intermediate);
        intermediateNext.setX(point.x);
        intermediateNext.setY(point.y);
        newArcNextList.add(intermediateNext);

        newArcNextList.addAll(target.getNext());

        String intermediateClazz = intermediate.getClazz();
        String mepnSpecifier = intermediateClazz.replace(PROCESS_EDGE_GLYPH_INDICATOR, "");

        if (mepnSpecifier.length() == 1)
        {
            switch (mepnSpecifier.charAt(0))
            {
                default:
                case 'A': newArc.setClazz("stimulation");
                case 'C': newArc.setClazz("catalysis");
                case 'I': newArc.setClazz("inhibition");
            }
        }
        else
        {
            newArc.setClazz(target.getClazz());
        }

        return newArc;
    }

    private static List<Arc> arcsGoingTo(Glyph glyph, List<Arc> arcList)
    {
        List<Arc> out = new ArrayList<Arc>();

        for (Arc arc : arcList)
        {
            if (arc.getTarget() == glyph)
            {
                out.add(arc);
            }
        }

        return out;
    }

    private static List<Arc> arcsComingFrom(Glyph glyph, List<Arc> arcList)
    {
        List<Arc> out = new ArrayList<Arc>();

        for (Arc arc : arcList)
        {
            if (arc.getSource() == glyph)
            {
                out.add(arc);
            }
        }

        return out;
    }

    private boolean pointsAreColinear(List<Point2D.Float> points, int first, int middle, int last)
    {
        final float EPSILON = 1.0f;

        Line2D.Float line = new Line2D.Float(points.get(first), points.get(last));
        return (float)line.ptLineDist(points.get(middle)) < EPSILON;
    }

    private List<Point2D.Float> simplifyArcs(List<Point2D.Float> points)
    {
        List<Point2D.Float> results = new ArrayList<Point2D.Float>();
        int first = 0;

        while (first < points.size())
        {
            int middle = first + 1;
            int last = first + 2;

            results.add(points.get(first));

            // While middle lies on line made by first to last
            while (last < points.size() && pointsAreColinear(points, first, middle, last))
            {
                middle++;
                last++;
            }

            first = middle;
        }

        return results;
    }

    private void simplifyArcs(Map map)
    {
        List<Arc> arcList = map.getArc();

        for (Arc arc : arcList)
        {
            if (arc.getNext().size() > 0)
            {
                List<Point2D.Float> points = new ArrayList<Point2D.Float>();
                points.add(new Point2D.Float(arc.getStart().getX(), arc.getStart().getY()));
                for (Arc.Next next : arc.getNext())
                {
                    points.add(new Point2D.Float(next.getX(), next.getY()));
                }
                points.add(new Point2D.Float(arc.getEnd().getX(), arc.getEnd().getY()));

                List<Point2D.Float> results = simplifyArcs(points);

                if (results.size() != points.size())
                {
                    Arc.Start start = new Arc.Start();
                    start.setX(results.get(0).x);
                    start.setY(results.get(0).y);
                    arc.setStart(start);

                    arc.getNext().clear();
                    for (int i = 1; i < results.size() - 1; i++)
                    {
                        Arc.Next next = new Arc.Next();
                        next.setX(results.get(i).x);
                        next.setY(results.get(i).y);
                        arc.getNext().add(next);
                    }


                    Arc.End end = new Arc.End();
                    end.setX(results.get(results.size() - 1).x);
                    end.setY(results.get(results.size() - 1).y);
                    arc.setEnd(end);
                }
            }
        }
    }

    private void transformProcessEdgeGlyphs(Map map)
    {
        List<Arc> arcList = map.getArc();

        for (Iterator<Glyph> glyphIt = map.getGlyph().iterator(); glyphIt.hasNext();)
        {
            Glyph glyph = glyphIt.next();

            if (glyph.getClazz().startsWith(PROCESS_EDGE_GLYPH_INDICATOR))
            {
                // Build lists of Arcs going to and coming from this glyph
                List<Arc> sourceArcList = arcsGoingTo(glyph, arcList);
                List<Arc> targetArcList = arcsComingFrom(glyph, arcList);

                // For each pair of Arcs that share the glyph
                for (Arc source : sourceArcList)
                {
                    for (Arc target : targetArcList)
                    {
                        arcList.add(mergeArcs(source, glyph, target));
                    }
                }

                // Remove original Arcs
                arcList.removeAll(sourceArcList);
                arcList.removeAll(targetArcList);

                // Remove original glyph
                glyphIt.remove();
            }
        }
    }

    private void transformEnergyTransferGlyphs(Map map)
    {
        List<Glyph> glyphList = map.getGlyph();
        List<Arc> arcList = map.getArc();
        List<Glyph> glyphsToAdd = new ArrayList<Glyph>();
        List<Arc> arcsToAdd = new ArrayList<Arc>();
        List<Glyph> glyphsToRemove = new ArrayList<Glyph>();
        List<Arc> arcsToRemove = new ArrayList<Arc>();

        for (Glyph glyph : glyphList)
        {
            if (glyph.getClazz().startsWith(ENERGY_TRANSFER_GLYPH_INDICATOR))
            {
                List<Arc> arcs = new ArrayList<Arc>();
                List<Arc> connectedSpnArcs = new ArrayList<Arc>();

                for (Arc arc : arcsComingFrom(glyph, arcList))
                {
                    if (spnArcIds.contains(arc.getId()))
                    {
                        connectedSpnArcs.add(arc);
                    }
                    else
                    {
                        arcs.add(arc);
                    }
                }

                for (Arc arc : arcsGoingTo(glyph, arcList))
                {
                    if (spnArcIds.contains(arc.getId()))
                    {
                        connectedSpnArcs.add(arc);
                    }
                    else
                    {
                        arcs.add(arc);
                    }
                }

                String originalLabel = glyph.getClazz().replace(ENERGY_TRANSFER_GLYPH_INDICATOR, "");
                Pattern r = Pattern.compile("^\\s*(.+?)\\s*->\\s*(.+?)\\s*$");
                Matcher m = r.matcher(originalLabel);

                if (arcs.size() != 1 || !m.matches())
                {
                    // This doesn't look like energy transfer
                    glyph.setClazz("unspecified entity");
                    Label label = new Label();
                    label.setText("Unknown energy/molecular transfer: " + originalLabel);
                    glyph.setLabel(label);
                    continue;
                }

                String leftText = m.group(1);
                String rightText = m.group(2);

                Arc arc = arcs.get(0);
                Glyph target = (Glyph)arc.getTarget();

                if (glyph == target)
                {
                    // Sometimes it points the other way
                    target = (Glyph)arc.getSource();
                }

                // Replace original glyph with two smaller ones
                Bbox originalBbox = glyph.getBbox();
                final float NEW_GLYPH_SCALE = 1.0f;
                float simpleChemicalWidth = originalBbox.getH() * NEW_GLYPH_SCALE;
                float horizontalShift = simpleChemicalWidth * 0.3f;

                Glyph leftGlyph = new Glyph();
                Arc leftArc = new Arc();
                {
                    Bbox leftBbox = new Bbox();
                    leftBbox.setW(simpleChemicalWidth);
                    leftBbox.setH(simpleChemicalWidth);
                    leftBbox.setX(originalBbox.getX() - horizontalShift);
                    leftBbox.setY(originalBbox.getY());

                    leftGlyph.setId(glyph.getId() + ".left");
                    leftGlyph.setBbox(leftBbox);
                    specialiseSimpleBiochemical(leftText, leftGlyph);

                    leftArc.setId(arc.getId() + ".left");
                    setArcStartToGlyph(leftArc, leftGlyph);
                    setArcEndToGlyph(leftArc, target);
                    leftArc.setClazz("consumption");
                }

                Glyph rightGlyph = new Glyph();
                Arc rightArc = new Arc();
                {
                    Bbox rightBbox = new Bbox();
                    rightBbox.setW(simpleChemicalWidth);
                    rightBbox.setH(simpleChemicalWidth);
                    rightBbox.setX(originalBbox.getX() + originalBbox.getW() -
                            simpleChemicalWidth + horizontalShift);
                    rightBbox.setY(originalBbox.getY());

                    rightGlyph.setId(glyph.getId() + ".right");
                    rightGlyph.setBbox(rightBbox);
                    specialiseSimpleBiochemical(rightText, rightGlyph);

                    rightArc.setId(arc.getId() + ".right");
                    setArcStartToGlyph(rightArc, target);
                    setArcEndToGlyph(rightArc, rightGlyph);
                    rightArc.setClazz("production");

                    // Reconnect any SPN arcs
                    for (Arc connectedSpnArc : connectedSpnArcs)
                    {
                        if (connectedSpnArc.getSource() == glyph)
                        {
                            connectedSpnArc.setSource(rightGlyph);
                        }

                        if (connectedSpnArc.getTarget() == glyph)
                        {
                            connectedSpnArc.setTarget(rightGlyph);
                        }
                    }
                }

                glyphsToAdd.add(leftGlyph);
                arcsToAdd.add(leftArc);
                glyphsToAdd.add(rightGlyph);
                arcsToAdd.add(rightArc);

                glyphsToRemove.add(glyph);
                arcsToRemove.add(arc);
            }
        }

        glyphList.addAll(glyphsToAdd);
        glyphList.removeAll(glyphsToRemove);
        arcList.addAll(arcsToAdd);
        arcList.removeAll(arcsToRemove);
    }

    private boolean glyphIsProcess(Glyph glyph)
    {
        List<String> processClazzes = new ArrayList<String>(
                Arrays.asList(
                "process",
                "uncertain process",
                "association",
                "dissociation",
                "source and sink"));

        String clazz = glyph.getClazz();

        return processClazzes.contains(clazz);
    }

    private boolean glyphIsLogicOperator(Glyph glyph)
    {
        List<String> logicOperatorClazzes = new ArrayList<String>(Arrays.asList("or", "and", "not"));
        String clazz = glyph.getClazz();

        return logicOperatorClazzes.contains(clazz);
    }

    private void specialiseSbgnArc(List<String> arrowHeads, Glyph source, Glyph target, Arc arc)
    {
        // This is a bit of a hack; we want to avoid marking things as
        // clones if they're connected to something else with an arrowless edge
        if (arrowHeads.size() == 2 && arrowHeads.get(0).equals("none") && arrowHeads.get(1).equals("none"))
        {
            glyphsToNotClone.add(source);
            glyphsToNotClone.add(target);
        }

        if (arrowHeads.contains("transparent_circle"))
        {
            arc.setClazz("catalysis");
        }
        else if (arrowHeads.contains("t_shape") ||
                arrowHeads.contains("diamond") ||
                arrowHeads.contains("white_diamond"))
        {
            arc.setClazz("inhibition");
        }
        else if (glyphIsLogicOperator(target))
        {
            arc.setClazz("logic arc");
        }
        else if (glyphIsLogicOperator(source))
        {
            arc.setClazz("stimulation");
        }
        else if (glyphIsProcess(source))
        {
            // Treat everything that's coming from a process as production
            arc.setClazz("production");
        }
        else
        {
            // Everything else
            arc.setClazz("consumption");
        }
    }

    private Arc translateEdgeToSbgnArc(GraphEdge graphEdge, Glyph source, Glyph target)
    {

        GraphNode startNode = graphEdge.getNodeFirst();
        GraphNode endNode = graphEdge.getNodeSecond();
        Tuple6<String, Tuple2<float[], ArrayList<Point2D.Float>>, String[], String[], String[], String[]> edgeData =
                gnc.getAllGraphmlEdgesMap().get(startNode.getNodeName() + " " +
                endNode.getNodeName());
        String id = edgeData.first;

        float startX, startY;
        float endX, endY;

        if (nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get())
        {
            float[] graphmlStartCoord = gnc.getAllGraphmlNodesMap().get(startNode.getNodeName()).first;
            float[] graphmlEndCoord = gnc.getAllGraphmlNodesMap().get(endNode.getNodeName()).first;
            startX = graphmlStartCoord[2] * SCALE;
            startY = graphmlStartCoord[3] * SCALE;
            endX = graphmlEndCoord[2] * SCALE;
            endY = graphmlEndCoord[3] * SCALE;
        }
        else
        {
            startX = startNode.getX() * SCALE;
            startY = startNode.getY() * SCALE;
            endX = endNode.getX() * SCALE;
            endY = endNode.getY() * SCALE;
        }

        Arc arc = new Arc();
        arc.setId(id);

        if (source != null)
        {
            arc.setSource(source);
        }

        if (target != null)
        {
            arc.setTarget(target);
        }

        Arc.Start start = new Arc.Start();
        start.setX(startX);
        start.setY(startY);
        arc.setStart(start);

        ArrayList<Point2D.Float> intermediatePoints = edgeData.second.second;
        String[] arrowHeads = edgeData.fourth;

        if (nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get())
        {
            if (intermediatePoints != null)
            {
                List<Arc.Next> nextList = arc.getNext();
                for (Point2D.Float polylinePoint2D : intermediatePoints)
                {
                    Arc.Next next = new Arc.Next();
                    next.setX(polylinePoint2D.x * SCALE);
                    next.setY(polylinePoint2D.y * SCALE);
                    nextList.add(next);
                }
            }
        }

        Arc.End end = new Arc.End();
        end.setX(endX);
        end.setY(endY);
        arc.setEnd(end);

        specialiseSbgnArc(Arrays.asList(arrowHeads), source, target, arc);

        if (spnGlyphIds.contains(source.getId()) || spnGlyphIds.contains(target.getId()))
        {
            spnArcIds.add(arc.getId());
        }

        return arc;
    }

    private Glyph translateContainerToSbgnGlyph(GraphmlComponentContainer componentContainer, int order)
    {
        float x = componentContainer.rectangle2D.x * SCALE;
        float y = componentContainer.rectangle2D.y * SCALE;
        float width = componentContainer.rectangle2D.width * SCALE;
        float height = componentContainer.rectangle2D.height * SCALE;

        Glyph glyph = new Glyph();
        glyph.setId(componentContainer.id);

        // We could probably be cleverer about this in that some compartments
        // will share a depth and arguably should have the same compartment order
        // This is nevertheless correct though
        glyph.setCompartmentOrder((float)order);

        String mepnLabel = componentContainer.name;
        Color mepnBackColor = componentContainer.color;

        Bbox bbox = new Bbox();
        bbox.setX(x - (width * 0.5f));
        bbox.setW(width);
        bbox.setY(y - (height * 0.5f));
        bbox.setH(height);
        glyph.setBbox(bbox);

        glyph.setClazz("compartment");

        Label label = new Label();

        // Label at the top centre of the compartment
        Bbox labelBbox = new Bbox();
        labelBbox.setX(x - (width * 0.5f));
        labelBbox.setW(width);
        labelBbox.setY(y - (height * 0.5f));
        labelBbox.setH(100.0f);
        label.setBbox(labelBbox);
        label.setText(mepnLabel);
        glyph.setLabel(label);

        return glyph;
    }

    private boolean listContainsGlyphWithId(List<Glyph> glyphs, String id)
    {
        for (Glyph glyph : glyphs)
        {
            if (glyph.getId().equals(id))
            {
                return true;
            }
        }

        return false;
    }

    private void makeGlyphIdUnique(Glyph glyph, List<Glyph> existingGlyphs)
    {
        while (listContainsGlyphWithId(existingGlyphs, glyph.getId()))
        {
            glyph.setId(glyph.getId() + "_");
        }
    }

    private Sbgn translateMepnToSbgn(Graph in)
    {
        Sbgn sbgn = new Sbgn();
        Map map = new Map();
        sbgn.setMap(map);
        map.setLanguage("process description");
        java.util.Map<Integer,Glyph> sbgnGlyphs = new HashMap<Integer,Glyph>();
        spnGlyphIds.clear();
        spnArcIds.clear();
        glyphsToNotClone.clear();

        if (nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get())
        {
            int containerOrder = 1;
            for (GraphmlComponentContainer componentContainer : gnc.getAllPathwayComponentContainersFor2D())
            {
                Glyph glyph = translateContainerToSbgnGlyph(componentContainer, containerOrder);

                map.getGlyph().add(glyph);
                containerOrder++;
            }
        }

        for (GraphNode graphNode : in.getGraphNodes())
        {
            Glyph glyph = translateNodeToSbgnGlyph(graphNode);

            makeGlyphIdUnique(glyph, map.getGlyph());
            map.getGlyph().add(glyph);
            sbgnGlyphs.put(graphNode.getNodeID(), glyph);
        }

        int edgeId = 1;
        List<GraphEdge> sortedGraphEdges = new ArrayList<GraphEdge>(in.getGraphEdges());
        Collections.sort(sortedGraphEdges, new java.util.Comparator<GraphEdge>()
        {
            @Override
            public int compare(GraphEdge a, GraphEdge b)
            {
                int aSourceNodeId = a.getNodeFirst().getNodeID();
                int aTargetNodeId = a.getNodeSecond().getNodeID();
                int bSourceNodeId = b.getNodeFirst().getNodeID();
                int bTargetNodeId = b.getNodeSecond().getNodeID();

                if (aSourceNodeId == bSourceNodeId)
                {
                    return aTargetNodeId - bTargetNodeId;
                }
                else
                {
                    return aSourceNodeId - bSourceNodeId;
                }
            }
        });

        for (GraphEdge graphEdge : sortedGraphEdges)
        {
            Glyph source = sbgnGlyphs.get(graphEdge.getNodeFirst().getNodeID());
            Glyph target = sbgnGlyphs.get(graphEdge.getNodeSecond().getNodeID());
            Arc arc = translateEdgeToSbgnArc(graphEdge, source, target);

            map.getArc().add(arc);
        }

        transformProcessEdgeGlyphs(map);
        transformEnergyTransferGlyphs(map);
        simplifyArcs(map);
        addCompartmentRefs(map);
        addCloneMarkers(map);
        addSubmapTags(map);

        return sbgn;
    }

    private void saveSbgnFile(File saveFile)
    {
        try
        {
            nc = layoutFrame.getNetworkRootContainer();
            gnc = nc.getGraphmlNetworkContainer();

            Sbgn sbgn = translateMepnToSbgn(layoutFrame.getGraph());
            SbgnUtil.writeToFile(sbgn, saveFile);

            if (!SbgnUtil.isValid(saveFile))
            {
                if (DEBUG_BUILD)
                {
                    println("SBGN validation failed on " + saveFile.getAbsolutePath());
                }
            }
            else
            {
                if (DEBUG_BUILD)
                {
                    println("SBGN validation succeeded " + saveFile.getAbsolutePath());
                }
            }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(layoutFrame, "Failed export to SBGN. Reason given:\n" + e.getMessage(),
                    "Export failure", JOptionPane.ERROR_MESSAGE);

            if (DEBUG_BUILD)
            {
                println("Failed to write SBGN file" + e.toString());
            }
        }
    }

    private class ExportSbgnProcess implements Runnable
    {
        private File saveFile = null;

        private ExportSbgnProcess(File saveFile)
        {
            this.saveFile = saveFile;
        }

        @Override
        public void run()
        {
            saveSbgnFile(saveFile);

            FILE_CHOOSER_PATH.set(saveFile.getAbsolutePath());
        }
    }
}