package org.BioLayoutExpress3D.CoreUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
*  A class designed to report the OpenCL Driver Capabilities in a dialog.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public final class LayoutOpenCLDriverCapsDialog extends JDialog implements ActionListener
{
    /**
    *  Serial version UID variable for the LayoutOpenCLDriverCapsDialog class.
    */
    public static final long serialVersionUID = 111222333444555696L;

    private AbstractAction openCLDriverCapsAction = null;
    private JButton okButton = null;
    private boolean doneOneInit = false;

    public LayoutOpenCLDriverCapsDialog(JFrame jframe)
    {
        super(jframe, "OpenCL Driver Capabilities", true);

        initActions();
    }

    private void initActions()
    {
        openCLDriverCapsAction = new AbstractAction("OpenCL Driver Caps")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555697L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (CL_ALL_PLATFORM_NAMES != null && !doneOneInit)
                {
                    doneOneInit = true;
                    initComponents();
                }
                setLocationRelativeTo(null);
                setVisible(true);
            }
        };
        openCLDriverCapsAction.setEnabled(false);
    }

    private void initComponents()
    {
        JPanel topPanel = new JPanel(true);
        topPanel.setLayout( new BorderLayout() );

        JTabbedPane allPlatformsTabbedPane = new JTabbedPane();
        JTabbedPane[] allDevicesTabbedPanes = new JTabbedPane[CL_ALL_PLATFORM_NAMES.length];
        JScrollPane[] allDevicesScrollPanes = new JScrollPane[CL_ALL_PLATFORM_NAMES.length];
        JTextArea textArea = null;
        for (int i = 0; i < CL_ALL_PLATFORM_NAMES.length; i++)
        {
            allDevicesTabbedPanes[i] = new JTabbedPane();

            for (int j = 0; j < CL_ALL_PLATFORM_DEVICES_NAMES[i].length; j++)
            {
                textArea = new JTextArea();
                textArea.setFont( this.getFont() );
                setOpenCLDriverCaps(i, j, textArea);
                textArea.setEditable(false);
                textArea.setCaretPosition(0); // so as to have the vertical scrollbar resetted to position 0
                allDevicesScrollPanes[i] = new JScrollPane(textArea);
                allDevicesTabbedPanes[i].addTab("Device " + (j + 1) + ": " + CL_ALL_PLATFORM_DEVICES_NAMES[i][j].trim(), null, allDevicesScrollPanes[i], CL_ALL_PLATFORM_DEVICES_NAMES[i][j]);
            }

            allPlatformsTabbedPane.addTab("Platform " + (i + 1) + ": " + CL_ALL_PLATFORM_NAMES[i].trim(), null, allDevicesTabbedPanes[i], CL_ALL_PLATFORM_NAMES[i]);
        }

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        okButton.setToolTipText("OK");

        allPlatformsTabbedPane.setBorder( BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "OpenCL Driver Capabilities") );
        topPanel.add(allPlatformsTabbedPane, BorderLayout.CENTER);
        topPanel.add(okButton, BorderLayout.SOUTH);

        this.getContentPane().add(topPanel);
        this.setResizable(false);
        this.pack();
        this.setSize(710, 705);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setOpenCLDriverCaps(int platformIndex, int deviceIndex, JTextArea textArea)
    {
        textArea.append( String.format("\nCL_DEVICE_VENDOR:\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_VENDORS[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DRIVER_VERSION:\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_DRIVER_VERSIONS[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DEVICE_VERSION:\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_VERSIONS[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DEVICE_OPENCL_C_VERSION:\t\t%s\n", CL_ALL_PLATFORM_DEVICES_OPENCL_C_VERSIONS[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DEVICE_PROFILE:\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_PROFILES[platformIndex][deviceIndex]) );
        for (int i = 0; i < 4; i++)
            if (CL_ALL_PLATFORM_DEVICES_TYPES[platformIndex][deviceIndex][i] != null)
                textArea.append( String.format("CL_DEVICE_TYPE:\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_TYPES[platformIndex][deviceIndex][i]) );
        textArea.append( String.format("CL_DEVICE_ENDIAN_LITTLE:\t\t\t%s\n", (CL_ALL_PLATFORM_DEVICES_ENDIAN_LITTLES[platformIndex][deviceIndex] != 0) ? "YES" : "NO") );
        textArea.append( String.format("CL_DEVICE_MAX_COMPUTE_UNITS:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_MAX_COMPUTE_UNITS[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_DIMENSIONS[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DEVICE_MAX_WORK_ITEM_SIZES:\t\t%d  /  %d  /  %d \n", CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_SIZES[platformIndex][deviceIndex][0],
                                                                                                CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_SIZES[platformIndex][deviceIndex][1],
                                                                                                CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_SIZES[platformIndex][deviceIndex][2]) );
        textArea.append( String.format("CL_DEVICE_MAX_WORK_GROUP_SIZE:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_MAX_WORK_GROUP_SIZES[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DEVICE_MAX_CLOCK_FREQUENCY:\t\t%d MHz\n", CL_ALL_PLATFORM_DEVICES_MAX_CLOCK_FREQUENCIES[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DEVICE_ADDRESS_BITS:\t\t\t%d\n", CL_ALL_PLATFORM_DEVICES_ADDRESSES_BITS[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DEVICE_MAX_MEM_ALLOC_SIZE:\t\t%d MByte\n", (int)(CL_ALL_PLATFORM_DEVICES_MAX_MEM_ALLOC_SIZES[platformIndex][deviceIndex] / (1024 * 1024))) );
        textArea.append( String.format("CL_DEVICE_GLOBAL_MEM_SIZE:\t\t%d MByte\n", (int)(CL_ALL_PLATFORM_DEVICES_GLOBAL_MEM_SIZES[platformIndex][deviceIndex] / (1024 * 1024))) );
        textArea.append( String.format("CL_DEVICE_ERROR_CORRECTION_SUPPORT:\t" + ( (IS_MAC) ? "\t" : "" ) + "%s\n", (CL_ALL_PLATFORM_DEVICES_ERROR_CORRECTIONS_SUPPORT[platformIndex][deviceIndex] != 0) ? "YES" : "NO") );
        textArea.append( String.format("CL_DEVICE_LOCAL_MEM_TYPE:\t\t%s\n", (CL_ALL_PLATFORM_DEVICES_LOCAL_MEM_TYPES[platformIndex][deviceIndex] == 1) ? "LOCAL" : "GLOBAL") );
        textArea.append( String.format("CL_DEVICE_LOCAL_MEM_SIZE:\t\t" + ( (IS_MAC) ? "\t" : "" ) + "%d KByte\n", (int)(CL_ALL_PLATFORM_DEVICES_LOCAL_MEM_SIZES[platformIndex][deviceIndex] / 1024)) );
        textArea.append( String.format("CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE:\t\t%d KByte\n", (int)(CL_ALL_PLATFORM_DEVICES_MAX_CONSTANT_BUFFER_SIZES[platformIndex][deviceIndex] / 1024)) );
        for (int i = 0; i < 2; i++)
            if (CL_ALL_PLATFORM_DEVICES_QUEUES_PROPERTIES[platformIndex][deviceIndex][i] != null)
                textArea.append( String.format("CL_DEVICE_QUEUE_PROPERTIES:\t\t%s\n", CL_ALL_PLATFORM_DEVICES_QUEUES_PROPERTIES[platformIndex][deviceIndex][i]) );
        textArea.append( String.format("CL_DEVICE_IMAGE_SUPPORT:\t\t" + ( (IS_MAC) ? "\t" : "" ) + "%s\n", (CL_ALL_PLATFORM_DEVICES_IMAGES_SUPPORT[platformIndex][deviceIndex] != 0) ? "YES" : "NO") );
        textArea.append( String.format("CL_DEVICE_MAX_SAMPLERS:\t\t\t%d\n", CL_ALL_PLATFORM_DEVICES_CL_DEVICE_MAX_SAMPLERS[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DEVICE_MAX_READ_IMAGE_ARGS:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_MAX_READ_IMAGES_ARGS[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DEVICE_MAX_WRITE_IMAGE_ARGS:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_MAX_WRITE_IMAGES_ARGS[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DEVICE_IMAGE2D_MAX_WIDTH:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_IMAGE_2D_MAX_WIDTHS[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DEVICE_IMAGE2D_MAX_HEIGHT:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_IMAGE_2D_MAX_HEIGHTS[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DEVICE_IMAGE3D_MAX_WIDTH:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_WIDTHS[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DEVICE_IMAGE3D_MAX_HEIGHT:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_HEIGHTS[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DEVICE_IMAGE3D_MAX_DEPTH:\t\t%d\n", CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_DEPTHS[platformIndex][deviceIndex]) );
        textArea.append( String.format("CL_DEVICE_PREFERRED_VECTOR_WIDTH:\t\t") );
        textArea.append( String.format("CHAR \t%d,\n\t\t\t\tSHORT \t%d,\n\t\t\t\tINT \t%d,\n\t\t\t\tLONG \t%d,\n\t\t\t\tFLOAT \t%d,\n\t\t\t\tDOUBLE \t%d\n",
                                       CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][0],
                                       CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][1],
                                       CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][2],
                                       CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][3],
                                       CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][4],
                                       CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS[platformIndex][deviceIndex][5]) );
        for (int i = 0; i < CL_ALL_PLATFORM_DEVICES_SINGLE_FP_CONFIGS[platformIndex][deviceIndex].length; i++)
            textArea.append( String.format( ( (i == 0) ? "CL_DEVICE_SINGLE_FP_CONFIG:\t\t%s\n" : "\t\t\t\t%s\n" ), CL_ALL_PLATFORM_DEVICES_SINGLE_FP_CONFIGS[platformIndex][deviceIndex][i]) );
        for (int i = 0; i < CL_ALL_PLATFORM_DEVICES_EXECUTION_CAPABILITIES[platformIndex][deviceIndex].length; i++)
            textArea.append( String.format( ( (i == 0) ? "CL_DEVICE_EXECUTION_CAPABILITIES:\t\t%s\n" : "\t\t\t\t%s\n" ), CL_ALL_PLATFORM_DEVICES_EXECUTION_CAPABILITIES[platformIndex][deviceIndex][i]) );
        for (int i = 0; i < CL_ALL_PLATFORM_DEVICES_EXTENSIONS[platformIndex][deviceIndex].length; i++)
            textArea.append( String.format( (i == 0) ? "CL_DEVICE_EXTENSIONS:\t\t\t%s\n" : "\t\t\t\t%s\n", CL_ALL_PLATFORM_DEVICES_EXTENSIONS[platformIndex][deviceIndex][i]) );
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getActionCommand().equals("OK") )
            setVisible(false);
    }

    public AbstractAction getOpenCLDriverCapsAction()
    {
        return openCLDriverCapsAction;
    }


}