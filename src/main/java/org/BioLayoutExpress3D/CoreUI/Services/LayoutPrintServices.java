package org.BioLayoutExpress3D.CoreUI.Services;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.print.*;
import java.util.*;
import javax.swing.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/*
*
* @author Anton Enright, full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class LayoutPrintServices implements Printable
{

    private PrintRequestAttributeSet printAttributeSet = new HashPrintRequestAttributeSet();
    private PrinterJob printJob = null;
    private PageFormat pageFormat = null;
    private Paper paper = null;

    private BufferedImage bufferedImage = null;
    private Component componentToBePrinted = null;
    private AbstractAction printGraphPageSetupDialogAction = null;

    public LayoutPrintServices()
    {
        initActions();
        initComponents();
    }

    private void initActions()
    {
        printGraphPageSetupDialogAction = new AbstractAction("Print Graph Page Setup")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555785L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (IS_MAC)
                {
                    pageFormat = new PageFormat();
                    pageFormat.setOrientation(PageFormat.REVERSE_LANDSCAPE);
                    pageFormat.setPaper(paper);
                }
                pageFormat = (IS_MAC) ? printJob.pageDialog(pageFormat) : printJob.pageDialog(printAttributeSet);
                if (pageFormat == null)
                    pageFormat = PrinterJob.getPrinterJob().defaultPage();
            }
        };
        printGraphPageSetupDialogAction.setEnabled(false);
    }

    private void initComponents()
    {
        if (!IS_MAC)
        {
            printAttributeSet = new HashPrintRequestAttributeSet();
            printAttributeSet.add( new JobName("BioLayout Printing", Locale.getDefault() ) );
            printAttributeSet.add(OrientationRequested.LANDSCAPE);
            printAttributeSet.add(Chromaticity.COLOR);
            // printAttributeSet.add(MediaSize.ISO.A4);
        }

        printJob = PrinterJob.getPrinterJob();
        pageFormat = printJob.defaultPage();
        paper = new Paper();
        paper.setSize(800, 1000);
        paper.setImageableArea(0, 0, 800, 1000);
    }

    public void setComponent(BufferedImage bufferedImage)
    {
        this.bufferedImage = bufferedImage;
        componentToBePrinted = null;
    }

    public void setComponent(Component componentToBePrinted)
    {
        this.componentToBePrinted = componentToBePrinted;
        bufferedImage = null;
    }

    public void print()
    {
        Book book = new Book();
        pageFormat.setPaper(paper);
        printJob.setPageable(book);
        printJob.setPrintable(this, pageFormat);
        if ( ( (IS_MAC) ? printJob.printDialog() : printJob.printDialog(printAttributeSet) ) )
        {
            try
            {
                PRINT_COPIES.set( printJob.getCopies() );
                for (int i = 0; i < PRINT_COPIES.get(); i++)
                    book.append(this, pageFormat);

                if (IS_MAC)
                    printJob.print();
                else
                    printJob.print(printAttributeSet);
            }
            catch (PrinterException pe)
            {
                if (DEBUG_BUILD) println("Error in LayoutPrintServices.print():\n" + pe.getMessage());
            }
        }
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex)
    {
        if ( pageIndex >= PRINT_COPIES.get() )
        {
            return NO_SUCH_PAGE;
        }
        else
        {
            Graphics2D g2d = (Graphics2D)g;
            g2d.translate( pageFormat.getImageableX(), pageFormat.getImageableY() );

            double scaleX = 1.0;
            double scaleY = 1.0;

            double pageWidth = pageFormat.getImageableWidth();
            double pageHeight = pageFormat.getImageableHeight();

            double graphicsWidth = 0.0;
            double graphicsHeight = 0.0;
            if (bufferedImage == null)
            {
                graphicsWidth = componentToBePrinted.getWidth();
                graphicsHeight = componentToBePrinted.getHeight();
            }
            else
            {
                graphicsWidth = bufferedImage.getWidth();
                graphicsHeight = bufferedImage.getHeight();
            }

            if (DEBUG_BUILD)
            {
                println("Graphic: " + graphicsWidth + " " + graphicsHeight);
                println("Page: " + pageWidth + " " + pageHeight);
            }

            if (graphicsWidth > pageWidth)
                scaleX = (pageWidth / graphicsWidth);

            if (graphicsHeight > pageHeight)
                scaleY = (pageHeight / graphicsHeight);

            if ( (scaleX < 1.0) || (scaleY < 1.0) )
            {
                if (scaleX < scaleY)
                {
                    if (DEBUG_BUILD) println("Recale Page: " + scaleX + " " + scaleX);

                    g2d.scale(scaleX, scaleX);
                }
                else
                {
                    if (DEBUG_BUILD) println("Rescale Page: " + scaleY + " " + scaleY);

                    g2d.scale(scaleY, scaleY);
                }

            }
            if (bufferedImage == null)
            {
                // The speed and quality of printing suffers dramatically if any of the containers have double buffering turned on. So this turns if off globally.
                RepaintManager.currentManager(componentToBePrinted).setDoubleBufferingEnabled(false);
                componentToBePrinted.paint(g2d);
                RepaintManager.currentManager(componentToBePrinted).setDoubleBufferingEnabled(true); // Re-enables double buffering globally.
            }
            else
            {
                AffineTransform transform = AffineTransform.getScaleInstance(1, 1);
                g2d.drawRenderedImage(bufferedImage, transform);
            }

            return PAGE_EXISTS;
        }
    }

    public AbstractAction getPrintGraphPageSetupDialogAction()
    {
        return printGraphPageSetupDialogAction;
    }


}