package org.BioLayoutExpress3D.Graph.Selection.SelectionUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.CoreUI.LayoutClasses.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* User: icases
* Date: 04-sep-02
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class FindClassDialog extends JDialog
{
    /**
    *  Serial version UID variable for the FindClassDialog class.
    */
    public static final long serialVersionUID = 111222333444555744L;

    private LayoutFrame layoutFrame = null;
    private ClassComboBox classComboBox = null;
    private int currentClassIndex = 0;
    private AbstractAction findClassDialogAction = null;

    public FindClassDialog(LayoutFrame layoutFrame, JFrame jFrame)
    {
        super(jFrame, "Find By Class", true);

        this.layoutFrame = layoutFrame;

        initActions();
        initComponents(jFrame);
    }

    private void initActions()
    {
        findClassDialogAction = new AbstractAction("Find By Class")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555683L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                setVisible(true);
            }
        };
        findClassDialogAction.setEnabled(false);
    }

    private void initComponents(final JFrame jFrame)
    {
        JLabel textLabel = new JLabel("Please Select a Class:");
        textLabel.setAlignmentX(CENTER_ALIGNMENT);
        classComboBox = new ClassComboBox(layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses(), false, false);
        classComboBox.setToolTipText("Select a Class");
        AbstractAction okAction = new AbstractAction("OK")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555745L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                VertexClass selectedVertexClass = (VertexClass)classComboBox.getSelectedItem();
                currentClassIndex = classComboBox.getSortedVertexClasses().indexOf(selectedVertexClass);
                layoutFrame.getGraph().getSelectionManager().findClass(jFrame, selectedVertexClass);

                setVisible(false);
            }
        };

        JButton okButton = new JButton(okAction);
        okButton.setToolTipText("OK");

        AbstractAction cancelAction = new AbstractAction("Cancel")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555746L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
            }
        };

        JButton cancelButton = new JButton(cancelAction);
        cancelButton.setToolTipText("Cancel");

        JPanel container = new JPanel(true);
        container.setLayout( new BoxLayout(container, BoxLayout.Y_AXIS) );
        container.setBorder( BorderFactory.createEmptyBorder(10, 10, 10, 10) );
        container.add(textLabel);
        container.add( Box.createRigidArea( new Dimension(10, 10) ) );
        container.add(classComboBox);
        container.add( Box.createRigidArea( new Dimension(10, 10) ) );
        JPanel buttonPanel = new JPanel(true);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        container.add(buttonPanel);

        this.add(container);
        this.getRootPane().setDefaultButton(okButton);
        this.setResizable(false);
        this.pack();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    public void resetCurrentClassIndex()
    {
        currentClassIndex = -1;
        classComboBox.updateClasses( layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses() );
        classComboBox.setSelectedIndex(0);
    }

    public void setCurrentClassIndex(int currentClassIndex)
    {
        this.currentClassIndex = currentClassIndex;
        classComboBox.updateClasses( layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses() );
        classComboBox.setSelectedIndex(currentClassIndex);
    }

    public int numberOfAllClasses()
    {
        return classComboBox.getSortedVertexClasses().size();
    }

    public int getClassIndex()
    {
        return currentClassIndex;
    }

    public VertexClass currentVertexClass()
    {
        VertexClass vertexClass = classComboBox.getSortedVertexClasses().get(currentClassIndex);
        classComboBox.setSelectedIndex(currentClassIndex);

        return vertexClass;
    }

    public VertexClass nextVertexClass()
    {
        // multiple ternary if code explanation
        if ( classComboBox.getSortedVertexClasses().size() > (currentClassIndex + 1) )
        {
            currentClassIndex++;
            VertexClass vertexClass = classComboBox.getSortedVertexClasses().get(currentClassIndex);
            classComboBox.setSelectedIndex(currentClassIndex);

            return ( !vertexClass.getName().equals(NO_CLASS) ) ? vertexClass : null;
        }
        else
            return null;
    }

    public VertexClass previousVertexClass()
    {
        if ( (currentClassIndex - 1) >= 0 )
        {
            currentClassIndex--;
            VertexClass vertexClass = classComboBox.getSortedVertexClasses().get(currentClassIndex);

            return vertexClass;
        }
        else
            return null;
    }

    public boolean checkNextVertexClass()
    {
        return ( classComboBox.getSortedVertexClasses().size() > (currentClassIndex + 1) ) ? ( !classComboBox.getSortedVertexClasses().get(currentClassIndex + 1).getName().equals(NO_CLASS) ) : false;
    }

    public boolean checkPreviousVertexClass()
    {
        return ( (currentClassIndex - 1) >= 0 );
    }

    public AbstractAction getFindClassDialogAction()
    {
        return findClassDialogAction;
    }

    @Override
    public void setVisible(boolean state)
    {
        classComboBox.updateClasses( layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses() );

        if (currentClassIndex >= 0 && currentClassIndex < classComboBox.getItemCount())
        {
            classComboBox.setSelectedIndex(currentClassIndex);
        }
        else
        {
            System.out.println("FindClassDialog.setVisible currentClassIndex " + currentClassIndex +
                    " out of bounds (classComboBox has " + classComboBox.getItemCount() + " elements)" );
        }

        super.setVisible(state);
    }


}