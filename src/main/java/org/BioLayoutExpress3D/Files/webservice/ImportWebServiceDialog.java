/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.BioLayoutExpress3D.Files.webservice;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

import cpath.client.CPathClient;
import cpath.client.util.CPathException;
import cpath.query.CPathGraphQuery;
import cpath.query.CPathQuery;
import cpath.query.CPathSearchQuery;
import cpath.query.CPathTraverseQuery;
import cpath.service.GraphType;
import cpath.service.OutputFormat;
import cpath.service.jaxb.SearchHit;
import cpath.service.jaxb.SearchResponse;
import cpath.service.jaxb.TraverseEntry;
import cpath.service.jaxb.TraverseResponse;

import gov.nih.nlm.ncbi.soap.eutils.EFetchTaxonService;
import gov.nih.nlm.ncbi.soap.eutils.EUtilsServiceSoap;
import gov.nih.nlm.ncbi.soap.eutils.efetch_taxonomy.EFetchRequest;
import gov.nih.nlm.ncbi.soap.eutils.efetch_taxonomy.EFetchResult;
import gov.nih.nlm.ncbi.soap.eutils.efetch_taxonomy.ObjectFactory;
import gov.nih.nlm.ncbi.soap.eutils.efetch_taxonomy.TaxonType;

//import gov.nih.nlm.ncbi.www.soap.eutils.EFetchTaxonServiceStub; //Apache Axis2 stub
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import net.miginfocom.swing.MigLayout;
import org.BioLayoutExpress3D.CoreUI.LayoutFrame;
import org.BioLayoutExpress3D.Environment.DataFolder;
import org.apache.commons.io.FileUtils;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Dialogue for searchQuerying remote databases via web service
 * @author Derek Wright
 */
public class ImportWebServiceDialog extends JDialog implements ActionListener{
    private static final Logger logger = Logger.getLogger(ImportWebServiceDialog.class.getName());

    //web service command parameters
    public static final String FORMAT_SIF = "BINARY_SIF";
    public static final String FORMAT_BIOPAX = "BIOPAX";

    public static final String DATASOURCE_REACTOME = "reactome";
    public static final String DATASOURCE_PID = "pid";
    public static final String DATASOURCE_PHOSPHOSITEPLUS = "phosphosite"; 
    public static final String DATASOURCE_HUMANCYC = "humancyc";
    public static final String DATASOURCE_HPRD = "HPRD";
    public static final String DATASOURCE_PANTHER = "panther";
    
    public static final String COMMAND_TOP_PATHWAYS = "top_pathways";
    public static final String COMMAND_SEARCH = "search";
    public static final String COMMAND_GET = "get";
    
    //timeouts for web service operations in seconds
    public static final int TIMEOUT_SEARCH = 30;
    public static final int TIMEOUT_GET = 60;
    
    /**
     * Preferred width in pixels.
     */
    public static final int WIDTH = 888;
    
    public static final String BIOPAX_FILE_EXTENSION = ".owl";
    
    private JButton searchButton, cancelButton,nextButton, previousButton, stopButton, openButton;
    private JButton advancedExecuteButton, advancedStopButton, advancedCancelButton, advancedRemoveButton, advancedClearButton;
    private JTextField searchField, organismField;
    private JComboBox<String> networkTypeCombo;
    private LayoutFrame frame;
    private JLabel numHitsLabel, retrievedLabel, pagesLabel, statusLabel;
    private JCheckBox anyOrganismCheckBox, allDatasourceCheckBox, nameCheckBox;
    private Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    private Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    
    private JEditorPane editorPane;
    //private JEditorPane advancedEditorPane;
    private JTable table; //search results table
    private JTable advancedTable;
    private DefaultTableModel model; 
    private DefaultTableModel advancedModel; 
    private JTabbedPane tabbedPane;
    
    private JRadioButton getRadio = new JRadioButton("Get", true); //default option
    private JRadioButton nearestNeighborhoodRadio = new JRadioButton("Nearest Neighborhood");
    private JRadioButton commonStreamRadio = new JRadioButton("Common Stream");
    private JRadioButton pathsBetweenRadio = new JRadioButton("Paths Between");
    private JRadioButton pathsFromToRadio = new JRadioButton("Paths From To");
        
    private JRadioButton downstreamRadio  = new JRadioButton("Downstream");
    private JRadioButton upstreamRadio = new JRadioButton("Upstream");
    private JRadioButton bothRadio = new JRadioButton("Both");
  
    ButtonGroup queryTypeGroup = new ButtonGroup();
    ButtonGroup directionGroup = new ButtonGroup();
        
    private List<SearchHit> searchHits; //retrieved search hits for current page
    private LinkedHashSet<SearchHit> advancedSearchHits; //search hits added to advanced tab, Set stops adding duplicates
    private List<List<SearchHit>> allPages; //list of lists to cache search pages
    
    private int currentPage;
    private int maxHitsPerPage;
    private int totalHits; //total number of search searchQuery matches
    private LinkedHashMap<JCheckBox, String> datasourceDisplayCommands, organismDisplayCommands;  
    private Map <SearchHit, Integer> hitInteractionCountMap; //map of search hitd to the number of interactions
    
    //search form values entered by user
    private String networkType = ""; //stores selected value of networkTypeCombo when search is run
    private String searchTerm = "";
    private String organism = "";
    private Set<String> organismSet, datasourceSet;
    
    /**
     * Maps search hit URI of database to display name. Map contents are immutable.
     */
    public static final Map<String, String> DATABASE_URI_DISPLAY = ImmutableMap.<String, String>builder()
        .put("reactome", "Reactome")
        .put("pid", "NCI Nature")
        .put("psp", "PhosphoSitePlus")
        .put("humancyc", "HumanCyc")
        .put("hprd", "HPRD")
        .put("panther", "PANTHER")
        .build();

    /**
     * Map of NCBI organism ID to species name. Not immutable so we can add new species from NCBI web service. 
     * Common species hard coded to avoid unnecessary web service calls. Map is final but contents are not!
     */
    private static final Map<String, String> organismIdNameMap = new HashMap<String, String>();
    static
    {
        organismIdNameMap.put("9606", "Homo sapiens");
        organismIdNameMap.put("11676", "Human immunodeficiency virus 1");
        organismIdNameMap.put("10090", "Mus musculus");
        organismIdNameMap.put("10116", "Rattus norvegicus");
    }

    private SearchWorker searchWorker = null; //search operation concurrent task runner
    private GetWorker getWorker = null; //GET operation concurrent task runner
    private CPathQuery<SearchResponse> searchQuery; //query for top pathways and search
    private CPathQuery cPathQuery;
    
    private static final Joiner commaJoiner = Joiner.on(',').skipNulls(); //for creating comma-separated strings

    /**
     * Name of directory where files are downloaded from the web service.
     */
    public static final String DIRECTORY = "import";   
    
    /**
     * Constructor.
     * @param frame
     * @param myMessage
     * @param modal 
     */
    public ImportWebServiceDialog(LayoutFrame frame, String myMessage, boolean modal) 
    {        
        //construct search dialog
        super(); //do not attach the dialog to a parent frame so it does not stay on top
        setModal(modal);
        
        setAlwaysOnTop(false);

        hitInteractionCountMap = new HashMap<SearchHit, Integer>();
        
        this.frame = frame;
        this.setTitle(myMessage);
        
        //search panel buttons
        searchButton = this.createJButton("Search", "Search Pathway Commons", false);
        cancelButton = this.createJButton("Close", "Close dialog", true); //cancel button
        stopButton = this.createJButton("Stop", "Stop Search", false); //stop button
        openButton = this.createJButton("Open", "Open network", false); //open button
        getRootPane().setDefaultButton(searchButton); //searches with enter key
        
        //advanced panel buttons
        advancedExecuteButton = this.createJButton("Execute", "Execute query", false);
        advancedCancelButton = this.createJButton("Close", "Close dialog", true); //cancel button
        advancedStopButton = this.createJButton("Stop", "Stop query", false); //stop button
        advancedRemoveButton = this.createJButton("Remove", "Remove search hit", false);
        advancedClearButton = this.createJButton("Clear", "Remove all search hits", modal);

        searchField = new JTextField(70);
        organismField = new JTextField(35); //organism text field
        
        //disable search button if search field has no text
        searchField.getDocument().addDocumentListener(new DocumentListener() 
        {
            public void changedUpdate(DocumentEvent e) {
              searchFieldChanged();
            }
            public void removeUpdate(DocumentEvent e) {
              searchFieldChanged();
            }
            public void insertUpdate(DocumentEvent e) {
              searchFieldChanged();
            }
          });        
                
        organismDisplayCommands = new LinkedHashMap<JCheckBox, String>();
        organismDisplayCommands.put(new JCheckBox("Human"), "9606");
        organismDisplayCommands.put(new JCheckBox("Mouse"), "10090");
        organismDisplayCommands.put(new JCheckBox("Fruit Fly"), "7227");
        organismDisplayCommands.put(new JCheckBox("Rat"), "10116");
        organismDisplayCommands.put(new JCheckBox("C. elegans"), "6239");
        organismDisplayCommands.put(new JCheckBox("S. cervisiae"), "4932");
        
        anyOrganismCheckBox = new JCheckBox("Any");
        anyOrganismCheckBox.setSelected(true);
        anyOrganismCheckBox.addActionListener(this);
        enableDisableOrganism(true);
        
        //Map checkboxes to web service commands
        datasourceDisplayCommands = new LinkedHashMap<JCheckBox, String>();
        datasourceDisplayCommands.put(new JCheckBox("Reactome"), "reactome");
        datasourceDisplayCommands.put(new JCheckBox("NCI Nature"), "pid");
        datasourceDisplayCommands.put(new JCheckBox("PhosphoSitePlus"), "phosphosite");
        datasourceDisplayCommands.put(new JCheckBox("HumanCyc"), "humancyc");
        datasourceDisplayCommands.put(new JCheckBox("HPRD"), "hprd");
        datasourceDisplayCommands.put(new JCheckBox("PANTHER"), "panther");        
        
        allDatasourceCheckBox = new JCheckBox("All");
        allDatasourceCheckBox.setSelected(true);
        allDatasourceCheckBox.addActionListener(this);
        enableDisableDatasource(true);
        
        //Network Type Drop Down
        networkTypeCombo = new JComboBox<String>();
        networkTypeCombo.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[] { "Pathway", "Interaction", "PhysicalEntity", "EntityReference", "Top Pathways" }));

        nameCheckBox = new JCheckBox("Name", true);
        nameCheckBox.setToolTipText("Restrict search to name field only");
        
        getRadio.setToolTipText("Retrieve multiple search hits as a single network graph");
        nearestNeighborhoodRadio.setToolTipText("Search neighborhood of a given source set of nodes");
        commonStreamRadio.setToolTipText("Search common downstream or common upstream of a specified set of entities based on the given directions");
        pathsBetweenRadio.setToolTipText("Find the paths between specific source set of states or entities");
        pathsFromToRadio.setToolTipText("Find the paths from a specific source set of states or entities (highlighted rows) to a specific target set of states or entities (non-highlighted rows)");
        
        upstreamRadio.setToolTipText("Graph search direction upstream");
        downstreamRadio.setToolTipText("Graph search direction downstream");
        bothRadio.setToolTipText("Graph search directions upstream and downstream");

        /**********add form fields******************/
        
        //create Search panel
        SearchHitsPanel searchPanel = new SearchHitsPanel();
        searchPanel.add(createFieldPanel(), BorderLayout.PAGE_START);   
        
        //create panel for status message and stats
        JPanel hitsPanel = createHitsPanel();
        getContentPane().add(hitsPanel, BorderLayout.PAGE_END);
        
        //create HTML editor panes
        editorPane = createEditorPane(); 
        advancedSearchHits = new LinkedHashSet<SearchHit>();
        
        hitInteractionCountMap.clear();

        //create results table //create table model
        String[] colHeadings = {"Name", "Database", "BioPAX Class", "Pathways"};
        model = createHitsModel(colHeadings);
        table = new ZebraJTable(model, colHeadings);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() //double click adds search hit to Advanced
        { 
            public void mouseClicked(MouseEvent e) 
            {
                if (e.getClickCount() == 2) //open network
                {
                    int viewRow = table.getSelectedRow();
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    SearchHit hit = searchHits.get(modelRow); //get SearchHit that relates to values in table model row (converted from sorted view row index)
                    if(advancedSearchHits.add(hit))
                    {
                        advancedModel.addRow(new Object[]{hit.getName(), joinDatabases(hit), hit.getBiopaxClass(), hit.getPathway().size()});  
                        int lastRow = advancedTable.convertRowIndexToView(advancedModel.getRowCount() - 1);                        
                        statusLabel.setText("Added " + hit.getName() + " to Advanced");                        
                    }
                    else //unable to add because search hit already in Set
                    {
                        statusLabel.setText(hit.getName() + " already added");
                    }
                }
            }
         });
        
        //display search hit info when row selected
        table.getSelectionModel().addListSelectionListener(new HitListSelectionListener());        
        
        //set column widths
        table.getColumn(colHeadings[0]).setPreferredWidth(400);
        table.getColumn(colHeadings[1]).setPreferredWidth(75);
        table.getColumn(colHeadings[2]).setPreferredWidth(125);

        //split search results on left and highlighted search hit excerpt on right
        JScrollPane tableScrollPane = new JScrollPane(table);
        JScrollPane editorScrollPane = new JScrollPane(editorPane);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, editorScrollPane);
        searchPanel.add(splitPane ,BorderLayout.CENTER);
        
        //create Advanced panel
        JPanel advancedPanel = new SearchHitsPanel(); //advanced tab panel for graph search
        advancedPanel.add(createAdvancedFieldPanel(), BorderLayout.PAGE_START);
        
        advancedModel = createHitsModel(colHeadings);
        advancedTable = new ZebraJTable(advancedModel, colHeadings);

        advancedTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        advancedTable.getColumn(colHeadings[0]).setPreferredWidth(WIDTH - 275);
        advancedTable.getColumn(colHeadings[1]).setPreferredWidth(75);
        advancedTable.getColumn(colHeadings[2]).setPreferredWidth(125);
        
        advancedTable.getSelectionModel().addListSelectionListener(new AdvancedHitListSelectionListener());
        
        //listener to disable radio buttons in advanced table if no data
        advancedModel.addTableModelListener(new TableModelListener() 
        {
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.INSERT) 
                {
                    if(advancedModel.getRowCount() >= 1) //first row, set up for get query
                    {
                        getRadio.setEnabled(true);
                        nearestNeighborhoodRadio.setEnabled(true);
                        commonStreamRadio.setEnabled(true);
                        pathsBetweenRadio.setEnabled(true);
                        enableAdvancedBySelection(); //enable remove/pathsfromto according to row selection
                        
                        enableAll(directionGroup, false);
                        advancedExecuteButton.setEnabled(true);
                        advancedStopButton.setEnabled(false);
                        advancedClearButton.setEnabled(true); //NB not enabling remove button here - do it when a row is selected
                    }
                }
                else if (e.getType() == TableModelEvent.DELETE)
                {
                    if(advancedModel.getRowCount() == 0) //no data, disable all radio buttons and execute/stop button
                    {
                        enableAll(queryTypeGroup, false);
                        enableAll(directionGroup, false);
                        advancedExecuteButton.setEnabled(false);
                        advancedStopButton.setEnabled(false);
                        advancedRemoveButton.setEnabled(false);
                        advancedClearButton.setEnabled(false);
                    }
                }
            }
        });        

        JScrollPane advancedTableScrollPane = new JScrollPane(advancedTable);
        advancedPanel.add(advancedTableScrollPane, BorderLayout.CENTER);       
        
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Search", searchPanel);
        tabbedPane.addTab("Advanced", advancedPanel);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);    
        
        pack();
        splitPane.setDividerLocation(0.75); //needs to be after pack() or split is reset to 50% 
        //advancedSplitPane.setDividerLocation(0.75); //needs to be after pack() or split is reset to 50% 
        setLocationRelativeTo(frame);
        setVisible(true);
    }
    
    private JPanel createAdvancedFieldPanel()
    {
        queryTypeGroup.add(getRadio);
        queryTypeGroup.add(nearestNeighborhoodRadio);
        queryTypeGroup.add(commonStreamRadio);
        queryTypeGroup.add(pathsBetweenRadio);
        queryTypeGroup.add(pathsFromToRadio);
        
        directionGroup.add(downstreamRadio);
        directionGroup.add(upstreamRadio);
        directionGroup.add(bothRadio);
        
        JPanel queryTypePanel = new JPanel();
        queryTypePanel.setBorder(BorderFactory.createTitledBorder("BioPAX Query Type"));
        queryTypePanel.add(getRadio);
        queryTypePanel.add(nearestNeighborhoodRadio);
        queryTypePanel.add(commonStreamRadio);
        queryTypePanel.add(pathsBetweenRadio);
        queryTypePanel.add(pathsFromToRadio);

        //set initial state for radio buttons - all disabled until search hits added
        enableAll(queryTypeGroup, false);
        enableAll(directionGroup, false);
        
        JPanel directionPanel = new JPanel();
        directionPanel.add(downstreamRadio);
        directionPanel.add(upstreamRadio);
        directionPanel.add(bothRadio);
        directionPanel.setBorder(BorderFactory.createTitledBorder("Direction"));
        
        JPanel advancedFieldPanel = new JPanel();
        advancedFieldPanel.setLayout(new MigLayout("fill"));
        advancedFieldPanel.add(queryTypePanel, "wrap, span");
        advancedFieldPanel.add(directionPanel, "Wrap");
                
        advancedFieldPanel.add(advancedExecuteButton, "tag right, span, split 5, sizegroup bttn");
        advancedFieldPanel.add(advancedStopButton, "tag right, sizegroup bttn");
        advancedFieldPanel.add(advancedCancelButton, "tag right, sizegroup bttn");
        
        advancedFieldPanel.add(advancedRemoveButton, "tag left, sizegroup bttn");
        advancedFieldPanel.add(advancedClearButton, "tag left, sizegroup bttn");
        
        ItemListener itemListener = new ItemListener() //listener for get, pathsbetween, pathsfromto
        {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    directionGroup.clearSelection();
                    enableAll(directionGroup, false);
                }
                else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    enableAll(directionGroup, true);
                }
            }            
        };
        
        //create listeners to conditionally enable radio buttons
        getRadio.addItemListener(itemListener);
        pathsBetweenRadio.addItemListener(itemListener);        
        pathsFromToRadio.addItemListener(itemListener);
        
        nearestNeighborhoodRadio.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    bothRadio.setSelected(true);
                }
            }            
        });
        
        commonStreamRadio.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    downstreamRadio.setSelected(true);
                    bothRadio.setEnabled(false);
                }
                else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    enableAll(directionGroup, true);
                }
            }            
        });        
        
        return advancedFieldPanel;
    }
    
    private void searchFieldChanged() 
    {
       if (searchField.getText().equals(""))
       {
         searchButton.setEnabled(false);
       }
       else 
       {
         searchButton.setEnabled(true);
      }
    }

            /**
     * Utility method to enable/disable all buttons in a ButtonGroup.
     * @param group
     * @param enabled 
     */
    public static void enableAll(ButtonGroup group, boolean enabled)
    {
        Enumeration<AbstractButton> elements = group.getElements();
        while (elements.hasMoreElements()) 
        {
          AbstractButton button = elements.nextElement();
          button.setEnabled(enabled);
        }
    }
    
    private JPanel createFieldPanel()
    {
        //search term label
        JLabel searchLabel = new JLabel("Keywords", JLabel.TRAILING);    
        searchLabel.setLabelFor(searchField);  

        JLabel organismLabel = new JLabel("Organism", JLabel.TRAILING);
        organismLabel.setLabelFor(organismField);  
        
        JLabel datasourceLabel = new JLabel("Data Source", JLabel.TRAILING);

        JLabel networkTypeLabel = new JLabel("Type", JLabel.TRAILING);    
        networkTypeLabel.setLabelFor(networkTypeCombo);

        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new MigLayout());
        fieldPanel.add(searchLabel, "align label");
        fieldPanel.add(searchField, "");
        
        fieldPanel.add(nameCheckBox, "wrap");

        fieldPanel.add(organismLabel, "align label");
        
        JPanel organismPanel = new JPanel();
        organismPanel.setLayout(new BoxLayout(organismPanel, BoxLayout.LINE_AXIS));
        for(JCheckBox checkBox: organismDisplayCommands.keySet())
        {
            organismPanel.add(checkBox);
        }        
        organismPanel.add(anyOrganismCheckBox);        
        fieldPanel.add(organismPanel, "wrap");
        
        //organism checkboxes
        fieldPanel.add(new JLabel(), "align label"); //dummy label for empty cell
        fieldPanel.add(organismField, "wrap, span");                
        
        //datasource checkboxes
        fieldPanel.add(datasourceLabel);
        JPanel datasourcePanel = new JPanel();
        datasourcePanel.setLayout(new BoxLayout(datasourcePanel, BoxLayout.LINE_AXIS));
        for(JCheckBox checkBox: datasourceDisplayCommands.keySet())
        {
           datasourcePanel.add(checkBox);
        }
        datasourcePanel.add(allDatasourceCheckBox);
        fieldPanel.add(datasourcePanel, "wrap");

        //network type
        fieldPanel.add(networkTypeLabel, "align label");
        fieldPanel.add(networkTypeCombo, "wrap");

        fieldPanel.add(searchButton, "tag right, span, split 4, sizegroup bttn");
        fieldPanel.add(stopButton, "tag right, sizegroup bttn");
        fieldPanel.add(openButton, "tag right, sizegroup bttn");
        fieldPanel.add(cancelButton, "tag right, sizegroup bttn");
        
        fieldPanel.setPreferredSize(new Dimension(WIDTH, 205));
        return fieldPanel;
    }
    
    private JPanel createHitsPanel()
    {
        //labels for search info
        totalHits = 0;
        numHitsLabel = new JLabel("Hits: " + totalHits);

        // same font but bold
        Font font = numHitsLabel.getFont();
        Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
        numHitsLabel.setFont(boldFont);        
        numHitsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        retrievedLabel = new JLabel("Retrieved: 0");
        retrievedLabel.setFont(boldFont);
        retrievedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        currentPage = 0;
        pagesLabel = new JLabel("Page: " + currentPage);
        pagesLabel.setFont(boldFont);
        pagesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        statusLabel = new JLabel("Ready");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel hitsPanel = new JPanel();
        hitsPanel.setLayout(new MigLayout());
        
        //create next and previous buttons
        previousButton = this.createJButton("< Previous", "Return to previous page", false); //previous button
        nextButton = this.createJButton("Next >", "Next page", false); //next button
        hitsPanel.add(previousButton, "span, split 2, center, sizegroup hbttn");
        hitsPanel.add(nextButton, "sizegroup hbttn, wrap");
        
        hitsPanel.add(statusLabel, "span, align center, wrap");
        hitsPanel.add(numHitsLabel, "w 33%, sizegroup hits");
        hitsPanel.add(retrievedLabel, "w 33%, sizegroup hits");
        hitsPanel.add(pagesLabel, "w 33%, sizegroup hits");        
        
        hitsPanel.setPreferredSize(new Dimension(WIDTH, 88));
        return hitsPanel;
    }
    
    
    private String generateExcerptHTML(SearchHit hit)
    {
        //construct HTML snippet of organism scientific names
        List<String> organismIdList = hit.getOrganism();
        String organismHTML = "<b>Organism:</b>";
        for(String organismString : organismIdList)
        {
            String ncbiId = organismString.substring(organismString.lastIndexOf("/")+1, organismString.length());
            String scientificName = organismIdNameMap.get(ncbiId);
            organismHTML = organismHTML 
                    + "<br />" 
                    + "<a href='" + organismString + "'>" + scientificName + "</a>";
        }

        //count number of interactions for a pathway
        String interactionsHTML = "";                   
        if(networkType.equals("Pathway"))
        {
            //check if interaction count has been previously cached
            Integer interactionCount = hitInteractionCountMap.get(hit);

            interactionsHTML = "<b>Interactions: </b>";
            if(interactionCount != null)
            {
                logger.info("Interaction count found: " + interactionCount);
                interactionsHTML += interactionCount;
            }
            else //interactions have not been previously counted - do traverse searchQuery
            {
                try
                {
                    interactionCount = traverseInteractions(hit); //calculate interaction count using TRAVERSE query, autobox int to Integer

                    hitInteractionCountMap.put(hit, interactionCount);
                    interactionsHTML += interactionCount;
                }
                catch(CPathException exception)
                {
                    logger.warning(exception.getMessage());
                    interactionsHTML += "unknown";
                }
            }
            interactionsHTML += "<br />";
        }

        //display excerpt
        String uri = hit.getUri();
        String abbreviatedUri = uri.substring(0, Math.min(uri.length(), 22)) + "..."; 

        String excerptHTML = "<b>Excerpt:</b><br />" 
                + hit.getExcerpt() 
                + "<br />" 
                + "<b>URI: </b>"
                + "<a href='" + hit.getUri() + "'>" + abbreviatedUri + "</a>"
                + "<br />" 
                + interactionsHTML
                + organismHTML;
        return excerptHTML;        
    }
    
    private ExcerptPane createEditorPane()
    {
        ExcerptPane editorPane = new ExcerptPane();
        editorPane.setText("<b>Excerpt:</b>");
        
        //open system web browser on hyperlink click
        editorPane.addHyperlinkListener(new HyperlinkListener() 
        {
            public void hyperlinkUpdate(HyperlinkEvent e) 
            {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) 
                {
                    if(Desktop.isDesktopSupported()) 
                    {
                        try
                        {
                            Desktop.getDesktop().browse(e.getURL().toURI());                            
                        }
                        catch(Exception exception)
                        {
                            logger.warning("Cannot open web browser: " + exception.getMessage()); //TODO error alert                            
                            statusLabel.setText("Unable to open web browser");
                        }
                    }
                }
            }
        });
        
        return editorPane;
        
    }
    
    private int traverseInteractions(SearchHit hit) throws CPathException
    {
        int pathwayCount = hit.getPathway().size() + 1; //getPathway returns sub-pathways - does not include the top level pathway
        ArrayList<String> uriList = new ArrayList<String>(pathwayCount);
        uriList.add(hit.getUri());
        uriList.addAll(hit.getPathway());

        //traverse all interactions for all pathways //TODO threading? //TODO what happens with GO term URI? //TODO what to count for interactions?
        CPathClient client = CPathClient.newInstance();
        CPathTraverseQuery traverseQuery = client.createTraverseQuery().sources(uriList).propertyPath("Pathway/pathwayComponent*:Interaction");
        HashSet<String> uniqueUriSet = new HashSet<String>(); //set of unique interaction URIs

        TraverseResponse traverseResponse = traverseQuery.result(); //run traverse query
        List<TraverseEntry> traverseEntryList = traverseResponse.getTraverseEntry();

        for (TraverseEntry traverseEntry : traverseEntryList) 
        {
            logger.info(traverseEntry.getUri());
            List<String> traverseEntryValues = traverseEntry.getValue();
            uniqueUriSet.addAll(traverseEntryValues);
        }
        return uniqueUriSet.size();       
    }
    
    private static DefaultTableModel createHitsModel(String[] colHeadings)
    {
        int numRows = 0;       
        DefaultTableModel model = new DefaultTableModel(numRows, colHeadings.length) 
        {
            @Override
            public boolean isCellEditable(int row, int column) 
            {               
               return false; //all cells false
            }
        };        
        model.setColumnIdentifiers(colHeadings);
        return model;
    }
    
    private class HitListSelectionListener implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e) 
        {
            if (e.getValueIsAdjusting()) return; //Ignore extra messages.

            openButton.setEnabled(true);

            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if (!lsm.isSelectionEmpty()) 
            {
                int selectedRow = lsm.getMinSelectionIndex();
                SearchHit hit = searchHits.get(selectedRow);
                String excerptHTML = generateExcerptHTML(hit);
                editorPane.setText(excerptHTML);
            }
        }
    }
    
    /**
     * Listener for selection/deselection of rows in Advanced table.
     */
    private class AdvancedHitListSelectionListener implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e)
        {
            if (e.getValueIsAdjusting()) return; //Ignore extra messages.
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if(lsm.isSelectionEmpty()) //deselection event and no rows selected
            {
                getRadio.setSelected(true); //ensure pathsfromto query can't be run
                pathsFromToRadio.setEnabled(false);
                advancedRemoveButton.setEnabled(false);
            }
            else
            {
                enableAdvancedBySelection();
            }
        }
    }
    
    /**
     * Enable Advanced components when table rows are selected.
     */
    private void enableAdvancedBySelection()
    {
        /*
        enable remove button if at least one row selected
        enable Paths From to
            if at least one row is selected
            and not all rows are selected
        disable PathsFromTo 
            if all rows are selected (i.e. all froms and no tos)
        */
        if(advancedTable.getSelectedRowCount() > 0) 
        {
            advancedRemoveButton.setEnabled(true);
            if(advancedTable.getSelectedRowCount() < advancedTable.getRowCount())
            {
                pathsFromToRadio.setEnabled(true);            
            }
            else
            {
                pathsFromToRadio.setEnabled(false);            
            }
        }
    }
    
    private void clearSearchResults(boolean clearAdvanced)
    {
        model.setRowCount(0); //clear previous search results
        editorPane.setText(""); //clear excerpt pane
        
        if(clearAdvanced)
        {
            advancedModel.setRowCount(0); //clear previous search results
            advancedSearchHits.clear(); //empty the list of added search hits
        }
    }
    
    /**
     * Get a BioPAX OWL file from Pathway Commons and display network.
     * Gets URI of selected search hit in table
     * Sends GET request to Pathway Commons
     * Downloads the file to a sub-directory of the application directory defined by DIRECTORY
     * Loads the network in BioLayout.
     * @throws IndexOutOfBoundsException - if table row not selected or not valid
     */
    private void openNetwork() throws IndexOutOfBoundsException
    {
        int viewRow = table.getSelectedRow();
        int modelRow = table.convertRowIndexToModel(viewRow);

        SearchHit hit = searchHits.get(modelRow); //get SearchHit that relates to values in table model row (converted from sorted view row index)
        String uriString = hit.getUri(); //URI for GET request

        //if search hit name is empty use BioPAX class as filename instead
        String hitName = hit.getName();
        if(hitName == null || hitName.isEmpty())
        {
            hitName = hit.getBiopaxClass();
        }
        
        String fileName = hitName + BIOPAX_FILE_EXTENSION; //name of .owl file to be created
        
        CPathClient client = CPathClient.newInstance();
        String[] uriArray = {uriString};
        
        if(networkType.equals("Pathway") || networkType.equals("Top Pathways")) //just get the pathway itself
        {
            cPathQuery = client.createGetQuery().sources(uriArray);
        }
        else //Interaction etc - network neighbourhood graph query
        {
            cPathQuery = client.createGraphQuery().sources(uriArray).kind(GraphType.NEIGHBORHOOD);
        }

        //retrieve file from Pathway Commons and load file for display
        getWorker = new GetWorker(fileName);
        getWorker.execute();
    }
    
    private void openAdvancedNetwork()
    {
        CPathClient client = CPathClient.newInstance();
        
        //String[] allUri = new String[advancedSearchHits.size()]; //array of all search hit URIs in advanced table
        
        HashSet<String> allUri = new HashSet<String>(advancedSearchHits.size()); //all selected search hit URIs
        
        //PATHSFROMTO query parameters
        HashSet<String> from = new HashSet<String>(); //all selected search hit URIs
        HashSet<String> to = new HashSet<String>(); //all unselected search hit URIs        
        
        // convert selected row indices to those of the underlying TableModel
        int[] selection = advancedTable.getSelectedRows();
        for (int i = 0; i < selection.length; i++) 
        {
             selection[i] = table.convertRowIndexToModel(selection[i]);
        }
        Arrays.sort(selection);

        int i = 0;
        int selectionIndex = 0;
        for(SearchHit hit: advancedSearchHits)
        {
            allUri.add(hit.getUri());
            if(selectionIndex < selection.length && selection[selectionIndex] == i) //row is selected
            {
                from.add(hit.getUri());
                selectionIndex++;
            }
            else
            {
                to.add(hit.getUri());
            }
            i++;
        }
        
        //logger.info(Arrays.toString(from.toArray()));
        //logger.info(Arrays.toString(to.toArray()));
        
        String fileName = "Advanced" + BIOPAX_FILE_EXTENSION; //default name of .owl file to be created

        //set up the get or graph query
        if(getRadio.isSelected())
        {
           cPathQuery = client.createGetQuery().sources(allUri); //get multiple sub-graphs
           fileName = "GET" + BIOPAX_FILE_EXTENSION;
           getWorker = new GetWorker(fileName);
           getWorker.execute();
        }
        else //create a graph query
        {   
            CPathGraphQuery graphQuery = client.createGraphQuery();
            //add kind parameter
            if(nearestNeighborhoodRadio.isSelected())
            {                
                /* Searches the neighborhood of given source set of nodes. Any direction. */
                graphQuery = graphQuery.sources(allUri).kind(GraphType.NEIGHBORHOOD);
                fileName = GraphType.NEIGHBORHOOD.toString();

                //add direction parameter
                if(downstreamRadio.isSelected())
                {
                    graphQuery = graphQuery.direction(CPathClient.Direction.DOWNSTREAM);
                    fileName = fileName + "_" + CPathClient.Direction.DOWNSTREAM.toString();
                }
                else if(upstreamRadio.isSelected())
                {
                    graphQuery = graphQuery.direction(CPathClient.Direction.UPSTREAM);
                    fileName = fileName + "_" + CPathClient.Direction.UPSTREAM.toString();
                }
                else //default is both directions
                {
                    graphQuery = graphQuery.direction(CPathClient.Direction.BOTHSTREAM);
                    fileName = fileName + "_" + CPathClient.Direction.BOTHSTREAM.toString();
                }
            
            }
            else if(commonStreamRadio.isSelected())
            {
                /* Searches common downstream or common upstream of a specified set of entities
                based on the given direction within the boundaries of a specified length limit. 
                Direction may be either upstream or downstream.
                */
                graphQuery = graphQuery.sources(allUri).kind(GraphType.COMMONSTREAM);            
                fileName = GraphType.COMMONSTREAM.toString();
            
                //add direction parameter
                if(upstreamRadio.isSelected())
                {
                    graphQuery = graphQuery.direction(CPathClient.Direction.UPSTREAM);
                    fileName = fileName + "_" + CPathClient.Direction.UPSTREAM.toString();
                }
                else //downstream
                {
                    graphQuery = graphQuery.direction(CPathClient.Direction.DOWNSTREAM);
                    fileName = fileName + "_" + CPathClient.Direction.DOWNSTREAM.toString();
                }
            }
            else if(pathsBetweenRadio.isSelected())
            {
                /* Finds the paths between the specified source set of states within the boundaries of a specified length limit.
                Direction not applicable. */
                graphQuery = graphQuery.sources(allUri).kind(GraphType.PATHSBETWEEN);            
                fileName = GraphType.PATHSBETWEEN.toString();
            }
            else if(pathsFromToRadio.isSelected())
            {
                /* Finds the paths between the specified source set of states within the boundaries of a specified length limit.
                    Direction not applicable.   */
                graphQuery = graphQuery.sources(from).targets(to).kind(GraphType.PATHSFROMTO);   
                fileName = GraphType.PATHSFROMTO.toString();
            }

            cPathQuery = graphQuery; //use the generic query interface so we can run get or graph queries the same way
            fileName += BIOPAX_FILE_EXTENSION;
            getWorker = new GetWorker(fileName);
            getWorker.execute();
        }
    }
    
    /**
     * Removes selected rows from the Advanced tab table.
     */
    private void removeAdvanced()
    {
        //remove selected rows from table model and convert selected table row indices to table model indices
        int[] selection = advancedTable.getSelectedRows();
        Arrays.sort(selection);
        for (int i = selection.length - 1; i >= 0; i--) //iterate over table model backwards and remove selected rows
        {
            //convert table row index to model index to safeguard against user sorting columns
            selection[i] = advancedTable.convertRowIndexToModel(selection[i]);
            advancedModel.removeRow(selection[i]); 
        }
        Arrays.sort(selection);

        //remove corresponding search hits
        int i = 0;
        int selectionIndex = 0;        
        Iterator<SearchHit> iterator = advancedSearchHits.iterator();
        while(iterator.hasNext())
        {
            SearchHit hit = iterator.next();
            if(selectionIndex < selection.length && selection[selectionIndex] == i) //row is selected
            {
                iterator.remove();
                selectionIndex++;
            }
            i++;
        }
    }
    
    /**
     * Clears advanced table model and advanced search hits.
     */
    private void clearAdvanced()
    {
        advancedModel.setRowCount(0); //clear previous search results
        advancedSearchHits.clear(); //empty search hits
    }


    /*
     * Performs Pathway Commons CPath2 web service GET operation concurrently and displays graph
     */
    private class GetWorker extends SwingWorker<File, Void>
    {
        /**
         * Constructor
         * @param fileName - name of file to save BioPAX data in
         */
        public GetWorker(String fileName) 
        {
            super();
            fName = fileName;
        }
        
        /**
         * The name of the BioPAX OWL file to be saved in sub-directory DIRECTORY of the application directorY
         */
        private String fName;
        
        @Override
        /**
         * Perform GET operation on Pathway Commons web service to retrieve BioPAX file
         * @return a BioPAX OWL File
         */
        protected File doInBackground() throws Exception
        {
            SwingWorker actualWorker = new SwingWorker<String, Void>() //anonymous inner worker to handle timeout for GET operation
            {
                 @Override
                protected String doInBackground() throws Exception 
                {
                    statusLabel.setText("Downloading...");              
                    ImportWebServiceDialog.this.getRootPane().setCursor(waitCursor);

                    previousButton.setEnabled(false);
                    nextButton.setEnabled(false);
                    searchButton.setEnabled(false);

                    stopButton.setEnabled(true);
                    openButton.setEnabled(false);
                    
                    advancedExecuteButton.setEnabled(false);
                    advancedStopButton.setEnabled(true);
                    
                    String responseString = cPathQuery.stringResult(OutputFormat.BIOPAX);
                    return responseString;
                }
                
            };
            actualWorker.execute();
            String responseString = "";
            try
            {
                responseString = (String)actualWorker.get(TIMEOUT_GET, TimeUnit.SECONDS); //TODO constant 
            }
            catch(Exception exception)
            {
                logger.warning(exception.getMessage());
                actualWorker.cancel(true); //stop inner search thread
                Exception cause = (Exception)exception.getCause(); //PathwayCommonsException is wrapped in ExecutionException
                if(cause != null)
                {
                    exception = cause;
                }
                throw exception;                
            }
            
            if(responseString == null || responseString.isEmpty()) //no data returned for query - do not create a file
            {
                throw new PathwayCommonsException("Empty query results returned from Pathway Commons");
            }
            
            //create directory to store downloaded file
            File importDir = new File(DataFolder.get(), DIRECTORY);
            if(!importDir.exists())
            {
                importDir.mkdir();
            }

            //create file and save web service data
            File importFile = new File(importDir, fName);
            logger.info("Writing to file " + importFile);
            FileUtils.writeStringToFile(importFile, responseString); //throws IOException
            statusLabel.setText("Success! Downloaded file: " + fName);      
            return importFile;
        }
       
        @Override
        /**
         * Save OWL file, parse and display graph
         */
        protected void done() 
        {
            /*
            * @throws PathwayCommonsException - when HTTP status code is not 200
            * @throws IOException - if OWL file cannot be written
            * @throws Exception - web service does not respond
            */
            try
            {
                File importFile = get(); //perform Pathway Commons GET in the background

                //parse and display file
                logger.info("Opening file: " + importFile);
                frame.requestFocus();
                frame.toFront();                        
                frame.loadDataSet(importFile);
                statusLabel.setText("Opened file: " + fName);      
            }
            catch(IllegalStateException exception) //runtime exception
            {
               logger.warning(exception.getMessage());
               statusLabel.setText("Search failed: connection not released");
            }
            catch(InterruptedException exception)
            {
                logger.warning(exception.getMessage());
                statusLabel.setText("Search failed: interrupted");
            }
            catch(Exception exception)
            {
                logger.warning(exception.getMessage());                     
                Throwable cause = exception.getCause(); //get wrapped exception
                if(cause == null)
                {
                    cause = exception;
                }
                     
                if(cause instanceof PathwayCommonsException || cause instanceof CPathException) //HTTP error code returned from GET request
                {
                    logger.warning(exception.getMessage());
                    statusLabel.setText(cause.getMessage());
                }
                else if(cause instanceof UnknownHostException) //Pathway Commons down
                {
                   logger.warning(cause.getMessage());
                   statusLabel.setText("Fetch error: unable to reach Pathway Commons");
                }
                else if(cause instanceof SocketException) //computer offline
                {
                   logger.warning(cause.getMessage());
                   statusLabel.setText("Fetch error: offline");
                }
                else if(cause instanceof TimeoutException) //no response after set time
                {
                   logger.warning(cause.getMessage());
                   statusLabel.setText("Fetch error: timeout");
                }
                else
                {
                    logger.warning(exception.getMessage());
                    statusLabel.setText("Fetch error: unable to get " + fName + " from Pathway Commons"); 
                }
            }
            finally
            {
                ImportWebServiceDialog.this.getRootPane().setCursor(defaultCursor);                
                restoreButtons();
            }
        }
    }
    
    /**
     * Enable or disable dialog buttons when dialog is in resting state according to search results.
     */
    private void restoreButtons()
    {
        stopButton.setEnabled(false);
        
        if(table.getSelectedRow() != -1) //a row is selected
        {
            openButton.setEnabled(true);
        }
        
        searchFieldChanged(); //enable search button if text in search field
        enableDisablePreviousButton(); //enable/disable previous button 
        enableDisableNextButton(); //enable/disable next button
        
        //advanced tab
        advancedStopButton.setEnabled(false);        
        if(advancedModel.getRowCount() > 0)
        {
            advancedExecuteButton.setEnabled(true);
        }
    }
    
    /**
     * Enable or disable the Next button according to whether more search hits exist following the current page
     */
    private void enableDisableNextButton()
    {
        if(maxHitsPerPage > 0 && totalHits > 0) //no hits - avoid division by zero
        {    
            int numPages = (totalHits + maxHitsPerPage - 1) / maxHitsPerPage; //calculate number of pages, round up integer division
            if((currentPage + 1) < numPages) //pages indexed from zero
            {
                nextButton.setEnabled(true);
            }
            else
            {
                nextButton.setEnabled(false); //last page
            }
        }
        else
        {
            nextButton.setEnabled(false);
        }
    }
    
    /**
     * Enable or disable the Previous button according to whether more search hits exist before the current page
     */
    private void enableDisablePreviousButton()
    {
        if(currentPage > 0) //on first page, disable previous
        {
            previousButton.setEnabled(true);
        }
        else
        {
            previousButton.setEnabled(false);
        }
    }
    
    /**
     * Creates a JButton with an ActionListener for this dialog. Convenience method.
     * @param text
     * @param toolTipText
     * @param enabled
     * @return - a new JButton
     */
    private JButton createJButton(String text, String toolTipText, boolean enabled)
    {
        JButton button = new JButton(text);
        button.setToolTipText(toolTipText);
        button.setEnabled(enabled);
        button.addActionListener(this);
        return button;
    }
    
    /**
     * Performs Pathway Commons search concurrently
     */
    private class SearchWorker extends SwingWorker<SearchResponse, Void>
    {
        private boolean clearAdvanced;
        private boolean newSearch;
        
        SearchWorker(boolean newSearch, boolean clearAdvanced)
        {
            this.newSearch = newSearch;
            this.clearAdvanced = clearAdvanced;
        }
        
        /**
         * Perform Pathway Commons search
         */
        @Override
        protected SearchResponse doInBackground() throws Exception
        {
            SwingWorker actualWorker = new SwingWorker<SearchResponse, Void>() //anonymous inner worker to handle timeout
            {
                @Override
                protected SearchResponse doInBackground() throws Exception 
                {
                    //display message/cursor and disable buttons
                    statusLabel.setText("Searching...");
                    ImportWebServiceDialog.this.getRootPane().setCursor(waitCursor);
                    previousButton.setEnabled(false);
                    nextButton.setEnabled(false);
                    searchButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    openButton.setEnabled(false);
                    
                    advancedExecuteButton.setEnabled(false);
                    advancedStopButton.setEnabled(true);
                    
                    return searchQuery.result(); //perform search
                }
            };

            actualWorker.execute();
            try 
            {
                SearchResponse sr = (SearchResponse)actualWorker.get(TIMEOUT_SEARCH, TimeUnit.SECONDS); //15 second timeout
                return sr;
            } 
            catch (Exception exception) 
            {
                logger.warning(exception.getMessage());
                actualWorker.cancel(true); //stop inner search thread
                Exception cause = (Exception)exception.getCause(); //PathwayCommonsException is wrapped in ExecutionException
                if(cause != null)
                {
                    exception = cause;
                }
                throw exception;
            }
        }

        /**
         * Display search results
         */
        @Override
        protected void done()
        {
            try
            {
                SearchResponse searchResponse = get(); //calls doInBackground() to perform search
                if(searchResponse != null)
                {
                    searchHits = searchResponse.getSearchHit();            
                    maxHitsPerPage = searchResponse.getMaxHitsPerPage(); //maximum number of search hits per page
                    totalHits = searchResponse.getNumHits();
                    currentPage = searchResponse.getPageNo();

                    statusLabel.setText("Search complete: success!");
                                        
                    cacheSearchHits(); //store search hits in allSearchHits List
                    
                    displaySearchResults(clearAdvanced);

                    if(organismIdNameMap.size() > 0)
                    {
                        fetchScientificNames(); //populate organismIdNameMap from NCBI SOAP web service
                    }
                }
                else
                {
                    statusLabel.setText("Search complete: no hits");
                }
            }
            catch(IllegalStateException exception) //runtime exception
            {
               logger.warning(exception.getMessage());
               statusLabel.setText("Search failed: connection not released");
            }
            catch(InterruptedException exception)
            {
                logger.warning(exception.getMessage());
                statusLabel.setText("Search failed: interrupted");
            }
            catch(ExecutionException exception)
            {
                 logger.warning(exception.getMessage());                     
                 Throwable cause = exception.getCause(); //get wrapped exception - the culprit!
                 if(cause == null)
                 {
                     cause = exception;
                 }
                     
                if(cause instanceof PathwayCommonsException) //no search hits
                {
                   logger.warning(cause.getMessage());
                   clearSearchResults(false); //clear previous search results
                   statusLabel.setText("Search error: " + cause.getMessage());
                }
                else if(cause instanceof UnknownHostException) //Pathway Commons down
                {
                   logger.warning(cause.getMessage());
                   statusLabel.setText("Search failed: unable to reach Pathway Commons");
                }
                else if(cause instanceof SocketException) //computer offline
                {
                   logger.warning(cause.getMessage());
                   statusLabel.setText("Search failed: offline");
                }
                else if(cause instanceof TimeoutException) //no response after set time
                {
                   logger.warning(cause.getMessage());
                   statusLabel.setText("Search failed: timeout");
                }
                else
                {
                    logger.warning(exception.getMessage());
                    statusLabel.setText("Search failed: generic error");
                }
            }
            finally
            {
                ImportWebServiceDialog.this.getRootPane().setCursor(defaultCursor);                
                restoreButtons();
            }
        }
        
        private void cacheSearchHits()
        {
            if(newSearch) //search button pressed - create new page cache
            {
                allPages = new ArrayList<List<SearchHit>>();
                allPages.add(searchHits);
            }
            else //next or previous button pressed
            {
                if(currentPage >= allPages.size()) //page not added yet
                {
                    allPages.add(currentPage, searchHits);
                }
                else if(allPages.get(currentPage) == null) //placeholder for this page empty - set page
                {
                    allPages.set(currentPage, searchHits);
                }
                //else this page has already been cached - do nothing
            }
        }
    }
    
    /**
     * Creates a HashSet of command strings associated with checked JCheckBoxes.
     * @param displayCommands - Map of JCheckBoxes and associated commands
     * @return a new HashSet of command Strings corresponding to checked JCheckBoxes
     */
    public static HashSet<String> createFilterSet(Map <JCheckBox, String> displayCommands)
    {
        HashSet<String> filterSet = new HashSet<String>();                    
        for (Map.Entry<JCheckBox, String> entry : displayCommands.entrySet()) {
             JCheckBox checkBox = entry.getKey();
             if(checkBox.isSelected())
             {
                 String filterParameter = entry.getValue();
                 filterSet.add(filterParameter);
             }
        }
        return filterSet;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if(searchButton == e.getSource() || nextButton == e.getSource() || previousButton == e.getSource()) 
        {
            if(searchButton == e.getSource())
            {
                currentPage = 0;
                searchTerm = searchField.getText();
                organism = organismField.getText();

                //restrict search to name
                if(nameCheckBox.isSelected())
                {
                    searchTerm = "name:'" + searchTerm + "'";
                }
    
                networkType = this.networkTypeCombo.getSelectedItem().toString();

                //add parameters for datasource checkboxes
                datasourceSet = null;
                if(!allDatasourceCheckBox.isSelected())
                {
                    datasourceSet = createFilterSet(datasourceDisplayCommands);
                }

                //add parameters for organism checkboxes
                organismSet = null;
                if(!anyOrganismCheckBox.isSelected()) //don't add organism parameters if Any is selected
                {
                    organismSet = createFilterSet(organismDisplayCommands);
                    if(!organism.equals(""))
                    {
                        organismSet.add(organism); //TODO multiple organisms comma separated?
                    }
                }
            }

            //CPathClient.newInstance() will block execution if Pathway Commons is down - set a timeout
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Callable<CPathClient> task = new Callable<CPathClient>() {
               public CPathClient call() {
                  return CPathClient.newInstance();
               }
            };
            Future<CPathClient> future = executor.submit(task);
            try
            {
                ImportWebServiceDialog.this.getRootPane().setCursor(waitCursor);
                CPathClient client = future.get(10, TimeUnit.SECONDS); 
              
                //CPathClient client = CPathClient.newInstance(); 
                if(networkType.equals("Top Pathways") && searchButton == e.getSource())
                {
                    searchQuery = client.createTopPathwaysQuery();
                    search(true, false); //perform search but do not clear advanced table
                }
                else
                {
                    //page to retrieve
                    int pageParameter = currentPage;
                    if(nextButton == e.getSource())
                    {
                       pageParameter++;
                    }
                    else if(previousButton == e.getSource())
                    {
                        pageParameter--;
                    }
                    
                    //check if page already exists in cache
                    if(searchButton != e.getSource() && allPages != null && pageParameter < allPages.size() && allPages.get(pageParameter) != null)
                    {
                        searchHits = allPages.get(pageParameter);
                        currentPage = pageParameter;
                        statusLabel.setText("Displaying cached search results");
                        displaySearchResults(false);
                        restoreButtons();
                    }
                    else //run the search
                    {
                        CPathSearchQuery searchQuery = client.createSearchQuery().queryString(searchTerm).typeFilter(networkType);
                        searchQuery.page(pageParameter);                

                        if(datasourceSet != null && !datasourceSet.isEmpty())
                        {
                            searchQuery.datasourceFilter(datasourceSet);
                        }

                        if(organismSet != null && !organismSet.isEmpty())                            
                        {
                            searchQuery.organismFilter(organismSet);
                        }

                        this.searchQuery = (CPathQuery<SearchResponse>)searchQuery;
                        search(searchButton == e.getSource(), false); //perform search but do not clear advanced table
                    }
                }
            }
            catch(HttpClientErrorException exception)
            {
                statusLabel.setText("Unable to connect to Pathway Commons");
                logger.warning(exception.getMessage());
            }
            catch (TimeoutException exception) {
                statusLabel.setText("Unable to connect to Pathway Commons: timed out");
                logger.warning(exception.getMessage());
            } catch (InterruptedException exception) {
                statusLabel.setText("Unable to connect to Pathway Commons");
                logger.warning(exception.getMessage());
            } catch (ExecutionException exception) {
                statusLabel.setText("Unable to connect to Pathway Commons");
                logger.warning(exception.getMessage());
            } finally {
                future.cancel(true); 
                ImportWebServiceDialog.this.getRootPane().setCursor(defaultCursor);
            }           
        }
        else if(stopButton == e.getSource() || advancedStopButton == e.getSource()) //stop running process
        { 
            if(searchWorker != null && !searchWorker.isDone()) //stop search process
            {
                try //stop search
                {
                    statusLabel.setText("Stopping search...");
                    boolean cancelled = searchWorker.cancel(true);
                    if(cancelled)
                    {
                        statusLabel.setText("Search stopped");
                    }
                    else
                    {
                        statusLabel.setText("Operation has already completed");
                    }
                    stopButton.setEnabled(false);
                    advancedStopButton.setEnabled(false);
                    logger.info("search SwingWorker cancel returned " + cancelled);
                }
                catch(CancellationException exception)
                {
                    statusLabel.setText("Unable to stop search");
                    logger.warning("Unable to stop search SwingWorker: " + exception.getMessage());
                }
            }
            
            if(getWorker != null && !getWorker.isDone()) //stop download process
            {
                try //stop download
                {
                    statusLabel.setText("Stopping import...");
                    boolean cancelled = getWorker.cancel(true);
                    if(cancelled)
                    {
                        statusLabel.setText("Import stopped");
                    }
                    else
                    {
                        statusLabel.setText("Import has already completed");
                    }
                    stopButton.setEnabled(false);
                    advancedStopButton.setEnabled(false);
                    logger.info("Download SwingWorker cancel returned " + cancelled);
                }
                catch(CancellationException exception)
                {
                    statusLabel.setText("Unable to stop download");
                    logger.warning("Unable to stop download SwingWorker: " + exception.getMessage());
                }
            }
        }
        else if(cancelButton == e.getSource() || advancedCancelButton == e.getSource())
        {
            //stop search threads
            if(searchWorker != null && !searchWorker.isDone()) //stop search process before closing
            {
                boolean cancelled = searchWorker.cancel(true);
                logger.info("search SwingWorker cancel returned " + cancelled);
            }
            
            //stop GET threads
            if(getWorker != null && !getWorker.isDone())
            {
                boolean cancelled = getWorker.cancel(true);
                logger.info("GET SwingWorker cancel returned " + cancelled);
            }
            
            this.dispose(); //destroy the dialog to free up resources
        }
        else if(openButton == e.getSource()) //search hit selected in table then open button pressed
        {
            openNetwork();
        }
        else if(anyOrganismCheckBox == e.getSource()) //"Any" organism checkbox has been checked or unchecked
        {
            enableDisableOrganism(anyOrganismCheckBox.isSelected()); //enable/disable organism checkboxes and text field
        }
        else if(allDatasourceCheckBox == e.getSource())
        {
            enableDisableDatasource(allDatasourceCheckBox.isSelected()); //enable/disable datasource checkboxes and text field
        }
        else if(advancedExecuteButton == e.getSource())
        {
            openAdvancedNetwork();
        }
        else if(advancedRemoveButton == e.getSource())
        {
            removeAdvanced(); //remove selected rows from advanced table
            statusLabel.setText("Removed selected rows from Advanced");
            
        }
        else if(advancedClearButton == e.getSource()) //clear advanced table
        {
            clearAdvanced();
            statusLabel.setText("Cleared Advanced");
        }
    }

     /**
     * Runs Pathway Commons REST web service SEARCH and displays results
     * @param searchClientRequest - contains search parameters
     * @param clearAdvanced - clear advanced search hits
     */
    private void search(boolean newSearch, boolean clearAdvanced)
    {
        searchWorker = new SearchWorker(newSearch, clearAdvanced); //concurrent threading for search process
        searchWorker.execute();
    }
    

    /**
     * Adds organism NCBI IDs as key to organismIdNameMap so that values may be populated later from NCBI Taxonomy SOAP service
     */
    private void mapOrganisms(SearchHit hit)
    {
        List<String> organismList = hit.getOrganism(); //URIs of organisms at identifiers.org

        //extract organism ID for each organism URI
        String[] organismArray = organismList.toArray(new String[0]);
        for (int i = 0; i < organismArray.length; i++)
        {
            String organismString = organismArray[i];
            organismArray[i] = organismString.substring(organismString.lastIndexOf("/")+1, organismString.length()); //extract ID from URI
            //add to NCBI ID/name map for later web service lookup if not already added
            if(!organismIdNameMap.containsKey(organismArray[i]))
            {
                organismIdNameMap.put(organismArray[i], organismArray[i]); //value also has NCBI ID as placeholder - to be replaced with name from web service
            }
        }
    }
    
    private void displaySearchResults(boolean clearAdvanced)
    {
        //update statistics
        numHitsLabel.setText("Hits: " + totalHits); //display total hits
        pagesLabel.setText("Page: " + currentPage); //display current page number
        retrievedLabel.setText("Retrieved: " + searchHits.size());

        clearSearchResults(clearAdvanced); //clear results tables
        
        for(SearchHit hit : searchHits)
        {
            this.mapOrganisms(hit);
            String joinedDatabases = joinDatabases(hit); //comma-separated string of datasources for display
            model.addRow(new Object[]{hit.getName(), joinedDatabases, hit.getBiopaxClass(), hit.getPathway().size()});  
        }//end for
        tabbedPane.setSelectedIndex(0); //reselect the Search tab
    }
    
    /**
     * Creates a comma-separated String of databases in a SearchHit.
     */
    public static String joinDatabases(SearchHit hit)
    {
            List<String> databases = hit.getDataSource();
            String[] databaseArray = databases.toArray(new String[0]);
            for(int i = 0; i < databaseArray.length; i++)
            {
                String databaseUri = databaseArray[i];

                //replace with database real name if found in map
                for (Map.Entry<String, String> entry : DATABASE_URI_DISPLAY.entrySet()) 
                {
                    String databaseString = entry.getKey();
                    if(databaseUri.contains(databaseString))
                    {
                        databaseArray[i] = entry.getValue();
                    }
                }
            }
            String joinedDatabases = commaJoiner.join(databaseArray);
            return joinedDatabases;
    }
    
    /**
    * Populate organism scientific names from NCBI web service
    */
    private boolean fetchScientificNames()
    {
        EFetchTaxonService service = new EFetchTaxonService();
        EUtilsServiceSoap serviceSoap = service.getEUtilsServiceSoap();
        ObjectFactory objectFactory = new ObjectFactory();
        EFetchRequest requ = objectFactory.createEFetchRequest();
         
        //set comma-separated String of organism IDs as search parameter
        String eFetchQuery = commaJoiner.join(this.organismIdNameMap.keySet());
        logger.info("eFetchQuery: " + eFetchQuery);
        requ.setId(eFetchQuery);
        
        try
        {
            EFetchResult resp = serviceSoap.runEFetch(requ);
            logger.info("EFetchResult: " + resp.getTaxaSet().getTaxon().size() + " Taxa");
            List<TaxonType> taxon = resp.getTaxaSet().getTaxon();
            for(TaxonType taxonType : taxon)
            {
                organismIdNameMap.put(taxonType.getTaxId(), taxonType.getScientificName());
            }
            return true;
        }
        catch(Exception exception) //com.sun.xml.internal.ws.client.ClientTransportException thrown if web service down - will display organism ID as name
        {
            logger.warning("runEFetch failed: " + exception);
            return false;
        }
    }
    
    private void enableDisableOrganism(boolean anySelected)
    {
        //enable/disable organism checkboxes
        for(JCheckBox checkBox: organismDisplayCommands.keySet())
        {
            if(anySelected)
            {
                checkBox.setSelected(true);
                checkBox.setEnabled(false);
            }
            else
            {
                checkBox.setSelected(false);
                checkBox.setEnabled(true);
            }
        }

        //enable/disable organism text field
        if(anySelected)
        {
            organismField.setText("");
            organismField.setEnabled(false);
        }
        else
        {
            organismField.setEnabled(true);
        }
    }
    
    private void enableDisableDatasource(boolean allSelected)
    {
        for(JCheckBox checkBox: datasourceDisplayCommands.keySet())
        {
            if(allSelected)
            {
                checkBox.setSelected(true);
                checkBox.setEnabled(false);
            }
            else
            {
                checkBox.setSelected(false);
                checkBox.setEnabled(true);
            }
        }
    }

    /**
     * Sets the preferred width of the visible column specified by vColIndex. The column
     * will be just wide enough to show the column head and the widest cell in the column.
     * margin pixels are added to the left and right
     * (resulting in an additional width of 2*margin pixels).
     */ 
    public static void packColumn(JTable table, int vColIndex, int margin) 
    {
        DefaultTableColumnModel colModel = (DefaultTableColumnModel)table.getColumnModel();
        TableColumn col = colModel.getColumn(vColIndex);
        int width = 0;

        // Get width of column header
        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }
        java.awt.Component comp = renderer.getTableCellRendererComponent(
            table, col.getHeaderValue(), false, false, 0, 0);
        width = comp.getPreferredSize().width;

        // Get maximum width of column data
        for (int r=0; r<table.getRowCount(); r++) {
            renderer = table.getCellRenderer(r, vColIndex);
            comp = renderer.getTableCellRendererComponent(
                table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
            width = Math.max(width, comp.getPreferredSize().width);
        }

        // Add margin
        width += 2*margin;

        // Set the width
        col.setPreferredWidth(width);
    }

    /**
     * Accessor for search field so search terms can be set externally e.g. from Class Viewer
     * @return search field
     */
    public JTextField getSearchField() 
    {
        return searchField;
    }   
}