package org.BioLayoutExpress3D.ClassViewerUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.BioLayoutExpress3D.StaticLibraries.InitDesktop;
import org.BioLayoutExpress3D.Textures.ImageSFXs;
import org.BioLayoutExpress3D.Textures.ImageSFXsCollateStates;
import org.jfree.chart.ChartPanel;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
 *
 * @author Tim Angus <tim.angus@roslin.ed.ac.uk>
 */
public abstract class ClassViewerPlotPanel extends JPanel
{
    public ClassViewerPlotPanel()
    {
        super(true);
    }

    public abstract void onFirstShown();
    public abstract AbstractAction getRenderPlotImageToFileAction();
    public abstract AbstractAction getRenderAllCurrentClassSetPlotImagesToFilesAction();
    public abstract void refreshPlot();

    static private BufferedImage createCenteredTextImage(String text, Font font, Color fontColor,
            boolean isAntiAliased, boolean usesFractionalMetrics, Color backGroundColor, int imageWidth)
    {
        FontRenderContext frc = new FontRenderContext(null, isAntiAliased, usesFractionalMetrics);
        Rectangle2D rectangle2D = font.getStringBounds(text, frc);

        if (imageWidth < rectangle2D.getWidth())
        {
            int newFontSize = (int) Math.floor(font.getSize() / (rectangle2D.getWidth() / imageWidth));
            return createCenteredTextImage(text, font.deriveFont(font.getStyle(),
                    newFontSize), fontColor, isAntiAliased, usesFractionalMetrics, backGroundColor, imageWidth);
        }
        else
        {
            BufferedImage image = new BufferedImage(imageWidth,
                    (int) Math.ceil(rectangle2D.getHeight()), BufferedImage.OPAQUE);
            Graphics2D g = image.createGraphics();
            g.setColor(backGroundColor);
            g.fillRect(0, 0, imageWidth, image.getHeight());
            g.setColor(fontColor);
            g.setFont(font);
            Object antiAliased = (isAntiAliased) ?
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON :
                    RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAliased);
            Object fractionalMetrics = (usesFractionalMetrics) ?
                    RenderingHints.VALUE_FRACTIONALMETRICS_ON :
                    RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics);
            g.drawString(text, (float) (imageWidth - rectangle2D.getWidth()) / 2, -(float) rectangle2D.getY());
            g.dispose();

            return image;
        }
    }

    protected boolean savePlotToImageFile(ChartPanel panel, File saveScreenshotFile,
            boolean openImageWithOS, String className)
    {
        final int PAD_BORDER = 5;
        final Color DESCRIPTIONS_COLOR = Color.BLACK;
        final int AXIS_FONT_SIZE = 14;
        final int AXIS_FONT_STYLE = Font.ITALIC | Font.BOLD;

        try
        {
            BufferedImage upperBorderScreenshotImage = null;
            Graphics g = null;

            upperBorderScreenshotImage = new BufferedImage(panel.getWidth(),
                    ((!className.isEmpty()) ? 1 : 2) * PAD_BORDER, Transparency.OPAQUE);
            g = upperBorderScreenshotImage.createGraphics();
            g.setColor(panel.getBackground());
            g.fillRect(0, 0, upperBorderScreenshotImage.getWidth(), upperBorderScreenshotImage.getHeight());

            if (!className.isEmpty())
            {
                BufferedImage upperBorderClassNameScreenshotImage =
                        createCenteredTextImage(className, panel.getFont().deriveFont(AXIS_FONT_STYLE, AXIS_FONT_SIZE),
                        DESCRIPTIONS_COLOR, true, false, panel.getBackground(), panel.getWidth());
                upperBorderScreenshotImage = ImageSFXs.createCollatedImage(upperBorderScreenshotImage,
                        upperBorderClassNameScreenshotImage, ImageSFXsCollateStates.COLLATE_SOUTH, true);

                BufferedImage upperBorderBottomScreenshotImage = new BufferedImage(panel.getWidth(),
                        PAD_BORDER, Transparency.OPAQUE);
                g = upperBorderBottomScreenshotImage.createGraphics();
                g.setColor(panel.getBackground());
                g.fillRect(0, 0, upperBorderBottomScreenshotImage.getWidth(),
                        upperBorderBottomScreenshotImage.getHeight());

                upperBorderScreenshotImage = ImageSFXs.createCollatedImage(upperBorderScreenshotImage,
                        upperBorderBottomScreenshotImage, ImageSFXsCollateStates.COLLATE_SOUTH, true);
            }

            BufferedImage rightBorderScreenshotImage = new BufferedImage(PAD_BORDER / 2,
                    panel.getHeight() + upperBorderScreenshotImage.getHeight(), Transparency.OPAQUE);
            g = rightBorderScreenshotImage.createGraphics();
            g.setColor(panel.getBackground());
            g.fillRect(0, 0, rightBorderScreenshotImage.getWidth(), rightBorderScreenshotImage.getHeight());

            BufferedImage plotScreenshotImage = new BufferedImage(panel.getWidth(),
                    panel.getHeight(), Transparency.OPAQUE);
            panel.paintComponent(plotScreenshotImage.createGraphics());

            plotScreenshotImage = ImageSFXs.createCollatedImage(upperBorderScreenshotImage,
                    plotScreenshotImage, ImageSFXsCollateStates.COLLATE_SOUTH, true);
            plotScreenshotImage = ImageSFXs.createCollatedImage(rightBorderScreenshotImage,
                    plotScreenshotImage, ImageSFXsCollateStates.COLLATE_WEST, true);

            String format = saveScreenshotFile.getAbsolutePath().substring(
                    saveScreenshotFile.getAbsolutePath().lastIndexOf(".") + 1,
                    saveScreenshotFile.getAbsolutePath().length());
            ImageIO.write(plotScreenshotImage, format, saveScreenshotFile);
            if (openImageWithOS)
            {
                InitDesktop.open(saveScreenshotFile);
            }

            return true;
        }
        catch (Exception exc)
        {
            if (DEBUG_BUILD)
            {
                println("Exception in ExpressionGraphPlotPanel.savePlotToImageFile():\n" + exc.getMessage());
            }

            return false;
        }
    }
}
