package org.BioLayoutExpress3D.Expression.Panels;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.lang.Math;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import static java.lang.Math.*;
import java.text.NumberFormat;
import org.BioLayoutExpress3D.ClassViewerUI.ClassViewerPlotPanel;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.DataStructures.*;
import org.BioLayoutExpress3D.Expression.*;
import org.BioLayoutExpress3D.Expression.Dialogs.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import org.BioLayoutExpress3D.Textures.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.chart.renderer.category.DefaultCategoryItemRenderer;
import org.jfree.chart.renderer.category.StatisticalLineAndShapeRenderer;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.util.ShapeUtilities;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.CategoryDataset;

/**
*
* The Expression Graph Panel class.
*
* @author Full refactoring and all updates by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class ExpressionGraphPanel extends ClassViewerPlotPanel implements ActionListener, ChangeListener
{
    /**
    *  Serial version UID variable for the ExpressionGraph class.
    */
    public static final long serialVersionUID = 111222333444555705L;

    public static final int PAD_X = 60;
    public static final int PAD_BORDER = 5;
    public static final int Y_TICKS = 20;

    public static final Color DESCRIPTIONS_COLOR = Color.BLACK;
    public static final Color GRID_LINES_COLOR = Color.GRAY;
    public static final BasicStroke THIN_BASIC_STROKE = new BasicStroke(0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    public static final BasicStroke THICK_BASIC_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    public static final String EXPRESSION_X_AXIS_LABEL = "Sample";
    public static final String EXPRESSION_Y_AXIS_LABEL = "Intensity";
    public static final int VALUES_FONT_SIZE = 6;
    public static final int AXIS_FONT_SIZE = 14;
    public static final int AXIS_FONT_STYLE = Font.ITALIC | Font.BOLD;
    public static final BasicStroke AXES_BASIC_STROKE = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private static final int WARNING_MESSAGE_FOR_RENDERING_NUMBER_OF_PLOTS = 10;

    private JFrame jframe = null;
    private LayoutFrame layoutFrame = null;
    private ExpressionData expressionData = null;
    private JPanel expressionGraphCheckBoxesPanel = null;

    private JCheckBox gridLinesCheckBox = null;
    private JComboBox<String> classStatComboBox = null;
    private JComboBox<String> selectionStatComboBox = null;
    private JCheckBox axesLegendCheckBox = null;
    private JButton columnInfoButton = null;
    private JPopupMenu columnInfoPopupMenu = null;
    private JComboBox<String> sortColumnAnnotationComboBox = null;
    private JCheckBoxMenuItem sampleNameCheckBox = null;
    private JComboBox<String> transformComboBox = null;
    private JButton exportPlotExpressionProfileAsButton = null;

    private JScrollBar zoomScrollBar = null;
    private JSpinner maximumVisibleSamplesSpinner = null;

    private AbstractAction renderPlotImageToFileAction = null;
    private AbstractAction renderAllCurrentClassSetPlotImagesToFilesAction = null;
    private AbstractAction exportPlotExpressionProfileAsAction = null;

    private JFileChooser exportPlotExpressionProfileToFileChooser = null;
    private FileNameExtensionFilter fileNameExtensionFilterText = null;

    private ChartPanel expressionGraphPlotPanel = null;
    private ExpressionChooseClassesToRenderPlotImagesFromDialog expressionChooseClassesToRenderPlotImagesFromDialog = null;

    public ExpressionGraphPanel(JFrame jframe, LayoutFrame layoutFrame, ExpressionData expressionData)
    {
        super();

        this.jframe = jframe;
        this.layoutFrame = layoutFrame;
        this.expressionData = expressionData;

        initActions();
        initComponents();
        initExportPlotExpressionProfileToFileChooser();
    }

    /**
    *  This method is called from within the constructor to initialize the expression graph panel actions.
    */
    private void initActions()
    {
        exportPlotExpressionProfileAsAction = new AbstractAction("Export Plot Expression Profile As...")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555736L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                save();
            }
        };
        exportPlotExpressionProfileAsAction.setEnabled(false);

        renderPlotImageToFileAction = new AbstractAction("Render Plot To File...")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555736L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                initiateTakeSingleScreenShotProcess();
            }
        };
        renderPlotImageToFileAction.setEnabled(false);

        renderAllCurrentClassSetPlotImagesToFilesAction = new AbstractAction("Render Class Set Plots To Files...")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 112222333444555993L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                expressionChooseClassesToRenderPlotImagesFromDialog.setVisible(true);
            }
        };
        renderAllCurrentClassSetPlotImagesToFilesAction.setEnabled(false);
    }

    private enum StatisticType
    {
        Individual_Lines,
        Mean_Line,
        Mean_Histogram,
        Mean_With_Std_Dev,
        Mean_Line_With_Std_Dev,
        Mean_Histogram_With_Std_Dev,
        Mean_With_Std_Err,
        Mean_Line_With_Std_Err,
        Mean_Histogram_With_Std_Err,
        IQR_Box_Plot
    }

    /**
    *  This method is called from within the constructor to initialize the expression graph panel components.
    */
    private void initComponents()
    {
        gridLinesCheckBox = new JCheckBox("Grid Lines");
        gridLinesCheckBox.setToolTipText("Grid Lines");
        axesLegendCheckBox = new JCheckBox("Axes Legend");
        axesLegendCheckBox.setToolTipText("Axes Legend");
        exportPlotExpressionProfileAsButton = new JButton(exportPlotExpressionProfileAsAction);
        exportPlotExpressionProfileAsButton.setToolTipText("Export Plot Expression Profile As...");
        gridLinesCheckBox.addActionListener(this);
        axesLegendCheckBox.addActionListener(this);
        gridLinesCheckBox.setSelected(PLOT_GRID_LINES.get());
        axesLegendCheckBox.setSelected(PLOT_AXES_LEGEND.get());
        maximumVisibleSamplesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        maximumVisibleSamplesSpinner.getModel().addChangeListener(this);

        classStatComboBox = new JComboBox<String>();
        for (StatisticType type : StatisticType.values())
        {
            String s = Utils.titleCaseOf(type.toString());
            classStatComboBox.addItem(s);
        }
        classStatComboBox.setSelectedIndex(PLOT_CLASS_STATISTIC_TYPE.get());
        classStatComboBox.setToolTipText("Class Plot");
        classStatComboBox.addActionListener(this);

        selectionStatComboBox = new JComboBox<String>();
        for (StatisticType type : StatisticType.values())
        {
            String s = Utils.titleCaseOf(type.toString());
            selectionStatComboBox.addItem(s);
        }
        selectionStatComboBox.setSelectedIndex(PLOT_SELECTION_STATISTIC_TYPE.get());
        selectionStatComboBox.setToolTipText("Selection Plot");
        selectionStatComboBox.addActionListener(this);

        transformComboBox = new JComboBox<String>();
        for (ExpressionEnvironment.TransformType type : ExpressionEnvironment.TransformType.values())
        {
            String s = Utils.titleCaseOf(type.toString());
            transformComboBox.addItem(s);
        }
        transformComboBox.setSelectedIndex(PLOT_TRANSFORM.get());
        transformComboBox.setToolTipText("Transform");
        transformComboBox.addActionListener(this);

        columnInfoPopupMenu = new JPopupMenu();
        columnInfoButton = new JButton();
        columnInfoButton.setAction(new AbstractAction("Column Info")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                columnInfoPopupMenu.show(columnInfoButton, 0, columnInfoButton.getHeight());
            }
        });

        sortColumnAnnotationComboBox = new JComboBox<String>();
        sortColumnAnnotationComboBox.addItem("No Column Sort");
        for (ExpressionData.ColumnAnnotation columnAnnotation : expressionData.getColumnAnnotations())
        {
            sortColumnAnnotationComboBox.addItem(columnAnnotation.getName());
        }
        sortColumnAnnotationComboBox.setToolTipText("Sort Column Annotation");
        sortColumnAnnotationComboBox.addActionListener(this);

        sampleNameCheckBox = new JCheckBoxMenuItem("Sample names");
        sampleNameCheckBox.addActionListener(this);

        JPanel expressionGraphUpperPartPanel = new JPanel(true);

        expressionGraphCheckBoxesPanel = new JPanel(true);
        JPanel plotOptionsLine1 = new JPanel();
        JPanel plotOptionsLine2 = new JPanel();
        plotOptionsLine1.add(new JLabel("Scaling:"));
        plotOptionsLine1.add(transformComboBox);
        plotOptionsLine1.add(new JLabel("Maximum Samples:"));
        plotOptionsLine1.add(maximumVisibleSamplesSpinner);
        plotOptionsLine1.add(columnInfoButton);
        plotOptionsLine1.add(sortColumnAnnotationComboBox);
        plotOptionsLine1.add(gridLinesCheckBox);
        plotOptionsLine1.add(axesLegendCheckBox);
        plotOptionsLine2.add(new JLabel("Class Plot:"));
        plotOptionsLine2.add(classStatComboBox);
        plotOptionsLine2.add(new JLabel("Selection Plot:"));
        plotOptionsLine2.add(selectionStatComboBox);
        expressionGraphCheckBoxesPanel.setLayout(new BoxLayout(expressionGraphCheckBoxesPanel, BoxLayout.PAGE_AXIS));
        expressionGraphCheckBoxesPanel.add(plotOptionsLine1);
        expressionGraphCheckBoxesPanel.add(plotOptionsLine2);

        expressionGraphPlotPanel = createChartPanel();
        expressionChooseClassesToRenderPlotImagesFromDialog = new ExpressionChooseClassesToRenderPlotImagesFromDialog(jframe, layoutFrame, this);

        JPanel expressionGraphButtonPanel = new JPanel(true);
        expressionGraphButtonPanel.add(exportPlotExpressionProfileAsButton);

        JPanel scrollerPanel = new JPanel(new BorderLayout());
        zoomScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 1);
        zoomScrollBar.getModel().addChangeListener(this);
        scrollerPanel.add(zoomScrollBar, BorderLayout.CENTER);
        scrollerPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        expressionGraphUpperPartPanel.setLayout( new BorderLayout() );
        expressionGraphUpperPartPanel.add(expressionGraphCheckBoxesPanel, BorderLayout.NORTH);
        expressionGraphUpperPartPanel.add(expressionGraphPlotPanel, BorderLayout.CENTER);
        expressionGraphUpperPartPanel.add(scrollerPanel, BorderLayout.SOUTH);

        this.setLayout( new BoxLayout(this, BoxLayout.Y_AXIS) );
        this.add(expressionGraphUpperPartPanel);
        this.add(expressionGraphButtonPanel);
        this.add( Box.createRigidArea( new Dimension(10, 10) ) );
    }

    private void initExportPlotExpressionProfileToFileChooser()
    {
        String saveFilePath = FILE_CHOOSER_PATH.get().substring(0, FILE_CHOOSER_PATH.get().lastIndexOf( System.getProperty("file.separator") ) + 1);
        exportPlotExpressionProfileToFileChooser = new JFileChooser(saveFilePath);
        fileNameExtensionFilterText = new FileNameExtensionFilter("Save as a Text File", "txt");
        exportPlotExpressionProfileToFileChooser.setFileFilter(fileNameExtensionFilterText);
        exportPlotExpressionProfileToFileChooser.setDialogTitle("Export Plot Expression Profile As");
    }

    private CategoryPlot mainPlot = null;
    private CategoryPlot columnAnnotationPlot = null;
    private CombinedDomainCategoryPlot combinedDomainCategoryPlot = null;

    class RowData
    {
        public RowData(int columns, Color color)
        {
            rows = new ArrayList<Integer>();
            classColor = color;
        }

        public ArrayList<Integer> rows;
        public Color classColor;
    }

    class SlidingStatisticalCategoryDataset extends SlidingCategoryDataset implements StatisticalCategoryDataset
    {
        public SlidingStatisticalCategoryDataset(StatisticalCategoryDataset underlying,
                int firstColumn, int maxColumns)
        {
            super(underlying, firstColumn, maxColumns);
            this.underlying = underlying;
        }

        private StatisticalCategoryDataset underlying;

        public @Override StatisticalCategoryDataset getUnderlyingDataset()
        {
            return underlying;
        }

        public @Override java.lang.Number getMeanValue(int row, int column)
        {
            return underlying.getMeanValue(row, column + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getMeanValue(Comparable rowKey, Comparable columnKey)
        {
            return underlying.getMeanValue(getRowIndex(rowKey), getColumnIndex(columnKey) + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getStdDevValue(int row, int column)
        {
            return underlying.getStdDevValue(row, column + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getStdDevValue(Comparable rowKey, Comparable columnKey)
        {
            return underlying.getStdDevValue(getRowIndex(rowKey), getColumnIndex(columnKey) + getFirstCategoryIndex());
        }
    }

    class SlidingBoxAndWhiskerCategoryDataset extends SlidingCategoryDataset implements BoxAndWhiskerCategoryDataset
    {
        public SlidingBoxAndWhiskerCategoryDataset(BoxAndWhiskerCategoryDataset underlying,
                int firstColumn, int maxColumns)
        {
            super(underlying, firstColumn, maxColumns);
            this.underlying = underlying;
        }

        private BoxAndWhiskerCategoryDataset underlying;

        public @Override BoxAndWhiskerCategoryDataset getUnderlyingDataset()
        {
            return underlying;
        }

        public @Override java.lang.Number getMeanValue(int row, int column)
        {
            return underlying.getMeanValue(row, column + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getMeanValue(Comparable rowKey, Comparable columnKey)
        {
            return underlying.getMeanValue(getRowIndex(rowKey), getColumnIndex(columnKey) + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getMedianValue(int row, int column)
        {
            return underlying.getMedianValue(row, column + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getMedianValue(Comparable rowKey, Comparable columnKey)
        {
            return underlying.getMedianValue(getRowIndex(rowKey), getColumnIndex(columnKey) + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getQ1Value(int row, int column)
        {
            return underlying.getQ1Value(row, column + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getQ1Value(Comparable rowKey, Comparable columnKey)
        {
            return underlying.getQ1Value(getRowIndex(rowKey), getColumnIndex(columnKey) + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getQ3Value(int row, int column)
        {
            return underlying.getQ3Value(row, column + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getQ3Value(Comparable rowKey, Comparable columnKey)
        {
            return underlying.getQ3Value(getRowIndex(rowKey), getColumnIndex(columnKey) + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getMinRegularValue(int row, int column)
        {
            return underlying.getMinRegularValue(row, column + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getMinRegularValue(Comparable rowKey, Comparable columnKey)
        {
            return underlying.getMinRegularValue(getRowIndex(rowKey), getColumnIndex(columnKey) + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getMaxRegularValue(int row, int column)
        {
            return underlying.getMaxRegularValue(row, column + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getMaxRegularValue(Comparable rowKey, Comparable columnKey)
        {
            return underlying.getMaxRegularValue(getRowIndex(rowKey), getColumnIndex(columnKey) + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getMinOutlier(int row, int column)
        {
            return underlying.getMinOutlier(row, column + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getMinOutlier(Comparable rowKey, Comparable columnKey)
        {
            return underlying.getMinOutlier(getRowIndex(rowKey), getColumnIndex(columnKey) + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getMaxOutlier(int row, int column)
        {
            return underlying.getMaxOutlier(row, column + getFirstCategoryIndex());
        }

        public @Override java.lang.Number getMaxOutlier(Comparable rowKey, Comparable columnKey)
        {
            return underlying.getMaxOutlier(getRowIndex(rowKey), getColumnIndex(columnKey) + getFirstCategoryIndex());
        }

        public @Override java.util.List getOutliers(int row, int column)
        {
            return underlying.getOutliers(row, column + getFirstCategoryIndex());
        }

        public @Override java.util.List getOutliers(java.lang.Comparable rowKey, java.lang.Comparable columnKey)
        {
            return underlying.getOutliers(getRowIndex(rowKey), getColumnIndex(columnKey) + getFirstCategoryIndex());
        }
    }

    class CategoryPlotReversedLegend extends CategoryPlot
    {
        public CategoryPlotReversedLegend()
        {
            super();
        }

        public CategoryPlotReversedLegend(CategoryDataset dataset, CategoryAxis domainAxis,
                ValueAxis rangeAxis, CategoryItemRenderer renderer)
        {
            super(dataset, domainAxis, rangeAxis, renderer);
        }

        public @Override LegendItemCollection getLegendItems()
        {
            // This is a monstrously ineffcient and hacky way of getting the legend in the right order
            // but for the number of items involved it's probably not too bad
            LegendItemCollection superLegendItems = super.getLegendItems();
            LegendItemCollection reversedLegendItems = new LegendItemCollection();
            int legendItemCount = superLegendItems.getItemCount();

            HashSet<String> orderedLegendLabels = new LinkedHashSet<String>();

            for (ExpressionData.ColumnAnnotation annotation : expressionData.getColumnAnnotations())
            {
                String annotationName = annotation.getName();

                for (int column = 0; column < expressionData.getTotalColumns(); column++)
                {
                    String annotationValue = annotation.getFullyQualifiedValue(column);
                    orderedLegendLabels.add(annotationValue);
                }
            }

            for (String legendLabel : orderedLegendLabels)
            {
                for (int legendItemIndex = 0; legendItemIndex < legendItemCount; legendItemIndex++)
                {
                    LegendItem legendItem = superLegendItems.get(legendItemIndex);

                    if (legendItem.getLabel().equals(legendLabel))
                    {
                        reversedLegendItems.add(legendItem);
                        break;
                    }
                }
            }

            return reversedLegendItems;
        }
    }

    private void addStatisticalPlot(int datasetIndex, int seriesIndex, ArrayList<Integer> rows, Color color, String className,
            StatisticType type)
    {
        int numColumns = expressionData.getTotalColumns();
        float[] mean;
        float[] stddev;
        float[] stderr;

        switch (type)
        {
            case Individual_Lines:
                break;

            case Mean_Line:
            {
                mean = expressionData.getMeanForRows(rows);

                SlidingCategoryDataset slidingDataset = (SlidingCategoryDataset)mainPlot.getDataset(datasetIndex);
                DefaultCategoryDataset dataset;
                AbstractCategoryItemRenderer r = (AbstractCategoryItemRenderer)mainPlot.getRenderer(datasetIndex);

                if (slidingDataset == null)
                {
                    dataset = new DefaultCategoryDataset();
                    slidingDataset = new SlidingCategoryDataset(dataset, 0, maximumVisibleSamples());
                    r = new DefaultCategoryItemRenderer();
                }
                else
                {
                    dataset = (DefaultCategoryDataset)slidingDataset.getUnderlyingDataset();
                }

                for (int column = 0; column < numColumns; column++)
                {
                    String columnName = expressionData.getColumnName(column);
                    dataset.addValue(mean[column], "Mean of " + className, columnName);
                }

                DefaultCategoryItemRenderer dcir = (DefaultCategoryItemRenderer)r;
                mainPlot.setDataset(datasetIndex, slidingDataset);
                dcir.setSeriesPaint(seriesIndex, color);
                dcir.setSeriesShapesVisible(seriesIndex, false);
                dcir.setSeriesStroke(seriesIndex, new BasicStroke(3.0f, 1, 1, 1.0f, new float[]
                        {
                            9.0f, 4.0f
                        }, 0.0f));
                dcir.setSeriesVisibleInLegend(seriesIndex, false);

                // The shapes aren't shown, but this defines the tooltip hover zone
                dcir.setBaseShape(new Rectangle2D.Double(-10.0, -10.0, 20.0, 20.0));
                dcir.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());

                mainPlot.setRenderer(datasetIndex, dcir);
            }
            break;

            case Mean_Histogram:
            {
                mean = expressionData.getMeanForRows(rows);

                SlidingCategoryDataset slidingDataset = (SlidingCategoryDataset)mainPlot.getDataset(datasetIndex);
                DefaultCategoryDataset dataset;
                AbstractCategoryItemRenderer r = (AbstractCategoryItemRenderer)mainPlot.getRenderer(datasetIndex);

                if (slidingDataset == null)
                {
                    dataset = new DefaultCategoryDataset();
                    slidingDataset = new SlidingCategoryDataset(dataset, 0, maximumVisibleSamples());
                    r = new BarRenderer();
                }
                else
                {
                    dataset = (DefaultCategoryDataset)slidingDataset.getUnderlyingDataset();
                }

                for (int column = 0; column < mean.length; column++)
                {
                    String columnName = expressionData.getColumnName(column);
                    dataset.addValue(mean[column], "Mean of " + className, columnName);
                }

                BarRenderer br = (BarRenderer)r;
                mainPlot.setDataset(datasetIndex, slidingDataset);
                br.setSeriesPaint(seriesIndex, color);
                br.setSeriesVisibleInLegend(seriesIndex, false);

                // The shapes aren't shown, but this defines the tooltip hover zone
                br.setBaseShape(new Rectangle2D.Double(-10.0, -10.0, 20.0, 20.0));
                br.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
                br.setShadowVisible(false);
                br.setBarPainter(new StandardBarPainter());

                mainPlot.setRenderer(datasetIndex, br);
            }
            break;

            case Mean_With_Std_Dev:
            case Mean_Line_With_Std_Dev:
            case Mean_Histogram_With_Std_Dev:
            case Mean_With_Std_Err:
            case Mean_Line_With_Std_Err:
            case Mean_Histogram_With_Std_Err:
            {
                SlidingStatisticalCategoryDataset slidingDataset = (SlidingStatisticalCategoryDataset)mainPlot.getDataset(datasetIndex);
                DefaultStatisticalCategoryDataset dataset;
                AbstractCategoryItemRenderer r = (AbstractCategoryItemRenderer)mainPlot.getRenderer(datasetIndex);

                if (slidingDataset == null)
                {
                    dataset = new DefaultStatisticalCategoryDataset();
                    slidingDataset = new SlidingStatisticalCategoryDataset(dataset, 0, maximumVisibleSamples());

                    switch (type)
                    {
                        case Mean_Histogram_With_Std_Dev:
                        case Mean_Histogram_With_Std_Err:
                            StatisticalBarRenderer sbr = new StatisticalBarRenderer();
                            sbr.setErrorIndicatorPaint(Color.black);
                            r = sbr;
                            break;

                        case Mean_Line_With_Std_Dev:
                        case Mean_Line_With_Std_Err:
                        {
                            StatisticalLineAndShapeRenderer slsr = new StatisticalLineAndShapeRenderer(true, true);
                            slsr.setUseSeriesOffset(true);
                            r = slsr;
                            break;
                        }

                        default:
                        {
                            StatisticalLineAndShapeRenderer slsr = new StatisticalLineAndShapeRenderer(false, true);
                            slsr.setUseSeriesOffset(true);
                            r = slsr;
                            break;
                        }
                    }
                }
                else
                {
                    dataset = (DefaultStatisticalCategoryDataset)slidingDataset.getUnderlyingDataset();
                }

                for (int column = 0; column < numColumns; column++)
                {
                    String columnName = expressionData.getColumnName(column);

                    switch (type)
                    {
                        case Mean_With_Std_Dev:
                        case Mean_Line_With_Std_Dev:
                        case Mean_Histogram_With_Std_Dev:
                            mean = expressionData.getMeanForRows(rows);
                            stddev = expressionData.getStddevForRows(rows);
                            dataset.add(mean[column], stddev[column], className, columnName);
                            break;

                        case Mean_With_Std_Err:
                        case Mean_Line_With_Std_Err:
                        case Mean_Histogram_With_Std_Err:
                            mean = expressionData.getMeanForRows(rows);
                            stderr = expressionData.getStderrForRows(rows);
                            dataset.add(mean[column], stderr[column], className, columnName);
                            break;
                    }
                }

                mainPlot.setDataset(datasetIndex, slidingDataset);

                r.setSeriesShape(seriesIndex, ShapeUtilities.createDiamond(3.0f));
                r.setSeriesPaint(seriesIndex, color);
                r.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
                r.setSeriesVisibleInLegend(seriesIndex, false);
                mainPlot.setRenderer(datasetIndex, r);
            }
            break;

            case IQR_Box_Plot:
            {
                SlidingBoxAndWhiskerCategoryDataset slidingDataset = (SlidingBoxAndWhiskerCategoryDataset)mainPlot.getDataset(datasetIndex);
                DefaultBoxAndWhiskerCategoryDataset dataset;
                AbstractCategoryItemRenderer r = (AbstractCategoryItemRenderer)mainPlot.getRenderer(datasetIndex);

                if (slidingDataset == null)
                {
                    dataset = new DefaultBoxAndWhiskerCategoryDataset();
                    slidingDataset = new SlidingBoxAndWhiskerCategoryDataset(dataset, 0, maximumVisibleSamples());
                    r = new BoxAndWhiskerRenderer();
                }
                else
                {
                    dataset = (DefaultBoxAndWhiskerCategoryDataset)slidingDataset.getUnderlyingDataset();
                }

                ArrayList<float[]> data = new ArrayList<float[]>();
                for (int rowIndex : rows)
                {
                    data.add(expressionData.getTransformedRow(rowIndex));
                }

                for (int column = 0; column < numColumns; column++)
                {
                    ArrayList<Double> values = new ArrayList<Double>();
                    for (float[] row : data)
                    {
                        values.add((double) row[column]);
                    }

                    String columnName = expressionData.getColumnName(column);
                    dataset.add(values, className, columnName);
                }

                BoxAndWhiskerRenderer bawr = (BoxAndWhiskerRenderer)r;
                mainPlot.setDataset(datasetIndex, slidingDataset);
                bawr.setSeriesPaint(seriesIndex, color);
                bawr.setMedianVisible(true);
                bawr.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
                bawr.setSeriesVisibleInLegend(seriesIndex, false);
                mainPlot.setRenderer(datasetIndex, bawr);
            }
            break;
        }
    }

    @Override
    public void refreshPlot()
    {
        boolean drawGridLines = PLOT_GRID_LINES.get();
        boolean drawStatsOfClass = StatisticType.values()[PLOT_CLASS_STATISTIC_TYPE.get()] != StatisticType.Individual_Lines;
        boolean drawStatsOfSelection = StatisticType.values()[PLOT_SELECTION_STATISTIC_TYPE.get()] != StatisticType.Individual_Lines;
        boolean drawAxesLegend = PLOT_AXES_LEGEND.get();

        HashSet<GraphNode> expandedSelectedNodes =
                layoutFrame.getGraph().getSelectionManager().getExpandedSelectedNodes();
        int numSelectedNodes = expandedSelectedNodes.size();

        int totalColumns = expressionData.getTotalColumns();
        int datasetIndex = 0;

        mainPlot.getRangeAxis().setAutoRange(false);
        mainPlot.setNotify(false);

        if (combinedDomainCategoryPlot.getSubplots().contains(columnAnnotationPlot))
        {
            combinedDomainCategoryPlot.remove(columnAnnotationPlot);
            columnAnnotationPlot = null;
        }

        if (numSelectedNodes > 0 && totalColumns > 0)
        {
            ExpressionEnvironment.TransformType transformType =
                    ExpressionEnvironment.TransformType.values()[PLOT_TRANSFORM.get()];
            expressionData.setTransformType(transformType);

            if (sortColumnAnnotationComboBox.getSelectedIndex() > 0)
            {
                expressionData.setSortColumnAnnotation((String) sortColumnAnnotationComboBox.getSelectedItem());
            }
            else
            {
                expressionData.setSortColumnAnnotation(null);
            }

            // Mean of selection
            RowData meanOfSelection = new RowData(totalColumns, new Color(0));
            int meanR = 0;
            int meanG = 0;
            int meanB = 0;

            // Mean of class
            HashMap<VertexClass, RowData> meanOfClassMap = new HashMap<VertexClass, RowData>();

            for (GraphNode graphNode : expandedSelectedNodes)
            {
                Integer index = expressionData.getIdentityMap(graphNode.getNodeName());
                if (index == null)
                {
                    continue;
                }

                float[] transformedData = expressionData.getTransformedRow(index);

                Color nodeColor = graphNode.getColor();
                VertexClass nodeClass = graphNode.getVertexClass();

                if (drawStatsOfSelection)
                {
                    meanOfSelection.rows.add(index);
                    meanR += nodeColor.getRed();
                    meanG += nodeColor.getGreen();
                    meanB += nodeColor.getBlue();
                }

                if (drawStatsOfClass)
                {
                    if (!meanOfClassMap.containsKey(nodeClass))
                    {
                        meanOfClassMap.put(nodeClass, new RowData(totalColumns, nodeColor));
                    }

                    RowData data = meanOfClassMap.get(nodeClass);
                    data.rows.add(index);
                }

                if (!drawStatsOfSelection && !drawStatsOfClass)
                {
                    String nodeName = graphNode.getNodeName();

                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                    for (int column = 0; column < totalColumns; column++)
                    {
                        String columnName = expressionData.getColumnName(column);
                        dataset.addValue(transformedData[column], nodeName, columnName);
                    }

                    mainPlot.setDataset(datasetIndex, new SlidingCategoryDataset(dataset, 0, maximumVisibleSamples()));
                    DefaultCategoryItemRenderer r = new DefaultCategoryItemRenderer();
                    r.setSeriesPaint(0, nodeColor);
                    r.setSeriesShapesVisible(0, false);

                    // The shapes aren't shown, but this defines the tooltip hover zone
                    r.setBaseShape(new Rectangle2D.Double(-10.0, -10.0, 20.0, 20.0));
                    r.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
                    r.setSeriesVisibleInLegend(0, false);

                    mainPlot.setRenderer(datasetIndex, r);
                    datasetIndex++;
                }
            }

            mainPlot.setDataset(datasetIndex, null);
            if (drawStatsOfSelection)
            {
                Color color = new Color(meanR / numSelectedNodes, meanG / numSelectedNodes, meanB / numSelectedNodes);

                addStatisticalPlot(datasetIndex, 0, meanOfSelection.rows, color, "Mean",
                        StatisticType.values()[PLOT_SELECTION_STATISTIC_TYPE.get()]);
                datasetIndex++;
            }

            mainPlot.setDataset(datasetIndex, null);
            if (drawStatsOfClass)
            {
                int seriesIndex = 0;
                for (Map.Entry<VertexClass, RowData> entry : meanOfClassMap.entrySet())
                {
                    VertexClass vertexClass = entry.getKey();
                    RowData data = entry.getValue();
                    Color color = entry.getValue().classColor;

                    addStatisticalPlot(datasetIndex, seriesIndex++, data.rows, color, vertexClass.getName(),
                        StatisticType.values()[PLOT_CLASS_STATISTIC_TYPE.get()]);
                }
                datasetIndex++;
            }

            refreshZoomScrollbar();

            SpinnerNumberModel snm = (SpinnerNumberModel)maximumVisibleSamplesSpinner.getModel();
            snm.setMaximum(totalColumns);
        }

        // Remove any datasets that shouldn't be displayed any more
        while (datasetIndex < mainPlot.getDatasetCount())
        {
            mainPlot.setDataset(datasetIndex, null);
            mainPlot.setRenderer(datasetIndex, null);
            datasetIndex++;
        }

        // This works around an apparent bug in JFreeChart whereby a CombinedDomainCategoryPlot's subplot
        // has no dataset and its categories are enumerated in CombinedDomainCategoryPlot.getCategories()
        combinedDomainCategoryPlot.getDomainAxis().setVisible(mainPlot.getCategories() != null);

        if (drawAxesLegend)
        {
            mainPlot.getDomainAxis().setLabel(EXPRESSION_X_AXIS_LABEL);
            mainPlot.getRangeAxis().setLabel(EXPRESSION_Y_AXIS_LABEL);
        }
        else
        {
            mainPlot.getDomainAxis().setLabel(null);
            mainPlot.getRangeAxis().setLabel(null);
        }

        for (datasetIndex = 0; datasetIndex < mainPlot.getDatasetCount(); datasetIndex++)
        {
            SlidingCategoryDataset slidingDataset = (SlidingCategoryDataset) mainPlot.getDataset(datasetIndex);
            if (slidingDataset != null)
            {
                slidingDataset.setFirstCategoryIndex(zoomScrollBar.getValue());
                slidingDataset.setMaximumCategoryCount(maximumVisibleSamples());
            }
        }

        mainPlot.getRangeAxis().setAutoRange(true);
        mainPlot.setNotify(true);

        mainPlot.setRangeGridlinesVisible(drawGridLines);
        mainPlot.setDomainGridlinesVisible(drawGridLines);
        mainPlot.getDomainAxis().setTickLabelsVisible(sampleNameCheckBox.isSelected());
        mainPlot.setBackgroundPaint(PLOT_BACKGROUND_COLOR.get());
        mainPlot.setRangeGridlinePaint(PLOT_GRIDLINES_COLOR.get());
        mainPlot.setDomainGridlinePaint(PLOT_GRIDLINES_COLOR.get());

        CategoryDataset dataset = createColumnAnnotationDataset();

        if (dataset.getRowCount() > 0)
        {
            // What is actually happening here is a StackedBarRenderer is being used with each datapoint
            // equal to 1, so that all the stacks line up. Essentially this is a way of getting something
            // similar to an XYBlockRenderer but on a category basis. It's hacky, but it substantially works.
            StackedBarRenderer sbr = new StackedBarRenderer();
            sbr.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("{0}, {1}", NumberFormat.getInstance()));
            sbr.setShadowVisible(false);
            sbr.setDrawBarOutline(false);
            sbr.setBarPainter(new StandardBarPainter());

            for (int seriesIndex = 0; seriesIndex < dataset.getRowKeys().size(); seriesIndex++)
            {
                sbr.setSeriesVisibleInLegend(seriesIndex, false); // Hide the legend, always; it's "too confusing"
            }

            columnAnnotationPlot = new CategoryPlotReversedLegend(null, new CategoryAxis(), new NumberAxis(), sbr);

            SlidingCategoryDataset columnAnnotationDataset = new SlidingCategoryDataset(
                    dataset, 0, maximumVisibleSamples());
            columnAnnotationDataset.setFirstCategoryIndex(zoomScrollBar.getValue());
            columnAnnotationDataset.setMaximumCategoryCount(maximumVisibleSamples());
            columnAnnotationPlot.setDataset(columnAnnotationDataset);

            columnAnnotationPlot.setRangeGridlinesVisible(false);
            columnAnnotationPlot.setDomainGridlinesVisible(false);
            columnAnnotationPlot.getRangeAxis().setTickMarksVisible(false);
            columnAnnotationPlot.getRangeAxis().setTickLabelsVisible(false);

            combinedDomainCategoryPlot.add(columnAnnotationPlot);
            int numAnnotations = selectedAnnotations().size();
            int totalWeight = 40;
            int annotationsWeight = Math.min(numAnnotations, totalWeight / 2);
            mainPlot.setWeight(totalWeight - annotationsWeight);
            columnAnnotationPlot.setWeight(annotationsWeight);

            // This keeps the colours consistent
            combinedDomainCategoryPlot.setDrawingSupplier(new DefaultDrawingSupplier());
        }

        exportPlotExpressionProfileAsAction.setEnabled(!expandedSelectedNodes.isEmpty());
    }

    private ArrayList<String> selectedAnnotations()
    {
        ArrayList<String> selectedAnnotations = new ArrayList<String>();
        for (Component component : columnInfoPopupMenu.getComponents())
        {
            JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) component;

            if (cbmi.isSelected())
            {
                selectedAnnotations.add(cbmi.getText());
            }
        }

        return selectedAnnotations;
    }

    private CategoryDataset createColumnAnnotationDataset()
    {
        ExpressionData.ColumnAnnotation sortColumnAnnotation =
                expressionData.getColumnAnnotationByName((String)sortColumnAnnotationComboBox.getSelectedItem());
        int[] sortedColumnMap = null;

        if (sortColumnAnnotation != null)
        {
            sortedColumnMap = sortColumnAnnotation.getSortedColumnMap();
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        int totalColumns = expressionData.getTotalColumns();

        ArrayList<ExpressionData.ColumnAnnotation> annotations =
                new ArrayList<ExpressionData.ColumnAnnotation>(expressionData.getColumnAnnotations());
        Collections.reverse(annotations); // So the rows appear in the same order as the source file

        for (ExpressionData.ColumnAnnotation annotation : annotations)
        {
            String annotationName = annotation.getName();

            if(!selectedAnnotations().contains(annotationName))
            {
                continue;
            }

            for (int column = 0; column < totalColumns; column++)
            {
                int mappedColumn = column;
                if (sortedColumnMap != null)
                {
                    mappedColumn = sortedColumnMap[column];
                }

                String annotationValue = annotation.getFullyQualifiedValue(mappedColumn);
                String columnName = expressionData.getColumnName(mappedColumn);
                dataset.addValue(1.0, annotationValue, columnName);
            }
        }

        return dataset;
    }

    private ChartPanel createChartPanel()
    {
        CategoryAxis categoryAxis = new CategoryAxis();
        categoryAxis.setLowerMargin(0.0);
        categoryAxis.setUpperMargin(0.0);
        categoryAxis.setCategoryMargin(0.0);
        categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);

        LineAndShapeRenderer lasr = new LineAndShapeRenderer(true, false);
        lasr.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        mainPlot = new CategoryPlot(null, categoryAxis, new NumberAxis(), lasr);
        mainPlot.setBackgroundPaint(PLOT_BACKGROUND_COLOR.get());
        mainPlot.setRangeGridlinePaint(PLOT_GRIDLINES_COLOR.get());
        mainPlot.setDomainGridlinePaint(PLOT_GRIDLINES_COLOR.get());

        combinedDomainCategoryPlot = new CombinedDomainCategoryPlot(categoryAxis);
        combinedDomainCategoryPlot.add(mainPlot);
        combinedDomainCategoryPlot.setGap(0.0);
        combinedDomainCategoryPlot.setDomainGridlinesVisible(false);

        JFreeChart expressionGraphJFreeChart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, combinedDomainCategoryPlot, true);
        ChartPanel chartPanel = new ChartPanel(expressionGraphJFreeChart);
        chartPanel.setMaximumDrawWidth(4096);
        chartPanel.setMaximumDrawHeight(4096);

        return chartPanel;
    }

    private void initiateTakeSingleScreenShotProcess()
    {
        File saveScreenshotFile = layoutFrame.getGraph().saveImageToFile(jframe, "Render Plot Image To File As", "plot");
        if (saveScreenshotFile != null)
        {
            if (!savePlotToImageFile(expressionGraphPlotPanel, saveScreenshotFile, true, ""))
            {
                JOptionPane.showMessageDialog(jframe, "Something went wrong while saving the plot image to file:\n" +
                        "Please try again with a different file name/path/drive.",
                        "Error with saving the image to file!", JOptionPane.ERROR_MESSAGE);
                initiateTakeSingleScreenShotProcess();
            }
        }
    }

    public void initiateTakeMultipleClassesScreenShotsProcess()
    {
        int initialClassIndex = layoutFrame.getClassViewerFrame().getClassIndex();
        int startingClassIndex = expressionChooseClassesToRenderPlotImagesFromDialog.getStartingClassIndex();
        int currentClassIndex = startingClassIndex;
        int endingClassIndex = expressionChooseClassesToRenderPlotImagesFromDialog.getEndingClassIndex() + 1;

        int option = 0;
        if (endingClassIndex - startingClassIndex < WARNING_MESSAGE_FOR_RENDERING_NUMBER_OF_PLOTS)
            option = JOptionPane.YES_OPTION;
        else
            option = JOptionPane.showConfirmDialog(jframe, "Please note that it may take some time to render all " + Integer.toString(endingClassIndex - startingClassIndex) + " class plot images.\nAre you sure you want to proceed ?", "Render All Current Class Set Plot Images To Files Process", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION)
        {
            File initialSaveScreenshotFile = layoutFrame.getGraph().saveImageToFile(jframe, "Render All Current Class Set Plot Images To Files As", "plot");
            if (initialSaveScreenshotFile != null)
            {
                layoutFrame.getClassViewerFrame().setCurrentClassIndex(currentClassIndex); // sets the current Class index
                VertexClass currentVertexClass = layoutFrame.getClassViewerFrame().navigateToCurrentClass();

                boolean savedOk = false;
                String currentVertexClassName = "";
                Tuple2<File, String> tuple2 = null;
                int numberOfSelectedNodes = 0;

                do
                {
                    currentVertexClassName = currentVertexClass.getName();
                    numberOfSelectedNodes = layoutFrame.getGraph().getSelectionManager().getSelectedNodes().size();
                    tuple2 = addCurrentClassNameToSaveScreenshotFile(initialSaveScreenshotFile, currentVertexClassName, numberOfSelectedNodes);
                    savedOk = savePlotToImageFile(expressionGraphPlotPanel, tuple2.first, false,
                            (numberOfSelectedNodes > 0) ? currentVertexClassName +
                            " (" + numberOfSelectedNodes + " nodes)" : currentVertexClassName);

                    if (!savedOk)
                    {
                        layoutFrame.getClassViewerFrame().setTitle("Class Viewer");
                        JOptionPane.showMessageDialog(jframe, "Something went wrong while saving the plot image to file:\n" +
                                "Please try again with a different file name/path/drive.",
                                "Error with saving the image to file!", JOptionPane.ERROR_MESSAGE);
                        initiateTakeMultipleClassesScreenShotsProcess();
                    }

                    layoutFrame.getClassViewerFrame().setTitle("Class Viewer (Now Rendering Plot Image To File " + ++currentClassIndex + " of " + endingClassIndex + " for Class: " + tuple2.second + ( (numberOfSelectedNodes > 0) ? " with " + numberOfSelectedNodes + " nodes" : "") + ")");
                }
                while ( ( currentVertexClass = layoutFrame.getClassViewerFrame().navigateToNextClass(false) ) != null && (currentClassIndex < endingClassIndex) && savedOk );

                String numberOfRenderedPlotImages = (startingClassIndex == 0) && (endingClassIndex == layoutFrame.getClassViewerFrame().numberOfAllClasses() - 1) ? "All" : Integer.toString(endingClassIndex - startingClassIndex);
                JOptionPane.showMessageDialog(jframe, "Render " + numberOfRenderedPlotImages + " Current Class Set Plot Images To Files Process successfully finished.", "Render " + numberOfRenderedPlotImages + " Current Class Set Plot Images To Files Process", JOptionPane.INFORMATION_MESSAGE);
                layoutFrame.getClassViewerFrame().setCurrentClassIndex(initialClassIndex); // sets the initial Class index
                currentVertexClass = layoutFrame.getClassViewerFrame().navigateToCurrentClass();
            }
        }
    }

    private Tuple2<File, String> addCurrentClassNameToSaveScreenshotFile(File saveScreenshotFile, String currentVertexClassName, int numberOfSelectedNodes)
    {
        String saveScreenshotFileName = saveScreenshotFile.getAbsolutePath().substring( 0, saveScreenshotFile.getAbsolutePath().lastIndexOf(".") );
        if (currentVertexClassName.length() > 50) currentVertexClassName = currentVertexClassName.substring(0, 50); // make sure class name string not too long
        String format = saveScreenshotFile.getAbsolutePath().substring( saveScreenshotFile.getAbsolutePath().lastIndexOf(".") + 1, saveScreenshotFile.getAbsolutePath().length() );

        currentVertexClassName = currentVertexClassName.replaceAll("\\;", "_");  // for filename compatibility
        currentVertexClassName = currentVertexClassName.replaceAll("\\:", "_");  // for filename compatibility
        currentVertexClassName = currentVertexClassName.replaceAll("\\/", "_");  // for filename compatibility
        currentVertexClassName = currentVertexClassName.replaceAll("\\*", "_");  // for filename compatibility
        currentVertexClassName = currentVertexClassName.replaceAll("\\<", "_");  // for filename compatibility
        currentVertexClassName = currentVertexClassName.replaceAll("\\>", "_");  // for filename compatibility
        currentVertexClassName = currentVertexClassName.replaceAll("\\\"", "_"); // for filename compatibility
        currentVertexClassName = currentVertexClassName.replaceAll("\\|", "");   // for filename compatibility
        currentVertexClassName = currentVertexClassName.replaceAll("\\\\", "_"); // for filename compatibility
        currentVertexClassName = currentVertexClassName.replaceAll("\\?", "_");  // for filename compatibility

        return Tuples.tuple(new File(saveScreenshotFileName + " for Class " + ( (numberOfSelectedNodes > 0) ? currentVertexClassName + " (" + numberOfSelectedNodes + " nodes)" : currentVertexClassName ) + "." + format), currentVertexClassName);
    }
    
    private BufferedImage createCenteredTextImage(String text, Font font, Color fontColor, boolean isAntiAliased, boolean usesFractionalMetrics, Color backGroundColor, int imageWidth)
    {
        FontRenderContext frc = new FontRenderContext(null, isAntiAliased, usesFractionalMetrics);
        Rectangle2D rectangle2D = font.getStringBounds(text, frc);

        if ( imageWidth < rectangle2D.getWidth() )
        {
            int newFontSize = (int)floor( font.getSize() / (rectangle2D.getWidth() / imageWidth) );
            return createCenteredTextImage(text, font.deriveFont(font.getStyle(), newFontSize), fontColor, isAntiAliased, usesFractionalMetrics, backGroundColor, imageWidth);
        }
        else
        {
            BufferedImage image = new BufferedImage(imageWidth, (int)ceil( rectangle2D.getHeight() ), BufferedImage.OPAQUE);
            Graphics2D g = image.createGraphics();
            g.setColor(backGroundColor);
            g.fillRect(0, 0, imageWidth, image.getHeight());
            g.setColor(fontColor);
            g.setFont(font);
            Object antiAliased = (isAntiAliased) ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAliased);
            Object fractionalMetrics = (usesFractionalMetrics) ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics);
            g.drawString(text, (float)( imageWidth - rectangle2D.getWidth() ) / 2, -(float)rectangle2D.getY());
            g.dispose();

            return image;
        }
    }

    private void save()
    {
        int dialogReturnValue = 0;
        boolean doSaveFile = false;
        File saveFile = null;

        exportPlotExpressionProfileToFileChooser.setSelectedFile( new File( IOUtils.getPrefix( layoutFrame.getFileNameLoaded() ) + "_Expression_Profile" ) );

        if (exportPlotExpressionProfileToFileChooser.showSaveDialog(jframe) == JFileChooser.APPROVE_OPTION)
        {
            String extension = fileNameExtensionFilterText.getExtensions()[0];
            String fileName = exportPlotExpressionProfileToFileChooser.getSelectedFile().getAbsolutePath();
            if ( fileName.endsWith(extension) ) fileName = IOUtils.getPrefix(fileName);

            saveFile = new File(fileName + "." + extension);

            if ( saveFile.exists() )
            {
                dialogReturnValue = JOptionPane.showConfirmDialog(jframe, "This File Already Exists.\nDo you want to Overwrite it?", "This File Already Exists. Overwrite?", JOptionPane.YES_NO_CANCEL_OPTION);

                if (dialogReturnValue == JOptionPane.YES_OPTION)
                    doSaveFile = true;
            }
            else
            {
                doSaveFile = true;
            }
        }

        if (doSaveFile)
        {
            saveExportPlotExpressionProfileFile(saveFile);
            FILE_CHOOSER_PATH.set( saveFile.getAbsolutePath() );
        }
    }

    private void saveExportPlotExpressionProfileFile(File file)
    {
        try
        {
            int totalColumns = expressionData.getTotalColumns();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("Name\t");
            for (int j = 0; j < totalColumns; j++)
                fileWriter.write(expressionData.getColumnName(j) + "\t");
            fileWriter.write("\n");

            Integer index = null;
            HashSet<GraphNode> expandedSelectedNodes = layoutFrame.getGraph().getSelectionManager().getExpandedSelectedNodes();
            for (GraphNode graphNode : expandedSelectedNodes)
            {
                index = expressionData.getIdentityMap( graphNode.getNodeName() );
                if (index == null) continue;

                fileWriter.write(graphNode.getNodeName() + "\t");
                for (int j = 0; j < totalColumns; j++)
                    fileWriter.write(expressionData.getExpressionDataValue(index, j) + "\t");
                fileWriter.write("\n");
            }

            fileWriter.flush();
            fileWriter.close();

            InitDesktop.edit(file);
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("Exception in saveExportPlotExpressionProfileFile()\n" + ioe.getMessage());

            JOptionPane.showMessageDialog(jframe, "Something went wrong while saving the file:\n" + ioe.getMessage() + "\nPlease try again with a different file name/path/drive.", "Error with saving the file!", JOptionPane.ERROR_MESSAGE);
            save();
        }
    }

    public AbstractAction getRenderPlotImageToFileAction()
    {
        return renderPlotImageToFileAction;
    }

    public AbstractAction getRenderAllCurrentClassSetPlotImagesToFilesAction()
    {
        return renderAllCurrentClassSetPlotImagesToFilesAction;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(gridLinesCheckBox))
        {
            PLOT_GRID_LINES.set(gridLinesCheckBox.isSelected());
        }
        else if (e.getSource().equals(classStatComboBox))
        {
            PLOT_CLASS_STATISTIC_TYPE.set(classStatComboBox.getSelectedIndex());
        }
        else if (e.getSource().equals(selectionStatComboBox))
        {
            PLOT_SELECTION_STATISTIC_TYPE.set(selectionStatComboBox.getSelectedIndex());
        }
        else if (e.getSource().equals(axesLegendCheckBox))
        {
            PLOT_AXES_LEGEND.set(axesLegendCheckBox.isSelected());
        }
        else if (e.getSource().equals(transformComboBox))
        {
            PLOT_TRANSFORM.set(transformComboBox.getSelectedIndex());
        }
        else if(Arrays.asList(columnInfoPopupMenu.getComponents()).contains((Component)e.getSource()))
        {
            // Reopen the popup menu when an item is selected
            columnInfoPopupMenu.setVisible(true);
        }

        layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        refreshPlot();
        this.repaint();
    }

    private int maximumVisibleSamples()
    {
        Integer value = (Integer)maximumVisibleSamplesSpinner.getValue();
        return value.intValue();
    }

    private void refreshZoomScrollbar()
    {
        int totalColumns = expressionData.getTotalColumns();

        if (totalColumns > maximumVisibleSamples())
        {
            zoomScrollBar.setVisible(true);
            int maxSamples = maximumVisibleSamples();
            zoomScrollBar.setMaximum(totalColumns - maxSamples);
        }
        else
        {
            zoomScrollBar.setVisible(false);
        }
    }

    @Override
    public void stateChanged(ChangeEvent ce)
    {
        if (ce.getSource().equals(zoomScrollBar.getModel()))
        {
            for (int datasetIndex = 0; datasetIndex < mainPlot.getDatasetCount(); datasetIndex++)
            {
                SlidingCategoryDataset slidingDataset = (SlidingCategoryDataset) mainPlot.getDataset(datasetIndex);
                if (slidingDataset != null)
                {
                    slidingDataset.setFirstCategoryIndex(zoomScrollBar.getValue());
                }
            }

            if (columnAnnotationPlot != null)
            {
                SlidingCategoryDataset slidingDataset = (SlidingCategoryDataset) columnAnnotationPlot.getDataset();
                if (slidingDataset != null)
                {
                    slidingDataset.setFirstCategoryIndex(zoomScrollBar.getValue());
                }
            }
        }
        else if (ce.getSource().equals(maximumVisibleSamplesSpinner.getModel()))
        {
            for (int datasetIndex = 0; datasetIndex < mainPlot.getDatasetCount(); datasetIndex++)
            {
                SlidingCategoryDataset slidingDataset = (SlidingCategoryDataset) mainPlot.getDataset(datasetIndex);
                if (slidingDataset != null)
                {
                    slidingDataset.setMaximumCategoryCount(maximumVisibleSamples());
                }
            }

            if (columnAnnotationPlot != null)
            {
                SlidingCategoryDataset slidingDataset = (SlidingCategoryDataset) columnAnnotationPlot.getDataset();
                if (slidingDataset != null)
                {
                    slidingDataset.setMaximumCategoryCount(maximumVisibleSamples());
                }
            }

            refreshZoomScrollbar();
        }
    }

    public void onFirstShown()
    {
        SpinnerNumberModel snm = (SpinnerNumberModel) maximumVisibleSamplesSpinner.getModel();
        snm.setValue(expressionData.getTotalColumns());

        columnInfoPopupMenu.removeAll();
        columnInfoPopupMenu.add(sampleNameCheckBox);
        sampleNameCheckBox.setSelected(true);
        for (ExpressionData.ColumnAnnotation annotation : expressionData.getColumnAnnotations())
        {
            String annotationName = annotation.getName();
            JCheckBoxMenuItem checkBox = new JCheckBoxMenuItem(annotationName);
            checkBox.addActionListener(this);
            columnInfoPopupMenu.add(checkBox);
        }

        sortColumnAnnotationComboBox.setVisible(expressionData.getColumnAnnotations().size() > 0);

        refreshPlot();
    }
}
