package org.BioLayoutExpress3D.Graph.Camera.CameraUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.BioLayoutExpress3D.Graph.Camera.CameraUI.*;
import static org.BioLayoutExpress3D.StaticLibraries.EnumUtils.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
* The GraphAnaglyphGlasses3DOptionsDialog class is the UI placeholder for the anaglyph glasses 3D options dialog.
*
*
* @author Thanos Theo 2011
* @version 3.0.0.0
*
*/

public class GraphAnaglyphGlasses3DOptionsDialog extends JDialog implements ActionListener
{

    private static final String GRAPH_ANAGLYPH_GLASSES_3D_OPTIONS_NAME = "Stereoscopic 3D View Options";

    private JComboBox<String> anaglyphGlassesTypesComboBox = null;
    private JComboBox<Double> intraOcularDistanceTypesComboBox = null;

    private AbstractAction graphAnaglyphGlasses3DOptionsOpenDialogAction = null;

    private JButton okButton = null;
    private JButton cancelButton = null;

    private GraphAnaglyphGlassesTypes graphAnaglyphGlassesType = GraphAnaglyphGlassesTypes.RED_BLUE;
    private GraphIntraOcularDistanceTypes graphIntraOcularDistanceType = GraphIntraOcularDistanceTypes._0_001;

    /**
    *  GraphAnaglyphGlasses3DOptionsDialogListener listener to be used as a callback for registering a a preference change through the UI.
    */
    private GraphAnaglyphGlasses3DOptionsDialogListener listener = null;

    /**
    *  The GraphAnaglyphGlasses3DOptionsDialog class constructor.
    */
    public GraphAnaglyphGlasses3DOptionsDialog(JDialog dialog)
    {
        super(dialog, GRAPH_ANAGLYPH_GLASSES_3D_OPTIONS_NAME, true);

        initActions();
        initComponents();
    }

    /**
    *  Initializes the UI actions for this dialog.
    */
    private void initActions()
    {
        graphAnaglyphGlasses3DOptionsOpenDialogAction = new AbstractAction("Options")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555697L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                setVisible(true);
            }
        };
        graphAnaglyphGlasses3DOptionsOpenDialogAction.setEnabled(true);
    }

    /**
    *  Initializes the UI components for this dialog.
    */
    private void initComponents()
    {
        anaglyphGlassesTypesComboBox = new JComboBox<String>();
        for ( GraphAnaglyphGlassesTypes localGraphAnaglyphGlassesType : GraphAnaglyphGlassesTypes.values() )
            anaglyphGlassesTypesComboBox.addItem( splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(localGraphAnaglyphGlassesType) );
        anaglyphGlassesTypesComboBox.setSelectedIndex( getEnumIndexForName( GraphAnaglyphGlassesTypes.class, ANAGLYPH_GLASSES_TYPE.get() ) );
        anaglyphGlassesTypesComboBox.addActionListener(this);
        anaglyphGlassesTypesComboBox.setToolTipText("Anaglyph Glasses Type");

        intraOcularDistanceTypesComboBox = new JComboBox<Double>();
        for ( GraphIntraOcularDistanceTypes localGraphIntraOcularDistanceType : GraphIntraOcularDistanceTypes.values() )
            intraOcularDistanceTypesComboBox.addItem( extractDouble(localGraphIntraOcularDistanceType) );
        intraOcularDistanceTypesComboBox.setSelectedIndex( getEnumIndexForName( GraphIntraOcularDistanceTypes.class, INTRA_OCULAR_DISTANCE_TYPE.get() ) );
        intraOcularDistanceTypesComboBox.addActionListener(this);
        intraOcularDistanceTypesComboBox.setToolTipText("Intra Ocular Distance");

        JPanel anaglyphGlassesTypesPanel = new JPanel(true);
        anaglyphGlassesTypesPanel.setLayout( new BoxLayout(anaglyphGlassesTypesPanel, BoxLayout.X_AXIS) );
        anaglyphGlassesTypesPanel.add( new JLabel("Anaglyph Glasses Type: ") );
        anaglyphGlassesTypesPanel.add(anaglyphGlassesTypesComboBox);

        JPanel intraOcularDistanceTypesPanel = new JPanel(true);
        intraOcularDistanceTypesPanel.setLayout( new BoxLayout(intraOcularDistanceTypesPanel, BoxLayout.X_AXIS) );
        intraOcularDistanceTypesPanel.add( new JLabel("Intra Ocular Distance: ") );
        intraOcularDistanceTypesPanel.add(intraOcularDistanceTypesComboBox);

        JPanel upperUIPanel = new JPanel(true);
        upperUIPanel.setBorder( BorderFactory.createTitledBorder("Anaglyph Glasses Options") );
        upperUIPanel.setLayout( new BoxLayout(upperUIPanel, BoxLayout.Y_AXIS) );
        upperUIPanel.add(anaglyphGlassesTypesPanel);
        upperUIPanel.add( Box.createRigidArea( new Dimension(5, 5) ) );
        upperUIPanel.add(intraOcularDistanceTypesPanel);

        JPanel buttonsPanel = new JPanel(true);
        okButton = new JButton();
        okButton.addActionListener(this);
        okButton.setText("OK");
        okButton.setToolTipText("OK");
        cancelButton = new JButton();
        cancelButton.addActionListener(this);
        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Cancel");
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);

        this.setLayout( new BorderLayout() );
        this.add(upperUIPanel, BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.SOUTH);
        this.pack();

        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                setVisible(false);
            }
        } );
    }

    /**
    *  Implements all UI related actions.
    */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getSource().equals(anaglyphGlassesTypesComboBox) )
        {
            int index = anaglyphGlassesTypesComboBox.getSelectedIndex();
            graphAnaglyphGlassesType = GraphAnaglyphGlassesTypes.values()[index];
        }
        else if ( e.getSource().equals(intraOcularDistanceTypesComboBox) )
        {
            int index = intraOcularDistanceTypesComboBox.getSelectedIndex();
            graphIntraOcularDistanceType = GraphIntraOcularDistanceTypes.values()[index];
        }
        else if ( e.getSource().equals(okButton) )
        {
            setVisible(false);
            if (listener != null) listener.updateGraphAnaglyphGlasses3DOptionsDialogPreferencesCallBack(graphAnaglyphGlassesType, graphIntraOcularDistanceType);
        }
        else if ( e.getSource().equals(cancelButton) )
        {
            setVisible(false);
        }
    }

    /**
    *  Gets the graphAnaglyphGlasses3DOptionsOpenDialogAction action.
    */
    public AbstractAction getGraphAnaglyphGlasses3DOptionsOpenDialogAction()
    {
        return graphAnaglyphGlasses3DOptionsOpenDialogAction;
    }

    /**
    *  Sets the GraphAnaglyphGlasses3DOptionsDialogListener listener.
    */
    public void setListener(GraphAnaglyphGlasses3DOptionsDialogListener listener)
    {
        this.listener = listener;
    }

    /**
    *  Removes the ModelShapeEditorParentUIDialogListener listener.
    */
    public void removeListener()
    {
        listener = null;
    }


}