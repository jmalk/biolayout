package org.BioLayoutExpress3D.Files;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* FileDragNDrop is the class that adds file Drag'n'Drop (DnD) support to BioLayout Express 3D.
*
* @see org.BioLayoutExpress3D.CoreUI.LayoutFrame
* @author Robert Harder, Nathan Blomquist, full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*/

public final class FileDragNDrop
{
    private static final Color DEFAULT_DRAG_BORDER_COLOR = new Color(0.0f, 0.0f, 1.0f, 0.25f);
    private static final Border DEFAULT_DRAG_BOARDER = BorderFactory.createMatteBorder(2, 2, 2, 2, DEFAULT_DRAG_BORDER_COLOR);
    private static final DataFlavor DATA_FLAVOR_WINDOWS = DataFlavor.javaFileListFlavor; // On Windows file DnD is a file list
    private static final DataFlavor DATA_FLAVOR_UNIX = initDataFlavorUnix(); // On Linux (and MacOSX) file DnD is a reader

    private Border normalBorder = null;
    private boolean supportsDnD = false;

    // BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
    private static final String ZERO_CHAR_STRING = "" + (char)0;

    /**
    *  Using the Singleton Design Pattern to initialize the Unix DataFlavor.
    *  On Linux (and MacOSX) file DnD is a reader.
    */
    private static DataFlavor initDataFlavorUnix()
    {
        try
        {
            return new DataFlavor("text/uri-list;class=java.io.Reader");
        }
        catch (ClassNotFoundException classExc)
        {
            if (DEBUG_BUILD) println("FileDragNDrop.initDataFlavorUnix(): ClassNotFoundException - abort:\n" + classExc.getMessage());

            return null;
        }
    }

    /**
    *  First FileDragNDrop constructor.
    */
    public FileDragNDrop(Component dropTargetComponent, FileDragNDropListener listener, FileFilter fileFilter)
    {
        this(dropTargetComponent, DEFAULT_DRAG_BOARDER, true, listener, fileFilter);
    }

    /**
    *  Second FileDragNDrop constructor.
    */
    public FileDragNDrop(Component dropTargetComponent, boolean recursive, FileDragNDropListener listener, FileFilter fileFilter)
    {
        this(dropTargetComponent, DEFAULT_DRAG_BOARDER, recursive, listener, fileFilter);
    }

    /**
    *  Third FileDragNDrop constructor.
    */
    public FileDragNDrop(Component dropTargetComponent, Border dragBorder, FileDragNDropListener listener, FileFilter fileFilter)
    {
        this(dropTargetComponent, dragBorder, false, listener, fileFilter);
    }

    /**
    *  Fourth FileDragNDrop constructor.
    */
    private FileDragNDrop(final Component dropTargetComponent, final Border dragBorder, final boolean recursive, final FileDragNDropListener listener, final FileFilter fileFilter)
    {
        if (DEBUG_BUILD) println("FileDragNDrop Check for DataFlavor types:\n" + DataFlavor.getTextPlainUnicodeFlavor().toString());

        if ( isDragNDropSupported() )
        {
            // Make a drop listener
            DropTargetListener dropListener = new DropTargetListener()
            {
                @Override
                public void dragEnter(DropTargetDragEvent dropTargetEvent)
                {
                    if (DEBUG_BUILD) println("FileDragNDrop.dragEnter(): dragEnter event.");

                    // Is this an acceptable drag event?
                    if ( isDragOk(dropTargetEvent) )
                    {
                        // If it's a Swing component, set its border
                        if (dropTargetComponent instanceof JComponent)
                        {
                            JComponent jc = (JComponent)dropTargetComponent;
                            normalBorder = jc.getBorder();
                            if (DEBUG_BUILD) println("FileDragNDrop.dragEnter(): normal border saved.");
                            jc.setBorder(dragBorder);
                            if (DEBUG_BUILD) println("FileDragNDrop.dragEnter(): drag border set.");
                        }

                        // Acknowledge that it's okay to enter
                        dropTargetEvent.acceptDrag(DnDConstants.ACTION_COPY);
                        if (DEBUG_BUILD) println("FileDragNDrop.dragEnter(): event accepted.");
                    }
                    else
                    {   // Reject the drag event
                        dropTargetEvent.rejectDrag();
                        if (DEBUG_BUILD) println("FileDragNDrop.dragEnter(): event rejected.");
                    }
                }

                @Override
                public void dragOver(DropTargetDragEvent dropTargetEvent)
                {
                    // This is called continually as long as the mouse is over the drag target.
                    if (DEBUG_BUILD) println("FileDragNDrop.dragOver(): dragOver event.");

                    // Is this an acceptable drag event?
                    if ( isDragOk(dropTargetEvent) )
                    {
                        try
                        {
                            // Get whatever was dropped
                            Transferable droppedItem = dropTargetEvent.getTransferable();
                            DataFlavor[] droppedItemFlavors = droppedItem.getTransferDataFlavors();
                            droppedItemFlavors = (droppedItemFlavors.length == 0) ? dropTargetEvent.getCurrentDataFlavors() : droppedItemFlavors;
                            DataFlavor flavor = DataFlavor.selectBestTextFlavor(droppedItemFlavors);

                            // Flavor will be null on Windows
                            // In which case use the 1st available flavor
                            flavor = (flavor == null) ? droppedItemFlavors[0] : flavor;
                            if (DEBUG_BUILD) println( getDataFlavorInformation(flavor) );

                            if ( flavor.equals(DATA_FLAVOR_WINDOWS) )
                            {
                                // dropTargetEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                                dropTargetEvent.acceptDrag(DnDConstants.ACTION_COPY);

                                File file = acceptWindowsDrop(droppedItem, flavor);

                                if ( fileFilter.accept(file) && !file.isDirectory() )
                                    dropTargetEvent.acceptDrag(DnDConstants.ACTION_COPY);
                                else
                                    dropTargetEvent.acceptDrag(DnDConstants.ACTION_NONE);
                            }
                            else if ( flavor.equals(DATA_FLAVOR_UNIX) )
                            {
                                // dropTargetEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                                dropTargetEvent.acceptDrag(DnDConstants.ACTION_COPY);

                                File file = acceptUnixDrop(droppedItem, flavor);

                                if ( fileFilter.accept(file) && !file.isDirectory() )
                                    dropTargetEvent.acceptDrag(DnDConstants.ACTION_COPY);
                                else
                                    dropTargetEvent.acceptDrag(DnDConstants.ACTION_NONE);

                                /*
                                // BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
                                DataFlavor[] flavors = droppedItem.getTransferDataFlavors();
                                boolean handled = false;
                                for (int i = 0; i < flavors.length; i++)
                                {
                                    if ( flavors[i].isRepresentationClassReader() )
                                    {
                                        // dropEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                                        dropTargetEvent.acceptDrag(DnDConstants.ACTION_COPY);
                                        if (DEBUG_BUILD) println("FileDragNDrop.dragOver(): reader accepted.");

                                        Reader reader = flavors[i].getReaderForText(droppedItem);
                                        BufferedReader br = new BufferedReader(reader);
                                        File[] files = createFileArrayForUnixSystems(br);

                                        if ( fileFilter.accept(files[0]) && !files[0].isDirectory() )
                                            dropTargetEvent.acceptDrag(DnDConstants.ACTION_COPY);
                                        else
                                            dropTargetEvent.acceptDrag(DnDConstants.ACTION_NONE);

                                        handled = true;
                                        break;
                                    }
                                }

                                if (!handled)
                                {
                                    println("FileDragNDrop.dragOver(): not a file list or reader - abort.");
                                    dropTargetEvent.rejectDrag();
                                }
                                // END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
                                */
                            }
                            else
                            {
                                if (DEBUG_BUILD) println("FileDragNDrop.dragOver(): DnD Error.");
                                dropTargetEvent.rejectDrag();
                            }
                        }
                        catch (IOException ioExc)
                        {
                            if (DEBUG_BUILD) println("FileDragNDrop.dragOver(): IOException - abort:\n" + ioExc.getMessage());
                            dropTargetEvent.rejectDrag();
                        }
                        catch (UnsupportedFlavorException ufe)
                        {
                            if (DEBUG_BUILD) println("FileDragNDrop.dragOver(): UnsupportedFlavorException - abort:\n" + ufe.getMessage());
                            dropTargetEvent.rejectDrag();
                        }
                        catch (ArrayIndexOutOfBoundsException arrExc)
                        {
                            if (DEBUG_BUILD) println("FileDragNDrop.dragOver(): DnD not initalized properly, please try again:\n" + arrExc.getMessage());
                            dropTargetEvent.rejectDrag();
                        }

                        if (DEBUG_BUILD) println("FileDragNDrop.dragOver(): event accepted.");
                    }
                    else
                    {
                        dropTargetEvent.rejectDrag();
                        if (DEBUG_BUILD) println("FileDragNDrop.dragOver(): event rejected.");
                    }
                }

                @Override
                public void drop(DropTargetDropEvent dropTargetEvent)
                {
                    if (DEBUG_BUILD) println("FileDragNDrop.drop(): drop event.");

                    try
                    {
                        // Get whatever was dropped
                        Transferable droppedItem = dropTargetEvent.getTransferable();
                        DataFlavor[] droppedItemFlavors = droppedItem.getTransferDataFlavors();
                        droppedItemFlavors = (droppedItemFlavors.length == 0) ? dropTargetEvent.getCurrentDataFlavors() : droppedItemFlavors;
                        DataFlavor flavor = DataFlavor.selectBestTextFlavor(droppedItemFlavors);

                        // Flavor will be null on Windows
                        // In which case use the 1st available flavor
                        flavor = (flavor == null) ? droppedItemFlavors[0] : flavor;
                        if (DEBUG_BUILD) println( getDataFlavorInformation(flavor) );

                        if ( flavor.equals(DATA_FLAVOR_WINDOWS) )
                        {
                            // dropTargetEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                            dropTargetEvent.acceptDrop(DnDConstants.ACTION_COPY);
                            if (DEBUG_BUILD) println("FileDragNDrop.drop(): file accepted.");

                            File file = acceptWindowsDrop(droppedItem, flavor);

                            // Alert listener to drop.
                            if (listener != null)
                                listener.filesDropped(file);

                            // Mark that drop is completed.
                            dropTargetEvent.getDropTargetContext().dropComplete(true);
                            if (DEBUG_BUILD) println("FileDragNDrop.drop(): drop complete.");
                        }
                        else if ( flavor.equals(DATA_FLAVOR_UNIX) )
                        {
                            // dropTargetEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                            dropTargetEvent.acceptDrop(DnDConstants.ACTION_COPY);
                            if (DEBUG_BUILD) println("FileDragNDrop.drop(): file accepted.");

                            File file = acceptUnixDrop(droppedItem, flavor);

                            // Alert listener to drop.
                            if (listener != null)
                                listener.filesDropped(file);

                            // Mark that drop is completed.
                            dropTargetEvent.getDropTargetContext().dropComplete(true);
                            if (DEBUG_BUILD) println("FileDragNDrop.drop(): drop complete.");

                            /*
                            // BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
                            DataFlavor[] flavors = droppedItem.getTransferDataFlavors();
                            boolean handled = false;
                            for (int i = 0; i < flavors.length; i++)
                            {
                                if ( flavors[i].isRepresentationClassReader() )
                                {
                                    // dropTargetEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                                    dropTargetEvent.acceptDrop(DnDConstants.ACTION_COPY);
                                    if (DEBUG_BUILD) println("FileDragNDrop.drop(): reader accepted.");

                                    Reader reader = flavors[i].getReaderForText(droppedItem);
                                    BufferedReader br = new BufferedReader(reader);

                                    File[] files = createFileArrayForUnixSystems(br);

                                    if (listener != null)
                                        listener.filesDropped(files);

                                    // Mark that drop is completed.
                                    dropTargetEvent.getDropTargetContext().dropComplete(true);
                                    if (DEBUG_BUILD) println("FileDragNDrop.drop(): drop complete.");

                                    handled = true;
                                    break;
                                }
                            }

                            if (!handled)
                            {
                                if (DEBUG_BUILD) println("FileDragNDrop.drop(): not a file list or reader - abort.");
                                dropTargetEvent.rejectDrop();
                            }
                            // END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
                            */
                        }
                        else
                        {
                            if (DEBUG_BUILD) println("FileDragNDrop.drop(): DnD Error.");
                            dropTargetEvent.rejectDrop();
                        }
                    }
                    catch (IOException ioExc)
                    {
                        if (DEBUG_BUILD) println("FileDragNDrop.drop(): IOException - abort:\n" + ioExc.getMessage());
                        dropTargetEvent.rejectDrop();
                    }
                    catch (UnsupportedFlavorException ufe)
                    {
                        if (DEBUG_BUILD) println("FileDragNDrop.drop(): UnsupportedFlavorException - abort:\n" + ufe.getMessage());
                        dropTargetEvent.rejectDrop();
                    }
                    catch (ArrayIndexOutOfBoundsException arrExc)
                    {
                        if (DEBUG_BUILD) println("FileDragNDrop.drop(): DnD not initalized properly, please try again:\n" + arrExc.getMessage());
                        dropTargetEvent.rejectDrop();
                    }
                    finally
                    {
                        // If it's a Swing component, reset its border
                        if (dropTargetComponent instanceof JComponent)
                        {
                            JComponent jc = (JComponent)dropTargetComponent;
                            jc.setBorder(normalBorder);

                            if (DEBUG_BUILD) println("FileDragNDrop.drop(): normal border restored.");
                        }
                    }
                }

                @Override
                public void dragExit(DropTargetEvent dropTargetEvent)
                {
                    if (DEBUG_BUILD) println("FileDragNDrop.dragExit(): dragExit event.");

                    // If it's a Swing component, reset its border
                    if (dropTargetComponent instanceof JComponent)
                    {
                        JComponent jc = (JComponent)dropTargetComponent;
                        jc.setBorder(normalBorder);
                        if (DEBUG_BUILD) println("FileDragNDrop.dragExit(): normal border restored.");
                    }
                }

                @Override
                public void dropActionChanged(DropTargetDragEvent dropTargetEvent)
                {
                    if (DEBUG_BUILD) println("FileDragNDrop.dropActionChanged(): dropActionChanged event.");

                    // Is this an acceptable drag event?
                    if ( isDragOk(dropTargetEvent) )
                    {
                        //dropTargetEvent.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
                        dropTargetEvent.acceptDrag(DnDConstants.ACTION_COPY);
                        if (DEBUG_BUILD) println("FileDragNDrop.dropActionChanged(): event accepted.");
                    }
                    else
                    {
                        dropTargetEvent.rejectDrag();
                        if (DEBUG_BUILD) println("FileDragNDrop.dropActionChanged(): event rejected.");
                    }
                }
            };

            // Make the component (and possibly children) drop targets
            makeDropTarget(dropTargetComponent, dropListener, recursive);
        }
        else
        {
            if (DEBUG_BUILD) println("FileDragNDrop: Drag and drop is not supported with this JVM.");
        }
    }

    private boolean isDragNDropSupported()
    {
        if (!supportsDnD)
        {
            try
            {
                // try to see if DnDConstants can be initialized
                Class.forName("java.awt.dnd.DnDConstants");
                supportsDnD = true;
            }
            catch (Exception exc)
            {
                if (DEBUG_BUILD) println("FileDragNDrop.isDragNDropSupported(): java.awt.dnd.DnDConstants cannot be initialized:\n" + exc.getMessage());
                supportsDnD = false;
            }
        }

        return supportsDnD;
    }

    @SuppressWarnings("unchecked") // suppresses the "unchecked" warning inside toArray() method
    private File acceptWindowsDrop(Transferable droppedItem, DataFlavor flavor) throws UnsupportedFlavorException, IOException
    {
        // Get a useful list
        java.util.List<File> fileList = (java.util.List<File>)droppedItem.getTransferData(flavor);

        return fileList.get(0);

    }

    private File acceptUnixDrop(Transferable droppedItem, DataFlavor flavor) throws UnsupportedFlavorException, IOException
    {
        BufferedReader br  = null;
        String fileName = "";

        try
        {
            br = new BufferedReader( flavor.getReaderForText(droppedItem) );
            String line;
            while ( ( line = br.readLine() ) != null )
            {
                if (DEBUG_BUILD)
                {
                    println("Dropped Item line: " + line);
                }

                fileName = line;

                // Remove 'file://' from file name
                fileName = fileName.substring(7).replace("%20", " ");
                // Remove 'localhost' from MaxOSX file names
                if ( fileName.substring(0, 9).equals("localhost") )
                {
                    fileName = fileName.substring(9);
                }

                if (DEBUG_BUILD)
                {
                    println("Unix fileName: " + fileName);
                }
            }
        }
        finally
        {
            if (br != null) br.close();
        }

        return new File(fileName);
    }

    // BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
    private File[] createFileArrayForUnixSystems(BufferedReader br)
    {
        try
        {
            String line = null;
            java.util.List<File> list = new ArrayList<File>();
            File file = null;
            while ( ( line = br.readLine() ) != null )
            {
                try
                {
                    // KDE seems to append a 0 char to the end of the reader
                    if ( ZERO_CHAR_STRING.equals(line) )
                        continue;

                    file = new File( new URI(line) );
                    list.add(file);
                }
                catch (URISyntaxException uriExc)
                {
                    if (DEBUG_BUILD) println("FileDragNDrop.createFileArrayForUnixSystems(): URISyntaxException:\n" + uriExc.getMessage());
                }
            }

            return (File[])list.toArray(new File[list.size()]);
        }
        catch (IOException ioExc)
        {
            if (DEBUG_BUILD) println("FileDragNDrop.createFileArrayForUnixSystems(): IOException:\n" + ioExc.getMessage());
        }

        return new File[0];
    }
    // END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.

    private void makeDropTarget(final Component dropTargetComponent, final DropTargetListener dropListener, boolean recursive)
    {
        // Make drop target
        DropTarget dropTarget = new DropTarget();
        try
        {
            dropTarget.addDropTargetListener(dropListener);
        }
        catch (TooManyListenersException lisExc)
        {
            if (DEBUG_BUILD) println("FileDragNDrop.makeDropTarget(): Drop will not work due to previous error. Do you have another listener attached?\n" + lisExc.getMessage());
        }

        // Listen for hierarchy changes and remove the drop target when the parent gets cleared out.
        dropTargetComponent.addHierarchyListener( new HierarchyListener()
        {
            @Override
            public void hierarchyChanged(HierarchyEvent dropTargetEvent)
            {
                if (DEBUG_BUILD) println("FileDragNDrop.hierarchyChanged(): Hierarchy changed.");
                Component parent = dropTargetComponent.getParent();
                if (parent == null)
                {
                    dropTargetComponent.setDropTarget(null);
                    if (DEBUG_BUILD) println("FileDragNDrop.hierarchyChanged(): Drop target cleared from component.");
                }
                else
                {
                    new DropTarget(dropTargetComponent, dropListener);
                    if (DEBUG_BUILD) println("FileDragNDrop.hierarchyChanged(): Drop target added to component.");
                }
            }
        } );

        if (dropTargetComponent.getParent() != null)
            new DropTarget(dropTargetComponent, dropListener);

        if ( recursive && (dropTargetComponent instanceof Container) )
        {
            // Get the container
            Container container = (Container)dropTargetComponent;

            // Get its components
            Component[] components = container.getComponents();

            // Set its components as listeners also
            for (int i = 0; i < components.length; i++)
                makeDropTarget(components[i], dropListener, recursive);
        }
    }

    private boolean isDragOk(DropTargetDragEvent dropTargetEvent)
    {
        if (IS_BLOCKED) return false;

        /*
        // Get data flavors being dragged
        DataFlavor[] flavors = dropTargetEvent.getCurrentDataFlavors();
        DataFlavor currentFlavor = null;

        // See if any of the flavors are a file list
        boolean ok = false;
        int i = 0;
        while (!ok && i < flavors.length)
        {
            // BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
            // Is the flavor a file list or reader?
            currentFlavor = flavors[i];
            if ( currentFlavor.equals(DataFlavor.javaFileListFlavor) || currentFlavor.isRepresentationClassReader() )
                ok = true;
            // END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
            i++;
        }

        // If logging is enabled, show data flavors
        if (DEBUG_BUILD)
        {
            if (flavors.length == 0)
                println("FileDragNDrop.isDragOk(): no data flavors.");

            for (i = 0; i < flavors.length; i++)
                println( flavors[i].toString() );
        }

        */

        // Get whatever was dropped
        Transferable droppedItem = dropTargetEvent.getTransferable();
        DataFlavor[] droppedItemFlavors = droppedItem.getTransferDataFlavors();
        droppedItemFlavors = (droppedItemFlavors.length == 0) ? dropTargetEvent.getCurrentDataFlavors() : droppedItemFlavors;
        DataFlavor flavor = DataFlavor.selectBestTextFlavor(droppedItemFlavors);

        // Flavor will be null on Windows
        // In which case use the 1st available flavor
        flavor = (flavor == null) ? droppedItemFlavors[0] : flavor;
        boolean ok = ( flavor.equals(DATA_FLAVOR_WINDOWS) || flavor.equals(DATA_FLAVOR_UNIX) );

        // If logging is enabled, show data flavors
        if (DEBUG_BUILD && !ok) println("FileDragNDrop.isDragOk(): no data flavors.");

        return ok;
    }

    private String getDataFlavorInformation(DataFlavor flavor)
    {
        return "DataFlavor details:\n" +
               flavor.getDefaultRepresentationClassAsString() + "\n" +
               flavor.getHumanPresentableName() + "\n" +
               flavor.getMimeType() + "\n" +
               flavor.getPrimaryType();
    }

    /**
    * Removes the drag-and-drop hooks from the component and optionally
    * from the all children. You should call this if you add and remove
    * components after you've set up the drag-and-drop.
    *
    * @param dropTargetComponent The component to unregister
    * @param recursive Recursively unregister components within a container
    * @since 1.0
    */
    public boolean remove(Component dropTargetComponent, boolean recursive)
    {
        if ( isDragNDropSupported() )
        {
            if (DEBUG_BUILD) println("FileDragNDrop.remove(): Removing drag-and-drop hooks.");
            dropTargetComponent.setDropTarget(null);
            if ( recursive && (dropTargetComponent instanceof Container) )
            {
                Component[] components = ( (Container)dropTargetComponent ).getComponents();
                for (int i = 0; i < components.length; i++)
                    remove(components[i], recursive);

                return true;
            }
            else
                return false;
        }
        else
            return false;
    }

    /* ********  I N N E R   I N T E R F A C E   L I S T E N E R  ******** */


    /**
    * Implement this inner interface to listen for when files are dropped. For example
    * your class declaration may begin like this:
    * <code><pre>
    *      public class MyClass implements org.bioLayout.file.FileDragNDrop.Listener
    *      ...
    *      public void filesDropped(java.io.file file)
    *      {
    *          ...
    *      }
    *      ...
    * </pre></code>
    *
    * @since 1.0
    */
    public interface FileDragNDropListener
    {
        /**
        * This method is called when files have been successfully dropped.
        *
        * @param file An array of <tt>file</tt>s that were dropped.
        * @since 1.0
        */
        public void filesDropped(File file);
    }


}